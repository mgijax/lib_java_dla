// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * @is An ExceptionFactory.
 * @has a hashmap of predefined DLAExceptions stored by a name key
 * @does looks up SeqloaderExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class SeqloaderExceptionFactory extends ExceptionFactory {

  public SeqloaderExceptionFactory() {
  }

  /**
   * Found a repeated sequence in the input
   */
  /*
  public static final String RepeatSequenceException =
      "org.jax.mgi.shr.dla.seqloader.RepeatSequenceException";
  static {
    exceptionsMap.put(RepeatSequenceException, new SeqloaderException(
        "This sequence is repeated in the input", false));
  }
  */
  /**
   * Cannot resolve one or more SequenceRawAttributes
   */
  /*
  public static final String SequenceResolverException =
      "org.jax.mgi.shr.dla.seqloader.SequenceResolverException";
  static {
    exceptionsMap.put(SequenceResolverException, new SeqloaderException(
        "Cannot resolve one or more SequenceRawAttributes", true));
  }
  */
 public static final String RepeatFileIOException =
     "org.jax.mgi.shr.dla.seqloader.RepeatFileIOException";
 static {
   exceptionsMap.put(RepeatFileIOException, new SeqloaderException(
        "Repeat file IOException", false));
 }
}
// $Log