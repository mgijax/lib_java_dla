package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.log.Logger;

/**
 * <p>IS: A static class which assigns names to a set of constants defined
 * for system exits codes and provides an exit method for DLA applications.
 * </p>
 * <p>HAS: A set of exit codes used as possible values for the parameter to
 * the System.exit method</p>
 * <p>DOES: Provides a static exit method for DLA applications which reports a
 * summary of errors, closes the log files, and calculates the system exit
 * code</p>
 * <p>Company: Jackson Laboratory</p>
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
   * <p>Purpose: exits the system, logs a message to the system
   * logger reporting errors counts and closes all log files.</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: a message will get logged to the system logger, all log
   * files will be closed and the java System.exit() method will get called
   * with a value of OK or NONFATAL_ERROR depending on the error count.
   */
  public static void exit() {
    exit(false);
  }

  /**
   * <p>Purpose: exits the system with a fatal exit code and logs a message
   * to the system logger reporting errors counts.</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: a message will get logged to the system logger, all log
   * files will be closed and the java System.exit() method will get called
   * with a value of FATAL_ERROR.
   */
  public static void fatalExit() {
    exit(true);
  }

  /**
   * <p>Purpose: exits the system with a calculated exit code based on
   * the total error counts along with logging the error tally to the
   * system logger.</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: the java System.exit() method will be called</p>
   */
  private static void exit(boolean fatal) {
    int exitCode;
    Logger logger = null;
    try {
      logger = DataLoadLogger.getInstance();
    }
    catch (LoggingException e) {
      fatal = true;
      System.out.println("Internal error occurred when calling system exit: " +
                         "A logger instance could not be obtained. Please " +
                         "report as a bug./n" + e.toString());
    }
    DLAExceptionHandler errHandler = new DLAExceptionHandler(logger);
    int errorCnt = errHandler.getErrorCount();
    int dataErrorCount = errHandler.getDataErrorCount();
    logMessage(logger, errorCnt, dataErrorCount);
    if (fatal)
      exitCode = 1;
    else if (errorCnt > 0)
      exitCode = 2;
    else
      exitCode = 0;
    System.exit(exitCode);
    logger.close();
  }

  /**
   * <p>Purpose: logs the error count to the system logger</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: a message gets logged to the system logger</p>
   * @param pErrorCnt the total number of errors
   * @param pDataRelatedCnt the total number of data related errors
   */
  private static void logMessage(Logger logger, int pErrorCnt,
                                 int pDataRelatedCnt) {
    String message = "The system exited with " + pErrorCnt +
        " total errors.";
    if (pErrorCnt > 0) {
      message = message + " The number of data related errors was " +
          pDataRelatedCnt + ".";
    }
    logger.logInfo(message);
  }
}