package org.jax.mgi.dbs.mgd.loads.Coord;

/**
 * An object that represents raw values needed to create a MAP_Coord_Feature
 * database object
 * @has
 *   <UL>
 *   <LI> raw attributes needed to create a MAP_Coord_Feature database object
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>provides getters and setters for each attribute
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordMapFeatureRawAttributes {

    private String record;
    private String coordMap;
    private Integer MGIType;
    private String objectId;
    private String logicalDB;
    private String startBP;
    private String endBP;
    private String strand;

    /**
     * sets the record attribute
     * @param rcd the record
     */

    public void setRecord (String rcd) {
        record = rcd;
    }

    /**
    * gets the record attribute
    * @return the record
    */

    public String getRecord () {
        return record;
    }

    /**
     * sets the Object id attribute
     * @param id String to be used to determine Feature object key
     */

    public void setObjectId(String id) {
        objectId = id;
    }

    /**
    * gets the object id attribute
    * @return String to be used to determine Feature object key
    */

    public String getObjectId() {
        return objectId;
    }

    /**
     * sets the start coordinate attribute
     * @param start the start coordinate
     */

    public void setStartCoord(String start) {
        startBP = start;
    }

    /**
    * gets the start  attribute
    * @return the start coordinate
    */

    public String getStartCoord() {
        return startBP;
    }

    /**
     * sets the end coordinate attribute
     * @param end the end coordinate
     */

    public void setEndCoord(String end) {
        endBP = end;
    }

    /**
    * gets the end coordinate attribute
    * @return the end coordinate
    */

    public String getEndCoord() {
        return endBP;
    }

    /**
     * sets the strand  attribute
     * @param str the strand
     */

    public void setStrand(String str) {
        strand = str;
    }

    /**
    * gets the stran attribute
    * @return the strand
    */

    public String getStrand() {
        return strand;
    }

    /**
    * resets instance variables
    */

    public void reset() {
       coordMap = null;
       MGIType = null;
       objectId = null;
       logicalDB = null;
       startBP = null;
       endBP =  null;
       strand = null;

    }
}
