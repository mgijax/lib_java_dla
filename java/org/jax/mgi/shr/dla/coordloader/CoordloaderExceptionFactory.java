// $Header
// $Name

package org.jax.mgi.shr.dla.coordloader;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * An ExceptionFactory for SeqloaderExceptions
 * @has a hashmap of predefined SeqloaderExceptions stored by a name key
 * @does looks up SeqloaderExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class CoordloaderExceptionFactory extends ExceptionFactory {

  public CoordloaderExceptionFactory() {
  }

  /**
   * Error deleting coordinates
   */
  public static final String ProcessDeletesErr =
      "org.jax.mgi.shr.dla.coordloader.ProcessDeletesErr";
  static {
    exceptionsMap.put(ProcessDeletesErr, new CoordloaderException(
        "Error deleting sequences for the load",
        false));
  }

}
// $Log
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
* Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
