package org.jax.mgi.dbs.mgd.MolecularSource;

import junit.framework.*;
import org.jax.mgi.shr.dbutils.*;
import org.jax.mgi.dbs.mgd.*;
import org.jax.mgi.dbs.mgd.lookup.*;
import org.jax.mgi.shr.exception.MGIException;

public class TestMSAttrResolver
    extends TestCase
{
    private MSAttrResolver mSAttrResolver = null;
    private SQLDataManager sqlMgr = null;
    private VocabKeyLookup segmentLookup;
    private VocabKeyLookup vectorLookup;
    private TissueKeyLookup tissueLookup;
    private VocabKeyLookup genderLookup;
    private VocabKeyLookup cellLineLookup;
    private StrainKeyLookup strainLookup;
    private OrganismKeyLookup organismLookup;


    private Integer orgNotApplicableKey = null;
    private Integer orgNotSpecifiedKey = null;
    private Integer cellNotApplicableKey = null;
    private Integer cellNotSpecifiedKey = null;
    private Integer cellNotResolvedKey = null;
    private Integer genNotApplicableKey = null;
    private Integer genNotSpecifiedKey = null;
    private Integer genNotResolvedKey = null;
    private Integer strNotApplicableKey = null;
    private Integer strNotSpecifiedKey = null;
    private Integer strNotResolvedKey = null;
    private Integer tissNotApplicableKey = null;
    private Integer tissNotSpecifiedKey = null;
    private Integer tissNotResolvedKey = null;
    private Integer segNotApplicableKey = null;
    private Integer vecNotApplicableKey = null;



    public TestMSAttrResolver(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        sqlMgr = new SQLDataManager();
        runDeletes();
        runInserts();
        mSAttrResolver = new GBMSAttrResolver();
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
        this.segNotApplicableKey =
            this.segmentLookup.lookup("Not Applicable");
        this.vecNotApplicableKey =
            this.vectorLookup.lookup("Not Applicable");
        this.orgNotApplicableKey =
            this.organismLookup.lookup("Not Applicable");
        this.tissNotApplicableKey =
            this.tissueLookup.lookup("Not Applicable");
        this.tissNotSpecifiedKey = this.tissueLookup.lookup("Not Specified");
            this.tissNotResolvedKey = this.tissueLookup.lookup("Not Resolved");
        this.strNotApplicableKey =
            this.strainLookup.lookup("Not Applicable");
        this.strNotSpecifiedKey = this.strainLookup.lookup("Not Specified");
            this.strNotResolvedKey = this.strainLookup.lookup("Not Resolved");
        this.genNotApplicableKey =
            this.genderLookup.lookup("Not Applicable");
        this.genNotSpecifiedKey = this.genderLookup.lookup("Not Specified");
            this.genNotResolvedKey = this.genderLookup.lookup("Not Resolved");
        this.cellNotApplicableKey =
            this.cellLineLookup.lookup("Not Applicable");
        this.cellNotSpecifiedKey =
            this.cellLineLookup.lookup("Not Specified");
        this.cellNotResolvedKey = this.cellLineLookup.lookup("Not Resolved");

    }

    protected void tearDown() throws Exception
    {
        runDeletes();
        mSAttrResolver = null;
        sqlMgr = null;
        super.tearDown();
    }

    public void testResolveAttributes() throws Exception
    {
        MSRawAttributes rawAttr = new MSRawAttributes();
        rawAttr.setCellLine("B-cells");
        rawAttr.setGender("Feminine");
        rawAttr.setOrganism("mouse, laboratory");
        rawAttr.setLibraryName("RPCI/22 Clone Set");
        rawAttr.setStrain("CB100");
        rawAttr.setTissue("placenta day 21");
        MolecularSource ms =
            mSAttrResolver.resolveAttributes(rawAttr);
        assertEquals(new Integer(-60), ms.getCellLineKey());
        assertEquals("Not Resolved", ms.getAge());
        assertTrue(!(ms.getCuratorEdited().booleanValue()));
        assertEquals(new Integer(-50), ms.getGenderKey());
        assertNull(ms.getMSKey());
        assertEquals(segNotApplicableKey, ms.getSegmentTypeKey());
        assertEquals(vecNotApplicableKey, ms.getVectorTypeKey());
        assertEquals(organismLookup.lookup("mouse, laboratory"),
                     ms.getOrganismKey());
        assertEquals(new Integer(-50), ms.getStrainKey());
        assertNull(ms.getName());
        assertEquals(new Integer(-50), ms.getTissueKey());
    }

    public void testNullOrganism() throws Exception
    {
        MSRawAttributes rawAttr = new MSRawAttributes();
        rawAttr.setCellLine("B-cells");
        rawAttr.setGender("Feminine");
        rawAttr.setTissue("placenta day 21");
        try
        {
            // this should generate an error since organism is null
            MolecularSource ms =
                mSAttrResolver.resolveAttributes(rawAttr);
            assertTrue(false); // should not get here
        }
        catch (MGIException e)
        {
            assertEquals("Organism raw attribute was found to be null.",
                         e.getMessage());
        }
    }

    public void testNotSpecified() throws Exception
    {
        MSRawAttributes rawAttr = new MSRawAttributes();
        rawAttr.setOrganism("mouse, laboratory");
        MolecularSource ms =
            mSAttrResolver.resolveAttributes(rawAttr);
        assertEquals(this.cellNotSpecifiedKey, ms.getCellLineKey());
        assertEquals("Not Resolved", ms.getAge());
        assertTrue(!(ms.getCuratorEdited().booleanValue()));
        assertEquals(this.genNotSpecifiedKey, ms.getGenderKey());
        assertNull(ms.getMSKey());
        assertEquals(segNotApplicableKey, ms.getSegmentTypeKey());
        assertEquals(vecNotApplicableKey, ms.getVectorTypeKey());
        assertEquals(organismLookup.lookup("mouse, laboratory"),
                     ms.getOrganismKey());
        assertEquals(this.strNotSpecifiedKey, ms.getStrainKey());
        assertNull(ms.getName());
        assertEquals(this.tissNotSpecifiedKey, ms.getTissueKey());
    }

    public void testNotResolved() throws Exception
    {
        MSRawAttributes rawAttr = new MSRawAttributes();
        rawAttr.setOrganism("mouse, laboratory");
        rawAttr.setCellLine("unresolvable value");
        rawAttr.setGender("unresolvable value");
        rawAttr.setLibraryName("unresolvable value");
        rawAttr.setStrain("unresolvable value");
        rawAttr.setTissue("unresolvable value");
        MolecularSource ms =
            mSAttrResolver.resolveAttributes(rawAttr);
        assertEquals(this.cellNotResolvedKey, ms.getCellLineKey());
        assertEquals("Not Resolved", ms.getAge());
        assertTrue(!(ms.getCuratorEdited().booleanValue()));
        assertEquals(this.genNotResolvedKey, ms.getGenderKey());
        assertNull(ms.getMSKey());
        assertEquals(segNotApplicableKey, ms.getSegmentTypeKey());
        assertEquals(vecNotApplicableKey, ms.getVectorTypeKey());
        assertEquals(organismLookup.lookup("mouse, laboratory"),
                     ms.getOrganismKey());
        assertEquals(this.strNotResolvedKey, ms.getStrainKey());
        assertNull(ms.getName());
        assertEquals(this.tissNotResolvedKey, ms.getTissueKey());
    }

    public void testOtherOrganism() throws Exception
    {
        MSRawAttributes rawAttr = new MSRawAttributes();
        rawAttr.setOrganism("goat");
        MolecularSource ms =
            mSAttrResolver.resolveAttributes(rawAttr);
        assertEquals(this.cellNotApplicableKey, ms.getCellLineKey());
        assertEquals("Not Applicable", ms.getAge());
        assertTrue(!(ms.getCuratorEdited().booleanValue()));
        assertEquals(this.genNotApplicableKey, ms.getGenderKey());
        assertNull(ms.getMSKey());
        assertEquals(segNotApplicableKey, ms.getSegmentTypeKey());
        assertEquals(vecNotApplicableKey, ms.getVectorTypeKey());
        assertEquals(organismLookup.lookup("Other (see notes)"),
                     ms.getOrganismKey());
        assertEquals(this.strNotApplicableKey, ms.getStrainKey());
        assertNull(ms.getName());
        assertEquals(this.tissNotApplicableKey, ms.getTissueKey());
    }

    public void testHumanOrRat() throws Exception
    {
        MSRawAttributes rawAttr = new MSRawAttributes();
        rawAttr.setOrganism("human");
        MolecularSource ms =
            mSAttrResolver.resolveAttributes(rawAttr);
        assertEquals(this.cellNotApplicableKey, ms.getCellLineKey());
        assertEquals("Not Applicable", ms.getAge());
        assertTrue(!(ms.getCuratorEdited().booleanValue()));
        assertEquals(this.genNotApplicableKey, ms.getGenderKey());
        assertNull(ms.getMSKey());
        assertEquals(segNotApplicableKey, ms.getSegmentTypeKey());
        assertEquals(vecNotApplicableKey, ms.getVectorTypeKey());
        assertEquals(organismLookup.lookup("human"),
                     ms.getOrganismKey());
        assertEquals(this.strNotApplicableKey, ms.getStrainKey());
        assertNull(ms.getName());
        assertEquals(this.tissNotApplicableKey, ms.getTissueKey());
    }

    public void testOrganismToStrain() throws Exception
    {
        // test one
        MSRawAttributes rawAttr = new MSRawAttributes();
        rawAttr.setOrganism("Mus musculus musculus");
        rawAttr.setStrain("CB100");
        MolecularSource ms =
            mSAttrResolver.resolveAttributes(rawAttr);
        assertEquals(organismLookup.lookup("mouse, laboratory"),
                     ms.getOrganismKey());
        assertEquals(this.strainLookup.lookup("CB100"),
                     ms.getStrainKey());
        // test two
        rawAttr = new MSRawAttributes();
        rawAttr.setOrganism("Mus abbotti test");
        rawAttr.setStrain("CB100");
        ms = mSAttrResolver.resolveAttributes(rawAttr);
        assertEquals(organismLookup.lookup("mouse, laboratory"),
                     ms.getOrganismKey());
        assertEquals(new Integer(-120), ms.getStrainKey());

    }

    private void runInserts() throws Exception
    {
        sqlMgr.executeUpdate(
            "insert into prb_source values (-50, 63470, 76017, 1, 11966, -1, " +
            "74831, 75982, 64221, 'RPCI-2', null, 'Not Specified', -1.0, " +
            "-1.0, 1, 1060, 1060, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_translation values (-80, " +
            TranslationTypeConstants.LIBRARY + ", -50, " +
            "'RPCI/22 Clone Set', 1, 1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_organism values (-50, 'mouse', 'Mus musculus', " +
            "1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_translation values (-70, " +
            TranslationTypeConstants.ORGANISM + ", -50, " +
            "'Mus musculus orgainsm', 1, 1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into prb_strain values (-50, 'CB100', 1, 0, 0, " +
            "getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_translation values (-50, " +
            TranslationTypeConstants.STRAIN + ", -50, " +
            "'CB/100 strain', 1, 1000, 1000, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into prb_tissue values (-50, 'placenta1', " +
            "1, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_translation values (-60, " +
            TranslationTypeConstants.TISSUE + ", -50, " +
            "'placenta day 21', 1, 1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into voc_term values (-50, " +
            VocabularyTypeConstants.GENDER + ", 'Feminine', 'F', 1, 0, " +
            "1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into voc_term values (-60, " +
            VocabularyTypeConstants.CELLLINE + ", 'B-cell', null, 113, 0, " +
            "1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_translation values (-90, " +
            TranslationTypeConstants.GENDER + ", -50, 'she', 1, " +
            "1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_translation values (-100, " +
            TranslationTypeConstants.CELL + ", -60, " +
            "'B-cells', 1, 1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into voc_term values (-70,  " +
            VocabularyTypeConstants.SEQUENCETYPE + ", 'DNA', 'D', 1, 0, 1200, " +
            "1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_translation values (-110, " +
            TranslationTypeConstants.SEQUENCETYPE + ", -70, " +
            "'Deoxyribonucleic Acid', 1, 1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_organism values (-130, 'mouse', 'Mus musculus', " +
            "1200, 1200, getDate(), getDate())"
            );

        sqlMgr.executeUpdate(
            "insert into prb_strain values (-120, 'Mus abbotti', 1, 0, 0, " +
            "getDate(), getDate())"
            );

        sqlMgr.executeUpdate(
            "insert into mgi_translation values (-120, " +
            TranslationTypeConstants.ORGANISM_TO_STRAIN + ", -120, " +
            "'Mus abbotti test', 1, 1200, 1200, getDate(), getDate())"
            );
        sqlMgr.executeUpdate(
            "insert into mgi_translation values (-130, " +
            TranslationTypeConstants.ORGANISM + ", 1, " +
            "'Mus abbotti test', 1, 1200, 1200, getDate(), getDate())"
            );

    }

    private void runDeletes() throws Exception
    {
        sqlMgr.executeUpdate(
            "delete from prb_source where _source_key = -50"
            );
        sqlMgr.executeUpdate(
            "delete from mgi_translation where _translation_key = -80"
            );
        sqlMgr.executeUpdate(
            "delete from mgi_organism where _organism_key = -50"
            );
        sqlMgr.executeUpdate(
            "delete from mgi_organism where _organism_key = -130"
            );
        sqlMgr.executeUpdate(
            "delete from mgi_translation where _translation_key = -70"
            );
        sqlMgr.executeUpdate(
            "delete prb_strain where _strain_key = -50"
            );
        sqlMgr.executeUpdate(
            "delete prb_strain where _strain_key = -120"
            );
        sqlMgr.executeUpdate(
            "delete mgi_translation where _translation_key = -50"
            );
        sqlMgr.executeUpdate(
            "delete prb_tissue where _tissue_key = -50"
            );
        sqlMgr.executeUpdate(
            "delete mgi_translation where _translation_key = -60"
            );
        sqlMgr.executeUpdate(
            "delete from voc_term where _term_key = -50"
            );
        sqlMgr.executeUpdate(
            "delete from mgi_translation where _translation_key = -90"
            );
        sqlMgr.executeUpdate(
            "delete from voc_term where _term_key = -60"
            );
        sqlMgr.executeUpdate(
            "delete from mgi_translation where _translation_key = -100"
            );
        sqlMgr.executeUpdate(
            "delete from voc_term where _term_key = -70"
            );
        sqlMgr.executeUpdate(
            "delete from mgi_translation where _translation_key = -110"
            );
        sqlMgr.executeUpdate(
            "delete from mgi_translation where _translation_key = -120"
            );
        sqlMgr.executeUpdate(
            "delete from mgi_translation where _translation_key = -130"
            );

    }

}
