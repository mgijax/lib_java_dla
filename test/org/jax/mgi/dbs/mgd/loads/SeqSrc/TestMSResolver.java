package org.jax.mgi.dbs.mgd.loads.SeqSrc;

import junit.framework.*;
import org.jax.mgi.shr.dbutils.*;
import org.jax.mgi.shr.cache.*;
import org.jax.mgi.dbs.mgd.*;
import org.jax.mgi.dbs.mgd.lookup.*;

public class TestMSResolver
    extends TestCase
{
    private MSResolver resolver = null;
    private SQLDataManager sqlMgr = null;
    private VocabKeyLookup segmentLookup;
    private VocabKeyLookup vectorLookup;
    private TissueKeyLookup tissueLookup;
    private VocabKeyLookup genderLookup;
    private VocabKeyLookup cellLineLookup;
    private StrainKeyLookup strainLookup;
    private OrganismKeyLookup organismLookup;


    public TestMSResolver(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        System.setProperty("SEQ_LOGICALDB", "Sequence DB");
        sqlMgr = new SQLDataManager();
        DBSchema schema = sqlMgr.getDBSchema();
        schema.createTriggers("PRB_Source"); // so it can be dropped
        schema.dropTriggers("PRB_Source");
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
        Translator translator =
                new Translator(TranslationTypeConstants.ORGANISM_TO_STRAIN,
                               CacheConstants.FULL_CACHE);

        sqlMgr.executeUpdate("delete prb_source where _source_key = -50");
        sqlMgr.executeUpdate("delete prb_source where _source_key = -60");
        sqlMgr.executeUpdate("delete prb_source where _source_key = -70");
        // source that is curatorEdited ... should not colllapse to this one
        sqlMgr.executeUpdate(
                "insert into PRB_Source values (-60, " +
                segmentLookup.lookup("Not Applicable") + ", " +
                vectorLookup.lookup("Not Applicable") + ", 1, " +
                strainLookup.lookup("Not Specified") + ", " +
                tissueLookup.lookup("Not Specified") + ", " +
                genderLookup.lookup("Female") + ", " +
                cellLineLookup.lookup("Not Specified") + ", null, null, " +
                "null, 'Not Applicable', -1.0, -1.0, 1, 1000, 1000, " +
                "getDate(), getDate())"
                );
        // source with organism and tissue
        sqlMgr.executeUpdate(
                "insert into PRB_Source values (-70, " +
                segmentLookup.lookup("Not Applicable") + ", " +
                vectorLookup.lookup("Not Applicable") + ", 1, " +
                strainLookup.lookup("Not Specified") + ", " +
                tissueLookup.lookup("brain") + ", " +
                genderLookup.lookup("Not Specified") + ", " +
                cellLineLookup.lookup("Not Specified") + ", null, null, " +
                "null, 'Not Applicable', -1.0, -1.0, 0, 1000, 1000, " +
                "getDate(), getDate())"
                );
        resolver = new MSResolver();
        schema.createTriggers("PRB_Source");
    }

    protected void tearDown() throws Exception
    {
        sqlMgr.executeUpdate("delete prb_source where _source_key = -60");
        sqlMgr.executeUpdate("delete prb_source where _source_key = -70");
        resolver = null;
        sqlMgr = null;
        super.tearDown();
    }

    public void testResolveByCollapse() throws MSException
    {
        MSRawAttributes raw = new MSRawAttributes();
        raw.setOrganism("Mus abbotti");
        raw.setTissue("brain");
        MolecularSource ms = resolver.resolve(raw);
        assertEquals(new Integer(-70), ms.getMSKey());
    }

    public void testResolveCuratorEdited() throws Exception
    {
        MSRawAttributes raw = new MSRawAttributes();
        raw.setOrganism("Mus abbotti");
        raw.setGender("Female");
        MolecularSource ms = resolver.resolve(raw);
        assertTrue(ms.getMSKey().intValue() > 0);
    }

}
