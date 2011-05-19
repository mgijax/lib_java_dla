package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.dbs.mgd.dao.ACC_AccessionState;
import org.jax.mgi.dbs.mgd.dao.SEQ_GeneTrapDAO;
import org.jax.mgi.dbs.mgd.dao.SEQ_GeneTrapState;
import org.jax.mgi.dbs.mgd.dao.SEQ_GeneTrapKey;
import org.jax.mgi.dbs.mgd.loads.Acc.AccAttributeResolver;
import org.jax.mgi.dbs.mgd.loads.Acc.AccessionRawAttributes;
import org.jax.mgi.dbs.mgd.lookup.SeqGeneTrapDAOLookupBySeqKey;
import org.jax.mgi.dbs.mgd.lookup.SeqTagIdLookupBySeqKey;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.loader.alo.ALOResolvingException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * an object that processes raw gene trap sequence information. 
 * Determines, if possible, object identity in the database, reporting 
 * differences in incoming information with respect to the data in the database.
 * @has
 * <UL>
 * <LI>SeqGeneTrapLookupBySeqKey
 * <LI>SeqTagIdLookupBySeqKey
 * <LI>SeqGeneTrapResolver 
 * </UL>
 * @does
 * Resolves a SeqGeneTrapRawAttributes to a SEQ_GeneTrapState. Determines if 
 * there is already a SEQ_GeneTrap object in the database, if so reports
 * differences if any, if not sets the State in ALO object
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqGeneTrapProcessor {
    private SeqGeneTrapDAOLookupBySeqKey seqGTLookup;
    private SeqTagIdLookupBySeqKey seqTagIdLookup;
    private SeqGeneTrapResolver seqGeneTrapResolver;
    private AccAttributeResolver accResolver;

    /** construct a SeqGeneTrapProcessor */
    public SeqGeneTrapProcessor() throws MGIException {
	seqGTLookup = new 
	    SeqGeneTrapDAOLookupBySeqKey();
	seqTagIdLookup = new
	    SeqTagIdLookupBySeqKey();
	seqGeneTrapResolver = new
	    SeqGeneTrapResolver();
	accResolver = new
	    AccAttributeResolver();
    }

  /**
   * resolve seq gene trap raw attributes, determine if already in the database. 
   * Report differences if in database, create new if not.
   * @param rawInput  raw attributes of an DBGSS Gene Trap ALO 
   * @param seqKey the sequence to which we are associating gene trap info
   * @param resolvedALO the DBGSS version of ALO which we are building.
   * @throws CacheException if error accessing lookup cache
   * @throws DBException if error using lookup 
   * @throws ConfigException if error using configurator
   * @throws TranslationException - doesn't throw, lookups don't use Translators
   * @throws ALOResolvingException if can't resolve raw attributes
   * @throws KeyNotFoundException if can't resolve LogicalDB name to key
   */

   public void process(DBGSSGeneTrapRawInput rawInput, Integer seqKey, 
           DBGSSGeneTrapALO resolvedALO) throws CacheException, DBException, 
	       ConfigException, TranslationException, ALOResolvingException, 
		KeyNotFoundException {
       // get the raw data
	SeqGeneTrapRawAttributes raw = rawInput.getSeqGeneTrap();
	//System.out.println("SeqGeneTrapProcessor.process rawSeqGeneTrap " + raw.getSeqID());
	// resolve to a state
	SEQ_GeneTrapState state = seqGeneTrapResolver.resolve(raw, seqKey);
	// see if this sequence already has a SEQ_GeneTrap object in the database
	SEQ_GeneTrapDAO dbDAO = seqGTLookup.lookup(seqKey);
	
	// if not in the database, create new one and add seq tag id association
	// if not already associated
	if (dbDAO == null) {
	    // add state to resolved ALO
	    //System.out.println("SeqGeneTrapProcessor.process: resolvedALO.setSeqGeneTrap(state) ");
	    resolvedALO.setSeqGeneTrap(state, new SEQ_GeneTrapKey(seqKey));
	}
	// associate seqTagId with sequence if not already associated
	String incomingID = raw.getSeqTagID();
	String dbID = seqTagIdLookup.lookup(seqKey);
	if (dbID == null || !incomingID.equals(dbID)) {
	    AccessionRawAttributes seqTagAcc = rawInput.getSeqTagAccession();
	    ACC_AccessionState accState = accResolver.resolveAttributes(
		seqTagAcc, seqKey);
	    // set in resolved ALO
	    resolvedALO.addAccession(accState);
	}
    }
}
