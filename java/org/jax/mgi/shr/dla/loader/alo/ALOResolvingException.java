package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates the type and value of an attribute
 * which cannot be resolved
 * Implements bindRecordString which allows runtime binding of repeated value
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class ALOResolvingException extends MGIException {
    
    /** Creates a new instance of ALOResolvingException */
    public ALOResolvingException() {
	super("Cannot resolve attribute type(s)/value(s) ??", false);
    }
    public void bindRecordString(String s) {
	bind(s);
    }   
}
