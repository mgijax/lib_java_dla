package org.jax.mgi.dbs.mgd.loads.SeqRefAssoc;

/**
 * An object that represents raw values for MGI_Reference_Assoc
 * @has
 *   <UL>
 *   <LI>MGI_Reference_Assoc column attributes
 *   </UL>
 * @does
 *   <UL>
 *   <LI>provides getters and setters for each column attribute
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class RefAssocRawAttributes {
    // instance variables representing raw values for MGI_Reference_Assoc table
    private String refId = null;
    private Integer refAssocType = null;
    private Integer mgiType = null;
    private String createdModifiedBy = null;

    /**
     * set the reference id attribute
     * @assumes Nothing
     * @effects Nothing
     * @param refId a reference id
     */

    public void setRefId(String refId) {this.refId = refId;}

    /**
     * set the reference association type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param refAssocType the reference association type
     */

    public void setRefAssocType(Integer refAssocType) {
        this.refAssocType = refAssocType;
    }

    /**
     * set the MGI type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param mgiType the MGI type of the object to be associated with the reference
     */

    public void setMgiType(Integer mgiType) {this.mgiType = mgiType;}

    /**
     * set the CreatedByModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @param createdModifiedBy the CreatedByModifiedBy attribute
     */

    public void setCreatedModifiedBy(String createdModifiedBy) {
        this.createdModifiedBy = createdModifiedBy;
    }

    /**
     * get the reference id attribute
     * @assumes Nothing
     * @effects Nothing
     * @return the reference id
     */


    public String getRefId() {return refId;}

    /**
     * get the reference association type attribute
     * @assumes Nothing
     * @effects Nothing
     * @return the reference association type
     */

    public Integer getRefAssocType() {return refAssocType;}

    /**
     * get the MGI type attribute
     * @assumes Nothing
     * @effects Nothing
     * @return the MGI type of the object to be associated with the reference
     */

    public Integer getmgiType() {return mgiType;}

    /**
     * get the CreatedByModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @return the CreatedByModifiedBy attribute
     */

    public String getCreatedModifiedBy() {return createdModifiedBy; }

    /**
     * Resets object values to null
     * @assumes Nothing
     * @effects Nothing
     */

    public void reset() {
        refId = null;
        refAssocType = null;
        mgiType = null;
        createdModifiedBy = null;
    }
}
