// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.BindableStatement;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.SchemaConstants;

/**
 * @is an object for looking up data in the mgi_attributeHistory table for
 * @has a MGI type, a connection to the MGD database, and a query
 * @does looks up data to see whether or not attributes from the prb_source
 * table have been edited by a curator
 * @company The Jackson Lab
 * @author M Walker
 * @version 1.0
 */

public class AttrHistory
{
    /**
     * object for getting access to the database
     */
    protected SQLDataManager sqlMgr = null;

    /**
     * MGI type of the MGI_attributeHistory._Object_key
     */
    String mgiType;

    /**
     * the query statement which allows binding of a source key and column
     * name to a query to the mgi_attributeHistory table at runtime
     */
    protected BindableStatement query = null;

    /**
     * the query string for the mgi_attributeHistory table which returns a
     * history record if it was modified by any user other than a user
     * of type 'Data Loads'
     */
    protected String sql =
        "SELECT 1 " +
        "FROM " +
            MGD.mgi_attributehistory._name + " his, " +
            MGD.mgi_user._name + " usr, " +
            MGD.voc_term._name + " trm " +
        "WHERE his." + MGD.mgi_attributehistory._mgitype_key + " = " +
            mgiType + " " +
        "AND his." + MGD.mgi_attributehistory.columnname + " = ? " +
        "AND his." + MGD.mgi_attributehistory._object_key + " = ? " +
        "AND his." + MGD.mgi_attributehistory._modifiedby_key + " = " +
            "usr." + MGD.mgi_user._user_key + " " +
        "AND usr." + MGD.mgi_user._usertype_key + " = " +
            "trm." + MGD.voc_term._term_key + " " +
        "AND trm." + MGD.voc_term.term + " != '" + DATALOAD_USER + "'";

    /**
     * constant definition for the voc_term value which
     * is used to join voc_term to mgi_user to identify a dataloads user
     * Assumption is that curator edited = ! DATALOAD_USER
     */
    protected static final String DATALOAD_USER = "Data Load";

    /**
     * constructor
     * @param mgiTypeKey mgiType representing the table whose attribute we
     * are testing
     * @throws ConfigException thrown if there is an error with the database
     * @throws DBException thrown if there is an error with configuration
     */
    public AttrHistory(int mgiTypeKey)
    throws ConfigException, DBException
    {
        sqlMgr = SQLDataManagerFactory.getShared(SchemaConstants.MGD);
        query = sqlMgr.getBindableStatement(sql);
    }


    /**
     * performs the database query for a given column name and source key
     * to see if a column has been curator edited
     * @param columnName the name of the column from the table indicated by
     * MGI type
     * @param objectKey the object key for the object for which attribute we are
     * determining curation
     * @return true if the attribute has been curator edited
     * @throws DBException thrown if there is an error with the database
     */
    protected boolean isCurated(String columnName, Integer objectKey)
    throws DBException
    {
        /**
         * bind parameters to the query
         */
        query.setString(1, columnName);
        query.setInt(2, objectKey.intValue());

        /**
         * execute the query
         */
        ResultsNavigator nav = query.executeQuery();
        if (nav.next())  // indicates records were found in history
            return true;
        else
            return false;
    }
}
// $ Log