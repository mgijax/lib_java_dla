package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.stringutil.StringLib;

import java.util.ArrayList;

/**
 * determines the vector_end of a sequence tag
 * @has  methods to determine vector end for GGTC, TIGM, ESDB
 * @does Given a sequence tag id, creator name, and sequence tag method
 * determines the vector_end of the sequence tag, if it has one
 * @company The Jackson Laboratory 
 * @author sc
 * @version 1.0
 */

public class VectorEndCellLineIDExtractor {
    
    // TIGM vector end values from sequence tag id
    private static final String TIGM_HMF = "HMF";
    private static final String TIGM_BBF = "BBF";
    private static final String TIGM_HMR = "HMR";
    private static final String TIGM_BBR = "BBR";
   
    // ESDB vector end values from sequence tag id
    private static final String ESDB_NL = "-NL";
    private static final String ESDB_NR = "-NR";
    
    // GGTC vector end values from sequence tag id
    private static final String GGTC_5S = "5S";
    private static final String GGTC_3S = "3S";

    // EUCOMM vector end values from sequence tag id
    private static final String EUCOMM_5 = "5SPK";
    private static final String EUCOMM_3 = "3SPK";


     /**
     * Extracts vector end information from a sequence tag id
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param creatorName - raw creator from dbGSS
     * @param seqTagMethod - the sequence tag method
     * @return KeyValue MutantCellLineID:vectorEnd or null 
     * if seqTagId has no vector end information
	 * @throws NoVectorEndException is vector end is not found in seqTagId
     */
    public KeyValue extract(String seqTagId, String creatorName, 
	    String seqTagMethod, String seqType) throws NoVectorEndException {
	
	/**
	 * extract by creator
	 */

	// These creators all use RNA based sequence tag methods: 
	// 3' or 5' RACE and their cell line ID == sequence tag ID
	// note WURST is GGTC 5'RACE sequences
	if (creatorName.equals(DBGSSGeneTrapLoaderConstants.BAYGENOMICS) ||
	        creatorName.equals(DBGSSGeneTrapLoaderConstants.LEXICON) ||
		creatorName.equals(DBGSSGeneTrapLoaderConstants.SIGTR) ||
		creatorName.equals(DBGSSGeneTrapLoaderConstants.TIGEM) ||
		creatorName.equals(DBGSSGeneTrapLoaderConstants.ISHIDA) ||
		creatorName.equals(DBGSSGeneTrapLoaderConstants.WURST)) {
	    return new KeyValue(
                seqTagId, DBGSSGeneTrapLoaderConstants.NOT_APPLICABLE);
        }
	else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.CMHD)) {
            return processCMHD(seqTagId, seqTagMethod);
        }
	else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.ESDB)) {
	    return processESDB(seqTagId);
	}
	else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.EUCOMM)) {
	   return processEUCOMM(seqTagId, seqTagMethod);
	}
	else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.FHCRC)) {
	    // where cellLineID == FHCRC-GT-S15-9G1
	    // id = S15-9G1
	    return processFHCRC(seqTagId, seqTagMethod);
	}
	else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.GGTC)) {
            return processGGTC(seqTagId, seqTagMethod);
        }
        else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.TIGM) ||
		creatorName.equals(DBGSSGeneTrapLoaderConstants.TIGM_2)) {
            return processTIGM(seqTagId);
        }
	else {
	    NoVectorEndException e = new NoVectorEndException();
            e.bindRecordString(seqTagId);
            throw e;
        }
    }
   
     /**
     * Determines cell line ID and vectore end for CMHD
     * @param seqTagId - the sequence tag id from which to determine 
     * @param seqTagMethod - the sequence tag method
     * @return KeyValue containing cellLineId and vector end
     * @note example of CMHD sequence tag ID: CMHD-GT_111.1G4-3 
     * where cell line id is 111.1G4 OR
     *     CMHD_GT_139A2-3 where cell  line id is 139A2
     */
    private KeyValue processCMHD(String seqTagId, String seqTagMethod) {
	// all CMHD are DNA based sequence tag methods
	String ve = DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED;
	if (seqTagMethod.equals(DBGSSGeneTrapLoaderConstants.RACE5_1) ||
		seqTagMethod.equals(DBGSSGeneTrapLoaderConstants.RACE3_1)) 
	    ve = DBGSSGeneTrapLoaderConstants.NOT_APPLICABLE;
	else if (seqTagMethod.toLowerCase().equals(DBGSSGeneTrapLoaderConstants.SPLINK3)) 
	    ve = DBGSSGeneTrapLoaderConstants.DOWNSTREAM;
	else if (seqTagMethod.toLowerCase().equals(DBGSSGeneTrapLoaderConstants.SPLINK5)) 
	    ve = DBGSSGeneTrapLoaderConstants.UPSTREAM;
	else if ( seqTagMethod.toLowerCase().equals(DBGSSGeneTrapLoaderConstants.INVERSEPCR))
	    ve = ve = DBGSSGeneTrapLoaderConstants.UPSTREAM;

	// Strip off 'CMHD-GT_'
        String temp = seqTagId.substring(8);
	//System.out.println("temp: " + temp);
	int index = temp.indexOf("-");
        String cellLineId = temp.substring(0, index);
	//System.out.println("cellLineId: " +cellLineId);
        return new KeyValue(cellLineId, ve);
    }

     /**
     * Determines cell line ID and vector end for FHCRC 
     * @param seqTagId - the sequence tag id 
     * @return KeyValue containing cellLineId and vector end
     * @note example of CMHD sequence tag ID:  FHCRC-GT-S15-9G1  where
     * cell line id is S15-9G1 OR
     * FHCRC-GT-S7-5G1_ where cell line id is S7-5G1_
     */
    private KeyValue processFHCRC(String seqTagId, String seqTagMethod) {

	// Strip off 'FHCRC-GT-'
        String cellLineId = seqTagId.substring(9);

	// RNA-based	
	if (seqTagMethod.equals(DBGSSGeneTrapLoaderConstants.RACE5_2) ||
                seqTagMethod.equals(DBGSSGeneTrapLoaderConstants.RACE3_2)) {
	     return new KeyValue (cellLineId, 
		DBGSSGeneTrapLoaderConstants.NOT_APPLICABLE);
	} // DNA-based 
	if (seqTagMethod.toLowerCase().equals(
	    DBGSSGeneTrapLoaderConstants.ADAPTORPCR)) {
	    return new KeyValue (cellLineId, 
		DBGSSGeneTrapLoaderConstants.UPSTREAM);
	}
	else { // DNA-based default
	   return new KeyValue (cellLineId, 
		DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED);
	}
    }

	
     /**
     * Determines cell line ID and vector end for TIGM
     * @param seqTagId - the sequence tag id 
     * @return KeyValue containing cellLineId and vector end
     * @note example of TIGM sequence tag id: IST10126BBR1
     */
    private KeyValue processTIGM(String seqTagId)
	    throws NoVectorEndException {
        int index = -1;
        String ve = null;
	// all TIGM are inverse PCR
	if (seqTagId.indexOf(TIGM_HMF) != -1) {
	    ve = DBGSSGeneTrapLoaderConstants.UPSTREAM;
	    index = seqTagId.indexOf(TIGM_HMF);
	}
	else if (seqTagId.indexOf(TIGM_HMR) != -1) {
	     ve = DBGSSGeneTrapLoaderConstants.DOWNSTREAM;
	     index = seqTagId.indexOf(TIGM_HMR);
	     }
	else if (seqTagId.indexOf(TIGM_BBF) != -1) {
	     ve = DBGSSGeneTrapLoaderConstants.UPSTREAM;
	     index = seqTagId.indexOf(TIGM_BBF);
	}
	else if (seqTagId.indexOf(TIGM_BBR) != -1) {
	    ve = DBGSSGeneTrapLoaderConstants.DOWNSTREAM;
	    index = seqTagId.indexOf(TIGM_BBR);
	}
        else {
	    ve = DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED;
	    index = seqTagId.length(); // the entire seqTagId
        }
        String cellLineId = seqTagId.substring(0, index);
        return new KeyValue(cellLineId, ve);
        }
   
     /**
     * Determines cell line ID and vector end for ESDB
     * @param seqTagId - the sequence tag id 
     * @param seqTagMethod - the sequence tag method
     * @returns KeyValue containing cellLineId and vector end
     * @note examples of ESDB sequence tag id:
     * <UL>
     * <LI> PST2612-NR, PST2612-NR
     * </UL>
     */
    private KeyValue processESDB(String seqTagId) {
        int index = -1;
        String ve = null;
	// ESDB is all plasmid rescue
	if(seqTagId.indexOf(ESDB_NL) != -1) {
	    ve = DBGSSGeneTrapLoaderConstants.UPSTREAM;
	    index = seqTagId.indexOf(ESDB_NL);
	}
	else if (seqTagId.indexOf(ESDB_NR) != -1) {
	    ve = DBGSSGeneTrapLoaderConstants.DOWNSTREAM;
	    index = seqTagId.indexOf(ESDB_NR);
	}
	else if (seqTagId.indexOf("-") != -1) {
	    ve = DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED;
	    index = seqTagId.indexOf("-");
	}
	else {
	   return new KeyValue(seqTagId, DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED);
	}
       String cellLineId = seqTagId.substring(0, index);
       return new KeyValue(cellLineId, ve);
    }
     /**
     * Determines cell line ID and vector end for GGTC (not including WURST)
     * @param seqTagId - the sequence tag id 
     * @param seqTagMethod - the sequence tag method
     * @returns KeyValue containing cellLineId and vector end
     * @note example of GGTC sequence tag id: 3SP126F08
     */
    private KeyValue processGGTC(String seqTagId, String seqTagMethod ) {

	int index = 2;
        String ve = null;
	// seqTagMethod is 3' or 5' splinkerette
	if (seqTagMethod.toLowerCase().equals(DBGSSGeneTrapLoaderConstants.SPLINK3)) {
            ve = DBGSSGeneTrapLoaderConstants.DOWNSTREAM;
	    index = seqTagId.indexOf(GGTC_3S);
	}
        else if (seqTagMethod.toLowerCase().equals(DBGSSGeneTrapLoaderConstants.SPLINK5)) {
            ve = DBGSSGeneTrapLoaderConstants.UPSTREAM;
	    index = seqTagId.indexOf(GGTC_5S);
	}
	else { // anything else just load the seqtagID and cellLine ID and set
		// vector end as not specified
	    return new  KeyValue(seqTagId, DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED);
        }
	String cellLineId = seqTagId.substring(index + 2);
	return new KeyValue(cellLineId, ve);
    }

    /**
     * Determines cell line ID and vector end for EUCOMM
     * @param seqTagId - the sequence tag id 
     * @returns KeyValue containing cellLineId and vector end
     * @note example of EUCOMM sequence tag id: EUCE0163h02.q1ka5SPK
     *       from this we get cell line id: EUCE0163h02 and upstream
     *       vector end 
     */

    private KeyValue processEUCOMM(String seqTagId, String seqTagMethod) {
        String cellLineId = (String)(StringLib.split(seqTagId, ".")).get(0);
        String ve = null;

	 if (seqTagMethod.toLowerCase().equals(DBGSSGeneTrapLoaderConstants.SPLINK3)) {
            ve = DBGSSGeneTrapLoaderConstants.DOWNSTREAM;
        }
        else if (seqTagMethod.toLowerCase().equals(DBGSSGeneTrapLoaderConstants.SPLINK5)) {
            ve = DBGSSGeneTrapLoaderConstants.UPSTREAM;
        }

	else {
	    ve = DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED;
	}
        return new KeyValue(cellLineId, ve);
    }
}

