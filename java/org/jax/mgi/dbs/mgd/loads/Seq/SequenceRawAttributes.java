//  $Header$
//  $Name$

package org.jax.mgi.dbs.mgd.loads.Seq;
import java.sql.Timestamp;

/**
 * An object that represents raw values needed to create a SEQ_Sequence object
 * @has
 *   <UL>
 *   <LI> raw attributes needed to create a SEQ_Sequence object
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>provides getters and setters for each attribute
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SequenceRawAttributes {

    private String type = null;
    private String length = null;
    private String description = null;
    private String version = null;
    private String division = null;
    private Boolean virtual = null;
    private String rawOrganisms = null;
    private int numberOfOrganisms = 0;
    private String createdModifiedBy = null;
    private Timestamp seqRecDate = null;
    private Timestamp seqDate = null;
    private String library = null;
    private String strain = null;
    private String tissue = null;
    private String age = null;
    private String sex = null;
    private String cellLine = null;
    private String provider = null;
    private String quality = null;
    private String status = null;
    private String seqRecord = null;

    // added 07/06/04 to support Genbank TPA sequences. General purpose
    // misc attribute
    private String misc =  null;

    /**
     * set the type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param type the type attribut
     */

    public void setType (String type) {this.type = type;}

    /**
     * set the length attribute
     * @assumes Nothing
     * @effects Nothing
     * @param length the length attribute
     */

    public void setLength (String length) {this.length = length;}

    /**
     * set the description attribute
     * @assumes Nothing
     * @effects Nothing
     * @param description the description
     */

    public void setDescription (String description) {
        this.description = description;
    }

    /**
     * set the version attribute
     * @assumes Nothing
     * @effects Nothing
     * @param  version the version
     */

    public void setVersion (String version) {this.version = version;}

    /**
     * set the division attribute
     * @assumes Nothing
     * @effects Nothing
     * @param division the division
     */

    public void setDivision (String division) {this.division = division;}

    /**
     * set the virtual attribute
     * @assumes Nothing
     * @effects Nothing
     * @param virtual true if this is a virtual sequence
     */

    public void setVirtual (String virtual) {this.virtual = new Boolean(virtual);}

    /**
     * set the raw organisms attribute
     * @assumes Nothing
     * @effects Nothing
     * @param rawOrganisms raw organisms
     */

    public void setRawOrganisms (String rawOrganisms) {
        this.rawOrganisms = rawOrganisms;
    }

    /**
     * set the number of organism attribute
     * @assumes Nothing
     * @effects Nothing
     * @param numberOfOrganisms number of non-mouse/human/rat organisms for this sequence
     */

    public void setNumberOfOrganisms (int numberOfOrganisms) {
        this.numberOfOrganisms = numberOfOrganisms;
    }

    /**
     * set the CreatedModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @param createdModifiedBy the process/person that created or modified
     * this sequence
     */

    public void setCreatedModifiedBy (String createdModifiedBy) {
        this.createdModifiedBy = createdModifiedBy;
    }

    /**
     * set the sequence record date attribute
     * @assumes Nothing
     * @effects Nothing
     * @param seqRecDate the sequence record date
     */

    public void setSeqRecDate (Timestamp seqRecDate) {
        this.seqRecDate = seqRecDate;
    }

    /**
     * set the sequence date attribute
     * @assumes Nothing
     * @effects Nothing
     * @param seqDate the sequence date
     */

    public void setSeqDate (Timestamp seqDate) {this.seqDate = seqDate;}

    /**
     * set the raw library attribute
     * @assumes Nothing
     * @effects Nothing
     * @param library the raw library
     */

    public void setLibrary (String library) {this.library = library;}

    /**
     * set the raw strain attribute
     * @assumes Nothing
     * @effects Nothing
     * @param strain the raw strain
     */

    public void setStrain (String strain) {this.strain = strain;}

    /**
     * set the raw tissue attribute
     * @assumes Nothing
     * @effects Nothing
     * @param tissue the raw tissue
     */

    public void setTissue (String tissue) {this.tissue = tissue;}

    /**
     * set the raw age attribute
     * @assumes Nothing
     * @effects Nothing
     * @param age the raw age
     */

    public void setAge (String age) {this.age = age;}

    /**
     * set the raw sex attribute
     * @assumes Nothing
     * @effects Nothing
     * @param sex the raw sex
     */

    public void setSex (String sex) {this.sex = sex;}

    /**
     * set the raw cell line attribute
     * @assumes Nothing
     * @effects Nothing
     * @param cellLine the raw cell line
     */

    public void setCellLine (String cellLine) {this.cellLine = cellLine;}

    /**
     * set the sequence provider attribute
     * @assumes Nothing
     * @effects Nothing
     * @param provider the sequence provider
     */

    public void setProvider (String provider) {this.provider = provider;}

    /**
     * set the sequence quality attribute
     * @assumes Nothing
     * @effects Nothing
     * @param quality the sequence quality
     */

    public void setQuality (String quality) {this.quality = quality;}

    /**
     * set the sequence status attribute
     * @assumes Nothing
     * @effects Nothing
     * @param status the sequence status
     */

    public void setStatus (String status) {this.status = status;}

    /**
    * set the miscellaneous attribute
    * @assumes Nothing
    * @effects Nothing
    * @param misc the misc attribute
    */

    public void setMisc (String misc) {this.misc = misc;}

   /**
     * set the sequence record attribute
     * @assumes Nothing
     * @effects Nothing
     * @param seqRecord the sequence record
     */

    public void setRecord (String seqRecord) {this.seqRecord = seqRecord;}

    /**
     * get the sequence type
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence type
     */

    public String getType () {return type;}

    /**
     * get the sequence length
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence length
     */

    public String getLength () {return length;}

    /**
     * get the sequence description
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence description
     */

    public String getDescription () {return description;}

    /**
     * get the sequence version
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence version
     */

    public String getVersion () {return version;}

    /**
     * get the sequence division
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence division
     */

    public String getDivision () {return division;}

    /**
     * get the virtual-ness of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return true if this is a virtual sequence
     */

    public Boolean getVirtual () {return virtual;}

    /**
     * get the raw organisms for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return raw organisms for the sequence
     */

    public String getRawOrganisms () {return rawOrganisms;}

    /**
     * get the number of non-human/mouse/rat organisms for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return the number of non-human/mouse/rat organisms for the sequence
     */

    public int getNumberOfOrganisms () {return numberOfOrganisms;}

    /**
     * get the CreatedByModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @return the process/person that created/modified the sequence
     */

    public String getCreatedModifiedBy () {return createdModifiedBy;}

    /**
     * get the sequence record date
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence record date
     */

    public Timestamp getSeqRecDate () { return seqRecDate;}

    /**
     * get the sequence date
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence date
     */

    public Timestamp getSeqDate () {

       return seqDate;
    }

    /**
     * get the raw library for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return the raw library for the sequence
     */

    public String getLibrary () {return library;}

    /**
     * get the rawStrain for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return the rawStrain for the sequence
     */

    public String getStrain () {return strain;}

    /**
     * get the raw tissue for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return the raw tissue for the sequence
     */

    public String getTissue () {return tissue;}

    /**
     * get the raw age for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return the raw age for the sequence
     */

    public String getAge () {return age;}

    /**
     * get the raw sex for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return the raw sex for the sequence
     */

    public String getSex () {return sex;}

    /**
     * get the raw cell line for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return the raw cell line for the sequence
     */

    public String getCellLine () {return cellLine;}

    /**
     * get the sequence provider
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence provider
     */

    public String getProvider () {return provider;}

    /**
     * get the sequence quality
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence quality
     */

    public String getQuality () {return quality;}

    /**
     * get the sequence status
     * @assumes Nothing
     * @effects Nothing
     * @return the sequence status
     */

     public String getStatus () {return status;}

     /**
      * get the miscellaneous attribute
      * @assumes Nothing
      * @effects Nothing
      * @return the misc attribute
      */

      public String getMisc () {return misc;}

     /**
      * get the sequence record attribute
      * @assumes Nothing
      * @effects Nothing
      * @return the sequence record
      */

     public String getRecord () {return seqRecord;}

    /**
     * Resets object values to null; number of organisms to 0
     * @assumes Nothing
     * @effects Nothing
     */

    public void reset() {
        type = null;
        length = null;
        description = null;
        version = null;
        division = null;
        virtual = null;
        rawOrganisms = null;
        numberOfOrganisms = 0;
        createdModifiedBy = null;
        seqRecDate = null;
        seqDate = null;
        library = null;
        strain = null;
        tissue = null;
        age = null;
        sex = null;
        cellLine = null;
        provider = null;
        quality = null;
        status = null;
        misc = null;
    }
}

//  $Log$
//  Revision 1.2.8.1  2005/01/24 19:07:42  sc
//  javadocs only
//
//  Revision 1.2  2004/12/07 20:09:46  mbw
//  merged tr6047 onto the trunk
//
//  Revision 1.1.2.1  2004/11/05 16:10:16  mbw
//  classes were renamed and reloacated as part of large refactoring effort (see tr6047)
//
//  Revision 1.4  2004/07/12 17:14:12  sc
//  added misc attribute, setter, and getter as general purpose field to hold whatever
//
//  Revision 1.3  2004/06/30 19:36:59  mbw
//  javadocs only
//
//  Revision 1.2  2004/02/02 19:45:17  sc
//  development since last tag
//
//  Revision 1.1  2004/01/06 20:09:48  mbw
//  initial version imported from lib_java_seqloader
//
//  Revision 1.2  2003/12/20 16:25:23  sc
//  changes made from code review~
//
//  Revision 1.1  2003/12/08 18:40:46  sc
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
