package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.exception.MGIException;

/**
 * <p>IS: An MGIException which represents exceptions occuring during
 * logging.</p>
 * <p>HAS: nothing</p>
 * <p>DOES: nothing</p>
 * <p>Company: Jackson Laboratory</p>
 * @author M Walker
 * @version 1.0
 */

public class LoggingException extends MGIException {
  public LoggingException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }
}
