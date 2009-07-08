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
 * @is a FullCachedLookup for caching MutantCellLine objects
 * by their cell line IDs
 * @has a RowDataCacheStrategy of type FULL_CACHE used for creating the
 * cache and performing the cache lookup
 * @does provides a lookup method to return a MutantCellLine objec given a mutant 
 * cell line ID
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MutantCellLineLookupByCellLineID extends FullCachedLookup {
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
  public MutantCellLineLookupByCellLineID()
          throws CacheException, DBException, ConfigException {
      super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
      // since cache is static make sure you do not reinit
      if (!hasBeenInitialized) {
          initCache(cache);
      }
      hasBeenInitialized = true;
  }

  /**
   * lookup the MutantCellLine given a mutant cell line ID and creator
   * @param idAndCreator mclID|creator
   * @return MutantCellLine object for idAndCreator
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
  public MutantCellLine lookup(String idAndCreator) throws CacheException,
      DBException, ConfigException, KeyNotFoundException {
      return (MutantCellLine)super.lookup(idAndCreator);
  }

  /**
   * get the full initialization query which is called by the CacheStrategy
   * class when performing cache initialization
   * @assumes nothing
   * @effects nothing
   * @return the full initialization query
   */
  public String getFullInitQuery() {
    return new String("SELECT a.accID, a._logicalDB_key, ldb.name as ldbName, " +
	"c._CellLine_key, c.cellLine, c._CellLine_Type_key, " +
	"v.term as cellLineType, c._Strain_key, s.strain, " +
	"c._Derivation_key, c.isMutant, c.creation_date, c.modification_date, " +
	"c._CreatedBy_key, c._ModifiedBy_key " +
	"FROM ACC_Accession a, ALL_CellLine_View c, VOC_Term v, PRB_Strain s, " +
	"ACC_LogicalDB ldb " +
	"WHERE a._MGIType_key =  " + MGITypeConstants.CELLLINE +
	" and a._LogicalDB_key = ldb._LogicalDB_key " +
	"and a._Object_key = c._CellLine_key " +
	"and c.isMutant = 1 " +
	"and c._CellLine_Type_key = v._Term_key " +
	"and c._Strain_key = s._Strain_key " +
	" and c._Derivation_key != null");
  }
  
  /**
   * get the RowDataInterpreter which is required by the CacheStrategy to
   * read the results of a database query.
   * @assumes nothing
   * @effects nothing
   * @return the partial initialization query
   */
  public RowDataInterpreter getRowDataInterpreter() {
      class Interpreter implements RowDataInterpreter {
	  public Object interpret(RowReference row) 
	    throws DBException, InterpretException {
	      
	      String accID = row.getString(1);
          Integer ldbKey = row.getInt(2);
          StringBuffer key = new StringBuffer();
          key.append(accID);
          key.append("|");
          key.append(ldbKey);
		  //System.out.println("lookupKey: " + key.toString());
		 
	      MutantCellLine mcl = null;
		try {
		    mcl = new MutantCellLine();
		} catch (DLALoggingException e) {
		    throw new InterpretException (
			"MutantCellLineLookupByCellLineID " + e.getMessage());
		}
	      mcl.setAccID(accID);
	      mcl.setLdbKey(ldbKey);
	      mcl.setLdbName(row.getString(3));
	      mcl.setMCLKey(row.getInt(4));
	      mcl.setCellLine(row.getString(5));
	      mcl.setCellLineTypeKey(row.getInt(6));
	      mcl.setCellLineType(row.getString(7));
	      mcl.setStrainKey(row.getInt(8));
	      mcl.setStrain(row.getString(9));
	      mcl.setDerivationKey(row.getInt(10));
	      mcl.setIsMutant(row.getBoolean(11));
	      mcl.setCreationDate(row.getTimestamp(12));
	      mcl.setModificationDate(row.getTimestamp(13));
	      mcl.setCreatedByKey(row.getInt(14));
	      mcl.setModifiedByKey(row.getInt(15));
              //System.out.println("KEY:" + key.toString());
              return new KeyValue(key.toString(), mcl);
	  }
      }
    return new Interpreter();
  } 
}
