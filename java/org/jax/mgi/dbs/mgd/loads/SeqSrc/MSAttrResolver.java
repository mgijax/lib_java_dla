package org.jax.mgi.dbs.mgd.loads.SeqSrc;

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

public abstract class MSAttrResolver {

    /**
     * the following are lookups used by this class
     */
    protected TissueKeyLookup tissueLookup;
    protected OrganismKeyLookup organismLookup;
    protected GenderKeyLookup genderLookup;
    protected CellLineKeyLookup cellLineLookup;
    protected StrainKeyLookup strainLookup;
    protected SegmentKeyLookup segmentLookup;
    protected VectorKeyLookup vectorLookup;
    protected Translator organismToStrainTranslator;

    /**
     * the following string constants that will be resolved and used
     * for organism
     */
    protected static final String NOT_SPECIFIED = "Not Specified";
    protected static final String NOT_APPLICABLE = "Not Applicable";
    protected static final String NOT_RESOLVED = "Not Resolved";
    protected static final String MOUSE = "mouse, laboratory";
    protected static final String HUMAN = "human";
    protected static final String RAT = "rat";
    protected static final String OTHER = "Other (see notes)";

    // keys for organism
    protected Integer mouseKey = null;
    protected Integer humanKey = null;
    protected Integer ratKey = null;
    protected Integer otherKey = null;
    protected Integer orgNotApplicableKey = null;
    // keys for cellLine
    protected Integer cellNotApplicableKey = null;
    protected Integer cellNotSpecifiedKey = null;
    protected Integer cellNotResolvedKey = null;
    // keys for gender
    protected Integer genNotApplicableKey = null;
    protected Integer genNotSpecifiedKey = null;
    protected Integer genNotResolvedKey = null;
    // keys for strain
    protected Integer strNotApplicableKey = null;
    protected Integer strNotSpecifiedKey = null;
    protected Integer strNotResolvedKey = null;
    // keys for tissue
    protected Integer tissNotApplicableKey = null;
    protected Integer tissNotSpecifiedKey = null;
    protected Integer tissNotResolvedKey = null;
    // keys for segment type
    protected Integer segNotApplicableKey = null;
    // keys for vector type
    protected Integer vecNotApplicableKey = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    protected static String MSAttrResolverInitErr =
        MSExceptionFactory.MSAttrResolverInitErr;
    protected static String AttrResolveErr = MSExceptionFactory.AttrResolveErr;
    protected static String NullOrganism = MSExceptionFactory.NullOrganism;

    public MSAttrResolver() throws MSException {
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
    public abstract MolecularSource resolveAttributes(MSRawAttributes rawAttr)
        throws MSException;
}