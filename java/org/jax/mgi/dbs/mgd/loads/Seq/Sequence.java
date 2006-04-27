package org.jax.mgi.dbs.mgd.loads.Seq;

import java.util.*;

import org.jax.mgi.shr.Sets;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.shr.dla.loader.seq.*;

    /**
     * An object that manages a set of DAOs representing a sequence.
     * @has
     *   <UL>
     *   <LI>SEQ_SequenceDAO
     *   <LI>ACC_AccessionDAO's for its primary seqid and any 2ndary seqids
     *   <LI>MGI_ReferenceAssocDAO's for any references associated with the
     *       sequence
     *   <LI>SEQ_SourceAssocDAO's for sources associated with the sequence
     *   <LI>Knows if its SEQ_SequenceDAO exists in MGI or is a new sequence
     *   <LI>If the SEQ_SequenceDAO exists in MGI, knows if its state has
     *       changed
     *   </UL>
     * @does
     *   <UL>
     *   <LI>creates DAO objects for Sequence, primary and 2ndary seqids,
     *       reference association(s) and source association(s).
     *   <LI>Updates a sequence and adds new reference associations in a
     *       database
     *   <LI>Adds a sequence, its seqids, reference and source associations to
     *       a database
     *   <LI>Provides methods to get *copies only* of States for each of its
     *       DAO's
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class Sequence {
    // the sequence
    private SEQ_SequenceSeqloaderDAO sequenceDAO;
    private SEQ_Sequence_RawDAO sequence_RawDAO;

    // the stream used to accomplish the database inserts, updates, deletes
    private SQLStream stream;

    // the primary seqid
    private ACC_AccessionDAO primaryAcc;

    // the set of secondary seqids for this sequence
    private Vector secondaryAcc = new Vector();

    // the set of reference associations for this sequence
    private Vector refAssociations = new Vector();

    // the set of ref keys (Integer) for the sequence we are processing
    // so we may determine if there are any refs for this sequence in MGI that
    // should be 'deleted' (we'll just qc them)
    private HashSet inputRefKeySet = new HashSet();

    // the current set of ref keys (Integer) for this sequence includes
    // existing and new ref keys
    // we use this in order to avoid compare between two
    // MGI_Reference_AssocState objects
    private HashSet currentRefKeySet = new HashSet();

    // the set of source associations for this sequence
    private Vector seqSrcAssoc = new Vector();

    // the set of seqids to delete from the database
    private Vector deleteAcc = new Vector();

    // the set of seqids to add to the database
    private Vector addAcc = new Vector();

    // the set of reference associations to delete from the database
    private Vector deleteRefAssoc = new Vector();

    // the set of reference associations to add to the database
    private Vector addReferenceAssoc = new Vector();

    // the set of source associations to delete from the databaase
    private Vector deleteSeqSrcAssoc = new Vector();

    // the set of source asociations to add to the database
    private Vector addSeqSrcAssoc = new Vector();

    // Updates the SequenceState object. seqUpdater is not initialized if
    // this is a new Sequence
    private SequenceUpdater seqUpdater;

    // Updates the SequenceRawState object. seqRawUpdater is not initialized if
    // this is a new Sequence
    private SequenceRawUpdater seqRawUpdater;


    // true if this object represents a new sequence (not in the database)
    private boolean isNewSequence = true;

    // true if this object represents an existing object in the database and
    // the sequence state needs to be updated
    private boolean isChangedSequence = false;

    // true if this object represents an existing object in the database and
    // the sequence raw state needs to be updated
    private boolean isChangedRawSequence = false;



    // true if this object represents a dummy sequence in MGI
    private boolean isDummySequence = false;


    /**
     * Constructs a Sequence object by creating SEQ_SequenceSeqloaderDAO
     * for 'seqState'
     * @assumes state does not exist in the database
     * @effects queries a database for the next sequence key
     * @param seqState a SequenceState representing a new sequence
     * @param stream the stream which to pass the DAO objects to perform
     * database inserts, updates, and deletes
     * @throws ConfigException if error creating SEQ_SequenceSeqloaderDAO
     * @throws DBException if error creating SEQ_SequenceSeqloaderDAO
     */

    public Sequence(SEQ_SequenceState seqState,
                    SEQ_Sequence_RawState seqRawState,
                    SQLStream stream)
        throws ConfigException, DBException {
        this.stream = stream;
        sequenceDAO = new SEQ_SequenceSeqloaderDAO(seqState);
        sequence_RawDAO = new SEQ_Sequence_RawDAO(seqRawState);
    }

   /**
    * Constructs a Sequence object by creating a SEQ_SequenceSeqloaderDAO
    * for 'state'
    * @assumes state exists in the database
    * @effects queries a database (SequenceUpdater to get logicalDB key)
    * @param state a SEQ_SequenceState representing an existing sequence
    * @param key the SEQ_SequenceKey for 'state'
    * @param stream the stream which to pass the DAO objects to perform
    * database inserts, updates, and deletes
    * @throws DBException if error creating SequenceUpdater
    * @throws ConfigException if error creating SequenceUpdater
    * @throws DLALoggingException if error creating SequenceUpdater
    * @throws CacheException if error creating SequenceUpdater
    * @throws KeyNotFoundException if error creating SequenceUpdater
    */

    public Sequence(SEQ_SequenceState state, SEQ_SequenceKey key,
                    SEQ_Sequence_RawState seqRawState, SQLStream stream)
        throws DBException, ConfigException, DLALoggingException,
            CacheException, KeyNotFoundException {
        this.stream = stream;
        sequenceDAO = new SEQ_SequenceSeqloaderDAO(key, state);
        seqUpdater = SequenceUpdater.getInstance();
        seqRawUpdater = SequenceRawUpdater.getInstance();
        SEQ_Sequence_RawKey rawKey =
            new SEQ_Sequence_RawKey(key.getKey());
        sequence_RawDAO = new SEQ_Sequence_RawDAO(rawKey, seqRawState);
    }

    /**
     * gets a *copy* of the sequence state
     * @assumes Nothing
     * @effects Nothing
     * @return state a *copy* of the sequence state
     */

    public SEQ_SequenceState getSequenceState() {
        return ((SEQ_SequenceDAO)sequenceDAO.clone()).getState();
    }

    /**
     * gets a *copy* of the sequence_raw state
     * @assumes Nothing
     * @effects Nothing
     * @return state a *copy* of the sequence_raw state
     */

    public SEQ_Sequence_RawState getSequenceRawState() {
        return ((SEQ_Sequence_RawDAO)sequence_RawDAO.clone()).getState();
    }


    /**
     * sets the primary accession of a new sequence
     * @assumes Nothing
     * @effects Queries a database for the next Accession key
     * @param state a primary accession state
     * @throws ConfigException if error creating the DAO object
     * @throws DBException
     */

    public void setAccPrimary(ACC_AccessionState state)
        throws ConfigException, DBException {
        primaryAcc = new ACC_AccessionDAO(state);
          addAcc.add(primaryAcc);
    }

    /**
     * sets the primary accession of an existing Sequence
     * @assumes Nothing
     * @effects Nothing
     * @param key the accession key for the primary accession of an
     * existing sequence
     * @param state a primary accession
     */

    public void setAccPrimary(ACC_AccessionKey key,
                              ACC_AccessionState state) {
        primaryAcc = new ACC_AccessionDAO(key, state);
    }

    /**
     * sets the preferred organism key within the internal SEQ_SequenceState
     * object
     * @assumes nothing
     * @effects the SEQ_SequenceState object will be changed
     * @param organismKey the preferred orgainsm key
     */
    public void setPrefferedOrganismKey(int organismKey)
    {
        SEQ_SequenceState state = sequenceDAO.getState();
        state.setOrganismKey(new Integer(organismKey));
    }

    /**
     * gets a *copy* of the primary seqid state of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return primaryAcc a *copy* of the primary seqid state of the sequence
     */

    public ACC_AccessionState getAccPrimary() {
        return primaryAcc.getState();
    }

    /**
     * Adds a ACC_AccessionDAO to the set of DAO's representing secondary
     * seqids of a new sequence
     * @assumes Nothing
     * @effects Queries a database for the next accession key
     * @param state ACC_AccessionState representing a 2ndary id of a new
     *        sequence
     * @throws ConfigException if error creating the DAO object
     * @throws DBException if error creating the DAO object
     */

    public void addAccSecondary(ACC_AccessionState state)
        throws ConfigException, DBException {
        secondaryAcc.add(new ACC_AccessionDAO(state));
        addAcc.add(secondaryAcc.lastElement());
    }

    /**
     * Adds a ACC_AccessionDAO to the set of DAO's representing secondary
     * seqids of an existing sequence
     * @assumes Nothing
     * @effects Nothing
     * @param accKey the ACC_AccessionKey of a 2ndary accession for an
     * existing sequence
     * @param accState a ACC_AccessionState representing a 2ndary accession
     * for an existing sequence
     */

    public void addAccSecondary(ACC_AccessionKey accKey,
                                ACC_AccessionState accState) {
        secondaryAcc.add(new ACC_AccessionDAO(accKey, accState));
    }


    /**
     * gets a *copy* of the set of secondary seqid states of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return Vector a set of *copies* of the secondary seqid states of the
     * sequence
     */

    public Vector getAccSecondary() {
        Vector v = new Vector();
        Iterator i = secondaryAcc.iterator();
        while (i.hasNext()) {
            v.add(((ACC_AccessionDAO)i.next()).getState());
        }
        return v;
    }

    /**
     * deletes 'accState' from the set of 2ndary seqids
     * @assumes Nothing
     * @effects Nothing
     * @param state a secondary seqid to be deleted
     */

    public void delete2ndaryAcc(ACC_AccessionState state) {
        // implement later; need a equals method for ACC_AccessionState
        // unless equal means same reference ...
    }

    /**
     * Adds a MGI_Reference_AssocDAO representing a new reference to the set of
     * reference associations for the  sequence (if it does not already have
     * an association for the reference). Used when building a Sequence that
     * represents a new sequence to MGI or when adding a reference to an
     * existing sequence in MGI
     * @assumes Nothing
     * @effects Queries a database for the next reference assoc key
     * @param state MGI_Reference_AssocState from which to create a DAO to add
     * to the set of reference associations of a new sequence
     * @throws ConfigException if error creating MGI_Reference_AssocDAO
     * @throws DBException if error creating MGI_Reference_AssocDAO
     */

    public void addRefAssoc(MGI_Reference_AssocState state)
        throws ConfigException, DBException {

        Integer refKey = state.getRefsKey();

        // if we haven't already created an association for this reference
        if(! currentRefKeySet.contains(refKey)) {
            // add a new DAO to the set of ref associations for this sequence
            refAssociations.add(new MGI_Reference_AssocDAO(state));

            // add reference to the set of new references for the sequence
            addReferenceAssoc.add(refAssociations.lastElement());

            // add the refs key to the full set (existing and new) of refs
            // for the sequence
            currentRefKeySet.add(refKey);
        }
        // add the refs key to the set of incoming sequence refs
        inputRefKeySet.add(refKey);
    }

    /**
     * Adds a MGI_Reference_AssocDAO to the set of reference associations if
     * this Sequence represent an existing sequence. Used when building the
     * sequence references from the set of references found in MGI
     * @assumes Nothing
     * @effects Nothing
     * @param key the MGI_Reference_AssocKey of a reference for an existing
     * sequence
     * @param state MGI_Reference_AssocState from which to create a DAO to add
     * to the set of reference associations of an existing sequence
     */

    public void addRefAssoc(MGI_Reference_AssocKey key,
                            MGI_Reference_AssocState state) {
        // add a new DAO to the set of ref associations for this sequence
        refAssociations.add(new MGI_Reference_AssocDAO(key, state));

        // add the refs key to the set of refs for this sequence
        currentRefKeySet.add(state.getRefsKey());
    }

    /**
     * deletes 'delRefAssoc' from the set of reference associations
     * @assumes Nothing
     * @effects Nothing
     * @param delRefAssoc the reference association to be deleted
     */
    public void deleteRefAssoc(MGI_Reference_AssocState delRefAssoc) {
        // implement later; need a equals method for MGI_Reference_AssocState
        // unless equal means same reference ...
    }

    /**
     * gets a *copy* of the set of reference association states of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return Vector set of *copies* of the reference association states
     * of the sequence
     */

    public Vector getRefAssoc() {
      Vector v = new Vector();
      Iterator i = refAssociations.iterator();
      while (i.hasNext()) {
          v.add(((MGI_Reference_AssocDAO)i.next()).getState());
      }
      return v;
    }

    /**
     * determines the set of MGI_Reference_Associations in MGI that are not
     * referenced in the provider sequence record
     * @assumes Nothing
     * @effects Nothing
     * @return Vector of *copies* of the reference association states that
     * are not referenced in the provider sequence record
     */

    public Vector getOldRefAssociations() {
         Set diffSet = Sets.difference(currentRefKeySet, inputRefKeySet);
         int diffSetSize = diffSet.size();
         Vector refVector = new Vector(diffSetSize);
         if (diffSetSize > 0 ) {
              for (Iterator i = refAssociations.iterator(); i.hasNext();) {
                  MGI_Reference_AssocState state =
                      ((MGI_Reference_AssocDAO)i.next()).getState();
                  if (diffSet.contains(state.getRefsKey()) ) {
                      refVector.add(state);
                  }
              }
         }
         return refVector;
    }

    /**
    * adds a SEQ_Source_AssocState to the set of source associations of a new
    *  sequence
    * @assumes Nothing
    * @effects Queries a database for the next source assoc key
    * @param state a SEQ_Source_AssocState for a new sequence
    * @throws ConfigException if error creating SEQ_Source_AssocDAO object
    * @throws DBException if error creating SEQ_Source_AssocDAO object
    */

    public void addSeqSrcAssoc(SEQ_Source_AssocState state)
        throws ConfigException, DBException {
        seqSrcAssoc.add(new SEQ_Source_AssocDAO(state));
        addSeqSrcAssoc.add(seqSrcAssoc.lastElement());
    }

    /**
    * adds a SEQ_Source_AssocState to the set of source associations of an
    * existing sequence
    * @assumes Nothing
    * @effects Nothing
    * @param key the SEQ_Source_AssocKey for an existing sequence
    * @param state a SEQ_Source_AssocState
    */

    public void addSeqSrcAssoc(SEQ_Source_AssocKey key,
                               SEQ_Source_AssocState state) {
         seqSrcAssoc.add(new SEQ_Source_AssocDAO(key, state));
     }


    /**
     * gets a copy of the set of source association states of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @return Vector set of *copies* of the source association states of
     * the sequence
     */

    public Vector getSeqSrcAssoc() {
      Vector v = new Vector();
      Iterator i = seqSrcAssoc.iterator();
      while (i.hasNext()) {
          v.add(((SEQ_Source_AssocDAO)i.next()).getState());
      }
      return v;
    }

    /**
     * deletes 'delSrcAssoc' from the set of source associations of the
     * sequence
     * @assumes Nothing
     * @effects Nothing
     * @param delSrcAssoc the source association to be deleted
     */

    public void deleteSeqSrcAssoc(SEQ_Source_AssocState delSrcAssoc) {
        // implement later; need a equals method for MGI_Reference_AssocState
        // unless equal means same reference ...
        // the seqloader will not be deleting source associations
    }

    /**
     * gets the sequence key as an Integer
     * @assumes Nothing
     * @effects Nothing
     * @return Integer sequence key
     */

    public Integer getSequenceKey() {
            return sequenceDAO.getKey().getKey();

    }

    /**
     * Updates the sequence object attributes in preparation for a database
     * update
     * @assumes Nothing
     * @effects Nothing
     * @param updateFrom - the set of attributes from which to update the
     * sequence
     * @throws DBException if error querying a database for attribute history
     * @throws CacheException if error using a lookup
     */

    public void updateSequenceState(SEQ_SequenceState updateFrom)
        throws DBException, CacheException {
        isChangedSequence = seqUpdater.updateSeq( this.sequenceDAO.getState(),
                                sequenceDAO.getKey().getKey(),
                                updateFrom);
    }

    /**
     * Updates the sequence object attributes in preparation for a database
     * update
     * @assumes Nothing
     * @effects Nothing
     * @param updateFrom - the set of attributes from which to update the
     * sequence
     * @throws DBException if error querying a database for attribute history
     * @throws CacheException if error using a lookup
     */

    public void updateSequenceRawState(SEQ_Sequence_RawState updateFrom)
        throws DBException, CacheException {
        isChangedRawSequence = seqRawUpdater.updateSeq(
            this.sequence_RawDAO.getState(),
            sequence_RawDAO.getKey().getKey(), updateFrom);
    }


    /**
     * sets the isNewSequence attribute
     * @assumes Nothing
     * @effects Nothing
     * @param isNew true if this is a new sequence
     */

    public void setIsNewSequence (boolean isNew) {
        isNewSequence = isNew;
    }

    /**
     * gets the isNewSequence attribute
     * @assumes Nothing
     * @effects Nothing
     * @return true if this is a new sequence
     */

    public boolean getIsNewSequence() {
        return isNewSequence;
    }

    /**
     * Sets the isChangedSequence attribute
     * @assumes Nothing
     * @effects Nothing
     * @param isChanged true this is a sequence in MGI needs updating
     */

    public void setIsChangedSequence (boolean isChanged) {
        isChangedSequence = isChanged;
    }

    /**
     * gets the isChangedSequence attribute
     * @assumes Nothing
     * @effects Nothing
     * @return true if this is a sequence in MGI that needs updating
     */

    public boolean getIsChangedSequence() {
        return isChangedSequence;
    }

    /**
     * sets the isDummySequence attribute
     * @assumes Nothing
     * @effects Nothing
     * @param isDummy true if this sequence is in MGI and is a dummy sequence
     */

    public void setIsDummySequence (boolean isDummy) {
        isDummySequence = isDummy;
    }

    /**
     * gets the isDummySequence attribute
     * @assumes Nothing
     * @effects Nothing
     * @return true if this is a sequence in MGI and is a dummy sequence
     */

    public boolean getIsDummySequence() {
        return isDummySequence;
    }

    /**
     * Determines the stream methods for and passes to those methods each of
     * its DAO objects.
     * Inserts or updates the sequence.
     * Inserts and deletes reference associations.
     * Inserts and deletes source associations.
     * Inserts primary seqid.
     * Inserts and deletes 2ndary seqids.
     * @assumes Nothing
     * @effects Performs database Inserts, updates, and deletes.
     * @throws DBException if error inserting, updating, or deleting in the
     * database
     */

    public void sendToStream() throws DBException {
        Iterator i;
        // New sequence - insert it with its accession and source associations
        // if bcp insert MGI_AttributeHistory
        if(isNewSequence) {
            stream.insert(sequenceDAO);
            stream.insert(sequence_RawDAO);
            i = addAcc.iterator();
            while(i.hasNext()) {
                stream.insert((ACC_AccessionDAO)i.next());
            }
            i = addSeqSrcAssoc.iterator();
            while(i.hasNext()) {
                stream.insert((SEQ_Source_AssocDAO)i.next());
            }
            // If bcp trigger won't add when adding sequence
            if (stream.isBCP()) {
                MGI_AttributeHistoryState typeHistoryState =
                    new MGI_AttributeHistoryState();
                typeHistoryState.setObjectKey(sequenceDAO.getKey().getKey());
                typeHistoryState.setMGITypeKey(
                    new Integer(MGITypeConstants.SEQUENCE));
                typeHistoryState.setColumnName(
                    MGD.seq_sequence._sequencetype_key);
                stream.insert(new MGI_AttributeHistoryDAO(typeHistoryState));
            }
        }
        // Existing sequence that needs updating - update it
        else if (isChangedSequence || isChangedRawSequence) {
            if (isChangedSequence)
                stream.update(sequenceDAO);
            if (isChangedRawSequence)
                stream.update(sequence_RawDAO);
        }
        else if (isDummySequence ) {
            stream.delete(sequenceDAO);
            stream.delete(sequence_RawDAO);
        }
        else {
            // for debugging log to debug or print out
            System.out.println("Unhandled case in Sequence.sendToStream");

        }
        // Whether existing or new sequence - add references
        i = addReferenceAssoc.iterator();
        while(i.hasNext()) {
            stream.insert((MGI_Reference_AssocDAO)i.next());
        }
    }
}
