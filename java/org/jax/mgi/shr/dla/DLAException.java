package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.exception.MGIException;

/**
 * <p>IS: An MGIException which represents errors occuring within a DLA
 * application.</p>
 * <p>HAS: an exception message, a data related indicator and a parent
 * exception which can be null.</p>
 * <p>DOES: nothing</p>
 * <p>Company: Jackson Laboratory</p>
 * @author M Walker
 * @version 1.0
 */

public class DLAException extends MGIException {
  /**
   * <p>Purpose: constructor</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: nothing</p>
   * @param pMessage the message string
   * @param pDataRelated indicator for whether or not the error is
   * data related
   */
  public DLAException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }

}