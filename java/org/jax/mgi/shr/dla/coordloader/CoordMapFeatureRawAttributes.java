package org.jax.mgi.shr.dla.coordloader;

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

    private String coordMap;
    private Integer MGIType;
    private String objectId;
    // logicalDB of objectId
    private String logicalDB;
    private String startBP;
    private String endBP;
    private String strand;

    public void setMap (String map) {
        coordMap = map;
    }
    public String getMap () {
        return coordMap;
    }
    public void setMGIType(Integer type) {
        MGIType = type;
    }
    public Integer getMGIType() {
        return MGIType;
    }
    public void setObjectId(String id) {
        objectId = id;
    }
    public String getObjectId() {
        return objectId;
    }
    public void setLogicalDB(String lDB) {
        logicalDB = lDB;
    }
    public String getLogicalDB() {
        return logicalDB;
    }
    public void setStartCoord(String start) {
        startBP = start;
    }
    public String getStartCoord() {
        return startBP;
    }
    public void setEndCoord(String end) {
        endBP = end;
    }
    public String getEndCoord() {
        return endBP;
    }

    public void setStrand(String str) {
        strand = str;
    }

    public String getStrand() {
        return strand;
    }
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
