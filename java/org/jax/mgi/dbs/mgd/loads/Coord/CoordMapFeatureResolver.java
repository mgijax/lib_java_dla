package org.jax.mgi.dbs.mgd.loads.Coord;

import java.util.HashMap;
import java.util.Iterator;

import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureState;
import org.jax.mgi.dbs.mgd.loads.Seq.NCBISequenceLookup;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.lookup.MGITypeLookup;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;


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

    // to resolve the object keys for NCBI Gene Models
    private NCBISequenceLookup ncbiLookup;

    // resolved MGIType key
    private Integer MGITypeKey;

    DLALogger logger;
    /**
     * Constructs a CoordMapFeatureResolver object
     * @throws DBException
     * @throws CacheException
     * @throws KeyNotFoundException
     * @throws ConfigException
     */

    public CoordMapFeatureResolver() throws DBException,
            CacheException, KeyNotFoundException, ConfigException,  DLALoggingException{
        coordCfg = new CoordLoadCfg();
        Integer logicalDBKey = new LogicalDBLookup().lookup(
            coordCfg.getLogicalDB());
        MGITypeKey = new MGITypeLookup().lookup(coordCfg.getFeatureMGIType());
        accLookup = new AccessionLookup(logicalDBKey.intValue(),
            MGITypeKey.intValue(), AccessionLib.PREFERRED);
        ncbiLookup  = new NCBISequenceLookup();
        logger = DLALogger.getInstance();
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
                                          Integer mapKey,
                                          String mapObject)
            throws DBException, CacheException, KeyNotFoundException, ConfigException {

        MAP_Coord_FeatureState state = new MAP_Coord_FeatureState();
        state.setMapKey(mapKey);
        state.setMGITypeKey(MGITypeKey);
        String oId = rawAttr.getObjectId();
        Integer objectKey = null;
        logger.logdDebug("coordCfg.getMapCollectionName: " + coordCfg.getMapCollectionName());
        if (coordCfg.getMapCollectionName().equals("NCBI Gene Model")) {
            // sequences is a set of KeyValue objects key=seqKey, value=chromosome
            logger.logdDebug("using ncbiLookup");
            HashMap sequences = ncbiLookup.lookup(oId);
            for (Iterator mapI = sequences.keySet().iterator(); mapI.hasNext();) {
                Integer seqKey = (Integer) mapI.next();
                String chromosome = (String) sequences.get(seqKey);
                logger.logdDebug("seqKey: " +  seqKey + " chromosome: " + chromosome + " mapObject: " + mapObject);
                if (mapObject.equals(chromosome)) {
                    objectKey = seqKey;
                }
            }
        }
        else {
            logger.logdDebug("using accessionLookup");
            objectKey = accLookup.lookup(oId);
        }

        if(objectKey == null) {
            throw new KeyNotFoundException(oId, "AccessionLookup or NCBI Seq Lookup");
        }
        state.setObjectKey(objectKey);
        state.setStartCoordinate(new Double(rawAttr.getStartCoord()));
        state.setEndCoordinate(new Double(rawAttr.getEndCoord()));
        state.setStrand(rawAttr.getStrand());
        return state;
    }
}
