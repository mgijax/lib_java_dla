package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.cache.CacheConstants;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.ConsoleLogger;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;

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
     * the logger to use
     */
    protected Logger logger = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    protected static String MSResolverInitErr =
        MSExceptionFactory.MSResolverInitErr;
    protected static String ResolveErr = MSExceptionFactory.ResolveErr;
    protected static String CacheErr = MSExceptionFactory.CacheErr;
    protected static String KeyErr = MSExceptionFactory.KeyErr;


    /**
     * constructor
     * @throws MSException if a error occurs during initialization of the
     * collapsed molecular source cache or the MSAttrResolver
     */
    public MSResolver()
    throws MSException
    {
        this.logger = new ConsoleLogger();
        init(logger);
    }

    /**
     * constructor which accepts a Logger
     * @param logger the Logger to use
     * @throws MSException thrown if a error occurs during initialization of
     * the collapsed molecular source cache or the MSAttrResolver
     */
    public MSResolver(Logger logger)
    throws MSException
    {
        this.logger = logger;
        init(logger);
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
        MolecularSource newMS =
            this.attrResolver.resolveAttributes(attributes);
        /**
         * now resolve the MolecularSource by looking in the cache
         * of collapsed sources and if not there create a new one and add
         * it to the cache
         */
        MolecularSource existingMS = null;
        try
        {
            existingMS = this.msCollapsedCache.lookup(newMS);
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(ResolveErr, e);
            e2.bind(attributes.toString());
            throw e2;
        }
        if (existingMS == null)
        {
            // assign a new key to the new MolecularSource and add
            // it to the cache
            try {
                newMS.assignKey();
            }
            catch (MGIException e)
           {
               MSExceptionFactory eFactory = new MSExceptionFactory();
               MSException e2 = (MSException)
                   eFactory.getException(ResolveErr, e);
               e2.bind(attributes.toString());
               throw e2;
           }
            try
            {
                this.msCollapsedCache.addToCache(newMS);
            }
            catch (MGIException e)
            {
                MSExceptionFactory eFactory = new MSExceptionFactory();
               MSException e2 = (MSException)
                   eFactory.getException(CacheErr, e);
               e2.bind(newMS.getClass().getName());
               throw e2;
            }
            return newMS;
        }
        else
          return existingMS;
    }

    /**
     * resolve the given molecular source raw attributes to a MolecularSource
     * object without a database key
     * @assumes nothing
     * @effects nothing
     * @param attributes the raw attributes
     * @return the MolecularSource object
     * @throws MSException thrown if there is an error trying to resolve the
     * attributes
     */
    public MolecularSource resolveAttrsOnly(MSRawAttributes attributes)
        throws MSException
    {
        return this.attrResolver.resolveAttributes(attributes);
    }

    /**
     * initialize this instance
     * @assumes nothing
     * @effects the collapsed cache and the MSAttrResolver will be initialized
     * @param logger the logger to use
     * @throws MSException thrown if there is an error during initialization
     */
    protected void init(Logger logger) throws MSException
    {
        try
        {
            msCollapsedCache = new MSCollapsedCache(CacheConstants.FULL_CACHE);
            // instantiate the correct MSAttrResolver based on logicalDB
            SequenceLoadCfg cfg = new SequenceLoadCfg();
            LogicalDBLookup lookup = new LogicalDBLookup();
            int logicalDB = lookup.lookup(cfg.getLogicalDB()).intValue();
            //logger.logDebug("MSResolver logicalDB = " + logicalDB);
            //logger.logDebug("MSResolver ncbi logicalDBConstant = " + LogicalDBConstants.NCBI_GENE);
            if (logicalDB == LogicalDBConstants.REFSEQ ||
                    logicalDB == LogicalDBConstants.SEQUENCE) {
                attrResolver = new GBMSAttrResolver();
            }
            else if (logicalDB == LogicalDBConstants.NCBI_GENE ||
                logicalDB == LogicalDBConstants.ENSEMBL_GENE) {
                attrResolver = new GenericMSAttrResolver();
             }
            else {
                attrResolver = new NonGBMSAttrResolver();
            }
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(MSResolverInitErr, e);
            throw e2;
        }
    }

}