//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

import java.util.Vector;
import org.jax.mgi.dbs.mgd.AccessionLib;

    /**
     * @is An object that represents raw values for ACC_Accession
     * @has
     *   <UL>
     *   <LI>ACC_Accession column attributes
     *   </UL>
     * @does
     *   <UL>
     *   <LI>provides getters and setters for each column attribute
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class AccessionRawAttributes {
    // instance variables representing raw values for ACC_Accession table
    private String accid = null;
    private String prefixPart = null;
    private Integer numericPart = null;
    private String logicalDB = null;
    private Integer mgiType = null;
    private Boolean isPrivate = Boolean.FALSE;
    private Boolean isPreferred = Boolean.TRUE;
    private String createdModifiedBy = null;

    /**
     * sets the accid, prefixPart, and numericPart attributes
     * @assumes Nothing
     * @effects Nothing
     * @param accid an accession id
     */

    public void setAccid(String accid) {
        this.accid = accid;
        // split up the accid into its prefix and numeric part
        Vector splitAccession = AccessionLib.splitAccID(accid);
        prefixPart = (String)splitAccession.get(0);
        numericPart = (Integer)splitAccession.get(1);
    }

    /**
     * set the logical db attribute
     * @assumes Nothing
     * @effects Nothing
     * @param logicalDB the logical db for the accession id
     */

    public void setLogicalDB(String logicalDB) {
        this.logicalDB = logicalDB;
    }

    /**
     * set the MGI type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param mgiType the MGI type for this accession
     */

    public void setMgiType(Integer mgiType) {
        this.mgiType = mgiType;
    }

    /**
     * set the isPrivate attribute
     * @assumes Nothing
     * @effects Nothing
     * @param isPrivate true if this is a private accession id
     */

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
     * set isPreferred attribute
     * @assumes Nothing
     * @effects Nothing
     * @param isPreferred true if this is a preferred accession id
     */

    public void setIsPreferred(Boolean isPreferred) {
        this.isPreferred = isPreferred;
    }

    /**
     * sets the CreatedModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @param createdModifiedBy the process/person which created or modified this accession
     */

    public void setCreatedModifiedBy(String createdModifiedBy) {
        this.createdModifiedBy = createdModifiedBy;
    }

    /**
     * gets the accession id attribute
     * @assumes Nothing
     * @effects Nothing
     * @return the accession id
     */

    public String getAccID() { return accid; }

    /**
     * gets the prefix part of the accession id
     * @assumes Nothing
     * @effects Nothing
     * @return the prefix part of the accession id
     */

    public String getPrefixPart() { return prefixPart;}

    /**
     * Gets the numeric part of the accession id
     * @assumes Nothing
     * @effects Nothing
     * @return  the numeric part of the accession id
     */

    public Integer getNumericPart() { return numericPart;}
    /**
     * the logical db attribute
     * @assumes Nothing
     * @effects Nothing
     * @return the logical db (key) of the accession id
     */

    public String getLogicalDB() { return logicalDB;}

    /**
     * gets the MGI type attribute
     * @assumes Nothing
     * @effects Nothing
     * @return the MGIType (key) of the object_key
     */

    public Integer getMgiType() { return mgiType;}

    /**
     * get the IsPrivate attribute
     * @assumes Nothing
     * @effects Nothing
     * @return true if this a private accession id
     */

    public Boolean getIsPrivate() { return isPrivate;}

    /**
     * gets IsPreferred attribute
     * @assumes Nothing
     * @effects Nothing
     * @return true if this is a preferred accession id
     */


    public Boolean getIsPreferred() { return isPreferred;}

    /**
     * gets the CreatedModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @return the CreatedModifiedBy attribute
     */

    public String getCreatedModifiedBy() { return createdModifiedBy;}

    /**
     * Resets resets isPrivate to false; isPreferred to true and remaining
     *    objects to null
     * @assumes Nothing
     * @effects Nothing
     */
    public void reset() {
        accid = null;
        prefixPart = null;
        numericPart = null;
        logicalDB = null;
        mgiType = null;
        isPrivate = Boolean.FALSE;
        isPreferred = Boolean.TRUE;
        createdModifiedBy = null;
    }
}

//  $Log$
//  Revision 1.2  2004/06/30 17:25:34  sc
//  merging sc2 branch to trunk
//
//  Revision 1.1.4.1  2004/05/18 15:18:36  sc
//  class/method headers updated
//
//  Revision 1.1  2004/01/06 20:09:34  mbw
//  initial version imported from lib_java_seqloader
//
//  Revision 1.2  2003/12/20 16:25:18  sc
//  changes made from code review~
//
//  Revision 1.1  2003/12/08 18:38:49  sc
//  initial commit//

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