package org.jax.mgi.dbs.mgd.lookup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.jax.mgi.dbs.mgd.loads.Alo.Allele;
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
 * @is a FullCachedLookup for caching Allele (see Allele.java) objects by mutant 
 *     cell line key associations
 * @has RowDataCacheStrategy of type FULL_CACHE used for creating the
 *      cache and performing the cache lookup
 * @does provides a lookup method for getting Allele objects by their associated
 *       mutant cell line key
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class AlleleLookupByMutantCellLineKey extends FullCachedLookup {

    // provide a static cache so that all instances share one cache
    private static HashMap cache = new HashMap();

    // indicator of whether or not the cache has been initialized
    private static boolean hasBeenInitialized = false;
    /**
     * Constructor
     * @throws DBException thrown if there is an error accessing the database
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws ConfigException thrown of there is an error accessing the
     * configuration
     */

    public AlleleLookupByMutantCellLineKey()
        throws DBException,
        ConfigException,
        CacheException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
	// since cache is static make sure you do not reinit
	if (!hasBeenInitialized) {
	    initCache(cache);
	}
	hasBeenInitialized = true;
       
    }

    /**
     * lookup alleles with their molecular mutation, notes, and reference ids 
     * given a cell line key
     * @assumes nothing
     * @effects if the cache has not been initialized then the query will be
     * executed and the cache will be loaded. Queries a database.
     * @param cellLineKey ALL_CellLine._CellLine_key
     * @return HashSet of Allele Objects associated with 'cellLineKey'
     * @throws CacheException thrown if there is an error accessing the
     * caches
     * @throws DBException thrown if there is an error accessing the database
     */
    public HashSet lookup(Integer cellLineKey)
    throws CacheException, DBException {
        return (HashSet)super.lookupNullsOk(cellLineKey);
    }

    /**
     * Get the query to fully initialize the cache.
     * @return The query to fully initialize the cache.
     */
    public String getFullInitQuery() {
	// select alleles with mutant cell lines
        String sql =
            "SELECT distinct " + 
	    "c._MutantCellLine_key, " +
	    "a._Allele_key, " +
	    "a._Marker_key, " +
	    "a.markerSymbol, " + 
	    "a._Strain_key, " +
	    "a.strain, " +
	    "inheritModeKey = a._Mode_key, " +
	    "inheritMode = v1.term, " + 
	    "a._Allele_Type_key, " + 
	    "alleleType = v2.term, " +
	    "a._Allele_Status_key, " + 
	    "alleleStatus = v3.term, " +
	    "alleleSymbol = a.symbol, " +
	    "alleleName = a.name, " +
	    "a.isWildType, " +
	    "a.isExtinct, " +
	    "a.isMixed, " +
	    "a._Transmission_key, " +
	    "transmission = v4.term " +
	    "FROM All_Allele_View a, ALL_Allele_CellLine c, VOC_term v1, " + 
	    "VOC_Term v2, VOC_Term v3, VOC_Term v4 " +
	    "WHERE a._Allele_key = c._Allele_key " +
	    "and c._MutantCellLine_key > 0 " +
	    "and a._Mode_key = v1._Term_key " +
	    "and a._Allele_Type_key = v2._Term_key " +
	    "and a._Allele_Status_key = v3._Term_key " +
	    "and a._Transmission_key = v4._Term_key " +
	    "order by c._MutantCellLine_key";
	    // "and c._MutantCellLine_key in (40236, 40237, 40238, 40240) " +
	   
        return sql;
    }

    /**
     * return the RowDataInterpreter for creating KeyValue objects from the 
     * query results
     * @return the RowDataInterpreter for this query
     */
    public RowDataInterpreter getRowDataInterpreter() {
           class Interpreter implements MultiRowInterpreter {
		
                public Object interpret(RowReference row)
                throws DBException {
                    return new RowData(row);
                }

                public Object interpretKey(RowReference row) 
			throws DBException {
                    return row.getInt(1);
                }

                public Object interpretRows(Vector v) throws InterpretException {
		    // the mutant cell line Key
		    Integer mutCLKey = ((RowData)v.get(0)).mutCellLineKey;
		    Allele currentAllele = null;
		    HashSet alleleSet = new HashSet();
		    
		    // get an iterator over the vector representing the alleles
		    // may be multiple) for this mutant cell line. 
		    for (Iterator i = v.iterator();i.hasNext(); ) {
			
			RowData row = (RowData)i.next();
			// create a new Allele
			currentAllele = createAlleleObject(row);
			alleleSet.add(currentAllele);
		    }
		   // System.out.println("AlleleLookupByMutantCellLineKey creating KeyValue: " + 
			//mutCLKey + "/" + alleleSet.toString());
		   return new KeyValue(mutCLKey, alleleSet);
		}
		private Allele createAlleleObject(RowData row) 
		    throws InterpretException {
		  
		    Allele allele = null;
		    try {
			allele = new Allele();
		    }  catch (DLALoggingException e) {
			throw new InterpretException (
			"AlleleLookupByMutantCellLineKey " + e.getMessage());
		    }
		    allele.setAlleleKey(row.alleleKey);
		    allele.setMarkerKey(row.markerKey);
		    allele.setMarkerSymbol(row.markerSymbol);
		    allele.setStrainKey(row.alleleStrainKey);
		    allele.setStrainName(row.alleleStrain);
		    allele.setInheritModeKey(row.inheritModeKey);
		    allele.setInheritMode(row.inheritMode);
		    allele.setAlleleTypeKey(row.alleleTypeKey);
		    allele.setAlleleType(row.alleleType);
		    allele.setAlleleStatusKey(row.alleleStatusKey);
		    allele.setAlleleStatus(row.alleleStatus);
		    allele.setAlleleSymbol(row.alleleSymbol);
		    allele.setAlleleName(row.alleleName);
		    allele.setIsWildType(row.isWildType);
		    allele.setIsExtinct(row.isExtinct);
		    allele.setIsMixed(row.isMixed);
		    allele.setTransmissionKey(row.transmissionKey);
		    allele.setTransmission(row.transmission);
		    return allele;	    
		}
				
		/**
		 * an object that represents a row of data from the query we are
		 * interpreting
		 * @has
		 * <UL>
		 * <LI> attributes representing each column selected in the 
		 *      query
		 * </UL>
		 * @does
		 * <UL>
		 * <LI> assigns its attributes from a RowReference object
		 * </UL>
		 * @company The Jackson Laboratory
		 * @author sc
		 * @version 1.0
		 */
		 class RowData {
		    protected Integer mutCellLineKey;
		    protected Integer alleleKey;
		    protected Integer markerKey;
		    protected String markerSymbol;
		    protected Integer alleleStrainKey;
		    protected String alleleStrain;
		    protected Integer inheritModeKey;
		    protected String inheritMode;
		    protected Integer alleleTypeKey;
		    protected String alleleType;
		    protected Integer alleleStatusKey;
		    protected String alleleStatus;
		    protected String alleleSymbol;
		    protected String alleleName;
		    protected Boolean isWildType;
		    protected Boolean isExtinct;
		    protected Boolean isMixed;
		    protected Integer transmissionKey;
		    protected String transmission;
		    
		    /**
		     * Constructs a RowData object from a RowReference
		     * @assumes Nothing
		     * @effects Nothing
		     * @param row a RowReference
		     * @throws DBException if error accessing RowReference 
		     *         methods
		     */

		    public RowData(RowReference row) throws DBException {
			mutCellLineKey = row.getInt(1);
			alleleKey = row.getInt(2);
			markerKey = row.getInt(3);
			markerSymbol = row.getString(4);
			alleleStrainKey = row.getInt(5);
			alleleStrain = row.getString(6);
			inheritModeKey = row.getInt(7);
			inheritMode = row.getString(8);
			alleleTypeKey = row.getInt(9);
			alleleType = row.getString(10);
			alleleStatusKey = row.getInt(11);
			alleleStatus = row.getString(12);
			alleleSymbol = row.getString(13);
			alleleName = row.getString(14);
			isWildType = row.getBoolean(15);
			isExtinct = row.getBoolean(16);
			isMixed = row.getBoolean(17);
			transmissionKey = row.getInt(18);
			transmission = row.getString(19);
			
		    }
		}
	   }
            
        return new Interpreter();
    }
    
}
