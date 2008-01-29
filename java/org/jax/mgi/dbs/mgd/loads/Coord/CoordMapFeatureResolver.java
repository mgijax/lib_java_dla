package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureState;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.lookup.MGITypeLookup;
import org.jax.mgi.dbs.mgd.lookup.CoordMapFeatureKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.CoordMapFeatureLookup;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureLookup;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureDAO;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureState;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ConfigException;


/**
 * An object that resolves a CoordMapFeatureRawAttributes to a MAP_Coord_FeatureState
 * @has
 *   <UL>
 *   <LI> CoordMapFeatureKeyLookup - looks up the feature object id to see if the
 *        feature already exists in the database
 *   <LI> AccessionLookup - looks up the feature object id to make sure the
 *        object exists in the database (e.g. seqID or marker MGI id)
 *   <LI> MAP_Coord_FeatureLookup - look up a feature in the database
 *        when it has been determined that there is a feature for the feature
 *        object Id, so we can determine if the feature is the same (i.e. same
 *        start/end coordinates and same strand)
 *   <LI> The MGI Type of the Feature (e.g. Marker or Sequence)
 *
 *   </UL>
 * @does
 *   <UL>
 *   <LI>resolves  a CoordMapFeatureRawAttributes to a MAP_Coord_FeatureState, 
 *       first checking to make sure the feature does not already exist in the
 *       database 
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordMapFeatureResolver {
    // coordinate load configurator
    private CoordLoadCfg coordCfg;
    
    // logger for logging discrepancies
    private DLALogger logger;

    // resolves the object ID of the feature to an object key
    private AccessionLookup accLookup;

    // to see if a feature is already in MGI
    private CoordMapFeatureKeyLookup featureKeyLookup;

    // resolves a feature key to a Feature DAO
    private MAP_Coord_FeatureLookup featureDAOLookup;
    private CoordMapFeatureLookup fLookup;
    
    // MGI Type of the Feature
    private Integer mgiTypeKey;

    // true if this is a feature for a new collection i.e. the collection
    // is not already in the database
    private Boolean isNewCollection;

    /**
     * Constructs a CoordMapFeatureResolver object
     * @param isNewCollection true if the feature is a member of a new collection
     * @throws DBException
     * @throws CacheException
     * @throws KeyNotFoundException
     * @throws ConfigException
     */

    public CoordMapFeatureResolver(Boolean isNewCollection) throws DBException,
            CacheException, KeyNotFoundException, ConfigException, 
		DLALoggingException {
	this.isNewCollection = isNewCollection;
        coordCfg = new CoordLoadCfg();
	logger = DLALogger.getInstance();
        Integer logicalDBKey = new LogicalDBLookup().lookup(
            coordCfg.getLogicalDB());
        mgiTypeKey = new MGITypeLookup().lookup(coordCfg.getFeatureMGIType());
        accLookup = new AccessionLookup(logicalDBKey.intValue(),
            mgiTypeKey.intValue(), AccessionLib.PREFERRED);
	// create these lookups only if the collection exists in MGI
	if (isNewCollection.equals(Boolean.FALSE) ) {
	    String collectionName = coordCfg.getMapCollectionName();
	    featureKeyLookup = new CoordMapFeatureKeyLookup (
		collectionName, mgiTypeKey);
	    featureDAOLookup = new MAP_Coord_FeatureLookup();
	    fLookup = new CoordMapFeatureLookup(collectionName, mgiTypeKey);
	
	}
    }

    /**
     * resolves a CoordMapFeatureRawAttributes to a MAP_Coord_FeatureState given
     * a mapKey
     * @effects queries a database
     * @param rawAttr raw attributes of the feature
     * @param mapKey map key
     * @param isNewCollection if true, we don't have to check if the feature is
     *          already in the database
     * @throws DBException if database error resolving object key
     * @throws CacheException if caching error resolving object key
     * @throws KeyNotFoundException if object key cannot be resolved or
     * feature is already in database
     * @return MAP_Coord_FeatureState
     */

    public MAP_Coord_FeatureState resolve(CoordMapFeatureRawAttributes rawAttr,
                                          Integer mapKey)
            throws DBException, CacheException, KeyNotFoundException, 
	         FeatureInMGIException, FeatureDifferentInMGIException, 
		FeatureObjectNotInMGIException, ConfigException {

        String oId = rawAttr.getObjectId();
	Double start = new Double(rawAttr.getStartCoord());
	Double end = new Double(rawAttr.getEndCoord());
	String strand = rawAttr.getStrand();
	
	// check to see if there are feature(s)s in the database for this objectID
	// - don't bother to if the collection is new
	if (isNewCollection.equals(Boolean.FALSE)) {
	    Integer featureKeys[] = featureKeyLookup.lookup(oId);
            // if we find some feature(s), check to see if they have the same
	    // map, coordinates, and strand and report accordingly
	    if(featureKeys != null) {
		String message = null;
		int numFeatures = featureKeys.length;
		if (numFeatures > 1) {
		    logger.logcInfo(oId + " has " + 
			new Integer(numFeatures).toString() + " coordinates in MGI", false);
		}
		// true if an incoming feature is different than in MGI
	        boolean hasDifferent = false;
	        for (int i = 0; i < numFeatures; i++){
		    MAP_Coord_FeatureDAO dao = fLookup.lookup(featureKeys[i]);
		    MAP_Coord_FeatureState state = dao.getState();
		    Double s = state.getStartCoordinate();
		    Double e = state.getEndCoordinate();
		    String st = state.getStrand();
		    Integer mk = state.getMapKey();
		    // if any single attribute is different report it
		    if (!(mapKey.equals(mk) && start.equals(s) && 
			    end.equals(e) && strand.equals(st))) {
			message = oId + " attribute discrepancy. \nIn MGI:\n" + 
			    mk + ", " + s + ", " + e + ", " + st +
			    "\nIn input:\n "  + mapKey + ", " + start + ", " +
			    end + ", " + strand;
			hasDifferent = true;
		    }	     
		}
	    	if (message != null) {
                    logger.logcInfo(message, false);
                }
		if (hasDifferent == true) {
		    throw new FeatureDifferentInMGIException(oId);
		}
		else {
		    throw new FeatureInMGIException(oId);
		}
	    }
	}
	// Make sure the feature object ID exists as an MGI object
	Integer objectKey = accLookup.lookup(oId);
	if(objectKey == null) {
	    throw new FeatureObjectNotInMGIException(oId);
	}
	
	// now create the state object
	MAP_Coord_FeatureState state = new MAP_Coord_FeatureState();
	state.setMapKey(mapKey);
	state.setMGITypeKey(mgiTypeKey);
	state.setObjectKey(objectKey);
	state.setStartCoordinate(new Double(rawAttr.getStartCoord()));
	state.setEndCoordinate(new Double(rawAttr.getEndCoord()));
	state.setStrand(rawAttr.getStrand());
	return state;
    }
}
    
