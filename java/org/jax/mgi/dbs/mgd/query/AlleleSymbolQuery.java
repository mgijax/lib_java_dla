package org.jax.mgi.dbs.mgd.query;

import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.ObjectQuery;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dla.loader.alo.ALOLoaderConstants;

/**
 * is an extension of ObjectQuery for specifically getting Allele symbol data
 * from the database. 
 * @has nothing
 * @does runs the query and creates the MGIMarker objects from the query
 * results and sets all the instance attributes and "bucketizable" attributes
 * for these objects
 * @company The Jackson Laboratory
 * @author sc - based on mbw Query objects in egload
 */



public class AlleleSymbolQuery extends ObjectQuery
{
    /**
     * Constructor
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws ConfigException thrown of there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the database
     */

    public AlleleSymbolQuery() throws CacheException, ConfigException,
        DBException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
      
    }



    /**
     * Get the query string for querying preferred MGI markers
     * @assumes Nothing
     * @effects Nothing
     * @return The query string
     */

    public String getQuery()
    {
        /**
         * gets mouse marker information from MGD
         * includes interim and official nomenclature only
         */
        String stmt = "SELECT symbol " +
            "FROM ALL_Allele " +
            "WHERE _Allele_Status_key != " + ALOLoaderConstants.ALLELE_STATUS_DELETED;
        return stmt;
    }

    /**
     * get a RowDataInterpreter for creating MGIMarker objects from the query
     * results
     * @assumes nothing
     * @effects nothing
     * @return The RowDataInterpreter object
     */

    public RowDataInterpreter getRowDataInterpreter() {
      class Interpreter
          implements RowDataInterpreter {

          public Object interpret(RowReference row) throws DBException {
	      return row.getString(1);
        }
      }
      return new Interpreter();
    }
}
