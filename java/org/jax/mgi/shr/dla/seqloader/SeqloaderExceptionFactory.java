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
    "org.jax.mgi.dbs.mgd.QCErr";
static {
  exceptionsMap.put(QCErr, new SeqloaderException(
      "Could not add a new qc item to the qc reporting table named ??",
      false));
}

}
// $Log