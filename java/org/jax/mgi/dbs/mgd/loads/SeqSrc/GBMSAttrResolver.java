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

public class GBMSAttrResolver extends MSAttrResolver {


    public GBMSAttrResolver() throws MSException {
        super();
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
        /**
         * vector type and segment type are always not applicable
         */
        ms.setVectorTypeKey(super.vecNotApplicableKey);
        ms.setSegmentTypeKey(super.segNotApplicableKey);
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
            organismKey = super.organismLookup.lookup(organism);
        }
        catch (KeyNotFoundException e) {
           throw new UnresolvedOrganismException(organism);
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
         * 'Other' and set all other values to 'Not Applicable'.
         */
        if (!organismKey.equals(super.mouseKey) &&
            !organismKey.equals(super.humanKey) &&
            !organismKey.equals(super.ratKey)) {
            ms.setOrganismKey(super.otherKey);
            ms.setCellLineKey(super.cellNotApplicableKey);
            ms.setGenderKey(super.genNotApplicableKey);
            ms.setStrainKey(super.strNotApplicableKey);
            ms.setTissueKey(super.tissNotApplicableKey);
            ms.setAge(NOT_APPLICABLE);
            /**
             * done
             */
            return ms;
        }

        /**
         * Check organism value. If in human or rat, then set all other
         * attributes to 'Not Applicable'
         */
        if (organismKey.equals(super.humanKey) ||
            organismKey.equals(this.ratKey)) {
            ms.setOrganismKey(organismKey);
            ms.setCellLineKey(this.cellNotApplicableKey);
            ms.setGenderKey(this.genNotApplicableKey);
            ms.setStrainKey(this.strNotApplicableKey);
            ms.setTissueKey(this.tissNotApplicableKey);
            ms.setAge(NOT_APPLICABLE);
            /**
             * done
             */
            return ms;
        }

        /**
         * Check organism value. If it translates to a known strain through
         * the Organism to Strain Translator then use that value for strain
         * in lieu of resolving the raw strain
         */

        // this boolean toggles to false if organism resolves to strain
        boolean okToResolveStrain = true;
        try {
            Integer strainKey =
                this.organismToStrainTranslator.translate(organism);
            if (strainKey != null)
            {
                ms.setStrainKey(strainKey);
                okToResolveStrain = false;
            }
        }
        catch (MGIException e) {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(AttrResolveErr, e);
            e2.bind("organism-to-strain");
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
                eFactory.getException(AttrResolveErr, e);
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
                eFactory.getException(AttrResolveErr, e);
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
                eFactory.getException(AttrResolveErr, e);
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
                    eFactory.getException(AttrResolveErr, e);
                e2.bind("strain");
                throw e2;
            }

        }
        // set age
        ms.setAge(NOT_RESOLVED);
        return ms;
    }
}
