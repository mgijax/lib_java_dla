package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.shr.cache.KeyValue;

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
		//System.out.println("EGTC");
	   	clIdToVectorEnd = new KeyValue(seqTagId, 
		    DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED);
	    }
	}
	return clIdToVectorEnd;
    }
   	
     /**
     * Determines vector end information for TIGM
     * @param seqTagId - the sequence tag id from which to determine the vector
     * end information
     * @param seqTagMethod - the sequence tag method
     * @return vector end or null
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
              //System.out.println("In TIGM_BBF vector end is " + ve + " index is " + index);
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
     * @returns vector end or null 
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
     * @returns vector end or null 
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
}
