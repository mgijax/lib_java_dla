package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.*;

import org.jax.mgi.dbs.mgd.dao.*;
//import org.jax.mgi.dbs.mgd.loads.MGI.MGINote;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.DBException;

/**
 * An object that manages a set of DAOs representing an Allele Like Object.
 * @has
 *   <UL>
 *   <LI>ALL_AlleleDAO ate)
 *   <LI>Set of ACC_AccessionDAO's for the allele MGI ID and any other ids
 *	     needed by a particular load
 *   <LI>Set of ALL_CellLineDAO's
 *   <LI>Set of ALL_MutationDAOs for molecular mutations
 *   <LI>MGI_Note - contains DAOs for a molecular note
 *   <LI>Set of MGI_ReferenceAssocDAOs - load reference, other references
 *	     needed by a particular load
 *   <LI>SEQ_Allele_AssocDAO - sequence association to the allele
 *   </UL>
 * @does
 *  <UL>
 *   <LI>creates DAO objects for allele, allele MGI ID, mutant cell lines
 *       allele mutations, allele molecular notes, allele reference
 *       associations and any other reference associations required by a
 *       load, and allele sequence association
 *   <LI>Updates some ALO attributes, currently just mutant cell lines.
 *   <LI>Adds an allele, allele MGI ID, mutant cell lines, allele mutations,
 *		allele molecular notes, ALO reference associations, and allele
 *		sequence associations to a database
 *   <LI>Provides methods to get DAOs
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class ALO {

	// the stream used to accomplish the database inserts, updates, deletes
	protected SQLStream stream;

	// The allele
	private ALL_AlleleDAO alleleDAO = null;

	// the set of molecular mutations for this ALO
	private HashSet alleleMutationDAOs = new HashSet();

	// List of ALO ACC_AccessionDAOs, one being the allele MGI ID
	private HashSet accessionDAOs = new HashSet();

	// the molecular notes for this allele
	private HashSet noteDAOs = new HashSet();

	// the note chunks for the the molecular notes
	private HashSet noteChunkDAOs = new HashSet();

	// Set of Cell Lines for this allele
	private HashSet cellLineDAOs = new HashSet();

	// Set of Cell Line updates for this Allele
	private HashSet cellLineUpdateDAOs = new HashSet();

	// Set of Cell LIne to Allele associations
	private HashSet alleleCellLineDAOs = new HashSet();

	// List of MGI_ReferenceAssocDAOs for this ALO
	private HashSet refAssociationDAOs = new HashSet();

	// sequence association for this allele
	private SEQ_Allele_AssocDAO seqAlleleDAO = null;

    // True if this is an ALo found to be in the database and has updates
    // Added for gene traps for logging whether a  ALO is updated or new
    private Boolean isUpdate = Boolean.FALSE;

	/**
	 * Constructs a ALO object
	 * @param stream the stream which to pass the DAO objects to perform
	 * database inserts, updates, and deletes
	 */
	public ALO(SQLStream stream) {
		//System.out.println("ALO constructor");
		this.stream = stream;
	}

	/**
	 * sets the allele DAO
	 * @effects Queries a database for the next allele key
	 * @param state - the state object from which to create the DAO
	 * @return ALL_AlleleDAO for convenient access e.g. to get the key
	 * @throws ConfigException if error creating the DAO object
	 * @throws DBException if error creating the DAO object
	 */
	public ALL_AlleleDAO setAllele(ALL_AlleleState state)
			throws ConfigException, DBException {
		//System.out.println("ALO.setAllele");
		alleleDAO = new ALL_AlleleDAO(state);
		return alleleDAO;
	}

	/**
	 * adds a molecular mutation DAO to the set of ALO mutations
	 * @param state the state object from which to create the DAO
	 * @return ALL_AlleleMutationDAO for convenient access e.g. to get the key
	 * @throws ConfigException if error creating the DAO object
	 * @throws DBException if error creating the DAO object
	 */
	public ALL_Allele_MutationDAO addMutation(ALL_Allele_MutationState state)
			throws ConfigException, DBException {
		//System.out.println("ALO.adddMutation");
		ALL_Allele_MutationDAO dao = new ALL_Allele_MutationDAO(state);
		alleleMutationDAOs.add(dao);
		return dao;
	}

	/**
	 * adds an accession DAO to the set of ALO accessions
	 * @effects Queries a database for the next Accession key
	 * @param state the state object from which to create the DAO
	 * @return ACC_AccessionDAO for convenient access e.g. to get the key
	 * @throws ConfigException if error creating the DAO object
	 * @throws DBException if error creating the DAO object
	 */
	public ACC_AccessionDAO addAccession(ACC_AccessionState state)
			throws ConfigException, DBException {
		//System.out.println("ALO.addAccession");
		ACC_AccessionDAO dao = new ACC_AccessionDAO(state);
		accessionDAOs.add(dao);
		return dao;
	}

	/**
	 * sets the set of molecular notes for an ALO
	 * @param noteSet set of MGI_NoteState objects
	 */
	public void setMolecularNotes(HashSet noteSet)
			throws ConfigException, DBException {
		for (Iterator i = noteSet.iterator(); i.hasNext();) {
			noteDAOs.add(new MGI_NoteDAO((MGI_NoteState) i.next()));
		}
	}

	/**
	 * adds a molecular note to the set of molecular notes for an ALO
	 * @param note molecular note for the ALO
	 * @return MGI_NoteDAO for convenient access e.g. to get the key
	 */
	public MGI_NoteDAO addMolecularNote(MGI_NoteState note)
			throws ConfigException, DBException {
		MGI_NoteDAO dao = new MGI_NoteDAO(note);
		noteDAOs.add(dao);
		return dao;
	}

	/**
	 * sets the set of molecular note chunks for an ALO
	 * @param noteChunks ordered set of MGI_NoteChunkState objects
	 */
	public void setMolecularNoteChunks(Vector noteChunks) {
		for (Iterator i = noteChunks.iterator(); i.hasNext();) {
			noteChunkDAOs.add(new MGI_NoteChunkDAO((MGI_NoteChunkState) i.next()));
		}
	}

	/**
	 * adds a molecular not chunk to set of  molecular note chunks for an ALO
	 * @param chunk a MGI_NoteChunkState object
	 * @return MGI_NoteChunkDAO for convenient access e.g. to get the key
	 */
	public MGI_NoteChunkDAO addMolecularNoteChunk(MGI_NoteChunkState chunk) {
		MGI_NoteChunkDAO dao = new MGI_NoteChunkDAO(chunk);
		noteChunkDAOs.add(dao);
		return dao;
	}

	/**
	 * adds a cell line DAO to the set of ALO cell lines
	 * @effects Queries a database for the next cell line key
	 * @param state the state object from which to create the DAO
	 * @return ALL_CellLineDAO for convenient access e.g. to get the key
	 * @throws ConfigException if error creating the DAO object
	 * @throws DBException if error creating the DAO object
	 */
	public ALL_CellLineDAO addCellLine(ALL_CellLineState state)
			throws ConfigException, DBException {
		//System.out.println("ALO.addCellLine adding dao for clID: " + state.getCellLine());
		ALL_CellLineDAO dao = new ALL_CellLineDAO(state);
		cellLineDAOs.add(dao);
		//System.out.println("CellLineDAOs.size: " + cellLineDAOs.size());
		return dao;
	}

	/**
	 * adds a  cell line DAO to the set of ALO cell lines to be updated
	 * @param dao ALL_CellLineDAO to add to the update set
	 */
	public void addCellLineUpdate(ALL_CellLineDAO dao) {
		cellLineUpdateDAOs.add(dao);
	}
	
	/**
	 * adds a allele to cell line association DAO to the set of associations
	 * for this ALO
	 * @effects Queries a database for the next association key
	 * @param state the state object from which to create the DAO
	 * @throws ConfigException if error creating the DAO object
	 * @throws DBException if error creating the DAO object
	 */
	public void addAlleleCellLine(ALL_Allele_CellLineState state)
			throws ConfigException, DBException {
		//System.out.println("ALO.addAlleleCellLine");
		alleleCellLineDAOs.add(new ALL_Allele_CellLineDAO(state));
	}

	/**
	 * Adds reference association DAO to the list of ALO ref associations
	 * @effects Queries a database for the next association key
	 * @param state the state object from which to create the DAO
	 * @return MGI_Reference_AssocDAO for convenient access e.g. to get the key
	 * @throws ConfigException if error creating the DAO object
	 * @throws DBException if error creating the DAO object
	 */
	public MGI_Reference_AssocDAO addRefAssociation(MGI_Reference_AssocState state)
			throws ConfigException, DBException {
		//System.out.println("ALO.addRefAssociation");
		MGI_Reference_AssocDAO dao = new MGI_Reference_AssocDAO(state);
		refAssociationDAOs.add(dao);
		return dao;
	}

	/**
	 * sets the seq allele association
	 * @effects Queries a database for the next association key
	 * @param state the state object from which to create the DAO
	 * @return SEQ_Allele_AssocDAO although probably no need for access to it
	 * @throws ConfigException if error creating the DAO object
	 * @throws DBException if error creating the DAO object
	 */
	public SEQ_Allele_AssocDAO setSeqAlleleAssociation(SEQ_Allele_AssocState state)
			throws ConfigException, DBException {
		//System.out.println("ALO.setSeqAlleleAssociation");
		seqAlleleDAO = new SEQ_Allele_AssocDAO(state);
		return seqAlleleDAO;
	}
    /**
     * Set whether this is an ALO update
     * @param b true if this ALO represents updates to an existing ALO
     */
    public void setIsUpdate(Boolean b) {
        this.isUpdate = b;
    }
	/**
	 * get of allele DAO
	 */
	public ALL_AlleleDAO getAlleleDAO() {
		//return (ALL_AlleleDAO)this.alleleDAO.clone();
		return alleleDAO;
	}

	/**
	 * get  mutation DAOs
	 */
	public HashSet getMutationDAOs() {
		return alleleMutationDAOs;
	}

	/**
	 * get accession DAOs
	 */
	public HashSet getAccessionDAOs() {
		return accessionDAOs;
	}

	/**
	 *  get MGI Notes
	 */
	public HashSet getMGINoteDAO() {
		
		return noteDAOs;
	}

	/**
	 * get the set of MGI Note Chunks
	 */
	public HashSet getMGINoteChunkDAOs() {
		
		return noteChunkDAOs;
	}

	/**
	 * get cell line DAOs
	 */
	public HashSet getCellLineDAOs() {
		
		return cellLineDAOs;
	}

	/**
	 * get cell line DAOs to be updated
	 */
	public HashSet getCellLineUpdateDAOs() {

		return cellLineUpdateDAOs;
	}
	/**
	 * get allele cell line DAOs
	 */
	public HashSet getAlleleCellLineDAOs() {
		return alleleCellLineDAOs;
	}

	/**
	 * get  the set of Integer cell line keys
	 */
	public HashSet getCellLineKeys() {
		HashSet keys = new HashSet();
		for (Iterator i = cellLineDAOs.iterator(); i.hasNext();) {
			ALL_CellLineDAO dao = (ALL_CellLineDAO) i.next();
			keys.add(dao.getKey().getKey());
		}
		return keys;
	}

	/**
	 * get reference association DAOs
	 */
	public HashSet getRefAssociationDAOs() {
		return refAssociationDAOs;
	}

	/**
	 * get sequence allele association DAO
	 */
	public SEQ_Allele_AssocDAO getSeqAlleleAssociationDAO() {
		//return (SEQ_Allele_AssocDAO)this.seqAlleleDAO.clone();
		return seqAlleleDAO;
	}
    /**
     * Get whether this is an ALO update
     */
    public Boolean getIsUpdate() {
        return isUpdate;
    }

	/**
	 * Determines the stream methods for and passes to those methods each of
	 * its DAO objects.
	 * Inserts the allele
	 * Inserts allele mutations
	 * Inserts cell lines
	 * Updates cell Lines
	 * Inserts allele cell line associations
	 * Inserts accessions
	 * Inserts molecular notes
	 * Inserts reference associations
	 * Inserts seq allele associations
	 * @effects Performs database Inserts, updates, and deletes.
	 * @throws DBException if error inserting, updating, or deleting in the
	 * database
	 */
	public void sendToStream() throws DBException {
		Iterator i;
		// insert the allele
		if (alleleDAO != null) {
			//System.out.println("stream.insert(alleleDAO)");
			stream.insert(alleleDAO);
		//System.out.println("after stream.insert(alleleDAO)");
		}

		// insert mutations
		for (i = alleleMutationDAOs.iterator(); i.hasNext();) {
			stream.insert((ALL_Allele_MutationDAO) i.next());
		}

		// insert cell lines
		//System.out.println("CellLineDAOs.size: " + cellLineDAOs.size());
		for (i = cellLineDAOs.iterator(); i.hasNext();) {
			//System.out.println("stream.insert((ALL_CellLineDAO)i.next())");
			stream.insert((ALL_CellLineDAO) i.next());
		}

		// update cell lines
		//System.out.println("CellLineDAOs.size: " + cellLineDAOs.size());
		for (i = cellLineUpdateDAOs.iterator(); i.hasNext();) {
			//System.out.println("stream.insert((ALL_CellLineDAO)i.next())");
			stream.update((ALL_CellLineDAO) i.next());
		}

		// insert allele cell line associations
		for (i = alleleCellLineDAOs.iterator(); i.hasNext();) {
			//System.out.println("stream.insert((ALL_Allele_CellLineDAO)i.next())");
			stream.insert((ALL_Allele_CellLineDAO) i.next());
		}

		// insert the accessions
		for (i = accessionDAOs.iterator(); i.hasNext();) {
			//System.out.println("stream.insert((ACC_AccessionDAO)i.next())");
			stream.insert((ACC_AccessionDAO) i.next());
		}

		// insert molecular notes
		for (i = noteDAOs.iterator(); i.hasNext();) {

			stream.insert((MGI_NoteDAO) i.next());
		}
		for (i = noteChunkDAOs.iterator(); i.hasNext();) {
			stream.insert((MGI_NoteChunkDAO) i.next());
		}

		// insert reference associations
		for (i = refAssociationDAOs.iterator(); i.hasNext();) {
			//System.out.println("stream.insert((MGI_Reference_AssocDAO)i.next())");
			stream.insert((MGI_Reference_AssocDAO) i.next());
		}

		// insert sequence to allele associations
		if (seqAlleleDAO != null) {
			//System.out.println("stream.insert(seqAlleleDAO)");
			stream.insert(seqAlleleDAO);
		}

	}
}

