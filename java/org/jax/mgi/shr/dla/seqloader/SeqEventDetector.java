// $Header
// $Name

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.dbs.mgd.lookup.TermNameLookup;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;

import java.util.HashSet;

public class SeqEventDetector {

    // if Processor can be null - used when processing an initial
    // incremental load
    private MergeSplitProcessor mergeSplitProcessor;
    private TermNameLookup termNameLookup;
    private HashSet seqIdsAlreadyAdded;

    public SeqEventDetector(MergeSplitProcessor mergeSplitProcessor)
         throws ConfigException, CacheException, DBException {
        this.mergeSplitProcessor = mergeSplitProcessor;
        termNameLookup = new TermNameLookup();
        seqIdsAlreadyAdded = new HashSet();
    }

    public int detectEvent(SequenceInput seqInput, Sequence sequence)
          throws CacheException, KeyNotFoundException, DBException {

        // init event to 'Non Event'
        int event = SeqloaderConstants.NON_EVENT;
        String seqid = seqInput.getPrimaryAcc().getAccID();

        // this is a new sequence
        if ( sequence == null ) {
              event = SeqloaderConstants.ADD;
              seqIdsAlreadyAdded.add(seqid);
        }

        // we've already already processed this sequence
        else if (seqIdsAlreadyAdded.contains(seqid)) {
          event = SeqloaderConstants.ALREADY_ADDED;
        }

        // this is a dummy sequence
        else if (termNameLookup.lookup(
            sequence.getSequenceState().getSequenceStatusKey()).equals(
                SeqloaderConstants.DUMMY_SEQ_STATUS)) {
            event = SeqloaderConstants.DUMMY;
            sequence.setIsDummySequence(true);

            // dummy seqs are deleted then processed as adds
            seqIdsAlreadyAdded.add(seqid);
        }

        // this sequence may need to be updated
        else if (sequence.getSequenceState().getSeqrecordDate().before(
                 seqInput.getSeq().getSeqRecDate())) {
          event = SeqloaderConstants.UPDATE;
        }

        // if we have a MergeSplitProcessor, find merge/split sequences;
        if (mergeSplitProcessor != null &&
                event != SeqloaderConstants.NON_EVENT &&
                event != SeqloaderConstants.ALREADY_ADDED) {
            mergeSplitProcessor.preProcess(seqInput);
        }

        return event;
    }
}
// $Log