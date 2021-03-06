package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

/**
 * An object that represents raw values needed to create a SEQ_GeneTrapState
 * object 
 * @has
 *   <UL>
 *   <LI> set of raw attributes
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>provides getters and setters for each attribute
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqGeneTrapRawAttributes {
    
    // sequence accession ID
    private String seqID = null;
    // sequence tag ID - this is the mutant cell line ID plus prefix/suffix
    // depending on creator
    private String seqTagID = null;
    // sequence tag method 
    private String seqTagMethod = null;
    // vector end of the sequence tag
    private String vectorEnd = null;
    // 'yes' if sequence tag is reverse complemented, else 'no'
    private String reverseComp = null;
    // number of good hits on the genome for the sequence tag
    private Integer goodHitCount = null;
    // insertion site point coordinate of the gene trap
    private Double pointCoord = null;
	
     /**
     * set the seqID attribute
     * @param seqID the sequence accession ID
     */

    public void setSeqID (String seqID) { this.seqID = seqID; }

    /**
     * set the sequence Tag ID attribute - this is the mutant cell line ID 
     * plus prefix/suffix
     * @param seqTagID the sequence tag ID
     */

    public void setSeqTagID (String seqTagID) { this.seqTagID = seqTagID; }

    /**
     * set sequence tag method attribute
     * @param seqTagMethod - the sequence tag method
     */

    public void setSeqTagMethod(String seqTagMethod) { 
	this.seqTagMethod = seqTagMethod; 
    }

     /**
     * set vector end attribute
     * @param vectorEnd - the vector end
     */

    public void setVectorEnd(String vectorEnd) { 
	this.vectorEnd = vectorEnd; 
    }
    
    /**
     * set reverse complemented attribute
     * @param reverseComp - yes if reverse complemented else no
     */

    public void setIsReverseComp(String reverseComp) { 
	this.reverseComp = reverseComp; 
    }  
    
    /**
     * set good hit count attribute
     * @param goodHitCount - number of times this sequence tag hits the genome
     */

    public void setGoodHitCount(Integer goodHitCount) { 
	this.goodHitCount = goodHitCount; 
    }
    
     /**
     * set point coordinate attribute
     * @param pointCoord - insertion site point coordinate of the gene trap
     */

    public void setPointCoord(Double pointCoord) { 
	this.pointCoord = pointCoord; 
    }
    
    /**
     * get the seqID attribute
     */

    public String getSeqID () { return seqID; }

    /**
     * get the sequence tag ID attribute
     */

    public String getSeqTagID () { return seqTagID; }

    /**
     * get sequence tag method attribute
     */

    public String getSeqTagMethod () { return seqTagMethod; }
    
    /**
     * get vector end attribute
     */

    public String getVectorEnd() { return vectorEnd; }
    
    /**
     * get reverse complemented attribute
     */

    public String getReverseComp () { return reverseComp; }
    
    /**
     * get number of good hits attribute
     */

    public Integer getGoodHitCount () { return goodHitCount; }
    
    /**
     * get is reverse complemented attribute
     */

    public Double getPointCoord () { return pointCoord; }
    
    /**
     * set all attributes to null
     */
    
    public void reset() {
		seqID = null;
        seqTagID = null;
        seqTagMethod = null;
		vectorEnd = null;
		reverseComp = null;
		goodHitCount = null;
		pointCoord = null;
    }
}

