package org.jax.mgi.dbs.mgd.lookup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.jax.mgi.dbs.mgd.loads.Alo.MutantCellLine;
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
   * look up the allele key to get a set of Mutant Cell Lines
   * @param alleleKey the allele key to look up
   * @return HashSet of MutantCellLine objects
   * @throws CacheException thrown if there is an error accessing the cache
   * @throws CacheException thrown if there is an error accessing the cache
   * @throws DBException thrown if there is an error accessing the
   * database
   * @throws ConfigException thrown if there is an error accessing the
   * configuration file
   */
  public HashSet lookup(Integer alleleKey) throws CacheException,
      DBException, ConfigException {
      
      return (HashSet)super.lookupNullsOk(alleleKey);
  }

  /**
   * get the full initialization query which is called by the CacheStrategy
   * class when performing cache initialization
   * @assumes nothing
   * @effects nothing
   * @return the full initialization query
   */
  public String getFullInitQuery() {
    return new String("SELECT  aac._Allele_key, a.accID, a._logicalDB_key, " +
	"ldb.name as ldbName, c._CellLine_key, c.cellLine, " +
	"c._CellLine_Type_key, v.term as cellLineType, c._Strain_key, " +
	"s.strain, c._Derivation_key, c.isMutant " +
	"FROM ACC_Accession a, ALL_CellLine c, ALL_Allele_CellLine aac, VOC_Term v, " +
	"PRB_Strain s, ACC_LogicalDB ldb " +
	"WHERE a._MGIType_key =  " + MGITypeConstants.CELLLINE + " " +
	"and a._LogicalDB_key = ldb._LogicalDB_key " +
	"and a._Object_key = c._CellLine_key " +
	"and c._CellLine_key = aac._MutantCellLine_Key " +
	"and c.isMutant = 1 " +
	"and c._CellLine_Type_key = v._Term_key " +
	"and c._Strain_key = s._Strain_key " +
	"ORDER BY aac._Allele_key");
  }
  
  /**
   * get the RowDataInterpreter which is required by the CacheStrategy to
   * read the results of a database query.
   * @assumes nothing
   * @effects nothing
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
	    RowData rd = (RowData)v.get(0);
	    Integer alleleKey = rd.alleleKey;
	    HashSet MCLSet = new HashSet();
	    for (Iterator i = v.iterator(); i.hasNext();) {
		rd = (RowData)i.next();
		 MutantCellLine mcl = null;
		try {
		    mcl = new MutantCellLine();
		} catch (DLALoggingException e) {
		    throw new InterpretException (
			"MutantCellLineLookupByCellLineID " + e.getMessage());
		}
		mcl.setAccID(rd.cellLineID);
		mcl.setLdbKey(rd.ldbKey);
		mcl.setLdbName(rd.ldbName);
		mcl.setMCLKey(rd.mclKey);
		mcl.setCellLine(rd.cellLine);
		mcl.setCellLineTypeKey(rd.cellLineTypeKey);
		mcl.setCellLineType(rd.cellLineType);
		mcl.setStrainKey(rd.strainKey);
		mcl.setStrain(rd.strain);
		mcl.setDerivationKey(rd.derivationKey);
		mcl.setIsMutant(rd.isMutant);		
	    }
	    return new KeyValue(alleleKey, MCLSet);
	}
      }
    return new Interpreter();
  }
  /**
     * Simple data object representing a row of data from the query
     */
    class RowData {
	protected Integer alleleKey = null;
	protected String cellLineID = null;
	protected Integer ldbKey = null;
	protected String ldbName = null;
	protected Integer mclKey = null;
	protected String cellLine = null;
	protected Integer cellLineTypeKey = null;
	protected String cellLineType = null;
	protected Integer strainKey = null;
	protected String strain = null;
	protected Integer derivationKey = null;
	protected Boolean isMutant = null;
	
        public RowData (RowReference row) throws DBException {
	    alleleKey = row.getInt(1);
            cellLineID = row.getString(2);
            ldbKey = row.getInt(3);
	    ldbName = row.getString(4);
	    mclKey = row.getInt(5);
	    cellLine = row.getString(6);
	    cellLineTypeKey = row.getInt(7);
	    cellLineType = row.getString(8);
	    strainKey =row.getInt(9);
	    strain = row.getString(10);
	    derivationKey = row.getInt(11);
	    isMutant = row.getBoolean(12);
        }
    }	
}

