// $Header
// $Name

package org.jax.mgi.shr.dla.seqloader;

/**
 * Debug stuff
 */
import org.jax.mgi.shr.timing.Stopwatch;
import java.util.HashMap;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.trans.TranslationException;
import org.jax.mgi.shr.dla.DLALogger;
import org.jax.mgi.shr.dla.DLALoggingException;
import org.jax.mgi.dbs.mgd.MolecularSource.MSProcessor;
import org.jax.mgi.dbs.mgd.MolecularSource.MSException;
import org.jax.mgi.dbs.mgd.MolecularSource.MSRawAttributes;
import org.jax.mgi.dbs.mgd.MolecularSource.MolecularSource;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;

import java.util.Vector;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IncremSeqProcessor extends SeqProcessor {

    // detects add/update/non/already_added events for sequences
    private SeqEventDetector eventDetector;

    // writer for repeated sequences
    BufferedWriter repeatWriter;

    // lookup a sequence in MGI
    private SequenceLookup seqLookup;

    public IncremSeqProcessor(SQLStream mgdSqlStream,
                              SQLStream qcSqlStream,
                              SequenceAttributeResolver sar,
                              MergeSplitProcessor msp,
                              BufferedWriter repeatSeqWriter)
        throws CacheException, DBException, ConfigException, MSException,
               DLALoggingException, KeyNotFoundException, SeqloaderException {

        /**
        * Debug stuff
        */
        stopWatch = new Stopwatch();
        runningLookupTime = 0.0;
        highLookupTime = 0.0;
        lowLookupTime = 0.0;
        runningMSPTime = 0.0;
        highMSPTime = 0.0;
        lowMSPTime = 0.0;
        sequenceCtr = 0;

        //super(mgdSqlStream, qcSqlstream, sar)
        mgdStream = mgdSqlStream;
        qcStream = qcSqlStream;
        seqResolver = sar;

        eventDetector = new SeqEventDetector(msp);
        repeatWriter = repeatSeqWriter;
        config = new SequenceLoadCfg();
        seqLookup = new SequenceLookup(mgdSqlStream);
        logicalDBKey = new LogicalDBLookup().lookup(config.getLogicalDB()).intValue();

         // Create an Accession Attribute Resolver
        accResolver = new AccAttributeResolver();

        // Create a Reference Association Processor
        refAssocProcessor = new SeqRefAssocProcessor();

         // Create a Molecular Source Processor
        msProcessor = new MSProcessor (mgdSqlStream, qcSqlStream);
        logger = DLALogger.getInstance();
    }
    /**
     * Does incremental processing on a sequence by detecting
     * add, update, already processed and non events. Also detects and processes
     * merge and split events<BR>
     * <UL>
     * <LI>Add event creates a SEQ_Sequence and SEQ_Source_Assoc, and ACC_Accession
     *    database objects, and may create MGI_Reference_Assoc (if there are
     *    sequence reference(s), and may create PRB_Source object(s)
     *    (See MSProcessor)
     * <LI>Update event updates SEQ_Sequence database object and may update
     *     or create PRB_Source (See MSProcessor)
     * <LI>Already processed event writes the sequence to a file for later
     *     processing.
     *  <LI>For merges and splits see MergeSplitProcessor)
     * </UL>
     * @assumes
     * @effects Depending on the stream, writes to bcp files, creates SQL batch,
     *  writes to SQL script, or does inline SQL
     * @param seqInput - a set of raw attributes for a sequence,
     *   including sequence, source, references, accessions
     * @return Nothing
     * @throws KeyNotFoundException
     * @throws MSException
     * @throws SeqloaderException if there is an IO error with the repeat
     *    sequence file
     * @throws RepeatSequenceException if we have already processed the current
     *    sequence in the input
     * @throws SequenceResolverException if we are unable to resolve one or more
     *    SequenceRawAttributes attributes
     */

  // need to throw SeqloaderException here. Catch and bundle these exceptions in
  // a seqloader exception from the factory - this method is promised by the
  // abstract super class SeqProcessor. DRSeqProcessor will also inherit from
  // htis super class and will not throw RepeatSequence, ChangedOrganism, ChangedLibrary
  // exceptions
    public void processSequence(SequenceInput seqInput)
      throws ConfigException, CacheException, DBException, TranslationException,
          KeyNotFoundException, MSException, SeqloaderException,
          RepeatSequenceException, SequenceResolverException,
          ChangedOrganismException, ChangedLibraryException {

        // see if this sequence is in MGI, existingSequence is null if not
        // in MGI
        sequenceCtr = sequenceCtr + 1;
        stopWatch.start();
        Sequence existingSequence = seqLookup.findBySeqId(
            seqInput.getPrimaryAcc().getAccID(), logicalDBKey);
        stopWatch.stop();
        double time = stopWatch.time();
        stopWatch.reset();
        if (highLookupTime < time) {
            highLookupTime = time;
        }
        else if (lowLookupTime > time) {
            lowLookupTime = time;
        }

        runningLookupTime += time;

        int event = eventDetector.detectEvent(seqInput, existingSequence);

        if (event == SeqloaderConstants.ALREADY_ADDED) {
          //System.out.println("This is a Already Added Event");
           processAlreadyAddedEvent(seqInput);
        }
        else if (event == SeqloaderConstants.UPDATE ) {
            processUpdateEvent(seqInput, existingSequence);
            //System.out.println("This is a Update Event");
        }
        else if (event == SeqloaderConstants.ADD) {
            processAddEvent(seqInput);
            //System.out.println("This is an Add Event");
        }
        else if (event == SeqloaderConstants.DUMMY) {
            processDummyEvent(seqInput, existingSequence);
            logger.logdInfo("Sequence: " + seqInput.getPrimaryAcc().getAccID() +
                            "is a Dummy Sequence and will be deleted", true);
            //System.out.println("This is a Dummy Event");
        }

        else if (event == SeqloaderConstants.NON_EVENT) {
            //System.out.println("This is a Non-event");
        }
        else {
          // raise error - unhandled case
          System.err.println("Unhandled event in IncremSeqPrcessor.processSequence");
        }
      }

      /**
      * processes AreadyAdded event by writing the sequence to a file for later
      *    processing
      * @assumes Nothing
      * @effects writes sequence record to a file
      * @param seqInput SequenceInput object - a set of raw sequence attributes
      *        including references assoc, source assoc and accession
      * @return nothing
      * @throws RepeatSequenceException
      * @throws SeqloaderException indicating an IO exception occurred writing
      *   to the repeat file
      */

      private void processAlreadyAddedEvent(SequenceInput seqInput)
          throws RepeatSequenceException, SeqloaderException {
      try {
          // write sequence to file and throw RepeatFileException
          repeatWriter.write(seqInput.getSeq().getRecord());
          throw new RepeatSequenceException();
        }
        catch (IOException e) {
          SeqloaderException e1 =
              (SeqloaderException) eFactory.getException(
                  SeqloaderExceptionFactory.RepeatFileIOException, e);
          throw e1;
        }
      }

      /**
      * processes Update events
      * @assumes Nothing
      * @effects queries, inserts into (any new references), and updates (
      *     any changed sequence or source attributes) a database
      * @param seqInput SequenceInput object - a set of raw attributes to resolve
      *        and update a sequence
      * @param existingSequence Sequence object for the existing sequence to update
      * @return nothing
      * @throws ConfigException
      * @throws CacheException
      * @throws DBException
      * @throws TranslationException
      * @throws KeyNotFoundException
      * @throws MSException
      * @throws SequenceResolverException
      */
      private void processUpdateEvent(SequenceInput seqInput, Sequence existingSequence)
          throws ConfigException, CacheException, DBException, TranslationException,
              KeyNotFoundException, MSException, SequenceResolverException,
              ChangedOrganismException, ChangedLibraryException {
        SequenceRawAttributes rawSeq = seqInput.getSeq();
        SEQ_SequenceState existingSeqState = existingSequence.getSequenceState();

        String inputRawOrganism = rawSeq.getRawOrganisms();
        String existingRawOrganism = existingSeqState.getRawOrganism();
        String inputRawLibrary = rawSeq.getLibrary();
        String existingRawLibrary = existingSeqState.getRawLibrary();

        // if input rawOrganism and existing rawOrganism don't match - QC
        if (!inputRawOrganism.equals(existingRawOrganism)) {
          // QC report and throw an exception
          logger.logcInfo("Sequence: " + seqInput.getPrimaryAcc().getAccID() +
                          " MGI rawOrganism: " +
                          existingSequence.getSequenceState().getRawOrganism() +
                          " Input rawOrganism: " +
                          seqInput.getSeq().getRawOrganisms(), false );
          throw new ChangedOrganismException();
        }
        // if both input and existing rawLibrary not null and not equal - QC
        else if ( (inputRawLibrary != null && existingRawLibrary != null) &&
                !inputRawLibrary.equals(existingRawLibrary)) {
            // QC report and throw an exception
            logger.logcInfo("Sequence: " + seqInput.getPrimaryAcc().getAccID() +
                            " MGI rawLibrary: " +
                            existingSequence.getSequenceState().getRawLibrary() +
                            " Input rawLibrary: " +
                            seqInput.getSeq().getLibrary(), false);
            throw new ChangedLibraryException();
        }
        // if one rawLibrary null and the other not - QC
        else if ( (inputRawLibrary != null && existingRawLibrary == null) ||
                 (inputRawLibrary == null && existingRawLibrary != null)) {
            // QC report and throw an exception
            logger.logcInfo("Sequence: " + seqInput.getPrimaryAcc().getAccID() +
                            " MGI rawLibary: " +
                            existingSequence.getSequenceState().getRawLibrary() +
                            " Input rawLibrary: " +
                            seqInput.getSeq().getLibrary(), false);
            throw new ChangedLibraryException();
       }
       else {
          // resolve raw sequence
          SEQ_SequenceState inputSequenceState = resolveRawSequence(rawSeq);

          // update state of existing sequence passing input sequence state
          existingSequence.updateSequenceState(inputSequenceState);

          // process Molecular Source - note that MSProcessor handles
          // sequence to source reassociations based on collapsing
          Iterator msIterator = seqInput.getMSources().iterator();
          MolecularSource ms;
          while (msIterator.hasNext()) {
              msProcessor.processExistingSeqSrc(
                seqInput.getPrimaryAcc().getAccID(),
                //existingSequence.getAccPrimary().getAccID(),
                existingSequence.getSequenceKey(),
                (MSRawAttributes) msIterator.next());
          }

          // resolve sequence reference associations and set theme
          // in the existing Sequence
          Vector references = seqInput.getRefs();
          if (!references.isEmpty()) {
            processReferences(existingSequence, references);
          }

        }
        // send the existing sequence to its stream for possible update
        existingSequence.sendToStream();
      }


    /**
    * processes Dummy sequence by deleting it in the database then adding the
    *    real sequence
    * @assumes Nothing
    * @effects Deletes dummy sequence and inserts real sequence
    * @param seqInput SequenceInput object representing the real sequence
    * @param exisingSequence - Sequence object representing the dummy sequence
    * @return nothing
    * @throws RepeatSequenceException
    * @throws SeqloaderException indicating an IO exception occurred writing
    *   to the repeat file
    */

    private void processDummyEvent(SequenceInput seqInput,
                                   Sequence existingSequence)
        throws ConfigException, CacheException, DBException, TranslationException,
          KeyNotFoundException, MSException, SequenceResolverException {

        // send dummy sequence to stream to be deleted
        existingSequence.sendToStream();

        // process seqInput as an add event
        processAddEvent(seqInput);

    }

}
// $Log
