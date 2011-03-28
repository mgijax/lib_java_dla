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
	
	// set default which assumes RNA with NOT APPLICABLE vector end
	// i.e. cellLineId = seqTagId, null vector end
	KeyValue clIdToVectorEnd = new KeyValue(
		seqTagId, DBGSSGeneTrapLoaderConstants.NOT_APPLICABLE);
	
	// Determine if this is a DNA sequence tag
	Boolean isDNA = Boolean.FALSE;
	int index = seqType.indexOf("DNA");
	if (index >= 0) {
	    isDNA = Boolean.TRUE;
	}
	// TIGM, ESDB, GGTC still use DNA-based sequence tag methods. 
	// GGTC currently have both DNA and RNA. Over time these creators
	// may all submit RNA-based sequence tags. It is not expected that 
	// those using RNA methods will move to DNA methods (old technology)
	if (isDNA.equals(Boolean.TRUE)) {
	    if (creatorName.equals(DBGSSGeneTrapLoaderConstants.TIGM)) {
		    clIdToVectorEnd = processTIGM(seqTagId, seqTagMethod); 
	    }
	    else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.ESDB)) {
		clIdToVectorEnd = processESDB(seqTagId, seqTagMethod);
	    }
	    else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.GGTC)) {
		clIdToVectorEnd = processGGTC(seqTagId, seqTagMethod);
	    }
	    else if  (creatorName.equals(DBGSSGeneTrapLoaderConstants.EGTC)) {
		// EGTC DNA seq with no vector end specified
		clIdToVectorEnd = new KeyValue(seqTagId,
			DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED);
	    }
	    else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.EUCOMM)) {
	       clIdToVectorEnd = processEUCOMM(seqTagId);
	    }
	    else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.CMHD)) {
            // where cellLineID == CMHD-GT_107A10-3
            // id = 107A10
            clIdToVectorEnd = processCMHD(seqTagId);

	    }
	    else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.FHCRC)) {
		// where cellLineID == FHCRC-GT-S15-9G1
		// id = S15-9G1
		clIdToVectorEnd = processFHCRC(seqTagId);
	    }
	 } // RNA
	else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.CMHD)) {
	    // where cellLineID == CMHD-GT_107A10-3
	    // id = 107A10
	    clIdToVectorEnd = processCMHD(seqTagId);
	    
	}
	else if (creatorName.equals(DBGSSGeneTrapLoaderConstants.FHCRC)) {
	    // where cellLineID == FHCRC-GT-S15-9G1
	    // id = S15-9G1
	    clIdToVectorEnd = processFHCRC(seqTagId);
	}

	//System.out.println("In extract seqTagId: " + seqTagId);
	
	//System.out.println("In extract clIdToVectorEnd.getKey: " + clIdToVectorEnd.getKey());
	//System.out.println("In extract clIdToVectorEnd.getValue: " + clIdToVectorEnd.getValue());
	return clIdToVectorEnd;
    }
   
     /**
     * Determines cell line ID for CMHD
     * @param seqTagId - the sequence tag id from which to determine 
     * cell line ID
     * @return KeyValue containing cellLineId and vector end
         * @throws NoVectorEndException if seqTagId can't be parsed
     * @note example of CMHD sequence tag ID: CMHD-GT_111.1G4-3 
     * where cell line id is 111.1G4 OR
     *     CMHD_GT_139A2-3 where cell  line id is 139A2
     */
    private KeyValue processCMHD(String seqTagId)
            throws NoVectorEndException {
	String ve = DBGSSGeneTrapLoaderConstants.NOT_APPLICABLE;

	// Strip off 'CMHD-GT_'
        String temp = seqTagId.substring(8);
	//System.out.println("temp: " + temp);
	int index = temp.indexOf("-");
        String cellLineId = temp.substring(0, index);
	//System.out.println("cellLineId: " +cellLineId);
        return new KeyValue(cellLineId, ve);
    }

     /**
     * Determines cell line ID for FHCRC
     * @param seqTagId - the sequence tag id from which to determine
     * cell line ID
     * @return KeyValue containing cellLineId and vector end
         * @throws NoVectorEndException if seqTagId can't be parsed
     * @note example of CMHD sequence tag ID:  FHCRC-GT-S15-9G1  where
     * cell line id is S15-9G1 OR
     * FHCRC-GT-S7-5G1_ where cell line id is S7-5G1_
     */
    private KeyValue processFHCRC(String seqTagId)
            throws NoVectorEndException {
        String ve = DBGSSGeneTrapLoaderConstants.NOT_APPLICABLE;

        // Strip off 'FHCRC-GT-'
        String cellLineId = seqTagId.substring(9);
        return new KeyValue(cellLineId, ve);
    }

	
     /**
     * Determines vector end information for TIGM
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param seqTagMethod - the sequence tag method
     * @return KeyValue containing cellLineId and vector end
	 * @throws NoVectorEndException if seqTagId contains no vector end
     * @note example of TIGM sequence tag id: IST10126BBR1
     */
    private KeyValue processTIGM(String seqTagId, String seqTagMethod) 
	    throws NoVectorEndException {
        int index = -1;
        String ve = null;
        if (seqTagMethod.toLowerCase().equals(
                DBGSSGeneTrapLoaderConstants.INVERSEPCR)) {
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
        }
        if (ve == null) {
            NoVectorEndException e = new NoVectorEndException();
            e.bindRecordString(seqTagId);
            throw e;
        }

        String cellLineId = seqTagId.substring(0, index);
        return new KeyValue(cellLineId, ve);
        }
   
     /**
     * Determines vector end information for ESDB
     * @throws 
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param seqTagMethod - the sequence tag method
     * @returns KeyValue containing cellLineId and vector end
     * @note examples of ESDB sequence tag id:
     * <UL>
     * <LI> PST2612-NR, PST2612-NR
     * </UL>
     */
    private KeyValue processESDB(String seqTagId, String seqTagMethod) 
	    throws NoVectorEndException {
        int index = -1;
        String ve = null;
        if (seqTagMethod.toLowerCase().equals(
            DBGSSGeneTrapLoaderConstants.PLASMRESCUE)) {
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
        }
        if (ve == null) {
           ve =   DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED;
           return new KeyValue(seqTagId, ve);
        }
        String cellLineId = seqTagId.substring(0, index);
        return new KeyValue(cellLineId, ve);
    }
     /**
     * Determines vector end information for GGTC
     * @throws 
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param seqTagMethod - the sequence tag method
     * @returns KeyValue containing cellLineId and vector end
     * @throws NoVectorEndException if seqTagId contains no vector end
     * @note example of GGTC sequence tag id: 3SP126F08
     */
    private KeyValue processGGTC(String seqTagId, String seqTagMethod) 
	    throws NoVectorEndException {
		int index = 2;
        String ve = null;
        if (seqTagMethod.toLowerCase().equals(DBGSSGeneTrapLoaderConstants.SPLINK3) ||
            seqTagMethod.toLowerCase().equals(DBGSSGeneTrapLoaderConstants.SPLINK5) ) {
            if(seqTagId.indexOf(GGTC_5S) != -1) {
                ve = DBGSSGeneTrapLoaderConstants.UPSTREAM;
            index = seqTagId.indexOf(GGTC_5S);
            }
            else if (seqTagId.indexOf(GGTC_3S) != -1) {
                ve = DBGSSGeneTrapLoaderConstants.DOWNSTREAM;
            index = seqTagId.indexOf(GGTC_3S);
            }
        }
        if (ve == null ) {
            NoVectorEndException e = new NoVectorEndException();
            e.bindRecordString(seqTagId);
            throw e;
        }
            String cellLineId = seqTagId.substring(index + 2);
            return new KeyValue(cellLineId, ve);
    }
    /**
     * Determines vector end information for EUCOMM
     * @throws 
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param seqTagMethod - the sequence tag method
     * @returns KeyValue containing cellLineId and vector end
     * @throws NoVectorEndException if seqTagMethod contains no vector end
     * @note example of EUCOMM sequence tag id: EUCE0163h02.q1ka5SPK
     *       from this we get cell line id: EUCE0163h02 and upstream
     *       vector end 
     */
    private KeyValue processEUCOMM(String seqTagId)
            throws NoVectorEndException {
        String cellLineId = (String)(StringLib.split(seqTagId, ".")).get(0);
        String ve = null;
        if (seqTagId.indexOf(EUCOMM_3) != -1) {
            ve = DBGSSGeneTrapLoaderConstants.DOWNSTREAM;
        }
        else if (seqTagId.indexOf(EUCOMM_5) != -1) {

            ve = DBGSSGeneTrapLoaderConstants.UPSTREAM;
        }
        if (ve == null ) {
            NoVectorEndException e = new NoVectorEndException();
            e.bindRecordString(seqTagId);
            throw e;
        }
        return new KeyValue(cellLineId, ve);
    }
}

