package org.jax.mgi.shr.dla.loader.coord;

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
