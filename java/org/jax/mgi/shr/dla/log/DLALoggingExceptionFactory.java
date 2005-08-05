package org.jax.mgi.shr.dla.log;

import org.jax.mgi.shr.exception.ExceptionFactory;


/**
 * An ExceptionFactory which returns LoggingExceptions
 * from a collection of named instances
 * @has a hashmap of LoggingException instances
 * @does returns a LoggingException by name
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLALoggingExceptionFactory extends ExceptionFactory {

  /**
   * could not initialize log files
   */
  public static final String InitializeErr =
      "org.jax.mgi.shr.log.InitializeErr";
  static {
    exceptionsMap.put(InitializeErr, new DLALoggingException(
        "Could not initialize log files", false));
  }

  /**
   * the log could not be configured due to errors in the configuration
   */
  public static final String ConfigurationErr =
      "org.jax.mgi.shr.log.ConfigureErr";
  static {
    exceptionsMap.put(ConfigurationErr, new DLALoggingException(
        "Could not configure log files due to error in configuration",
        false));
  }


}
