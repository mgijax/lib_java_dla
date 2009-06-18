package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.shr.exception.MGIException;
/**
 * An MGIException which indicates a sequence tag id which should have a vector
 * end component, but does not.
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class NoVectorEndException extends MGIException{
    
    /** Creates a new instance of NoVectorEndException */
    public NoVectorEndException() {
	super("Sequence Tag Id does not have a vector end component: ??", false);
    }
    public void bindRecordString(String s) {
	bind(s);
    }
}
