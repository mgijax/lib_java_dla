package org.jax.mgi.shr.dla.log;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.Date;
import java.text.MessageFormat;

/**
 * A Formatter object that formats the content of a message prepended
 * by a header stamp.
 * @has nothing.
 * @does Formats the content of a message record along with a standard
 * header stamp. An example illustrating the format is as follows:<br>
 * Tue Jul 13 10:14:56 EDT 2004 org.jax.mgi.log.TestDataLoadLogger testMethod<br>
 * INFO: this is a test message
 * @company The Jackson Lab
 * @author M Walker
 * @version 1.0
 */

public class DLALogFormatter extends Formatter{
  private static final String lineSeparator =
      System.getProperty( "line.separator");
  private static Date date = new Date();
  private static Object args[] = { date };
  private static MessageFormat format =
      new MessageFormat("{0, date, EEE MMM dd HH:mm:ss z yyyy}");
  private static final String LOGGER =
      "org.jax.mgi.logging.DataLoadLogger";
  private static final String FRAMEWORKS =
      "java.util.logging";
  private static final String THIS_CLASS =
      "org.jax.mgi.logging.DLAFormatter";
  private static Level level = null;

  /**
   * Formats the content of the LogRecord with a standard message header
   * defined by the DLA standards.
   * @param record the log record
   * @return the formatted message
   */
  synchronized public String format(LogRecord record) {
    StringBuffer buff = new StringBuffer();
    StringBuffer dateString = new StringBuffer();
    date.setTime(record.getMillis());
    format.format( args, dateString, null );
    dateString = dateString.deleteCharAt(0);
    buff.append(lineSeparator);
    buff.append(dateString);
    buff.append(" ");
    buff.append(record.getSourceClassName());
    buff.append(" ");
    buff.append(record.getSourceMethodName());
    buff.append(lineSeparator);
    level = record.getLevel();
    if (level == Level.FINEST)
      buff.append("DEBUG: ");
    else if (level == Level.WARNING)
      buff.append("WARNING: ");
    else if (level == Level.SEVERE)
      buff.append("ERROR: ");
    else
      buff.append("INFO: ");
    buff.append(formatMessage(record));
    buff.append(lineSeparator);
    buff.append(lineSeparator);
    return buff.toString();
  }
/*
  private void callerID() {
    String name;
    StackTraceElement stack[] = new Throwable().getStackTrace();
    int len = stack.length;
    for (int i = 0; i < stack.length; i++) {
      name = stack[i].getClassName();
      if (!name.equals(LOGGER) && !name.equals(THIS_CLASS)
                               && !name.startsWith(FRAMEWORKS)) {
        callingClass.append(name);
        callingMethod.append(stack[i].getMethodName());
        break;
      }
    }
  }
*/
}
