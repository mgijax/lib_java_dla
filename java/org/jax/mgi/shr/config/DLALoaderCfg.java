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
   * @assumes nothing
   * @effects nothing
   * @throws ConfigException thrown if loader name could not be obtained
   * from the configuration
   */
  public Object getLoaderClass() throws ConfigException {
    return getConfigObject("DLA_LOADER");
  }

  /**
   * get the database prefix name used in the configuration file for
   * configuring the load database.
   * @return database prefix name.
   * @assumes nothing
   * @effects nothing
   */
  public String getLoadPrefix() {
    return getConfigString("DLA_DB_PREFIX", "MGD");
  }


  /**
   * get the name of the SQLStream for loading data (the load stream)
   * @assumes nothing
   * @effects nothing
   * @return the name of the SQLStream
   */
  public String getLoadStreamName()
  {
      return getConfigString("DLA_LOAD_STREAM",
                             "org.jax.mgi.shr.dbutils.dao.Inline_Stream");
  }

  /**
   * get the name of the SQLStream for qc data (the qc stream)
   * @assumes nothing
   * @effects nothing
   * @return the name of the SQLStream
   */
  public String getQCStreamName()
  {
      return getConfigString("DLA_QC_STREAM",
                             "org.jax.mgi.shr.dbutils.dao.Inline_Stream");
  }

  /**
   * get the list of table names to truncate for the load stream
   * @assumes nothing
   * @effects nothing
   * @return the list of table names to truncate for the load stream
   */
  public String[] getTruncateLoadTables()
  {
      return getConfigStringArrayNull("DLA_TRUNCATE_LOAD_TABLES");
  }

  /**
   * get the list of table names to truncate for the qc stream
   * @assumes nothing
   * @effects nothing
   * @return the list of table names to truncate for the qc stream
   */
  public String[] getTruncateQCTables()
  {
      return getConfigStringArrayNull("DLA_TRUNCATE_QC_TABLES");
  }

}