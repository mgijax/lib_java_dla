package org.jax.mgi.shr.config;

import java.io.File;
import org.jax.mgi.shr.config.ConfigException;

/**
 * An object for configuring a DataLoadLogger.
 * @has a set of DataLoadLogger configuration parameters and a reference
 * to a ConfigurationManager
 * @does provides methods for getting and setting configuration paramaters
 * for a DataLoadLogger.
 * <p>Description: This class obtains a reference to the ConfiguratRionManager
 * singleton class which holds all the system configuration parameters read in
 * from the system properties and the configuration file. It then provides
 * get methods for looking up parameter values that pertain to a
 * DataLoadLogger and provides set methods for overriding these values.
 * @company Jackson Laboratory</p>
 * @author M. Walker
 * @version 1.0
 */

public class DLALoggerCfg extends Configurator {

    private static String THIS_CLASS = DLALoggerCfg.class.getName();
    private static String PROC_SUFFIX = ".proc.log";
    private static String CUR_SUFFIX = ".cur.log";
    private static String VAL_SUFFIX = ".val.log";
    private static String DIAG_SUFFIX = ".diag.log";
    private static String DEFAULT_PATH = ".";
    private static String DEFAULT_NAME = "dataLoad";


  /**
   * default constructor which obtains a reference to the
   * ConfigurationManager singleton class.
   * @throws ConfigException
   */
    public DLALoggerCfg() throws ConfigException {
      super();
    }

    /**
     * get the name of the process log. The parameter name read from the
     * configuration file or system properties is LOG_PROC. The default
     * value is the designated process name configured by the LOG_PROCESSNAME
     * parameter followed by the string ".proc.log". If the LOG_PROCESSNAME
     * is not configured then the default name is dataLoadLogger.proc.log.
     * @return name of log
     */
    public String getLogp() {
      String s = getConfigString("LOG_PROC", getDefaultName() + PROC_SUFFIX);
      return getFullPathName(s);
    }

    /**
     * get the name of the curator log. The parameter name read from the
     * configuration file or system properties is LOG_CUR. The default
     * value is the designated process name configured by the LOG_PROCESSNAME
     * parameter followed by the string ".cur.log". If the LOG_PROCESSNAME is
     * not configured then the default name is dataLoadLogger.cur.log.
     * @return name of log
     */
    public String getLogc() {
      String s = getConfigString("LOG_CUR", getDefaultName() + CUR_SUFFIX);
      return getFullPathName(s);
    }

    /**
     * get the name of the diagnostic log. The parameter name read from the
     * configuration file or system properties is LOG_DIAG. The default
     * value is the designated process name configured by the LOG_PROCESSNAME
     * parameter followed by the string ".diag.log". If the LOG_PROCESSNAME is
     * not configured then the default name is dataLoadLogger.diag.log.
     * @return name of log
     */
    public String getLogd() {
      String s = getConfigString("LOG_DIAG", getDefaultName() + DIAG_SUFFIX);
      return getFullPathName(s);
    }

    /**
     * get the name of the validation log. The parameter name read from the
     * configuration file or system properties is LOG_VAL. The default
     * value is the designated process name configured by the LOG_PROCESSNAME
     * parameter followed by the string ".val.log". If the LOG_PROCESSNAME is
     * not configured then the default name is dataLoadLogger.val.log.
     * @return name of log
     */
    public String getLogv() {
      String s = getConfigString("LOG_VAL", getDefaultName() + VAL_SUFFIX);
      return getFullPathName(s);
    }

    /**
     * get the path name of the directory where the logs will be stored. The
     * parameter name read from the configuration file or system properties is
     * LOG_PATH. The default value is the current directory.
     * @return path of logs
     */
    private String getPath() {
      return getConfigString("LOG_PATH", DEFAULT_PATH);
    }

    /**
     * get the value of the option which designates whether to log debug
     * messages. The parameter name read from the configuration file or
     * system properties is LOG_DEBUG. The value can be yes, no, true or
     * false and the case of the letters are ignored. The default value is
     * false.
     * @return true if debug is on
     * @throws ConfigException if an error occurs during configuration
     */
    public Boolean getDebug() throws ConfigException {
      return getConfigBoolean("LOG_DEBUG", new Boolean(false));
    }

    /**
     * get the process name which will be used in calculating default log
     * names. The parameter name read from the configuration file or system
     * properties is LOG_PROCESSNAME. The default value is the
     * "dataLoadLogger".
     * @return default base log name
     */
    private String getDefaultName() {
      return getConfigString("LOG_DEFAULTNAME", DEFAULT_NAME);
    }



    /**
     * return whether the full path is specified within the given file name.
     * @param filename the provided filename
     * @return true if the full path is specified within the given file name.
     */
    private boolean isPathFull(String filename) {
      File file = new File(filename);
      return file.isAbsolute();
    }

    /**
     * calculate the full path name for the given file name. If it is already
     * a full path then it's value is returned. If it is not then the path
     * parameter value is applied.
     * @param name the filename to inspect
     * @return the calculated filename which includes the full path
     */
    private String getFullPathName(String name) {
      if (isPathFull(name))
        return name;
      return getPath() + File.separator + name;
    }

}
