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
