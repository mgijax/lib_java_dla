///  $Header
//  $Name

package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.dbutils.ScriptWriter;
import org.jax.mgi.dbs.rdr.qc.SeqQCReporter;
import org.jax.mgi.shr.dbutils.ScriptException;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import org.jax.mgi.shr.dla.input.SequenceInput;

/**
 * an object that determines sequences that have been merged or split and
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
    private DLALogger logger;
    private SeqQCReporter qcReporter;
    private int mergeCtr = 0;
    private int splitCtr = 0;

    /**
     * Constructs a MergeSplitProcessor
     * @assumes Nothing
     * @effects Nothing
     * @param seqidLookup - a lookup of seqids for this provider that are
     * primary in MGI
     * @param reporter - qcreporter to report merge/split statistics
     * @throws KeyNotFoundException
     * @throws DBException
     * @throws CacheException
     * @throws ConfigException
     * @throws DLALoggingException
     */

     public MergeSplitProcessor(AccessionLookup seqidLookup, SeqQCReporter reporter)
         throws KeyNotFoundException, DBException, CacheException,
         ConfigException, DLALoggingException {
         qcReporter = reporter;
         mergeSplitHelper = new MergeSplitHelper(seqidLookup);
         mergeSplitSeqs = new HashMap();
         logger = DLALogger.getInstance();
     }
     /**
      * Detects a Merge/Split event for a sequence by determining if any of the
      *       sequences secondary ids are in MGI as primary<BR>
      * Maps primary seqid (String) to the  sequence keys (Vector of
      *       Integers) that are merges or splits for later processing.
      * @assumes
      * @effects
      * @param seqInput SequenceInput object for which we will determine
      * merges and splits
      * @throws KeyNotFoundException
      * @throws DBException
      * @throws CacheException
      */

    public void preProcess(SequenceInput seqInput)
        throws KeyNotFoundException, DBException, CacheException {
        Vector v = mergeSplitHelper.getMergeSplitSeqs(seqInput);
        if( ! v.isEmpty() ) {
           mergeSplitSeqs.put(seqInput.getPrimaryAcc().getAccID(), v);
        }
    }
    /**
     * Determines a Merge or Split event for a secondary seqid that is primary
     *    in MGI and processes accordingly
     * @assumes
     * @effects
     * @param writer the ScriptWriter which to write the merge and split stored
     * procedure calls
     * @throws ScriptException
     * @throws SeqloaderException
     */

    public void process(ScriptWriter writer)
            throws ScriptException, SeqloaderException {
        HashMap secondaryToPrimary = mergeSplitHelper.createHash(mergeSplitSeqs);
        // SEQ_Merge fromSeqid toSeqid
        // SEQ_Split fromSeqid to Seqid
        String mergeProc = "SEQ_merge ";
        String splitProc= "SEQ_split ";
        for (Iterator mapI = secondaryToPrimary.keySet().iterator();
            mapI.hasNext();) {
               // get the key; a secondary id that is primary in MGI
               String fromSeqid = (String) mapI.next();

               // get the value; a Vector of primary ids
               Vector currentV = (Vector) secondaryToPrimary.get(fromSeqid);
               if (currentV.isEmpty()) {
                   //throw exception
                   logger.logdErr("MergeSplitProcessor.process" +
                                    "Vector of primary ids is empty");
               }
               else if (currentV.size() > 1) {
                   // write out call to split stored procedure
                   for (Iterator i = currentV.iterator(); i.hasNext();) {
                       String toSeqid = (String)i.next();
                       String cmd = splitProc +
                           SeqloaderConstants.SPC +
                           SeqloaderConstants.SGL_QUOTE +
                           fromSeqid + SeqloaderConstants.SGL_QUOTE +
                           SeqloaderConstants.COMMA +
                           SeqloaderConstants.SGL_QUOTE + toSeqid +
                           SeqloaderConstants.SGL_QUOTE;
                       writer.write(cmd);
                       writer.go();
                       splitCtr++;
                   }
               }
               else {
                   // write out call to merge stored procedure
                   String toSeqid = (String)currentV.get(0);
                   String cmd = mergeProc +
                       SeqloaderConstants.SPC +
                       SeqloaderConstants.SGL_QUOTE +
                       fromSeqid + SeqloaderConstants.SGL_QUOTE +
                       SeqloaderConstants.COMMA +
                       SeqloaderConstants.SGL_QUOTE + toSeqid +
                       SeqloaderConstants.SGL_QUOTE;
                   qcReporter.reportMergedSeqs(fromSeqid, toSeqid);
                   writer.write(cmd);
                   writer.go();
                   mergeCtr++;
               }
        }
    }

    /**
     * gets the current count of 'merge' events
     * @assumes The split event counter is 0 unless the process method has been called
     * @effects Nothing
     * @return int current count of 'merge' events
     * @throws Nothing
     */
    public int getMergeEventCount() {
        return mergeCtr;
    }

    /**
     * gets the current count of 'split' events
     * @assumes The split event counter is 0 unless the process method has been called
     * @effects Nothing
     * @return int current count of 'split' events
     * @throws Nothing
     */
    public int getSplitEventCount() {
        return splitCtr;
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
