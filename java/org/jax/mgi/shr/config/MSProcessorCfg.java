package org.jax.mgi.shr.config;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * A Configurator for configuring the MSProcessor
 * @has A set of configuration values and accessors for them
 * @does Reads values of configuration parameters from configuration
 * files and java system properties and makes these value available to the
 * calling class
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class MSProcessorCfg extends Configurator {

  /**
   * constructor
   * @throws ConfigException
   */
  public MSProcessorCfg() throws ConfigException {
  }

  /**
   * get the ok to search assoc clones when processing an anoymous source in
   * order to find a named source. The configuration variable search is
   * MS_OK_TO_SEARCH_ASSOC_CLONES and the default is true if the parameter is
   * not found
   * @return true if the lookup should be performed and false otherwise
   * @assumes nothing
   * @effects if true and if the lookup cache is configured to be full, a
   * high performance hit is to be expected on the initial lookup
   * @throws ConfigException if there is an error accessing the configuration
   */
  public Boolean getOkToSearchAssocClones() throws ConfigException {
    return getConfigBoolean("MS_OK_TO_SEARCH_ASSOC_CLONES", new Boolean(true));
  }

  /**
   * get the ok to use a full cache when looking up sources through the
   * associated clones. With this option set to true, there will be a high
   * overhead on the first lookup to fully load the cache. If this option is
   * set to false then a lazy cache strategy will be used. The configuration
   * variable is MS_USE_ASSOC_CLONES_FULL_CACHE and the default is true
   * @return true if the assoc clone lookup should be fully cached or false
   * if the lookup should be a lazy cache
   * @assumes nothing
   * @effects if true a high performance hit is to be expected on the
   * initial lookup
   * @throws ConfigException if there is an error accessing the configuration
   */
  public Boolean getUseAssocClonesFullCache() throws ConfigException {
    return getConfigBoolean("MS_USE_ASSOC_CLONES_FULL_CACHE",
                            new Boolean(true));
  }


}
