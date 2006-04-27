package org.jax.mgi.dbs.mgd.loads.Seq;

import org.jax.mgi.shr.exception.MGIException;

public class SequenceResolverException extends MGIException {
    public SequenceResolverException(Exception e) {
        super("Cannot resolve one or more SequenceRawAttributes attributes", true);
        super.setParent(e);
    }
 }
