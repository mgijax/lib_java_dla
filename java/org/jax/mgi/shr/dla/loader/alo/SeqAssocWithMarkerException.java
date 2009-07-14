package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates a sequence is already associated with a 
 *  marker in the database
 * Implements bindRecordString which allows runtime binding of repeated value
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class SeqAssocWithMarkerException extends MGIException {
    
    /** Creates a new instance of ALOResolvingException */
    public SeqAssocWithMarkerException() {
        super("Sequence associated with marker: ?? " , false);
    }
    public void bindRecordString(String s) {
        bind(s);
    }
    
}

