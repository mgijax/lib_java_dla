// $Header
// $Name

package org.jax.mgi.dbs.mgd.loads.Seq;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSException;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSRawAttributes;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MolecularSource;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.dao.MGI_Reference_AssocState;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.UnresolvedAttributeException;
import org.jax.mgi.shr.timing.Stopwatch;
import org.jax.mgi.shr.dla.input.SequenceInput;
import org.jax.mgi.dbs.rdr.qc.SeqQCReporter;
import org.jax.mgi.shr.dla.loader.seq.*;

import java.util.Vector;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * an object that incrementally processes sequence by resolving a sequence, its
 * accession ids, references, and molecular sources raw attributes
 *     then adding or updating them in a database.
 * @has
 *   <UL>
 *   <LI>an event detecter
 *   <LI>a qc reporter
 *   <LI>a logger
 *   <LI>various lookups
 *   </UL>
 * @does
 *   <UL>
 *   <LI>detects events
 *   <LI>process events
 *   <LI>does QC reporting
 *   <LI>keeps track of counts of each event
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class IncremSequenceInputProcessor extends SequenceInputProcessor {

    // detects add/update/non/already_added events for sequences
    private SeqEventDetector eventDetector;

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

    // a cache of SeqInput object with seqid keys that have been determined
    // to be in MGI (possible updates)
    private HashMap batchMap;

    // The following are public to provide easy access for debug logging
    // the number of seqids for the SequenceLookup to query for at one time
    public int batchSize;

    // current number of sequences in the "In MGI" batch
    // this will always be <= batchSize
    private int batchCtr;

    // Current total SequenceLookup time
    private double runningLookupTime;

    // Current highest SequenceLookup time
    private double highLookupTime;

    // Current lowest SequenceLookup time
    private double lowLookupTime;

    // current number of non-fatal errors processing sequences already in MGI
    private int existingSeqErrCtr;

    // current number of existing sequences processed
    private int existingSeqCtr;

    // current SequenceLookup average
    private double runningLookupAverage;

    /**
     * Constructs a IncremSeqProcessor that handles processing of add, update,
     * dummy, non-event, merge, and split events
     * @assumes Nothing
     * @effects Nothing
     * @param mgdSqlStream an sql stream for processing updates to an MGD database
     * @param radarSqlStream an sql stream for qc reporting to a RADAR database
     * @param qcReporter for managing inserts to qc report tables
     * @param sar a SequenceAttributeResolver to resolve SEQ_Sequence attributes
     * @param msp a MergeSplitProcessor - handles determining and processing
     *    merge and split events
     * @throws CacheException if error using lookups
     * @throws DBException if lookup error querying a database
     * @throws ConfigException if error reading config file
     * @throws MSException if error creating an MSProcessor
     * @throws DLALoggingException if error creating a logger
     * @throws KeyNotFoundException if logicalDB not found in lookup
     */

    public IncremSequenceInputProcessor(SQLStream mgdSqlStream,
                              SQLStream radarSqlStream,
                              SeqQCReporter qcReporter,
                              SequenceAttributeResolver sar,
                              MergeSplitProcessor msp)
        throws CacheException, DBException, ConfigException,  MSException,
               DLALoggingException, KeyNotFoundException {
        super(mgdSqlStream, radarSqlStream, sar);
        this.qcReporter = qcReporter;
        eventDetector = new SeqEventDetector(msp);
        logicalDBKey = new LogicalDBLookup().lookup(config.getLogicalDB()).intValue();
        seqIdLookup = new AccessionLookup(logicalDBKey,
              MGITypeConstants.SEQUENCE, AccessionLib.PREFERRED);
        batchSize = new Integer(config.getQueryBatchSize()).intValue();
        seqLookup = new SequenceLookup(mgdSqlStream, batchSize);
        batchCtr = 0;
        batchMap = new HashMap();
        runningLookupTime = 0.0;
        highLookupTime = 0.0;
        lowLookupTime = 0.0;
        existingSeqErrCtr = 0;
        existingSeqCtr = 0;
        runningLookupAverage = 0.0;
    }

    /**
    * incapacitates the superclass delete method
    * @assumes Nothing
    * @effects Nothing
    */

    public void deleteSequences() {

    }

    /**
     * Does incremental processing on a sequence by detecting whether the sequence
     * is new (add event) or in MGI (update, dummy, non events).
     * Sequences determined to be in MGI are processed in configurable
     * batches of 1 to 400 sequences. Also detects and processes
     * merge and split events under the covers<BR>
     * <UL>
     * <LI>Add event creates a SEQ_Sequence, one or more SEQ_Source_Assoc,
     *     one or more ACC_Accession database objects, and may create
     *     MGI_Reference_Assoc (if there are sequence reference(s)),
     *     and may create PRB_Source object(s)(See MSProcessor)
     * <LI>Update event updates SEQ_Sequence database object and may update
     *     or create PRB_Source (See MSProcessor)
     * <LI>Non event does nothing
     * <LI>Dummy Event deletes the dummy sequence and processes incoming
     * sequence as an add
     * <LI>For merges and splits see MergeSplitProcessor
     * </UL>
     * @effects Depending on the stream, writes to bcp files, creates SQL batch,
     * writes to SQL script, or does inline SQL
     * @param seqInput - a set of raw attributes for a Sequence,
     *   including source(s), reference(s), accession(s)
     * @throws SeqloaderException if
     * @throws SequenceResolverException if we are unable to resolve one or more
     *    SequenceRawAttributes attributes
     * @throws ChangedOrganismException if input raw organism != existing raw organism
     * @throws MSException if error processing molecular source
     */

    public void processInput(SequenceInput seqInput)
      throws SeqloaderException, ChangedOrganismException,
          SequenceResolverException, MSException {

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
          // seqid is in MGI, add seqInput to the batch and process the batch
          // when it reaches the configured batch size
          if (seqKey != null) {
              String seqId = seqInput.getPrimaryAcc().getAccID();
              // add the seqInput to the batch
              batchMap.put(seqId, seqInput);
              batchCtr++;
              // process the batch
              if (batchCtr == batchSize) {
                  processUpdateBatch();
                  // reset the batch counter and the batch map
                  batchCtr = 0;
                  batchMap = new HashMap();
              }
          }
          // seqid not in MGI, detect possible merge/split event then process
          // as an add
          else {
              try {
                  eventDetector.detectMergeSplitEvent(seqInput);
                  super.processInput(seqInput);
              }
              catch (MGIException e) {
                  SeqloaderException e1 =
                      (SeqloaderException) eFactory.getException(
                      SeqloaderExceptionFactory.EventDetectionErr, e);
                  throw e1;
              }
          }
      }

    /**
     * Gets the number of existing sequences processed thus far
     * @assumes nothing
     * @effects nothing
     * @return int number of existing sequences processed thus far
     */
    public int getCurrentExistingSeqCtr() {
        return existingSeqCtr;
    }

    /**
     * Gets the number of existing sequences with non-fatal processing errors
     * thus far
     * @assumes nothing
     * @effects nothing
     * @return int number of existing sequences with non-fatal processing errors
     * thus far
     */
    public int getCurrentExistingSeqErrCtr() {
        return existingSeqErrCtr;
    }

    /**
     * The current greatest time to query the SequenceLookup thus far
     * @assumes nothing
     * @effects nothing
     * @return double current greatest time to query the SequenceLookup thus far
     */
    public double getCurrentHighSequenceLookupTime() {
        return highLookupTime;
    }

    /**
     * The current lowest time to query the SequenceLookup thus far
     * @assumes nothing
     * @effects nothing
     * @return double current lowest time to query the SequenceLookup thus far
     */
    public double getCurrentLowSequenceLookupTime() {
        return lowLookupTime;
    }

    /**
     * The current total time to query the SequenceLookup thus far
     * @assumes nothing
     * @effects nothing
     * @return double current total time to query the SequenceLookup thus far
     */
    public double getCurrentSequenceLookupTime() {
        return runningLookupTime;
    }

    /**
      * The current Average time to query the SequenceLookup thus far
      * @assumes nothing
      * @effects nothing
      * @return double current average time to query the SequenceLookup thus far
      */
     public double getCurrentAverageSequenceLookupTime() {
         return runningLookupTime / existingSeqCtr;
     }

    /**
    * Gets a Vector of Strings reporting counts for a different events processed
    * thus far
    * @assumes nothing
    * @effects nothing
    * @return Vector of Strings reporting counts for different events processed
    * thus far
    */
     public Vector getProcessedReport() {
         Vector report = super.getProcessedReport();
         report.add("Total Update Events: " + eventDetector.getUpdateEventCount());
         report.add("Total Dummy Events: " + eventDetector.getDummyEventCount());
         report.add("Total Non Events: " + eventDetector.getNonEventCount());
         report.add("Total Merge Events: " + eventDetector.getMergeEventCount());
         report.add("Total Split Events: " + eventDetector.getSplitEventCount());
         return report;
     }

     /**
      * processes the last batch
      * @assumes nothing
      * @effects queries and updates a database
      * @throws SeqloaderException if error using seqLookup, detecting or
      * processing an event
      * @throws MSException if of type UnresolvedAttributeException
      */
     public void finishUpdateBatch() throws SeqloaderException, MSException {
          if(batchMap.size() > 0 ) {
              try {
                  processUpdateBatch();
              }
              catch (SeqloaderException e) {
                   SeqloaderException e1 =
                       (SeqloaderException) eFactory.getException(
                       SeqloaderExceptionFactory.ProcessUpdateErr, e);
                   throw e1;
               }
          }
      }

    /**
     * Processes a batch of SequenceInput's determined to be in MGI. Detects and handles
     * update, dummy, merge, split and non-events
     * @assumes nothing
     * @effects Depending on the stream, writes to bcp files, creates SQL batch,
     * writes to SQL script, or does inline SQL
     * @throws SeqloaderException if error using seqLookup, detecting or
     * processing an event
     * @throws MSException if of type UnresolvedAttributeException
     */

    private void processUpdateBatch() throws  SeqloaderException, MSException  {
        // get the set of seqids to pass to the SequenceLookup
        Set seqIdSet = batchMap.keySet();
        Vector sequences;
        // get Sequence objects for the batch
        try {
            stopWatch.reset();
            stopWatch.start();
            sequences = seqLookup.findBySeqId(seqIdSet, logicalDBKey);
            stopWatch.stop();
            double time = stopWatch.time();
            if (highLookupTime < time) {
                highLookupTime = time;
            }
            else if (lowLookupTime > time) {
                lowLookupTime = time;
            }
            runningLookupTime += time;
        }
        catch (MGIException e) {
            SeqloaderException e1 =
                (SeqloaderException) eFactory.getException(
                SeqloaderExceptionFactory.SeqQueryErr, e);
            e1.bind(seqIdSet.toString());
            throw e1;
        }
        // iterate thru the Sequence objects detecting and processing events
        for (Iterator i = sequences.iterator(); i.hasNext(); ) {
            Sequence existingSequence = (Sequence) i.next();
            String primarySeqId = existingSequence.getAccPrimary().getAccID();
            //System.out.println("Primary seqid: " + primarySeqId);
            SequenceInput seqInput = (SequenceInput) batchMap.get(primarySeqId);
            if (seqInput == null) {
                throw new RuntimeException("IncremSequenceInputProcessor: " +
                    "No SequenceInput object for existing sequence in MGI!");
            }

            // must declare outside try block
            int event;
            try {
                event = eventDetector.detectEvent(seqInput, existingSequence);
            }
            catch (MGIException e) {
                SeqloaderException e1 =
                    (SeqloaderException) eFactory.getException(
                    SeqloaderExceptionFactory.EventDetectionErr, e);
                throw e1;
            }
            if (event == SeqloaderConstants.UPDATE) {
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
                // If incoming raw organism != existing raw organism -
                // qcReports handles reporting, log to diagnostic and curator
                catch (ChangedOrganismException e) {
                    String message = e.getMessage() + " Sequence: " +
                        seqInput.getPrimaryAcc().getAccID();
                    logger.logdDebug(message, true);
                    logger.logcInfo(message, true);
                    existingSeqErrCtr++;
                    continue;
                }

                // if we can't resolve SEQ_Sequence attributes,  log to curation log
                // go to the next sequence
                catch (SequenceResolverException e) {
                    String message = e.getMessage() + " Sequence: " +
                        seqInput.getPrimaryAcc().getAccID();
                    logger.logdDebug(message, true);
                    logger.logcInfo(message, true);
                    existingSeqErrCtr++;
                    continue;
                }
                // UnresolvedAttributeException is thrown for those loaders that
                // must resolve *all* source attributes or fail.
                // For other loads if we can't resolve the source for a sequence,
                // log to curation log and go to the next sequence
                catch (MSException e) {
                    if (e instanceof UnresolvedAttributeException) {
                        throw e;
                    }
                    String message = e.getMessage() + " Sequence: " +
                        seqInput.getPrimaryAcc().getAccID();
                    logger.logdInfo(message, true);
                    logger.logcInfo(message, true);
                    existingSeqErrCtr++;
                    continue;
                }

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
                    "Unhandled event in IncremSeqProcessor.processSequence");
            }
        }
        existingSeqCtr = existingSeqCtr + seqIdSet.size();
        runningLookupAverage = runningLookupTime / existingSeqCtr;
    }

      /**
      * processes Update events
      * @assumes Nothing
      * @effects queries, inserts into (any new references), and updates (
      *     any changed sequence or source attributes) a database
      * @param seqInput SequenceInput object - a set of raw attributes to resolve
      *        and update a sequence
      * @param existingSequence Sequence object for the existing sequence to update
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
              stopWatch.start();
              msProcessor.processExistingSeqSrc(
                  primarySeqid,
                  existingSequence.getSequenceKey(),
                  oldRawLibrary,
                  (MSRawAttributes) msIterator.next());
              stopWatch.stop();
              double time = stopWatch.time();
              stopWatch.reset();
              if (highMSPTime < time) {
                  highMSPTime = time;
              }
              else if (lowMSPTime > time) {
                  lowMSPTime = time;
              }
              runningMSPTime += time;
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
    * @param existingSequence Sequence object representing the dummy sequence
    * @throws SeqloaderException if there are configuration, cacheing, database,
    *         translation, or lookup errors. These errors cause load to fail
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
        throws SeqloaderException, ChangedOrganismException, MSException,
        SequenceResolverException {

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
