package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.shr.exception.MGIException;

/**
 * @is A MGIException which represents an error occuring during a
 * process involving classes related to molecular sources
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company
 * @author M Walker
 * @version 1.0
 */

public class MSException extends MGIException {
  public MSException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }
}
