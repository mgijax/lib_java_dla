
// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.exception.MGIException;

public class ChangedLibraryException extends MGIException {
    public ChangedLibraryException() {
        super("This sequence has a different Raw Library than the existing MGI sequence", false);
    }
 }
// $Log