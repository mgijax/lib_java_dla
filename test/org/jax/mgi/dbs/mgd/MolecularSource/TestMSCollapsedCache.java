package org.jax.mgi.dbs.mgd.MolecularSource;

import junit.framework.*;
import org.jax.mgi.shr.cache.*;
import org.jax.mgi.shr.dbutils.*;
import org.jax.mgi.shr.log.*;
import org.jax.mgi.dbs.mgd.lookup.*;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;

public class TestMSCollapsedCache
    extends TestCase {
  private MSCollapsedCache lookup = null;
  private long beforeInit;
  private SQLDataManager sqlMgr = null;
  private VocabKeyLookup segmentLookup;
  private VocabKeyLookup vectorLookup;
  private TissueKeyLookup tissueLookup;
  private VocabKeyLookup genderLookup;
  private VocabKeyLookup cellLineLookup;
  private StrainKeyLookup strainLookup;
  private OrganismKeyLookup organismLookup;


  public TestMSCollapsedCache(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
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
    sqlMgr.executeUpdate("delete prb_source where _source_key = -50");
    sqlMgr.executeUpdate("delete prb_source where _source_key = -60");
    sqlMgr.executeUpdate("delete prb_source where _source_key = -70");
    // source for organism only
    sqlMgr.executeUpdate(
            "insert into PRB_Source values (-50, " +
            segmentLookup.lookup("Not Applicable") + ", " +
            vectorLookup.lookup("Not Applicable") + ", 1, " +
            strainLookup.lookup("Not Specified") + ", " +
            tissueLookup.lookup("Not Specified") + ", " +
            genderLookup.lookup("Not Specified") + ", " +
            cellLineLookup.lookup("Not Specified") + ", null, null, " +
            "null, 'Not Applicable', -1.0, -1.0, 0, 1000, 1000, " +
            "getDate(), getDate())"
            );
    // source that is curatorEdited
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
    lookup = new MSCollapsedCache(new ConsoleLogger(), CacheConstants.FULL_CACHE);
  }

  protected void tearDown() throws Exception {
      sqlMgr.executeUpdate("delete prb_source where _source_key = -50");
      sqlMgr.executeUpdate("delete prb_source where _source_key = -60");
      sqlMgr.executeUpdate("delete prb_source where _source_key = -70");
      sqlMgr = null;
      lookup = null;
      super.tearDown();
  }

  public void testAddToCache() throws Exception
  {
    System.out.println("Printing cache...");
    lookup.printCache(System.out);
    System.out.println("End of Cache");
    lookup.initCache();
    System.out.println("Printing cache...");
    lookup.printCache(System.out);
    System.out.println("End of Cache");

    int size = lookup.cacheSize();
    // add a new MolecularSource that should not collapse
    MolecularSource newMS =
        new MolecularSource(
            new PRB_SourceDAO(new PRB_SourceKey(new Integer(-100)),
                              new PRB_SourceState()));
    newMS.setOrganismKey(new Integer(1));
    newMS.setCellLineKey(cellLineLookup.lookup("Not Applicable"));
    newMS.setGenderKey(genderLookup.lookup("Not Applicable"));
    newMS.setSegmentTypeKey(segmentLookup.lookup("Not Applicable"));
    newMS.setStrainKey(strainLookup.lookup("RR"));
    newMS.setTissueKey(tissueLookup.lookup("Not Applicable"));
    lookup.addToCache(newMS);
    int newSize = lookup.cacheSize();
    assertEquals(new Integer(newSize), new Integer(size + 1));

    // add a new MolecularSource that should collapse
    MolecularSource anotherMS =
        new MolecularSource(
            new PRB_SourceDAO(new PRB_SourceKey(new Integer(-200)),
                              new PRB_SourceState()));
    anotherMS.setOrganismKey(new Integer(1));
    anotherMS.setSegmentTypeKey(segmentLookup.lookup("Not Applicable"));
    anotherMS.setVectorTypeKey(vectorLookup.lookup("Not Applicable"));
    anotherMS.setCellLineKey(cellLineLookup.lookup("Not Specified"));
    anotherMS.setGenderKey(genderLookup.lookup("Not Specified"));
    anotherMS.setStrainKey(strainLookup.lookup("Not Specified"));
    anotherMS.setTissueKey(tissueLookup.lookup("brain"));
    lookup.addToCache(anotherMS);
    newSize = lookup.cacheSize();
    assertEquals(new Integer(newSize), new Integer(size + 1));

    // get a unresolved MS and then do a lookup
    MSRawAttributes raw = new MSRawAttributes();
    raw.setOrganism("mouse, laboratory");
    raw.setTissue("brain");
    MSAttrResolver attrResolver = new MSAttrResolver();
    MolecularSource unresolvedMS = attrResolver.resolveAttributes(raw);
    MolecularSource foundMS = lookup.lookup(unresolvedMS);
    assertEquals(new Integer(-70), foundMS.getMSKey());
  }

}
