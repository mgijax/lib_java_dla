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
 * MSRawAttributes using the lookup objects. Throws Exception if any
 * MSRawAttributes are null or if they do not resolve.
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class GenericMSAttrResolver extends MSAttrResolver {


    public GenericMSAttrResolver() throws MSException {
        //super();
    }

    /**
     * resolve the given MSRawAttributes to a MolecularSource
     * object with all its attributes set to known database keys, but
     * without the resolving of the actual MolecularSource object (see
     * MSResolver for resolving MolecularSource objects)
     * @assumes nothing
     * @effects nothing
     * @param rawAttr the raw attributes
     * @return the MolecularSource object
     * @throws UnresolvedOrganismException if error resolving organism
     * @throw MSException thrown if any raw values are null or if there is an
     * error resolving the attributes
     */
    public MolecularSource resolveAttributes(MSRawAttributes rawAttr) throws
        MSException {
        MolecularSource ms = new MolecularSource();
        String rawValue = null;

        /**
         * vector type and segment type are always not applicable
         */
        ms.setVectorTypeKey(super.vecNotApplicableKey);
        ms.setSegmentTypeKey(super.segNotApplicableKey);
        /**
         * resolve organism
         */
        try {
            if ( (rawValue = rawAttr.getOrganism()) != null) {
                ms.setOrganismKey(this.organismLookup.lookup(rawValue));
            }
            else {
                throw new UnresolvedAttributeException("organism value is null");
            }
        }

        catch (MGIException e) {
            throw new UnresolvedAttributeException("organism: " + rawValue);
        }

        // resolve strain
        try {
            // lookup rawStrain and set strain
            if ( (rawValue = rawAttr.getStrain()) != null) {
                ms.setStrainKey(this.strainLookup.lookup(rawValue));
            }
            else {
                throw new UnresolvedAttributeException("strain value is null");
            }
        }
        catch (MGIException e) {
            throw new UnresolvedAttributeException("strain: " + rawValue);
        }

        // resolve tissue
        try {
          if ( (rawValue = rawAttr.getTissue()) != null) {
              ms.setTissueKey(this.tissueLookup.lookup(rawValue));
          }
          else {
            throw new UnresolvedAttributeException("tissue value is null");

          }
        }
        catch (MGIException e) {
            throw new UnresolvedAttributeException("tissue: " + rawValue);
        }
        // set age
        if ( (rawValue = rawAttr.getAge()) != null) {
            if (rawValue.equals(this.NOT_APPLICABLE) ||
                    rawValue.equals(this.NOT_RESOLVED) ||
                    rawValue.equals(this.NOT_SPECIFIED)) {
                  //System.out.println("GenericMSAttrResolver setting age: " + rawValue);
                  ms.setAge(rawValue);
            }
            else {
                throw new UnresolvedAttributeException("age: " + rawValue);
            }
        }
        else {
            throw new UnresolvedAttributeException("age value is null");
        }

        // resolve gender
        try {
            if ( (rawValue = rawAttr.getGender()) != null) {
              ms.setGenderKey(this.genderLookup.lookup(rawValue));
            }
            else {
                throw new UnresolvedAttributeException("gender value is null");
            }
        }
        catch (MGIException e) {
            throw new UnresolvedAttributeException("gender: " + rawValue);
        }
        // resolve cell line
        try {
            if ( (rawValue = rawAttr.getCellLine()) != null) {
                ms.setCellLineKey(this.cellLineLookup.lookup(rawValue));
            }
            else {
                throw new UnresolvedAttributeException("cell line value is null");
            }
        }
        catch (MGIException e) {
            throw new UnresolvedAttributeException("cell line: " + rawValue);
        }
        return ms;
    }
}
