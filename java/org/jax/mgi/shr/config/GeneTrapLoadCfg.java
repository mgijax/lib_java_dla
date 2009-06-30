package org.jax.mgi.shr.config;

/**
 * An object that retrieves Configuration parameters for dbGSS gene trap 
 * ALO load
 * @does
 *   <UL>
 *   <LI> provides methods to retrieve Configuration parameters that are
 *        specific to the dbGSS gene trap ALO Load
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class GeneTrapLoadCfg extends ALOLoadCfg {

    /**
    * Constructs a gene trap load configurator
    * @assumes Nothing
    * @effects Nothing
    * @throws ConfigException if a configuration manager cannot be obtained
    */

    public GeneTrapLoadCfg() throws ConfigException {
    }

    /**
      * Gets gene trap sequence tag method
      * @assumes Nothing
      * @effects Nothing
      * @return The sequence tag method
      * @throws ConfigException if "GT_SEQTAG_METHOD" not found in configuration
      */
     public String getSeqTagMethods() throws ConfigException {
         return getConfigString("GT_SEQTAG_METHODS");
     }
     /**
      * Gets the best hits file path with which to determine goodHitCount
      * @assumes Nothing
      * @effects Nothing
      * @return The best hits file path with which to determine goodHitCount
      * @throws ConfigException if "BEST_HITS_ALL" not found in configuration
      */
     public String getBestHitsFile() throws ConfigException {
         return getConfigString("BEST_HITS_ALL");
     }
     /**
      * Gets the single hits file path with which to determine goodHitCount
      * @assumes Nothing
      * @effects Nothing
      * @return The single hits file path with which to determine goodHitCount
      * @throws ConfigException if "SINGLE_HITS_ALL" not found in configuration
      */
     public String getSingleHitsFile() throws ConfigException {
         return getConfigString("SINGLE_HITS_ALL");
     }

     /**
      * Gets the name of the file which to write the set of sequence keys processed
      * @return the full file path of the sequence key file
      * @throws ConfigException if "SEQUENCES_PROCESSED" not found in configuration
      */
     public String getSeqFile() throws ConfigException {
         return getConfigString("SEQUENCES_PROCESSED");
     }
}
