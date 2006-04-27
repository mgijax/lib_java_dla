package org.jax.mgi.shr.dla.input;

import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.shr.config.ConfigException;

/**
 * An abstract Coordinate Map Interpreter class that gets the configurable
 *     attributes needed by all coordinate map interpreters
 * @has
 *   <UL>
 *   <LI>A configurator
 *   <LI>map collection
 *   <LI>map collection abbreviation
 *   <LI>coordinate map type
 *   <LI>coordinate map unit
 *   <LI>coordinate map version
 *   <LI>logicalDB of the coordinate feature object key to create lookup to resolve
 *       the object key
 *   </UL>
 * @does
 *   <UL>
 *       gets configurable values needed by all coordinate map interpreters
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

abstract public class CoordinateInterpreter implements RecordDataInterpreter {
    // configurator
    protected CoordLoadCfg coordCfg;

    // map collection
    protected String mapCollection;
    protected String mapAbbreviation;

    // coordinate map
    protected String mapType;
    protected String mapUnits;
    protected String version;

    // coordinate feature logicalDB
    protected String logicalDB;


    /**
       * constructs a CoordinateInterpreter by getting Configuration values
       * that are common to all Coordinate Maps
       * @assumes Nothing
       * @effects Nothing
       * @throws ConfigException if error getting configured values
       */
    public CoordinateInterpreter() throws ConfigException {
        coordCfg = new CoordLoadCfg();
        mapCollection = coordCfg.getMapCollectionName();
        mapAbbreviation = coordCfg.getMapCollectionAbbrev();
        mapType = coordCfg.getMapType();
        mapUnits = coordCfg.getMapUnits();
        version = coordCfg.getMapVersion();
        logicalDB = coordCfg.getLogicalDB();
    }

}
