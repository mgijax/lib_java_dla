package org.jax.mgi.shr.dla.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A Formatter object that formats the content of a log message
 * without prepending a standard header stamp.
 * @has A message record.
 * @does Formats a message record without a standard header
 * stamp.
 * @company The Jackson Lab
 * @author M Walker
 * @version 1.0
 */


public class MessageOnlyFormatter extends Formatter{
  private static String lineSeparator =
      System.getProperty( "line.separator" );

  /**
   * Formats the log record with just the message content without inluding
   * any header information.
   * @param record the log record
   * @return the formatted message.
   */
  public String format(LogRecord record) {
    return formatMessage(record) + lineSeparator;
  }
}
