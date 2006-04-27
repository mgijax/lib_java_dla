package org.jax.mgi.shr.dla.loader;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * An object which handles exceptions of type DLALoaderException
 * @has An instance of a logger and static variables for totaling system
 * warnings and errors
 * @does provides a standard way to handle caught exceptions which include
 * functionality for logging and error/warning count totaling
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLALoaderExceptionHandler {

  /**
   * provides a standard method for handling exceptions
   * @assumes this method is not being called concurrently
   * @effects a message is written to a log file or files depending on the
   * type of exception and the warning count or error count is updated
   * depending on the attributes of exception.
   * @param e an exception that implements LoggableException
   */
  public static void handleException(DLALoaderException e) {
      DLALogger logger = null;
      try {
        logger = DLALogger.getInstance();
      }
      catch (DLALoggingException e2) {
        System.err.print("Cannot obtain a Logger for the following reason. " +
                         "This is really bad news. " +
                         "Exiting program unequivocally.\n" + e2.toString());
        System.exit(DLASystemExit.FATAL_ERROR);
      }

      e.printStackTrace();
      logger.logError(e.toString());
  }
}
