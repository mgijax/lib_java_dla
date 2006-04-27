package org.jax.mgi.shr.dla.input.refseq;

import java.util.*;

import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.shr.dla.input.OrganismChecker;
import org.jax.mgi.shr.config.SeqDeleterCfg;

    /**
     * @is An object that parses a RefSeq delete record.<BR>
     * @has
     *   <UL>
     *   <LI>String seqid to delete
     *   </UL>
     * @does
     *   <UL>
     *   <LI>Parses a RefSeq delete record
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class RefSeqDeleterInterpreter   implements RecordDataInterpreter {

    private String seqIdToDelete;

    /**
     * Parses a sequence delete record to obtain the seqid to delete
     * @param rcd A sequence record
     * @return String seqId from 'rcd'
     * @throws RecordFormatException if record improperly formatted
     */

    public Object interpret(String rcd) throws RecordFormatException {

        ArrayList tokens = StringLib.split(rcd, SeqloaderConstants.TAB);
        // the seqid to delete is in column 3
        try {
            // column 3 has the seqIdVersion e.g. M15131.1
            String seqIdVersion = ( (String) tokens.get(2)).trim();
            // get seqid without version e.g. M15131
            seqIdToDelete = (String) (StringLib.split(seqIdVersion,
                SeqloaderConstants.PERIOD)).get(0);
        }
        catch (Exception e) {
            RecordFormatException e1 = new RecordFormatException();
            e1.bindRecord("Delete record improperly formatted: " + rcd);
            throw e1;
        }
        return seqIdToDelete;
    }
    /**
     * Not implemented - always return true
     * @param rcd
     * @return true
     */
    public boolean isValid(String rcd) {
        return true;
    }

}

