package org.jax.mgi.shr.dla;

/**
 * @is A static class which assigns names to a set of constants defined
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
   * The application ran to completion but encountered at least one non
   * fatal error during processing
   */
  public static final int NONFATAL_ERROR = 2;

  /**
   * @purpose exits the system, logs a message to the system
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
   * @purpose exits the system with a fatal exit code and logs a message
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
   * @purpose exits the system with a calculated exit code based on
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
    int errorCnt = DLAExceptionHandler.getErrorCount();
    int dataErrorCount = DLAExceptionHandler.getDataErrorCount();
    logMessage(logger, errorCnt, dataErrorCount);
    if (fatal)
      exitCode = FATAL_ERROR;
    else if (errorCnt > 0)
      exitCode = NONFATAL_ERROR;
    else
      exitCode = OK;
    System.exit(exitCode);
    logger.close();
  }

  /**
   * @purpose logs the error count to the system logger
   * @assumes nothing
   * @effects a message gets logged to the system logger
   * @param logger thr logger instance
   * @param pErrorCnt the total number of errors
   * @param pDataRelatedCnt the total number of data related errors
   */
  private static void logMessage(DLALogger logger, int pErrorCnt,
                                 int pDataRelatedCnt) {
    String message = "The system exited with " + pErrorCnt +
        " total errors.";
    if (pErrorCnt > 0) {
      message = message + " The number of data related errors was " +
          pDataRelatedCnt + ".";
    }
    logger.logpInfo(message, true);
    logger.logcInfo(message, true);
  }
}