// $Header$
// $Name$

package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.config.ConfigException;

/**
 * <p>IS: An object which handles exceptions of type MGIException</p>
 * <p>HAS: An instance of a logger and static variables for totaling system
 * warnings and errors</p>
 * <p>DOES: provides a standard way to handle caught exceptions which include
 * functionality for logging and error/warning count totaling</p>
 * <p>Company: Jackson Laboratory</p>
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
      System.err.print("Cannot obtain a Logger. This is really bad news." +
                       "Exiting stage left");
      System.exit(DLASystemExit.FATAL_ERROR);
    }
  }
  private static int errorCount = 0;
  private static int dataErrorCount = 0;


  /**
   * <p>Purpose: provides a standard method for handling exceptions</p>
   * <p>Assumes: this method is not being called concurrently</p>
   * <p>Effects: a message is written to a log file or files depending on the
   * type of exception and the warning count or error count is updated
   * depending on the attributes of exception.</p>
   * @param e an exception that implements LoggableException
   */
  public static void handleException(DLAException e) {
    logger.logError(e.toString());
    updateCounts(e);
  }

  /**
   * <p>Purpose: accesses the error count</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: nothing</p>
   * @return the count of errors that have occured
   */
  public static int getErrorCount() {
    return errorCount;
  }

  /**
   * <p>Purpose: accesses the data error count</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: nothing</p>
   * @return the count of errors which are data related that have occured
   */
  public static int getDataErrorCount() {
    return dataErrorCount;
  }


  /**
   * <p>Purpose: updates warning or error counts based on the exception</p>
   * <p>Assumes: this method is not being called concurrently</p>
   * <p>Effects: the warning count or error count is updated depending on the
   * attributes of exception.</p>
   * @param e an exception that implements LoggableException
   */
  private static void updateCounts(MGIException e) {
      errorCount++;
      if (e.isDataRelated())
        dataErrorCount++;
  }

}
// $Log$
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
