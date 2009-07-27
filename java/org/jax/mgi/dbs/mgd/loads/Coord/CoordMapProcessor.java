package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.dla.loader.coord.*;



/**
 * A base class that provides attributes common to all CoordMapProcessors
 * @abstract defines the process and setMGITypeKey method
 * signatures.
 * @notes since the name of the subclass is configured, an instance of which is 
 * created by the Configuration class, it cannot take constructor parameters
 * therefore we provide the abstract method setMGITypeKey. In addition
 * only the creator of this class know what collection so we implement the
 * initCollection method
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
      * Constructs a CoordMapProcessor with a null collection key
      * @throws ConfigException thrown if there is an error creating configurator
      * @throws DBException thrown if there is an error creating the cache or resolver
      * @throws CacheException thrown if there is an error creating the cache or resolver
      * @throws TranslationException thrown if there is an error creating the resolver
      */

    public CoordMapProcessor() throws ConfigException,
        DBException, CacheException, TranslationException {
        coordCfg = new CoordLoadCfg();
		setMGITypeKey();
        resolver = new CoordMapResolver();
    }
	

	/**
	 * sets the collection key and initializes the collection cache; only the
	 * creator of a CoordMapProcessor knows which Collection
	 * @param collKey
	 * @throws org.jax.mgi.shr.dbutils.DBException
	 * @throws org.jax.mgi.shr.cache.CacheException
	 * @throws org.jax.mgi.shr.config.ConfigException
	 */

	public void initCollection(Integer collKey)
		throws DBException, CacheException, ConfigException {

		this.collectionKey = collKey;
		cache = new CoordMapKeyCache(collectionKey, coordCfg.getMapVersion(),MGITypeKey);
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
      * @throws KeyNotFoundException
      */

    public abstract Integer process(CoordMapRawAttributes rawAttr,
                                    Coordinate coordinate)
        throws CacheException, DBException, KeyNotFoundException,
        ConfigException, TranslationException;

    

    /**
     * method provided for subclasses to set their particular MGIType, this
     * method is called in this class's constructor as we need it in order to
     * construct the cache
     */
    public abstract void setMGITypeKey();

    
}
