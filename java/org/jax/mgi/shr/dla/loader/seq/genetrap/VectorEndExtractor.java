package org.jax.mgi.shr.dla.loader.seq.genetrap;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.stringutil.StringLib;
/**
 * determines the vector_end of a sequence tag
 * @has  methods to determine vector end for GGTC, TIGM, ESDB
 * @does Given a sequence tag id, creator name, and sequence tag method
 * determines the vector_end of the sequence tag, if it has one
 * @company The Jackson Laboratory 
 * @author sc
 * @version 1.0
 */

public class VectorEndExtractor {
    
    // TIGM vector end values from sequence tag id
    private static String TIGM_HMF = "HMF";
    private static String TIGM_BBF = "BBF";
    private static String TIGM_HMR = "HMR";
    private static String TIGM_BBR = "BBR";
   
    // ESDB vector end values from sequence tag id
    private static String ESDB_NL = "-NL";
    private static String ESDB_NR = "-NR";
    
    // GGTC vector end values from sequence tag id
    private static String GGTC_5S = "5S";
    private static String GGTC_3S = "3S";


     /**
     * Extracts vector end information from a sequence tag id
     * @throws 
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param creatorName - DB name of creator as specified in GeneTrapConstants
     * for build37 this is logicalDBName, for Less Filling we will no longe
     * use individual IGTC creator ldb's
     * @param seqTagMethod - the sequence tag method
     * @returns vector end or null if seqTagId has no vector end
     * information
     */
    public String extract(String seqTagId, String creatorName, 
	    String seqTagMethod) {
	String vectorEnd = null;
	if (creatorName.equals(GeneTrapConstants.TIGM)) {
	    vectorEnd = processTIGM(seqTagId, seqTagMethod); 
	}
	else if (creatorName.equals(GeneTrapConstants.ESDB)) {
	    vectorEnd = processESDB(seqTagId, seqTagMethod);
	}
	else if (creatorName.equals(GeneTrapConstants.GGTC)) {
	    vectorEnd = processGGTC(seqTagId, seqTagMethod);
	}
	return vectorEnd;
    }
	
     /**
     * Determines vector end information for TIGM
     * @throws 
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param seqTagMethod - the sequence tag method
     * @returns vector end or null 
     * @note example of TIGM sequence tag id: IST10126BBR1
     */
    private String processTIGM(String seqTagId, String seqTagMethod) {
	String ve = null;
	if (seqTagMethod.equals(GeneTrapConstants.INVERSEPCR)) {
	     if (seqTagId.indexOf(TIGM_HMF) != -1) {
		 ve = GeneTrapConstants.UPSTREAM;
	     }
	     else if (seqTagId.indexOf(TIGM_HMR) != -1) {
		 ve = GeneTrapConstants.DOWNSTREAM;
	     }
	     else if (seqTagId.indexOf(TIGM_BBF) != -1) {
		 ve = GeneTrapConstants.UPSTREAM;
	     }
	     else if (seqTagId.indexOf(TIGM_BBR) != -1) {
		 ve = GeneTrapConstants.DOWNSTREAM;
	     }
	}
	return ve;
    }
    
     /**
     * Determines vector end information for ESDB
     * @throws 
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param seqTagMethod - the sequence tag method
     * @returns vector end or null 
     * @note example of ESDB sequence tag id: 
     */
    private String processESDB(String seqTagId, String seqTagMethod) {
	String ve = null;
	if (seqTagMethod.equals(GeneTrapConstants.PLASMRESCUE)) {
	    if(seqTagId.indexOf(ESDB_NL) != -1) {
		ve = GeneTrapConstants.UPSTREAM;
	    }
	    else if (seqTagId.indexOf(ESDB_NR) != -1) {
	        ve = GeneTrapConstants.DOWNSTREAM;
	    }
	}
	return ve;
    }
     /**
     * Determines vector end information for GGTC
     * @throws 
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param seqTagMethod - the sequence tag method
     * @returns vector end or null 
     * @note example of GGTC sequence tag id: 3SP126F08
     */
    private String processGGTC(String seqTagId, String seqTagMethod) {
	String ve = null;
		if (seqTagMethod.equals(GeneTrapConstants.SPLINK3) || 
	    seqTagMethod.equals(GeneTrapConstants.SPLINK5) )
	    	    if(seqTagId.indexOf(GGTC_5S) != -1) {
	        ve = GeneTrapConstants.UPSTREAM;
	    }
	    else if (seqTagId.indexOf(GGTC_3S) != -1) {
	        ve = GeneTrapConstants.DOWNSTREAM;
	    }
	return ve;
    }
}
