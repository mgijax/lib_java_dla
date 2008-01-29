package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException thrown when an incoming Feature is different than that
 *  in MGI
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class FeatureDifferentInMGIException extends MGIException {
    
    /** Creates a new instance of FeatureDifferentInMGIException */
    public FeatureDifferentInMGIException(String feature) {
	super("The incoming feature is different than that in MGI for object ID: " + feature, false);
    }
    
}
