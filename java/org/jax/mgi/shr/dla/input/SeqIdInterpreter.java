package org.jax.mgi.shr.dla.input;
import java.util.*;

import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;

    /**
     * @is An object that parses a seqid from a file of one seqid per line.<BR>
     * @has
     *   <UL>
     *   <LI>Nothing
     *   </UL>
     * @does
     *   <UL>
     *   <LI>Parses a seqid from a file of one seqid per line.
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class SeqIdInterpreter implements RecordDataInterpreter {
        private String seqIdToDelete;

 /**
  * Parses a seqid from a file of one seqid per line.
  * @param rcd A seqid
  * @return String seqId
  * @throws RecordFormatException if 'rcd' improperly formatted
  */

 public Object interpret(String rcd) throws RecordFormatException {

     StringTokenizer lineSplitter = new StringTokenizer(rcd);
     if(lineSplitter.countTokens() == 1) {
         return ((String)lineSplitter.nextToken()).trim();
     }
     else {
         RecordFormatException e = new RecordFormatException();
         e.bindRecord("Delete record improperly formatted: " + rcd);
         throw e;
     }
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
