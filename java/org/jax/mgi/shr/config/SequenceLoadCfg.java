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

public class SequenceLoadCfg extends Configurator {

    /**
    * Constructs a sequence load configurator
    * @assumes Nothing
    * @effects Nothing
    * @param None
    * @throws ConfigException if a configuration manager cannot be obtained
    */

    public SequenceLoadCfg() throws ConfigException {
    }

    /**
     * Gets the load mode e.g. 'incremental' or 'delete_reload'
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Theload mode
     * @throws ConfigException if "LOAD_MODE" not found in configuration file
     */
    public String getLoadMode() throws ConfigException {
        return getConfigString("LOAD_MODE");
    }

    /**
     * Gets the "Virtualness" of the sequences in this load
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the String true or false
     * @throws ConfigException if "VIRTUAL" not found in configuration file
     */

    public String getVirtual() throws ConfigException {
        return getConfigString("VIRTUAL");
    }

    /**
     * Gets the jobstream name for this load
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return The jobstream name
     * @throws ConfigException if "JOBSTREAM" not found in configuration file
     */
    public String getJobStreamName() throws ConfigException {
        return getConfigString("JOBSTREAM");
    }

    /**
     * Gets the MGIType name for sequence table
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return MGIType for Sequence name
     * @throws ConfigException if "SEQ_MGITYPE" not found in configuration file
     */
    public String getSeqMGIType() throws ConfigException {
        return getConfigString("SEQ_MGITYPE");
    }

    /**
     * Gets the logicalDB name for this load
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return logicalDB name
     * @throws ConfigException if "LOGICALDB" not found in configuration file
     */
    public String getLogicalDB() throws ConfigException {
        return getConfigString("LOGICALDB");
    }
    /**
   * Gets the reference association type for this load
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return reference assoc typ name
   * @throws ConfigException if "REF_ASSOC_TYPE" not found in configuration file
   */

    public String getReferenceAssocType() throws ConfigException {
        return getConfigString("REF_ASSOC_TYPE");
    }

    /**
    * Gets the provider name for this load
    * @assumes Nothing
    * @effects Nothing
    * @param None
    * @return Provider name
    * @throws ConfigException if "PROVIDER" not found in configuration file
    */
   public String getProvider() throws ConfigException {
       return getConfigString("PROVIDER");
   }

   /**
   * Gets the repeat sequence file name
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return repeat sequence file name
   * @throws ConfigException if "REPEAT_FILE" not found in configuration file
   */
  public String getRepeatFileName() throws ConfigException {
      return getConfigString("REPEAT_FILE");
  }
}

//  $Log$
//  Revision 1.2  2004/02/02 19:42:27  sc
//  Added repeat file name
//
//  Revision 1.1  2004/01/06 20:09:30  mbw
//  initial version imported from lib_java_seqloader
//
//  Revision 1.2  2003/12/20 16:23:37  sc
//  comment changes from code review
//
//  Revision 1.1  2003/12/19 12:56:01  sc
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
