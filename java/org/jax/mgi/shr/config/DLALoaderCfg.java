package org.jax.mgi.shr.config;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * @is A Configurator for configuring the DLALoader class
 * @has A set of configuration values and accessors for them
 * @does Reads values of configuration parameters from configuration
 * files and java system properties and makes these value available to the
 * calling class
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLALoaderCfg extends Configurator {

  public DLALoaderCfg() throws ConfigException {
  }

  /**
   * get the loader name to run.
   * @return loader name.
   * @throws ConfigException thrown if loader name could not be obtained
   * from the configuration
   */
  public String getLoaderClass() throws ConfigException {
    return getConfigString("DLA_LOADER");
  }


}