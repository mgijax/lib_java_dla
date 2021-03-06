package org.jax.mgi.dbs.mgd.loads.Acc;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.dao.ACC_AccessionState;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;

/**
 * An object that resolves a AccessionRawAttribute object to a
 *     ACC_AccessionState
 * @has
 *   <UL>
 *   <LI>Lookups to resolve attributes
 *   <LI>An ACC_AccessionState
 *   <LI>An AccessionRawAttributes
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Uses lookups to resolve raw accession attributes
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class AccAttributeResolver {

    // lookup a logical db key
    private LogicalDBLookup logicalDBLookup;

    /**
     * Constructs an AccAttributeResolver object by creating the necessary
     * lookups
     * @assumes Nothing
     * @effects Nothing
     * @throws CacheException if error creating a LogicalDBLookup cache
     * @throws ConfigException if error configuring a LogicalDBLookup's
     *          SQLDataManager
     * @throws DBException if error creating a LogicalDBLookups SQLDataManager
     */

    public AccAttributeResolver() throws  DBException, CacheException,
        ConfigException {
        logicalDBLookup = new LogicalDBLookup();
    }

    /**
     * resolves raw accession attributes and creates a ACC_AccessionState
     * @assumes Nothing
     * @effects Queries a database
     * @param rawAttributes A AccessionRawAttributes object
     * @param objectKey the primary key of the object associated with the accid
     * @return accessionState an ACC_AccessionState object
     * @throws CacheException - if logicalDBLookup cache error
     * @throws DBException - if logicalDBLookup error querying the database
     * @throws KeyNotFoundException if logical db key not found
     */

    public ACC_AccessionState resolveAttributes(
        AccessionRawAttributes rawAttributes, Integer objectKey)
        throws KeyNotFoundException, DBException, CacheException {

        // create a new accession state
        ACC_AccessionState state = new ACC_AccessionState();

        // resolve the logical db
        state.setLogicalDBKey(logicalDBLookup.lookup(
            rawAttributes.getLogicalDB()));

        // copy remaining raw attributes to the state
        state.setMGITypeKey(rawAttributes.getMgiType());
        state.setAccID(rawAttributes.getAccID());
        state.setNumericPart(rawAttributes.getNumericPart());
        state.setPrefixPart(rawAttributes.getPrefixPart());
        state.setPreferred(rawAttributes.getIsPreferred());
        state.setPrivateVal(rawAttributes.getIsPrivate());

        // set the object key
        state.setObjectKey(objectKey);

        return state;
    }
}
