package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.dbs.mgd.lookup.StrainKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.TissueKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.OrganismKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceState;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;

/**
 * @is an object that resolves raw attributes for MolecularSource to their
 * corresponding database key values and performs vocabulary translation
 * @has the following lookup objects
 * <ul>
 * <li>TissueKeyLookup</li>
 * <li>OrganismKeyLookup</li>
 * <li>StrainKeyLookup</li>
 * <li>VocabKeyLookup</li>
 * </ul>
 * @does looks up the key values in the database for each attribute of a
 * MSRawAttributes using the lookup objects.
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class MSAttrResolver {

    /**
     * the following are lookups used by this class
     */
    private TissueKeyLookup tissueLookup;
    private OrganismKeyLookup organismLookup;
    private VocabKeyLookup genderLookup;
    private VocabKeyLookup cellLineLookup;
    private StrainKeyLookup strainLookup;
    private VocabKeyLookup segmentLookup;
    private VocabKeyLookup vectorLookup;

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
    private static String ResolveErr = MSExceptionFactory.ResolveErr;
    private static String NullOrganism = MSExceptionFactory.NullOrganism;

    public MSAttrResolver() throws MSException {
        /**
         * initialize instance variables
         */
        try {
            this.tissueLookup = new TissueKeyLookup();
            this.organismLookup = new OrganismKeyLookup();
            this.strainLookup = new StrainKeyLookup();
            this.genderLookup =
                new VocabKeyLookup(VocabularyTypeConstants.GENDER);
            this.cellLineLookup =
                new VocabKeyLookup(VocabularyTypeConstants.CELLLINE);
            this.segmentLookup =
                new VocabKeyLookup(VocabularyTypeConstants.SEGMENTTYPE);
            this.vectorLookup =
                new VocabKeyLookup(VocabularyTypeConstants.VECTORTYPE);
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

    public MolecularSource resolveAttributes(MSRawAttributes rawAttr) throws
        MSException {
        MolecularSource ms = new MolecularSource();
        /**
         * vector type and segment type are always not applicable
         */
        ms.setVectorTypeKey(this.vecNotApplicableKey);
        ms.setSegmentTypeKey(this.segNotApplicableKey);
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
                eFactory.getException(ResolveErr, e);
            e2.bind("organism");
            throw e2;
        }
        /**
         * Check organism value. If not in mouse, human or rat then map it to
         * 'Other' and set all other values to 'Not Applicable'.
         */
        if (!organismKey.equals(this.mouseKey) &&
            !organismKey.equals(this.humanKey) &&
            !organismKey.equals(this.ratKey)) {
            ms.setOrganismKey(this.otherKey);
            ms.setCellLineKey(this.cellNotApplicableKey);
            ms.setGenderKey(this.genNotApplicableKey);
            ms.setStrainKey(this.strNotApplicableKey);
            ms.setTissueKey(this.tissNotApplicableKey);
            ms.setAge(this.NOT_APPLICABLE);
            /**
             * done
             */
            return ms;
        }

        /**
         * Check organism value. If in human or rat, then set all other
         * attributes to 'Not Applicable'
         */
        if (organismKey.equals(this.humanKey) ||
            organismKey.equals(this.ratKey)) {
            ms.setOrganismKey(organismKey);
            ms.setCellLineKey(this.cellNotApplicableKey);
            ms.setGenderKey(this.genNotApplicableKey);
            ms.setStrainKey(this.strNotApplicableKey);
            ms.setTissueKey(this.tissNotApplicableKey);
            ms.setAge(this.NOT_APPLICABLE);
            /**
             * done
             */
            return ms;
        }

        /**
         * Check organism value. If it resolves to a known strain then use
         * that value for strain in lieu of resolving the raw strain
         */
        // this boolean toggles to false if organism resolves to strain
        boolean okToResolveStrain = true;
        try {
            Integer strainKey = this.strainLookup.lookup(organism);
            ms.setStrainKey(strainKey);
            okToResolveStrain = false;
        }
        catch (KeyNotFoundException e) {
            okToResolveStrain = true;
        }
        catch (MGIException e) {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(ResolveErr, e);
            e2.bind("strain");
            throw e2;
        }

        /**
         * all special cases have been handled at this point. Now just resolve
         * all attributes in the typical way
         */

        ms.setOrganismKey(organismKey);
        // resolve tissue
        String rawValue = null;
        try {
            if ( (rawValue = rawAttr.getTissue()) != null)
                ms.setTissueKey(this.tissueLookup.lookup(rawValue));
            else
                ms.setTissueKey(this.tissNotSpecifiedKey);
        }
        catch (KeyNotFoundException e) {
            ms.setTissueKey(this.tissNotResolvedKey);
        }
        catch (MGIException e) {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(ResolveErr, e);
            e2.bind("tissue");
            throw e2;
        }
        // resolve gender
        try {
            if ( (rawValue = rawAttr.getGender()) != null)
                ms.setGenderKey(this.genderLookup.lookup(rawValue));
            else
                ms.setGenderKey(this.genNotSpecifiedKey);
        }
        catch (KeyNotFoundException e) {
            ms.setGenderKey(this.genNotResolvedKey);
        }
        catch (MGIException e) {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(ResolveErr, e);
            e2.bind("gender");
            throw e2;
        }
        // resolve cell line
        try {
            if ( (rawValue = rawAttr.getCellLine()) != null)
                ms.setCellLineKey(this.cellLineLookup.lookup(rawValue));
            else
                ms.setCellLineKey(this.cellNotSpecifiedKey);
        }
        catch (KeyNotFoundException e) {
            ms.setCellLineKey(this.cellNotResolvedKey);
        }
        catch (MGIException e) {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(ResolveErr, e);
            e2.bind("cell line");
            throw e2;
        }
        // resolve strain

        // if organism didn't resolve to strain
        if (okToResolveStrain) {
            try {
                // lookup rawStrain and set strain
                if ( (rawValue = rawAttr.getStrain()) != null) {
                    ms.setStrainKey(this.strainLookup.lookup(rawValue));
                }
                // rawStrain is null - set strain to 'Not Specified'
                else {
                    ms.setStrainKey(this.strNotSpecifiedKey);
                }
            }
            // lookup failed = set strain to 'Not Resolved'
            catch (KeyNotFoundException e) {
                ms.setStrainKey(this.strNotResolvedKey);
            }
            catch (MGIException e) {
                MSExceptionFactory eFactory = new MSExceptionFactory();
                MSException e2 = (MSException)
                    eFactory.getException(ResolveErr, e);
                e2.bind("strain");
                throw e2;
            }

        }
        // set age
        ms.setAge(this.NOT_RESOLVED);
        return ms;
    }
}