package org.jax.mgi.shr.dla.log;

import org.jax.mgi.shr.exception.MGIException;

/**
 * @is An MGIException which represents exceptions occuring during
 * logging.
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLALoggingException extends MGIException {
  public DLALoggingException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }
}
