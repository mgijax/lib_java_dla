//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

import java.util.*;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.dbs.mgd.dao.*;

// CLASS header
    /**
     * @is An object that manages a set of DAOs representing a sequence.
     * @has
     *   <UL>
     *   <LI>SEQ_SequenceDAO
     *   <LI>ACC_AccessionDAO's for its primary seqid and any 2ndary seqids
     *   <LI>MGI_ReferenceAssocDAO's for any references associated with the sequence
     *   <LI>SEQ_SourceAssocDAO's for sources associated with the sequence
     *   <LI>Knows if its SEQ_SequenceDAO exists in MGI or is a new sequence
     *   <LI>If the SEQ_SequenceDAO exists in MGI, knows if its state has changed
     *   </UL>
     * @does
     *   <UL>
     *   <LI>creates DAO objects for Sequence, primary and 2ndary seqids,
     *       reference and source associations.
     *   <LI>Updates a sequence and adds new reference associations in a database
     *   <LI>Adds a sequence, its seqids, reference and source associations to
     *       a database
     *   <LI>Provides methods to get *copies only* of States for each of its DAO's
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class Sequence {
    // the sequence
    private SEQ_SequenceDAO sequenceDAO;

    // the primary seqid
    private ACC_AccessionDAO primaryAcc;

    // the set of secondary seqids for this sequence
    private Vector secondaryAcc = new Vector();

    // the set of reference associations for this sequence
    private Vector refAssociations = new Vector();

    // the set of source associations for this sequence
    private Vector seqSrcAssoc = new Vector();

    // the set of seqids to delete from the database
    private Vector deleteAcc = new Vector();

    // the set of seqids to add to the database
    private Vector addAcc = new Vector();

    // the set of reference associations to delete from the database
    private Vector deleteRefAssoc = new Vector();

    // the set of reference associations to add to the database
    private Vector addRefAssoc = new Vector();

    // the set of source associations to delete from the databaase
    private Vector deleteSeqSrcAssoc = new Vector();

    // the set of source asociations to add to the database
    private Vector addSeqSrcAssoc = new Vector();

    // an iterator to reuse
    Iterator i;

    // Updates the SequenceState object
    private SequenceUpdater seqUpdater;

    // the stream used to accomplish the database inserts, updates, deletes
    private SQLStream stream;

    // true if this object represents a new sequence (not in the database)
    private boolean isNewSequence = true;

    // true if this object represents an existing object in the database and
    // its SEQ_SequenceDAO state needs to be updated in the database
    private boolean isChangedSequence = false;


    /**
     * Constructs a Sequence object by creating a SequenceKey and SequenceDAO
     * for 'state'
     * @assumes state does not exist in the database
     * @effects
     * @param state a SequenceState representing a new sequence
     * @param stream the stream which to pass the DAO objects to perform database
     *        inserts, updates, and deletes
     * @throws
     */

    public Sequence(SEQ_SequenceState state, SQLStream stream)
        throws ConfigException, DBException{
        this.stream = stream;
        sequenceDAO = new SEQ_SequenceDAO(state);
    }

   /**
    * Constructs a Sequence object by creating a SequenceDAO for 'state'
    * @assumes state exists in the database
    * @effects
    * @param state a SequenceState representing an existing sequence
    * @param key the SequenceKey of an existing sequence
    * @param stream the stream which to pass the DAO objects to perform database
    *        inserts, updates, and deletes
    * @throws DBException
    */

    public Sequence(SEQ_SequenceState state, SEQ_SequenceKey key, SQLStream stream)
        throws DBException {
        this.stream = stream;
        sequenceDAO = new SEQ_SequenceDAO(key, state);
    }

    /**
     * gets a *copy* of the sequence state
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return state a *copy* of the sequence state
     * @throws Nothing
     */

    public SEQ_SequenceState getSequenceState() {
        //implement later, need a copy method in SEQ_SequenceState
        return null;
    }

    /**
     * sets the primary accession of a new sequence
     * @assumes Nothing
     * @effects Nothing
     * @param primary a primary accession state
     * @return  nothing
     * @throws Nothing
     */

    public void setAccPrimary(ACC_AccessionState primary)
        throws ConfigException, DBException {
        primaryAcc = new ACC_AccessionDAO(primary);
        addAcc.add(primaryAcc);
    }

    /**
     * sets the primary accession of an existing Sequence
     * @assumes Nothing
     * @effects Nothing
     * @param primaryAccKey the accession key for the primary accession of an
     * existing sequence
     * @param primaryAccState a primary accession
     * @return  nothing
     * @throws Nothing
     */

    public void setAccPrimary(ACC_AccessionKey key,
                              ACC_AccessionState state)

        throws DBException {
        primaryAcc = new ACC_AccessionDAO(key, state);
    }

    /**
     * gets a *copy* of the primary seqid state of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return primaryAcc a *copy* of the primary seqid state of the sequence
     * @throws Nothing
     */

    public ACC_AccessionState getAccPrimary() {
        // implement later - need a copy method in ACC_AccessionState
        return null;
    }



    /**
     * Adds a ACC_AccessionDAO to the set of DAO's representing secondary seqids of
     * a new sequence
     * @assumes Nothing
     * @effects Nothing
     * @param accState ACC_AccessionState representing a 2ndary id of a new
     *        sequence
     * @return
     * @throws Nothing
     */

    public void addAccSecondary(ACC_AccessionState state)
        throws ConfigException, DBException{
        secondaryAcc.add(new ACC_AccessionDAO(state));
        addAcc.add(secondaryAcc.lastElement());

    }

    /**
     * Adds a ACC_AccessionDAO to the set of DAO's representing secondary seqids of
     * an existing sequence
     * @assumes Nothing
     * @effects Nothing
     * @param accKey the ACC_AccessionKey of a 2ndary accession for an existing sequence
     * @param accState a ACC_AccessionState representing a 2ndary accession for an
     *        existing sequence
     * an existing sequence
     * @return
     * @throws Nothing
     */

    public void addAccSecondary(ACC_AccessionKey accKey, ACC_AccessionState accState)
        throws DBException{
        secondaryAcc.add(new ACC_AccessionDAO(accKey, accState));
    }


    /**
     * gets a *copy* of the set of secondary seqid states of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Vector a set of *copies* of the secondary seqid states of the
     * sequence
     * @throws Nothing
     */

    public Vector getAccSecondary() {
        // implement later
        return null;
    }

    /**
     * deletes 'accState' from the set of 2ndary seqids
     * @assumes Nothing
     * @effects Nothing
     * @param accState a secondary seqid to be deleted
     * @return Nothing
     * @throws Nothing
     */

    public void delete2ndaryAcc(ACC_AccessionState state) {
        // implement later; need a equals method for ACC_AccessionState
        // unless equal means same reference ...
    }

    /**
     * Adds a MGI_Reference_AssocDAO to the set of reference associations of
     * a new sequence
     * @assumes Nothing
     * @effects Nothing
     * @param state MGI_Reference_AssocState from which to create a DAO to add to
     *        the set of reference associations of a new sequence
     * @return Nothing
     * @throws Nothing
     */

    public void addRefAssoc(MGI_Reference_AssocState state)
        throws ConfigException, DBException {
        refAssociations.add(new MGI_Reference_AssocDAO(state));
        addRefAssoc.add(refAssociations.lastElement());
    }

    /**
     * Adds a MGI_Reference_AssocDAO to the set of reference associations of
     * an existing sequence
     * @assumes Nothing
     * @effects Nothing
     * @param key the MGI_Reference_AssocKey of a reference for an existing
     * sequence
     * @param state MGI_Reference_AssocState from which to create a DAO to add to
     *        the set of reference associations of an existing sequence
     * @return Nothing
     * @throws Nothing
     */

    public void addRefAssoc(MGI_Reference_AssocKey key, MGI_Reference_AssocState state)
        throws DBException {
        refAssociations.add(new MGI_Reference_AssocDAO(key, state));
    }

    /**
     * deletes 'delRefAssoc' from the set of reference associations
     * @assumes Nothing
     * @effects Nothing
     * @param delRefAssoc the reference association to be deleted
     * @return nothing
     * @throws Nothing
     */
    public void deleteRefAssoc(MGI_Reference_AssocState delRefAssoc) {
        // implement later; need a equals method for MGI_Reference_AssocState
        // unless equal means same reference ...
    }

    /**
     * gets a *copy* of the set of reference association states of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Vector set of *copies* of the reference association states
     * of the sequence
     * @throws Nothing
     */

    public Vector getRefAssoc() {
        //implement later; need a copy method in MGI_Reference_AssocState
        return null;
    }


    /**
    * adds a SEQ_Source_AssocState to the set of source associations of a new
    *  sequence
    * @assumes Nothing
    * @effects Nothing
    * @param state a SEQ_Source_AssocState for a new sequence
    * @return Nothing
    * @throws Nothing
    */

    public void addSeqSrcAssoc(SEQ_Source_AssocState state)
        throws ConfigException, DBException {
        seqSrcAssoc.add(new SEQ_Source_AssocDAO(state));
        addSeqSrcAssoc.add(seqSrcAssoc.lastElement());
    }

    /**
    * adds a SEQ_Source_AssocState to the set of source associations of an existing
    *  sequence
    * @assumes Nothing
    * @effects Nothing
    * @param key the SEQ_Source_AssocKey for an existing sequence
    * @param state a SEQ_Source_AssocState
    * @return Nothing
    * @throws Nothing
    */

    public void addSeqSrcAssoc(SEQ_Source_AssocKey key,
                               SEQ_Source_AssocState state)
         throws DBException {
         seqSrcAssoc.add(new SEQ_Source_AssocDAO(key, state));
     }


    /**
     * gets a copy of the set of source association states of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Vector set of *copies* of the source association states of
     * the sequence
     * @throws Nothing
     */

    public Vector getSeqSrcAssoc() {
        // implement later
        return null;
    }

    /**
     * deletes 'delSrcAssoc' from the set of source associations of the sequence
     * @assumes Nothing
     * @effects Nothing
     * @param delSrcAssoc the source association to be deleted
     * @return Nothing
     * @throws Nothing
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
     * @param Nothing
     * @return Integer sequence key
     * @throws Nothing
     */

    public Integer getSequenceKey() {
            return sequenceDAO.getKey().getKey();

    }

    /**
     * Updates the sequence object attributes in preparation for a database update
     * @assumes Nothing
     * @effects Nothing
     * @param updateFrom - the set of attributes from which to update the sequence
     * @return
     * @throws Nothing
     */

    public void updateSequenceState(SEQ_SequenceState updateFrom) {
        // pass updateFrom to the SequenceUpdater
    }

    /**
     * Determines the stream method for and passes to that method each of
     * its objects.
     * Inserts or updates the sequence.
     * Inserts and deletes reference associations.
     * Inserts and deletes source associations.
     * Inserts primary seqid.
     * Inserts and deletes 2ndary seqids.
     * @assumes Nothing
     * @effects Performs database Inserts, updates, and deletes.
     * @param None
     * @return Nothing
     * @throws Nothing
     */

    public void sendToStream() throws DBException{
        // New sequence - insert it with its accession and source associations
        if(isNewSequence == true) {
            stream.insert(sequenceDAO);
            i = addAcc.iterator();
            while(i.hasNext()) {
                stream.insert((ACC_AccessionDAO)i.next());
            }
            i = addSeqSrcAssoc.iterator();
            while(i.hasNext()) {
                stream.insert((SEQ_Source_AssocDAO)i.next());
            }
        }
        // Existing sequence - update it
        else {
            stream.update(sequenceDAO);
        }
        // Whether existing or new sequence - add references
        i = addRefAssoc.iterator();
        while(i.hasNext()) {
            stream.insert((MGI_Reference_AssocDAO)i.next());
        }
    }
}

//  $Log$
//  Revision 1.2  2003/12/20 16:25:21  sc
//  changes made from code review~
//
//  Revision 1.1  2003/12/08 18:40:43  sc
//  initial commit
//

/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/