package org.jax.mgi.shr.dla.input.genbank;

import java.util.*;

import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;

    /**
     * @is An object that parses a GenBank delete record.<BR>
     * @has
     *   <UL>
     *   <LI>String seqid to delete
     *   </UL>
     * @does
     *   <UL>
     *   <LI>Parses a GenBank delete record
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class GBSeqDeleterInterpreter   implements RecordDataInterpreter {

    private String seqIdToDelete;

    /**
     * Parses a sequence delete record to obtain the seqid to delete
     * @param rcd A sequence record
     * @return String seqId from 'rcd'
     * @throws RecordFormatException if record improperly formatted
     */

    public Object interpret(String rcd) throws RecordFormatException {

        StringTokenizer lineSplitter = new StringTokenizer(rcd, "|");
        if(lineSplitter.countTokens() == 2) {
            lineSplitter.nextToken();
           seqIdToDelete = ((String)lineSplitter.nextToken()).trim();

        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("Delete record improperly formatted: " + rcd);
            throw e;
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
