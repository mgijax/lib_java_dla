package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureState;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.lookup.MGITypeLookup;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ConfigException;


/**
 * An object that resolves a CoordMapFeatureRawAttributes to a MAP_Coord_FeatureState
 * @has
 *   <UL>
 *   <LI> AccessionLookup to resolve the Object key
 *   <LI> MGITypeLookup to resolve the MGIType of the Object key
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>resolves  a CoordMapFeatureRawAttributes to a MAP_Coord_FeatureState
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordMapFeatureResolver {
    // coordinate load configurator
    private CoordLoadCfg coordCfg;

    // to resolve the object key
    private AccessionLookup accLookup;

    // resolved MGIType key
    private Integer MGITypeKey;

    /**
     * Constructs a CoordMapFeatureResolver object
     * @throws DBException
     * @throws CacheException
     * @throws KeyNotFoundException
     * @throws ConfigException
     */

    public CoordMapFeatureResolver() throws DBException,
            CacheException, KeyNotFoundException, ConfigException{
        coordCfg = new CoordLoadCfg();
        Integer logicalDBKey = new LogicalDBLookup().lookup(
            coordCfg.getLogicalDB());
        MGITypeKey = new MGITypeLookup().lookup(coordCfg.getFeatureMGIType());
        accLookup = new AccessionLookup(logicalDBKey.intValue(),
            MGITypeKey.intValue(), AccessionLib.PREFERRED);
    }

    /**
     * resolves a CoordMapFeatureRawAttributes to a MAP_Coord_FeatureState given
     * a mapKey
     * @effects queries a database
     * @param rawAttr coordinat raw attributes
     * @param mapKey map key
     * @throws DBException if database error resolving object key
     * @throws CacheException if caching error resolving object key
     * @throws KeyNotFoundException if object key cannot be resolved
     * load
     * @return MAP_Coord_FeatureState
     */

    public MAP_Coord_FeatureState resolve(CoordMapFeatureRawAttributes rawAttr,
                                          Integer mapKey)
            throws DBException, CacheException, KeyNotFoundException{

        MAP_Coord_FeatureState state = new MAP_Coord_FeatureState();
        state.setMapKey(mapKey);
        state.setMGITypeKey(MGITypeKey);
        String oId = rawAttr.getObjectId();
        Integer objectKey = accLookup.lookup(oId);
        if(objectKey == null) {
            throw new KeyNotFoundException(oId, "AccessionLookup");
        }
        state.setObjectKey(objectKey);
        state.setStartCoordinate(new Double(rawAttr.getStartCoord()));
        state.setEndCoordinate(new Double(rawAttr.getEndCoord()));
        state.setStrand(rawAttr.getStrand());
        return state;
    }
}
