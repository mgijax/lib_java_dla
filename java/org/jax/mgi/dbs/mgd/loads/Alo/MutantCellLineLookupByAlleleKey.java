package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.InterpretException;
import org.jax.mgi.shr.dbutils.MultiRowInterpreter;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * @is a FullCachedLookup for caching MutantCellLine objects 
 * by their associated allele keys
 * @has a RowDataCacheStrategy of type FULL_CACHE used for creating the
 * cache and performing the cache lookup
 * @does provides a lookup method to return a MutantCellLine object given an
 * allele key
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MutantCellLineLookupByAlleleKey extends FullCachedLookup {
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
  public MutantCellLineLookupByAlleleKey()
          throws CacheException, DBException, ConfigException {
      super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
      // since cache is static make sure you do not reinit
      if (!hasBeenInitialized) {
          initCache(cache);
      }
      hasBeenInitialized = true;
  }

  /**
   * look up an allele key to get a set of Mutant Cell Lines
   * @param alleleKey the allele key to look up
   * @return HashMap of Mutant Cell Line keys and names
   * @throws CacheException thrown if there is an error accessing the cache
   * @throws CacheException thrown if there is an error accessing the cache
   * @throws DBException thrown if there is an error accessing the
   * database
   * @throws ConfigException thrown if there is an error accessing the
   * configuration file
   */
  public HashMap lookup(Integer alleleKey) throws CacheException,
      DBException, ConfigException {
      
      return (HashMap)super.lookupNullsOk(alleleKey);
  }

    /**
    * get the full initialization query which is called by the CacheStrategy
    * class when performing cache initialization
    * @return the full initialization query
    */
    public String getFullInitQuery() {
        return new String("SELECT  aac._Allele_key, c._CellLine_key, c.cellLine " +
        "FROM ALL_CellLine c, ALL_Allele_CellLine aac " +
        "where c._CellLine_key = aac._MutantCellLine_Key " +
        "and c.isMutant = 1 " +
        "ORDER BY aac._Allele_key");
    }
  
  /**
   * get the RowDataInterpreter which is required by the CacheStrategy to
   * read the results of a database query.
   * @return the partial initialization query
   */
  public RowDataInterpreter getRowDataInterpreter() {
      class Interpreter implements MultiRowInterpreter {
	
	  public Object interpret(RowReference row) 
	    throws DBException, InterpretException {
	      
	      return new RowData(row);
	  }
	  public Object interpretKey(RowReference ref) throws DBException {
	    return ref.getInt(1);
	  }
	  public Object interpretRows(Vector v) throws InterpretException {
	    Integer alleleKey =  ((RowData)v.get(0)).alleleKey;
	    HashMap MCLMap = new HashMap();
	    for (Iterator i = v.iterator(); i.hasNext();) {
		RowData rd = (RowData)i.next();
		MCLMap.put(rd.mclKey, rd.cellLine);
	    }
	    return new KeyValue(alleleKey, MCLMap);
	}
      }
    return new Interpreter();
  }
    /**
     * Simple data object representing a row of data from the query
     */
    class RowData {
	protected Integer alleleKey = null;
	protected Integer mclKey = null;
	protected String cellLine = null;
	
        public RowData (RowReference row) throws DBException {
	    alleleKey = row.getInt(1);
	    mclKey = row.getInt(2);
	    cellLine = row.getString(3);
        }
    }	
}

