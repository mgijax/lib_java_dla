//  $Header
//  $Name

package org.jax.mgi.dbs.mgd.loads.Coord;

/**
 * An object that represents raw values needed to create a MAP_Coordinate object
 * @has
 *   <UL>
 *   <LI> raw attributes needed to create a MAP_Coordinate database object
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>provides getters and setters for each attribute
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordMapRawAttributes {
    // The map collection to which this map belongs
    private String mapCollection;

    // a resolver uses this value to get the coordinate map object key
    // e.g. chromosome name for NCBI and Ensembl loads
    // note if object key is null, then mgiType key is null
    private String coordMapObject;

    // the mgi type of the map object
    private Integer mapMGITypeKey;

    // the map type e.g. 'assembly'
    private String mapType;

    // the unit type e.g. 'base pair'
    private String unitType;

    // the length of the map e.g. length of the chromosome
    private String length;

    // the ordering of this map within the collection
    private String sequenceNum;

    // the name of the map
    private String mapName;

    // abbrev for the map name
    private String mapAbbreviation;

    // the version of the map
    private String mapVersion;

    /**
      * sets the map collection name attribute
      * @param collection name of the map collection
      */

    public void setMapCollection(String collection) {
        mapCollection = collection;
    }

    /**
     * gets the map collection name attribute
     * @returns the map collection name
     */

    public String getMapCollection() {
        return mapCollection;
    }
    /**
      * sets the coordinate map object attribute
      * @param object String that can be resolved to an MGI database object key
      * e.g. Chromosome number
      */

    public void setCoordMapObject(String object) {
        coordMapObject = object;
    }

    /**
     * gets the coordinate map object attribute
     * @returns the coordinate map object
     */

    public String getCoordMapObject() {
        return coordMapObject;
    }

    /**
      * sets the coordinate map object MGI type key attribute
      * @param mgiType the MGI type key of 'coordMapObject'
      */

    public void setMapMGITypeKey(Integer mgiTypeKey) {
        mapMGITypeKey = mgiTypeKey;
    }

    /**
     * gets the MGI type key attribute
     * @returns the MGI type key of 'coordMapObject'
     */

    public Integer getMapMGITypeKey() {
        return mapMGITypeKey;
    }

    /**
      * sets the coordinate map type attribute
      * @param mType the coordinate map type
      */

    public void setMapType(String mType) {
        mapType = mType;
    }

    /**
     * gets the coordinate map type attribute
     * @returns the coordinate map type
     */

    public String getMapType() {
        return mapType;
    }
    /**
      * sets the map unit type attribute
      * @param uType the map unit type
      */

    public void setUnitType(String uType) {
        unitType = uType;
    }

    /**
     * gets the map unit type attribute
     * @returns the map unit type
     */

    public String getUnitType() {
        return unitType;
    }
    /**
      * sets the coordinate map length attribute
      * @param len the length of the coordinate map
      */

    public void setLength(String len) {
        length = len;
    }

    /**
     * gets the coordinate map length attribute
     * @returns the length of the coordinate map
     */

    public String getLength() {
        return length;
    }
    /**
      * sets the sequence number attribute
      * @param seqNum the order of the coordinate map in its collection
      */

    public void setSequenceNum(String seqNum) {
        sequenceNum = seqNum;
    }

    /**
     * gets the sequence number attribute
     * @returns order of the coordinate map in its collection
     */

    public String getSequenceNum() {
        return sequenceNum;
    }

    /**
      * sets the coordinate map name attribute
      * @param name name of the coordinate map
      */

    public void setMapName(String name) {
        mapName = name;
    }

    /**
     * gets the coordinate map name attribute
     * @returns the coordinate map name
     */

    public String getMapName() {
        return mapName;
    }

    /**
      * sets the coordinate map name abbreviation attribute
      * @param abbrev the coordinate map name abbreviation
      */

    public void setMapAbbrev(String abbrev) {
        mapAbbreviation = abbrev;
    }

    /**
     * gets the coordinate map name abbreviation
     * @returns the coordinate map name abbreviation
     */

    public String getMapAbbrev() {
        return mapAbbreviation;
    }

    /**
      * sets the coordinate map version ttribute
      * @param version the version of the coordinate map
      */

    public void setMapVersion(String version) {
        mapVersion = version;
    }

    /**
     * gets the coordinate map version
     * @returns the coordinate map version
     */

    public String getMapVersion() {
        return mapVersion;
    }

    /**
     * resets instance variables
     */

     public void reset() {
         mapCollection = null;
         coordMapObject = null;
         mapMGITypeKey =  null;
         mapType = null;
         unitType = null;
         length = null;
         sequenceNum = null;
         mapName = null;
         mapAbbreviation = null;
         mapVersion = null;
     }

}

// $Log
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
