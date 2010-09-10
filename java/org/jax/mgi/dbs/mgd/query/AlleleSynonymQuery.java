package org.jax.mgi.dbs.mgd.query;

import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.ObjectQuery;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;

/**
 * is an extension of ObjectQuery for specifically getting Gene Trapped Allele 
 * synonym data from the database. 
 * @has SQLDataManager
 * @does runs the query and creates the MGIMarker objects from the query
 * results and sets all the instance attributes and "bucketizable" attributes
 * for these objects
 * @company The Jackson Laboratory
 * @author sc - based on mbw Query objects in egload
 */



public class AlleleSynonymQuery extends ObjectQuery
{

  // the MGD database manager
  //private SQLDataManager sqlMgr = null;

 
    /**
     * Constructor
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws ConfigException thrown of there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the database
     */

    public AlleleSynonymQuery() throws CacheException, ConfigException,
        DBException
    {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
        //sqlMgr = SQLDataManagerFactory.getShared(SchemaConstants.MGD);
      
    }



    /**
     * Get the query string for querying for gene trapped allele synonyms
     * @assumes Nothing
     * @effects Nothing
     * @return The query string
     */

    public String getQuery()
    {
        /**
         * gets gene trapped allele synonyms
         */

        String stmt = "select synonym " +
	    "from MGI_Synonym s, ALL_Allele a " +
	    "where s._MGIType_key =  " + MGITypeConstants.ALLELE +
	    " and s._Object_key = a._Allele_key " +
	    "and a._Allele_Type_key = 847121";

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

