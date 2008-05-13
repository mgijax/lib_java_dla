package org.jax.mgi.shr.dla.loader.seq.genetrap;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.stringutil.StringLib;
/**
 * determines whether a sequence tag is reverse complemented
 * @has  
 * @does Given a sequence tag id and its creator (from GeneTrapsConstants)
 * determines if a sequence tag is reverse complemented
 * @company The Jackson Laboratory 
 * @author sc
 * @version 1.0
 */

public class ReverseComplDetector {

     /**
     * Extracts vector end information from a sequence tag id
     * @throws 
     * @param creatorName - name of creator from GeneTrapConstants)
     * @returns true if is Reverse Complemented, null if not applicable
     * (.e.g  RNA sequence tags)
     */
    public Boolean detect(String creatorName) {
	Boolean isReverseCompl = null;
	if (creatorName.equals(GeneTrapConstants.TIGM) || 
	    creatorName.equals(GeneTrapConstants.ESDB) ||
	    creatorName.equals(GeneTrapConstants.GGTC) ) {
	    if(creatorName.equals(GeneTrapConstants.TIGM)) {
	        isReverseCompl = Boolean.FALSE;
	    }
	    else {
		isReverseCompl = Boolean.TRUE;
            }	
	}
	return isReverseCompl;
    }
}
