package org.jax.mgi.dbs.mgd.MolecularSource;

import java.util.Vector;

import org.jax.mgi.dbs.mgd.dao.PRB_SourceDAO;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceInterpreter;
import org.jax.mgi.dbs.mgd.dao.SEQ_Source_AssocDAO;
import org.jax.mgi.dbs.mgd.dao.SEQ_Source_AssocInterpreter;
import org.jax.mgi.shr.dbutils.dao.DAO;
import org.jax.mgi.shr.dbutils.dao.RecordStamper;
import org.jax.mgi.shr.dbutils.dao.RecordStampException;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.DBExceptionFactory;
import org.jax.mgi.shr.dbutils.Table;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.types.Converter;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.MGD;

/**
 * @is an SQLTranslatable object which represents data from the
 * seq_source_assoc and prb_source tables
 * @has a MolecularSource object and and the key to its associated sequence
 * and the key to the corresponding seq_source_assoc record
 * @does provides static lookup methods for accessing a MSSeqSource and
 * provides an implementation for the SQLTranslatable interface so that the
 * object can be placed onto SQLStream. Currently only the update aspect of
 * SQLTranslatable is implemented.
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class MSSeqAssoc extends DAO
{

    /**
     * the DAO object for the PRB_Source table
     */
    private PRB_SourceDAO prbSourceDAO = null;
    /**
     * the DAO object for the SEQ_Source_Assoc table
     */
    private SEQ_Source_AssocDAO seqSourceAssocDAO = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String TooManyRows = MSExceptionFactory.TooManyRows;
    private static String ConfigErr = DBExceptionFactory.ConfigErr;

    /**
     * constructor
     * @param prbSourceDAO the DAO object for the PRB_Source table
     * @param seqSourceAssocDAO the DAP object for the SEQ_Source_Assoc table
     * @throws ConfigException thrown if there is an error trying to access
     * the job stream key from the configuration
     */
    private MSSeqAssoc(PRB_SourceDAO prbSourceDAO,
                      SEQ_Source_AssocDAO seqSourceAssocDAO)
    throws ConfigException
    {
        this.prbSourceDAO = prbSourceDAO;
        this.seqSourceAssocDAO = seqSourceAssocDAO;
    }

    /**
     * returns the MolecularSource object for this instance
     * @assumes nothing
     * @effects nothing
     * @return the MolecularSource
     */
    public MolecularSource getMolecularSource()
    {
        return new MolecularSource(this.prbSourceDAO);
    }

    /**
     *changes the association to the MolecularSource object
     * @assumes nothing
     * @effects nothing
     * @param ms the new MolecularSource object to associate with
     */
    public void changeMolecularSource(MolecularSource ms)
    {
        Integer key = ms.getMSKey();
        this.seqSourceAssocDAO.getState().setSourceKey(key);
        this.prbSourceDAO = ms.getSourceDAO();
    }

    /**
     * get the sequence key associated for this instance
     * @assumes nothing
     * @effects nothing
     * @return the sequence key
     */
    public Integer getSeqKey()
    {
        return this.seqSourceAssocDAO.getState().getSequenceKey();
    }

    /**
     * get the key from the SEQ_Source_Assoc record for this instance
     * @assumes nothing
     * @effects nothing
     * @return the SEQ_Source_Assoc key
     */
    public Integer getAssocKey()
    {
        return this.seqSourceAssocDAO.getKey().getKey();
    }

    /**
     * get the bcp tables supported.
     * currently this is not implemented and a runtime exception is thrown when
     * this method is called
     * @assumes nothing
     * @effects a runtime exception is thrown
     * @return the insert sql for this instance once the implementation is
     * completed. currently a runtime exception is called
     */
    public Vector getBCPSupportedTables()
    {
        throw MGIException.getUnsupportedMethodException();
    }

    /**
     * get the bcp vector for a given table supported.
     * currently this is not implemented and a runtime exception is thrown
     * when this method is called
     * @assumes nothing
     * @effects a runtime exception is thrown
     * @param table the Table instance to get bcp vector for
     * @return the insert sql for this instance once the implementation is
     * completed. currently a runtime exception is called
     */
    public Vector getBCPVector(Table table)
    {
        throw MGIException.getUnsupportedMethodException();
    }


    /**
     * get the sql for doing inserts.
     * currently this is not implemented and a runtime exception is thrown
     * when this method called
     * @assumes nothing
     * @effects a runtime exception is thrown
     * @return the insert sql for this instance once the implementation is
     * completed. currently a runtime exception is called
     */
    public String getInsertSQL()
    {
        throw MGIException.getUnsupportedMethodException();
    }

    /**
     * get the sql for updating the PRB_Source and SEQ_Source_Assoc tables
     * with data from this instance.
     * this method is typically called by a
     * SQLStrategy class when performing updates
     * @assumes nothing
     * @effects nothing
     * @return the update SQL
     * @throws DBException thrown if there is an error with the database
     */
    public String getUpdateSQL() throws DBException
    {
        MolecularSource ms = this.getMolecularSource();
        try
        {
          RecordStamper.stampForUpdate(prbSourceDAO.getState());
        }
        catch (RecordStampException e)
        {
          DBExceptionFactory ef = new DBExceptionFactory();
          DBException e2 = (DBException)
            ef.getException(DBExceptionFactory.RecordStampErr);
          e2.bind("PRB_Source");
          throw e2;
        }

        StringBuffer sql =  new StringBuffer("exec PRB_processSeqLoaderSource ");

        sql.append(Converter.toString(this.seqSourceAssocDAO.getKey().
                                      getKey()) + ", ");
        sql.append(Converter.toString(this.seqSourceAssocDAO.getState().
                                      getSequenceKey()) + ", ");
        sql.append(Converter.toString(this.prbSourceDAO.getKey().getKey()) +
                                      ", ");
        sql.append(Converter.toString(this.prbSourceDAO.getState().
                                      getOrganismKey()) + ", ");
        sql.append(Converter.toString(this.prbSourceDAO.getState().
                                      getStrainKey()) + ", ");
        sql.append(Converter.toString(this.prbSourceDAO.getState().
                                      getTissueKey()) + ", ");
        sql.append(Converter.toString(this.prbSourceDAO.getState().
                                      getGenderKey()) + ", ");
        sql.append(Converter.toString(this.prbSourceDAO.getState().
                                      getCellLineKey()) + ", ");
        sql.append(Converter.toString(this.prbSourceDAO.getState().
                                      getModifiedByKey()));
        return new String(sql);

    }

    /**
     * get the sql for doing deletes.
     * currently this is not implemented and a runtime exception is thrown when
     * this method called
     * @assumes nothing
     * @effects a runtime exception is thrown
     * @return the insert sql for this instance once the implementation is
     * completed. currently a runtime exception is called
     */
    public String getDeleteSQL()
    {
        throw MGIException.getUnsupportedMethodException();
    }

    /**
     * find a MSSeqAssoc object from the database with the given
     * sequence key and organism key
     * @param seqkey the sequence key
     * @param organismKey the organism key
     * @return the MolecularSource
     * @throws DBException thrown if there is an error with the database
     * @throws ConfigException thrown if there is an error with the
     * configuration
     * @throws MSException thrown if more than one row is returned
     */
    public static MSSeqAssoc findBySeqKeyOrganism(Integer seqkey,
                                                  Integer organismKey)
    throws DBException, ConfigException, MSException
    {

        class MSSeqSourceInterpreter implements RowDataInterpreter
        {
            SEQ_Source_AssocInterpreter seqSourceInterpreter =
                new SEQ_Source_AssocInterpreter();
            PRB_SourceInterpreter prbSourceInterpreter =
                new PRB_SourceInterpreter();

            public Object interpret(RowReference row) throws DBException
            {
                SEQ_Source_AssocDAO seqSourceDAO =
                    (SEQ_Source_AssocDAO)seqSourceInterpreter.interpret(row);
                PRB_SourceDAO prbSourceDAO =
                    (PRB_SourceDAO)prbSourceInterpreter.interpret(row);
                MSSeqAssoc assoc = null;
                try
                {
                    assoc = new MSSeqAssoc(prbSourceDAO, seqSourceDAO);
                }
                catch (ConfigException e)
                {
                    DBExceptionFactory eFactory = new DBExceptionFactory();
                    DBException e2 =
                        (DBException)eFactory.getException(ConfigErr, e);
                    throw e2;
                }
                return assoc;
            }
        }

        SQLDataManager sqlMgr =
            SQLDataManagerFactory.getShared(SchemaConstants.MGD);
        String sql =
          "SELECT src.*, " +
          "       assoc." + MGD.seq_source_assoc._assoc_key + ", " +
          "       assoc." + MGD.seq_source_assoc._sequence_key + " " +
          "FROM " + MGD.prb_source._name + " src, " +
                    MGD.seq_source_assoc._name + " assoc " +
          "WHERE src." + MGD.prb_source._source_key + " = " +
                "assoc." + MGD.seq_source_assoc._source_key + " " +
          "AND assoc." + MGD.seq_source_assoc._sequence_key + " = " +
                   Converter.toString(seqkey) + " " +
          "AND " + MGD.prb_source._organism_key + " = " +
                   Converter.toString(organismKey);
        ResultsNavigator nav = sqlMgr.executeQuery(sql);
        if (!nav.next())
            return null;
        nav.setInterpreter(new MSSeqSourceInterpreter());
        MSSeqAssoc assoc = (MSSeqAssoc)nav.getCurrent();
        if (nav.next()) // more than one record was found...throw exception
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e =
                (MSException)eFactory.getException(TooManyRows);
            e.bind(1);
            e.bind(sql);
            throw e;
        }
        nav.close();
        return assoc;
    }
}
