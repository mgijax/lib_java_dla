package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates a Derivation cannot be found in the database
 * Implements bindRecordString which allows runtime binding of repeated value
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class DerivationProcessorException extends MGIException {
    
    /** Creates a new instance of DerivationProcessorException */
    public DerivationProcessorException() {
        super("Cannot find Derivation in the database for raw cell line library: " +
	    "??", false);
    }
    public void bindRecordString(String s) {
        bind(s);
    }
    
}

