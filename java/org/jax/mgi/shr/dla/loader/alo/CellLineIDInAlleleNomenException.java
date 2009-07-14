package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which indicates a cell line ID has been found in
 *  an allele symbol or synonym
 * Implements bindRecordString which allows runtime binding of cell
 * line ID and symbol/synonym
 * @company Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class CellLineIDInAlleleNomenException extends MGIException {
    
    /** Creates a new instance of CellLineIDInAlleleNomenException */
    public CellLineIDInAlleleNomenException() {
        super("Cell Line ID in allele nomen: ?? " , false);
    }
    public void bindRecordString(String s) {
        bind(s);
    }
    
}

