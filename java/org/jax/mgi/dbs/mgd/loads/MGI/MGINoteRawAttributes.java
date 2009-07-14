package org.jax.mgi.dbs.mgd.loads.MGI;

/**
 * An object that represents raw values needed to create a MGI_NoteState and
 * MGI_NoteChunkState's
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

public class MGINoteRawAttributes {
    
    // the text of the note
    private String note = null;
    // the MGI Type of the note e.g. ALL_Allele
    private String mgiType = null;
    // the note type e.g. Allele Molecular Mutation note
    private String noteType = null;
     
     /**
     * set the note attribute
     * @param note the text of the note
     */

    public void setNote (String note) { this.note = note; }

    /**
     * set the note MGI Type attribute
     * @param mgiType the MGI Type of the note e.g. ALL_Allele
     */

    public void setMGIType (String mgiType) { this.mgiType = mgiType; }

    /**
     * set note type attribute
     * @param noteType the note type e.g. Allele Molecular Mutation note
     */

    public void setNoteType (String noteType) { 
	this.noteType = noteType; 
    }

    
    /**
     * get the note attribute
     */

    public String getNoteD () { return note; }

    /**
     * get the MGI Type attribute
     */

    public String getMGIType () { return mgiType; }

    /**
     * get the note type attribute
     */

    public String getNoteType () { return noteType; }
    
    /**
     * set all attributes to null
     */
    
    public void reset() {
		note = null;
        mgiType = null;
        noteType = null;
        
    }
}
