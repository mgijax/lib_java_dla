
package org.jax.mgi.shr.config;

import java.sql.Timestamp;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dla.fasta.FASTAFilter;

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

public class GeneIndexLoadCfg extends Configurator {

  /**
  * Constructs a sequence load configurator
  * @assumes Nothing
  * @effects Nothing
  * @throws ConfigException if a configuration manager cannot be obtained
  */

  public GeneIndexLoadCfg() throws ConfigException {
    super();
  }

  /**
   * Gets the Jobstream name
   * @assumes Nothing
   * @effects Nothing
   * @return the Jobstream name
   * @throws ConfigException if "SEQ_JOBSTREAM" not found in configuration file
   */
  public FASTAFilter getFilter() throws ConfigException {
    Object o = getConfigObjectNull("GNIDX_FILTER");
    if (o == null)
      return null;
    return (FASTAFilter)o;
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
