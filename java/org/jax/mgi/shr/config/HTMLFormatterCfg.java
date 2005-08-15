// $Header$
// $Name$

package org.jax.mgi.shr.config;

import java.io.File;
import org.jax.mgi.shr.config.ConfigException;

/**
 * @is an object for configuring a DataLoadLogger.
 * @has a set of DataLoadLogger configuration parameters and a reference
 * to a ConfigurationManager
 * @does provides methods for getting and setting configuration paramaters
 * for a DataLoadLogger.
 * <p>Description: This class obtains a reference to the ConfiguratRionManager
 * singleton class which holds all the system configuration parameters read in
 * from the system properties and the configuration file. It then provides
 * get methods for looking up parameter values that pertain to a
 * DataLoadLogger and provides set methods for overriding these values.
 * @company Jackson Laboratory</p>
 * @author M. Walker
 * @version 1.0
 */

public class HTMLFormatterCfg extends Configurator {

  private String DEFAULT_WEBURL = "shire.informatics.jax.org";


  /**
   * default constructor which obtains a reference to the
   * ConfigurationManager singleton class.
   * @throws ConfigException
   */
    public HTMLFormatterCfg() throws ConfigException {
      super();
    }
    /**
     * get the name of the web server to which all MGI database calls are made
     * @return the name of the webserver
     */
    public String getWebServerUrl()
    {
        return getConfigString("FMT_WEB_SERVER_URL", DEFAULT_WEBURL);
    }


}

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
* Copyright \251 1996, 1999, 2002 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
