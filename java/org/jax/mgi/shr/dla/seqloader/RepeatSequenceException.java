// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.exception.MGIException;

public class RepeatSequenceException extends MGIException {
    public RepeatSequenceException() {
        super("This sequence is repeated in the input", false);
    }
 }
// $Log