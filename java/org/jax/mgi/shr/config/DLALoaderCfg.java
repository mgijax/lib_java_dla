package org.jax.mgi.shr.config;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * <p>IS: </p>
 * <p>HAS: </p>
 * <p>DOES: </p>
 * <p>Company: Jackson Laboratory</p>
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