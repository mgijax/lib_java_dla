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
   * database error querying for a sequence key
   */
/*
  public static final String SeqKeyQueryErr =
      "org.jax.mgi.shr.dla.seqloader.SeqKeyQueryErr";
  static {
    exceptionsMap.put(SeqKeyQueryErr, new SeqloaderException(
        "Error querying for sequence key for ??",
        false));
  }
  /**
    * database error querying for a Sequence
    */
/*
   public static final String SeqQueryErr =
       "org.jax.mgi.shr.dla.seqloader.SeqQueryErr";
   static {
     exceptionsMap.put(SeqQueryErr, new SeqloaderException(
         "Error querying for the Sequence object for ??",
         false));
   }

  /**
   * Error resolving a sequence
   */
/*
  public static final String SeqResolverErr =
      "org.jax.mgi.shr.dla.seqloader.SeqResolverErr";
  static {
    exceptionsMap.put(SeqResolverErr, new SeqloaderException(
        "Error resolving Sequence ??",
        false));
  }

  /**
   * Error creating a Sequence object
   */
/*
  public static final String CreateSequenceErr =
      "org.jax.mgi.shr.dla.seqloader.CreateSequenceErr";
  static {
    exceptionsMap.put(CreateSequenceErr, new SeqloaderException(
        "Error creating a Sequence object for ??",
        false));
  }


  /**
   * Database Error sending Sequence object to stream
   */
/*
  public static final String SequenceSendToStreamErr =
      "org.jax.mgi.shr.dla.seqloader.SequenceSendToStreamErr";
  static {
    exceptionsMap.put(SequenceSendToStreamErr, new SeqloaderException(
        "Database Error sending Sequence object to stream for ??",
        false));
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
