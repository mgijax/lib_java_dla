//  $Header
//  $Name

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
     * @throws DBException if database error resolving object key
     * @throws CacheException if caching error resolving object key
     * @throws KeyNotFoundException if object key cannot be resolved
     * load
     */

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
