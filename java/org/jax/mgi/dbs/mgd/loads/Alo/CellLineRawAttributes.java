package org.jax.mgi.dbs.mgd.loads.Alo;

/**
 * An object that represents raw values needed to create a ALL_CellLineState object
 * including an accID for creating a ACC_AccessionState for a cell line
 * @has
 *   <UL>
 *   <LI> set of raw attributes
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>provides getters and setters for each attribute
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CellLineRawAttributes {
    // cell line name
    private String cellLine = null;
    // cell line type
    private String type = null;
    // cell line strain
    private String strain = null;
    // raw cell line derivation object
    private DerivationRawAttributes rawDerivation = null;
    // true of this is a mutant cell line
    private Boolean isMutant = null;
    // ID to be associated via the accession table (for gene traps same as
    // cellLine attribute)
    private String cellLineID = null;
    // logicalDB name for 'cellLineID'
    private String logicalDB = null;
     
     /**
     * set the cellLine attribute
     * @assumes Nothing
     * @effects Nothing
     * @param cellLine the cellLine name
     */

    public void setCellLine (String cellLine) { this.cellLine = cellLine; }

    /**
     * set the type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param type the cell line type
     */

    public void setType (String type) { this.type = type; }

    /**
     * set the strain attribute
     * @assumes Nothing
     * @effects Nothing
     * @param strain the cell line strain
     */

    public void setStrain (String strain) { this.strain = strain; }

    /**
     * set the mode attribute
     * @assumes Nothing
     * @effects Nothing
     * @param mode allele inheritance mode
     */

    public void setDerivation (DerivationRawAttributes raw) { 
	this.rawDerivation = raw; 
    }

    /**
     * set the isMutant attribute
     * @assumes Nothing
     * @effects Nothing
     * @param  isMutant true if mutant cell line
     */

    public void setIsMutant (Boolean isMutant) { this.isMutant = isMutant; }

    /**
     * set the cellLineID attribute
     * @assumes Nothing
     * @effects Nothing
     * @param cellLineID ID to be associated via the accession table
     */

    public void setCellLineID (String cellLineID) { 
	this.cellLineID = cellLineID; 
    }

    /**
     * set the cellLineID logicalDB attribute
     * @assumes Nothing
     * @effects Nothing
     * @param ldb logicalDB with which to associate 'cellLineID' 
     * via the accession table
     */
    public void setLogicalDB (String ldb) {
	this.logicalDB = ldb;
    }
    
    /**
     * get the cellLine attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getCellLine () { return cellLine; }

    /**
     * get the cell line type attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getType () { return type; }

        /**
     * get the cell line strain attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getStrain () { return strain; }
    
    /**
     * get the derivation attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public DerivationRawAttributes getDerivation () { return  rawDerivation; }

    /**
     * get the isMutant attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public Boolean getIsMutant () { return isMutant; }

    /**
     * get the cell line ID attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getCellLineID() { return cellLineID; }
    
    /**
     * get the logicalDB attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getLogicalDB() { return logicalDB; }

     /**
     * set all attributes to null
     * @assumes Nothing
     * @effects Nothing
     */
    
    public void reset() {
	cellLine = null;
        type = null;
	strain = null;
        rawDerivation = null;
        isMutant = null;
        cellLineID = null;
	logicalDB = null;
    }
}
