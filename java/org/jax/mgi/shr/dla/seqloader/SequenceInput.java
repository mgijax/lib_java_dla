//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

import java.util.Vector;

import org.jax.mgi.dbs.mgd.MolecularSource.MSRawAttributes;

/**
 * @is An object that represents data from a DataProvider sequence record
 *     and/or Configuration in its raw form. (SequenceRawAttributes,
 *     AccessionRawAttributes (for primary and 2ndary ids),
 *     RefAssocRawAttributes (for PubMed and Medline reference associations), and
 *     MSRawAttributes (for source associations) )

 * @has
 *   <UL>
 *   <LI>A SequenceRawAttributes
 *   <LI>A Vector of MSRawAttributes (1 or more)
 *   <LI>A Vector of RefAssocRawAttributes (0 or more)
 *   <LI>An AccessionRawAttributes for the primary seqid of the sequence
 *   <LI>A Vector of AccessionRawAttributes (0 ore more)for any
 *        secondary accession ids for a sequence
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Provides getters and setters for its attributes
 *   <LI>Resets itself
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SequenceInput {
    // raw sequence attributes
    private SequenceRawAttributes seq = null;

    // a set of MSRawAttributes
    private Vector mSources = new Vector();

    // a set RefAssociationRawAttributes
    private Vector refs = new Vector();

    // raw accession attributes for the primary seqid
    private AccessionRawAttributes primaryAcc = null;

    // a set of AccessionRawAttributes for secondary seqids
    private Vector secondaryAcc = new Vector();

    /**
     * set the raw sequence
     * @assumes Nothing
     * @effects Nothing
     * @param seq the value which to set the raw sequence
     */

    public void setSeq (SequenceRawAttributes seq) {this.seq = seq;}

    /**
     * set the set of raw molecular sources
     * @assumes Nothing
     * @effects Nothing
     * @param mSources Vector of MSRawAttributes
     */

    public void setMSources (Vector mSources) {this.mSources = mSources;}

    /**
     * add one raw molecular source to the set
     * @assumes Nothing
     * @effects Nothing
     * @param ms an MSRawAttributes to add to the set of raw MS's
     */

    public void addMSource (MSRawAttributes ms) {
        mSources.add(ms);
    }

    /**
     * set the set of reference associations
     * @assumes Nothing
     * @effects Nothing
     * @param refs A Vector of RefAssocRawAttributes
     */

    public void setRefs(Vector refs) {this.refs = refs;}

    /**
     * add one raw reference association to the set
     * @assumes Nothing
     * @effects Nothing
     * @param ref a SeqRefAssocPair to add to the set of reference associations
     */

    public void addRef(SeqRefAssocPair ref) {refs.add(ref);}

    /**
     * add one raw reference association to the set
     * @assumes Nothing
     * @effects Nothing
     * @param ref a RefAssocRawAttributes to add to the set of reference associations
     */

    public void addRef(RefAssocRawAttributes ref) {refs.add(ref);}


    /**
     * set the raw primary accession
     * @assumes Nothing
     * @effects Nothing
     * @param primaryAcc An AccessionRawAttributes representing the primary
     *        seqid
     */

    public void setPrimaryAcc(AccessionRawAttributes primaryAcc) {
        this.primaryAcc = primaryAcc;
    }

    /**
     * set the set of raw secondary accessions
     * @assumes Nothing
     * @effects Nothing
     * @param secondaryAcc A Vector of AccessionRawAttributes representing
     *        the set of 2ndary accession ids
     */

    public void setSecondary(Vector secondaryAcc) {
         this.secondaryAcc = secondaryAcc;
    }

    /**
     * add one secondary accession to the set
     * @assumes Nothing
     * @effects Nothing
     * @param secondary an AccessionRawAttributes
     */

    public void addSecondary(AccessionRawAttributes secondary) {
         secondaryAcc.add(secondary);
    }

    /**
     * get the raw sequence
     * @assumes Nothing
     * @effects Nothing
     * @return SequenceRawAttributes
     */

    public SequenceRawAttributes getSeq() {
        return seq;
    }

    /**
     * get the set of raw sources
     * @assumes Nothing
     * @effects Nothing
     * @return Vector of MSRawAttributes
     */

    public Vector getMSources() {
        return mSources;
    }

    /**
     * get the set of raw reference associations
     * @assumes Nothing
     * @effects Nothing
     * @return Vector of RefAssocRawAttributes
     */

    public Vector getRefs() {
        return refs;
    }

    /**
     * get the raw primary accession
     * @assumes Nothing
     * @effects Nothing
     * @return AccessionRawAttributes representing the primary seqid
     */

    public AccessionRawAttributes getPrimaryAcc() {
        return primaryAcc;
    }

    /**
     *get the set of raw secondary accessions
     * @assumes Nothing
     * @effects Nothing
     * @return Vector of AccessionRawAttributes representing the 2ndary seqid(s)
     */

    public Vector getSecondary() {
        return secondaryAcc;
    }

    /**
     * clears Vectors and sets other objects to null
     * @assumes Nothing
     * @effects Nothing
     */

    public void reset() {
        seq = null;
        mSources.clear();
        refs.clear();
        primaryAcc = null;
        secondaryAcc.clear();
    }
}
//  $Log$
//  Revision 1.4  2004/06/30 17:25:36  sc
//  merging sc2 branch to trunk
//
//  Revision 1.3.4.1  2004/05/18 15:32:48  sc
//  updated class/method headers
//
//  Revision 1.3  2004/04/06 15:52:57  mbw
//  added new method to add a RefAssocRawAttrinute
//
//  Revision 1.2  2004/02/25 21:42:40  mbw
//  fixed compiler warnings only
//
//  Revision 1.1  2004/01/06 20:09:46  mbw
//  initial version imported from lib_java_seqloader
//
//  Revision 1.2  2003/12/20 16:25:22  sc
//  changes made from code review~
//
//  Revision 1.1  2003/12/08 18:40:44  sc
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
