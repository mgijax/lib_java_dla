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
import org.jax.mgi.dbs.rdr.dao.QC_MS_ChangedLibraryDAO;
import org.jax.mgi.dbs.rdr.dao.QC_MS_ChangedLibraryState;
import org.jax.mgi.dbs.rdr.dao.QC_MS_UnresolvedOrganismDAO;
import org.jax.mgi.dbs.rdr.dao.QC_MS_UnresolvedOrganismState;
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
     * constructor
     * @param stream the SQLStream to use
     */
    public MSQCReporter(SQLStream stream)
    {
        this.stream = stream;
    }


    public void reportCloneNameDiscrepancy(String accid,
                                           String name,
                                           String conflictName)
        throws MSException
    {
        QC_MS_NameConflictState state = new QC_MS_NameConflictState();
        state.setAccid(accid);
        state.setLibName1(name);
        state.setLibName2(conflictName);
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

    public void reportChangedLibrary(Integer seqid,
                                     Integer oldSourceKey,
                                     String oldRawLibName,
                                     String oldResolvedLibName,
                                     Integer newSourceKey,
                                     String newRawLibName,
                                     String newResolvedLibName,
                                     String foundMethod)
        throws MSException
    {
        QC_MS_ChangedLibraryState state = new QC_MS_ChangedLibraryState();
        state.setSequenceKey(seqid);
        state.setOldSourceKey(oldSourceKey);
        state.setOldRawName(oldRawLibName);
        state.setOldResolvedName(oldResolvedLibName);
        state.setNewSourceKey(newSourceKey);
        state.setNewRawName(newRawLibName);
        state.setNewResolvedName(newResolvedLibName);
        state.setFoundMethod(foundMethod);
        try
        {
            stream.insert(new QC_MS_ChangedLibraryDAO(state));
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(QCErr, e);
            e2.bind("QC_MS_ChangedLibrary");
            throw e2;
        }
    }

    public void reportUnresolvedOrganism(String accid, String rawOrganism)
    throws MSException
    {
        QC_MS_UnresolvedOrganismState state =
            new QC_MS_UnresolvedOrganismState();
        state.setAccid(accid);
        state.setRawOrganism(rawOrganism);
        try
        {
            stream.insert(new QC_MS_UnresolvedOrganismDAO(state));
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(QCErr, e);
            e2.bind("QC_MS_UnresolvedOrganism");
            throw e2;
        }

    }

}
