package org.jax.mgi.shr.dla.coordloader;

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
    // comments refer to how the MGS Assembly loads use these attributes
    private String mapCollection;  // interpreter sets from file

    // chromosome for NCBI and Ensembl loads
    // a resolver uses this value to get the coordinate map object key
    // if object key is null, then mgiType_key is null
    private String coordMapObject; // interpreter sets from file
    private Integer mapMGIType; //interpreter sets as constant

    private String mapType; // interpreter sets from Configuratin
    private String unitType;  // interpreter sets from Configuration
    private String length; // interpreter sets from Configuration
    private String sequenceNum; // not used; determined by resolver
    private String mapName;  // null
    private String mapAbbreviation; // null
    private String mapVersion; // interpreter sets from Configuration
    private String seqRetrievalParam; // interpreter sets from Configuration

    public void setMapCollection(String collection) {
        mapCollection = collection;
    }
    public String getMapCollection() {
        return mapCollection;
    }
    public void setCoordMapObject(String map) {
        coordMapObject = map;
    }
    public String getCoordMapObject() {
        return coordMapObject;
    }
    public void setMapMGIType(Integer mgiType) {
        mapMGIType = mgiType;
    }
    public Integer getMapMGIType() {
        return mapMGIType;
    }
    public void setMapType(String mType) {
        mapType = mType;
    }
    public String getMapType() {
        return mapType;
    }
    public void setUnitType(String uType) {
        unitType = uType;
    }
    public String getUnitType() {
        return unitType;
    }
    public void setLength(String len) {
        length = len;
    }
    public String getLength() {
        return length;
    }
    public void setSequenceNum(String seqNum) {
        sequenceNum = seqNum;
    }
    public String getSequenceNum() {
        return sequenceNum;
    }
    public void setMapName(String name) {
        mapName = name;
    }
    public String getMapName() {
        return mapName;
    }
    public void setMapAbbrev(String abbrev) {
        mapAbbreviation = abbrev;
    }
    public String getMapAbbrev() {
        return mapAbbreviation;
    }
    public void setMapVersion(String version) {
        mapVersion = version;
    }
    public String getMapVersion() {
        return mapVersion;
    }
    public void setSeqRetParam(String param) {
        seqRetrievalParam = param;
    }
    public String getSeqRetParam() {
        return seqRetrievalParam;
    }
     public void reset() {
         mapCollection = null;
         coordMapObject = null;
         mapMGIType =  null;
         mapType = null;
         unitType = null;
         length = null;
         sequenceNum = null;
         mapName = null;
         mapAbbreviation = null;
         mapVersion = null;
         seqRetrievalParam = null;
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
