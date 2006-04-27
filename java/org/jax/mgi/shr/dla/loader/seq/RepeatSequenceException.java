package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates a repeated sequence was found in the input
 * sequences
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class RepeatSequenceException extends MGIException {
    public RepeatSequenceException() {
        super("This sequence is repeated in the input", false);
    }
 }
