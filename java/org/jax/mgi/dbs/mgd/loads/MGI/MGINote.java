package org.jax.mgi.dbs.mgd.loads.MGI;

import java.util.Vector;
import java.util.Iterator;

import org.jax.mgi.dbs.mgd.dao.*;

/**
 * An object that represents a resolved note i.e. an MGI_NoteDAO and a set of
 * corresponding MGI_NoteChunkDAOs objects
 * @has
 *   <UL>
 *   <LI>MGI_NoteDAO
 *   <LI>Set of MGI_NoteChunkDAOs
 *   <LI>isUpdate flag, true if this note is in the database, but needs to be 
 *        updated
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>provides getters and setters for each attribute
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MGINote {
    // the MGI note
    private MGI_NoteDAO noteDAO;
    // the set of chunks, we use vector so user of this class may set the order
    // if desired
    private Vector noteChunkVector;
    // true if in the database
    private boolean isUpdate = false;
	
    // construct an empty MGINote
    public MGINote() {
	noteChunkVector = new Vector();
    }
    
    // construct MGINote with full set of attributes
    public MGINote(MGI_NoteDAO n, Vector nc, boolean iu) {
	this();
	noteDAO = n;
	noteChunkVector = nc;
	isUpdate = iu;
    }
    
    /**
     * setters
     */
    public void setNote(MGI_NoteDAO n) {
	noteDAO = n;
    }
    
    public void setNote(MGI_NoteKey k, MGI_NoteState n) {
	noteDAO = new MGI_NoteDAO(k, n);
    }
    
    public void setNoteChunks(Vector c) {
	noteChunkVector = c;
    }
    
    public void setNoteChunk(MGI_NoteChunkDAO c) {
	
	noteChunkVector.add(c);
    }
    public void setNoteChunk(MGI_NoteChunkState s) {
	MGI_NoteChunkDAO c = new MGI_NoteChunkDAO(s);
	noteChunkVector.add(c);
    }
    public void setIsUpdate(boolean b) {
	isUpdate = b;
    }
    /**
     * getters
     */
    public int getCurrentNoteChunkNum() {
        return noteChunkVector.size();
    }
    public MGI_NoteDAO getNote() {
	return noteDAO;
    }
    public Vector getNoteChunks() {
	return noteChunkVector;
    }
    public Iterator getNoteChunkIterator() {
	return noteChunkVector.iterator();
    }
    public boolean getIsUpdate() {
	return isUpdate;
    }
}
