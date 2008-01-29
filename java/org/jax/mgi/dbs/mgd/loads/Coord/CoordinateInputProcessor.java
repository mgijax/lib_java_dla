package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureState;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_CollectionState;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_CollectionDAO;
import org.jax.mgi.dbs.mgd.loads.Coord.CoordMapProcessor;
import org.jax.mgi.dbs.mgd.loads.Coord.CoordMapFeatureResolver;
import org.jax.mgi.dbs.mgd.lookup.CoordMapCollectionKeyLookup;
import org.jax.mgi.shr.dla.loader.coord.CoordloaderExceptionFactory;
import org.jax.mgi.shr.dla.input.CoordinateInput;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.dla.loader.coord.CoordloaderException;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.dbs.SchemaConstants;

/**
 * An object that resolves raw date and creates map collection, a coordinate map
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

public class CoordinateInputProcessor {
    // gets configuration values for coordinate loads
    private CoordLoadCfg loadCfg;

    // name of the collection
    private String collectionName;

    // abbreviation for the collection
    private String collectionAbbrev;

    // delete_reload or incremental
    private String loadMode;
 
    // count of current number of features found to already be in MGI
    private int featureInMGICtr = 0;

    // count of current number of features found to be in MGI, but with
    // different attribute(s)
    private int featureDiffInMGICtr = 0;

    // count of current number of feature objects (e.g. sequence/marker)
    // not found in MGI
    private int featureObjectNotInMGICtr = 0;

    // true if collection is not in the database
    private Boolean isNewCollection;

    // Lookup a collection key
    private CoordMapCollectionKeyLookup collectionLookup;

    // the collection key for this load
    private Integer collectionKey;

    // gets existing or creates the coordinate map for the coordinate
    private CoordMapProcessor mapProcessor;

    // resolves CoordMapFeatureRawAttributes to a MAP_Coord_FeatureState
    private CoordMapFeatureResolver featureResolver;
    
    // needed to execute Queries
    private SQLDataManager sqlMgr;

    // a stream for handling MGD DAO objects
    private SQLStream mgdStream;

    // a coordinate Exception Factory
    private CoordloaderExceptionFactory eFactory;
    
    private DLALogger logger;
    /**
     * Constructs a CoordinateInputProcessor
     * @param stream stream for adding coordinates to an  MGD databaseth
     * @throws CacheException if error creating CoordMapFeatureResolver
     * @throws DBException if error creating CoordMapFeatureResolver
     * @throws ConfigException if error creating configurator or collection name
     *          not found. Note  that loadCfg.getMapCollectionAbbrev supplies
     *          a default value if not configured.
     * @throws KeyNotFoundException if error creating CoordMapFeatureResolver
     */

    public CoordinateInputProcessor(SQLStream stream) throws DBException, CacheException,
        ConfigException, KeyNotFoundException, DLALoggingException, TranslationException,
	CoordloaderException {
   
        mgdStream = stream;
	
	sqlMgr = SQLDataManagerFactory.getShared(SchemaConstants.MGD);
        eFactory = new CoordloaderExceptionFactory();
	
	// set values from configuration
        loadCfg = new CoordLoadCfg();
        collectionName = loadCfg.getMapCollectionName();
	loadMode = loadCfg.getLoadMode();
	collectionAbbrev = loadCfg.getMapCollectionAbbrev();

        // set collection abbreviation to the collectionName value if not configured
        if(collectionAbbrev == null || collectionAbbrev.equals("")) {
                collectionAbbrev = collectionName;
        }
        // create an instance of a CoordMapProcessor from configuration
        mapProcessor = (CoordMapProcessor)loadCfg.getMapProcessorClass();
	
	// delete reload load mode
	if (loadMode.equals("delete_reload")) {
	    // this removes the collection too
	    deleteCoordinates();
	}
	// find the collection in mgi or create one
	determineCollection();
	
	featureResolver = new CoordMapFeatureResolver(isNewCollection);
	logger = DLALogger.getInstance();
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
     * @throws ConfigException if there is an error accessing the configuration
     */

    public void processInput(CoordinateInput input) throws ConfigException,
            KeyNotFoundException, DBException, CacheException, TranslationException {
        // the compound DAO object we are building
        Coordinate coordinate = new Coordinate(mgdStream);

        // get a map key
        Integer mapKey = mapProcessor.process(
                input.getCoordMapRawAttributes(), coordinate);

        // resolve the feature
	MAP_Coord_FeatureState state = null; 
	try {
            state = featureResolver.resolve(
            input.getCoordMapFeatureRawAttributes(), mapKey);
	}
	catch (FeatureInMGIException e1) {
	    // There is already a Feature for this object ID in MGI
	    featureInMGICtr++;
	    return;
	}
	catch (FeatureDifferentInMGIException e2) {
            // There is a Feature in MGI with different attributes 
	    // for this object ID 
            featureDiffInMGICtr++;
            return;
        }
	catch (FeatureObjectNotInMGIException e3) {
	    // This object ID does not exist in the database
	    featureObjectNotInMGICtr++;
	    logger.logvInfo(e3.getMessage(), true);
    	    return;
	}

        // set the feature in the coordinate object
        coordinate.setCoordMapFeatureState(state);

        // send the CoordinateMap object to its stream
        coordinate.sendToStream();
    }
    /**
     * get the number of features already in MGI
     *
     */
	public int getFeatureInMGICount() {
	    return featureInMGICtr;
	}
    /**
     * get the number of features already in MGI, but with different attributes
     *
     */
        public int getFeatureDiffInMGICount() {
            return featureDiffInMGICtr;
        }


    /**
     * get the number of feature objects not in MGI
     *
     */
        public int getFeatureObjectNotInMGICount() {
            return featureObjectNotInMGICtr;
        }


    /**
     * deletes the coordinate collection, all coordinate maps and features for the
     * collection
     * @assumes Nothing
     * @effects deletes records from a database
     * @throws CoordloaderException if error getting SQLDataManager or executing
     *         a delete.
     */

     private void deleteCoordinates() throws CoordloaderException{

       String spCall = "MAP_deleteByCollection '" + collectionName + "'";
       try {
         sqlMgr.executeSimpleProc(spCall);
       }
       catch (MGIException e) {
         CoordloaderException e1 =
             (CoordloaderException) eFactory.getException(
          CoordloaderExceptionFactory.ProcessDeletesErr, e);
         throw e1;
       }
     }

    private void determineCollection () throws  DBException, ConfigException, 
	    CacheException {
	    CoordMapCollectionKeyLookup collectionLookup = 
		new CoordMapCollectionKeyLookup();
	    collectionKey = collectionLookup.lookup(collectionName);
	    
	    if (collectionKey == null) {
		mapProcessor.setCollectionKey(createCollection());
		isNewCollection = Boolean.TRUE;
	    }
	    else {
		mapProcessor.setCollectionKey(collectionKey);
		isNewCollection = Boolean.FALSE;
	    }
    }     
    /**
     * Creates a collection object and sets the collection key in the map processor
     * @throws DBException if errors creating or inserting collection DAO
     * @throws ConfigException if error collection DAO
     */
    private Integer createCollection () throws  DBException, ConfigException {
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
        
	    return collectionKey;
    }
}
