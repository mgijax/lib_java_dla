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
 * @abstract defines the process, setMGITypeKey, and setCollectionKey method
 * signatures.
 * @notes since the name of the subclass is configured, an instance of which is created by the
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
	// the subclass implements this method
        setMGITypeKey();
	cache = new CoordMapKeyCache(coordCfg.getMapVersion(),MGITypeKey);
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
      * @throws KeyNotFoundException
      */

    public abstract Integer process(CoordMapRawAttributes rawAttr,
                                    Coordinate coordinate)
        throws CacheException, DBException, KeyNotFoundException,
        ConfigException, TranslationException;

    /**
     * The following two methods exist because an instance of a subclass is
     * created by a Configurator and cannot take parameters. 
     * 1) setMGIType is abstract because only the subclass knows the MGIType. 
     * 2) initCache is implemented to do the work of a constructor; it must be called.
     *    the collectionKey parameter is null when the collection not in the 
     *    database (a new collection) 
     * 3) setCollectionKey is implemented and called at the time a new collection
     *    has been created.
     */

    /**
     * method provided for subclasses to set their particular MGIType, this
     * method is called in this class's constructor as we need it in order to
     * construct the cache
     */
    public abstract void setMGITypeKey();
    
    
    /**
     * get the _MGIType_key of this Map
     **/
    public Integer getMGITypeKey() {
	return MGITypeKey;
    }
    
     /**
     *  set the collection key 
     * @param collKey collection key
     */
   public void setCollectionKey(Integer collKey) {
        collectionKey = collKey;
	cache.setCollectionKey(collKey);
    }
   /**
    * get the collection key
    */
   public Integer getCollectionKey() {
       return collectionKey;
   }
}