// $Header
// $Name
package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * An ExceptionFactory for SeqloaderExceptions
 * @has a hashmap of predefined SeqloaderExceptions stored by a name key
 * @does looks up SeqloaderExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class SeqloaderExceptionFactory extends ExceptionFactory {

  public SeqloaderExceptionFactory() {
  }

  /**
   * Repeat file IOException
   */
  public static final String RepeatFileIOException =
      "org.jax.mgi.shr.dla.seqloader.RepeatFileIOException";
  static {
    exceptionsMap.put(RepeatFileIOException, new SeqloaderException(
        "Repeat file IOException", false));
  }

  /**
   * could not add qc dao object to stream
   */
  public static final String QCErr =
      "org.jax.mgi.shr.dla.seqloader.QCErr";
  static {
    exceptionsMap.put(QCErr, new SeqloaderException(
        "Could not add a new qc item to the qc reporting table named ??",
        false));
  }

  /**
   * database error querying for a sequence key
   */
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
  public static final String CreateSequenceErr =
      "org.jax.mgi.shr.dla.seqloader.CreateSequenceErr";
  static {
    exceptionsMap.put(CreateSequenceErr, new SeqloaderException(
        "Error creating a Sequence object for ??",
        false));
  }

  /**
   * Error creating a Primary ACC_AccessionDAO object
   */
  public static final String CreatePrimaryAccessionErr =
      "org.jax.mgi.shr.dla.seqloader.CreatePrimaryAccessionErr";
  static {
    exceptionsMap.put(CreatePrimaryAccessionErr, new SeqloaderException(
        "Error creating Primary ACC_AccessionDAO object for ??",
        false));
  }

  /**
   * Error creating a Secondary ACC_AccessionDAO object
   */
  public static final String CreateSecondaryAccessionErr =
      "org.jax.mgi.shr.dla.seqloader.CreateSecondaryAccessionErr";
  static {
    exceptionsMap.put(CreateSecondaryAccessionErr, new SeqloaderException(
        "Error creating Secondary ACC_AccessionDAO object for ??",
        false));
  }

  /**
   * Error creating a MGI_Reference_AssocDAO objects
   */
  public static final String CreateRefAssocErr =
      "org.jax.mgi.shr.dla.seqloader.CreateRefAssocErr";
  static {
    exceptionsMap.put(CreateRefAssocErr, new SeqloaderException(
        "Error creating MGI_Reference_AssocDAO objects for ??",
        false));
  }

  /**
   * Error creating a SEQ_Source_AssocDAO object
   */
  public static final String CreateSrcAssocErr =
      "org.jax.mgi.shr.dla.seqloader.CreateSrcAssocErr";
  static {
    exceptionsMap.put(CreateSrcAssocErr, new SeqloaderException(
        "Error creating SEQ_Source_AssocDAO objects for ??",
        false));
  }

  /**
   * Database Error sending Sequence object to stream
   */
  public static final String SequenceSendToStreamErr =
      "org.jax.mgi.shr.dla.seqloader.SequenceSendToStreamErr";
  static {
    exceptionsMap.put(SequenceSendToStreamErr, new SeqloaderException(
        "Database Error sending Sequence object to stream for ??",
        false));
  }

  /**
   * Error detecting sequence event
   */
  public static final String EventDetectionErr =
      "org.jax.mgi.shr.dla.seqloader.EventDetectionErr";
  static {
    exceptionsMap.put(EventDetectionErr, new SeqloaderException(
        "Error detecting event for sequence ??",
        false));
  }

  /**
   * Error processing add event
   */
  public static final String ProcessAddErr =
      "org.jax.mgi.shr.dla.seqloader.ProcessAddErr";
  static {
    exceptionsMap.put(ProcessAddErr, new SeqloaderException(
        "Error processing add event for ??",
        false));
  }

  /**
   * Error processing update event
   */
  public static final String ProcessUpdateErr =
      "org.jax.mgi.shr.dla.seqloader.ProcessUpdateErr";
  static {
    exceptionsMap.put(ProcessUpdateErr, new SeqloaderException(
        "Error processing update event for ??",
        false));
  }

  /**
   * Error processing dummy event
   */
  public static final String ProcessDummyErr =
      "org.jax.mgi.shr.dla.seqloader.ProcessDummyErr";
  static {
    exceptionsMap.put(ProcessDummyErr, new SeqloaderException(
        "Error processing dummy event for ??",
        false));
  }

  /**
   * Error deleting sequences
   */
  public static final String ProcessDeletesErr =
      "org.jax.mgi.shr.dla.seqloader.ProcessDeletesErr";
  static {
    exceptionsMap.put(ProcessDeletesErr, new SeqloaderException(
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
