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

public class NonGBMSAttrResolver extends MSAttrResolver {

    public NonGBMSAttrResolver() throws MSException {
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
            ms.setOrganismKey(this.organismLookup.lookup(organism));
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

        return ms;
    }
}
