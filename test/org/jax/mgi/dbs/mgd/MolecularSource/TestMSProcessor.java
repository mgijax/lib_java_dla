package org.jax.mgi.dbs.mgd.MolecularSource;

import junit.framework.*;
import org.jax.mgi.shr.dbutils.*;
import org.jax.mgi.shr.dbutils.dao.*;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.*;
import org.jax.mgi.dbs.mgd.lookup.*;

public class TestMSProcessor
    extends TestCase
{
    private MSProcessor msProcessor = null;
    private SQLDataManager sqlMgr = null;
    private VocabKeyLookup segmentLookup;
    private VocabKeyLookup vectorLookup;
    private TissueKeyLookup tissueLookup;
    private VocabKeyLookup genderLookup;
    private VocabKeyLookup cellLineLookup;
    private StrainKeyLookup strainLookup;
    private OrganismKeyLookup organismLookup;


    public TestMSProcessor(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        sqlMgr = new SQLDataManager();
        this.segmentLookup =
            new VocabKeyLookup(VocabularyTypeConstants.SEGMENTTYPE);
        this.vectorLookup =
            new VocabKeyLookup(VocabularyTypeConstants.VECTORTYPE);
        this.tissueLookup = new TissueKeyLookup();
        this.organismLookup = new OrganismKeyLookup();
        this.strainLookup = new StrainKeyLookup();
        this.genderLookup =
            new VocabKeyLookup(VocabularyTypeConstants.GENDER);
        this.cellLineLookup =
            new VocabKeyLookup(VocabularyTypeConstants.CELLLINE);
        doDeletes();
        doInserts();
        msProcessor = new MSProcessor(new Inline_Stream(sqlMgr));
    }

    protected void tearDown() throws Exception
    {
        doDeletes();
        sqlMgr = null;
        msProcessor = null;
        super.tearDown();
    }

    /**
     * an error should be generated if the source could not be found for an
     * existing sequence
     * @throws MSException
     */
    public void testExistingSeqWithoutSrc() throws Exception
    {
        String accid = null;
        MSRawAttributes raw = new MSRawAttributes();
        raw.setOrganism("mouse, laboratory");
        try
        {
            msProcessor.processExistingSeqSrc("NoAccid",
                                              new Integer(-80) /* not in db */,
                                              raw);
        }
        catch (MGIException e)
        {
            assertEquals(e.getMessage(), "Could not find molecular source " +
            "for existing sequence with accid = NoAccid and organism key = 1");
        }
    }

    /**
     * test existing sequence with a changed anonymous source on fields not edited by
     * a curator and uncollapsed so that the source record is updated directly
     * @throws Exception
     */
    public void testExistingSeqWithChangedSrc() throws Exception
    {
      Integer C30 = this.cellLineLookup.lookup("C30");
      String sql = "select * from prb_source where _source_key " +
                   "= -40 and _cellLine_key = " + C30.toString();
      ResultsNavigator nav = sqlMgr.executeQuery(sql);
      assertTrue(!nav.next()); // assure no records found
      String accid = "T00313";
      MSRawAttributes raw = new MSRawAttributes();
      raw.setOrganism("mouse, laboratory");
      raw.setCellLine("C30");
      Integer seqKey = new Integer(-200);
      msProcessor.processExistingSeqSrc(accid, seqKey, raw);
      nav = sqlMgr.executeQuery(sql);
      assertTrue(nav.next()); // assure a record was found
    }

    /**
     * test existing sequence with a changed named source which should not get
     * updated in the database
     * @throws Exception
     */
    public void testExistingSeqWithChangedNamedSrc() throws Exception
    {
      Integer C30 = this.cellLineLookup.lookup("C30");
      String sql = "select * from prb_source where _source_key " +
                   "= -20 and _cellLine_key = " + C30.toString();
      ResultsNavigator nav = sqlMgr.executeQuery(sql);
      assertTrue(!nav.next()); // assure no records found
      String accid = "T00313";
      MSRawAttributes raw = new MSRawAttributes();
      raw.setOrganism("mouse, laboratory");
      raw.setLibraryName("name1");
      raw.setCellLine("C30");
      Integer seqKey = new Integer(-300);
      msProcessor.processExistingSeqSrc(accid, seqKey, raw);
      nav = sqlMgr.executeQuery(sql);
      assertTrue(!nav.next()); // assure no records still not found
    }

    /**
     * test existing sequence with a changed source which should cause
     * collapsing to a new source record and the seq_source_assoc table
     * should be checked to see that the source record was redirected
     * @throws Exception
     */
    public void testExistingSeqWithChangedSrcCollapse() throws Exception
    {
      String sql = "select _source_key from seq_source_assoc " +
                   "where _assoc_key = -100";
      String accid = "T00313";
      MSRawAttributes raw = new MSRawAttributes();
      raw.setOrganism("mouse, laboratory");
      raw.setCellLine("C57MG");
      Integer seqKey = new Integer(-200);
      msProcessor.processExistingSeqSrc(accid, seqKey, raw);
      ResultsNavigator nav = sqlMgr.executeQuery(sql);
      nav.next();
      RowReference row = nav.getRowReference();
      assertEquals(new Integer(-90), row.getInt(1));
    }

    /**
     * test existing sequence with a changed source on a field which has been
     * curator edited and should cause the change to not go into effect in
     * the source record
     * @throws Exception
     */
    public void testExistingSeqWithChangedSrcCuratorEdited() throws Exception
    {
      sqlMgr.executeUpdate("delete MGI_AttributeHistory where _object_key = -40");
      sqlMgr.executeUpdate("insert into MGI_AttributeHistory values (" +
                           "-40, 5, '_CellLine_key', 1060, 1060, getDate(), " +
                           "getDate())");
      sqlMgr.executeUpdate("update prb_source set isCuratorEdited = 1 where _source_key = -40");
      Integer C30 = this.cellLineLookup.lookup("C30");
      String sql = "select * from prb_source where _source_key " +
                   "= -40 and _cellLine_key = " + C30.toString();
      ResultsNavigator nav = sqlMgr.executeQuery(sql);
      assertTrue(!nav.next()); // assure no records found
      String accid = "T00313";
      MSRawAttributes raw = new MSRawAttributes();
      raw.setOrganism("mouse, laboratory");
      raw.setCellLine("C30");
      Integer seqKey = new Integer(-200);
      msProcessor.processExistingSeqSrc(accid, seqKey, raw);
      nav = sqlMgr.executeQuery(sql);
      assertTrue(!nav.next()); // assure record was not edited
      sqlMgr.executeUpdate("delete MGI_AttributeHistory where _object_key = -40");
    }

    /**
     * test collapsing to an existing source for a new sequence
     * @throws MSException
     */
    public void testNewSeqNamedSrc() throws Exception
    {
        String accid = null;
        MSRawAttributes raw = new MSRawAttributes();
        raw.setLibraryName("name1");
        raw.setOrganism("mouse, laboratory");
        MolecularSource ms =
            msProcessor.processNewSeqSrc(accid, raw);
        assertEquals(new Integer(-20), ms.getMSKey());
    }

    /**
     * test new sequence with a non-existing source so that a new
     * source record should be created
     * @throws Exception
     */
    public void testNewSeqNewSrc() throws Exception
    {
        String accid = null;
        MSRawAttributes raw = new MSRawAttributes();
        raw.setOrganism("mouse, laboratory");
        raw.setGender("Male");
        MolecularSource ms =
            msProcessor.processNewSeqSrc(accid, raw);
        assertTrue(ms.getMSKey().intValue() > 0); // a new key was created
        // cleanup new record
        sqlMgr.executeUpdate(
            "delete prb_source where _source_key = " + ms.getMSKey()
            );
    }

    /**
     * test new sequence with clones associated with anonymous sources
     * which will cause the given RawAttributes to collapse to an existing
     * source record and the anonymous records are not used.
     * @throws Exception
     */
    public void testNewSeqCloneAnnonSrc() throws Exception
    {
        String accid = "T00313";
        MSRawAttributes raw = new MSRawAttributes();
        raw.setOrganism("mouse, laboratory");
        raw.setCellLine("HeLa");
        MolecularSource ms =
            msProcessor.processNewSeqSrc(accid, raw);
        assertEquals(new Integer(-40), ms.getMSKey());
    }

    /**
     * test new sequence with one associated clone having a named source which
     * will cause the named source record to be used
     * @throws Exception
     */
    public void testNewSeqCloneNamedSrc() throws Exception
    {
        String accid = "T00613";
        MSRawAttributes raw = new MSRawAttributes();
        raw.setOrganism("mouse, laboratory");
        MolecularSource ms =
            msProcessor.processNewSeqSrc(accid, raw);
        assertEquals(new Integer(-20), ms.getMSKey());
    }

    /**
     * test new sequence with two associated clones with conflicting names which
     * will cause the raw attributes to be resolved and the clone records
     * ignored
     * @throws Exception
     */
    public void testNewSeqCloneNamedSrcConflict() throws Exception
    {
        String accid = "T00513";
        MSRawAttributes raw = new MSRawAttributes();
        raw.setOrganism("mouse, laboratory");
        raw.setCellLine("HeLa");
        MolecularSource ms =
            msProcessor.processNewSeqSrc(accid, raw);
        assertEquals(new Integer(-40), ms.getMSKey());
    }

    private void doInserts() throws Exception
    {
      Integer seg = segmentLookup.lookup("Not Applicable");
      Integer vec = vectorLookup.lookup("Not Applicable");
      Integer strn = strainLookup.lookup("Not Specified");
      Integer tis = tissueLookup.lookup("Not Specified");
      Integer gen = genderLookup.lookup("Not Specified");
      Integer cell = cellLineLookup.lookup("Not Specified");
      Integer HeLa = cellLineLookup.lookup("HeLa");
      Integer C57MG = cellLineLookup.lookup("C57MG");
        // a named source = name1
        sqlMgr.executeUpdate(
            "insert into prb_source values (-20, " + seg + ", " +
            vec + ", 1, " + strn + ", " + tis + ", " + gen + ", " +
            HeLa + ", " + "null, 'name1', null, 'Not Resolved', " +
            "-1.0, -1.0, 1, 1060, 1060, getDate(), getDate())"
            );
        // a named source = name2
        sqlMgr.executeUpdate(
            "insert into prb_source values (-30, " + seg + ", " +
            vec + ", 1, " + strn + ", " + tis + ", " + gen + ", " +
            HeLa + ", " + "null, 'name2', null, 'Not Resolved', -1.0, " +
            "-1.0, 1, 1060, 1060, getDate(), getDate())"
            );
        // an annonymous source
        sqlMgr.executeUpdate(
            "insert into prb_source values (-40, " + seg + ", " +
            vec + ", 1, " + strn + ", " + tis + ", " + gen + ", " +
            HeLa + ", " + "null, null, null, 'Not Resolved', -1.0, " +
            "-1.0, 0, 1060, 1060, getDate(), getDate())"
            );
        // an annonymous source
        sqlMgr.executeUpdate(
            "insert into prb_source values (-50, " + seg + ", " +
            vec + ", 1, " + strn + ", " + tis + ", " + gen + ", " +
            cell + ", " + "null, null, null, 'Not Resolved', " +
            "-1.0, -1.0, 1, 1060, 1060, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
        "insert into prb_source values (-90, " + seg + ", " +
            vec + ", 1, " + strn + ", " + tis + ", " + gen + ", " +
            C57MG + ", null, null, null, 'Not Resolved', " +
            "-1.0, -1.0, 0, 1060, 1060, getDate(), getDate())"
            );
        // a probe linked to a named source = name1
        sqlMgr.executeUpdate(
            "insert into prb_probe values (-20, 'clone1', null, -20, " +
            seg + ", " + vec + ", null, null, null, null, null, null, null, " +
            "null, 0, 1200, 1200, getDate(), getDate())"
            );
        // a probe linked to a named source = name2
        sqlMgr.executeUpdate(
            "insert into prb_probe values (-30, 'clone2', null, -30, " +
            seg + ", " + vec + ", null, null, null, null, null, null, null, " +
            "null, 0, 1200, 1200, getDate(), getDate())"
            );
        // a probe linked to an annoymous source
        sqlMgr.executeUpdate(
            "insert into prb_probe values (-40, 'clone3', null, -40, " +
            seg + ", " + vec + ", null, null, null, null, null, null, null, " +
            "null, 0, 1200, 1200, getDate(), getDate())"
            );
        // a probe linked to an annoymous source
        sqlMgr.executeUpdate(
            "insert into prb_probe values (-50, 'clone4', null, -50, " +
            seg + ", " + vec + ", null, null, null, null, null, null, null, " +
            "null, 0, 1200, 1200, getDate(), getDate())"
            );
        // accession linked to a probe without named source
        sqlMgr.executeUpdate(
            "insert into acc_accession values (-200, 'T00313', 'T', 313, " +
            "9, -40, 3, 0, 1, 1200, 1200, getDate(), getDate())"
            );
        // accession linked to a probe without named source
        sqlMgr.executeUpdate(
            "insert into acc_accession values (-300, 'T00313', 'T', 313, " +
            "9, -50, 3, 0, 1, 1200, 1200, getDate(), getDate())"
            );
        // accession linked to a probe with a conflicting named source
        sqlMgr.executeUpdate(
            "insert into acc_accession values (-500, 'T00513', 'T', 513, " +
            "9, -20, 3, 0, 1, 1200, 1200, getDate(), getDate())"
            );
        // accession linked to a probe with a conflicting named source
        sqlMgr.executeUpdate(
            "insert into acc_accession values (-600, 'T00513', 'T', 513, " +
            "9, -30, 3, 0, 1, 1200, 1200, getDate(), getDate())"
            );
        // accession linked to a probe without named source
        sqlMgr.executeUpdate(
            "insert into acc_accession values (-800, 'T00613', 'T', 613, " +
            "9, -40, 3, 0, 1, 1200, 1200, getDate(), getDate())"
            );
        // an accession linked to a probe with a named source
        sqlMgr.executeUpdate(
            "insert into acc_accession values (-900, 'T00613', 'T', 613, " +
            "9, -20, 3, 0, 1, 1200, 1200, getDate(), getDate())"
            );
        // a seq and seq_assoc linked to a annonoymous source (editable)
        sqlMgr.executeUpdate(
            "insert into seq_sequence values (-200, 76004, 7600, 76046, " +
            "76040, 34595, 'Mus musculus, Tpi, mRNA.', '1', null, 0, " +
            "'NotLoaded', 'NotLoaded', 'NotLoaded', 'NotLoaded', " +
            "'NotLoaded', 'NotLoaded', 'NotLoaded', 'NotLoaded', null, " +
            "getDate(), getDate(), 1000, 1000, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into seq_source_assoc values (-100, -200, -40, 1000, " +
            "1000, getDate(), getDate())"
            );
        // a seq and seq_assoc linked to a named source (uneditable)
        sqlMgr.executeUpdate(
            "insert into seq_sequence values (-300, 76004, 7600, 76046, " +
            "76040, 34595, 'Mus musculus, Tpi, mRNA.', '1', null, 0, " +
            "'NotLoaded', 'NotLoaded', 'NotLoaded', 'NotLoaded', " +
            "'NotLoaded', 'NotLoaded', 'NotLoaded', 'NotLoaded', null, " +
            "getDate(), getDate(), 1000, 1000, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into seq_source_assoc values (-200, -300, -20, 1000, " +
            "1000, getDate(), getDate())"
            );
    }

    private void doDeletes() throws Exception
    {
        sqlMgr.executeUpdate(
            "delete acc_accession where _accession_key = -200"
            );
        sqlMgr.executeUpdate(
            "delete acc_accession where _accession_key = -300"
            );
        sqlMgr.executeUpdate(
            "delete acc_accession where _accession_key = -500"
            );
        sqlMgr.executeUpdate(
            "delete acc_accession where _accession_key = -600"
            );
        sqlMgr.executeUpdate(
            "delete acc_accession where _accession_key = -800"
            );
        sqlMgr.executeUpdate(
            "delete acc_accession where _accession_key = -900"
            );
        sqlMgr.executeUpdate(
            "delete seq_sequence where _sequence_key = -200"
            );
        sqlMgr.executeUpdate(
            "delete seq_source_assoc where _assoc_key = -100"
            );
        sqlMgr.executeUpdate(
            "delete seq_sequence where _sequence_key = -300"
            );
        sqlMgr.executeUpdate(
            "delete seq_source_assoc where _assoc_key = -200"
            );
        sqlMgr.executeUpdate(
            "delete prb_probe where _probe_key = -20"
            );
        sqlMgr.executeUpdate(
            "delete prb_probe where _probe_key = -30"
            );
        sqlMgr.executeUpdate(
            "delete prb_probe where _probe_key = -40"
            );
        sqlMgr.executeUpdate(
            "delete prb_probe where _probe_key = -50"
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
        sqlMgr.executeUpdate(
            "delete prb_source where _source_key = -50"
            );
        sqlMgr.executeUpdate(
            "delete prb_source where _source_key = -90"
            );
    }

}
