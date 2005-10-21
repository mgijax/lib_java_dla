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

  /**
   * get the path name of the vocload command. The default is
   * /usr/local/mgi/live/dataload/vocload/simpleLoad.py
   * @return the command path name
   */
  public String getCommandPath()
  {
      return super.getConfigString("VOCLOAD_COMMAND_PATH",
                                   DEFAULT_VOCLOAD_PATH);
  }

  /**
   * get the name of the rcd file to use. The default is
   * /usr/local/mgi/live/dataload/annotload/simple.rcd
   * @return the name of th rcd file to use
   */
  public String getRCDFilename()
  {
      return super.getConfigString("VOCLOAD_RCD_FILENAME",
                                   DEFAULT_VOCLOAD_RCD);
  }

  /**
   * get whether or not to prevent the results from being bcped into the
   * database. The default is false.
   * @return true if the bcp command should be prevented, false otherwise
   * @throws ConfigException thrown if there is an error reading the boolean
   * value from the configuration
   */
  public Boolean getOkToPreventUpdate() throws ConfigException
  {
      return super.getConfigBoolean("VOCLOAD_OK_TO_PREVENT_UPDATE",
                                    new Boolean(false));
  }

  /**
   * get whether or not this instance is a simple vocload. The default
   * is true.
   * @return true if the is a simple vocaload, false otherwise
   * @throws ConfigException thrown if there is an error reading the boolean
   * value from the configuration
   */
  public Boolean getIsSimple()
  throws ConfigException
  {
      return super.getConfigBoolean("VOCLOAD_IS_SIMPLE", new Boolean(true));
  }

  /**
   * get whether or not this is a full vocload. The defualt is true.
   * @return true if this is a full vocload, false otherwise
   * @throws ConfigException thrown if there is an error reading the boolean
   * value from the configuration
   */
  public Boolean getIsFull()
  throws ConfigException
  {
      return super.getConfigBoolean("VOCLOAD_IS_FULL", new Boolean(true));
  }

  /**
   * get the name of the vocabulary. There is no default
   * @return the name of the vocabulary
   * @throws ConfigException thrown if this value is not set in the
   * configuration
   */
  public String getVocabName() throws ConfigException
  {
      return super.getConfigString("VOCLOAD_NAME");
  }

  /**
   * get the name of the results output directory. The defualt is the current
   * runtime directory.
   * @return the output directory name
   */
  public String getOutputDirectory()
  {
      return super.getConfigString("VOCLOAD_OUTPUTDIR", ".");
  }



}
