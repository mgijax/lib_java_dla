package org.jax.mgi.dbs.mgd.MolecularSource;

import junit.framework.*;
import org.jax.mgi.shr.dbutils.*;

public class TestMSAttrHistory
    extends TestCase
{
    private MSAttrHistory history = null;
    private SQLDataManager sqlMgr = null;
    private Integer sourceKey1 = new Integer(-20);
    private Integer sourceKey2 = new Integer(-30);

    public TestMSAttrHistory(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        sqlMgr = new SQLDataManager();
        sqlMgr.executeUpdate("delete from mgi_attributeHistory " +
                             "where _object_key = -20");
        history = new MSAttrHistory();
        BindableStatement stmt = sqlMgr.getBindableStatement(
            "insert into mgi_attributehistory " +
            "values (-20, 5, ?, 1111, " +
            "1111, getDate(), getDate())");
        stmt.setString(1, "_CellLine_key");
        stmt.executeUpdate();
        stmt.setString(1, "_Gender_key");
        stmt.executeUpdate();
        stmt.setString(1, "_Organism_key");
        stmt.executeUpdate();
        stmt.setString(1, "_Strain_key");
        stmt.executeUpdate();
        stmt.setString(1, "_Tissue_key");
        stmt.executeUpdate();
        stmt.setString(1, "age");
        stmt.executeUpdate();
    }

    protected void tearDown() throws Exception
    {
        sqlMgr.executeUpdate("delete from mgi_attributeHistory " +
                             "where _object_key = -20");
        history = null;
        sqlMgr = null;
        super.tearDown();
    }

    public void testIsAgeCurated() throws DBException
    {
        assertTrue(history.isAgeCurated(sourceKey1));
        assertTrue(!history.isAgeCurated(sourceKey2));
    }

    public void testIsCellLineCurated() throws DBException
    {
        assertTrue(history.isCellLineCurated(sourceKey1));
        assertTrue(!history.isCellLineCurated(sourceKey2));
    }

    public void testIsGenderCurated() throws DBException
    {
        assertTrue(history.isGenderCurated(sourceKey1));
        assertTrue(!history.isGenderCurated(sourceKey2));
    }

    public void testIsOrganismCurated() throws DBException
    {
        assertTrue(history.isOrganismCurated(sourceKey1));
        assertTrue(!history.isOrganismCurated(sourceKey2));
    }

    public void testIsStrainCurated() throws DBException
    {
        assertTrue(history.isStrainCurated(sourceKey1));
        assertTrue(!history.isStrainCurated(sourceKey2));
    }

    public void testIsTissueCurated() throws DBException
    {
        assertTrue(history.isTissueCurated(sourceKey1));
        assertTrue(!history.isTissueCurated(sourceKey2));
    }

}
