package org.jax.mgi.shr.dla.coordloader;

import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;

public abstract class CoordMapProcessor {
    protected Integer collectionKey;
    protected CoordLoadCfg coordCfg;
    // a cache of MAP_Coordinate _object_key keys with _coordinate_key values
    protected  CoordMapKeyCache cache;
    protected Integer MGITypeKey;

    // resolves a CoordMapRawAttributes to MAP_CoordinateState
    protected CoordMapResolver resolver;

    public CoordMapProcessor() throws ConfigException,
        DBException, CacheException, TranslationException {
        coordCfg = new CoordLoadCfg();
        setMGITypeKey();
        cache = new CoordMapKeyCache(collectionKey, coordCfg.getMapVersion(),MGITypeKey);
        resolver = new CoordMapResolver();
    }
    // Gets an existing coordinate map key for rawAttr, else creates a new
    // coordinate map object
    public abstract Integer process(CoordMapRawAttributes rawAttr, Coordinate coordinate)
        throws CacheException, DBException, KeyNotFoundException, ConfigException, TranslationException;

    public abstract void setMGITypeKey();

    // this class instantiated using Configurator which cannot take parameters
    // need to explicitly set the collection key
   public void setCollectionKey(Integer collKey) {
               collectionKey = collKey;
   }

}