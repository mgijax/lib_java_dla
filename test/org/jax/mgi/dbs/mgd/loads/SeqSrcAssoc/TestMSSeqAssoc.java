package org.jax.mgi.dbs.mgd.loads.SeqSrcAssoc;

import junit.framework.*;
import org.jax.mgi.shr.dbutils.*;
import org.jax.mgi.shr.config.*;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.*;

public class TestMSSeqAssoc
    extends TestCase
{
    private MSSeqAssoc assoc = null;
    private SQLDataManager sqlMgr = null;

    public TestMSSeqAssoc(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        sqlMgr = new SQLDataManager();
        sqlMgr.executeUpdate(
            "insert into prb_source values (-20, 63474, 76026, 1, -2, " +
            "-2, 74831, 75982, null, null, null, " +
            "'Not Resolved', -1.0, -1.0, 1, 1060, 1060, getDate(), " +
            "getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into seq_sequence values (-200, 76004, 7600, 76046, " +
            "76040, 34595, 'Mus musculus, Tpi, mRNA.', '1', null, 0, " +
            "'NotLoaded', 'NotLoaded', 'NotLoaded', 'NotLoaded', " +
            "'NotLoaded', 'NotLoaded', 'NotLoaded', 'NotLoaded', null, " +
            "getDate(), getDate(), 1000, 1000, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into seq_source_assoc values (-100, -200, -20, " +
            "1000, 1000, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_user values (-60, 76008, 76005, 'TESTUSR', " +
            "'TESTUSR', 1000, 1000, getDate(), getDate())"
            );
        assoc =
            MSSeqAssoc.findBySeqKeyOrganism(new Integer(-200), new Integer(1));
        System.setProperty("JOBSTREAM", "TESTUSR");
        ConfigReinitializer.reinit();
    }

    protected void tearDown() throws Exception
    {
        sqlMgr.executeUpdate("delete seq_source_assoc where _assoc_key = -100");
        sqlMgr.executeUpdate("delete prb_source where _source_key = -20");
        sqlMgr.executeUpdate("delete seq_sequence where _sequence_key = -200");
        sqlMgr.executeUpdate("delete mgi_user where _user_key = -60");
        assoc = null;
        sqlMgr = null;
        super.tearDown();
    }

    public void testGetAssocKey()
    {
        assertEquals(new Integer(-100), assoc.getAssocKey());
    }

    public void testGetMolecularSource() throws Exception
    {
        MolecularSource ms = assoc.getMolecularSource();
        assertEquals(new Integer(63474), ms.getSegmentTypeKey());
        assertEquals(new Integer(76026), ms.getVectorTypeKey());
        assertEquals(new Integer(1), ms.getOrganismKey());
        assertEquals(new Integer(-2), ms.getStrainKey());
        assertEquals(new Integer(-2), ms.getTissueKey());
        assertEquals(new Integer(74831), ms.getGenderKey());
        assertEquals(new Integer(75982), ms.getCellLineKey());
        assertNull(ms.getName());
        assertEquals("Not Resolved", ms.getAge());
        assertEquals(new Boolean(true), ms.getCuratorEdited());
    }

    public void testGetUpdateSQL() throws Exception
    {
        String expectedResults =
            "execute PRB_processSeqLoaderSource -100, -200, -20, " +
            "1, -2, -2, 74831, 75982, -60";
        assertEquals(expectedResults, assoc.getUpdateSQL());
    }

    public void testGetSeqKey()
    {
        assertEquals(new Integer(-200), assoc.getSeqKey());

    }

}
