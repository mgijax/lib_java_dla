package org.jax.mgi.shr.dla.coordloader;

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

public class CoordMapFeatureResolver {
    private CoordLoadCfg coordCfg;
    private AccessionLookup accLookup;
    private Integer MGITypeKey;

    public CoordMapFeatureResolver() throws DBException,
            CacheException, KeyNotFoundException, ConfigException{
        coordCfg = new CoordLoadCfg();
        Integer logicalDBKey = new LogicalDBLookup().lookup(
            coordCfg.getLogicalDB());
        MGITypeKey = new MGITypeLookup().lookup(coordCfg.getFeatureMGIType());
        accLookup = new AccessionLookup(logicalDBKey.intValue(),
            MGITypeKey.intValue(), AccessionLib.PREFERRED);
    }
    public MAP_Coord_FeatureState resolve(CoordMapFeatureRawAttributes rawAttr, Integer mapKey)
            throws DBException, CacheException, KeyNotFoundException{

        MAP_Coord_FeatureState state = new MAP_Coord_FeatureState();
        state.setMapKey(mapKey);
        state.setMGITypeKey(MGITypeKey);
        state.setObjectKey(accLookup.lookup(rawAttr.getObjectId()));
        state.setStartCoordinate(new Float(rawAttr.getStartCoord()));
        state.setEndCoordinate(new Float(rawAttr.getEndCoord()));
        state.setStrand(rawAttr.getStrand());
        return state;
    }
}