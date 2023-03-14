package org.jax.mgi.dbs.mgd.loads.Seq;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
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
 * @is a FullCachedLookup for caching sequences by
 *     NCBI ID 
 * @has RowDataCacheStrategy of type FULL_CACHE used for creating the
 *      cache and performing the cache lookup
 * @does provides a lookup method for getting Sequences by their associated
 *      NCBI ID
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class NCBISequenceLookup extends FullCachedLookup {
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

    public NCBISequenceLookup()
            throws DBException, ConfigException, CacheException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
        // since cache is static make sure you do not reinit
        if (!hasBeenInitialized) {
          initCache(cache);
        }
        hasBeenInitialized = true;
	}

    /**
     * lookup sequences given a NCBI ID
     * @param NCBI ID
     * @return HashSet of Allele Objects associated with 'seqeKey'
     * @throws CacheException thrown if there is an error accessing the
     * caches
     * @throws DBException thrown if there is an error accessing the database
     */
    public HashMap lookup(String ncbiID)
    throws CacheException, DBException, KeyNotFoundException {
        HashMap sequences = (HashMap)super.lookupNullsOk(ncbiID);
        if (sequences == null){
            sequences = new HashMap();
        }
            return sequences;
    }

    /**
     * Get the query to fully initialize the cache.
     * @return The query to fully initialize the cache.
     */
    public String getFullInitQuery() {
	// select alleles associated with sequences
        String sql =
            "SELECT a.accid, " +
            "s._sequence_key, " +
	    "s.description " +
	    "FROM acc_accession a, seq_sequence s " + 
	    "WHERE a._object_key = s._sequence_key " +
            "AND a._mgitype_key = " + MGITypeConstants.SEQUENCE  +
            "AND a._logicaldb_key = " + LogicalDBConstants.NCBI_GENE +
	    "order by a.accid";

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
                return row.getString(1);
            }

            public Object interpretRows(Vector v) throws InterpretException {
                // the accid
                String accid = ((RowData)v.get(0)).accid;
		HashMap sequences = new HashMap();
                // get an iterator over the vector representing the sequences
                // may be multiple for this accid
                for (Iterator i = v.iterator();i.hasNext(); ) {
                    RowData row = (RowData)i.next();
                    String description = row.description;
                    String chrValue = description.split(":")[0];
                    String chr = chrValue.substring(3);
		    sequences.put(row.sequenceKey, chr);
                }
               return new KeyValue(accid, sequences);
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
                protected String accid;
                protected Integer sequenceKey;
                protected String description;

                /**
                 * Constructs a RowData object from a RowReference
                 * @param row a RowReference
                 * @throws DBException if error accessing RowReference
                 *         methods
                 */

                public RowData(RowReference row) throws DBException {
                    accid = row.getString(1);
                    sequenceKey = row.getInt(2);
                    description = row.getString(3);
                }
            }
        }
            
        return new Interpreter();
    } 
}
