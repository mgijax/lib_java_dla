
// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.exception.MGIException;

public class ChangedOrganismException extends MGIException {
    public ChangedOrganismException() {
        super("This sequence has a different Raw Organism than the existing MGI sequence", false);
    }
 }
// $Log
