//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;
import java.sql.Timestamp;

/**
 * @is An object that represents raw values for SEQ_Sequence
 *
 * @has
 *   <UL>
 *   <LI> SEQ_Sequence column attributes
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>provides getters and setters for each column attribute
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

    /**
     * set the type attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the type attribut
     * @return Nothing
     * @throws Nothing
     */

    public void setType (String type) {this.type = type;}

    /**
     * set the length attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the length attribute
     * @return Nothing
     * @throws Nothing
     */

    public void setLength (String length) {this.length = length;}

    /**
     * set the description attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the description
     * @return Nothing
     * @throws Nothing
     */

    public void setDescription (String description) {
        this.description = description;
    }

    /**
     * set the version attribute
     * @assumes Nothing
     * @effects Nothing
     * @param  the version
     * @return Nothing
     * @throws Nothing
     */

    public void setVersion (String version) {this.version = version;}

    /**
     * set the division attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the division
     * @return Nothing
     * @throws Nothing
     */

    public void setDivision (String division) {this.division = division;}

    /**
     * set the virtual attribute
     * @assumes Nothing
     * @effects Nothing
     * @param true if this is a virtual sequence
     * @return Nothing
     * @throws Nothing
     */

    public void setVirtual (String virtual) {this.virtual = new Boolean(virtual);}

    /**
     * set the raw organisms attribute
     * @assumes Nothing
     * @effects Nothing
     * @param raw organisms
     * @return Nothing
     * @throws Nothing
     */

    public void setRawOrganisms (String rawOrganisms) {
        this.rawOrganisms = rawOrganisms;
    }

    /**
     * set the number of organism attribute
     * @assumes Nothing
     * @effects Nothing
     * @param number of non-mouse/human/rat organisms for this sequence
     * @return Nothing
     * @throws Nothing
     */

    public void setNumberOfOrganisms (int numberOfOrganisms) {
        this.numberOfOrganisms = numberOfOrganisms;
    }

    /**
     * set the CreatedModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the process/person that created or modified this sequence
     * @return Nothing
     * @throws Nothing
     */

    public void setCreatedModifiedBy (String createdModifiedBy) {
        this.createdModifiedBy = createdModifiedBy;
    }

    /**
     * set the sequence record date attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the sequence record date
     * @return Nothing
     * @throws Nothing
     */

    public void setSeqRecDate (Timestamp seqRecDate) {
        this.seqRecDate = seqRecDate;
    }

    /**
     * set the sequence date attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the sequence date
     * @return Nothing
     * @throws Nothing
     */

    public void setSeqDate (Timestamp seqDate) {this.seqDate = seqDate;}

    /**
     * set the raw library attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the raw library
     * @return Nothing
     * @throws Nothing
     */

    public void setLibrary (String library) {this.library = library;}

    /**
     * set the raw strain attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the raw strain
     * @return Nothing
     * @throws Nothing
     */

    public void setStrain (String strain) {this.strain = strain;}

    /**
     * set the raw tissue attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the raw tissue
     * @return Nothing
     * @throws Nothing
     */

    public void setTissue (String tissue) {this.tissue = tissue;}

    /**
     * set the raw age attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the raw age
     * @return Nothing
     * @throws Nothing
     */

    public void setAge (String age) {this.age = age;}

    /**
     * set the raw sex attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the raw sex
     * @return Nothing
     * @throws Nothing
     */

    public void setSex (String sex) {this.sex = sex;}

    /**
     * set the raw cell line attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the raw cell line
     * @return Nothing
     * @throws Nothing
     */

    public void setCellLine (String cellLine) {this.cellLine = cellLine;}

    /**
     * set the sequence provider attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the sequence provider
     * @return Nothing
     * @throws Nothing
     */

    public void setProvider (String provider) {this.provider = provider;}

    /**
     * set the sequence quality attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the sequence quality
     * @return Nothing
     * @throws Nothing
     */

    public void setQuality (String quality) {this.quality = quality;}

    /**
     * set the sequence status attribute
     * @assumes Nothing
     * @effects Nothing
     * @param the sequence status
     * @return Nothing
     * @throws Nothing
     */

    public void setStatus (String status) {this.status = status;}

    /**
     * get the sequence type
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence type
     * @throws Nothing
     */

    public String getType () {return type;}

    /**
     * get the sequence length
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence length
     * @throws Nothing
     */

    public String getLength () {return length;}

    /**
     * get the sequence description
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence description
     * @throws Nothing
     */

    public String getDescription () {return description;}

    /**
     * get the sequence version
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence version
     * @throws Nothing
     */

    public String getVersion () {return version;}

    /**
     * get the sequence division
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence division
     * @throws Nothing
     */

    public String getDivision () {return division;}

    /**
     * get the virtual-ness of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return true if this is a virtual sequence
     * @throws Nothing
     */

    public Boolean getVirtual () {return virtual;}

    /**
     * get the raw organisms for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return raw organisms for the sequence
     * @throws Nothing
     */

    public String getRawOrganisms () {return rawOrganisms;}

    /**
     * get the number of non-human/mouse/rat organisms for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the number of non-human/mouse/rat organisms for the sequence
     * @throws Nothing
     */

    public int getNumberOfOrganisms () {return numberOfOrganisms;}

    /**
     * get the CreatedByModifiedBy attribute
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the process/person that created/modified the sequence
     * @throws Nothing
     */

    public String getCreatedModifiedBy () {return createdModifiedBy;}

    /**
     * get the sequence record date
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence record date
     * @throws Nothing
     */

    public Timestamp getSeqRecDate () { return seqRecDate;}

    /**
     * get the sequence date
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence date
     * @throws Nothing
     */

    public Timestamp getSeqDate () {

       return seqDate;
    }

    /**
     * get the raw library for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the raw library for the sequence
     * @throws Nothing
     */

    public String getLibrary () {return library;}

    /**
     * get the rawStrain for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the rawStrain for the sequence
     * @throws Nothing
     */

    public String getStrain () {return strain;}

    /**
     * get the raw tissue for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the raw tissue for the sequence
     * @throws Nothing
     */

    public String getTissue () {return tissue;}

    /**
     * get the raw age for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the raw age for the sequence
     * @throws Nothing
     */

    public String getAge () {return age;}

    /**
     * get the raw sex for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the raw sex for the sequence
     * @throws Nothing
     */

    public String getSex () {return sex;}

    /**
     * get the raw cell line for the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the raw cell line for the sequence
     * @throws Nothing
     */

    public String getCellLine () {return cellLine;}

    /**
     * get the sequence provider
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence provider
     * @throws Nothing
     */

    public String getProvider () {return provider;}

    /**
     * get the sequence quality
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence quality
     * @throws Nothing
     */

    public String getQuality () {return quality;}

    /**
     * get the sequence status
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the sequence status
     * @throws Nothing
     */

     public String getStatus () {return status;}

    /**
     * Resets object values to null; number of organisms to 0
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Nothing
     * @throws Nothing
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
    }
}

//  $Log$
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
