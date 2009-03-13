package org.jax.mgi.shr.dla.loader.coord;

import org.jax.mgi.shr.exception.MGIException;
/**
 * An MGIException which indicates a coordinate is already in the database
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class CoordInDatabaseException extends MGIException{
    
    /** Creates a new instance of NoVectorEndException */
    public CoordInDatabaseException() {
	super("Coordinate in database for objectID: ??", false);
    }
    public void bindRecordString(String s) {
	bind(s);
    }
}

