package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException thrown when there is not object in MGI for the feature
 * e.g. Sequence or Marker object
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class FeatureObjectNotInMGIException extends MGIException {
    
    /** Creates a new instance of FeatureObjectNotInMGIException */
    public FeatureObjectNotInMGIException(String id) {
	super("There is no MGI Object for this Feature ID: " + id, false);
    }
    
}
