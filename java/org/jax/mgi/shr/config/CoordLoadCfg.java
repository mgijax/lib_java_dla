package org.jax.mgi.shr.config;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * An object that retrieves Configuration pararmeters for coordinate loaders
 * @has Nothing
 *   <UL>
 *   <LI> a configuration manager
 *   </UL>
 * @does
 *   <UL>
 *   <LI> provides methods to retrieve Configuration parameters that are
 *        specific to coordinate loads
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordLoadCfg extends Configurator {

    /**
    * Constructs a CoordLoadCfg object
    * @throws ConfigException if a configuration manager cannot be obtained
    */

    public CoordLoadCfg() throws ConfigException {
    }

    /**
     * Gets the map coordinate organism name for the load
     * @return the map coordinat organism name
     * @throws ConfigException if "COORD_ORGANISM" not found in configuration file
     */
    public String getMapOrganism() throws ConfigException {
        return getConfigString("COORD_ORGANISM");
    }

    /**
    * Gets the map coordinate version
    * @return Provider name
    * @throws ConfigException if "COORD_VERSION" not found in configuration file
    */
   public String getMapVersion() throws ConfigException {
       return getConfigString("COORD_VERSION", "");
   }

    /**
     * Gets the map coordinate collection name for the load
     * @return the map coordinate collection name
     * @throws ConfigException if "COORD_COLLECTION" not found in configuration file
     */
    public String getMapCollectionName() throws ConfigException {
        return getConfigString("COORD_COLLECTION_NAME");
    }

    /**
     * Gets the map collection abbreviation for the load
     * @return the map collection abbreviation
     * @throws ConfigException - doesn't really because it has a default value
     * of empty string
     */

    public String getMapCollectionAbbrev() throws ConfigException {
        return getConfigString("COORD_COLLECTION_ABBREV", "");
    }

    /**
     * Gets the coordinate map type
     * @return coordinate map type
     * @throws ConfigException if "COORD_TYPE" not found in configuration file
     */
    public String getMapType() throws ConfigException {
        return getConfigString("COORD_TYPE");
    }
    /**
   * Gets the coordinate map units
   * @return coordinate map units
   * @throws ConfigException if "COORD_UNITS" not found in configuration file
   */

    public String getMapUnits() throws ConfigException {
        return getConfigString("COORD_UNITS");
    }

    /**
    * Gets the coordinate map name
    * @return coordinate map name
    * @throws ConfigException - doesn't really because it has a default value
     * of empty string
    */

     public String getMapName() throws ConfigException {
         return getConfigString("COORD_NAME", "");
     }

     /**
     * Gets the coordinate map abbreviation
     * @return coordinate map abbreviation
     * @throws ConfigException - doesn't really because it has a default value
     * of empty string
     */

      public String getMapAbbrev() throws ConfigException {
        return getConfigString("COORD_ABBREV", "");
      }
      /**
      * Gets the coordinate feature MGIType name
      * @return  coordinate feature MGIType name
      * @throws ConfigException if "COORD_FEATURE_MGITYPE" not found in configuration file
      */

       public String getFeatureMGIType() throws ConfigException {
         return getConfigString("COORD_FEATURE_MGITYPE");
       }
    /**
     * Gets the Jobstream name
     * @return the Jobstream name
     * @throws ConfigException if "JOBSTREAM" not found in configuration file
     */
    public String getJobstreamName() throws ConfigException {
        return getConfigString("JOBSTREAM");
    }
    /**
     * get the coordinate interpreter object to use.
     * @return interpreter object.
     * @throws ConfigException thrown if "COORD_INTERPRETOR" not found in configuration
     *   file or interpreter object could not be created from the value
     */
    public Object getInterpreterClass() throws ConfigException {
        return getConfigObject("COORD_INTERPRETER");
    }

    /**
     * get the coordinate map processor object to use.
     * @return coordinate map processor object.
     * @throws ConfigException thrown if "COORD_PROCESSOR" not found in configuration
     * or processo object could not be created from the configuration value
     */
    public Object getMapProcessorClass() throws ConfigException {
        return getConfigObject("COORD_PROCESSOR");
    }

    /**
     * get the logicalDB name for the coordIds
     * @return logicalDB
     * @assumes nothing
     * @effects nothing
     * @throws ConfigException thrown if "COORD_LOGICALDB" not found in
     *    configuration file
     */
    public String getLogicalDB() throws ConfigException {
        return getConfigString("COORD_LOGICALDB");
    }
    /**
     * get the coordinate repeat ok value
     * @return the coordinate repeats ok value
     * @throws ConfigException thrown if "COORD_REPEATS_OK" not found in
     *    configuration file
     */
    public String getCoordRepeatsOk() throws ConfigException {
        return getConfigString("COORD_REPEATS_OK");
    }

   /**
    * get the coordinate repeat file name
    * @return the coordinate repeat file name
    * @throws ConfigException thrown if "COORD_REPEAT_FILE" not found in
    *    configuration file
    */
    public String getRepeatFileName() throws ConfigException {
        return getConfigString("COORD_REPEAT_FILE");
    }

}
