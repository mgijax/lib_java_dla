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



public class CommandsCfg extends Configurator {

    private String DEFAULT_ANNOTLOAD_PATH =
        "/usr/local/mgi/live/dataload/annotload/annotload.py";
    private String DEFAULT_ANNOTLOAD_MODE = "new";

  /**
   * default constructor which will use unprefixed parameters from the
   * configuration file for configuring
   * @throws ConfigException thrown if the there is an error accessing the
   * configuration file
   */

  public CommandsCfg() throws ConfigException {
    super();
  }


  /**
   * constructor which accepts a prefix string that will be prepended to
   * all configuration parameter on lookup
   * @param pParameterPrefix the given prefix string
   * @throws ConfigException throws if there is a configuration error
   */

  public CommandsCfg(String pParameterPrefix) throws ConfigException {
    super();
    super.parameterPrefix = pParameterPrefix;
  }

  public Boolean getOkToPreventAnnotLoadUpdate() throws ConfigException
  {
      return super.getConfigBoolean("CMD_OK_TO_PREVENT_ANNOTLOAD_UPDATE",
                                    new Boolean(false));
  }


  public String getAnnotLoadPath() throws ConfigException
  {
      return super.getConfigString("CMD_ANNOTLOAD_PATH",
                                        DEFAULT_ANNOTLOAD_PATH);
  }

  public String getAnnotLoadMode() throws ConfigException
  {
      return super.getConfigString("CMD_ANNOTLOAD_MODE",
                                        DEFAULT_ANNOTLOAD_MODE);
  }

  public String getAnnotLoadType() throws ConfigException
  {
      return super.getConfigString("CMD_ANNOTLOAD_TYPE");
  }

  public String getAnnotLoadReference() throws ConfigException
  {
      return super.getConfigString("CMD_ANNOTLOAD_JNUMBER");
  }





}
