// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.exception.MGIException;

public class SequenceResolverException extends MGIException {
    public SequenceResolverException(Exception e) {
        super("Cannot resolve one or more SequenceRawAttributes attributes", true);
        super.setParent(e);
    }
 }
// $Log