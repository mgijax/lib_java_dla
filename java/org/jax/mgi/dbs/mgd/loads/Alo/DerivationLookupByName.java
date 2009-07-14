package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashMap;

import org.jax.mgi.dbs.mgd.TranslationTypeConstants;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.lookup.Translator;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.cache.CacheConstants;
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
 * names
 * @has 
 *<UL>
 * <LI>a RowDataCacheStrategy of type FULL_CACHE used for creating the
 * cache and performing the cache lookup
 * <LI>Translator for translating a derivation name to a key, if this fails 
 *     (returns null) then the cache is queried which returns a Derivation object
 * <LI>DeriviationLookupByKey if the translator returns a Derivation key
 *</UL>
 * @does provides a lookup method to return a Derivation object
 *       given a derivation name
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class DerivationLookupByName extends FullCachedLookup {
    // provide a static cache so that all instances share one cache
    private static HashMap cache = new HashMap(); 
    
    // the Translator object shared by all instances of this class
    private static Translator translator = null;
     
    // indicator of whether or not the cache has been initialized
    private static boolean hasBeenInitialized = false;
     
    // Lookup to get Derivation object if the translator returns a key
    private DerivationLookupByKey derivationLookupByKey;
     
    /**
     * Constructs a DerivationLookupByName object.
     * @effects queries a database
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the
     * database
     */
    public DerivationLookupByName ()
        throws  CacheException, ConfigException, DBException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
        // since cache is static make sure you do not reinit
        if (!hasBeenInitialized) {
            initCache(cache);
        }
        hasBeenInitialized = true;
        derivationLookupByKey = new DerivationLookupByKey();
    }

 /**
     * Get the query to fully initialize the cache.
     * @return The query to fully initialize the cache.
     */
    public String getFullInitQuery () {

        return new String("SELECT d._Derivation_key, d.name, d.description, " +
            "d._Vector_key, vectorName=v1.term, d._VectorType_key, " +
            "vecType=v2.term, " +
            "d._ParentCellLine_key, parentName=c.cellLine, " +
            "d._DerivationType_key, derivType=v3.term, d._Creator_key, " +
            "creator=v4.term, d._Refs_key, Jnum=a.accID, c._Strain_key, " +
            "s.strain  " +
            "FROM ALL_CellLine_Derivation d, VOC_Term v1, VOC_Term v2, " +
            "VOC_Term v3, VOC_Term v4, ALL_CellLine c, PRB_Strain s, " +
            "ACC_Accession a " +
            "WHERE d.name != null " +
            "AND d._Vector_key = v1._Term_key " +
            "AND d._VectorType_key = v2._Term_key " +
            "AND d._ParentCellLine_key = c._CellLine_key " +
            "AND c._Strain_key = s._Strain_key " +
            "AND d._DerivationType_key = v3._Term_key " +
            "AND d._Creator_key *= v4._Term_key " +
            "AND d._Refs_key *= a._Object_key " +
            "AND a._MGIType_key = 1 " +
            "AND a._LogicalDB_key = 1 " +
            "AND a.prefixPart = 'J:'");
    }
    /**
    * Look up a Derivation name to get a Derivation object
    * @param name derivation name to lookup
    * @return A Derivation object, or null if not in database
    * @throws CacheException thrown if there is an error accessing the cache
    * @throws DBException thrown if there is an error accessing the
    * database
    */
    public Derivation lookup (String name) 
        throws DBException, CacheException, ConfigException,
            TranslationException {
        // since a Translator is not obtained until this method is called,
        // we need to check for null
        if (translator == null){
          translator = new Translator(TranslationTypeConstants.DERIVATION,
            CacheConstants.FULL_CACHE);
        }
        // do a translation of the name and expect null if term is not found.
        // if the name translates to a key then we use DerivationLookupByKey
            // to get the Derivation, if it doesn't translate, we use the cache
        Integer key = translator.translate(name);
        if (key != null){
            return derivationLookupByKey.lookup(key);
        }
        else { // no translation, so look in the cache
           return (Derivation)super.lookupNullsOk(name);
        }
    }

    /**
     * Get a RowDataInterpreter for creating a KeyValue object from a database
     * used for creating a new cache entry.
     * @effects queries a database
     * @return The RowDataInterpreter object
     */
    public RowDataInterpreter getRowDataInterpreter()  {
        class Interpreter implements RowDataInterpreter {
            public Object interpret (RowReference row)
                throws DBException, InterpretException {
				String name = row.getString(2);
				Derivation derivation = null;
				try {
					derivation = new Derivation();
				} catch (DLALoggingException e) {
					throw new InterpretException ("DerivationLookupByName " +
                            e.getMessage());
				}

				derivation.setDerivationKey(row.getInt(1));
				derivation.setName(name);
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

                return new KeyValue(name, derivation);
            }
        }
        return new Interpreter();
    }
}

