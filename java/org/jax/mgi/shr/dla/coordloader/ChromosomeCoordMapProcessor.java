package org.jax.mgi.shr.dla.coordloader;

import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.dbs.mgd.dao.MAP_CoordinateState;
import org.jax.mgi.dbs.mgd.dao.MRK_ChromosomeLookup;
import org.jax.mgi.dbs.mgd.lookup.ChromosomeKeyLookup;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;

/**
 * An object that resolves a String chromosome name to a MAP_CoordinateKey
 * by doing a database lookup (may expand this to creating a a MAP_CoordinateState
 * and setting it in a passed in CoordinateMap object
 * @has
 *   <UL>
 *   <LI>A ChromosomeKeyLookup for getting a chromosome key by looking up the
 *       chromosome name
 *   <LI>A CoordmapKeyLookup for getting a map key by looking up the map object
 *       key (the chromosome key).
 *   <LI>A CoordMapCollectionKeyLookup for getting a collection key by
 *       looking up the collection name
 *   <LI>A configurator to get the configured collection name.
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Determines a map key for a chromosome name
 *   <LI>May, in the future, create a coordinate map object if none in the db
 *       for the chromosome in which case it would use a generic coordMap
 *       resolver
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class ChromosomeCoordMapProcessor extends CoordMapProcessor {
    private ChromosomeKeyLookup chrLookupByName;
    private MRK_ChromosomeLookup chrLookupByKey;

    public ChromosomeCoordMapProcessor() throws ConfigException,
        DBException, CacheException, KeyNotFoundException, TranslationException {
        chrLookupByName = new ChromosomeKeyLookup(coordCfg.getMapOrganism());
        chrLookupByKey = new MRK_ChromosomeLookup();

    }

    public void setMGITypeKey() {
        MGITypeKey = new Integer(MGITypeConstants.CHROMOSOME);
    }
    public Integer process(CoordMapRawAttributes rawAttr, Coordinate coordinate)
            throws CacheException, DBException, KeyNotFoundException, ConfigException,
            TranslationException {
        if(collectionKey == null) {
            // throw a fatal exception
        }
        Integer chromosomeKey = chrLookupByName.lookup(rawAttr.getCoordMapObject());
        Integer sequenceNum = chrLookupByKey.findBySeqKey(chromosomeKey).getState().getSequenceNum();
        Integer mapKey = cache.lookup(chromosomeKey);
        // create a new MAP_CoordinateState object and set it in the CoordinateMap
        // add the new Coordinate map to the cache
        // will need to lookup the sequenceNum and pass it to the resolver
        // need to create a chromosome sequence num lookup passing chr key
        if (mapKey == null) {
            //System.out.println("mapKey is null, making another map");
            MAP_CoordinateState state = resolver.resolve(
                rawAttr, collectionKey, chromosomeKey, sequenceNum);
            coordinate.setCoordinateMapState(state);
            mapKey = coordinate.getCoordinateMapKey();
            cache.addToCache(chromosomeKey, mapKey);
        }
        return mapKey;
    }
}