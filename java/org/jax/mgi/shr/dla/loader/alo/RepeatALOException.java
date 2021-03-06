package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates a repeated ALO found in the input
 * Implements bindRecordString which allows runtime binding of repeated value
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class RepeatALOException extends MGIException {
    
    /** Creates a new instance of RepeatCellLineIDException */
    public RepeatALOException() {
        super("This ALO is repeated in the input: ??", false);
    }
    public void bindRecordString(String s) {
        bind(s);
    }
    
}
