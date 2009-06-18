package org.jax.mgi.dbs.mgd.loads.Alo;

/**
 * An object that represents raw values needed to find  
 * ALL_Allele_DerivationState object in the database
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

public class DerivationRawAttributes {

    // cell line library name or null
    private String name = null;
    // cell Line library description or null
    private String description = null;
    // vector name
    private String vectorName = null;
    // vectory type
    private String vectorType = null;
    // parent cell line
    private String parentCellLine = null;
    // strain of the parent cell line (dbGSS gene traps) may want have a 
    // CellLineRawAttributes representing the parent object instead at some point
    private String parentCellLineStrain =  null;
    // derivation type
    private String derivationType = null;
    // creator of the derivation
    private String creator = null;
    // J Number for the derivation
    private String jNum = null;
    
 
     /**
     * set the name attribute
     * @assumes Nothing
     * @effects Nothing
     * @param name cell line library  name or null
     */

    public void setName (String name) { this.name = name; }

    /**
     * set the description attribute
     * @assumes Nothing
     * @effects Nothing
     * @param description cell line library description 
     */

    public void setDescription (String description) { 
	this.description = description; 
    }

    /**
     * set the vectorName attribute
     * @assumes Nothing
     * @effects Nothing
     * @param vectorName derivation vector name
     */

    public void setVectorName (String vectorName) { 
	this.vectorName = vectorName; 
    }

    /**
     * set the vectorType attribute
     * @assumes Nothing
     * @effects Nothing
     * @param  vectorType allele vectorType
     */

    public void setVectorType (String vectorType) { 
	this.vectorType = vectorType; 
    }

    /**
     * set the parentCellLine attribute
     * @assumes Nothing
     * @effects Nothing
     * @param parentCellLine  parent cell line
     */

    public void setParentCellLine (String parentCellLine) { 
	this.parentCellLine = parentCellLine; 
    }

    /**
     * set the parentCellLine strain
     * @assumes Nothing
     * @effects Nothing
     * @param strain  parent cell line strain
     */

    public void setParentCellLineStrain (String strain) { 
	this.parentCellLineStrain = strain; 
    }
    
    /**
     * set the type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param type the derivation type
     */

    public void setDerivationType (String type) { 
	this.derivationType = type;
    }

    /**
     * set the creator attribute
     * @assumes Nothing
     * @effects Nothing
     * @param creator the derivation creator
     */

    public void setCreator (String creator) { 
	this.creator = creator; 
    }

    /**
     * set the JNumber for this derivation 
     * @assumes Nothing
     * @effects Nothing
     * @param reference derivation reference
     */

    public void setReference (String jNum) {
        this.jNum = jNum;
    }

   

    /**
     * get the derivation name attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getName () { return name; }

    /**
     * get the derivation description attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getDescription () { return description; }

    /**
     * get the vector name attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getVectorName () { return  vectorName; }

    /**
     * get the vector type attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getVectorType () { return vectorType; }

    /**
     * get the parent cell line attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getParentCellLine() { return parentCellLine; }

    /**
     * get the parent cell line strain attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getParentCellLineStrain() { return parentCellLineStrain; }
    
    /**
     * get the derivation type attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getDerivationType() { return derivationType; }

    /**
     * get the derivation creator attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getCreator () { return creator; }

    /**
     * get the derivation jNumber attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getJNum () { return jNum; }

    
    
     /**
     * set all attributes to null
     * @assumes Nothing
     * @effects Nothing
     */

    public void reset() {
	name = null;
	description = null;
	vectorName = null;
	vectorType = null;
	parentCellLine = null;
	derivationType = null;
	creator = null;
	jNum = null;
    }
}
