package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.BindableStatement;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.shr.dla.seqloader.AttrHistory;

/**
 * @is an object for looking up data in the mgi_attributeHistory table for
 * prb_source attributes
 * @has a connection to the MGD database and a set of queries
 * @does looks up data to see whether or not attributes from the prb_source
 * table have been edited by a curator
 * @company The Jackson Lab
 * @author M Walker
 * @version 1.0
 */

public class MSAttrHistory extends AttrHistory
{
    /**
     * contants for prb_source column names
     */
    protected static final String ORGANISM_COL = MGD.prb_source._organism_key;
    protected static final String TISSUE_COL = MGD.prb_source._tissue_key;
    protected static final String STRAIN_COL = MGD.prb_source._strain_key;
    protected static final String CELLLINE_COL = MGD.prb_source._cellline_key;
    protected static final String GENDER_COL = MGD.prb_source._gender_key;
    protected static final String AGE_COL = MGD.prb_source.age;

    /**
     * constructor
     * @throws ConfigException thrown if there is an error with the database
     * @throws DBException thrown if there is an error with configuration
     */
    public MSAttrHistory()
    throws ConfigException, DBException
    {
        super(MGITypeConstants.SOURCE);
    }

    /**
     * return whether or not the tissue attribute is curator edited
     * @assumes nothing
     * @effects nothing
     * @return true if the tissue attribute is curator edited,
     * false otherwise
     * @throws DBEXception thrown if there is an error with the database
     */
    public boolean isTissueCurated(Integer sourceKey)
    throws DBException
    {
      return isCurated(TISSUE_COL, sourceKey);
    }

    /**
     * return whether or not the age attribute is curator edited
     * assumes nothing
     * @effects nothing
     * @return true if the age attribute is curator edited,
     * false otherwise
     * @throws DBEXception thrown if there is an error with the database
     */
    public boolean isAgeCurated(Integer sourceKey)
    throws DBException
    {
      return isCurated(AGE_COL, sourceKey);
    }

    /**
     * return whether or not the cell line attribute is curator edited
     * @assumes nothinh
     * @effects nothing
     * @return true if the cell line attribute is curator edited,
     * false otherwise
     * @throws DBEXception thrown if there is an error with the database
     */
    public boolean isCellLineCurated(Integer sourceKey)
    throws DBException
    {
      return isCurated(CELLLINE_COL, sourceKey);
    }

    /**
     * return whether or not the strain attribute is curator edited
     * @assumes nothing
     * @effects nothing
     * @return true if the strain attribute is curator edited,
     * false otherwise
     * @throws DBEXception thrown if there is an error with the database
     */
    public boolean isStrainCurated(Integer sourceKey)
    throws DBException
    {
      return isCurated(STRAIN_COL, sourceKey);
    }

    /**
     * return whether or not the gender attribute is curator edited
     * @assumes nothing
     * @effects nothing
     * @return true if the gender attribute is curator edited,
     * false otherwise
     * @throws DBEXception thrown if there is an error with the database
     */
    public boolean isGenderCurated(Integer sourceKey)
    throws DBException
    {
      return isCurated(GENDER_COL, sourceKey);
    }

    /**
     * return whether or not the organism attribute is curator edited
     * @assumes nothing
     * @effects nothing
     * @return true if the organsim attribute is curator edited,
     * false otherwise
     * @throws DBEXception thrown if there is an error with the database
     */
    public boolean isOrganismCurated(Integer sourceKey)
    throws DBException
    {
      return isCurated(ORGANISM_COL, sourceKey);
    }


}