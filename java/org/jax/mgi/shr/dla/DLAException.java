package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.exception.MGIException;

/**
 * @is An MGIException which represents errors occuring within a DLA
 * application.
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLAException extends MGIException {
  /**
   * @purpose constructor
   * @assumes nothing
   * @effects nothing
   * @param pMessage the message string
   * @param pDataRelated indicator for whether or not the error is
   * data related
   */
  public DLAException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }

}