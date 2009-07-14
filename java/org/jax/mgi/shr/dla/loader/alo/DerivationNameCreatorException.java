package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates a missing attributes when creating a Derivation
 * Name 
 * Implements bindRecordString which allows runtime binding of repeated value
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class DerivationNameCreatorException extends MGIException {
    
    /** Creates a new instance of DerivationNameCreatorException */
    public DerivationNameCreatorException() {
        super("??", false);
    }
    public void bindRecordString(String s) {
        bind(s);
    }
    
}

