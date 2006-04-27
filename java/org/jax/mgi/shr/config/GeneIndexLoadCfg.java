package org.jax.mgi.shr.config;

import java.sql.Timestamp;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dla.input.fasta.FASTAFilter;

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
