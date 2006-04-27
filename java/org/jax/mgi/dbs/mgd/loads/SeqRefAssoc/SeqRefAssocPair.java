package org.jax.mgi.dbs.mgd.loads.SeqRefAssoc;

/**
 * An object that holds two RefAsocRawAttributes for the same reference. One
 *     Medline and one Pubmed.
 * @has
 *   <UL>
 *   <LI>A RefAssocRawAttributes representing a pubmed id
 *   <LI>A RefAssocRawAttributes representing a medline id
 *   </UL>
 * @does
 *   <UL>
 *   <LI>provides getters and setters for each RefAssocRawAttributes
 *   <LI>resets itself
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */


public class SeqRefAssocPair{
    // pubmed raw reference association
    private RefAssocRawAttributes pubmed = null;

    // medline raw reference association
    private RefAssocRawAttributes medline = null;

    /**
     * Constructs an empty SeqRefAssoc pair
     * @assumes Nothing
     * @effects Nothing
     */

    public SeqRefAssocPair() { }

    /**
     * Constructs a SeqRefAssocPair with a PubMed and a Medline id
     * @assumes Nothing
     * @effects Nothing
     * @param pubmed PubMed id for a reference
     * @param medline Medline id for the same reference
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
     * @param pubmed a RefAssocRawAttributes for a pubmed reference
     */

    public void setPubmed(RefAssocRawAttributes pubmed) {
        this.pubmed = pubmed;
    }

    /**
     * sets the Medline RefAssocRawAttributes object
     * @assumes Nothing
     * @effects Nothing
     * @param medline a RefAssocRawAttributes for a medline reference
     */

    public void setMedline(RefAssocRawAttributes medline) {
        this.medline = medline;
    }

    /**
     * gets the PubMed RefAssocRawAttributes object
     * @assumes Nothing
     * @effects Nothing
     * @return the  RefAssocRawAttributes for a pubmed reference
     */

    public RefAssocRawAttributes getPubmed() {
        return pubmed;
    }

    /**
     * gets the Medline RefAssocRawAttributes object
     * @assumes Nothing
     * @effects Nothing
     * @return the RefAssocRawAttributes for a medline reference
     */

    public RefAssocRawAttributes getMedline() {
        return medline;
    }

    /**
     * Resets reference association objects to null
     * @assumes Nothing
     * @effects Nothing
     */
     public void reset () {
         pubmed = null;
         medline = null;
     }
}
