// $Header$
// $Name$

package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.exception.MGIException;

/**
 * @is An object which handles exceptions of type MGIException
 * @has An instance of a logger and static variables for totaling system
 * warnings and errors
 * @does provides a standard way to handle caught exceptions which include
 * functionality for logging and error/warning count totaling
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLAExceptionHandler {

  private static DLALogger logger = null;
  static {
    try {
      logger = DLALogger.getInstance();
    }
    catch (DLALoggingException e) {
      System.err.print("Cannot obtain a Logger for the following reason. " +
                       "This is really bad news. " +
                       "Exiting program unequivocally\n" + e.toString());
      System.exit(DLASystemExit.FATAL_ERROR);
    }
  }
  private static int errorCount = 0;
  private static int dataErrorCount = 0;


  /**
   * provides a standard method for handling exceptions
   * @assumes this method is not being called concurrently
   * @effects a message is written to a log file or files depending on the
   * type of exception and the warning count or error count is updated
   * depending on the attributes of exception.
   * @param e an exception that implements LoggableException
   */
  public static void handleException(DLAException e) {
    logger.logError(e.toString());
    updateCounts(e);
  }

  /**
   * accesses the error count
   * @assumes nothing
   * @effects nothing
   * @return the count of errors that have occured
   */
  public static int getErrorCount() {
    return errorCount;
  }

  /**
   * accesses the data error count
   * @assumes nothing
   * @effects nothing
   * @return the count of errors which are data related that have occured
   */
  public static int getDataErrorCount() {
    return dataErrorCount;
  }


  /**
   * updates warning or error counts based on the exception
   * @assumes this method is not being called concurrently
   * @effects the warning count or error count is updated depending on the
   * attributes of exception.
   * @param e an exception that implements LoggableException
   */
  private static void updateCounts(MGIException e) {
      errorCount++;
      if (e.isDataRelated())
        dataErrorCount++;
  }

}
// $Log$
// Revision 1.6  2003/10/28 20:56:33  mbw
// fixed imports
//
// Revision 1.5  2003/06/04 18:28:56  mbw
// javadoc edits
//
// Revision 1.4  2003/05/22 15:49:11  mbw
// javadocs edits
//
// Revision 1.3  2003/05/13 18:18:24  mbw
// modified exit message
//
// Revision 1.2  2003/05/08 20:40:02  mbw
// incorporated changes from code reviews
//
// Revision 1.1  2003/04/22 22:31:57  mbw
// initial version
//
// Revision 1.4  2003/04/01 21:53:34  mbw
// resolved impact of class name change
//
// Revision 1.3  2003/03/21 15:51:34  mbw
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
