package org.jax.mgi.shr.config;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * @is A Configurator for configuring the MSProcessor
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
   * @effects if true then the lookup will be made and a full cache will be
   * created to hold all the associated clone sources from the database
   */
  public Boolean getOkToSearchAssocClones() throws ConfigException {
    return getConfigBoolean("MS_OK_TO_SEARCH_ASSOC_CLONES", new Boolean(true));
  }

}
