//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.dla.seqloader.RefAssocRawAttributes;

/**
 * @is An object that represents raw PubMed and Medline reference associations
 *     for a sequence
 * @has
 *   <UL>
 *   <LI>A RefAssocRawAttributes representing a pubmed id
 *   <LI>A RefAssocRawAttributes representing a medline id
 *   </UL>
 * @does
 *   <UL>
 *   <LI>provides getters and setters for each id
 *   <LI>resets itself
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */


public class SeqRefAssocPair{
    // pubmed reference association
    private RefAssocRawAttributes pubmed = null;

    // medline reference association
    private RefAssocRawAttributes medline = null;

    /**
     * Constructs an empty SeqRefAssoc pair
     * @assumes
     * @effects
     * @param None
     * @throws Nothing
     */

    public SeqRefAssocPair() { }

    /**
     * Constructs a SeqRefAssocPair with a PubMed and a Medline id
     * @assumes
     * @effects
     * @param pubmed PubMed id for a sequence
     * @param medline Medline id to associate for a sequence
     * @throws
     */

    public SeqRefAssocPair(RefAssocRawAttributes pubmed,
                           RefAssocRawAttributes medline) {
        this.pubmed = pubmed;
        this.medline = medline;
    }

    /**
     * sets the PubMed RefAssocRawAttributes object
     * @assumes Nothing
     * @effects Nothing
     * @param pubmed a RefAssocRawAttributes
     * @return Nothing
     * @throws Nothing
     */

    public void setPubmed(RefAssocRawAttributes pubmed) {
        this.pubmed = pubmed;
    }

    /**
     * sets the Medline RefAssocRawAttributes object
     * @assumes Nothing
     * @effects Nothing
     * @param medline a RefAssocRawAttributes
     * @return Nothing
     * @throws Nothing
     */

    public void setMedline(RefAssocRawAttributes medline) {
        this.medline = medline;
    }

    /**
     * gets the PubMed RefAssocRawAttributes object
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the pubmed RefAssocRawAttributes
     * @throws Nothing
     */

    public RefAssocRawAttributes getPubmed() {
        return pubmed;
    }

    /**
     * gets the Medline RefAssocRawAttributes object
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return the medline RefAssocRawAttributes
     * @throws Nothing
     */

    public RefAssocRawAttributes getMedline() {
        return medline;
    }

    /**
     * Resets reference association objects to null
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return Nothing
     * @throws Nothing
     */
     public void reset () {
         pubmed = null;
         medline = null;
     }
}
 //  $Log$
 //  Revision 1.2  2003/12/20 16:25:20  sc
 //  changes made from code review~
 //
 //  Revision 1.1  2003/12/08 18:40:41  sc
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