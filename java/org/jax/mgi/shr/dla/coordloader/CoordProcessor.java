package org.jax.mgi.shr.dla.coordloader;

import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.CoordMapCollectionKeyLookup;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureState;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_CollectionState;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_CollectionDAO;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.dbs.SchemaConstants;

/**
 * an object that resolves raw coordinate map attributes and adds coordinate
 * map collection, a coordinate map and a coord map feature to a database.
 * @has
 *   <UL>
 *   <LI>a logger
 *   <LI>various lookups for resolving
 *   </UL>
 * @does
 *   <UL>
 *   <LI>adds a resolves a coordinate map and adds it to a database
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordProcessor {
    // gets configuration values for coordinate loads
    private CoordLoadCfg coordCfg;

    // a lookup to get a collection key
    private CoordMapCollectionKeyLookup collectionLookup;

    // name of the collection
    String collectionName;

    // the collection key for this load
    private Integer collectionKey;

    // gets/creates the map object for the current input
    private CoordMapProcessor mapProcessor;

    // resolves CoordMapFeatureRAwAttributes to a MAP_Coord_FeatureState
    private CoordMapFeatureResolver featureResolver;

    // a stream for handling MGD DAO objects
    private SQLStream mgdStream;

    // a coordinate Exception Factory
    private CoordloaderExceptionFactory eFactory;

    public CoordProcessor(SQLStream stream) throws DBException, CacheException,
        ConfigException {
        mgdStream = stream;
        eFactory = new CoordloaderExceptionFactory();
        coordCfg = new CoordLoadCfg();
        collectionName = coordCfg.getMapCollectionName();
        // get the collection key or create a new collection if necessary
        setCollectionKey();
        mapProcessor = (CoordMapProcessor)coordCfg.getMapProcessorClass();
        // we have to explicitly set the collection key/ Configurator doesn't
        // take parameters
        mapProcessor.setCollectionKey(collectionKey);
        featureResolver =
            (CoordMapFeatureResolver)coordCfg.getMapFeatureResolverClass();

    }

    /**
     * deletes all Coordinates loaded by a given loader from a database
     * @assumes Nothing
     * @effects deletes coordinates from a database
     * @throws CoordLoaderException if error getting SQLDataManager or executing
     *         a delete.
     */

     public void deleteCoordinates() throws CoordloaderException{

       String spCall = "MAP_deleteByCollection '" + collectionName + "'";
       try {
         SQLDataManager sqlMgr = SQLDataManagerFactory.getShared(SchemaConstants.MGD);
         sqlMgr.executeSimpleProc(spCall);
       }
       catch (MGIException e) {
         CoordloaderException e1 =
             (CoordloaderException) eFactory.getException(
          CoordloaderExceptionFactory.ProcessDeletesErr, e);
         throw e1;
       }
     }

    public void processInput(CoordinateInput input) throws ConfigException,
            KeyNotFoundException, DBException, CacheException, TranslationException {
        // the compound object we are building
        Coordinate coordinate = new Coordinate(mgdStream);

        // get a map key
        Integer mapKey = mapProcessor.process(
                input.getCoordMapRawAttributes(), coordinate);

        // resolve the feature
        MAP_Coord_FeatureState state = featureResolver.resolve(
            input.getCoordMapFeatureRawAttributes(), mapKey);

        // set the feature in the coordMap object
        coordinate.setCoordMapFeatureState(state);

        // send the CoordinateMap object to its stream
        coordinate.sendToStream();
    }
    private void setCollectionKey () throws DBException, CacheException,
        ConfigException {
        collectionLookup = new CoordMapCollectionKeyLookup();
        try {
            collectionKey = collectionLookup.lookup(collectionName);
        }
        catch (KeyNotFoundException e) {
            //System.out.println("Collection Key not found - creating");
            String collectionAbbrev = coordCfg.getMapCollectionAbbrev();
            //System.out.println("Abbrev from config: " + collectionAbbrev);
            // if abbreviation isn't configured, just use the name
            if(collectionAbbrev == null || collectionAbbrev.equals("")) {
                collectionAbbrev = collectionName;
                //System.out.println("Abbrev as name: " + collectionAbbrev);
            }
            MAP_Coord_CollectionState collection = new MAP_Coord_CollectionState();
            collection.setName(collectionName);
            collection.setAbbreviation(collectionAbbrev);
            MAP_Coord_CollectionDAO dao = new MAP_Coord_CollectionDAO(collection);
            collectionKey = dao.getKey().getKey();
            mgdStream.insert(dao);
        }
    }
}

// $Log
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
