package org.jax.mgi.dbs.mgd.loads.MGI;

import java.util.Vector;
import java.util.Iterator;

import org.jax.mgi.dbs.mgd.dao.*;

/**
 * An object that represents a resolved note i.e. an MGI_NoteDAO 
 * @has
 *   <UL>
 *   <LI>MGI_NoteDAO
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

    // true if in the database
    private boolean isUpdate = false;
	
    // construct an empty MGINote
    public MGINote() {
	
    }
    
    // construct MGINote with full set of attributes
    public MGINote(MGI_NoteDAO n, boolean iu) {
	this();
	noteDAO = n;
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
    
    public void setIsUpdate(boolean b) {
	isUpdate = b;
    }
    /**
     * getters
     */
    public MGI_NoteDAO getNote() {
	return noteDAO;
    }
    public boolean getIsUpdate() {
	return isUpdate;
    }
}
