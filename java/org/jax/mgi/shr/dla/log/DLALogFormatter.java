// $Header$
// $Name$

package org.jax.mgi.shr.dla.log;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.Date;
import java.text.MessageFormat;

/**
 * @is A Formatter object that formats the content of a message prepended
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

// $Log$
// Revision 1.1.2.1  2004/11/05 16:18:05  mbw
// classes were renamed or moved as part of large refactoring effort (see tr604)
//
// Revision 1.6  2004/07/13 14:57:19  mbw
// bug fix: fixed timestamp to remove space and quotes
//
// Revision 1.5  2004/06/30 19:45:39  mbw
// changed timestamping format of log entries to match job stream format
//
// Revision 1.4  2003/10/28 21:00:17  mbw
// fixed imports
//
// Revision 1.3  2003/05/22 15:49:12  mbw
// javadocs edits
//
// Revision 1.2  2003/05/08 20:40:02  mbw
// incorporated changes from code reviews
//
// Revision 1.1  2003/04/22 22:31:58  mbw
// initial version
//
// Revision 1.2.2.2  2003/03/21 16:18:41  mbw
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
