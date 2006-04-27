package org.jax.mgi.shr.dla.loader.coord;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which represents exceptions occuring while processing
 * coordinates
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class CoordloaderException extends MGIException {
  public CoordloaderException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }
}
