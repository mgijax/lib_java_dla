///  $Header
//  $Name

package org.jax.mgi.shr.dla.seqloader;

//import org.jax.mgi.dbs.mgd.lookup.PrimarySeqLookup;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;

/**
 * @is an object that determines sequences that have been merged or split and
 *     reassociates merged or split sequences with their proper MGI sequence object
 * @has
 *   <UL>
 *   <LI> Mapping of primary seqids to their secondary seqids that are primary
 *        in MGI
 *   <LI> Remapping of above - each secondary seqid to its set of primary seqids
 *   <LI>     SQL to call stored procedures to accomplish Merges and Splits
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Detects a Merge/Split event for a sequence by determining if any of the
 *       sequences secondary ids are in MGI as primary
 *   <LI>Stores primary seqid with its secondary seqid(s) that are merges or
 *       splits for later processing.
 *   <LI>From the 'merge/split' set, determine a merge or a split event -
 *     <UL>
 *       <LI>Create a mapping of secondary seqids to their primary seqids
 *       <LI>Detect merge and split events from the 2ndaryToPrimary HashMap.<BR>
 *          If 2ndary has > 1 primary: Event = Merge<BR>
 *          Else: Event = split<BR>
 *     </UL>
 *   <LI>Process merges by:
 *     <UL>
 *        <LI>by moving non-duplicate database associations of the
 *            MGI sequence(s) (References, Molecular Segments, Markers) to the new
 *            sequence
 *        <LI>Make the primary seqid (and the secondary seqid(s) )of the merged
 *            MGI sequence a secondary seqid of the ‘new’ Sequence
 *        <LI>Mark the MGI Sequence as deleted
 *      </UL>
 *   <LI>Process splits by:
 *     <UL>
 *       <LI>setting the MGI sequence status to ‘split'.
 *       <LI>Make the primary seqid (and the secondary seqid(s) )of the split
 *           MGI sequence secondary seqid(s) of the ‘new’ Sequence
 *       <LI>only curators can move associations and mark the split sequence as
 *           deleted
 *     </UL>
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MergeSplitProcessor {
    private MergeSplitHelper mergeSplitHelper;
    private HashMap mergeSplitSeqs;

    /**
     * Constructs a MergeSplitProcessor
     * @assumes Nothing
     * @effects Nothing
     * @param logicalDBKey LogicalDB of load
     * @throws
     */

     public MergeSplitProcessor(String logicalDB)
         throws KeyNotFoundException, DBException, CacheException,
         ConfigException {
         mergeSplitHelper = new MergeSplitHelper(logicalDB);
         mergeSplitSeqs = new HashMap();
     }
     /**
      * Detects a Merge/Split event for a sequence by determining if any of the
      *       sequences secondary ids are in MGI as primary<BR>
      * Stores primary seqid with its secondary seqid(s) that are merges or
      *       splits for later processing.
      * @assumes
      * @effects
      * @param logicalDB to create an AccessionLookup for Sequences
      * @return Nothing
      * @throws
      */

    public void preProcess(SequenceInput seqInput)
        throws KeyNotFoundException, DBException, CacheException {
        Vector v = mergeSplitHelper.getMergeSplitSeqs(seqInput);
        if( ! v.isEmpty() ) {
           mergeSplitSeqs.put(seqInput.getPrimaryAcc().getAccID(), v);
        }
    }
    /**
     * Determines a Merge or Split even for a secondary seqid that is primary
     *    in MGI and processes accordingly
     * @assumes
     * @effects
     * @param None
     * @return Nothing
     * @throws
     */

    public void process() {
        HashMap secondaryToPrimary =mergeSplitHelper.createHash(mergeSplitSeqs);

        for (Iterator mapI = secondaryToPrimary.keySet().iterator();
            mapI.hasNext();) {
               // get the key; a secondary id that is primary in MGI
               String secondary = (String) mapI.next();
               System.out.println("mapKey: " + secondary);
               // get the value; a Vector of primary ids
               Vector currentV = (Vector) secondaryToPrimary.get(secondary);
               if (currentV.isEmpty()) {
                   //throw exception
                   System.out.println("Throw Exception");
               }
               else if (currentV.size() > 1) {
                   //call stored split stored procedure
                   System.out.println("Calling SEQ_split");
               }
               else {
                   // call merge stored procedure
                   System.out.println("Calling SEQ_merge");
               }

        }
    }
}

  //  $Log
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
