//  $Header
//  $Name

package org.jax.mgi.shr.dla.input;

import org.jax.mgi.dbs.mgd.loads.Coord.*;

/**
 * An object that represents raw values needed to create MAP_Coord_Collection,
 * MAP_Coordinate, and MAP_Coordinate_Feature databas objects
 * @has
 *   <UL>
 *   <LI> map Collection name
 *   <LI> CoordMapRawAttributes object
 *   <LI>CoordMapFeatureRawAttributes object
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>provides getters and setters for each attribute
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordinateInput {
    private String mapCollectionName;
    private CoordMapRawAttributes mapRawAttr;
    private CoordMapFeatureRawAttributes featureRawAttr;

    /**
     * set the coordinate map collection name attribute
     * @param name the name of the coordinate map collection
     */
    public void setMapCollectionName(String name) {
        this.mapCollectionName = name;
    }

    /**
     * get the Collection Name
     * @return the coordinate map collection name
     */
    public String getMapCollectionName() {
        return mapCollectionName;
    }

    /**
     * set the raw coordinate map object
     * @param mapRawAttr a CoordMapRawAttributes object representing a
     * Coordinate Map
     */
    public void setCoordMapRawAttributes(CoordMapRawAttributes mapRawAttr) {
        this.mapRawAttr = mapRawAttr;
    }

    /**
     * get raw coordinate map object
     * @return the raw map coordinate object
     */
    public CoordMapRawAttributes getCoordMapRawAttributes() {
        return mapRawAttr;
    }

    /**
     * set the raw coordinate map feature object
     * @param featureRawAttr a CoordMapFeatureRawAttributes object representing a
     * Coordinate Map Feature
     */
    public void setCoordMapFeatureRawAttributes(CoordMapFeatureRawAttributes
                                                featureRawAttr) {
        this.featureRawAttr = featureRawAttr;
    }

    /**
     * get raw coordinate map feature object
     * @return the raw coordinate map feature object
     */
    public CoordMapFeatureRawAttributes getCoordMapFeatureRawAttributes() {
        return featureRawAttr;
    }

    /**
     *  sets all attributes to null
     */

    public void reset() {
        mapCollectionName = null;
        mapRawAttr = null;
        featureRawAttr = null;
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
