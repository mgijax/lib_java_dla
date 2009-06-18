package org.jax.mgi.dbs.mgd.lookup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.jax.mgi.dbs.mgd.loads.Alo.Allele;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.KeyNotFoundException;
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
 * @is a FullCachedLookup for caching Allele (see Allele.java) objects by the
 *     sequence key to which the allele is associated
 * @has RowDataCacheStrategy of type FULL_CACHE used for creating the
 *      cache and performing the cache lookup
 * @does provides a lookup method for getting Allele objects by their associated
 *       sequence key
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class AlleleLookupBySeqKey extends FullCachedLookup {
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

    public AlleleLookupBySeqKey()
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
     * lookup alleles given a sequence key
     * @assumes nothing
     * @effects if the cache has not been initialized then the query will be
     * executed and the cache will be loaded. Queries a database.
     * @param seqKey SEQ_Sequence._Sequence_key
     * @return HashSet of Allele Objects associated with 'seqeKey'
     * @throws CacheException thrown if there is an error accessing the
     * caches
     * @throws DBException thrown if there is an error accessing the database
     */
    public HashSet lookup(Integer seqKey)
    throws CacheException, DBException, KeyNotFoundException {
	HashSet alleles = (HashSet)super.lookupNullsOk(seqKey);
	if (alleles == null){
	    alleles = new HashSet();
	}
        return alleles;
    }

    /**
     * Get the query to fully initialize the cache.
     * @return The query to fully initialize the cache.
     */
    public String getFullInitQuery() {
	// select alleles associated with sequences
        String sql =
            "SELECT distinct " + 
	    "s._Sequence_key, " +
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
	    "FROM All_Allele_View a, SEQ_Allele_Assoc s, VOC_term v1, " + 
	    "VOC_Term v2, VOC_Term v3, VOC_Term v4 " +
	    "WHERE a._Allele_key = s._Allele_key " +
	    "and a._Mode_key = v1._Term_key " +
	    "and a._Allele_Type_key = v2._Term_key " +
	    "and a._Allele_Status_key = v3._Term_key " +
	    "and a._Transmission_key = v4._Term_key " +
	    "order by s._Sequence_key";

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
		    // the sequence Key
		    Integer seqKey = ((RowData)v.get(0)).seqKey;
		    Allele currentAllele = null;
		    HashSet alleleSet = new HashSet();
		   
		    // get an iterator over the vector representing the alleles
		    // may be multiple for this sequence 
		    for (Iterator i = v.iterator();i.hasNext(); ) {
			
			RowData row = (RowData)i.next();
			// create a new Allele
			currentAllele = createAlleleObject(row);
			alleleSet.add(currentAllele);
		    }
		   return new KeyValue(seqKey, alleleSet);
		}
		public Allele createAlleleObject(RowData row)
		    throws InterpretException {
		  
		    Allele allele = null;
		    try {
			allele = new Allele();
		    }  catch (DLALoggingException e) {
			throw new InterpretException (
			"AlleleLookupBySeqKey " + e.getMessage());
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
		    protected Integer seqKey;
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
			seqKey = row.getInt(1);
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
