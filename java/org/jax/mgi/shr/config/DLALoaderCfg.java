package org.jax.mgi.shr.config;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * A Configurator for configuring the DLALoader class
 * @has A set of configuration values and accessors for them
 * @does Reads values of configuration parameters from configuration
 * files and java system properties and makes these value available to the
 * calling class
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLALoaderCfg extends Configurator {

    private String DEFAULT_REPORTDIR = ".";

  public DLALoaderCfg() throws ConfigException {
      super();
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
   * get the name of the Job Stream
   * @assumes Nothing
   * @effects Nothing
   * @return the Jobstream name
   * @throws ConfigException if "JOBSTREAM" not found in configuration file
   */
  public String getJobstreamName() throws ConfigException {
    return getConfigString("JOBSTREAM");
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

  /**
   * get whether or not to only run the post report formatters
   * @assumes nothing
   * @effects nothing
   * @return true if ok to only run post report formatters, false otherwise
   * @throws ConfigException thrown if there is an error reading boolean
   * values from the configuration file
   */
  public Boolean getOkToFormatReportsOnly()
  throws ConfigException
  {
      return getConfigBoolean("DLA_FORMAT_REPORTS_ONLY", new Boolean(false));
  }

  /**
   * get the reports directory name from the configuration file designated as
   * "RPTDIR"
   * @return the reports directory name
   */
  public String getReportsDir()
  {
      return getConfigString("RPTDIR", DEFAULT_REPORTDIR);
  }

}
