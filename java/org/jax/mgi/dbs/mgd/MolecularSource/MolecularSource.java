package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceDAO;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceState;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceKey;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceLookup;
import org.jax.mgi.dbs.mgd.dao.MGI_AttributeHistoryDAO;
import org.jax.mgi.dbs.mgd.dao.MGI_AttributeHistoryState;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.dao.SQLTranslatable;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * @is an object which represents a molecular source
 * @has a PRB_SourceDAO object and a SQLStream object for commiting instances
 * to the PRB_Source table
 * @does provides accessors for the attributes, looks up to see if
 * any of the attributes have been currated edited, and updates and
 * inserts itself into the PRB_Source table
 */

public class MolecularSource
{

  /**
   * an indicator of whether or not this instance represents an
   * existing record in the database
   */
  protected boolean isInDatabase = false;

  /**
   * an indicator of whether or not this instance has been added
   * to an SQLStream which performs
   */
  protected boolean isInBatch = false;


  /**
   * an indicator of whether or not this instance has changed since
   * it was obtained from the database. If isInDatabase is true, then
   * this field is irrelevant.
   */
  protected boolean hasChanged = false;

  /**
   * the dao state object for storing PRB_Source column values other than
   * the _source_key which is stored in the key variable
   */
  protected PRB_SourceState state = null;

  /**
   * the variable for storing a sequential key value for a
   * record in the PRB_Source table
   */
  protected PRB_SourceKey key = null;

  protected Boolean curatedEditedTissue = null;
  protected Boolean curatedEditedStrain = null;
  protected Boolean curatedEditedGender = null;
  protected Boolean curatedEditedCellLine = null;
  protected Boolean curatedEditedOrganism = null;
  protected Boolean curatedEditedAge = null;

  /**
   * the class for checking MolecularSource attribute history
   */
  protected MSAttrHistory history = null;


  /**
   * the delimiter to use within the toString method
   */
  private static final String DELIMITER = ".";

  /**
   * these constants are used for setting age, ageMin and ageMax
   */
  private static final String NOT_APPLICABLE = "Not Applicable";
  private static final String NOT_RESOLVED = "Not Resolved";
  private static final Float ageMin = new Float(-1.0);
  private static final Float ageMax = new Float(-1.0);

  /*
   * the following constant definitions are exceptions thrown by this class
   */
  private static String AlreadyOnSQLStream =
      MSExceptionFactory.AlreadyOnSQLStream;
  private static String AlreadyInDatabase =
      MSExceptionFactory.AlreadyInDatabase;
  private static String AttrHistoryErr = MSExceptionFactory.AttrHistoryErr;
  private static String NoKeyFound = MSExceptionFactory.NoKeyFound;


  /**
   * constructor which creates a new MolecularSource and assigns no database key
   * @throws DBException thrown if there is an error with database
   * @throws ConfigException thrown if there is an error with configuration
   */
  public MolecularSource()
  {
    this.state = new PRB_SourceState();
    this.state.setIsCuratorEdited(new Boolean(false));
  }

  /**
   * constructor which accepts a given DAO object
   * @param dao the DAO object
   */
  protected MolecularSource(PRB_SourceDAO dao)
  {
    this.state = dao.getState();
    this.key = dao.getKey();
  }

  /**
   * assign the next database key value to this object
   * @assumes nothing
   * @effects the key value for this object will be replaced with the next
   * available key for the PRB_Source table
   * @throws ConfigException thrown if there is an exception with the
   * configuration
   * @throws DBException thrown if there is an error with the database
   */
  public void assignKey() throws ConfigException, DBException
  {
    this.key = new PRB_SourceKey();
  }

  /**
   * get the database key for this object
   * @return the database key
   */
  public Integer getMSKey()
  {
    if (key == null)
      return null;
    else
      return key.getKey();
  }

  /**
   * get the name of this source
   * @return the source name
   */
  public String getName()
  {
    return this.state.getName();
  }

  /**
   * get the age of this source
   * @return the age value
   */
  public String getAge()
  {
    return this.state.getAge();
  }

  /**
   * get the organism key
   * @return the organism key
   */
  public Integer getOrganismKey()
  {
    return this.state.getOrganismKey();
  }

  /**
   * get the tissue key
   * @return the tissue key
   */
  public Integer getTissueKey()
  {
    return this.state.getTissueKey();
  }

  /**
   * get the strain key
   * @return the strain key
   */
  public Integer getStrainKey()
  {
    return this.state.getStrainKey();
  }

  /**
   * get the cell line key
   * @return the cell line key
   */
  public Integer getCellLineKey()
  {
    return this.state.getCellLineKey();
  }

  /**
   * get the gender key
   * @return the gender key
   */
  public Integer getGenderKey()
  {
    return this.state.getGenderKey();
  }

  /**
   * get the segment type key
   * @return the segment type key
   */
  public Integer getSegmentTypeKey()
  {
      return this.state.getSegmentTypeKey();
  }

  /**
   * get the vector type key
   * @return the vector type key
   */
  public Integer getVectorTypeKey()
  {
      return this.state.getVectorKey();
  }


  /**
   * get whether or not this record has been currated edited
   * @return true if this record has been currated edited, false otherwise
   */
  public Boolean getCuratorEdited()
  {
    return this.state.getIsCuratorEdited();
  }

  /**
   * set the source name
   * @param name the source name
   */
  public void setName(String name)
  {
    this.state.setName(name);
  }

  /**
   * set the age
   * @param age the age value
   */
  public void setAge(String age)
  {
    this.state.setAge(age);
    if (age.equals(NOT_APPLICABLE) ||
        age.equals(NOT_RESOLVED))
    {
      this.state.setAgeMax(ageMax);
      this.state.setAgeMin(ageMin);
    }
  }

  /**
   * set the organism key
   * @param key the organism key
   */
  public void setOrganismKey(Integer key)
  {
    this.state.setOrganismKey(key);
  }

  /**
   * set the strain key
   * @param key the organism key
   */
  public void setStrainKey(Integer key)
  {
    this.state.setStrainKey(key);
  }

  /**
   * set the tissue key
   * @param key the tissue key
   */
  public void setTissueKey(Integer key)
  {
    this.state.setTissueKey(key);
  }

  /**
   * set the cell line key
   * @param key the cell line key
   */
  public void setCellLineKey(Integer key)
  {
    this.state.setCellLineKey(key);
  }

  /**
   * set the gender key
   * @param key the gender key
   */
  public void setGenderKey(Integer key)
  {
    this.state.setGenderKey(key);
  }

  /**
   * set the segment type key
   * @param key the segment type key
   */
  public void setSegmentTypeKey(Integer key)
  {
    this.state.setSegmentTypeKey(key);
  }

  /**
   * set the vector type key
   * @param key the vector type key
   */
  public void setVectorTypeKey(Integer key)
  {
    this.state.setVectorKey(key);
  }

  /**
   * get whether or not this object is currently in the database
   * @return true if in the database; false otherwise
   */
  public boolean isInDatabase()
  {
      return this.isInDatabase;
  }

  /**
   * get whether or not this object is currently in a batch stream
   * @return true if in a batch stream; false otherwise
   */
  public boolean isInBatch()
  {
      return this.isInBatch;
  }



  /**
   * return whether or not the tissue attribute is curator edited
   * @return true if the tissue attribute is curator edited,
   * false otherwise
   */
  public boolean isTissueCurated()
  throws MSException
  {
      /**
       * no need to check attribute history if the record
       * hasnt been curator edited
       */
    if (!this.getCuratorEdited().booleanValue())
        return false;

    /**
     * check instance variable first to see if it has been set
     */
    if (this.curatedEditedTissue == null)
    {
        /**
         * get history and set instance variable
         */
        try
        {
            if (history == null)
                history = new MSAttrHistory();
            this.curatedEditedTissue =
                new Boolean(history.isTissueCurated(this.getMSKey()));
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 =
                (MSException)eFactory.getException(this.AttrHistoryErr, e);
            e2.bind("tissue");
            throw e2;
        }
    }
    return this.curatedEditedTissue.booleanValue();
  }

  /**
   * return whether or not the age attribute is curator edited
   * @return true if the age attribute is curator edited,
   * false otherwise
   */
  public boolean isAgeCurated()
  throws MSException
  {
      /**
       * no need to check attribute history if the record
       * hasnt been curator edited
       */
    if (!this.getCuratorEdited().booleanValue())
        return false;

    /**
     * check instance variable first to see if it has been set
     */
    if (this.curatedEditedAge == null)
    {
        /**
         * get history and set instance variable
         */
        try
        {
            if (history == null)
                history = new MSAttrHistory();
            this.curatedEditedAge =
                new Boolean(history.isAgeCurated(this.getMSKey()));
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 =
                (MSException)eFactory.getException(this.AttrHistoryErr, e);
            e2.bind("age");
            throw e2;
        }


    }
    return this.curatedEditedAge.booleanValue();

  }

  /**
   * return whether or not the cell line attribute is curator edited
   * @return true if the cell line attribute is curator edited,
   * false otherwise
   */
  public boolean isCellLineCurated()
  throws MSException
  {
      /**
       * no need to check attribute history if the record
       * hasnt been curator edited
       */
    if (!this.getCuratorEdited().booleanValue())
        return false;

    /**
     * check instance variable first to see if it has been set
     */
    if (this.curatedEditedCellLine == null)
    {
        /**
         * get history and set instance variable
         */
        try
        {
            if (history == null)
                history = new MSAttrHistory();
            this.curatedEditedCellLine =
                new Boolean(history.isCellLineCurated(this.getMSKey()));
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 =
                (MSException)eFactory.getException(this.AttrHistoryErr, e);
            e2.bind("cellLine");
            throw e2;
        }


    }
    return this.curatedEditedCellLine.booleanValue();

  }

  /**
   * return whether or not the strain attribute is curator edited
   * @return true if the strain attribute is curator edited,
   * false otherwise
   */
  public boolean isStrainCurated()
  throws MSException
  {
      /**
       * no need to check attribute history if the record
       * hasnt been curator edited
       */
    if (!this.getCuratorEdited().booleanValue())
        return false;

    /**
     * check instance variable first to see if it has been set
     */
    if (this.curatedEditedStrain == null)
    {
        /**
         * get history and set instance variable
         */
        try
        {
            if (history == null)
                history = new MSAttrHistory();
            this.curatedEditedStrain =
                new Boolean(history.isStrainCurated(this.getMSKey()));
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 =
                (MSException)eFactory.getException(this.AttrHistoryErr, e);
            e2.bind("strain");
            throw e2;
        }


    }
    return this.curatedEditedStrain.booleanValue();

  }

  /**
   * return whether or not the gender attribute is curator edited
   * @return true if the gender attribute is curator edited,
   * false otherwise
   */
  public boolean isGenderCurated()
  throws MSException
  {
      /**
       * no need to check attribute history if the record
       * hasnt been curator edited
       */
    if (!this.getCuratorEdited().booleanValue())
        return false;

    /**
     * check instance variable first to see if it has been set
     */
    if (this.curatedEditedGender == null)
    {
        /**
         * get history and set instance variable
         */
        try
        {
            if (history == null)
                history = new MSAttrHistory();
            this.curatedEditedGender =
                new Boolean(history.isGenderCurated(this.getMSKey()));
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 =
                (MSException)eFactory.getException(this.AttrHistoryErr, e);
            e2.bind("gender");
            throw e2;
        }


    }
    return this.curatedEditedGender.booleanValue();

  }

  /**
   * return whether or not the organism attribute is curator edited
   * @return true if the organsim attribute is curator edited,
   * false otherwise
   */
  public boolean isOrganismCurated()
  throws MSException
  {
      /**
       * no need to check attribute history if the record
       * hasnt been curator edited
       */
    if (!this.getCuratorEdited().booleanValue())
        return false;

    /**
     * check instance variable first to see if it has been set
     */
    if (this.curatedEditedOrganism == null)
    {
        /**
         * get history and set instance variable
         */
        try
        {
            if (history == null)
                history = new MSAttrHistory();
            this.curatedEditedOrganism =
                new Boolean(history.isOrganismCurated(this.getMSKey()));
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 =
                (MSException)eFactory.getException(this.AttrHistoryErr, e);
            e2.bind("organism");
            throw e2;
        }

    }
    return this.curatedEditedOrganism.booleanValue();

  }

  /**
   * insert this instance to the database.
   * @assumes nothing
   * @effects a record will be inserted into the database
   * @throws DBException thrown if there is an error with the database
   * @throws ConfigException thrown if there is an error with configuration
   * @throws MSException thrown if this object is already in the database
   * or has already been added to the SQLStream
   */
  public void insert(SQLStream stream)
      throws ConfigException, DBException, MSException
  {
    if (this.isInBatch)
    {
        MSExceptionFactory eFactory = new MSExceptionFactory();
        MSException e = (MSException)
            eFactory.getException(AlreadyOnSQLStream);
        e.bind(toString());
        throw e;
    }
    if (this.isInDatabase)
    {
        MSExceptionFactory eFactory = new MSExceptionFactory();
        MSException e = (MSException)
            eFactory.getException(AlreadyInDatabase);
        e.bind(toString());
        throw e;
    }

    if (key == null)
      stream.insert(new PRB_SourceDAO(state));
    else
      stream.insert(new PRB_SourceDAO(key, state));
    this.isInBatch = true;
    /**
     * triggers are in place in the database to update records in the
     * MGI_AttributeHistory table when new records are inserted into the
     * PRB_Source table. If this stream uses a BCPStrategy for doing inserts,
     * then records will have be added to the MGI_AttributeHistory table
     * at this point since the insert triggers will not get fired
     */
    if (stream.isBCP())
    {
      MGI_AttributeHistoryState state = new MGI_AttributeHistoryState();
      state.setMGITypeKey(new Integer(MGITypeConstants.SOURCE));
      state.setObjectKey(this.key.getKey());

      state.setColumnName(MGD.prb_source._segmenttype_key);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source._vector_key);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source._organism_key);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source._strain_key);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source._tissue_key);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source._cellline_key);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source._refs_key);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source.name);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source.description);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source.age);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source.agemin);
      stream.insert(new MGI_AttributeHistoryDAO(state));

      state.setColumnName(MGD.prb_source.agemax);
      stream.insert(new MGI_AttributeHistoryDAO(state));
    }
  }



  /**
   * get a string which represents this instance. If this instance is
   * not an anonymous source then return it's name else concatenate
   * organismKey, strainKey, tissueKey, cellLineKey and genderKey
   * @return a string to represent this instance
   */
  public String toString()
  {
    if (this.getName() != null)
      return this.getName();
    else
      return this.getOrganismKey() + DELIMITER +
        this.getStrainKey() + DELIMITER +
        this.getTissueKey() + DELIMITER +
        this.getGenderKey() + DELIMITER +
        this.getCellLineKey() + DELIMITER +
        this.getVectorTypeKey() + DELIMITER +
        this.getSegmentTypeKey();
  }

  /**
   * used by MSLookup method to indicate that this object came from the
   * database
   * @param bool true if the object is in the database, false otherwise
   */
  protected void setInDatabase(boolean bool)
  {
      this.isInDatabase = bool;
  }
}