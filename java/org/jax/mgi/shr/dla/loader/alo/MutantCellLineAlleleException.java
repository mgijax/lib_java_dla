package org.jax.mgi.shr.dla.loader.alo; 

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates that a mutant cell line is in the database
 * and associated with no allele or multiple alleles
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class MutantCellLineAlleleException extends MGIException{
    
    /** Creates a new instance of MutantCellLineAlleleException */
    public MutantCellLineAlleleException() {
        super("MCL in database associated with no allele or multiple alleles: ?? " , false);
    }
    public void bindRecordString(String s) {
        bind(s);
    }
}
