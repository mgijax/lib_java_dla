// $Header
// $Name

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.dbs.mgd.lookup.VocabTermLookup;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;

import java.util.HashSet;

public class SeqEventDetector {

    private MergeSplitProcessor mergeSplitProcessor;
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
    // current count of merge events
    private int mergeCtr = 0;
    // current count of split events
    private int splitCtr = 0;

    public SeqEventDetector(MergeSplitProcessor mergeSplitProcessor)
         throws ConfigException, CacheException, DBException {
        this.mergeSplitProcessor = mergeSplitProcessor;
        termNameLookup = new VocabTermLookup();
        seqIdsAlreadyProcessed = new HashSet();
    }

    public int detectEvent(SequenceInput seqInput, Sequence sequence)
          throws CacheException, KeyNotFoundException, DBException {

        // init event to 'Non Event'
        int event = SeqloaderConstants.NON_EVENT;
        String seqid = seqInput.getPrimaryAcc().getAccID();

        // we've already already processed this sequence
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
        // its a non event, increment the counter, but don't add to the
        // already added seqid cache
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