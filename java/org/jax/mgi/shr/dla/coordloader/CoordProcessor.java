//  $Header
//  $Name
package org.jax.mgi.shr.dla.coordloader;

import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
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
 * an object that resolves raw date and creates map collection, a coordinate map
 *  and a coord map feature objects in a database.
 * @has
 *   <UL>
 *   <LI>a logger
 *   <LI>a configurator
 *   <LI>a CoordMapCollectionKeyLookup to lookup the collections key for the coordinate
 *   <LI>CoordMapProcessor to determine/create the coordinate map for the coordinate
 *   <LI>CoordMapFeatureResolver to resolve map features
 *   </UL>
 * @does
 *   <UL>
 *   <LI>deletes existing collection, map, and features for a collection
 *   <LI>gets or creates the collection and the map for a coordinate
 *   >LI>creates a coordinate
 *   <LI>
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordProcessor {
    // gets configuration values for coordinate loads
    private CoordLoadCfg coordCfg;

    // name of the collection
    String collectionName;

    // abbreviation for the collection
    String collectionAbbrev;

    // the collection key for this load
    private Integer collectionKey;

    // gets existing or creates the coordinate map for the coordinate
    private CoordMapProcessor mapProcessor;

    // resolves CoordMapFeatureRqwAttributes to a MAP_Coord_FeatureState
    private CoordMapFeatureResolver featureResolver;

    // a stream for handling MGD DAO objects
    private SQLStream mgdStream;

    // a coordinate Exception Factory
    private CoordloaderExceptionFactory eFactory;

    /**
     * Constructs a CoordProcessor
     * @param stream stream for adding coordinates to an  MGD databaseth
     * @throws CacheException if error creating CoordMapFeatureResolver
     * @throws DBException if error creating CoordMapFeatureResolver
     * @throws ConfigException if error creating configurator or collection name
     *          not found. Note  that coordCfg.getMapCollectionAbbrev supplies
     *          a default value if not configured.
     * @throws KeyNotFoundException if error creating CoordMapFeatureResolver
     */

    public CoordProcessor(SQLStream stream) throws DBException, CacheException,
        ConfigException, KeyNotFoundException {

        mgdStream = stream;
        eFactory = new CoordloaderExceptionFactory();
        coordCfg = new CoordLoadCfg();
        collectionName = coordCfg.getMapCollectionName();
        collectionAbbrev = coordCfg.getMapCollectionAbbrev();

        // set collection abbreviation to the name value if not configured
        if(collectionAbbrev == null || collectionAbbrev.equals("")) {
                collectionAbbrev = collectionName;
        }
        // create an instance of a CoordMapProcessor from configuration
        mapProcessor = (CoordMapProcessor)coordCfg.getMapProcessorClass();

        featureResolver = new CoordMapFeatureResolver();
    }
    /**
     * deletes the collection, all coordinate maps and features for the
     * collection. Creates a new collection object and sets the collection key
     * in the map processor
     * @throws CoordloaderException if error deleting collectin and associated
     *    maps and features
     * @throws ConfigException if error creating new collection
     * @throws DBException if error creating new collection
     */
    public void preprocess() throws CoordloaderException, ConfigException,
        DBException {
        deleteCoordinates();
        createCollection();
    }

     /**
     * Adds a Coordinate Collection, Coordinate Maps for the Collections and
     * Coordinate Features to the database
     * @assumes Nothing
     * @effects queries and inserts into a database
     * @param input CoordinateInput object - a set of raw attributes to resolve
     *        and add to the database
     * @throws KeyNotFoundException if erros processing  map or resolving feature
     * @throws DBException if erros creating  map object, resolving feature,
     *      or executing the stream
     * @throws CacheException if errors creating map object or resolving feature
     * @throws TranslationException if errors creating map object
     */

    public void processInput(CoordinateInput input) throws ConfigException,
            KeyNotFoundException, DBException, CacheException, TranslationException {
        // the compound DAO object we are building
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

    /**
     * deletes the coordinate collection, all coordinate maps and features for the
     * collection
     * @assumes Nothing
     * @effects deletes records from a database
     * @throws CoordLoaderException if error getting SQLDataManager or executing
     *         a delete.
     */

     private void deleteCoordinates() throws CoordloaderException{

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

    /**
     * Creates a collection object and sets the collection key in the map processor
     * @throws DBException if errors creating or inserting collection DAO
     * @throws ConfigException if error collection DAO
     */
    private void createCollection () throws  DBException, ConfigException {
            // create the state
            MAP_Coord_CollectionState collection = new MAP_Coord_CollectionState();

            // set the collection name and abbreviation
            collection.setName(collectionName);
            collection.setAbbreviation(collectionAbbrev);

            // create the dao
            MAP_Coord_CollectionDAO dao = new MAP_Coord_CollectionDAO(collection);

            // get the collection key from the dao
            collectionKey = dao.getKey().getKey();

            // insert the collection
            mgdStream.insert(dao);
        // we have to explicitly set the collection key in the CoordMapProcessor
        // because it is configured; Configurator does not take parameters
        mapProcessor.setCollectionKey(collectionKey);
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
