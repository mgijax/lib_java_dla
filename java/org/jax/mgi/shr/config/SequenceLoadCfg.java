// $Header$
//  $Name$
package org.jax.mgi.shr.config;

import java.sql.Timestamp;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * An object that retrieves Configuration pararmeters for sequence loaders
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
    * @throws ConfigException if a configuration manager cannot be obtained
    */

    public SequenceLoadCfg() throws ConfigException {
    }

    /**
     * Gets the load mode e.g. 'incremental' or 'delete_reload'
     * @assumes Nothing
     * @effects Nothing
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
     * @return logicalDB name
     * @throws ConfigException if "SEQ_LOGICALDB" not found in configuration file
     */
    public String getLogicalDB() throws ConfigException {
        return getConfigString("SEQ_LOGICALDB");
    }

    /**
    * Gets the provider name for this load
    * @assumes Nothing
    * @effects Nothing
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
   * @return the sequence status
   * @throws ConfigException if "SEQ_STATUS" not found in configuration file
   */
  public String getStatus() throws ConfigException {
    return getConfigString("SEQ_STATUS");
  }

  /**
   * Gets the sequence strain for this load
   * @assumes Nothing
   * @effects Nothing
   * @return the sequence strain
   * @throws ConfigException if "SEQ_STRAIN" not found in configuration file
   */
  public String getStrain() throws ConfigException {
    return getConfigString("SEQ_STRAIN");
  }

  /**
   * Gets the sequence tissue for this load
   * @assumes Nothing
   * @effects Nothing
   * @return the sequence tissue
   * @throws ConfigException if "SEQ_TISSUE" not found in configuration file
   */
  public String getTissue() throws ConfigException {
    return getConfigString("SEQ_TISSUE");
  }

  /**
   * Gets the sequence age for this load
   * @assumes Nothing
   * @effects Nothing
   * @return the sequence age
   * @throws ConfigException if "SEQ_AGE" not found in configuration file
   */
  public String getAge() throws ConfigException {
    return getConfigString("SEQ_AGE");
  }

  /**
   * Gets the sequence gender for this load
   * @assumes Nothing
   * @effects Nothing
   * @return the sequence gender
   * @throws ConfigException if "SEQ_GENDER" not found in configuration file
   */
  public String getGender() throws ConfigException {
    return getConfigString("SEQ_GENDER");
  }

  /**
   * Gets the sequence cell line for this load
   * @assumes Nothing
   * @effects Nothing
   * @return the sequence cell lline
   * @throws ConfigException if "SEQ_CELLLINE" not found in configuration file
   */
  public String getCellLine() throws ConfigException {
    return getConfigString("SEQ_CELLLINE");
  }

  /**
   * Gets the sequence release data
   * @assumes Nothing
   * @effects Nothing
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
    return getConfigObject("SEQ_INTERPRETER");
  }

  /**
   * get the SequenceLookup batch size, default is 100
   * @return number of seqids for the SequenceLookup to query with at once.
   * @assumes nothing
   * @effects nothing
   * @throws ConfigException thrown if interpreter object could not be created
   * from the configuration
   */
  public String getQueryBatchSize() throws ConfigException {
    return getConfigString("SEQ_QUERY_BATCHSIZE", "400");
  }

}

//  $Log$
//  Revision 1.13  2005/08/05 18:57:12  mbw
//  merged code from tr6086
//
//  Revision 1.12  2005/02/09 14:52:36  sc
//  tr6473
//
//  Revision 1.11.12.1  2005/01/21 19:17:10  sc
//  added getQueryBatchSize with default of 400
//
//  Revision 1.11.10.2  2004/12/10 18:15:22  mbw
//  javadocs only
//
//  Revision 1.11.10.1  2004/12/09 18:08:30  mbw
//  fixed javadocs warnings
//
//  Revision 1.11.6.1  2005/08/02 16:24:02  mbw
//  merged branch tr6086
//
//  Revision 1.11.10.2  2004/12/10 18:15:22  mbw
//  javadocs only
//
//  Revision 1.11  2004/10/20 17:47:57  mbw
//  removed getType() and getReferenceAssocType() methods (see TR6135, item 3)
//
//  Revision 1.10  2004/10/13 11:59:28  sc
//  added gets for assembly sequence load configuration values
//
//  Revision 1.9  2004/07/28 20:10:11  mbw
//  javadocs only
//
//  Revision 1.8.8.2  2004/08/23 16:20:33  sc
//  added several methods to get source info and getInterpreterClass()
//
//  Revision 1.8.8.1  2004/07/27 18:19:00  sc
//  added getInterpreterClass method.
//
//  Revision 1.8  2004/04/02 19:07:33  mbw
//  changed config parm name from SEQ_JOBSTREAM to JOB_STREAM
//
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
