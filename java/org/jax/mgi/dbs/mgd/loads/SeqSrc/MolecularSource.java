package org.jax.mgi.dbs.mgd.loads.SeqSrc;

import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceDAO;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceState;
import org.jax.mgi.dbs.mgd.dao.PRB_SourceKey;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object which represents a molecular source
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

  protected Boolean curatedEditedTissue = false;
  protected Boolean curatedEditedStrain = false;
  protected Boolean curatedEditedGender = false;
  protected Boolean curatedEditedCellLine = false;
  protected Boolean curatedEditedOrganism = false;
  protected Boolean curatedEditedAge = false;

  /**
   * the delimiter to use within the toString method
   */
  private static final String DELIMITER = ".";

  /**
   * these constants are used for setting age, ageMin and ageMax
   */
  private static final String NOT_APPLICABLE = "Not Applicable";
  private static final String NOT_RESOLVED = "Not Resolved";
  private static final String NOT_SPECIFIED = "Not Specified";
  private static final Double ageMin = new Double(-1.0);
  private static final Double ageMax = new Double(-1.0);

  /*
   * the following constant definitions are exceptions thrown by this class
   */
  private static String AlreadyOnSQLStream =
      MSExceptionFactory.AlreadyOnSQLStream;
  private static String AlreadyInDatabase =
      MSExceptionFactory.AlreadyInDatabase;
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
  public MolecularSource(PRB_SourceDAO dao)
  {
    this.state = dao.getState();
    this.key = dao.getKey();
  }

  /**
   * get the DAO object for this instance
   * @assumes nothing
   * @effects a new PRB_SourceDAO object is created
   * @return PRB_SourceDAO object
   */
  public PRB_SourceDAO getSourceDAO()
  {
      return new PRB_SourceDAO(this.key, this.state);
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
        age.equals(NOT_RESOLVED) ||
        age.equals(NOT_SPECIFIED))
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
   * @throws MSException thrown if there is an error looking up history
   * records
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

    return this.curatedEditedTissue.booleanValue();
  }

  /**
   * return whether or not the age attribute is curator edited
   * @return true if the age attribute is curator edited,
   * false otherwise
   * @throws MSException thrown if there is an error looking up history
   * records
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

    return this.curatedEditedAge.booleanValue();

  }

  /**
   * return whether or not the cell line attribute is curator edited
   * @return true if the cell line attribute is curator edited,
   * false otherwise
   * @throws MSException thrown if there is an error looking up history
   * records
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

    return this.curatedEditedCellLine.booleanValue();

  }

  /**
   * return whether or not the strain attribute is curator edited
   * @return true if the strain attribute is curator edited,
   * false otherwise
   * @throws MSException thrown if there is an error looking up history
   * records
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

    return this.curatedEditedStrain.booleanValue();

  }

  /**
   * return whether or not the gender attribute is curator edited
   * @return true if the gender attribute is curator edited,
   * false otherwise
   * @throws MSException thrown if there is an error looking up history
   * records
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

    return this.curatedEditedGender.booleanValue();

  }

  /**
   * return whether or not the organism attribute is curator edited
   * @return true if the organsim attribute is curator edited,
   * false otherwise
   * @throws MSException thrown if there is an error looking up history
   * records
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

    return this.curatedEditedOrganism.booleanValue();

  }

  /**
   * insert this instance to the database.
   * @assumes nothing
   * @effects a record will be inserted into the database
   * @param stream the SQLStream to insert on
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

    if (key == null) {
        PRB_SourceDAO psd = new PRB_SourceDAO(state);
        this.key = psd.getKey();
        stream.insert(psd);
    }
    else
      stream.insert(new PRB_SourceDAO(key, state));

    this.isInBatch = true;

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
        this.getAge()  + DELIMITER +
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
