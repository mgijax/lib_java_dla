package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException thrown when a Coordinate Feature is already in MGI
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class FeatureInMGIException extends MGIException {
    
    /** Creates a new instance of FeatureInMGIException */
    public FeatureInMGIException(String feature) {
	super("There is already a Feature in MGI for object ID: " + feature, false);
    }
    
}