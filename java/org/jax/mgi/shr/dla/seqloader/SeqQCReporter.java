//  $Header
// $Name

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.dbs.rdr.dao.QC_SEQ_OldRefDAO;
import org.jax.mgi.dbs.rdr.dao.QC_SEQ_OldRefState;
import org.jax.mgi.dbs.rdr.dao.QC_SEQ_RawSourceConflictState;
import org.jax.mgi.dbs.rdr.dao.QC_SEQ_RawSourceConflictDAO;
import org.jax.mgi.dbs.rdr.dao.QC_SEQ_MergedDAO;
import org.jax.mgi.dbs.rdr.dao.QC_SEQ_MergedState;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.exception.MGIException;

/**
 * @is An object that manages inserts to the seqloader QC reports tables
 * @has
 *   <UL>
 *   <LI> a report* method for each Sequence QC report table
 *   </UL>
 * @does
 *   <UL>
 *   <LI>writes inserts to bcp file, jdbc batch, or script file depending
 *       on the SQLStream.
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqQCReporter
{
    private SQLStream stream = null;

    /*
    * exception thrown by this class
    */
    private static String QCErr = SeqloaderExceptionFactory.QCErr;

    /**
     * Constructs a SeqQCReporter
     * @assumes Nothing
     * @effects Nothing
     * @param stream The SQLStream used to write to the QC tables
     * @throws Nothing
     */

    public SeqQCReporter(SQLStream stream) {
      this.stream = stream;
    }

    /**
    * Reports sequence reference in MGI, but no longer in a sequence record
    * @assumes Nothing
    * @effects writes to a bcp/sql file (or jdbc batch) or does an inline insert
    *          depending on the type of stream
    * @param sequenceKey key of the sequence that should no longer be associated
    *        with 'refsKey'
    * @param refsKey key of the reference that should no longer be associated
    *        with 'sequenceKey'
    * @throws SeqloaderException if error calling the insert method on the stream
    */
    public void reportOldReferences(Integer sequenceKey, Integer refsKey)
        throws SeqloaderException {
        QC_SEQ_OldRefState state = new QC_SEQ_OldRefState();
        state.setSequenceKey(sequenceKey);
        state.setRefsKey(refsKey);
        try
        {
            stream.insert(new QC_SEQ_OldRefDAO(state));
        }
        catch (MGIException e)
        {
            SeqloaderExceptionFactory eFactory = new SeqloaderExceptionFactory();
            SeqloaderException e2 = (SeqloaderException)eFactory.getException(QCErr, e);
            e2.bind("QC_SEQ_OldRef");
            throw e2;
      }
    }
    /**
    * Reports sequence raw source conflicts between a raw attribute in MGI and
    *         the input sequence record
    * @assumes Nothing
    * @effects writes to a bcp/sql file (or jdbc batch) or does an inline insert
    *          depending on the type of stream
    * @param sequenceKey key of the sequence that has a conflict
    * @param attrName name of the sequence attribute for which there is a conflict
    * @param incomingValue the incoming attribute value that conflicts with MGI
    * @throws SeqloaderException if error calling the insert method on the stream
    */

    public void reportRawSourceConflicts(Integer sequenceKey, String attrName,
                                         String incomingValue)
        throws SeqloaderException {
        QC_SEQ_RawSourceConflictState state = new QC_SEQ_RawSourceConflictState();
        state.setSequenceKey(sequenceKey);
        state.setAttrName(attrName);
        state.setIncomingValue(incomingValue);

        try{
            stream.insert(new QC_SEQ_RawSourceConflictDAO(state));
        }
        catch (MGIException e){
            SeqloaderExceptionFactory eFactory = new SeqloaderExceptionFactory();
            SeqloaderException e2 = (SeqloaderException)eFactory.getException(QCErr, e);
            e2.bind("QC_SEQ_RawSourceConflict");
            throw e2;
      }
    }
    /**
    * Reports a sequence that has been merged
    * @assumes Nothing
    * @effects writes to a bcp/sql file (or jdbc batch) or does an inline insert
    *          depending on the type of stream
    * @param fromSeqid the seqid of the sequence that is merging with toSeqid
    *        (fromSeqid will now be secondary id for toSeqid)
    * @param toSeqid the seqid of the sequence that 'fromSeqid' should be merged
    *         into
    * @throws SeqloaderException if error calling the insert method on the stream
    */

    public void reportMergedSeqs(String fromSeqid, String toSeqid)
              throws SeqloaderException {
        QC_SEQ_MergedState state = new QC_SEQ_MergedState();
        state.setFromSeqId(fromSeqid);
        state.setToSeqId(toSeqid);

        try {
            stream.insert(new QC_SEQ_MergedDAO(state));
        }
        catch (MGIException e){
            SeqloaderExceptionFactory eFactory = new SeqloaderExceptionFactory();
            SeqloaderException e2 = (SeqloaderException) eFactory.getException(QCErr,
                e);
            e2.bind("QC_SEQ_Merged");
            throw e2;
        }
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
