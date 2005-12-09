package org.jax.mgi.shr.config;

import org.jax.mgi.shr.log.LoggerFactory;

/**
 * A class for accessing configuration information about external command
 * paths
 * @has accessor methods for accessing configuration parameters
 * @does lookups configuration parameters from the ConfigurationManagement
 * object
 * @company The Jackson Laboratory
 * @author M Walker
 */



public class AnnotloadCfg extends Configurator {

    private String DEFAULT_ANNOTLOAD_PATH =
        "/usr/local/mgi/live/dataload/annotload/annotload.py";
    private String DEFAULT_ANNOTLOAD_MODE = "new";
    private String DEFAULT_ANNOTLOAD_OUTPUTDIR = ".";

  /**
   * default constructor which will use unprefixed parameters from the
   * configuration file for configuring
   * @throws ConfigException thrown if the there is an error accessing the
   * configuration file
   */

  public AnnotloadCfg() throws ConfigException {
    super();
  }


  /**
   * constructor which accepts a prefix string that will be prepended to
   * all configuration parameter on lookup
   * @param pParameterPrefix the given prefix string
   * @throws ConfigException throws if there is a configuration error
   */

  public AnnotloadCfg(String pParameterPrefix) throws ConfigException {
    super();
    super.parameterPrefix = pParameterPrefix;
  }

  /**
   * get whether or not to prevent bcping the results into the database. The
   * default is false. The parameter name read from the configuration is
   * ANNOTLOAD_OK_TO_PREVENT_UPDATE.
   * @return true if the bcp should be prevented, false otherwise
   * @throws ConfigException thrown if there is an error reading the
   * boolean value from the configuration
   */
  public Boolean getOkToPreventAnnotLoadUpdate() throws ConfigException
  {
      return super.getConfigBoolean("ANNOTLOAD_OK_TO_PREVENT_UPDATE",
                                    new Boolean(false));
  }

  /**
   * get the command path for the annotload. The default value is
   * usr/local/mgi/live/dataload/annotload/annotload.py. The parameter name
   * read from the configuration is ANNOTLOAD_PATH.
   * @return the command path name
   */
  public String getAnnotLoadPath()
  {
      return super.getConfigString("ANNOTLOAD_PATH",
                                        DEFAULT_ANNOTLOAD_PATH);
  }

  /**
   * get the mode of the annotload. The default is 'new'. See annotload
   * documentation for more details. The parameter name read from the
   * configuration is ANNOTLOAD_MODE.
   * @return the mode
   */
  public String getAnnotLoadMode()
  {
      return super.getConfigString("ANNOTLOAD_MODE",
                                        DEFAULT_ANNOTLOAD_MODE);
  }

  /**
   * get the annotation type. The parameter name read from the configuration is
   * ANNOTLOAD_TYPE. There is no default value.
   * @return the annotation type
   * @throws ConfigException thrown if this parameter is not set
   */
  public String getAnnotLoadType() throws ConfigException
  {
      return super.getConfigString("ANNOTLOAD_TYPE");
  }

  /**
   * get the annot load jnumber. The parameter name read from the
   * configuration is ANNOTLOAD_JNUMBER. There is no default value.
   * @return the jnumber
   * @throws ConfigException thrown if this value is not set
   */
  public String getAnnotLoadReference() throws ConfigException
  {
      return super.getConfigString("ANNOTLOAD_JNUMBER");
  }

  /**
   * get the annot load output directory name. The parameter name read from the
   * configuration is ANNOTLOAD_OUTPUTDIR. The default value is the current
   * directory.
   * @return the output directory name
   */
  public String getAnnotLoadOutputPath() throws ConfigException
  {
      return super.getConfigString("ANNOTLOAD_OUTPUTDIR",
                                   DEFAULT_ANNOTLOAD_OUTPUTDIR);
  }






}
