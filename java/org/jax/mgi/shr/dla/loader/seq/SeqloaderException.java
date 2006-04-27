package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which represents exceptions occuring while processing
 * sequences
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqloaderException extends MGIException {
  public SeqloaderException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }
}
