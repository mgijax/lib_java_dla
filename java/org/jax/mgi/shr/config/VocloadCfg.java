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



public class VocloadCfg extends Configurator {

    private String DEFAULT_VOCLOAD_PATH =
        "/usr/local/mgi/live/dataload/vocload/simpleLoad.py";
    private String DEFAULT_VOCLOAD_RCD =
        "/usr/local/mgi/live/dataload/annotload/simple.rcd";

  /**
   * default constructor which will use unprefixed parameters from the
   * configuration file for configuring
   * @throws ConfigException thrown if the there is an error accessing the
   * configuration file
   */

  public VocloadCfg() throws ConfigException {
    super();
  }


  /**
   * constructor which accepts a prefix string that will be prepended to
   * all configuration parameter on lookup
   * @param pParameterPrefix the given prefix string
   * @throws ConfigException throws if there is a configuration error
   */

  public VocloadCfg(String pParameterPrefix) throws ConfigException {
    super();
    super.parameterPrefix = pParameterPrefix;
  }

  public String getCommandPath() throws ConfigException
  {
      return super.getConfigString("VOCLOAD_COMMAND_PATH",
                                   DEFAULT_VOCLOAD_PATH);
  }

  public String getRCDFilename() throws ConfigException
  {
      return super.getConfigString("VOCLOAD_RCD_FILENAME",
                                   DEFAULT_VOCLOAD_RCD);
  }

  public Boolean getOkToPreventUpdate() throws ConfigException
  {
      return super.getConfigBoolean("VOCLOAD_OK_TO_PREVENT_UPDATE",
                                    new Boolean(false));
  }

  public Boolean getIsSimple()
  throws ConfigException
  {
      return super.getConfigBoolean("VOCLOAD_IS_SIMPLE", new Boolean(true));
  }

  public Boolean getIsFull()
  throws ConfigException
  {
      return super.getConfigBoolean("VOCLOAD_IS_FULL", new Boolean(true));
  }

  public String getVocabName() throws ConfigException
  {
      return super.getConfigString("VOCLOAD_NAME");
  }

  public String getOutputDirectory()
  {
      return super.getConfigString("VOCLOAD_OUTPUTDIR", ".");
  }



}
