package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.dao.ALL_Allele_CellLineState;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dbutils.DBException;

/**
 * An object that processes an allele to mutant cell line association
 * @has AlleleMutantCellLineResolver
 * @does creates a ALL_Allele_CellLineState and sets it in the ALO
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class AlleleMutantCellLineProcessor {

    // creates an ALL_Allele_CellLineState
    AlleleMutantCellLineResolver alleleMCLResolver;

    /**
     * Constructs a AlleleMutantCellLineProcessor 
     * @throws MGIException
     */

    public AlleleMutantCellLineProcessor() 
            throws MGIException {
        alleleMCLResolver = new AlleleMutantCellLineResolver();
    }

  /**
   * processes an allele to mutant cell line association
   * @param mutantCellLineKey - cell line key to associate with alleleKey
   * @param resolvedALO - the ALO we are building
   * @assumes the allele has already been processed i.e. resolvedALO contains
   *   the allele key
   */

   public void process(Integer mutantCellLineKey, ALO resolvedALO) throws
            ConfigException, DBException {
        Integer alleleKey = resolvedALO.getAlleleDAO().getKey().getKey();
        ALL_Allele_CellLineState state = alleleMCLResolver.resolve(
            mutantCellLineKey, alleleKey);
        resolvedALO.addAlleleCellLine(state);
   }   
}
