//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

/**
 * @is An object that represents raw values for MGI_Reference_Assoc
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
     * set the reference id
     * @assumes Nothing
     * @effects Nothing
     * @param a reference id
     * @return Nothing
     * @throws Nothing
     */

    public void setRefId(String refId) {this.refId = refId;}

    /**
     * set the reference association type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the reference association type
     * @return Nothing
     * @throws Nothing
     */

    public void setRefAssocType(Integer refAssocType) {
        this.refAssocType = refAssocType;
    }

    /**
     * set the MGI type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the MGI type of the object to be associated with the reference
     * @return Nothing
     * @throws Nothing
     */

    public void setMgiType(Integer mgiType) {this.mgiType = mgiType;}

    /**
     * set the CreatedByModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the CreatedByModifiedBy attribute
     * @return Nothing
     * @throws Nothing
     */

    public void setCreatedModifiedBy(String createdModifiedBy) {
        this.createdModifiedBy = createdModifiedBy;
    }

    /**
     * get the reference id attribute
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the reference id
     * @throws Nothing
     */


    public String getRefId() {return refId;}

    /**
     * get the reference association type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the reference association type
     * @throws Nothing
     */

    public Integer getRefAssocType() {return refAssocType;}

    /**
     * get the MGI type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the MGI type of the object to be associated with the reference
     * @throws Nothing
     */

    public Integer getmgiType() {return mgiType;}

    /**
     * get the CreatedByModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the CreatedByModifiedBy attribute
     * @throws Nothing
     */

    public String getCreatedModifiedBy() {return createdModifiedBy; }

    /**
     * Resets object values to null
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Nothing
     * @throws Nothing
     */

    public void reset() {
        refId = null;
        refAssocType = null;
        mgiType = null;
        createdModifiedBy = null;
    }
}

//  $Log$
//  Revision 1.1  2003/12/08 18:40:39  sc
//  initial commit
//

/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
