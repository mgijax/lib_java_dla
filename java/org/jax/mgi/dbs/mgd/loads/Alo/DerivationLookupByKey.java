package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashMap;

import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.InterpretException;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * @is a FullCachedLookup for caching Derivation objects by their 
 * database keys
 * @has 
 *<UL>
 * <LI>a RowDataCacheStrategy of type FULL_CACHE used for creating the
 * cache and performing the cache lookup
 *</UL>
 * @does provides a lookup method to return a Derivation object
 *       given a derivation key
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class DerivationLookupByKey extends FullCachedLookup {
    // provide a static cache so that all instances share one cache
    private static HashMap cache = new HashMap(); 
    
    // indicator of whether or not the cache has been initialized
    private static boolean hasBeenInitialized = false;
     
    /**
     * Constructs a DerivationLookupByKey object.
     * @effects queries a database
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the
     * database
     */
    public DerivationLookupByKey ()
        throws  CacheException, ConfigException, DBException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
	// since cache is static make sure you do not reinit
	if (!hasBeenInitialized) {
	    initCache(cache);
	}
	hasBeenInitialized = true;
	//derivationLookupByKey = new DerivationLookupByKey();
    }

    /**
     * Get the query to fully initialize the cache.
     * @return The query to fully initialize the cache.
     */
    public String getFullInitQuery () {

	return new String("SELECT d._Derivation_key, d.name, d.description, " +
		"d._Vector_key, v1.term as vectorName, d._VectorType_key, " +
		"v2.term as vecType, " +
		"d._ParentCellLine_key, c.cellLine as parentName, " +
		"d._DerivationType_key, v3.term as derivType, d._Creator_key, " +
		"v4.term as creator, d._Refs_key, a.accID as Jnum, c._Strain_key, " +
		"s.strain  " +
	    "FROM PRB_Strain s, VOC_Term v1, VOC_Term v2, " +
		"VOC_Term v3, ALL_CellLine c, ALL_CellLine_Derivation d " +
		"left outer join " +
		"ACC_Accession a on " +
			"a._object_key = d._refs_key " +
		"left outer join " +
		"VOC_Term v4 on " +
			"v4._term_key = d._creator_key " +
	    "WHERE d.name is not null " +
	    "AND d._Vector_key = v1._Term_key " +
	    "AND d._VectorType_key = v2._Term_key " +
	    "AND d._ParentCellLine_key = c._CellLine_key " +
	    "AND c._Strain_key = s._Strain_key " +
	    "AND d._DerivationType_key = v3._Term_key " +
	    "AND a._MGIType_key = 1 " +
	    "AND a._LogicalDB_key = 1 " +
	    "AND a.prefixPart = 'J:'");
    }
    /**
    * Look up a Derivation key to get a Derivation object
    * @param key derivation key to lookup
    * @return A Derivation object, or null if not in the database
    * @throws CacheException thrown if there is an error accessing the cache
    * @throws DBException thrown if there is an error accessing the
    * database
    */
    public Derivation lookup (Integer key) 
	throws DBException, CacheException, ConfigException {
	   return (Derivation)super.lookupNullsOk(key);
    }

    /**`
     * Get a RowDataInterpreter for creating a KeyValue object from a database
     * used for creating a new cache entry.
     * @effects queries a database
     * @return The RowDataInterpreter object
     */
    public RowDataInterpreter getRowDataInterpreter()  {
        class Interpreter implements RowDataInterpreter {
            public Object interpret (RowReference row)
                throws DBException, InterpretException {
				Integer key = row.getInt(1);
				Derivation derivation = null;
				try {
					derivation = new Derivation();
				} catch (DLALoggingException e) {
					throw new InterpretException ("DerivationLookupByKey "
                            + e.getMessage());
				}
					derivation.setDerivationKey(key);
					derivation.setName(row.getString(2));
					derivation.setDescription(row.getString(3));
					derivation.setVectorKey(row.getInt(4));
					derivation.setVectorName(row.getString(5));
					derivation.setVectorTypeKey(row.getInt(6));
					derivation.setVectorType(row.getString(7));
					derivation.setParentCellLineKey(row.getInt(8));
					derivation.setParentCellLine(row.getString(9));
					derivation.setDerivationTypeKey(row.getInt(10));
					derivation.setDerivationType(row.getString(11));
					derivation.setCreatorKey(row.getInt(12));
					derivation.setCreator(row.getString(13));
					derivation.setRefsKey(row.getInt(14));
					derivation.setJNum(row.getString(15));
					derivation.setParentStrainKey(row.getInt(16));
					derivation.setParentStrain(row.getString(17));

					return new KeyValue(key, derivation);
				}
			}
        return new Interpreter();
    }
}

