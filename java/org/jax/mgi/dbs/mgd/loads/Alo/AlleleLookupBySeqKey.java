package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

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
            throws DBException, ConfigException, CacheException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
        // since cache is static make sure you do not reinit
        if (!hasBeenInitialized) {
          initCache(cache);
        }
        hasBeenInitialized = true;
	}

    /**
     * lookup alleles given a sequence key
     * @param seqKey SEQ_Sequence._Sequence_key
     * @return HashSet of Allele Objects associated with 'seqeKey'
     * @throws CacheException thrown if there is an error accessing the
     * caches
     * @throws DBException thrown if there is an error accessing the database
     */
    public HashMap lookup(Integer seqKey)
    throws CacheException, DBException, KeyNotFoundException {
        HashMap alleles = (HashMap)super.lookupNullsOk(seqKey);
        if (alleles == null){
            alleles = new HashMap();
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
	    "a.symbol as alleleSymbol " +
	    "FROM All_Allele a, SEQ_Allele_Assoc s " + 
	    "WHERE a._Allele_key = s._Allele_key " +
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

            public Object interpretKey(RowReference row) throws DBException {
                return row.getInt(1);
            }

            public Object interpretRows(Vector v) throws InterpretException {
                // the sequence Key
                Integer seqKey = ((RowData)v.get(0)).seqKey;
		HashMap alleles = new HashMap();
                // get an iterator over the vector representing the alleles
                // may be multiple for this sequence
                for (Iterator i = v.iterator();i.hasNext(); ) {
                    RowData row = (RowData)i.next();
		    alleles.put(row.alleleKey, row.alleleSymbol);
                }
               return new KeyValue(seqKey, alleles);
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
                protected String alleleSymbol;

                /**
                 * Constructs a RowData object from a RowReference
                 * @param row a RowReference
                 * @throws DBException if error accessing RowReference
                 *         methods
                 */

                public RowData(RowReference row) throws DBException {
                    seqKey = row.getInt(1);
                    alleleKey = row.getInt(2);
                    alleleSymbol = row.getString(3);
                }
            }
        }
            
        return new Interpreter();
    } 
}
