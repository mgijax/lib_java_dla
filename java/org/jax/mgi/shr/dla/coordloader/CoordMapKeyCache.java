//  $Header
//  $Name

package org.jax.mgi.shr.dla.coordloader;

import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.SchemaConstants;

/**
 * @is a CachedLookup for storing and retrieving MAP_CoordinateKey objects
 * @has a cache, a full initialization query,
 * a "add" query for adding from the database to the cache and a
 * RowDataInterpreter
 * @does can be constructed as either a full or lazy cache and performs
 * cache initialization and looks up values within the cache. Additionally
 * there is a method to add new MAP_CoordinateKey objects not in the database to
 * the cache
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordMapKeyCache extends FullCachedLookup {

    // the collection to which the map belongs
    private Integer collectionKey;

    // the version of the map
    private String version;

    // the MGI type key of the map
    private Integer MGITypeKey;


    /**
     * constructor
     * @param cacheType the type of cache to use from
     * org.jax.mgi.shr.cache.CacheConstants
     * @throws ConfigException thrown if there is an error with configuration
     * @throws DBException thrown if there is an error accessing the database
     * @throws CacheException thrown if there is an error with the cache
     */
    public CoordMapKeyCache(Integer collKey, String vers, Integer mgiTypeKey)
            throws ConfigException, DBException, CacheException {
            super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
            collectionKey = collKey;
            version = vers;
            MGITypeKey = mgiTypeKey;
    }

    /**
     * take the given object key if it is in the caceh
     * @param objectKey
     * @return the map key
     * @throws DBException thrown if there is an error with the database
     * @throws CacheException thrown if there is an error accessing the cache
     */
    public Integer lookup(Integer objectKey)
    throws DBException, CacheException {
        Integer key = (Integer)super.lookupNullsOk(objectKey);
        return key;
    }

    /**
     * get the sql string for fully initializing the cache. This method is
     * required when extending the FullCacheLookup class.
     * @assumes nothing
     * @effects nothing
     * @return the sql string for fully initializing the cache
     */
    public String getFullInitQuery()
    {
        return new String(
             "SELECT " + MGD.map_coordinate._object_key + ", " +
                 MGD.map_coordinate._map_key +
                 " FROM " + MGD.map_coordinate._name +
                 " WHERE " + MGD.map_coordinate._collection_key + " = " +
                 collectionKey +
                 " AND " + MGD.map_coordinate.version + " = '" + version +
                 "' AND " + MGD.map_coordinate._mgitype_key + " = " + MGITypeKey);
    }

    /**
     * add a new map to the cache
     * @assumes nothing
     * @effects a new map will be added to the cache if it is not already there
     * @param objectKey - key to the database object this map represents
     * @param mapKey - key to the map represented by 'objectKey'
     * @throws DBException thrown if there is an error with the database
     * @throws CacheException thrown if there is an error with the cache
     */
    protected void addToCache(Integer objectKey, Integer mapKey)
    throws DBException, CacheException {
        if (super.lookupNullsOk(objectKey) == null) {
                super.cache.put(objectKey, mapKey);
        }
    }

    /**
     * Get a RowDataInterpreter for creating a KeyValue object from a database
     * used for creating a new cache entry.
     * @assumes nothing
     * @effects nothing
     * @return The RowDataInterpreter object.
     */
    public RowDataInterpreter getRowDataInterpreter() {
        class Interpreter implements RowDataInterpreter {
            public Object interpret (RowReference row)
                throws DBException {
                return new KeyValue(row.getInt(1), row.getInt(2));
            }
        }
        return new Interpreter();
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
