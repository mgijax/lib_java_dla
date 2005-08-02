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

//  $Log$
//  Revision 1.2.6.1  2004/12/10 18:15:13  mbw
//  javadocs only
//
//  Revision 1.2  2004/12/07 20:09:49  mbw
//  merged tr6047 onto the trunk
//
//  Revision 1.1.2.1  2004/11/05 16:10:17  mbw
//  classes were renamed and reloacated as part of large refactoring effort (see tr6047)
//
//  Revision 1.4  2004/07/08 15:03:49  sc
//  javdocs changes
//
//  Revision 1.3  2004/06/30 19:35:42  mbw
//  javadocs only
//
//  Revision 1.2  2004/06/30 17:25:35  sc
//  merging sc2 branch to trunk
//
//  Revision 1.1.4.1  2004/05/18 15:32:08  sc
//  updated class/method headers
//
//  Revision 1.1  2004/01/06 20:09:39  mbw
//  initial version imported from lib_java_seqloader
//
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
