package org.jax.mgi.dbs.mgd.MolecularSource;

/**
 * @is An object that stores raw attribute values for a MolecularSource object.
 * @has the following attributes:
 * <UL>
 * <li> library name </li>
 * <li> organism </li>
 * <li> strain </li>
 * <li> cell line </li>
 * <li> tissue </li>
 * <li> gender </li>
 * </UL>
 * @does nothing
 * @author M Walker
 * @company The Jackson Laboratory
 */

public class MSRawAttributes
{

  private String libraryName;
  private String organism;
  private String tissue;
  private String strain;
  private String gender;
  private String cellLine;
  private String age;

  /**
   * get the library name
   * @return the library name
   */
  public String getLibraryName(){ return libraryName; }

  /**
   * set the library name
   * @param libraryName the library name
   */
  public void setLibraryName(String libraryName)
  {
      this.libraryName = libraryName;
  }

  /**
   * get the organism
   * @return the organism
   */
  public String getOrganism(){ return organism; }

  /**
   * set the organism
   * @param organism the organism value
   */
  public void setOrganism(String organism){ this.organism = organism; }

  /**
   * get the tissue
   * @return the tissue
   */
  public String getTissue(){ return tissue; }

  /**
   * set the tissue
   * @param tissue the tissue value
   */
  public void setTissue(String tissue){ this.tissue = tissue; }

  /**
   * get the strain
   * @return the strain
   */
  public String getStrain(){ return strain; }

  /**
   * set the strain
   * @param strain the strain value
   */
  public void setStrain(String strain){ this.strain = strain; }

  /**
   * get the gender
   * @return the gender
   */
  public String getGender(){ return gender; }

  /**
   * set the gender
   * @param gender the gender value
   */
  public void setGender(String gender){ this.gender = gender; }

  /**
   * get the cell line
   * @return the cell line
   */
  public String getCellLine(){ return cellLine; }

  /**
   * set the cell line
   * @param cellLine the cell line value
   */
  public void setCellLine(String cellLine) {
    this.cellLine = cellLine;
  }

  /**
  * get the age
  * @return the age
  */
 public String getAge(){ return age; }

 /**
  * set the age
  * @param age the age value
  */
 public void setAge(String age) {
   this.age = age;
 }

  /**
   * rest the object attributes in order to reuse this object
   * @assumes nothing
   * @effects all attributes will be set to null
   */
  public void reset() {
    libraryName = null;
    organism = null;
    tissue = null;
    strain = null;
    gender = null;
    cellLine = null;
  }

  /**
   * return a string representation
   * @assumes nothing
   * #effects nothing
   * @return a string representation of this instance
   */
  public String toString()
  {
      return "libraryName = " + libraryName + ", organism = " + organism +
          ", tissue = " + tissue + ",strain = " + strain + ", gender = " +
          gender + ", cellLine = " + cellLine;
  }


}
