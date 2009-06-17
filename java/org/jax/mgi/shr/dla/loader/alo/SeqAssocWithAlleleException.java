package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates a sequence is already associated with an 
 *  allele in the database
 * Implements bindRecordString which allows runtime binding of repeated value
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class SeqAssocWithAlleleException extends MGIException {
    
    /** Creates a new instance of SeqAssocWithAlleleException */
    public SeqAssocWithAlleleException() {
	super("Sequence associated with allele: ?? " , false);
    }
    public void bindRecordString(String s) {
	bind(s);
    }
    
}

