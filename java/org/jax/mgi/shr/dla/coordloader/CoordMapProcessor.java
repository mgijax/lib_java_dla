//  $Header
//  $Name

package org.jax.mgi.shr.dla.coordloader;

import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;


/**
 * @is a base class that provides attributes common to all CoordMapProcessors
 * @abstract defines the process, setMGITypeKey, and setCollectionKey method
 * signatures.
 * @note since the name of the subclass is configured, an instance of which is created by the
 * Configuration class, it cannot take constructor parameters therefore
 * we provide the abstract methods setMGITypeKey, and setCollectionKey method
 * @has
 * <UL>
 * <LI>CoordMapKeyCache full cache loaded with maps from this loads collection
 *     and version.
 * <LI>CoordMapResolver - to resolve maps when we need to create a new one
 * <LI>
 * @does
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public abstract class CoordMapProcessor {
    // the collection key for this coordinate map
    protected Integer collectionKey;

    // a coordinate load configurator
    protected CoordLoadCfg coordCfg;

    // a cache of MAP_Coordinate _object_key keys with _Map_key values
    protected  CoordMapKeyCache cache;

    // The MGIType of the Coordinate Map's object key
    protected Integer MGITypeKey;

    // resolves a CoordMapRawAttributes to MAP_CoordinateState
    protected CoordMapResolver resolver;

    /**
      * Constructs a CoordMapProcessor
      * @throws ConfigException thrown if there is an error creating configurator
      * @throws DBException thrown if there is an error creating the cache or resolver
      * @throws CacheException thrown if there is an error creating the cache or resolver
      * @throws TranslationException thrown if there is an error creating the resolver
      */

    public CoordMapProcessor() throws ConfigException,
        DBException, CacheException, TranslationException {
        coordCfg = new CoordLoadCfg();
        setMGITypeKey();
        cache = new CoordMapKeyCache(collectionKey, coordCfg.getMapVersion(),MGITypeKey);
        resolver = new CoordMapResolver();
    }
    /**
      * method provided for subclasses to implement map processing
      * to get an existing map key or create a new map
      * @param rawAttr the set of raw attributes needed to create a collection
      *        a map and a feature
      * @param coordinate the compound objects that contains the DAO's for the
      *        collection, map, and feature
      * @return the map key
      * @throws ConfigException thrown if error using configurator
      * @throws DBException  thrown if error using resolver or lookups
      * @throws CacheException  thrown if error using cache or resolver
      * @throws TranslationException thrown if error using resolver
      */

    public abstract Integer process(CoordMapRawAttributes rawAttr, Coordinate coordinate)
        throws CacheException, DBException, KeyNotFoundException, ConfigException, TranslationException;

    /**
     * The following two methods exist because an instance of a subclass is
     * created by a Configurator and cannot take parameters. setMTIType is absract
     * because only the subclass knows the MGIType. setCollectionKey is implemented
     * because only the creator of the subclass knows which collection.
     */

    /**
     * method provided for subclasses to set their particular MGIType, this
     * method is called in this class's constructor as we need it in order to
     * construct the cache
     */
    public abstract void setMGITypeKey();

    /**
     *  set the collection key for this processors
     * @param collKey
     */
    public void setCollectionKey(Integer collKey) {
        collectionKey = collKey;
    }
}

//  $Log

 /**************************************************************************
 *
 * Warranty Disclaimer and Copyright Notice
 *
 *  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
 *  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
 *  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
 *  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
 *
 *  This software and data are provided to enhance knowledge and encourage
 *  progress in the scientific community and are to be used only for research
 *  and educational purposes.  Any reproduction or use for commercial purpose
 *  is prohibited without the prior express written permission of The Jackson
 *  Laboratory.
 *
 * Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
 *
 * All Rights Reserved
 *
 **************************************************************************/
