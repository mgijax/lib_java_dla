package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashSet;

/**
 * An object that represents raw values needed to create a ALL_AlleleState object
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

public class AlleleRawAttributes {

    // markers for this allele
    private HashSet markerMgiIDs = null;
    // strain of this allele e.g strain of specimen or strain of origin
    private String strain = null;
    // allele inheritance mode e.g. dominant or recessive
    private String inheritMode = null;
    // allele type e.g. targeted or gene trapped
    private String type = null;
    // allele status e.g. Approved or Autoload
    private String status = null;
    // allele symbol
    private String symbol = null;
    // allele name
    private String name = null;
    // true if allele is wild type allele
    private Boolean isWildType = null;
    // true if allele is extinct allele
    private Boolean isExtinct = null;
    // true if this is a mixed cell line
    private Boolean isMixed = null;
    // allele transmission e.g. germ line/chimera
    private String transmission = null;
    // how to determine if this allele is in the database e.g. MGI ID or symbol
    private String objectIdentity = null;
 
     /**
     * set the list of marker MGI IDs
     * @param markerMgiIDs set of markers for this allele 
     */

    public void setMarkers (HashSet markerMgiIDs) { 
	this.markerMgiIDs = markerMgiIDs; }

     /**
      * set a single marker MGI IDs
      * @param markerMgiID a marker for this allele
      */

    public void setMarker (String markerMgiID) {
        this.markerMgiIDs.add(markerMgiID);
    }

    /**
     * set the strain attribute
     * @param strain the strain of this allele
     */

    public void setStrain (String strain) { this.strain = strain; }

    /**
     * set the inheritance mode attribute
     * @param inheritMode allele inheritance mode
     */

    public void setInheritMode (String inheritMode) { this.inheritMode = inheritMode; }

    /**
     * set the type attribute
     * @param  type allele type
     */

    public void setType (String type) { this.type = type; }

    /**
     * set the status attribute
     * @param status  allele status
     */

    public void setStatus (String status) { this.status = status; }

    /**
     * set the symbol attribute
     * @param symbol the allele symbol
     */

    public void setSymbol (String symbol) { this.symbol = symbol; }

    /**
     * set the name attribute
     * @param name the allele name
     */

    public void setName (String name) { this.name = name; }

    /**
     * set the isWildType attribute
     * @param isWildType true if this is a wild type allele
     */

    public void setIsWildType (Boolean isWildType) {
        this.isWildType = isWildType;
    }

    /**
     * set the isExtinct attribute
     * @param isExtinct  true if this allele is extinct
     * this sequence
     */

    public void setIsExtinct (Boolean isExtinct) {
        this.isExtinct = isExtinct;
    }

    /**
     * set the isMixed attribute
     * @param isMixed  true if cell line is mixed
     */

    public void setIsMixed (Boolean isMixed) { this.isMixed = isMixed; }
    /**
     * set the transmission attribute
     * @param transmission attribute
     */

    public void setTransmission (String transmission) { 
	this.transmission = transmission; 
    }

    /**
     * set the objectIdentity attribute
     * @param objectIdentity - how to determine if this allele is in the 
     * database for example by MGI ID or symbol
     */

    public void setObjectIdentity (String objectIdentity) {
	this.objectIdentity = objectIdentity;
    }

    /**
     * get the set of marker mgiIDs for this allele
     */

    public HashSet getMarkers () { return markerMgiIDs; }

    /**
     * get the strain attribute
     */

    public String getStrain () { return strain; }

    /**
     * get the inheritance mode attribute
     */

    public String getInheritMode () { return  inheritMode; }

    /**
     * get the type attribute
     */

    public String getType () { return type; }

    /**
     * get the status attribute
     */

    public String getStatus () { return status; }

    /**
     * get the symbol attribute
     */

    public String getSymbol () { return symbol; }

    /**
     * get the name organisms attribute
     */

    public String getName () { return name; }

    /**
     * get the isWildType attribute
     */

    public Boolean getIsWildType () { return isWildType; }

    /**
     * get the isExtinct attribute
     */

    public Boolean getIsExtinct () { return isExtinct; }

    /**
     * get the isMixed attribute
     */

    public Boolean getIsMixed () { return isMixed; }

    /**
     * get the transmission attribute
     */

    public String getTransmission () { return transmission; }
    /**
     * get the objectIdentity attribute
     */

    public String getObjectIdentity () { return objectIdentity; }

     /**
     * set all attributes to null
     */

    public void reset() {
        markerMgiIDs = null;
        strain = null;
        inheritMode = null;
        type = null;
        status = null;
        symbol = null;
        name = null;
        isWildType = null;
        isExtinct = null;
        isMixed = null;
        transmission = null;
        objectIdentity = null;
    }
}
