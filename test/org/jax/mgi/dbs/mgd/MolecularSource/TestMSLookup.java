package org.jax.mgi.dbs.mgd.MolecularSource;

import junit.framework.*;
import org.jax.mgi.shr.dbutils.*;
import org.jax.mgi.shr.exception.*;
import java.util.*;

public class TestMSLookup
    extends TestCase
{
    private SQLDataManager sqlMgr = null;

    public TestMSLookup(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        sqlMgr = new SQLDataManager();
        DBSchema schema = sqlMgr.getDBSchema();
        createTriggers(schema);
        dropTriggers(schema);
        runDeletes();
        runInserts();
        //createTriggers(schema);
    }

    protected void tearDown() throws Exception
    {
        DBSchema schema = sqlMgr.getDBSchema();
        dropTriggers(schema);
        runDeletes();
        createTriggers(schema);
        sqlMgr = null;
        super.tearDown();
    }

    public void testFindAssocClonesByAccid() throws Exception
    {
        String accid = "T00613";
        int limit = 5;
        Vector results = MSLookup.findAssocClonesByAccid(accid, limit);
        assertTrue(results.size() == 2);
        MolecularSource ms = (MolecularSource)results.get(0);
        assertEquals(new Integer(-30), ms.getMSKey());
        ms = (MolecularSource)results.get(1);
        assertEquals(new Integer(-40), ms.getMSKey());
    }

    public void testFindBySourceKey() throws Exception
    {
        Integer key = new Integer(-20);
        MolecularSource actualReturn = MSLookup.findBySourceKey(key);
        assertEquals(new Integer(-20), actualReturn.getMSKey());
    }

    public void testOverLimit() throws Exception
    {
        String accid = "T00613";
        int limit = 1;
        try
        {
            Vector results = MSLookup.findAssocClonesByAccid(accid, limit);
            // should not get here
            assertTrue(false);
            return;
        }
        catch (MGIException e)
        {
            assertTrue(true);
        }
    }

    private void runDeletes() throws Exception
    {
        sqlMgr.executeUpdate(
            "delete acc_accession where _accession_key = -800"
            );
        sqlMgr.executeUpdate(
            "delete acc_accession where _accession_key = -900"
            );
        sqlMgr.executeUpdate(
            "delete prb_probe where _probe_key = -30"
            );
        sqlMgr.executeUpdate(
            "delete prb_probe where _probe_key = -40"
            );
        sqlMgr.executeUpdate(
            "delete prb_source where _source_key = -20"
            );
        sqlMgr.executeUpdate(
            "delete prb_source where _source_key = -30"
            );
        sqlMgr.executeUpdate(
            "delete prb_source where _source_key = -40"
            );
    }

    private void runInserts() throws DBException
    {
        sqlMgr.executeUpdate(
            "insert into prb_source values (-20, 63474, 76026, 1, -2, " +
            "-2, 74831, 75982, null, 'name1', null, 'Not Resolved', " +
            "-1.0, -1.0, 1, 1060, 1060, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into prb_source values (-30, 63474, 76026, 1, -2, " +
            "-2, 74831, 75982, null, 'name2', null, 'Not Resolved', " +
            "-1.0, -1.0, 1, 1060, 1060, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into prb_source values (-40, 63474, 76026, 1, -2, " +
            "-2, 74831, 75982, null, null, null, 'Not Resolved', -1.0, " +
            "-1.0, 1, 1060, 1060, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into prb_probe values (-30, 'clone2', null, -30, " +
            "76021, 63468, null, null, null, null, null, null, null, " +
            "null, 0, 1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into prb_probe values (-40, 'clone3', null, -40, " +
            "76021, 63468, null, null, null, null, null, null, null, null, " +
            "0, 1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into acc_accession values (-900, 'T00613', 'T', 613, " +
            "9, -30, 3, 0, 1, 1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into acc_accession values (-800, 'T00613', 'T', 613, " +
            "9, -40, 3, 0, 1, 1200, 1200, getDate(), getDate())"
            );

    }

    protected void dropTriggers(DBSchema schema) throws Exception
    {
      createTriggers(schema);
      schema.dropTriggers("ACC_Accession");
      schema.dropTriggers("PRB_Source");
      schema.dropTriggers("PRB_Probe");
    }

    protected void createTriggers(DBSchema schema) throws Exception
    {
      schema.createTriggers("ACC_Accession");
      schema.createTriggers("PRB_Source");
      schema.createTriggers("PRB_Probe");
    }


}
