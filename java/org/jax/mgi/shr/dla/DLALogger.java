// $Header$
// $Name$

package org.jax.mgi.shr.dla;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.io.IOException;
import java.lang.Throwable;

import org.jax.mgi.shr.config.DLALoggerCfg;
import org.jax.mgi.shr.config.ConfigException;

/**
 * @is an object that logs messages of various severity levels to a set of
 * predetermined log files in terms with the DLA logging standards.
 * @has log files, message handlers, message formatters and a configurator.
 *
 * @does formats and writes messages to the set of log files defined in
 * the DLA.
 * <p>Description:
 * The DLA standards names four log files for various types of reporting.
 * These include the process log, curator log, sql validation log, and the
 * diagnostics log. See the DLA Standards document for further information
 * regarding the use of these logs.
 * <p>
 * Log entries can be written in two ways. First, an entry can be
 * posted with a standard header stamp, which includes the date, class name,
 * severity level, and method name. A set of alternative methods are provided
 * which do not include the standard header stamps when posting to the logs.
 * These methods are provided as a way to do multiline information reporting
 * without cluttering the output with many header stamps.
 * <p>
 * The logger allows different severity levels of messages for each log
 * file. The process and curator logs are provided with only one severity
 * level, namely informational. The validation logs are provided with
 * informational, warning and error levels, wheareas the diagnostics logs are
 * provided with debug, error, informational and warning levels. Debug
 * messages can be turned off by calling the setDebug method with a value of
 * true.
 * <p>The following summarizes the methods for witing variaous types of
 * messages and to each log file:<br>
 * <ul>
 * <li>logpInfo - logs an informational message to the process log
 * <li>logcInfo - logs an informational message to the currator log
 * <li>logvInfo - logs an informational message to the valiadtion log
 * <li>logdInfo - logs an informational message to the diagnostics log<br><br>
 * <li>logvErr - logs an error message to the valiadtion log
 * <li>logdErr - logs an error message to the diagnostics log<br><br>
 * <li>logdDebug - logs a debug message to the diagnostics log
 * <p>
 * The DataLoadLogger class was not written for a multithreaded environment.
 *
 * @company The Jackson Lab
 * @author M Walker
 * @version 1.0
 */

public class DLALogger implements org.jax.mgi.shr.log.Logger {

  // the Singleton instance
  private static DLALogger instance = null;
  // the java1.4 Logger instance for the process log
  private Logger processLogger = null;
  // the java1.4 Logger instance for the curator log
  private Logger curatorLogger = null;
  // the java1.4 Logger instance for the validation log
  private Logger validationLogger = null;
  // the java1.4 Logger instance for the diagnostics log
  private Logger diagnosticsLogger = null;
  // the java 1.4 FileHandler object for the process log
  private FileHandler logpHandler;
  // the java 1.4 FileHandler object for the curator log
  private FileHandler logcHandler;
  // the java 1.4 FileHandler object for the diagnostics log
  private FileHandler logdHandler;
  // the java 1.4 FileHandler object for the validation log
  private FileHandler logvHandler;
  // the name of the process log
  private String logp;
  // the name of the curator log
  private String logc;
  // the name of the diagnostics log
  private String logd;
  // the name of the validation log
  private String logv;
  // indicator on which formatter has been applied to the process log
  private int logpFormatter;
  // indicator on which formatter has been applied to the curator log
  private int logcFormatter;
  // indicator on which formatter has been applied to the diagnostics log
  private int logdFormatter;
  // indicator on which formatter has been applied to the validation log
  private int logvFormatter;
  // an object for formatting messages without a header stamp
  private MessageOnlyFormatter
          messageOnlyFormatter = new MessageOnlyFormatter();
  // an object for formatting messages with the conventional DLA header
  private DLALogFormatter stampedFormatter = new DLALogFormatter();
  // an indicator of the debug state. Debug messages will not get logged
  // if this state is false
  private boolean debugState = false;
  // the name of the class which logs a message
  private String clientClass = null;
  // the name of the method which logs a message
  private String clientMethod = null;
  // the logging line separator
  private static final String lineSeparator =
      System.getProperty( "line.separator");
  // named constant for the message format which includes header stamps
  private static final int STAMPED_FORMATTER = 1;
  // named constant for the message format which doesnt include header stamps
  private static final int BRIEF_FORMATTER = 2;

  // the following are string constants used throughout this class
  private final String LOGGER = this.getClass().getName();
  private static final String FRAMEWORKS = "java.util.logging";
  private static final String DEFAULT_LOG =
      "org.jax.mgi.logging.DefaultLogger";
  private static final String PROC_LOG =
      "org.jax.mgi.logging.ProcessLogger";
  private static final String CUR_LOG =
      "org.jax.mgi.logging.curatorLogger";
  private static final String VAL_LOG =
      "org.jax.mgi.logging.validationLogger";
  private static final String DIAG_LOG =
      "org.jax.mgi.logging.DiagnosticLogger";

  // the following are exceptions thrown by this class
  private static final String InitializeErr =
      DLALoggingExceptionFactory.InitializeErr;
  private static final String ConfigurationErr =
      DLALoggingExceptionFactory.ConfigurationErr;



 /**
  * public constructor method to obtain a singleton instance of
  * DataLoadLogger.
  * @assumes nothing
  * @effects a new instance of the DataLoadLogger will cretaed if it
  * doesnt already exist.
  * @return singleton instance of DataLoadLogger
  * @throws DLALoggingException thrown if an error occurs during
  * configuration.
  */

  public static synchronized DLALogger getInstance()
  throws DLALoggingException {
    if (instance == null) {
      try {
        instance = new DLALogger(new DLALoggerCfg());
      }
      catch (ConfigException e) {
        DLALoggingExceptionFactory eFactory =
            new DLALoggingExceptionFactory();
        DLALoggingException e2 =
            (DLALoggingException)eFactory.getException(ConfigurationErr, e);
        throw e2;
      }
      instance.createLogp();
      instance.createLogv();
      instance.createLogc();
      instance.createLogd();
    }
    return instance;
  }

  /**
   * logs an informational message with a standard header
   * stamp to the diagnostics log
   * @assumes nothing
   * @effects a message will get logged to the diagnostics log
   * @param message the message to log
   */
  public void logInfo(String message) {
    logdInfo(message, false);
  }

  /**
   * logs an error message with a standard header stamp to the
   * diagnostics log
   * @assumes nothing
   * @effects an error message will get logged to the diagnostics log
   * @param message the message to log
   */
  public void logError(String message) {
    logdErr(message);
  }

  /**
   * logs a debug message with a standard header stamp to the
   * diagnostics log
   * @assumes nothing
   * @effects a debug will get logged to the diagnostics log if the
   * debug state is true
   * @param message the message to log
   */
  public void logDebug(String message) {
    logdDebug(message);
  }



  /**
    * Writes an informational message to the process log.
    * A standard header stamp will be included.
    * @assumes nothing
    * @effects a message will be written to the process log
    * @param  msg string message.
    * @param doStamping true if the message should be time stamped
    */
  public void logpInfo(String msg, boolean doStamping) {
    if (doStamping) {
      if (logpFormatter != STAMPED_FORMATTER) {
        // swith the formatter to stamped
        logpHandler.setFormatter(stampedFormatter);
        // set indicator to stamped
        logpFormatter = STAMPED_FORMATTER;
      }
      setClassNameMethodName();
      // log the message with header
      processLogger.logp(Level.INFO, clientClass, clientMethod, msg);
    }
    else {
      if (logpFormatter != BRIEF_FORMATTER) {
        // switch the formatter to non-stamped
        logpHandler.setFormatter(messageOnlyFormatter);
        // set the indicator to non-stamped
        logpFormatter = BRIEF_FORMATTER;
      }
      // log message without header
      processLogger.finer(msg);
    }
  }

  /**
    * Writes an informational message to the curator log.
    * A standard header stamp will be included.
    * @assumes nothing
    * @effects a message will be written to the curator log
    * @param  msg string message.
    * @param doStamping true if the message should be time stamped
    */
  public void logcInfo(String msg, boolean doStamping) {
    if (doStamping) {
      if (logcFormatter != STAMPED_FORMATTER) {
        // swith the formatter to stamped
        logcHandler.setFormatter(stampedFormatter);
        // set indicator to stamped
        logcFormatter = STAMPED_FORMATTER;
      }
      setClassNameMethodName();
      // log the message with header
      curatorLogger.logp(Level.INFO, clientClass, clientMethod, msg);
    }
    else {
      if (logcFormatter != BRIEF_FORMATTER) {
        // switch the formatter to non-stamped
        logcHandler.setFormatter(messageOnlyFormatter);
        // set the indicator to non-stamped
        logcFormatter = BRIEF_FORMATTER;
      }
      // log message without header
      curatorLogger.finer(msg);
    }
  }

  /**
    * Writes an informational message to the validation log.
    * A standard header stamp will be included.
    * @assumes nothing
    * @effects a message will be written to the validation log
    * @param  msg string message.
    * @param doStamping true if the message should be time stamped
    */
  public void logvInfo(String msg, boolean doStamping) {
    if (doStamping) {
      if (logvFormatter != STAMPED_FORMATTER) {
        // swith the formatter to stamped
        logvHandler.setFormatter(stampedFormatter);
        // set indicator to stamped
        logvFormatter = STAMPED_FORMATTER;
      }
      setClassNameMethodName();
      // log the message with header
      validationLogger.logp(Level.INFO, clientClass, clientMethod, msg);
    }
    else {
      if (logvFormatter != BRIEF_FORMATTER) {
        // switch the formatter to non-stamped
        logvHandler.setFormatter(messageOnlyFormatter);
        // set the indicator to non-stamped
        logvFormatter = BRIEF_FORMATTER;
      }
      // log message without header
      validationLogger.finer(msg);
    }
  }

  /**
    * Writes an informational message to the diagnostic log.
    * A standard header stamp will be included.
    * @assumes nothing
    * @effects a message will be written to the diagnostics log
    * @param  msg string message.
    * @param doStamping true if the message should be time stamped
    */
  public void logdInfo(String msg, boolean doStamping) {
    if (doStamping) {
      if (logdFormatter != STAMPED_FORMATTER) {
        // swith the formatter to stamped
        logdHandler.setFormatter(stampedFormatter);
        // set indicator to stamped
        logdFormatter = STAMPED_FORMATTER;
      }
      setClassNameMethodName();
      // log the message with header
      diagnosticsLogger.logp(Level.INFO, clientClass, clientMethod, msg);
    }
    else {
      if (logdFormatter != BRIEF_FORMATTER) {
        // switch the formatter to non-stamped
        logdHandler.setFormatter(messageOnlyFormatter);
        // set the indicator to non-stamped
        logdFormatter = BRIEF_FORMATTER;
      }
      // log message without header
      diagnosticsLogger.finer(msg);
    }
  }



  /**
    * Writes an error message to the validation log.
    * A standard header stamp will be included.
    * @assumes nothing
    * @effects a message will be written to the validation log
    * @param  msg string message.
    */
  public void logvErr(String msg) {
    if (logvFormatter != STAMPED_FORMATTER) {
      // swith the formatter to stamped
      logvHandler.setFormatter(stampedFormatter);
      // set indicator to stamped
      logvFormatter = STAMPED_FORMATTER;
    }
    setClassNameMethodName();
    // log the message with header stamp
    validationLogger.logp(Level.SEVERE, clientClass, clientMethod, msg);
  }

  /**
    * Writes an error message to the diagnostics log.
    * A standard header stamp will be included.
    * @assumes nothing
    * @effects a message will be written to the diagnostics log
    * @param  msg string message.
    */
  public void logdErr(String msg) {
    if (logdFormatter != STAMPED_FORMATTER) {
      // swith the formatter to stamped
      logdHandler.setFormatter(stampedFormatter);
      // set indicator to stamped
      logdFormatter = STAMPED_FORMATTER;
    }
    setClassNameMethodName();
    // log the message with header stamp
    diagnosticsLogger.logp(Level.SEVERE, clientClass, clientMethod, msg);
  }

  /**
    * Writes a debug message to the diagnostics log.
    * A standard header stamp will be included.
    * This message will only be written if the debug
    * state is set to true. The setDebug method is
    * used to toggle the debug state.
    * @assumes nothing
    * @effects a message will be written to the diagnostics log if
    * the debug state is true
    * @param  msg string message.
    */
  public void logdDebug(String msg) {
    if (logdFormatter != STAMPED_FORMATTER) {
      // swith the formatter to stamped
      logdHandler.setFormatter(stampedFormatter);
      // set indicator to stamped
      logdFormatter = STAMPED_FORMATTER;
    }
    setClassNameMethodName();
    // log message to diagnostic log only if logging level is at FINEST
    diagnosticsLogger.logp(Level.FINEST, clientClass, clientMethod, msg);
  }
  
  /**
    * Writes a debug message to the diagnostics log.
    * A standard header stamp will be included.
    * This message will only be written if the debug
    * state is set to true. The setDebug method is
    * used to toggle the debug state.
    * @assumes nothing
    * @effects a message will be written to the diagnostics log if
    * the debug state is true
    * @param  msg string message.
    */
  public void logdDebug(String msg, boolean doStamping) {
    if (doStamping) {
      logdDebug(msg);
    }
    else {
      if (logdFormatter != BRIEF_FORMATTER) {
        // switch the formatter to non-stamped
        logdHandler.setFormatter(messageOnlyFormatter);
        // set the indicator to non-stamped
        logdFormatter = BRIEF_FORMATTER;
      }
      // log message without header
      diagnosticsLogger.finest(msg);
    }
  }

  /**
   * sets the debug state to on or off
   * @assumes nothing
   * @effects debug messages will either begin getting logged or end
   * @param bool the value of the debug indicator - true for on
   */
  public void setDebug(boolean bool) {
    debugState = bool;
    // check debug state and set logging level.
    if (debugState)
      diagnosticsLogger.setLevel(Level.FINEST);
    else
      diagnosticsLogger.setLevel(Level.FINER);
  }

  /**
   * return the debug state of the logger
   * @assumes nothing
   * @effects nothing
   * @return the debug state
   */
  public boolean isDebug() {
    return debugState;
  }

  /**
   * close the log files
   * @assumes nothing
   * @effects log files will be closed
   */
  public void close() {
    // close file handlers
    if (logpHandler != null)
      logpHandler.close();
    if (logcHandler != null)
      logcHandler.close();
    if (logdHandler != null)
      logdHandler.close();
    if (logvHandler != null)
      logvHandler.close();
  }


  /**
    * Creates file handlers for the loggers.
    * @throws DLALoggingException if an IO error occurs
    */
  private void createLogp() throws DLALoggingException {
    try {
      // create a new file handler for the process log
      logpHandler = new FileHandler(logp, true);
      // set the formatter for the file handler
      logpHandler.setFormatter(stampedFormatter);
      // set the format indicator
      logpFormatter = STAMPED_FORMATTER;
      // add the handler to the logger
      processLogger.addHandler(logpHandler);
      // set the logging level
      processLogger.setLevel(Level.FINER);
    }
    catch (IOException e) {
      DLALoggingExceptionFactory eFactory =
          new DLALoggingExceptionFactory();
      DLALoggingException e2 =
          (DLALoggingException)eFactory.getException(InitializeErr);
      e2.bind(logp);
      throw e2;
    }
  }

  /**
    * Creates file handlers for the loggers.
    * @throws DLALoggingException if an IO error occurs
    */
  private void createLogc() throws DLALoggingException {
    try {
      // create a new file handler for the curator log
      logcHandler = new FileHandler(logc, true);
      // set the formatter for the file handler
      logcHandler.setFormatter(stampedFormatter);
      // set the format indicator
      logcFormatter = STAMPED_FORMATTER;
      // add the handler to the logger
      curatorLogger.addHandler(logcHandler);
      // set the logging level
      curatorLogger.setLevel(Level.FINER);
    }
    catch (IOException e) {
      DLALoggingExceptionFactory eFactory =
          new DLALoggingExceptionFactory();
      DLALoggingException e2 =
          (DLALoggingException)eFactory.getException(InitializeErr);
      e2.bind(logc);
      throw e2;
    }

  }

  /**
    * Creates file handlers for the loggers.
    * @throws DLALoggingException if an IO error occurs
    */
  private void createLogd() throws DLALoggingException {
    try {
      // create a new file handler for the curator log
      logdHandler = new FileHandler(logd, true);
      // set the formatter for the file handler
      logdHandler.setFormatter(stampedFormatter);
      // set the format indicator
      logdFormatter = STAMPED_FORMATTER;
      // add the handler to the logger
      diagnosticsLogger.addHandler(logdHandler);
      // only logging level of FINEST will log debug messages
      if (debugState)
        diagnosticsLogger.setLevel(Level.FINEST);
      else
        diagnosticsLogger.setLevel(Level.FINER);
    }
    catch (IOException e) {
      DLALoggingExceptionFactory eFactory =
          new DLALoggingExceptionFactory();
      DLALoggingException e2 =
          (DLALoggingException)eFactory.getException(InitializeErr);
      e2.bind(logd);
      throw e2;
    }

  }

  /**
    * Creates file handlers for the loggers.
    * @throws DLALoggingException if an IO error occurs
    */
  private void createLogv() throws DLALoggingException {
    try {
      // create a new file handler for the curator log
      logvHandler = new FileHandler(logv, true);
      // set the formatter for the file handler
      logvHandler.setFormatter(stampedFormatter);
      // set the format indicator
      logvFormatter = STAMPED_FORMATTER;
      // add the handler to the logger
      validationLogger.addHandler(logvHandler);
      // set the logging level
      validationLogger.setLevel(Level.FINER);
    }
    catch (IOException e) {
      DLALoggingExceptionFactory eFactory =
          new DLALoggingExceptionFactory();
      DLALoggingException e2 =
          (DLALoggingException)eFactory.getException(InitializeErr);
      e2.bind(logv);
      throw e2;
    }

  }


  /**
    * Private constructor method. Public access to instance is through the
    * methods setup() and getInstance().
    * @param config the the configuration class
    * @throws ConfigException thron if there is an error with configuration
    */

  private DLALogger(DLALoggerCfg config) throws ConfigException {
    // obtain values from config
    logp = config.getLogp();
    logc = config.getLogc();
    logv = config.getLogv();
    logd = config.getLogd();
    debugState = config.getDebug().booleanValue();
    // get a named Logger class from the java 1.4 frameworks.
    // naming these logs is for java 1.4 internal use.
    // remove the default handlers for each log.
    processLogger = Logger.getLogger(PROC_LOG);
    removeHandlers(processLogger);
    curatorLogger = Logger.getLogger(CUR_LOG);
    removeHandlers(curatorLogger);
    validationLogger = Logger.getLogger(VAL_LOG);
    removeHandlers(validationLogger);
    diagnosticsLogger = Logger.getLogger(DIAG_LOG);
    removeHandlers(diagnosticsLogger);
    // do not use any java 1.4 global handler
    setGlobalHandlerOff();
  }

  /**
   * remove global handlers from the loggers
   */
  private void setGlobalHandlerOff() {
    Handler[] handlers =
      Logger.getLogger( "" ).getHandlers();
    for ( int index = 0; index < handlers.length; index++ ) {
      handlers[index].setLevel(Level.OFF);
    }
  }

  /**
   * remove any handlers on a given logger
   * @param logger the logger for which to remove handlers
   */
  private void removeHandlers(Logger logger) {
    Handler[] handlers = logger.getHandlers();
    for ( int index = 0; index < handlers.length; index++ ) {
      logger.removeHandler(handlers[index]);
    }
  }

  /**
   * sets the instance variables clientClass and clientMethod for use in
   * message header stamping
   */
  private void setClassNameMethodName() {
    String name;
    StackTraceElement stack[] = new Throwable().getStackTrace();
    int len = stack.length;
    for (int i = 0; i < stack.length; i++) {
      name = stack[i].getClassName();
      if (!name.equals(LOGGER) && !name.startsWith(FRAMEWORKS)) {
        clientClass = name;
        clientMethod = stack[i].getMethodName();
        break;
      }
    }
  }
}
// $Log$
// Revision 1.5  2003/06/04 18:28:57  mbw
// javadoc edits
//
// Revision 1.4  2003/05/22 15:49:13  mbw
// javadocs edits
//
// Revision 1.3  2003/05/16 15:09:43  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.2  2003/05/13 18:17:51  mbw
// fixed debug and header stamping aspects
//
// Revision 1.1  2003/05/08 20:40:03  mbw
// incorporated changes from code reviews
//
// Revision 1.2  2003/04/29 19:31:42  mbw
// call to newInstance no returns a DataLoadLogger not Logger and by default header stamping is turned off when logging messages through the Logger interface
//
// Revision 1.1  2003/04/22 22:31:59  mbw
// initial version
//
// Revision 1.3.2.9  2003/04/08 21:11:16  mbw
// removed use of MessageCount class, removed checks for log creation, removed secondary constructor
//
// Revision 1.3.2.8  2003/04/03 19:23:47  mbw
// bug fix: missing calls to createLog methods in instantiation code
//
// Revision 1.3.2.7  2003/04/03 19:07:14  mbw
// changed so that log files are created at the time of instantiation
//
// Revision 1.3.2.6  2003/03/21 16:19:06  mbw
// added standard header/footer
//
/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
