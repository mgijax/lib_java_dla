package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.CoordMapFeatureKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.FeatureKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.MGITypeLookup;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureState;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_CollectionState;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_CollectionDAO;
import org.jax.mgi.dbs.mgd.loads.Coord.CoordMapProcessor;
import org.jax.mgi.dbs.mgd.loads.Coord.CoordMapFeatureResolver;
import org.jax.mgi.shr.dla.loader.coord.CoordloaderExceptionFactory;
import org.jax.mgi.shr.dla.input.CoordinateInput;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.dla.loader.coord.CoordloaderException;
import org.jax.mgi.shr.dla.loader.coord.CoordloaderConstants;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.dla.loader.coord.CoordInDatabaseException;

/**
 * An object that resolves raw date and creates map collection, a coordinate map
 *  and a coord map feature objects in a database.
 * @has
 *   <UL>
 *   <LI>a logger
 *   <LI>a configurator
 *   <LI>a CoordMapCollectionKeyLookup to lookup the collections key 
 *	 for the coordinate
 *   <LI>CoordMapProcessor to determine/create the coordinate map for the 
 * 	 coordinate
 *   <LI>CoordMapFeatureResolver to resolve map features
 *   </UL>
 * @does
 *   <UL>
 *   <LI>deletes existing collection, map, and features for a collection
 *   <LI>gets or creates the collection and the map for a coordinate
 *   <LI>creates a coordinate
 *   <LI>
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordinateInputProcessor {
    // gets configuration values for coordinate loads
    private CoordLoadCfg coordCfg;

    // name of the collection
    private String collectionName;

    // abbreviation for the collection
    private String collectionAbbrev;

    // the collection key for this load
    private Integer collectionKey;

    // gets existing or creates the coordinate map for the coordinate
    private CoordMapProcessor mapProcessor;

    // resolves CoordMapFeatureRawAttributes to a MAP_Coord_FeatureState
    private CoordMapFeatureResolver featureResolver;

    // a stream for handling MGD DAO objects
    private SQLStream mgdStream;

    // for doing deletes
    private SQLDataManager sqlMgr;

    // a coordinate Exception Factory
    private CoordloaderExceptionFactory eFactory;
    
    // load mode - e.g. delete_reload, delete reload by object or add
    private String loadMode;
    
    // lookup a objectId of a given MGI type for given collection
    private CoordMapFeatureKeyLookup cmFeatureLookup;

    // lookup a object ID of a given MGI type regardless of collection
    private FeatureKeyLookup featureLookup;

    DLALogger logger;
    /**
     * Constructs a CoordinateInputProcessor
     * @param stream stream for adding coordinates to an  MGD databaseth
     * @throws CacheException if error creating CoordMapFeatureResolver
     * @throws DBException if error creating CoordMapFeatureResolver
     * @throws ConfigException if error creating configurator or collection name
     *          not found. Note  that coordCfg.getMapCollectionAbbrev supplies
     *          a default value if not configured.
     * @throws KeyNotFoundException if error creating CoordMapFeatureResolver
     */

    public CoordinateInputProcessor(SQLStream stream) throws DBException, 
	    CacheException, ConfigException, KeyNotFoundException, 
	    DLALoggingException {

        mgdStream = stream;
        eFactory = new CoordloaderExceptionFactory();
        coordCfg = new CoordLoadCfg();
        collectionName = coordCfg.getMapCollectionName();
        collectionAbbrev = coordCfg.getMapCollectionAbbrev();
	loadMode = coordCfg.getLoadMode();
	//System.out.println("load mode: " + loadMode);
	sqlMgr = SQLDataManagerFactory.getShared(SchemaConstants.MGD);
        // set collection abbreviation to the name value if not configured
        if(collectionAbbrev == null || collectionAbbrev.equals("")) {
	    collectionAbbrev = collectionName;
        }
	
        // create an instance of a CoordMapProcessor from configuration
        mapProcessor = (CoordMapProcessor)coordCfg.getMapProcessorClass();
	Integer mgiTypeKey = new MGITypeLookup().lookup(
            coordCfg.getFeatureMGIType());
        featureResolver = new CoordMapFeatureResolver();
	if (loadMode.equals(CoordloaderConstants.ADD_LOAD_MODE) ) {
	    //System.out.println("ADD Load Mode Lookup collection: " + collectionName);
	    //System.out.println("ADD Load Mode Lookup mgiTypeKey: " + mgiTypeKey.toString());
	    
	    cmFeatureLookup = new CoordMapFeatureKeyLookup(
		collectionName, mgiTypeKey);
	}
	else if (loadMode.equals(CoordloaderConstants.DR_BY_OBJECT_MODE )) {
	    featureLookup = new FeatureKeyLookup(mgiTypeKey);
            //System.out.println("DR_BY_OBJECT Load Mode Lookup mgiTypeKey: " + mgiTypeKey.toString());
	}
	logger = DLALogger.getInstance();
    }

     /**
     * Adds a Coordinate Collection, Coordinate Maps for the Collections and
     * Coordinate Features to the database
     * @assumes Nothing
     * @effects queries and inserts into a database
     * @param input CoordinateInput object - a set of raw attributes to resolve
     *        and add to the database
     * @throws KeyNotFoundException if error processing map or resolving feature
     * @throws DBException if erros creating  map object, resolving feature,
     *      or executing the stream
     * @throws CacheException if errors creating map object or resolving feature
     * @throws TranslationException if errors creating map object
     * @throws ConfigException if there is an error accessing the configuration
     */

    public void processInput(CoordinateInput input) throws ConfigException,
            KeyNotFoundException, DBException, CacheException, 
	    TranslationException, CoordInDatabaseException {

	// get Feature Raw attributes
        CoordMapFeatureRawAttributes featureRaw =
                input.getCoordMapFeatureRawAttributes();

        String objectID = featureRaw.getObjectId();

       /**
         * delete coordinates if feature is in the database 
         * (DR_BY_OBJECT_MODE only)
         */
	if (loadMode.equals(CoordloaderConstants.DR_BY_OBJECT_MODE)) {
	    /* DEBUG start
	    Integer [] featureKey = featureLookup.lookup(objectID);
	    if (featureKey == null) {
		System.out.println("featureKey not in database");
	    }
	    else {
		for (int i=0; i< featureKey.length; i++) {
		    System.out.println("featureKey: " + featureKey[i]);
		}
	    }
	    // DEBUG end
	    */
            if (featureLookup.lookup(objectID) != null) {
		//System.out.println("Deleting " + objectID);
		deleteByObject(objectID);
	    } 

	    // if we are deleting and not reloading, the only raw attribute
	    // is the objectID; if any other attribute empty, just return
	    //System.out.println("startcoord '" + featureRaw.getStartCoord() + "'");
	    if (featureRaw.getStartCoord().equals("")) {
		//System.out.println("in delete by object mode and start coord is empty");
		return; 
	    }
        }


        /**
         * Bail (skip) if feature is in the database for this collection
         * (ADD_LOAD_MODE only)
         */

        if (loadMode.equals(CoordloaderConstants.ADD_LOAD_MODE) &&
            cmFeatureLookup.lookup(objectID) != null) {
                CoordInDatabaseException e =
                    new CoordInDatabaseException();
                e.bindRecordString(objectID);
                throw e;
        }


	/**
         * now resolve the feature
	 */
        // the compound DAO object we are building
        Coordinate coordinate = new Coordinate(mgdStream);

        // Get map object - used by NCBI gene model only (chromosome)
        String mapObject = input.getCoordMapRawAttributes().getCoordMapObject();

        // get a map key
        Integer mapKey = mapProcessor.process(
            input.getCoordMapRawAttributes(), coordinate);

	MAP_Coord_FeatureState state;

	try {
	    state = featureResolver.resolve(
	    featureRaw, mapKey, mapObject);
	}
	catch (KeyNotFoundException e) {
	    logger.logcInfo(e.getMessage(), true);
	    return;
	}
        logger.logdDebug("MAP_Coord_FeatureState: " + state.toString());
        
	// set the feature in the coordMap object
        coordinate.setCoordMapFeatureState(state);

        // send the CoordinateMap object to its stream
        coordinate.sendToStream();
    }

    /**
     * deletes the coordinate collection, all coord maps and features for the
     * collection
     * @assumes Nothing
     * @effects deletes records from a database
     * @throws CoordloaderException if error getting SQLDataManager or executing
     *         a delete.
     */

    public void deleteCoordinates() throws CoordloaderException{

	String sql = "delete from MAP_Coord_Collection where name = '" + collectionName + "'";
	try {
            SQLDataManager sqlMgr = SQLDataManagerFactory.getShared(
		SchemaConstants.MGD);
            sqlMgr.executeVoid(sql);
        }
        catch (MGIException e) {
	    CoordloaderException e1 =
		(CoordloaderException) eFactory.getException(
		    CoordloaderExceptionFactory.ProcessDeletesErr, e);
	    throw e1;
        }
    }

    /**
     * Creates a collection object and sets the coll key in the map processor
     * @param collectionKey collectionKey to set; if null new collection made
     * @throws DBException if errors creating or inserting collection DAO
     * @throws ConfigException if error collection DAO
     */
    public void createCollection (Integer collectionKey) throws DBException,
		   CacheException, ConfigException {
	
        if (collectionKey != null) {
	    this.collectionKey = collectionKey;
	    
	    mapProcessor.initCollection(collectionKey);
		return;
	}
	// otherwise create a new collection
	MAP_Coord_CollectionState collection = 
		new MAP_Coord_CollectionState();

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
	// because it is configured; Configurator does not take parameter
	mapProcessor.initCollection(collectionKey);
    }
    /**
     * deletes the features for the object ID
     * @assumes objectID is not null and featureLookup returns a featureKey
     * @effects deletes all features from the database for objectID
     * @throws  DBException if errors using lookup
     * @throws CacheException if errors using lookup
     */
    private void deleteByObject (String objectID) throws DBException,
        CacheException {
	String del = "delete from MAP_Coord_Feature where _Feature_key = ";
        Integer[] featureKeys = (Integer[])featureLookup.lookup(objectID);
	for (int i = 0; i < featureKeys.length; i++) {
	    Integer key = featureKeys[i];
	    sqlMgr.executeUpdate( del + key.toString());
	}
    }

}
