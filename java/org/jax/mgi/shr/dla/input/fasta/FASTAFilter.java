package org.jax.mgi.shr.dla.input.fasta;



/**
 * An interface used for filtering fasta data for the purposes specific to
 * a specific loader process
 * @has nothing
 * @does rejects or changes a given FASTAData object
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public interface FASTAFilter {

  /**
   * rejects (returns null) or changes the FASTAData input
   * @param data the incoming data
   * @return the outgoing data or null if rejected
   */
  public FASTAData filter(FASTAData data);
}