// $Header
// $Name

package org.jax.mgi.shr.dla.seqloader;

/**
 * Debug stuff
 */
import org.jax.mgi.shr.timing.Stopwatch;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.dla.DLALogger;
import org.jax.mgi.shr.dla.DLALoggingException;
import org.jax.mgi.dbs.mgd.MolecularSource.MSProcessor;
import org.jax.mgi.dbs.mgd.MolecularSource.MSException;
import org.jax.mgi.dbs.mgd.MolecularSource.MSRawAttributes;
import org.jax.mgi.dbs.mgd.MolecularSource.MolecularSource;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceKey;
import org.jax.mgi.dbs.mgd.dao.MGI_Reference_AssocState;


import java.util.Vector;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.IOException;

public class IncremSeqProcessor extends SeqProcessor {

    // detects add/update/non/already_added events for sequences
    private SeqEventDetector eventDetector;

    // writer for repeated sequences
    private BufferedWriter repeatWriter;

    // QCReporter to manage writing to seqloader QC tables
    private SeqQCReporter qcReporter;

    // lookup a sequence in MGI
    private SequenceLookup seqLookup;

    // exception factory for seqloader exceptions
    protected SeqloaderExceptionFactory eFactory;

    /**
     * Constructs a IncremSeqProcessor that handles adding sequences only; does
     * not do event detection/handling
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @throws CacheException
     * @throws DBException
     * @throws ConfigException
     * @throws MSException
     * @throws DLALoggingException
     * @throws KeyNotFoundException
     */

    public IncremSeqProcessor(SQLStream mgdSqlStream,
                              SQLStream radarSqlStream,
                              SeqQCReporter qcReporter,
                              SequenceAttributeResolver sar,
                              MergeSplitProcessor msp,
                              BufferedWriter repeatSeqWriter)
        throws CacheException, DBException, ConfigException, MSException,
               DLALoggingException, KeyNotFoundException {
        super(mgdSqlStream, radarSqlStream, sar);
        this.qcReporter = qcReporter;
        eventDetector = new SeqEventDetector(msp);
        repeatWriter = repeatSeqWriter;
        seqLookup = new SequenceLookup(mgdSqlStream);
    }

    /**
    * incapacitates the superclass delete method
    * @assumes Nothing
    * @effects Nothing
    * @param None
    * @return nothing
    * @throws
    */

    public void deleteSequences() {

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
     *  <LI>For merges and splits see MergeSplitProcessor
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
     * @throws ChangedOrganismException if input organism != existing organism
     * @throws ChangedLibraryException if input library != existing library
     */

    public void processSequence(SequenceInput seqInput)
      throws ConfigException, CacheException, DBException, TranslationException,
          KeyNotFoundException, MSException, SeqloaderException,
          RepeatSequenceException, SequenceResolverException,
          ChangedOrganismException, ChangedLibraryException {

        // get the primary seqid of the sequence we are processing
        String primarySeqid = seqInput.getPrimaryAcc().getAccID();

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
           logger.logdDebug("Already Added Event Primary: " + primarySeqid);
           processAlreadyAddedEvent(seqInput);
        }
        else if (event == SeqloaderConstants.UPDATE ) {
            logger.logdDebug("Update Event Primary: " + primarySeqid);
            processUpdateEvent(seqInput, existingSequence);
            //System.out.println("This is a Update Event");
        }
        else if (event == SeqloaderConstants.ADD) {
            addSequence(seqInput);
        }
        else if (event == SeqloaderConstants.DUMMY) {
            logger.logdDebug("Dummy Event Primary: " + primarySeqid);
            processDummyEvent(seqInput, existingSequence);
            //System.out.println("This is a Dummy Event");
        }

        else if (event == SeqloaderConstants.NON_EVENT) {
            //System.out.println("This is a Non-event");
            logger.logdDebug("NON Event Primary: " + primarySeqid);

        }
        else {
          // raise error - unhandled case
          logger.logdDebug("UNHANDLED Event Primary: " + primarySeqid);
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
              SeqloaderException, ChangedOrganismException, ChangedLibraryException {

        // get input values needed to accomplish update
        SequenceRawAttributes rawSeq = seqInput.getSeq();
        String inputRawOrganism = rawSeq.getRawOrganisms();
        String inputRawLibrary = rawSeq.getLibrary();
        String primarySeqid = seqInput.getPrimaryAcc().getAccID();
        SEQ_SequenceState existingSeqState = existingSequence.getSequenceState();

        // get existing values needed to accomplish update
        String existingRawOrganism = existingSeqState.getRawOrganism();
        String existingRawLibrary = existingSeqState.getRawLibrary();
        Integer existingSeqKey = existingSequence.getSequenceKey();

        // if input rawOrganism and existing rawOrganism don't match - QC
        if (!inputRawOrganism.equals(existingRawOrganism)) {
          // QC report and throw an exception
          logger.logcInfo("Sequence: " + primarySeqid +
                          " MGI rawOrganism: " + existingRawOrganism +
                          " Input rawOrganism: " + inputRawOrganism, false );
          qcReporter.reportRawSourceConflicts(existingSeqKey, inputRawOrganism,
                                              inputRawLibrary);

          throw new ChangedOrganismException();
        }
        // if both input and existing rawLibrary not null and not equal - QC
        else if ( (inputRawLibrary != null && existingRawLibrary != null) &&
                !inputRawLibrary.equals(existingRawLibrary)) {
            // QC report and throw an exception
            logger.logcInfo("Sequence: " + primarySeqid +
                            " MGI RawLibrary: " + existingRawLibrary +
                            " Input rawLibrary: " + inputRawLibrary, false);
            qcReporter.reportRawSourceConflicts(existingSeqKey, inputRawOrganism,
                                              inputRawLibrary);
            throw new ChangedLibraryException();
        }
        // if one rawLibrary null and the other not - QC
        else if ( (inputRawLibrary != null && existingRawLibrary == null) ||
                 (inputRawLibrary == null && existingRawLibrary != null)) {
            // QC report and throw an exception
            logger.logcInfo("Sequence: " + primarySeqid +
                            " MGI rawLibrary: " + existingRawLibrary +
                            " Input rawLibrary: " + inputRawLibrary, false);
            qcReporter.reportRawSourceConflicts(existingSeqKey, inputRawOrganism,
                                              inputRawLibrary);
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
                primarySeqid,
                //existingSequence.getAccPrimary().getAccID(),
                existingSequence.getSequenceKey(),
                (MSRawAttributes) msIterator.next());
          }

          // resolve sequence reference associations and set new ones
          // in the existing Sequence; reports any existing references
          // that no longer apply
          Vector references = seqInput.getRefs();
          if (!references.isEmpty()) {
              processReferences(existingSequence, references);
              // Now report any existing references that may be outdated
              }
          Vector oldReferences = existingSequence.getOldRefAssociations();
          MGI_Reference_AssocState refState;
          Integer refsKey;

          if (oldReferences != null) {
              for (Iterator i = oldReferences.iterator(); i.hasNext();) {
                  refState = (MGI_Reference_AssocState)i.next();
                  refsKey = refState.getRefsKey();
                  // use SeqQCReporter here to report sequenceKey and refs_key
                  // existingSequence.getSequenceKey();
                  logger.logcInfo("Old _refs_key: " +
                      refsKey + " for seqid: " + primarySeqid, true);
                  qcReporter.reportOldReferences(existingSeqKey, refsKey);
                  }
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
        addSequence(seqInput);

    }

}
// $Log
