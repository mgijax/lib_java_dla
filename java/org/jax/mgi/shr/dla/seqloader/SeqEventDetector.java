// $Header
// $Name

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.dbs.mgd.lookup.VocabTermLookup;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;

import java.util.HashSet;

/**
 * @is an object that determines sequence events
 * @has
 *   <UL>
 *   <LI>A MergeSplitProcessor to handle merge and split events
 *   <LI>A VocabTermLookup to help determine Dummy events
 *   <LI>A list of sequences that have already been processed
 *   <LI>Counters for each event
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Determines the following events for a sequence:
 *   <UL>
 *       <LI>Already Added Event
 *       <LI>Update Event
 *       <LI>Add Event
 *       <LI>Dummy Event
 *       <LI>Non Event
 *       <LI>Merges and Splits - See MergeSplitProcessor
 *   </UL>
 *   <LI>Keeps counts of each event
 *   <LI>Processes merge and split events - See MergSplitProcessor
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqEventDetector {

    // object to determine and process Merge and Split Events
    private MergeSplitProcessor mergeSplitProcessor;

    // Lookup to help determine Dummy Events - this is a lazy cache
    private VocabTermLookup termNameLookup;

    // cache of seqids we have already processed
    private HashSet seqIdsAlreadyProcessed;
    /**
     * Event counters
     */

    // current count of already added events
    private int alreadyAddedCtr = 0;
    // current count of update events
    private int updateCtr = 0;
    // current count of add events
    private int addCtr = 0;
    // current count of dummy events
    private int dummyCtr = 0;
    // current count of Non events
    private int nonCtr = 0;

    /**
    * Constructs a SeqEventDetector
    * @assumes Nothing
    * @effects Nothing
    * @throws ConfigException if error creating VocabTermLookup()
    * @throws CacheException if error creating VocabTermLookup()
    * @throws DBException if error creating VocabTermLookup()
    */

    public SeqEventDetector(MergeSplitProcessor mergeSplitProcessor)
         throws ConfigException, CacheException, DBException {
        this.mergeSplitProcessor = mergeSplitProcessor;
        termNameLookup = new VocabTermLookup();
        seqIdsAlreadyProcessed = new HashSet();
    }

    /**
      * Detects Sequence events
      * @assumes Nothing
      * @effects Queries a database
      * @param seqInput set of raw values for the sequence being processed
      * @param sequence sequence as it exists in the database,
      *        null if it doesn't exist
      * @return int representation of an Event
      * @throws CacheException if TermNameLookup error or MergeSplitProcessor
      *         preprocessing error
      * @throws KeyNotFoundException if MergeSplitProcessor preprocessing error
      * @throws DBException if TermNameLookup error or MergeSplitProcessor
      *         preprocessing error
      */

    public int detectEvent(SequenceInput seqInput, Sequence sequence)
          throws CacheException, KeyNotFoundException, DBException {

        // init event to 'Non Event'
        int event = SeqloaderConstants.NON_EVENT;
        // get the primary seqid
        String seqid = seqInput.getPrimaryAcc().getAccID();

        // we've already processed this sequence
        if (seqIdsAlreadyProcessed.contains(seqid)) {
          event = SeqloaderConstants.ALREADY_ADDED;
          alreadyAddedCtr++;
        }

        // this is a new sequence
        else if ( sequence == null ) {
              event = SeqloaderConstants.ADD;
              seqIdsAlreadyProcessed.add(seqid);
              addCtr++;
        }

        // this is a dummy sequence
        else if (termNameLookup.lookup(
            sequence.getSequenceState().getSequenceStatusKey()).equals(
                SeqloaderConstants.DUMMY_SEQ_STATUS)) {
            event = SeqloaderConstants.DUMMY;
            dummyCtr++;
            sequence.setIsDummySequence(true);

            // dummy seqs are deleted then processed as adds
            seqIdsAlreadyProcessed.add(seqid);
        }

        // this sequence may need to be updated
        else if (sequence.getSequenceState().getSeqrecordDate().before(
                 seqInput.getSeq().getSeqRecDate())) {
          event = SeqloaderConstants.UPDATE;
          seqIdsAlreadyProcessed.add(seqid);
          updateCtr++;
        }
        // don't add to the already added seqid cache
        else {
            nonCtr++;
        }

        // find merge/split sequences;
        if( event != SeqloaderConstants.NON_EVENT &&
                event != SeqloaderConstants.ALREADY_ADDED) {
            mergeSplitProcessor.preProcess(seqInput);
        }

        return event;
    }
    /**
    * gets the current count of 'already added' events
    * @assumes Nothing
    * @effects Nothing
    * @return int current count of 'already added' events
    * @throws Nothing
    */
      public int getAlreadyAddedEventCount() {
          return alreadyAddedCtr;
      }

    /**
    * gets the current count of 'update' events
    * @assumes Nothing
    * @effects Nothing
    * @return int current count of 'update' events
    * @throws Nothing
    */
     public int getUpdateEventCount() {
         return updateCtr;
     }

    /**
    * gets the current count of 'add' events
    * @assumes Nothing
    * @effects Nothing
    * @return int current count of 'add' events
    * @throws Nothing
    */
     public int getAddEventCount() {
         return addCtr;
     }

      /**
      * gets the current count of 'dummy' events
      * @assumes Nothing
      * @effects Nothing
      * @return int current count of 'dummy' events
      * @throws Nothing
      */
      public int getDummyEventCount() {
            return dummyCtr;
      }
      /**
      * gets the current count of 'Non' events
      * @assumes Nothing
      * @effects Nothing
      * @return int current count of 'Non' events
      * @throws Nothing
      */
        public int getNonEventCount() {
            return nonCtr;
       }
   /**
   * gets the current count of 'merge' events
   * @assumes Nothing
   * @effects Nothing
   * @return int current count of 'merge' events
   * @throws Nothing
   */
     public int getMergeEventCount() {
         return mergeSplitProcessor.getMergeEventCount();
     }

    /**
    * gets the current count of 'split' events
    * @assumes Nothing
    * @effects Nothing
    * @return int current count of 'split' events
    * @throws Nothing
    */
     public int getSplitEventCount() {
         return mergeSplitProcessor.getSplitEventCount();
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
