// $Header$                                                                                      //  $Header$
//  $Name$

package org.jax.mgi.shr.config;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * @is an object that retrieves Configuration pararmeters for sequence loaders
 * @has Nothing
 *   <UL>
 *   <LI> a configuration manager
 *   </UL>
 * @does
 *   <UL>
 *   <LI> provides methods to retrieve Configuration parameters that are
 *        specific to sequence loads
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */


public class OrganismCheckerCfg extends Configurator {
    /**
     * Constructs a sequence load configurator
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @throws ConfigException if a configuration manager cannot be obtained
     */

    public OrganismCheckerCfg() throws ConfigException {

    }

    /**
     * Gets whether to load mouse sequence or not
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Boolean true if we are loading mouse
     * @throws ConfigException if "LOAD_MOUSE" not found in configuration file
     */

    public Boolean getMouse() throws ConfigException {
        return new Boolean(getConfigString("LOAD_MOUSE"));
    }

    /**
     * Gets whether to load rat sequence or not
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Boolean true if we are loading rat
     * @throws ConfigException if "LOAD_RAT" not found in configuration file
     */

    public Boolean getRat() throws ConfigException {
        return new Boolean(getConfigString("LOAD_RAT"));
    }
    /**
     * Gets whether to load human sequence or not
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Boolean true if we are loading human
     * @throws ConfigException if "LOAD_HUMAN" not found in configuration file
     */

    public Boolean getHuman() throws ConfigException {
        return new Boolean(getConfigString("LOAD_HUMAN"));
    }
}

//  $Log$
//  Revision 1.1  2003/12/19 12:55:46  sc
//  initial commit
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
* Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/