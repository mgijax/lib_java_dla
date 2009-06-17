package org.jax.mgi.shr.dla.input.alo;

import java.util.HashSet;

import org.jax.mgi.dbs.mgd.loads.Alo.*;
import org.jax.mgi.dbs.mgd.loads.Acc.*;
import org.jax.mgi.dbs.mgd.loads.MGI.MGINote;
import org.jax.mgi.dbs.mgd.loads.Alo.SeqAlleleAssocRawAttributes;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.RefAssocRawAttributes;


/**
 * An object that represents data from a DataProvider ALO record
 *     and/or Configuration in its raw form. 
 * @has
 *   <UL>
 *   <LI>A AlleleRawAttributes
 *   <LI>A Set of CellLineRawAttributes
 *   <LI>A Set of AccessionRawAttributes
 *   <UL>
 *      <LI>Mutant Cell Line ID to Cell Line association
 *       <LI>Allele MGI ID
 *   </UL>
 *   <LI>A Set of Allele Molecular Mutation Strings
 *   <LI>A MGINoteRawAttributes for Molecular Notes
 *   <LI>A Set of RefAssocRawAttributes
 *   <UL>
 *     <LI>Load reference
 *     <LI>Other references e.g. sequence refs for gene traps
 *   </UL>
 *   <LI>SeqAlleleAssocRawAttributes
 *   <LI>The complete ALO Record - for writing out repeated records in the input
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

public class ALORawInput {
    // raw allele attributes
    private AlleleRawAttributes allele = null;

    // a set of CellLineRawAttributes
    private HashSet cellLineSet = new HashSet();

    // a set of AccessionRawAttributes
    private HashSet accessionSet = new HashSet();

    // a set of mutation strings
    private HashSet mutationSet = new HashSet();

    // An MGI Note - the molecular note
    private MGINote molecularNote = null;
    
    // a set of RefAssocRawAttributes
    private HashSet referenceSet = new HashSet();
    
    // a sequence to allele association
    private SeqAlleleAssocRawAttributes seqAssoc = null;
    
    // The complete input record
    private String inputRecord = null;
    
    /**
     * set the raw allele
     * @assumes Nothing
     * @effects Nothing
     * @param allele the raw allele
     */

    public void setAllele (AlleleRawAttributes allele) {this.allele = allele;}

    /**
     * set the set of raw cell lines
     * @assumes Nothing
     * @effects Nothing
     * @param cellLineSet the set of raw  cell lines
     */

    public void setCellLines (HashSet cellLineSet) {this.cellLineSet = 
	cellLineSet;}

    /**
     * add one raw cell line to the set
     * @assumes Nothing
     * @effects Nothing
     * @param cl a CellLineRawAttributes to add to the set of raw cell lines
     */

    public void addCellLine (CellLineRawAttributes cl) {
        cellLineSet.add(cl);
    }

    /**
     * set the set of raw accessions
     * @assumes Nothing
     * @effects Nothing
     * @param accessionSet the set of raw accessions
     */

    public void setAccessions(HashSet accessionSet) {this.accessionSet = 
	accessionSet;}
  
    /**
     * add one raw accession to the set
     * @assumes Nothing
     * @effects Nothing
     * @param acc a AccessionRawAttributes to add to the set of accessions
     */

    public void addAccession(AccessionRawAttributes acc) {accessionSet.add(acc);}

    /**
     * set the set of mutation Strings
     * @assumes Nothing
     * @effects Nothing
     * @param mutationSet set of mutation Strings
     */

    public void setMutations(HashSet mutationSet) {
        this.mutationSet = mutationSet;
    }

    /**
     * add one mutation String to the set
     * @assumes Nothing
     * @effects Nothing
     * @param mut a mutation String to add to the set of mutation Strings
     */

    public void setMutation(String mut) {
         this.mutationSet.add(mut);
    }

    /**
     * set the MGI Note - raw molecular note attributes
     * @assumes Nothing
     * @effects Nothing
     * @param mgiNote a MGINote the raw molecular note
     */

    public void setMGINote(MGINote molecularNote) {
         this.molecularNote = molecularNote;
    }

     /**
     * set the set of raw reference associations
     * @assumes Nothing
     * @effects Nothing
     * @param HashSet - set of RefAssocRawAttributes
     */

    public void setReferenceAssociations(HashSet referenceSet) {
         this.referenceSet = referenceSet;
    }  
    
    /**
     * add one raw reference to the set 
     * @assumes Nothing
     * @effects Nothing
     * @param refAssoc a RefAssocRawAttributes
     */

    public void setReferenceAssociation(RefAssocRawAttributes refAssoc) {
         this.referenceSet.add(refAssoc);
    }  
    
    /**
     * set the raw sequence association
     * @assumes Nothing
     * @effects Nothing
     * @param seqAssoc a SeqAlleleAssocRawAttributes
     */

    public void setSequenceAssociation(SeqAlleleAssocRawAttributes seqAssoc) {
         this.seqAssoc = seqAssoc;
    }
    
    /**
     * set the complete input record 
     * @assumes Nothing
     * @effects Nothing
     * @param inputRecord the complete input record
     */

    public void setInputRecord(String inputRecord) {
         this.inputRecord = inputRecord;
    }
    
    /**
     * get the raw allele
     * @assumes Nothing
     * @effects Nothing
     * @return AlleleRawAttributes
     */

    public AlleleRawAttributes getAllele() {
        return allele;
    }

    /**
     * get the set of raw cell lines
     * @assumes Nothing
     * @effects Nothing
     * @return HashSet of CellLineRawAttributes
     */

    public HashSet getCellLines() {
        return cellLineSet;
    }

    /**
     * get the set of raw accessions
     * @assumes Nothing
     * @effects Nothing
     * @return HashSet of AccessionRawAttributes
     */

    public HashSet getAccessions() {
        return accessionSet;
    }

    /**
     * get the set of mutation strings
     * @assumes Nothing
     * @effects Nothing
     * @return HashSet of mutation Strings
     */

    public HashSet getMutations() {
        return mutationSet;
    }

    /**
     * get the MGI Note a molecular note
     * @assumes Nothing
     * @effects Nothing
     * @return MGINote molecular note
     */

    public MGINote getMolecularNote() {
        return molecularNote;
    }

    /**
     * get the set of raw reference associations
     * @assumes Nothing
     * @effects Nothing
     * @return HashSet of RefAssocRawAttributes
     */

    public HashSet getReferenceAssociations() {
        return referenceSet;
    }
    
     /**
     * get the raw sequence association
     * @assumes Nothing
     * @effects Nothing
     * @return SeqAlleleAssocRawAttributes
     */

    public SeqAlleleAssocRawAttributes getSequenceAssociation() {
         return seqAssoc;
    }
    
     /**
     * get the input record 
     * @assumes Nothing
     * @effects Nothing
     * @return String  the complete input record
     */

    public String getInputRecord() {
         return inputRecord;
    }
    
    /**
     * clears Vectors and sets other objects to null
     * @assumes Nothing
     * @effects Nothing
     */

    public void reset() {
	allele = null;
	cellLineSet.clear();
	accessionSet.clear();
	mutationSet.clear();
	molecularNote = null;
	referenceSet.clear();
	seqAssoc = null;
	inputRecord = null;
    }
}
