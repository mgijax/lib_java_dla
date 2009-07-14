package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import java.sql.Timestamp;

import org.jax.mgi.dbs.mgd.loads.Acc.AccessionRawAttributes;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;

/**
 * An object that represents dbGSS Gene Trap data from a dbGSS sequence record
 *     and/or Configuration in its raw form. We extend ALORawInput to add dbGSS 
 *     gene trap information needed to create a SEQ_GeneTrapState for a gene
 *     trap sequence
 * @has
 *   <UL>
 *   <LI>see superclass
 *   <LI>A SeqGeneTrapRawAttributes
 *   <LI>A sequence record date - if we see a given seqID in the input more than 
 *          once, and a subsequent instance is more recent thant the first 
 *          instance processed, then we write the sequence record out for 
 *          repeat processing
 *   <LI>Note: load will add sequence tag ID to Sequence associations to the 
 *          superclass AccessionRawAttributes set
 *   </UL>
 * @does Provides getters and setters for its attributes
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class DBGSSGeneTrapRawInput extends ALORawInput {
    
    // raw gene trap sequence attributes
    private SeqGeneTrapRawAttributes seqGT = null;
    
    // seqTagId to sequence association
    private AccessionRawAttributes seqTagAcc = null;
    
    // the sequence record date
    private Timestamp seqRecordDate = null;
    
    /**
     * set the raw gene trap sequence attributes
     * @param seqGT the raw gene trap sequence ingo
     */

    public void setSeqGeneTrap (SeqGeneTrapRawAttributes seqGT) {
	this.seqGT = seqGT;
    }
     /**
     * set the raw seqtagId to sequence association
     * @param seqTagAcc sequence tag id accession object
     */

    public void setSeqTagAccession (AccessionRawAttributes seqTagAcc) {
	this.seqTagAcc = seqTagAcc;
    }  
    /**
     * set sequence record date
     * @param seqRecordDate the sequence record date
     */

    public void setSeqRecordDate (Timestamp seqRecordDate) {this.seqRecordDate = 
	seqRecordDate;}
    
    /**
     * get the raw gene trap sequence attributes
     * @return SeqGeneTrapRawAttributes
     */

    public SeqGeneTrapRawAttributes getSeqGeneTrap() {
         return seqGT;
    }

    /**
     * get the seq tag ID accession
     * @return AccessionRawAttributes
     */

    public AccessionRawAttributes getSeqTagAccession() {
         return seqTagAcc;
    }
    
     /**
     * get sequence record date 
     * @return sequence record date
     */

    public Timestamp getSeqRecordDate() {
         return seqRecordDate;
    }
    
    /**
     * clears Vectors and sets other objects to null
	 * @Override ALOInput.reset()
     */

    public void reset() {
		super.reset();
		seqGT = null;
		seqTagAcc = null;
		seqRecordDate = null;
    }
}
