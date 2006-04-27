package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException thrown when a sequences raw organism in the database
 *     does not match the incoming raw organism
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class ChangedOrganismException extends MGIException {
    public ChangedOrganismException() {
        super("This sequence has a different Raw Organism than the existing MGI sequence", false);
    }
 }
