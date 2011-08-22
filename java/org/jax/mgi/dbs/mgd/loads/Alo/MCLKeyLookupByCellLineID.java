package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashMap;

import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.InterpretException;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * @is a FullCachedLookup for caching MutantCellLine keys
 * by a key consisting of  cell line ID and ldbKey
 * @has a RowDataCacheStrategy of type FULL_CACHE used for creating the
 * cache and performing the cache lookup
 * @does provides a lookup method to return a MutantCellLine key given a mutant 
 * cell line ID and logicalDBKey
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MCLKeyLookupByCellLineID extends FullCachedLookup {
    // provide a static cache so that all instances share one cache
    private static HashMap cache = new HashMap();

    // indicator of whether or not the cache has been initialized
    private static boolean hasBeenInitialized = false;
  
  
  /**
   * constructor
   * @throws CacheException thrown if there is an error with the cache
   * @throws DBException thrown if there is an error accessing the db
   * @throws ConfigException thrown if there is an error accessing the
   * configuration file
   */
  public MCLKeyLookupByCellLineID()
          throws CacheException, DBException, ConfigException {
      super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
      // since cache is static make sure you do not reinit
      if (!hasBeenInitialized) {
          initCache(cache);
      }
      hasBeenInitialized = true;
  }

  /**
   * lookup the Mutant Cell Line key given a mutant cell line ID and creator
   * logicalDBKey
   * @param idAndCreator mclID|ldbKey
   * @return MutantCellLine key for idAndCreator
   * @throws CacheException thrown if there is an error accessing the cache
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   * @throws CacheException thrown if there is an error accessing the cache
   * @throws DBException thrown if there is an error accessing the
   * database
   * @throws ConfigException thrown if there is an error accessing the
   * configuration file
   * @throws KeyNotFoundException thrown if the key is not found
   */
  public Integer lookup(String idAndCreator) throws CacheException,
      DBException, ConfigException, KeyNotFoundException {
      return (Integer)super.lookup(idAndCreator);
  }

  /**
   * get the full initialization query which is called by the CacheStrategy
   * class when performing cache initialization
   * @return the full initialization query
   */
  public String getFullInitQuery() {
    return new String("SELECT a.accID, a._logicalDB_key, c._CellLine_key " +
	"FROM ACC_Accession a, ALL_CellLine_View c, ACC_LogicalDB ldb " +
	"WHERE a._MGIType_key =  " + MGITypeConstants.CELLLINE +
	" and a._LogicalDB_key = ldb._LogicalDB_key " +
	"and a._Object_key = c._CellLine_key " +
	"and c.isMutant = 1 " +
	"and c._Derivation_key != null");
  }
  
  /**
   * get the RowDataInterpreter which is required by the CacheStrategy to
   * read the results of a database query.
   * @return the partial initialization query
   */
  public RowDataInterpreter getRowDataInterpreter() {
      class Interpreter implements RowDataInterpreter {
	  public Object interpret(RowReference row) 
	    throws DBException, InterpretException {
	      
	      String accID = row.getString(1);
	      Integer ldbKey = row.getInt(2);
	      Integer mclKey = row.getInt(3);
	      StringBuffer key = new StringBuffer();
	      key.append(accID);
	      key.append("|");
	      key.append(ldbKey);
		     
	      return new KeyValue(key.toString(), mclKey);
	  }
      }
    return new Interpreter();
  } 
}
