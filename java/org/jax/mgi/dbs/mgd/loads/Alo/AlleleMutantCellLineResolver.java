package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.dao.ALL_Allele_CellLineState;

/**
 * An object that creates an ALL_Allele_CellLineState from an allele key 
 * and a cell line key
 * @has
 * @does Creates an ALL_Allele_CellLineState
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class AlleleMutantCellLineResolver {

    /**
      * creates a ALL_Allele_CellLineState
      * @param mutantCellLineKey - cell line key to associate with alleleKey
      * @param alleleKey - allele key to associate with cellLineKey
      * @return An ALL_Allele_CellLineState
      */
    public ALL_Allele_CellLineState resolve(Integer mutantCellLineKey,
            Integer alleleKey) {
        ALL_Allele_CellLineState state = new ALL_Allele_CellLineState();
    	state.setMutantCellLineKey(mutantCellLineKey);
        state.setAlleleKey(alleleKey);
        return state;
    }
}
