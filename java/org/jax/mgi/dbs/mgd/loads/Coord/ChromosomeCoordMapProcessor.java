//  $Header
//  $Name

package org.jax.mgi.dbs.mgd.loads.Coord;

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
 * An object that finds an existing MAP_Coordinate database key
 *  for a given chromosome or creates one
 * @has
 *   <UL>
 *   <LI>A ChromosomeKeyLookup for getting a chromosome key by looking up the
 *       chromosome name
 *   <LI>A MRK_ChromosomeLookup - to get an MRK_Chromosome database object by
 *       chromosome key to get the sequenceNum value
 *   <LI>Also see superclass
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Gets a MAP_CoordinateKey from the database for the chromosome if one exists
 *        else resolves a CoordMapRawAttributes to create a MAP_CoordinateState and
 *        sets the MAP_CoordinateState in the passed in Coordinate object
 *   <LI> implements the superclass seqMGITypeKey() method to set the type to
 *        MRK_Chromosome
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class ChromosomeCoordMapProcessor extends CoordMapProcessor {
    private ChromosomeKeyLookup chrLookupByName;
    private MRK_ChromosomeLookup chrLookupByKey;


    /**
     * Constructs a ChromosomeMapProcessor object
     * @throws ConfigException if error getting organism
     * @throws DBException
     * @throws CacheException
     * @throws KeyNotFoundException
     * @throws TranslationException
     */

    public ChromosomeCoordMapProcessor() throws ConfigException,
        DBException, CacheException, KeyNotFoundException, TranslationException {
        chrLookupByName = new ChromosomeKeyLookup(coordCfg.getMapOrganism());
        chrLookupByKey = new MRK_ChromosomeLookup();
    }

    /**
     * Sets MGITypeKey for MRK_Chromosome
     */

    public void setMGITypeKey() {
        MGITypeKey = new Integer(MGITypeConstants.CHROMOSOME);
    }

    /**
     * Gets a MAP_CoordinateKey from the database  if one exists
     * else resolves a CoordMapRawAttributes to create a MAP_CoordinateState and
     * sets the MAP_CoordinateState in the passed in Coordinate object
     * @return Integer key for the Coordinate map found of created
     * @effects Queries a database
     * @param rawAttr Set of raw attributes to resolve
     * @param coordinate Coordinate object to set MAP_CoordinateState in if we
     *         need to create one
     * @throws CacheException - if caching error using lookups
     * @throws DBException  if database errors using lookups
     * @throws KeyNotFoundException if can't find Chromosome key
     * @throws ConfigException if configuration errors using chrLookupByKey or
     *         resolving
     * @throws TranslationException - if translation errors resolving
     * @throws KeyNotFoundException
     */

    public Integer process(CoordMapRawAttributes rawAttr, Coordinate coordinate)
           throws CacheException, DBException, KeyNotFoundException,
               ConfigException, TranslationException {
        // Is there a Chromosome object in the database for the chromosome?
        Integer chromosomeKey = chrLookupByName.lookup(rawAttr.getCoordMapObject());

        // get the sequenceNum for this chromosome object (won't get here if
        // chromosomeKey not found; KeyNotFoundException thrown)
        Integer sequenceNum = chrLookupByKey.findBySeqKey(chromosomeKey).getState().getSequenceNum();

        // now see if there is a map for this chromosome in the cache
        Integer mapKey = cache.lookup(chromosomeKey);

        // We didn't find a map for this chromosome - create one
        if (mapKey == null) {
            // resolve the raw to a state
            MAP_CoordinateState state = resolver.resolve(
                rawAttr, collectionKey, chromosomeKey, sequenceNum);
            // set the State in the Coordinate
            coordinate.setCoordinateMapState(state);
            // get the map key
            mapKey = coordinate.getCoordinateMapKey();
            // add the chromosomeKey to the cache so we don't create another one
            cache.addToCache(chromosomeKey, mapKey);
        }
        return mapKey;
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
