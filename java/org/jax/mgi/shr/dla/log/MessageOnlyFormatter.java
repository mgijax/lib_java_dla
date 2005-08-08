// $Header$
// $Name$
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

// $Log$
// Revision 1.3  2005/08/05 18:57:30  mbw
// merged code from tr6086
//
// Revision 1.2.6.1  2004/12/09 18:08:51  mbw
// fixed javadocs warnings
//
// Revision 1.2.2.1  2005/08/02 16:24:28  mbw
// merged branch tr6086
//
// Revision 1.2  2004/12/07 20:10:49  mbw
// merged tr6047 onto the trunk
//
// Revision 1.1.2.1  2004/11/05 16:18:07  mbw
// classes were renamed or moved as part of large refactoring effort (see tr604)
//
// Revision 1.5  2003/10/28 20:56:55  mbw
// fixed import statements
//
// Revision 1.4  2003/05/22 15:49:14  mbw
// javadocs edits
//
// Revision 1.2  2003/04/29 19:30:30  mbw
// changed package name
//
// Revision 1.1  2003/04/24 20:47:11  mbw
// initial version
//
// Revision 1.3.2.2  2003/03/21 16:18:20  mbw
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
