//  $Header
//  $Name

package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.dbs.mgd.dao.MAP_CoordinateState;
import org.jax.mgi.dbs.mgd.dao.MRK_ChromosomeLookup;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;

public class CoordMapResolver {

    // lookup to resolve map type
    private VocabKeyLookup mapTypeLookup;

    // lookup to resolve unit type
    private VocabKeyLookup unitLookup;

    /**
     * Constructs a CoordMapResolver
     * @throws TranslationException thrown if errors creating lookups
     * @throws ConfigException thrown if errors creating lookups
     * @throws DBException thrown if errors creating lookups
     * @throws CacheException thrown if errors creating lookups
     */

    public CoordMapResolver() throws TranslationException, ConfigException,
            DBException, CacheException {
        mapTypeLookup = new VocabKeyLookup(VocabularyTypeConstants.MAPTYPE);
        unitLookup = new VocabKeyLookup(VocabularyTypeConstants.MAPUNIT);
    }

    /**
     * resolves a CoordMapRawAttributes to a MAP_CoordinateState
     * @param rawAttr the set of raw attributes to resolve
     * @param collectionKey - the collection key for the map
     * @param objectKey - the object key for the map
     * @param seqNum - the order of the map within the collection
     * @return MAP_CoordinateState
     * @throws TranslationException if translation error using lookups
     * @throws ConfigException if config error using lookups
     * @throws DBException if database error using lookups
     * @throws CacheException if caching error  using lookups
     * @throws KeyNotFoundException if lookup unable to find key
     */
    public MAP_CoordinateState resolve(CoordMapRawAttributes rawAttr,
            Integer collectionKey, Integer objectKey, Integer seqNum)
                throws TranslationException, ConfigException, DBException,
                CacheException, KeyNotFoundException{
        // object we are building
        MAP_CoordinateState state = new MAP_CoordinateState();

        // set the collection key in the stat
        state.setCollectionKey(collectionKey);

        // set the object key in the state
        state.setObjectKey(objectKey);

        // set the MGI type key in the state
        state.setMGITypeKey(rawAttr.getMapMGITypeKey());

        // resolve and set the map type key in the state
        state.setMapTypeKey(mapTypeLookup.lookup(rawAttr.getMapType()));

        // resolve and se the unit key in the state
        state.setUnitsKey(unitLookup.lookup(rawAttr.getUnitType()));

        // set the length in the state
        state.setLength(new Integer(rawAttr.getLength()));

        // set the sequence number in the state
        state.setSequenceNum(seqNum);

        // set the map name in the state
        state.setName(rawAttr.getMapName());

        // set mapAbbrev to mapName if not defined
        String mapAbbrev = rawAttr.getMapAbbrev();
        if (mapAbbrev == null || mapAbbrev.equals("")) {
            state.setAbbreviation(rawAttr.getMapName());
        }
        else state.setAbbreviation(mapAbbrev);

        state.setVersion(rawAttr.getMapVersion());

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
