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
        return getConfigString("SEQ_VIRTUAL", "false");
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
  /**
   * If true, add sequence references.
   * @return whether to add sequence references
   * @throws ConfigException if "SEQ_OK_TO_LOAD_REFS" not found by the Configurator
   */

  public Boolean getOkToLoadReferences() throws ConfigException {
      return getConfigBoolean("SEQ_OK_TO_LOAD_REFS", Boolean.TRUE);
  }

  /**
   * get the ok to use a full cache when looking up sequences using the
   * AccessionLookup. With this option set to true, there will be a high
   * overhead on the first lookup to fully load the cache. If this option is
   * set to false then a lazy cache strategy will be used. The configuration
   * variable is SEQ_USE_ACCESSION_FULL_CACHE and the default is true
   * @return true if the accession lookup should be fully cached or false
   * if the lookup should be a lazy cache
   * @assumes nothing
   * @effects if true a high performance hit is to be expected on the
   * initial lookup
   * @throws ConfigException if there is an error accessing the configuration
   */
  public Boolean getUseAssocClonesFullCache() throws ConfigException {
    return getConfigBoolean("SEQ_USE_ACCESSION_FULL_CACHE",
                            new Boolean(true));
  }


}
