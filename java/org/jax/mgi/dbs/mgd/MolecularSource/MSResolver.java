package org.jax.mgi.dbs.mgd.MolecularSource;

import java.util.HashMap;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceDAO;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceState;
import org.jax.mgi.shr.cache.CacheConstants;

/**
 * @is an object for resolving MolecularSource raw attributes into
 * a MolecularSource object which has a database key and can be inserted
 * the database
 * @has a MSAttrResolver for resolving the raw attributes, a MSCollapsedCache
 * for reusing MolecularSource objects which share the same attributes
 * @does finds an appropriate MolecularSource object from the database
 * for a given set of MolecularSource raw attributes or creates a new
 * one based on the given attributes
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class MSResolver {

    /**
     * the cache of collapsed MolecularSource objects
     */
    protected MSCollapsedCache msCollapsedCache = null;

    /**
     * the MSAttributeResolver used for resolving the raw attributes
     */
    protected MSAttrResolver attrResolver = null;
    /**
     * reusable object for holding a MolecularSource after being
     * resolved by the MSAttrResolver but before being resolved by this class
     */
    private MolecularSource unresolvedMS = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    protected static String MSResolverInitErr =
        MSExceptionFactory.MSResolverInitErr;
    protected static String ResolveErr = MSExceptionFactory.ResolveErr;
    protected static String CacheErr = MSExceptionFactory.CacheErr;


    public MSResolver()
    throws MSException
    {
        try
        {
            msCollapsedCache = new MSCollapsedCache(CacheConstants.FULL_CACHE);
            attrResolver = new MSAttrResolver();
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(MSResolverInitErr, e);
            throw e2;
        }
    }

    /**
     * resolve the given set of raw molecular source attributes to a
     * PRB_Source record in the database. This methos implements collapsing
     * as discussed in the Molecular Source Requirements and Design doc
     * @param attributes the unresolved attributes for the Molecular Source
     * @return the MolecularSource object which represents a record in the
     * PRB_Source table.
     * @throws MSException thrown if any error occurs with the database
     * resource, the configuration resource or the cache handling of
     * collapsed MolecularSource objects
     */
    public MolecularSource resolve(MSRawAttributes attributes)
    throws MSException
    {
        // resolve attributes only
        this.unresolvedMS = this.attrResolver.resolveAttributes(attributes);
        /**
         * now resolve the MolecularSource by looking in the cache
         * of collapsed sources and if not there create a new one and add
         * it to the cache
         */
        MolecularSource resolvedMS = null;
        try
        {
            resolvedMS = this.msCollapsedCache.lookup(this.unresolvedMS);
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(ResolveErr, e);
            e2.bind(attributes.toString());
            throw e2;
        }
        if (resolvedMS == null)
        {
            // create a bare naked MolecularSource and copy attributes into it
            try {
                resolvedMS = new MolecularSource(
                    new PRB_SourceDAO(new PRB_SourceState()));
            }
            catch (MGIException e)
           {
               MSExceptionFactory eFactory = new MSExceptionFactory();
               MSException e2 = (MSException)
                   eFactory.getException(ResolveErr, e);
               e2.bind(attributes.toString());
               throw e2;
           }
            resolvedMS.setAge(this.unresolvedMS.getAge());
            resolvedMS.setCellLineKey(this.unresolvedMS.getCellLineKey());
            resolvedMS.setGenderKey(this.unresolvedMS.getGenderKey());
            resolvedMS.setName(this.unresolvedMS.getName());
            resolvedMS.setOrganismKey(this.unresolvedMS.getOrganismKey());
            resolvedMS.setStrainKey(this.unresolvedMS.getStrainKey());
            resolvedMS.setTissueKey(this.unresolvedMS.getTissueKey());
            try
            {
                this.msCollapsedCache.addToCache(resolvedMS);
            }
            catch (MGIException e)
            {
                MSExceptionFactory eFactory = new MSExceptionFactory();
               MSException e2 = (MSException)
                   eFactory.getException(CacheErr, e);
               e2.bind(resolvedMS.toString());
               throw e2;
            }
        }
        return resolvedMS;
    }

}