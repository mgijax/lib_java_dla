package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.dbs.mgd.lookup.StrainKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.TissueKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.OrganismKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.GenderKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.CellLineKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.SegmentKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.VectorKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.Translator;
import org.jax.mgi.dbs.mgd.TranslationTypeConstants;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheConstants;

/**
 * @is an object that resolves raw attributes for MolecularSource to their
 * corresponding database key values and performs vocabulary translation
 * @has the following lookup objects
 * <ul>
 * <li>TissueKeyLookup</li>
 * <li>OrganismKeyLookup</li>
 * <li>StrainKeyLookup</li>
 * <li>GenderKeyLookup</li>
 * <li>CellLineKeyLookup</li>
 * <li>SegmentKeyLookup</li>
 * <li>VectorKeyLookup</li>
 * </ul>
 * @does looks up the key values in the database for each attribute of a
 * MSRawAttributes using the lookup objects.
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class NonGBMSAttrResolver extends MSAttrResolver {

    /**
     * the following are lookups used by this class
     */
    private TissueKeyLookup tissueLookup;
    private OrganismKeyLookup organismLookup;
    private GenderKeyLookup genderLookup;
    private CellLineKeyLookup cellLineLookup;
    private StrainKeyLookup strainLookup;
    private SegmentKeyLookup segmentLookup;
    private VectorKeyLookup vectorLookup;
    private Translator organismToStrainTranslator;

    /**
     * the following string constants that will be resolved and used
     * for organism
     */
    private static final String NOT_SPECIFIED = "Not Specified";
    private static final String NOT_APPLICABLE = "Not Applicable";
    private static final String NOT_RESOLVED = "Not Resolved";
    private static final String MOUSE = "mouse, laboratory";
    private static final String HUMAN = "human";
    private static final String RAT = "rat";
    private static final String OTHER = "Other (see notes)";

    // keys for organism
    private Integer mouseKey = null;
    private Integer humanKey = null;
    private Integer ratKey = null;
    private Integer otherKey = null;
    private Integer orgNotApplicableKey = null;
    // keys for cellLine
    private Integer cellNotApplicableKey = null;
    private Integer cellNotSpecifiedKey = null;
    private Integer cellNotResolvedKey = null;
    // keys for gender
    private Integer genNotApplicableKey = null;
    private Integer genNotSpecifiedKey = null;
    private Integer genNotResolvedKey = null;
    // keys for strain
    private Integer strNotApplicableKey = null;
    private Integer strNotSpecifiedKey = null;
    private Integer strNotResolvedKey = null;
    // keys for tissue
    private Integer tissNotApplicableKey = null;
    private Integer tissNotSpecifiedKey = null;
    private Integer tissNotResolvedKey = null;
    // keys for segment type
    private Integer segNotApplicableKey = null;
    // keys for vector type
    private Integer vecNotApplicableKey = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String MSAttrResolverInitErr =
        MSExceptionFactory.MSAttrResolverInitErr;
    private static String AttrResolveErr = MSExceptionFactory.AttrResolveErr;
    private static String NullOrganism = MSExceptionFactory.NullOrganism;

    public NonGBMSAttrResolver() throws MSException {
        /**
         * initialize instance variables
         */
        try {
            this.tissueLookup = new TissueKeyLookup();
            this.organismLookup = new OrganismKeyLookup();
            this.strainLookup = new StrainKeyLookup();
            this.genderLookup = new GenderKeyLookup();
            this.cellLineLookup = new CellLineKeyLookup();
            this.segmentLookup = new SegmentKeyLookup();
            this.vectorLookup = new VectorKeyLookup();
            this.organismToStrainTranslator =
                new Translator(TranslationTypeConstants.ORGANISM_TO_STRAIN,
                               CacheConstants.FULL_CACHE);
            // set organism keys
            this.humanKey = this.organismLookup.lookup(HUMAN);
            this.mouseKey = this.organismLookup.lookup(MOUSE);
            this.ratKey = this.organismLookup.lookup(RAT);
            this.otherKey = this.organismLookup.lookup(OTHER);
            this.orgNotApplicableKey =
                this.organismLookup.lookup(NOT_APPLICABLE);
            // set cell line keys
            this.cellNotApplicableKey =
                this.cellLineLookup.lookup(NOT_APPLICABLE);
            this.cellNotSpecifiedKey =
                this.cellLineLookup.lookup(NOT_SPECIFIED);
            this.cellNotResolvedKey = this.cellLineLookup.lookup(NOT_RESOLVED);
            // set gender keys
            this.genNotApplicableKey =
                this.genderLookup.lookup(NOT_APPLICABLE);
            this.genNotSpecifiedKey = this.genderLookup.lookup(NOT_SPECIFIED);
            this.genNotResolvedKey = this.genderLookup.lookup(NOT_RESOLVED);
            // set strain keys
            this.strNotApplicableKey =
                this.strainLookup.lookup(NOT_APPLICABLE);
            this.strNotSpecifiedKey = this.strainLookup.lookup(NOT_SPECIFIED);
            this.strNotResolvedKey = this.strainLookup.lookup(NOT_RESOLVED);
            // set tissue keys
            this.tissNotApplicableKey =
                this.tissueLookup.lookup(NOT_APPLICABLE);
            this.tissNotSpecifiedKey = this.tissueLookup.lookup(NOT_SPECIFIED);
            this.tissNotResolvedKey = this.tissueLookup.lookup(NOT_RESOLVED);
            this.segNotApplicableKey =
                this.segmentLookup.lookup(NOT_APPLICABLE);
            this.vecNotApplicableKey =
                this.vectorLookup.lookup(NOT_APPLICABLE);
        }
        catch (MGIException e) {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(MSAttrResolverInitErr, e);
            throw e2;
        }
    }

    /**
     * resolve the given molecular source raw attributes to a MolecularSource
     * object with all its attributes set to known database keys, but
     * without the resolving of the actual MolecularSource object (see
     * MSResolver for resolving MolecularSource objects)
     * @assumes nothing
     * @effects nothing
     * @param rawAttr the raw attributes
     * @return the MolecularSource object
     * @throws MSException thrown if there is an error trying to resolve the
     * attributes
     */
    public MolecularSource resolveAttributes(MSRawAttributes rawAttr) throws
        MSException {
        MolecularSource ms = new MolecularSource();

        ms.setVectorTypeKey(this.vecNotApplicableKey);
        ms.setSegmentTypeKey(this.segNotApplicableKey);
        ms.setCellLineKey(this.cellNotApplicableKey);
        ms.setGenderKey(this.genNotApplicableKey);
        ms.setStrainKey(this.strNotApplicableKey);
        ms.setTissueKey(this.tissNotApplicableKey);
        ms.setAge(NOT_APPLICABLE);

        /**
         * resolve organism
         */
        Integer organismKey = null;
        String organism = rawAttr.getOrganism();
        if (organism == null) { // throw an exception for null organism
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e = (MSException)
                eFactory.getException(NullOrganism);
            throw e;
        }

        try {
            organismKey = this.organismLookup.lookup(organism);
        }
        catch (KeyNotFoundException e) {
            organismKey = this.otherKey;
            ms.setOrganismKey(this.otherKey);
        }
        catch (MGIException e) {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(AttrResolveErr, e);
            e2.bind("organism");
            throw e2;
        }
        /**
         * Check organism value. If not in mouse, human or rat then map it to
         * 'Other'
         */
        if (!organismKey.equals(this.mouseKey) &&
            !organismKey.equals(this.humanKey) &&
            !organismKey.equals(this.ratKey))
            ms.setOrganismKey(this.otherKey);

        return ms;
    }
}
