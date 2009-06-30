package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.loads.Alo.*;
import org.jax.mgi.dbs.mgd.lookup.MarkerSymbolLookupByKey;
import org.jax.mgi.dbs.mgd.lookup.AlleleSymbolLookupByKey;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.GeneTrapLoadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;
import org.jax.mgi.shr.dla.loader.alo.ALOResolvingException;
import org.jax.mgi.shr.dla.loader.alo.RepeatALOException;
import org.jax.mgi.shr.dla.loader.alo.SeqAssocWithAlleleException;
import org.jax.mgi.shr.dla.loader.alo.SeqAssocWithMarkerException;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.exception.MGIException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.Timestamp;

/**
 * An object that processes Gene Trap allele sequence information from a 
 * DBGSSGeneTrapinput object by resolving sequence attributes to DAOs 
 * @has
 * <UL>
 * <LI>See superclass
 * <LI>SeqGeneTrapProcessor
 * <LI>HashMap seqKeysAlreadyProcessed - maps seqKey to seqRecordDate
 * </UL>
 * @does implements superclass methods to create:
 * <UL>
 * <LI>sequence to allele association
 * <LI>sequence tag ID to sequence association
 * <LI>sequence gene trap information to sequence association
 * </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class DBGSSGeneTrapAlleleSequenceProcessor 
        extends AlleleSequenceProcessor {

	private String CRT = "\n";

	// processes SEQ_GeneTrap information
    private SeqGeneTrapProcessor seqGTProcessor;

    // Integer:TimeStamp i.e seqKey:seqrecord date of the first instance of
    // sequence found in input
    private HashMap seqKeysAlreadyProcessed;

	// writes out sequence keys of all GSS sequence processed for downstream
	// updating to ACTIVE status, if necessary
    private BufferedWriter seqKeyWriter;

	// lookup symbol for marker key for reporting
	private MarkerSymbolLookupByKey markerSymbolLookup;

	// lookup symbol for allele key for reporting
	private AlleleSymbolLookupByKey alleleSymbolLookup;
    /**
     * Constructs a DBGSSGeneTrapAlleleSequenceProcessor
     * @throws MGIException
     */

    public DBGSSGeneTrapAlleleSequenceProcessor () 
	    throws MGIException {
		super();
		GeneTrapLoadCfg geneTrapConfig = new GeneTrapLoadCfg();
		seqGTProcessor = new SeqGeneTrapProcessor();
		seqKeysAlreadyProcessed = new HashMap();
		try {
			seqKeyWriter = new BufferedWriter(
					new FileWriter(geneTrapConfig.getSeqFile()));
		}
		catch (IOException e) {
			throw new MGIException(e.getMessage());
		}
		markerSymbolLookup = new MarkerSymbolLookupByKey();
		alleleSymbolLookup = new AlleleSymbolLookupByKey();
	}

    public void preprocess(ALORawInput aloInput, ALO resolvedALO)
        throws MGIException {
        super.preprocess(aloInput, resolvedALO);
        try {
            seqKeyWriter.write(this.sequenceKey.toString() + CRT);
        } catch (IOException e) {
            throw new MGIException(e.getMessage());
        }
    }
  /**
   * Processes DBGSS Gene Trap allele sequence information, creating associations
   * where the allele is new, and adding associations where necessary when the
   * allele is in the database
   * @param aloInput DBGSSGeneTrapRawInput object in ALORawInput clothing - 
   *       a set of raw attributes to resolve and add to the database
   * @param resolvedALO - the ALO object to which will will add resolved
   *         sequence information
   * @param incomingAlleleKey - allele Key of allele we are processing, may be
   *   new or already in the database
   * @throws ALOResolvingException if errors resolving derivation or mutant
   *         cell line attributes
   * @throws CacheException if errors accessing a Lookup cache
   * @throws DBException if errors adding to LazyCached lookups
   * @throws ConfigException if resolvers have errors accessing configuration
   * @throws TranslationException if resolvers have issues resolving 
   *         translated attributes
   * @throws KeyNotFoundException - doesnt throw, lookup returns null
   */
    public void process(ALORawInput aloInput, ALO resolvedALO, Integer incomingAlleleKey) 
	throws  ALOResolvingException, CacheException, DBException, 
	    KeyNotFoundException, ConfigException, TranslationException, 
		DLALoggingException, RepeatALOException, 
		SeqAssocWithMarkerException, SeqAssocWithAlleleException {
	
	// cast the ALORawInput  to a DBGSSGeneTrapRawInput
	DBGSSGeneTrapRawInput gtAloInput = (DBGSSGeneTrapRawInput)aloInput;
	
	// cast the ALO to a DBGSSGeneTrapALO
	DBGSSGeneTrapALO gtResolvedALO = (DBGSSGeneTrapALO)resolvedALO;
	
	// true if we are going to create a sequence to allele association
	Boolean createAssoc = Boolean.FALSE;
	
	/**
	 * skip this record if this sequence has already been seen in the input
	 */
	Timestamp incomingSeqrecordDate = (gtAloInput).
	    getSeqRecordDate();
	//logger.logcInfo("DBGSSAlleleSequenceProcessor incomingSeqRecordDate: " + 
	  //  incomingSeqrecordDate, false);
	Timestamp alreadyProcessedDate = (Timestamp)seqKeysAlreadyProcessed.
		get(sequenceKey);
	//logger.logcInfo("DBGSSAlleleSequenceProcessor alreadyProcessedDate: " + 
	  //  alreadyProcessedDate, false);
	if (alreadyProcessedDate != null) {
	    if(incomingSeqrecordDate.after(alreadyProcessedDate)) {
		//logger.logcInfo("repeat " + gtAloInput.
		  //  getSequenceAssociation().getSeqID(), false);
		RepeatALOException e =  new RepeatALOException();
		e.bindRecordString(" Sequence ID " + gtAloInput.
		    getSequenceAssociation().getSeqID());
		throw e;
	    }
	}
	else {
	    seqKeysAlreadyProcessed.put(sequenceKey, incomingSeqrecordDate);
	}
	
	/**
	 * skip this record if the sequence is already associated with marker
	 */
	HashSet markerKeys = markerLookup.lookup(sequenceKey);
	if(markerKeys != null ) {
	    StringBuffer b = new StringBuffer();
	    for (Iterator i = markerKeys.iterator(); i.hasNext();) {
			Integer key = (Integer)i.next();
			String s = markerSymbolLookup.lookup(key);
			b.append(s);
			b.append(",");
	    }
	    	    
	    // throw exception to go on to next
	    SeqAssocWithMarkerException e = new SeqAssocWithMarkerException();
	    e.bindRecordString(" Sequence ID /Marker(s) " + 
		gtAloInput.getSequenceAssociation().getSeqID() + "/" + b.toString());
	    throw e;
	}
		
	/** 
	 * skip this record if 1) this is a new allele and sequence is
	 * associated with another allele in the database 2) this is an existing 
	 * allele and the sequence is associated with another allele in the
	 * database, regardless of whether the existing allele is also associated
	 * with this sequence
	 */
	// get the set of alleles associated with this sequence
	HashSet alleles = alleleLookup.lookup(sequenceKey);
	
	// The string for reporting alleles associated with this sequence, other
	// than the allele we are processing
	StringBuffer b = new StringBuffer();
	
	// TRUE if the sequence we are processing is already associated (in the
	// database) with the allele we are processing 
	Boolean seqIsAssocWithAllele = Boolean.FALSE;
	
	/**
	 * if there is no allele DAO in the resolved DAO object then the allele 
	 * we are processing is in the database; note the parameter 'incomingAlleleKey'
	 * may represent a new allele
	 */
	
	if (gtResolvedALO.getAlleleDAO() == null) {
	    // iterate through the allele keys associated with this sequence in 
	    // the database; determine if any are not the current sequence
	    for (Iterator i = alleles.iterator();i.hasNext();) {
			Allele currentDBAllele = (Allele)i.next();
			Integer alleleKeyAssocWithSeq = currentDBAllele.getAlleleKey();
			if (incomingAlleleKey.equals(alleleKeyAssocWithSeq)) {
				// this sequence is associated already with the allele in the db
				seqIsAssocWithAllele = Boolean.TRUE;
			} else {
				// this sequence is associated with different allele in the db
				String s = currentDBAllele.getAlleleSymbol();
				b.append(s);
				b.append(", ");
			}
	    }
	
	    /**
	     * if 'b' is not empty than we have a discrepancy to report
	     */
	    String message = "";
		String incomingAlleleSymbol =
				alleleSymbolLookup.lookup(incomingAlleleKey);
	    if (b.length() != 0) {
			if (seqIsAssocWithAllele.equals(Boolean.TRUE)) {
				message = "Sequence ID " + gtAloInput.
				getSequenceAssociation().getSeqID() + " IS associated with " +
					"current allele symbol " +  incomingAlleleSymbol + ", and also" +
				" associated with the following allele(s): " + b.toString();
			}
			else {
				message = "Sequence ID " + gtAloInput.
				getSequenceAssociation().getSeqID() + " is NOT associated with " +
					"current allele symbol " +  incomingAlleleSymbol + ", but is" +
				" associated with the following allele(s): " + b.toString();
			}
			// throw exception to go on to next
			SeqAssocWithAlleleException e = new SeqAssocWithAlleleException();
			e.bindRecordString(message);
			throw e;
	    } 
	    /**
	     * if we get here then this sequence is not associated with any other 
	     * alleles in the database
	     * if not already associated with the allele, associate it
	     */
	    if (seqIsAssocWithAllele.equals(Boolean.FALSE)) {
		createAssoc = Boolean.TRUE;
	    }
	}
	
	/**
	 * there is an allele DAO in the resolved DAO, so the allele we are 
	 * processing is not in the database, so process the association
	 */
	else {
	    createAssoc = Boolean.TRUE;
	}
	/** create the association if 1) this is a new allele and sequence not
	 * associated with another allele 2) this is an existing allele  and 
	 * the sequence not associated with this allele nor is it associated
	 * with any other allele
	 */
	if (createAssoc.equals(Boolean.TRUE)) {
	    String jNum = gtAloInput.getSequenceAssociation().getJNum();
	    String assocQualifier = gtAloInput.getSequenceAssociation().getQualifier();
	    SEQ_Allele_AssocState state = seqAlleleResolver.resolve(
		incomingAlleleKey, sequenceKey, assocQualifier, jNum);
	    gtResolvedALO.setSeqAlleleAssociation(state);
	}
	
	/**
	 * Now resolve the seq gene trap
	 */
	//System.out.println(" seqGTProcessor.process(gtAloInput, sequenceKey,  gtResolvedALO)");
	seqGTProcessor.process(gtAloInput, sequenceKey, 
	    gtResolvedALO);	
    } 
    
    public void postprocess() throws MGIException {
		System.out.println("Postprocessing DBGSSGeneTrapAlleleSequenceProcessor");
        try {
            seqKeyWriter.close();
        } catch (IOException e) {
            throw new MGIException (e.getMessage());
        }
    }
}
