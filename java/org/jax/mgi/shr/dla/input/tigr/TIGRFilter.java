package org.jax.mgi.shr.dla.input.tigr;

import org.jax.mgi.shr.dla.input.fasta.FASTAFilter;
import org.jax.mgi.shr.dla.input.fasta.FASTAData;



/**
 * a FASTAFilter for exluding non TC sequences
 * @has nothing
 * @does rejects a given FASTAData if it is a non TC sequence
 * @company Jackson Laboratory
 * @author M Walker
 *
 */



public class TIGRFilter implements FASTAFilter {


  /**
   * rejects (returns null) a given FASTAData if it does not refer to a
   * TC sequence
   * @param data the given FASTAData
   * @return the given FASTAData if it is a TC sequence and null otherwise
   */

  public FASTAData filter(FASTAData data)
  {
    if (data.getAccid().substring(0, 2).equals("TC"))
      return data;
    else
      return null;
  }



}