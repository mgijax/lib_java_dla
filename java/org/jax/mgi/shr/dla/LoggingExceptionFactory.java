package org.jax.mgi.shr.log;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * <p>IS: An ExceptionFactory which returns LoggingExceptions
 * from the its storage of Exceptions.</p>
 * <p>HAS: a hashmap of Exceptions</p>
 * <p>DOES: returns Exceptions by name</p>
 * <p>Company: Jackson Laboratory</p>
 * @author M Walker
 * @version 1.0
 */

public class LoggingExceptionFactory extends ExceptionFactory {

  /**
   * a string could not be converted to another datatype
   */
  public static final String InitializeErr =
      "org.jax.mgi.shr.log.InitializeErr";
  static {
    exceptionsMap.put(InitializeErr, new LoggingException(
        "Could not initialize log files", false));
  }

  /**
   * the log could not be configured due to errors in the configuration
   */
  public static final String ConfigurationErr =
      "org.jax.mgi.shr.log.ConfigureErr";
  static {
    exceptionsMap.put(ConfigurationErr, new LoggingException(
        "Could not configure log files due to error in configuration",
        false));
  }


}