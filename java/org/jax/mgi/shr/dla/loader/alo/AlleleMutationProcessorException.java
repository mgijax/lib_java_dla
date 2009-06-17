package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates an allele exist in the database without
 * a mutation or the allele does not have the mutation(s) we are processing
 * Implements bindRecordString which allows runtime binding of repeated value
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class AlleleMutationProcessorException extends MGIException {
    
    /** Creates a new instance of AlleleMutationProcessorException */
    public AlleleMutationProcessorException() {
	super("Allele Key: ??", false);
    }
    public void bindRecordString(String s) {
	bind(s);
    }
    
}
