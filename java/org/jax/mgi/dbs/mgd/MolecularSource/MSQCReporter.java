/*
 * Created on Nov 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.dbs.rdr.dao.QC_MS_AttrEditDAO;
import org.jax.mgi.dbs.rdr.dao.QC_MS_AttrEditState;
import org.jax.mgi.dbs.rdr.dao.QC_MS_NameConflictDAO;
import org.jax.mgi.dbs.rdr.dao.QC_MS_NameConflictState;
import org.jax.mgi.dbs.rdr.dao.QC_MS_NoLibFoundDAO;
import org.jax.mgi.dbs.rdr.dao.QC_MS_NoLibFoundState;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.exception.MGIException;

public class MSQCReporter
{

    private SQLStream stream = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String QCErr = MSExceptionFactory.QCErr;

    /**
     *
     */
    public MSQCReporter(SQLStream stream)
    {
      this.stream = stream;
    }

    public void reportLibraryNameNotFound(String name)
    throws MSException
    {
      QC_MS_NoLibFoundState state = new QC_MS_NoLibFoundState();
      state.setLibraryName(name);
      try
      {
        stream.insert(new QC_MS_NoLibFoundDAO(state));
      }
      catch (MGIException e)
      {
        MSExceptionFactory eFactory = new MSExceptionFactory();
        MSException e2 = (MSException)
            eFactory.getException(QCErr, e);
        e2.bind("QC_MS_NoLibFound");
        throw e2;

      }

    }

    public void reportCloneNameDiscrepancy(String accid,
                                           String name,
                                           String conflictName)
    throws MSException
    {
      QC_MS_NameConflictState state = new QC_MS_NameConflictState();
      state.setAccid(accid);
      //state.setName(name);
      state.setClone1Name(name);
      //state.setConflict(conflictName);
      state.setClone2Name(conflictName);

      try
      {
        stream.insert(new QC_MS_NameConflictDAO(state));
      }
      catch (MGIException e)
      {
        MSExceptionFactory eFactory = new MSExceptionFactory();
        MSException e2 = (MSException)
            eFactory.getException(QCErr, e);
        e2.bind("QC_MS_NameConflict");
        throw e2;
      }
    }

    public void reportAttributeDiscrepancy(Integer sourceKey, String attrName,
                                           Integer attrValue, String newValue)
    throws MSException
    {
      QC_MS_AttrEditState state = new QC_MS_AttrEditState();
      state.setSourceKey(sourceKey);
      state.setAttrName(attrName);
      state.setAttrValue(attrValue);
      state.setIncomingValue(newValue);
      try
     {
       stream.insert(new QC_MS_AttrEditDAO(state));
     }
     catch (MGIException e)
     {
       MSExceptionFactory eFactory = new MSExceptionFactory();
       MSException e2 = (MSException)
           eFactory.getException(QCErr, e);
       e2.bind("QC_MS_AttrEdit");
       throw e2;
     }

    }

}
