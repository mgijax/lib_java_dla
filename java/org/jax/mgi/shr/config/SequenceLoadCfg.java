// $Header$                                                                                      //  $Header$
//  $Name$

package org.jax.mgi.shr.config;

import java.sql.Timestamp;

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
     * @throws ConfigException if "SEQ_LOAD_MODE" not found in configuration file
     */
    public String getLoadMode() throws ConfigException {
        return getConfigString("SEQ_LOAD_MODE");
    }

    /**
     * Gets the "Virtualness" of the sequences in this load
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the String true or false
     * @throws ConfigException if "SEQ_VIRTUAL" not found in configuration file
     */

    public String getVirtual() throws ConfigException {
        return getConfigString("SEQ_VIRTUAL");
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
     * @throws ConfigException if "SEQ_LOGICALDB" not found in configuration file
     */
    public String getLogicalDB() throws ConfigException {
        return getConfigString("SEQ_LOGICALDB");
    }
    /**
   * Gets the reference association type for this load
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return reference assoc typ name
   * @throws ConfigException if "SEQ_REF_ASSOC_TYPE" not found in configuration file
   */

    public String getReferenceAssocType() throws ConfigException {
        return getConfigString("SEQ_REF_ASSOC_TYPE");
    }

    /**
    * Gets the provider name for this load
    * @assumes Nothing
    * @effects Nothing
    * @param None
    * @return Provider name
    * @throws ConfigException if "SEQ_PROVIDER" not found in configuration file
    */
   public String getProvider() throws ConfigException {
       return getConfigString("SEQ_PROVIDER");
   }

   /**
   * Gets the repeat sequence file name
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return repeat sequence file name
   * @throws ConfigException if "SEQ_REPEAT_FILE" not found in configuration file
   */
  public String getRepeatFileName() throws ConfigException {
      return getConfigString("SEQ_REPEAT_FILE");
  }
  /**
  * Gets the sequence quality
  * @assumes Nothing
  * @effects Nothing
  * @param None
  * @return sequence quality
  * @throws ConfigException if "SEQ_QUALITY" not found in configuration file
  */
  public String getQuality() throws ConfigException {
     return getConfigString("SEQ_QUALITY");
  }
  /**
   * Gets the sequence type
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return sequence quality
   * @throws ConfigException if "SEQ_TYPE" not found in configuration file
   */
  public String getSeqType() throws ConfigException {
    return getConfigString("SEQ_TYPE");
  }

  /**
   * Gets the organism
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return organism
   * @throws ConfigException if "SEQ_ORGANISM" not found in configuration file
   */
  public String getOrganism() throws ConfigException {
    return getConfigString("SEQ_ORGANISM");
  }

  /**
   * Gets the release number for this load
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return release number
   * @throws ConfigException if "SEQ_RELEASE_NO" not found in configuration file
   */
  public String getReleaseNo() throws ConfigException {
    return getConfigString("SEQ_RELEASE_NO");
  }

  /**
   * Gets the sequence status for this load
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return the sequence status
   * @throws ConfigException if "SEQ_STATUS" not found in configuration file
   */
  public String getStatus() throws ConfigException {
    return getConfigString("SEQ_STATUS");
  }

  /**
   * Gets the sequence type for this load
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return the sequence type
   * @throws ConfigException if "SEQ_TYPE" not found in configuration file
   */
  public String getType() throws ConfigException {
    return getConfigString("SEQ_TYPE");
  }

  /**
   * Gets the sequence release data
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return the sequence release date
   * @throws ConfigException if "SEQ_RELEASE_DATE" not found in configuration file
   */
  public Timestamp getReleaseDate() throws ConfigException {
    return getConfigDate("SEQ_RELEASE_DATE");
  }

  /**
   * Gets the jnumber for this load
   * @assumes Nothing
   * @effects Nothing
   * @param None
   * @return the jnumber for this load
   * @throws ConfigException if "SEQ_JNUMBER" not found in configuration file
   */
  public String getJnumber() throws ConfigException {
    return getConfigString("SEQ_JNUMBER");
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
}

//  $Log$
//  Revision 1.7  2004/04/02 18:39:24  mbw
//  added a getJnumber method
//
//  Revision 1.6  2004/04/01 20:19:00  sc
//  added JOBSTREAM
//
//  Revision 1.5  2004/03/29 20:26:08  mbw
//  changed existing parameter names to be prefixed by the SEQ string and added a few new parameters
//
//  Revision 1.4  2004/03/24 18:31:31  sc
//  added getQuality and getSeqType
//
//  Revision 1.3  2004/02/27 13:57:16  sc
//  removed getLoadReference
//
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
