package org.jax.mgi.dbs.mgd.loads.SeqSrc;

import java.util.Vector;

import org.jax.mgi.dbs.mgd.dao.PRB_SourceDAO;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceLookup;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceInterpreter;
import org.jax.mgi.dbs.mgd.lookup.NamedSourceLookup;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.CacheException;

/**
 * An object for looking up MolecularSource objects from the database
 * @has static lookup methods
 * @does provides a variety of ways to lookup MolecularSource objects
 * @author MWalker
 * @company The Jackson Laboratory
 */

public class MSLookup {

    /**
     * a lookup for finding prb_sourceDAO objects by name
     */
    private NamedSourceLookup namedSrcLookup = null;

  /**
   * the SQLDataManager to use for performing queries
   */
  private static SQLDataManager sqlMgr = null;

  /*
   * the following constant definitions are exceptions thrown by this class
   */
  private static String TooManyRows = MSExceptionFactory.TooManyRows;


  /**
   * create a MolecularSource object by quering a record in the database by key
   * @assumes nothing
   * @effects a new connection could be opened to the database if one does
   * not already exist
   * @param key the key to use within the query
   * @return the MolecularSource object represented by the database record
   * @throws DBException thrown if there is an error with the database
   * @throws ConfigException if there is an error with configuration
   */
  public static MolecularSource findBySourceKey(Integer key)
  throws DBException, ConfigException
  {
    PRB_SourceLookup lookup = new PRB_SourceLookup();
    PRB_SourceDAO src = null;
    src = lookup.findBySeqKey(key);
    if (src == null)
        return null;
    MolecularSource ms = new MolecularSource(src);
    ms.setInDatabase(true);
    return ms;
  }

  public MolecularSource findByName(String name)
  throws DBException, ConfigException, CacheException
  {
      if (this.namedSrcLookup == null)
          this.namedSrcLookup = new NamedSourceLookup();
      PRB_SourceDAO dao = this.namedSrcLookup.lookup(name);
      if (dao == null)
          return null;
      else
          return new MolecularSource(dao);
  }

  /**
   * find MolecularSource objects from the database associated to the given
   * accid.
   * @assumes nothing
   * @effects a new connection could be opened to the database if one does
   * not already exist
   * @param accid the accid
   * @param limit the maximum number of rows to allow from the query results
   * @return Vector of MolecularSource objects associated to the
   * given accid
   * @throws DBException thrown if there is an error with the database
   * @throws ConfigException if there is an error with configuration
   * @throws MSException if the maximum number of rows is reached
   */
  public static Vector findAssocClonesByAccid(String accid, int limit)
  throws DBException, ConfigException, MSException
  {
    String query =
        "SELECT src.* FROM " +
           MGD.prb_source._name + " src, " +
           MGD.prb_probe._name + " prb, " +
           MGD.acc_accession._name + " acc " +
        "WHERE acc." + MGD.acc_accession.accid + " = '" + accid + "' " +
        "AND acc." + MGD.acc_accession._logicaldb_key + " = " +
           LogicalDBConstants.SEQUENCE + " " +
        "AND acc." + MGD.acc_accession._mgitype_key + " = " +
           MGITypeConstants.CLONE + " " +
        "AND acc." + MGD.acc_accession._object_key + " = " +
           "prb." + MGD.prb_probe._probe_key + " " +
        "AND prb." + MGD.prb_probe._source_key + " = " +
           "src." + MGD.prb_source._source_key;

    class AssociatedCloneInterpreter implements RowDataInterpreter
    {
      private PRB_SourceInterpreter in = null;
      public AssociatedCloneInterpreter()
      {
        in = new PRB_SourceInterpreter();
      }
      public Object interpret(RowReference row) throws DBException
      {
        PRB_SourceDAO dao = (PRB_SourceDAO)in.interpret(row);
        return new MolecularSource(dao);
      }
    }
    AssociatedCloneInterpreter interpreter = new AssociatedCloneInterpreter();
    ResultsNavigator nav = null;
    if (sqlMgr == null)
      sqlMgr = SQLDataManagerFactory.getShared(SchemaConstants.MGD);
    nav = sqlMgr.executeQuery(query);
    nav.setInterpreter(interpreter);
    return resultsAsVector(query, nav, limit);
  }


    /**
     * parse the query results and place MolecularSource objects into a vector
     * @assumes nothing
     * @effects a new Vector will be created storing results from a query
     * and all MolecularSource objects put in the vector will have their
     * inDatabase flag set to true and additionally the ResultsNavigator
     * will be closed
     * @param sql the query string
     * @param nav the ResultsNavigator
     * @param limit the maximum number of rows to allow
     * @return the vector of MolecularSources
     * @throws DBException if there is an error while iterating through the
     * ResultsNavigator
     * @throws MSException thrown if the maximum number of rows is reached
     */
    private static Vector resultsAsVector(String sql,
                                          ResultsNavigator nav, int limit)
    throws DBException, MSException
    {
        Vector v = new Vector();
        int cnt = 0; // count results
        while (nav.next())
        {
            MolecularSource ms = (MolecularSource)nav.getCurrent();
            ms.setInDatabase(true);
            v.add(ms);
            cnt++;
            if (cnt == limit)
            {
                MSExceptionFactory eFactory = new MSExceptionFactory();
                MSException e = (MSException)eFactory.getException(TooManyRows);
                e.bind(limit);
                e.bind(sql);
                throw e;
            }
        }
        nav.close();
        return v;
    }

}