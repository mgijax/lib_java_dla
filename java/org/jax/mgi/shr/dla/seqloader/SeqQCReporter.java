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
 *   <LI>SQL stream for inserts
 *
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Provides a report* method for each QC report table
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
     * @param stream The SQLStream use to write to the QC tables
     * @throws SeqloaderException
     */

    public SeqQCReporter(SQLStream stream) {
      this.stream = stream;
    }

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
