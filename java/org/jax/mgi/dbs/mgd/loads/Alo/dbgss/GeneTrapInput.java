package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

/**
 * An object that represents gene trap data in its raw form.

 * @has
 *   <UL>
 *   <LI>
 *   <LI>A Vector of MSRawAttributes (1 or more)
 *   <LI>A Vector of RefAssocRawAttributes (0 or more)
 *   <LI>An AccessionRawAttributes for the primary seqid of the sequence
 *   <LI>A Vector of AccessionRawAttributes (0 ore more)for any
 *        secondary accession ids for a sequence
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Provides getters and setters for its attributes
 *   <LI>Resets itself
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class GeneTrapInput {
    // dbGSS primary sequence accession ID
    private String seqID;
    // sequence Type (DNA or RNA)
    private String seqType;
    // organism
    private String organism;
    // cell line creator
    private String creator;
    // sequence tag method
    private String seqTagMethod;
    // strain
    private String strain;
    // cell ID is available from different sections of a sequence record
    // depending on creator
    private String definitionCellLineID;
    private String featureCellLineID;

    // cell line name
    private String cellLineName;
    // clone library
    private String libraryName;
    // vector name
    private String gtVector;

    public GeneTrapInput() {
    }
    // set methods
    public void setSeqID(String s){seqID = s;}
    public void setSeqType(String s){seqType = s;}
    public void setOrganism(String s){organism = s;}
    public void setCreator(String s){creator = s;}
    public void setSeqTagMethod(String s){seqTagMethod = s;}
    public void setStrain(String s){strain = s;}
    public void setDefinitionCellLineID(String s){definitionCellLineID = s;}
    public void setFeatureCellLineID(String s){featureCellLineID = s;}
    public void setCellLineName(String s){cellLineName = s;}
    public void setLibraryName(String s){libraryName = s;}
    public void setGtVector(String s){gtVector= s;}

    // get methods
    public String getSeqID() {return seqID;}
    public String getSeqType() {return seqType;}
    public String getOrganism() {return organism;}
    public String getCreator() {return creator;}
    public String getSeqTagMethod() {return seqTagMethod;}
    public String getStrain() {return strain;}
    public String getDefinitionCellLineID() {return definitionCellLineID;}
    public String getFeatureCellLineID() {return featureCellLineID;}
    public String getCellLineName() {return cellLineName;}
    public String getLibraryName() {return libraryName;}
    public String getGtVector() {return gtVector;}

    /**
     * clears sets instance variable to null
     * @assumes Nothing
     * @effects Nothing
     */

    public void reset() {
        seqID = null;
        seqType = null;
        organism = null;
        creator = null;
        seqTagMethod = null;
        strain = null;
        definitionCellLineID = null;
        featureCellLineID = null;
        cellLineName = null;
        libraryName = null;
        gtVector = null;
    }

}