package org.jax.mgi.dbs.mgd.loads.Alo;

/**
 * An object that represents raw values needed to create a SEQ_AlleleAssocState
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

public class SeqAlleleAssocRawAttributes {
    
    // sequence accession ID
    private String seqID = null;
    // association qualifier
    private String qualifier = null;
    // association reference
    private String jNum = null;
     
     /**
     * set the seqID attribute
     * @assumes Nothing
     * @effects Nothing
     * @param seqID the sequence accession ID
     */

    public void setSeqID (String seqID) { this.seqID = seqID; }

    /**
     * set the association qualifier attribute
     * @assumes Nothing
     * @effects Nothing
     * @param qualifier the association qualifier e.g. representative sequence
     */

    public void setQualifier (String qualifier) { this.qualifier = qualifier; }

    /**
     * set association reference attribute
     * @assumes Nothing
     * @effects Nothing
     * @param reference a J Number
     */

    public void setJNum (String reference) { 
	this.jNum = reference; 
    }

    
    /**
     * get the seqID attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getSeqID () { return seqID; }

    /**
     * get the association qualifier attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getQualifier () { return qualifier; }

    /**
     * get the reference (J Number) attribute
     * @assumes Nothing
     * @effects Nothing
     */

    public String getJNum () { return jNum; }
    
    /**
     * set all attributes to null
     * @assumes Nothing
     * @effects Nothing
     */
    
    public void reset() {
	seqID = null;
        qualifier = null;
        jNum = null;
        
    }
}

