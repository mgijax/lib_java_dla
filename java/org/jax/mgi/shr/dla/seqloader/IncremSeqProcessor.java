// $Header
// $Name

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.shr.dla.DLALoggingException;
import org.jax.mgi.dbs.mgd.MolecularSource.MSException;
import org.jax.mgi.dbs.mgd.MolecularSource.MSRawAttributes;
import org.jax.mgi.dbs.mgd.MolecularSource.MolecularSource;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.dao.MGI_Reference_AssocState;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.AccessionLib;

import java.util.Vector;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.IOException;


// Debug

import org.jax.mgi.shr.timing.Stopwatch;

/**
 * @is an object that incrementally processes sequence by resolving a sequence, its
 * accession ids, references, and molecular sources raw attributes
 *     then adding or updating them in a database.
 * @has
 *   <UL>
 *   <LI>an event detecter
 *   <LI>a qc reporter
 *   <LI>a logger
 *   <LI>various lookups
 *   <LI>a writer to write out repeated sequence records to a file
 *   </UL>
 * @does
 *   <UL>
 *   <LI>detects events
 *   <LI>process events
 *   <LI>does QC reporting
 *   <LI>writes out repeated sequences to a file
 *   <LI>keeps track of counts of each event
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class IncremSeqProcessor extends SeqProcessor {

    // detects add/update/non/already_added events for sequences
    private SeqEventDetector eventDetector;

    // writer for repeated sequences
    private BufferedWriter repeatWriter;

    // QCReporter to manage writing to seqloader QC tables
    private SeqQCReporter qcReporter;

    // lookup a seqid in MGI ( to get a _Sequence_key)
    private AccessionLookup seqIdLookup;

    // lookup a Sequence in MGI ( to get a Sequence object)
    private SequenceLookup seqLookup;

    // Lookup for LogicalDB key
    LogicalDBLookup logicalDBLookup;

    // logicalDB_key for the load
    private int logicalDBKey;

    /**
     * Constructs a IncremSeqProcessor that handles adding sequences only; does
     * not do event detection/handling
     * @assumes Nothing
     * @effects Nothing
     * @param mgdSqlStream an sql stream for processing updates to an MGD database
     * @param radarSqlStream an sql stream for qc reporting to a RADAR database
     * @param qcReporter for managing inserts to qc report tables
     * @param sar a SequenceAttributeResolver to resolve SEQ_Sequence attributes
     * @param msp a MergeSplitProcessor - handles determining and processing
     *    merge and split events
     * @param repeatSeqWriter - a BufferedWriter to handle writing repeated
     *    sequences to a file for later processing
     * @throws CacheException if error using lookups
     * @throws DBException if lookup error querying a database
     * @throws ConfigException if error reading config file
     * @throws MSException if error creating an MSProcessor
     * @throws DLALoggingException if error creating a logger
     * @throws KeyNotFoundException if logicalDB not found in lookup
     */

    public IncremSeqProcessor(SQLStream mgdSqlStream,
                              SQLStream radarSqlStream,
                              SeqQCReporter qcReporter,
                              SequenceAttributeResolver sar,
                              MergeSplitProcessor msp,
                              BufferedWriter repeatSeqWriter)
        throws CacheException, DBException, ConfigException,  MSException,
               DLALoggingException, KeyNotFoundException {
        super(mgdSqlStream, radarSqlStream, sar);
        this.qcReporter = qcReporter;
        eventDetector = new SeqEventDetector(msp);
        repeatWriter = repeatSeqWriter;
        logicalDBKey = new LogicalDBLookup().lookup(config.getLogicalDB()).intValue();
        seqIdLookup = sidLookup;
            //new AccessionLookup(logicalDBKey,
              //  MGITypeConstants.SEQUENCE, AccessionLib.PREFERRED);
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
     * merge and split events under the covers<BR>
     * <UL>
     * <LI>Add event creates a SEQ_Sequence, one or more SEQ_Source_Assoc,
     *     one or more ACC_Accession database objects, and may create
     *     MGI_Reference_Assoc (if there are sequence reference(s)),
     *     and may create PRB_Source object(s)(See MSProcessor)
     * <LI>Update event updates SEQ_Sequence database object and may update
     *     or create PRB_Source (See MSProcessor)
     * <LI>Already processed event writes the sequence to a file for later
     *     processing.
     * <LI>Non event does nothing
     * <LI>Dummy Event deletes the dummy sequence then processes as an add
     * <LI>For merges and splits see MergeSplitProcessor
     * </UL>
     * @assumes
     * @effects Depending on the stream, writes to bcp files, creates SQL batch,
     * writes to SQL script, or does inline SQL
     * @param seqInput - a set of raw attributes for a Sequence,
     *   including source(s), reference(s), accession(s)
     * @return Nothing
     * @throws SeqloaderException if there is an IO error with the repeat
     *    sequence file
     * @throws RepeatSequenceException if we have already processed the current
     *    sequence in the input
     * @throws SequenceResolverException if we are unable to resolve one or more
     *    SequenceRawAttributes attributes
     * @throws ChangedOrganismException if input raw organism != existing raw organism
     * @throws MSException if error processing molecular source
     */

    public void processInput(SequenceInput seqInput)
      throws SeqloaderException, RepeatSequenceException,
          ChangedOrganismException, SequenceResolverException, MSException {

          // get the primary seqid of the sequence we are processing
          String primarySeqId = seqInput.getPrimaryAcc().getAccID();

          // must declare outside try block
          Integer seqKey;

          // do quick lookup to see if the primary is in MGI

          try {
              seqKey = seqIdLookup.lookup(primarySeqId);
          }
          catch (MGIException e) {
            SeqloaderException e1 =
                  (SeqloaderException) eFactory.getException(
                      SeqloaderExceptionFactory.SeqKeyQueryErr, e);
            e1.bind(primarySeqId);
              throw e1;
          }

          // If primarySeqId not in MGI existing sequence will remain null
          Sequence existingSequence = null;

          if (seqKey != null) {
              // the sequence is in MGI get its Sequence object
              sequenceCtr = sequenceCtr + 1;
              stopWatch.start();
              logger.logdDebug("Seq in MGI, looking up Sequence", true);
              try {
                existingSequence = seqLookup.findBySeqId(primarySeqId, logicalDBKey);
                logger.logdDebug("Got Sequence " + primarySeqId);
              }
              catch (MGIException e) {
                SeqloaderException e1 =
                    (SeqloaderException) eFactory.getException(
                        SeqloaderExceptionFactory.SeqQueryErr, e);
                e1.bind(primarySeqId);
                throw e1;
              }

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
          }

          // must declare outside try block
          int event;

          // Note: we pass existing Sequence to the event detector even though
          // it might be null
          try {
              event = eventDetector.detectEvent(seqInput, existingSequence);
          }
          catch (MGIException e) {
            SeqloaderException e1 =
                (SeqloaderException) eFactory.getException(
            SeqloaderExceptionFactory.EventDetectionErr, e);
            throw e1;
          }

          if (event == SeqloaderConstants.ALREADY_ADDED) {
            logger.logdDebug("Already Added Event Primary: " + primarySeqId);
            processAlreadyAddedEvent(seqInput);
          }
          else if (event == SeqloaderConstants.UPDATE) {
            logger.logdDebug("Update Event Primary: " + primarySeqId);
            try {
              processUpdateEvent(seqInput, existingSequence);
            }
            catch (ConfigException e) {
              SeqloaderException e1 =
                  (SeqloaderException) eFactory.getException(
              SeqloaderExceptionFactory.ProcessUpdateErr, e);
              throw e1;
            }
	   catch (CacheException e) {
              SeqloaderException e1 =
                  (SeqloaderException) eFactory.getException(
              SeqloaderExceptionFactory.ProcessUpdateErr, e);
              throw e1;
            }
	   catch (DBException e) {
              SeqloaderException e1 =
                  (SeqloaderException) eFactory.getException(
              SeqloaderExceptionFactory.ProcessUpdateErr, e);
              throw e1;
            }
	    catch (TranslationException e) {
              SeqloaderException e1 =
                  (SeqloaderException) eFactory.getException(
              SeqloaderExceptionFactory.ProcessUpdateErr, e);
              throw e1;
            }
	    catch (KeyNotFoundException e) {
              SeqloaderException e1 =
                  (SeqloaderException) eFactory.getException(
              SeqloaderExceptionFactory.ProcessUpdateErr, e);
              throw e1;
            }
          }
          else if (event == SeqloaderConstants.ADD) {
            super.processInput(seqInput);
          }
          else if (event == SeqloaderConstants.DUMMY) {
            logger.logdDebug("Dummy Event Primary: " + primarySeqId);
            try {
              processDummyEvent(seqInput, existingSequence);
            }
            catch (MGIException e) {
             SeqloaderException e1 =
                 (SeqloaderException) eFactory.getException(
             SeqloaderExceptionFactory.ProcessDummyErr, e);
             throw e1;
           }
          }

          else if (event == SeqloaderConstants.NON_EVENT) {
            logger.logdDebug("NON Event Primary: " + primarySeqId);
          }
          else {
            // raise error - unhandled case
            logger.logdErr("UNHANDLED Event Primary: " + primarySeqId);
            System.err.println(
                "Unhandled event in IncremSeqPrcessor.processSequence");

          }
        }

    /**
    * Gets a Vector of Strings reporting counts for a different events processed
    * thus far
    * @assumes nothing
    * @effects nothing
    * @param None
    * @returns Vector of Strings reporting counts for different events processed
    * thus far
    * @throws Nothing
    */
     public Vector getProcessedReport() {
         Vector report = new Vector();
         report .add("Total Already Added Events (repeated sequences): " + eventDetector.getAlreadyAddedEventCount());
         report.add("Total Add Events: " + eventDetector.getAddEventCount());
         report.add("Total Update Events: " + eventDetector.getUpdateEventCount());
         report.add("Total Dummy Events: " + eventDetector.getDummyEventCount());
         report.add("Total Non Events: " + eventDetector.getNonEventCount());
         report.add("Total Merge Events: " + eventDetector.getMergeEventCount());
         report.add("Total Split Events: " + eventDetector.getSplitEventCount());
         return report;
     }

      /**
      * processes AlreadyAdded event by writing the sequence to a file for later
      *    processing
      * @assumes Nothing
      * @effects writes sequence record to a file
      * @param seqInput SequenceInput object - a set of raw sequence attributes
      *        including references assoc, source assoc and accession
      * @return nothing
      * @throws RepeatSequenceException if the sequence has already been processed
      * @throws SeqloaderException indicating an IO exception occurred writing
      *   to the repeat file
      */

      private void processAlreadyAddedEvent(SequenceInput seqInput)
          throws RepeatSequenceException, SeqloaderException {
      try {
          // write sequence to file and throw RepeatFileException
          repeatWriter.write(seqInput.getSeq().getRecord() + SeqloaderConstants.CRT);
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
      * @throws ConfigException if error reading config file
      * @throws CacheException if error using lookups
      * @throws DBException if lookup error querying database
      * @throws TranslationException if translation error resolving raw sequence
      * @throws KeyNotFoundException if error doing lookups
      * @throws MSException if error processing molecular source
      * @throws SequenceResolverException if can't resolve any SEQ_Sequence
      *         attributes
      * @throws SeqloaderException if error doing QCReporting
      * @throws ChangedOrganismException if existing raw organism != incoming
      *          raw organism
      */

      private void processUpdateEvent(SequenceInput seqInput, Sequence existingSequence)
          throws ConfigException, CacheException, DBException, TranslationException,
              KeyNotFoundException, MSException, SequenceResolverException,
              SeqloaderException, ChangedOrganismException {

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
          qcReporter.reportRawSourceConflicts(existingSeqKey,
                                              SeqloaderConstants.ORGANISM,
                                              inputRawOrganism);
          throw new ChangedOrganismException();
        }
       else {
          // resolve raw sequence
          SEQ_SequenceState inputSequenceState = resolveRawSequence(rawSeq);

          // obtain old raw library name for call to MSProcessor
          String oldRawLibrary =
              existingSequence.getSequenceState().getRawLibrary();

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
                oldRawLibrary,
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
                  logger.logcInfo("Old _refs_key: " +
                      refsKey + " for seqid: " + primarySeqid, false);
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
    * @throws SeqloaderException if there are configuration, cacheing, database,
    *         translation, or lookup errors. These errors cause load to fail
    * @throws RepeatSequenceException errors writing to repeat sequence file -
    *         meant to be caught in order to skip current sequence
    * @throws ChangedLibrary if raw library for existing sequence is different
    *         than for current sequence being processed. Meant to be caught
    *         in order to skip current sequence
    * @throws ChangedOrganismException if raw organism for existing sequence is
    *         different than for current sequence being processed. Meant to be
    *         caught in order to skip current sequence
    * @throws SequenceResolverException if errors resolving a sequence. Meant
    *         to be caught in order to skip current sequence
    * @throws MSException is errors resolving a sequences source. Meant to be
    *         caught in order to skip current sequence
    */

    private void processDummyEvent(SequenceInput seqInput,
                                   Sequence existingSequence)
        throws SeqloaderException, RepeatSequenceException,
           ChangedLibraryException, ChangedOrganismException,
           MSException, SequenceResolverException {

        // send dummy sequence to stream to be deleted
        try {
          existingSequence.sendToStream();
        }
        catch (MGIException e) {
         SeqloaderException e1 =
             (SeqloaderException) eFactory.getException(
         SeqloaderExceptionFactory.SequenceSendToStreamErr, e);
         throw e1;
       }

        // process seqInput as an add event
        super.processInput(seqInput);

    }
}
// $Log
/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
