package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

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
 * @is a FullCachedLookup for caching Allele attributes (see AlleleData.java)
 *     
 * @has RowDataCacheStrategy of type FULL_CACHE used for creating the
 *      cache and performing the cache lookup
 * @does provides a lookup method for getting an AlleleData object by its associated
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
               throws DBException, ConfigException, CacheException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
        // since cache is static make sure you do not reinit
        if (!hasBeenInitialized) {
            initCache(cache);
        }
        hasBeenInitialized = true;
    }

    /**
     * lookup allele information
     * @effects if the cache has not been initialized then the query will be
     * executed and the cache will be loaded. Queries a database.
     * @param cellLineKey ALL_CellLine._CellLine_key
     * @return HashMap of Allele Objects associated with 'cellLineKey'
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
	    "a._Strain_key, " +
	    "a.symbol as alleleSymbol " +
	    "FROM All_Allele a, ALL_Allele_CellLine c " +
	    "WHERE a._Allele_key = c._Allele_key " +
	    "and c._MutantCellLine_key > 0 " +
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

            public Object interpret(RowReference row) throws DBException {
                return new RowData(row);
            }

            public Object interpretKey(RowReference row) throws DBException {
                return row.getInt(1);
            }

            public Object interpretRows(Vector v) throws InterpretException {
                // the mutant cell line Key
                Integer mutCLKey = ((RowData)v.get(0)).mutCellLineKey;
		AlleleData currentAllele = null;
                HashSet alleleSet = new HashSet();

                // get an iterator over the vector representing the alleles
                // may be multiple) for this mutant cell line.
                for (Iterator i = v.iterator();i.hasNext(); ) {

                    RowData row = (RowData)i.next();
		    currentAllele = createAlleleData(row);
		    alleleSet.add(currentAllele);
                }

                return new KeyValue(mutCLKey, alleleSet);
            }
	    private AlleleData createAlleleData(RowData row) {
		AlleleData allele = null;

		allele = new AlleleData();
		allele.setMCLKey(row.mutCellLineKey);
		allele.setAlleleKey(row.alleleKey);
		allele.setStrainKey(row.alleleStrainKey);
		allele.setAlleleSymbol(row.alleleSymbol);
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
	    protected Integer alleleStrainKey;
            protected String alleleSymbol;

            /**
             * Constructs a RowData object from a RowReference
             * @param row a RowReference
             * @throws DBException if error accessing RowReference
             *         methods
             */

            public RowData(RowReference row) throws DBException {
		mutCellLineKey = row.getInt(1);
                alleleKey = row.getInt(2);
		alleleStrainKey = row.getInt(3);
                alleleSymbol = row.getString(4);

            }
        }
    }
        return new Interpreter();
  }
    
}
