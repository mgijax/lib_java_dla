// $Header$
// $Name$

package org.jax.mgi.shr.dla;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.Enumeration;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.lang.Throwable;

import org.jax.mgi.shr.config.DLALoggerCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.log.MessageOnlyFormatter;
import org.jax.mgi.shr.log.LoggingException;
import org.jax.mgi.shr.log.LoggingExceptionFactory;

/**
 * <p>IS: an object that logs messages of various severity levels to a set of
 * predetermined log files in terms with the DLA logging standards.</p>
 * <p>HAS: log files, message handlers, message formatters and a configurator.
 * </p>
 * <p>DOES: formats and writes messages to the set of log files defined in
 * the DLA.</p>
 * <p>Description:
 * The DLA standards names four log files for various types of reporting.
 * These include the process log, curator log, sql validation log, and the
 * diagnostics log. See the DLA Standards document for further information
 * regarding the use of these logs.</p>
 * <p>
 * Log entries can be written in two ways. First, an entry can be
 * posted with a standard header stamp, which includes the date, class name,
 * severity level, and method name. A set of alternative methods are provided
 * which do not include the standard header stamps when posting to the logs.
 * These methods are provided as a way to do multiline information reporting
 * without cluttering the output with many header stamps. </p>
 * <p>
 * The logger allows different severity levels of messages for each log
 * file. The process and curator logs are provided with only one severity
 * level, namely informational. The validation logs are provided with
 * informational, warning and error levels, wheareas the diagnostics logs are
 * provided with debug, error, informational and warning levels. Debug
 * messages can be turned off by calling the setDebug method with a value of
 * true.</p>
 * <p>
 * The DataLoadLogger class is not synchronized and is not safe to use in
 * a multithreaded environment.
 * </p>
 * <p>Company: The Jackson Lab</p>
 * @author M Walker
 * @version 1.0
 */

public class DataLoadLogger implements org.jax.mgi.shr.log.Logger {

  // the Singleton instance
  private static DataLoadLogger instance = null;
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
  private static final String LOGGER =
      "org.jax.mgi.logging.DataLoadLogger";
  private static final String FRAMEWORKS =
      "java.util.logging";
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
      LoggingExceptionFactory.InitializeErr;
  private static final String ConfigurationErr =
      LoggingExceptionFactory.ConfigurationErr;



 /**
  * <p>Purpose: public constructor method to obtain a singleton instance of
  * DataLoadLogger.</p>
  * <p>Assumes: nothing</p>
  * <p>Effects: a new instance of the DataLoadLogger will cretaed if it
  * doesnt already exist.</p>
  * @return singleton instance of DataLoadLogger
  */

  public static synchronized org.jax.mgi.shr.log.Logger getInstance()
  throws LoggingException {
    if (instance == null) {
      try {
        instance = new DataLoadLogger(new DLALoggerCfg());
      }
      catch (ConfigException e) {
        LoggingExceptionFactory eFactory = new LoggingExceptionFactory();
        LoggingException e2 =
            (LoggingException)eFactory.getException(ConfigurationErr, e);

      }
      instance.createLogp();
      instance.createLogv();
      instance.createLogc();
      instance.createLogd();
    }
    return instance;
  }

  /**
   * <p>Purpose: logs an informational message with a standard header
   * stamp to the diagnostics log</p>
   * <p>Assumes: nothing </p>
   * <p>Effects: a message will get logged to the diagnostics log</p>
   * @param message the message to log
   */
  public void logInfo(String message) {
    logd(message, true);
  }

  /**
   * <p>Purpose: logs an error message with a standard header stamp to the
   * diagnostics log</p>
   * <p>Assumes: nothing </p>
   * <p>Effects: an error message will get logged to the diagnostics log</p>
   * @param message the message to log
   */
  public void logError(String message) {
    logdErr(message);
  }

  /**
   * <p>Purpose: logs a debug message with a standard header stamp to the
   * diagnostics log</p>
   * <p>Assumes: nothing </p>
   * <p>Effects: a debug will get logged to the diagnostics log if the
   * debug state is true</p>
   * @param message the message to log
   */
  public void logDebug(String message) {
    logdDebug(message);
  }



  /**
    * <p>Purpose: Writes an informational message to the process log.
    * A standard header stamp will be included.</p>
    * <p>Assumes: nothing</p>
    * <p>Effects: a message will be written to the process log
    * @param  msg string message.
    * @param doStamping true if the message should be time stamped
    */
  public void logp(String msg, boolean doStamping) {
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
    * <p>Purpose: Writes an informational message to the curator log.
    * A standard header stamp will be included.</p>
    * <p>Assumes: nothing</p>
    * <p>Effects: a message will be written to the curator log
    * @param  msg string message.
    * @param doStamping true if the message should be time stamped
    */
  public void logc(String msg, boolean doStamping) {
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
    * <p>Purpose: Writes an informational message to the validation log.
    * A standard header stamp will be included.</p>
    * <p>Assumes: nothing</p>
    * <p>Effects: a message will be written to the validation log
    * @param  msg string message.
    * @param doStamping true if the message should be time stamped
    */
  public void logv(String msg, boolean doStamping) {
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
    * <p>Purpose: Writes an informational message to the diagnostic log.
    * A standard header stamp will be included.</p>
    * <p>Assumes: nothing</p>
    * <p>Effects: a message will be written to the diagnostics log
    * @param  msg string message.
    * @param doStamping true if the message should be time stamped
    */
  public void logd(String msg, boolean doStamping) {
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
    * <p>Purpose: Writes an error message to the validation log.
    * A standard header stamp will be included.</p>
    * <p>Assumes: nothing</p>
    * <p>Effects: a message will be written to the validation log
    * @param  msg string message.
    */
  public void logvErr(String msg) {
    setClassNameMethodName();
    // log the message with header stamp
    validationLogger.logp(Level.SEVERE, clientClass, clientMethod, msg);
    clientClass = null;
    clientMethod = null;
  }

  /**
    * <p>Purpose: Writes an warning message to the validation log.
    * A standard header stamp will be included.</p>
    * <p>Assumes: nothing</p>
    * <p>Effects: a message will be written to the validation log
    * @param  msg string message.
    */
  public void logvWarn(String msg) {
    setClassNameMethodName();
    // log the message with header stamp
    validationLogger.logp(Level.WARNING, clientClass, clientMethod, msg);
    clientClass = null;
    clientMethod = null;
  }

  /**
    * <p>Purpose: Writes a warning message to the diagnostics log.
    * A standard header stamp will be included.</p>
    * <p>Assumes: nothing</p>
    * <p>Effects: a message will be written to the diagnostics log
    * @param  msg string message.
    */
  public void logdWarn(String msg) {
    setClassNameMethodName();
    // log the message with header stamp
    diagnosticsLogger.logp(Level.WARNING, clientClass, clientMethod, msg);
    clientClass = null;
    clientMethod = null;
  }

  /**
    * <p>Purpose: Writes an error message to the diagnostics log.
    * A standard header stamp will be included.</p>
    * <p>Assumes: nothing</p>
    * <p>Effects: a message will be written to the diagnostics log
    * @param  msg string message.
    */
  public void logdErr(String msg) {
    setClassNameMethodName();
    // log the message with header stamp
    diagnosticsLogger.logp(Level.SEVERE, clientClass, clientMethod, msg);
    clientClass = null;
    clientMethod = null;
  }

  /**
    * <p>Purpose: Writes a debug message to the diagnostics log.
    * A standard header stamp will be included.
    * This message will only be written if the debug
    * state is set to true. The setDebug method is
    * used to toggle the debug state.</p>
    * <p>Assumes: nothing</p>
    * <p>Effects: a message will be written to the diagnostics log if
    * the debug state is true
    * @param  msg string message.
    */
  public void logdDebug(String msg) {
    setClassNameMethodName();
    // log message to diagnostic log only if logging level is at FINEST
    diagnosticsLogger.logp(Level.FINEST, clientClass, clientMethod, msg);
    clientClass = null;
    clientMethod = null;
  }

  /**
   * <p>Purpose: sets the debug state to on or off</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: debug messages will either begin getting logged or end</p>
   * @param bool
   */
  public void setDebug(boolean bool) {
    debugState = bool;
    // check debug state and set logging level.
    if (debugState)
      diagnosticsLogger.setLevel(Level.FINEST);
    else
      diagnosticsLogger.setLevel(Level.FINE);
  }

  /**
   * <p>Purpose: return the debug state of the logger</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: nothing</p>
   * @return the debug state
   */
  public boolean isDebug() {
    return debugState;
  }

  /**
   * <p>Purpose: close the log files</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: log files will be closed</p>
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
    * @throws LoggingException if an IO error occurs
    */
  private void createLogp() throws LoggingException {
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
      LoggingExceptionFactory eFactory = new LoggingExceptionFactory();
      LoggingException e2 =
          (LoggingException)eFactory.getException(InitializeErr);
      e2.bind(logp);
      throw e2;
    }
  }

  /**
    * Creates file handlers for the loggers.
    * @throws LoggingException if an IO error occurs
    */
  private void createLogc() throws LoggingException {
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
      LoggingExceptionFactory eFactory = new LoggingExceptionFactory();
      LoggingException e2 =
          (LoggingException)eFactory.getException(InitializeErr);
      e2.bind(logc);
      throw e2;
    }

  }

  /**
    * Creates file handlers for the loggers.
    * @throws LoggingException if an IO error occurs
    */
  private void createLogd() throws LoggingException {
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
        diagnosticsLogger.setLevel(Level.FINE);
    }
    catch (IOException e) {
      LoggingExceptionFactory eFactory = new LoggingExceptionFactory();
      LoggingException e2 =
          (LoggingException)eFactory.getException(InitializeErr);
      e2.bind(logd);
      throw e2;
    }

  }

  /**
    * Creates file handlers for the loggers.
    * @throws LoggingException if an IO error occurs
    */
  private void createLogv() throws LoggingException {
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
      LoggingExceptionFactory eFactory = new LoggingExceptionFactory();
      LoggingException e2 =
          (LoggingException)eFactory.getException(InitializeErr);
      e2.bind(logv);
      throw e2;
    }

  }


  /**
    * Private constructor method. Public access to instance is through the
    * methods setup() and getInstance().
    */

  private DataLoadLogger(DLALoggerCfg config) {
    // obtain values from config
    logp = config.getLogp();
    logc = config.getLogc();
    logv = config.getLogv();
    logd = config.getLogd();
    debugState = config.getDebug();
    // get a named Logger class from the java 1.4 frameworks
    // and remove the default handlers for each log
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


  private void setGlobalHandlerOff() {
    Handler[] handlers =
      Logger.getLogger( "" ).getHandlers();
    for ( int index = 0; index < handlers.length; index++ ) {
      handlers[index].setLevel(Level.OFF);
    }
  }

  private void removeHandlers(Logger logger) {
    Handler[] handlers = logger.getHandlers();
    for ( int index = 0; index < handlers.length; index++ ) {
      logger.removeHandler(handlers[index]);
    }
  }

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
