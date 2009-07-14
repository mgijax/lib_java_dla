package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.shr.exception.MGIException;
/**
 * An MGIException which indicates a sequence is not in the database
 * i.e. no SEQ_Sequence object
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class SequenceNotInDatabaseException extends MGIException{
    
    /** Creates a new instance of SequenceNotInDatabaseException */
    public SequenceNotInDatabaseException() {
        super("SeqID does not have a sequence object in the database: ??", false);
    }
    public void bindRecordString(String s) {
        bind(s);
    }
}

