package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.shr.cache.CachedLookup;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceDAO;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceInterpreter;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.SchemaConstants;

/**
 * @is a CachedLookup for storing and retrieving MolecularSource objects
 * @has a cache, a full initialization query, a partial initialization query,
 * a "add" query for adding from the database to the cache and a
 * RowDataInterpreter
 * @does can be constructed as either a full or lazy cache and performs
 * cache initialization and looks up values within the cache. Additionally
 * there is a method to add new MolecularSource objects not in the database to
 * the cache
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class MSCollapsedCache
    extends CachedLookup {
    /**
     * the RowDataInterpreter returned from call to getRowDataInterpreter()
     */
    private Interpreter interpreter = null;


    /**
     * constructor
     * @throws ConfigException thrown if there is an error with configuration
     * @throws DBException thrown if there is an error accessing the database
     * @throws CacheException thrown if there is an error with the cache
     */
    public MSCollapsedCache(int cacheType)
    throws ConfigException, DBException, CacheException
{
    super(cacheType,
          SQLDataManagerFactory.getShared(SchemaConstants.MGD));
    interpreter = new Interpreter();
    }

    /**
     * take the given unresolved MolecularSource and see if it has attributes
     * which match a resolved MolecularSource within the cache
     * @param unresolvedMS an unresolved MolecularSource
     * @return a resolved MolecularSource or null if not in cache
     */
    public MolecularSource lookup(MolecularSource unresolvedMS)
    throws DBException, CacheException
    {
        return (MolecularSource)super.lookupNullsOk(unresolvedMS.toString());
    }

    /**
     * get the sql string for fully initializing the cache. This method is
     * required when extending the FullCacheLookup class.
     * @assumes nothing
     * @effects nothing
     * @return the sql string for fully initializing the cache
     */
    public String getFullInitQuery()
    {
        return new String(
            "SELECT " + MGD.prb_source._name + ".* " +
            "FROM " + MGD.prb_source._name + " " +
            "WHERE " + MGD.prb_source.iscuratoredited + " = 0 " +
            "AND " + MGD.prb_source.name + " is null"
            );
    }

    /**
     * implemented abstract method of the CachedLookup for partially
     * initializing the cache if it is constructed as a lazy cache
     * @return the partial initialization query which is null for not doing
     * partial initialization
     */
    public String getPartialInitQuery()
    {
        return null;
    }

    /**
     * get the query used to get new items from the database and add them to
     * the cache
     * @param addObject the target object used on the call to lookup
     * @return the sql string
     */
    public String getAddQuery(Object addObject)
    {
        MolecularSource ms = (MolecularSource)addObject;
        return new String(
            "SELECT " + MGD.prb_source._name + ".* " +
            "FROM " + MGD.prb_source._name + " " +
            "WHERE " + MGD.prb_source.iscuratoredited + " = 0 " +
            "AND " + MGD.prb_source._segmenttype_key + " = " +
                     ms.getSegmentTypeKey() + " " +
            "AND " + MGD.prb_source._vector_key + " = " +
                     ms.getVectorTypeKey() + " " +
            "AND " + MGD.prb_source._organism_key + " = " +
                     ms.getOrganismKey() + " " +
            "AND " + MGD.prb_source._strain_key + " = " +
                     ms.getStrainKey() + " " +
            "AND " + MGD.prb_source._tissue_key + " = " +
                     ms.getTissueKey() + " " +
            "AND " + MGD.prb_source._gender_key + " = " +
                     ms.getGenderKey() + " " +
            "AND " + MGD.prb_source._cellline_key + " = " +
                     ms.getCellLineKey() + " " +
            "AND " + MGD.prb_source.age + " = '" +
                     ms.getAge() + "'");
    }

    /**
     * get the RowDataInterpreter for creating KeyValue objects from a
     * database query. This method is required when extending FullCachedLookup
     * @return the RowDataInterpreter
     */
    public RowDataInterpreter getRowDataInterpreter()
    {
        return interpreter;
    }

    /**
     * add a MolecularSource object to the cache if it does not already exist
     * there
     * @assumes nothing
     * @effects a new MolecularSource object will be added to the cache if
     * it does not already exist there
     * @param ms the MolecularSource to add
     */
    protected void addToCache(MolecularSource ms)
    throws DBException, CacheException
    {
        if (super.lookupNullsOk(ms.toString()) == null)
            super.cache.put(ms.toString(), ms);
    }

    /**
     * @is a RowDataInterpreter
     * @has a PRB_SourceInterpreter
     * @does parses a given RowReference and creates a KeyValue object used
     * for cahing query results
     * @ompany The JacksonLaboratory
     * @author M Walker
     * @version 1.0
     */
    private class Interpreter
        implements RowDataInterpreter
    {
        private PRB_SourceInterpreter srcInterpreter = null;
        public Interpreter()
        {
            srcInterpreter = new PRB_SourceInterpreter();
        }

        public Object interpret(RowReference row) throws DBException
        {
            PRB_SourceDAO dao = (PRB_SourceDAO) srcInterpreter.interpret(row);
            MolecularSource ms = new MolecularSource(dao);
            ms.setInDatabase(true);
            String key = ms.toString();
            return new KeyValue(key, ms);
        }
    }

}