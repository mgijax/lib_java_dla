package org.jax.mgi.dbs.mgd.loads.Alo;

/**
 * An object that represents raw values needed to find  
 * ALL_Allele_Derivation object in the database
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
    * @param name cell line library  name or null
    */

    public void setName (String name) { this.name = name; }

    /**
    * set the description attribute
    * @param description cell line library description
    */

    public void setDescription (String description) { 
	this.description = description; 
    }

    /**
     * set the vectorName attribute
     * @param vectorName derivation vector name
     */

    public void setVectorName (String vectorName) { 
	this.vectorName = vectorName; 
    }

    /**
     * set the vectorType attribute
     * @param  vectorType allele vectorType
     */

    public void setVectorType (String vectorType) { 
	this.vectorType = vectorType; 
    }

    /**
     * set the parentCellLine attribute
     * @param parentCellLine  parent cell line
     */

    public void setParentCellLine (String parentCellLine) { 
	this.parentCellLine = parentCellLine; 
    }

    /**
     * set the parentCellLine strain
     * @param strain  parent cell line strain
     */

    public void setParentCellLineStrain (String strain) { 
	this.parentCellLineStrain = strain; 
    }
    
    /**
     * set the type attribute
     * @param type the derivation type
     */

    public void setDerivationType (String type) { 
	this.derivationType = type;
    }

    /**
     * set the creator attribute
     * @param creator the derivation creator
     */

    public void setCreator (String creator) { 
	this.creator = creator; 
    }

    /**
     * set the JNumber for this derivation 
     * @param jNum derivation reference
     */

    public void setReference (String jNum) {
        this.jNum = jNum;
    }

   

    /**
     * get the derivation name attribute
	 * @return derivation name
     */

    public String getName () { return name; }

    /**
     * get the derivation description attribute
     * @return derivation description
     */

    public String getDescription () { return description; }

    /**
     * get the vector name attribute
     * @return vector name
     */

    public String getVectorName () { return  vectorName; }

    /**
     * get the vector type attribute
     * @return vector type
     */

    public String getVectorType () { return vectorType; }

    /**
     * get the parent cell line attribute
     * @@return parent cell line name
     */

    public String getParentCellLine() { return parentCellLine; }

    /**
     * get the parent cell line strain attribute
     * @return parent cell line strain
     */

    public String getParentCellLineStrain() { return parentCellLineStrain; }
    
    /**
     * get the derivation type attribute
     * @return derivation type
     */

    public String getDerivationType() { return derivationType; }

    /**
     * get the derivation creator attribute
     * @return derivation creator
     */

    public String getCreator () { return creator; }

    /**
     * get the derivation jNumber attribute
     * @return derivation reference
     */

    public String getJNum () { return jNum; }

    
    
     /**
     * reset the instance - set all attributes to null
     */

    public void reset() {
	name = null;
	description = null;
	vectorName = null;
	vectorType = null;
	parentCellLine = null;
	parentCellLineStrain = null;
	derivationType = null;
	creator = null;
	jNum = null;
    }
}
