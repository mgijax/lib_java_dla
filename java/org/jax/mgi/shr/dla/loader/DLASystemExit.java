package org.jax.mgi.shr.dla.loader;

import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * A static class which assigns names to a set of constants defined
 * for system exits codes and provides an exit method for DLA applications.
 * @has A set of exit codes used as possible values for the parameter to
 * the System.exit method
 * @does Provides a static exit method for DLA applications which reports a
 * summary of errors, closes the log files, and calculates the system exit
 * code
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLASystemExit {
  /**
   * Application ran to completion without any errors
   */
  public static final int OK = 0;
  /**
   * The application is exiting because of a fatal error
   */
  public static final int FATAL_ERROR = 1;

  /**
   * exits the system, logs a message to the system
   * logger reporting errors counts and closes all log files.
   * @assumes nothing
   * @effects a message will get logged to the system logger, all log
   * files will be closed and the java System.exit() method will get called
   * with a value of OK or NONFATAL_ERROR depending on the error count.
   */
  public static void exit() {
    exit(false);
  }

  /**
   * exits the system with a fatal exit code and logs a message
   * to the system logger reporting errors counts.
   * @assumes nothing
   * @effects a message will get logged to the system logger, all log
   * files will be closed and the java System.exit() method will get called
   * with a value of FATAL_ERROR.
   */
  public static void fatalExit() {
    exit(true);
  }

  /**
   * exits the system with a calculated exit code based on
   * the total error counts along with logging the error tally to the
   * system logger.
   * @assumes nothing
   * @effects the java System.exit() method will be called
   * @param fatal true if this is a fatal exit; false otherwise
   */
  private static void exit(boolean fatal) {
    int exitCode;
    DLALogger logger = null;
    try {
      logger = DLALogger.getInstance();
    }
    catch (DLALoggingException e) {
      fatal = true;
      System.err.println("Internal error occurred when calling system exit: " +
                         "A logger instance could not be obtained. Please " +
                         "report as a bug./n" + e.toString());
    }
    if (fatal)
      exitCode = FATAL_ERROR;
    else
      exitCode = OK;
    logger.close();
    System.exit(exitCode);
  }
}