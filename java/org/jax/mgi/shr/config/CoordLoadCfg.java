package org.jax.mgi.shr.config;

// $Header
//  $Name

import java.sql.Timestamp;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * @is an object that retrieves Configuration pararmeters for coordinate loaders
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
    * @assumes Nothing
    * @effects Nothing
    * @param None
    * @throws ConfigException if a configuration manager cannot be obtained
    */

    public CoordLoadCfg() throws ConfigException {
    }

    /**
     * Gets the map organism name for the load
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the map organism name
     * @throws ConfigException if "COORD_ORGANISM" not found in configuration file
     */
    public String getMapOrganism() throws ConfigException {
        return getConfigString("COORD_ORGANISM");
    }

    /**
    * Gets the coordinate map version
    * @assumes Nothing
    * @effects Nothing
    * @param None
    * @return Provider name
    * @throws ConfigException if "COORD_VERSION" not found in configuration file
    */
   public String getMapVersion() throws ConfigException {
       return getConfigString("COORD_VERSION", "");
   }

    /**
     * Gets the map collection name for the load
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the map collection name
     * @throws ConfigException if "COORD_COLLECTION" not found in configuration file
     */
    public String getMapCollectionName() throws ConfigException {
        return getConfigString("COORD_COLLECTION_NAME");
    }

    /**
     * Gets the map collection abbreviation for the load
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the map collection abbreviation
     * @throws ConfigException if "COORD_COLLECTION_ABBREV" not found in configuration file
     */

    public String getMapCollectionAbbrev() throws ConfigException {
        return getConfigString("COORD_COLLECTION_ABBREV", "");
    }

    /**
     * Gets the coordinate map type
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return coordinate map type
     * @throws ConfigException if "COORD_TYPE" not found in configuration file
     */
    public String getMapType() throws ConfigException {
        return getConfigString("COORD_TYPE");
    }
    /**
   * Gets the coordinate map units
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return coordinate map units
   * @throws ConfigException if "COORD_UNITS" not found in configuration file
   */

    public String getMapUnits() throws ConfigException {
        return getConfigString("COORD_UNITS");
    }

    /**
    * Gets the coordinate map name
    * @assumes Nothing
    * @effects Nothing
    * @param None
    * @return coordinate map name
    * @throws ConfigException if "COORD_NAME" not found in configuration file
    */

     public String getMapName() throws ConfigException {
         return getConfigString("COORD_NAME", "");
     }

     /**
     * Gets the coordinate map abbreviation
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return coordinate map abbreviation
     * @throws ConfigException if "COORD_ABBREV" not found in configuration file
     */

      public String getMapAbbrev() throws ConfigException {
        return getConfigString("COORD_ABBREV", "");
      }
      /**
      * Gets the coordinate feature MGIType name
      * @assumes Nothing
      * @effects Nothing
      * @param None
      * @return  coordinate feature MGIType name
      * @throws ConfigException if "COORD_FEATURE_MGITYPE" not found in configuration file
      */

       public String getFeatureMGIType() throws ConfigException {
         return getConfigString("COORD_FEATURE_MGITYPE");
       }

   /**
   * Gets the coordinate map sequence retrieval parameter
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return the coordinate map sequence retrieval parameter
   * @throws ConfigException if "COORD_SEQ_RETR_PARAM" not found in configuration file
   */
  public String getSeqRetrievalParam() throws ConfigException {
      return getConfigString("COORD_SEQ_RETR_PARAM", "");
  }

  /**
   * Gets the Jobstream name
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return the Jobstream name
   * @throws ConfigException if "SEQ_JOBSTREAM" not found in configuration file
   */
  public String getJobstreamName() throws ConfigException {
    return getConfigString("JOBSTREAM");
  }
  /**
   * get the interpreter to use.
   * @return interpreter object.
   * @assumes nothing
   * @effects nothing
   * @throws ConfigException thrown if interpreter object could not be created
   * from the configuration
   */
  public Object getInterpreterClass() throws ConfigException {
    return getConfigObject("COORD_INTERPRETER");
  }

  /**
   * get the map processor to use.
   * @return map processor object.
   * @assumes nothing
   * @effects nothing
   * @throws ConfigException thrown if resolver object could not be created
   * from the configuration
   */
  public Object getMapProcessorClass() throws ConfigException {
    return getConfigObject("COORD_PROCESSOR");
  }

  /**
   * get the map feature resolver to use
   * @return map feature resolver object.
   * @assumes nothing
   * @effects nothing
   * @throws ConfigException thrown if error retrieving value
   * from the configuration
   */
  public Object getMapFeatureResolverClass() throws ConfigException {
    return getConfigObject("COORD_FEATURE_RESOLVER");
  }

  /**
    * get the logicalDB name for the coordIds
    * @return logicalDB
    * @assumes nothing
    * @effects nothing
    * @throws ConfigException thrown if error reading config file
    * from the configuration
    */
   public String getLogicalDB() throws ConfigException {
     return getConfigString("COORD_LOGICALDB");
   }
}


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
