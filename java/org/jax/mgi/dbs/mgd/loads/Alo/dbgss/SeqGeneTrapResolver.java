package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.shr.cache.CacheConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.loader.alo.*;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that resolves a set of attributes to a SEQ_GeneTrapState
 * @has VocabKeyLookups for:
 * <UL>
 * <LI>Sequence Tag Method 
 * <LI>Vector End
 * <LI>Reverse Complement
 * </UL>
 * @does
 * <UL>
 * <LI>Resolves a set of attributes to SEQ_GeneTrapState
 * </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqGeneTrapResolver {
    //  lookups to resolve keys to controlled vocabulary terms
    private VocabKeyLookup tagMethodLookup;
    private VocabKeyLookup vectorEndLookup;
    private VocabKeyLookup reverseComplimentLookup;
    private GoodHitCountLookup hitCountLookup;

    /**
     * Constructs a SeqGeneTrapResolver
     * @effects queries a database to load each lookup cache
     * @throws MGIException if
     * -translation error creating or using strain lookup
     * -if configuration error creating a lookup
     * -if database error creating a lookup
     * -if caching error creating a lookup
     * -if error opening/reading good hit count files
     */

    public SeqGeneTrapResolver() throws MGIException {
	// many sequence tag methods have use "'" (prime) e.e. "5 Race"
	// the frameworks using single quotes to surround  sql text strings
	// therefore we use a full cache here (default VocabKeyLookup is Lazy)
	// to avoid  sql like this "where term = '5' RACE' i.e a single quote
	// within single quotes, which produces a JDBC error
        tagMethodLookup = new VocabKeyLookup(
	    VocabularyTypeConstants.SEQ_TAG_METHOD, CacheConstants.FULL_CACHE,
		CacheConstants.FULL_CACHE);
        vectorEndLookup = new VocabKeyLookup(
	    VocabularyTypeConstants.GT_VECTOR_END, CacheConstants.FULL_CACHE,
		CacheConstants.FULL_CACHE);
        reverseComplimentLookup = new VocabKeyLookup(
	    VocabularyTypeConstants.REVERSE_COMP, CacheConstants.FULL_CACHE, 
		CacheConstants.FULL_CACHE);
        hitCountLookup = new GoodHitCountLookup();
    }

    /**
      * resolves a set of attributes to a SEQ_GeneTrapState
      * @assumes Point Coordinate is not set
      * @param raw SeqGeneTrapRawAttributes object to resolve
      * @param  seqKey 
      * @return SEQ_GeneTrapState
      * @throws ALOResolvingException if any required raw attributes not set or
      *         any of the lookups fail to find a key
      * @throws DBException if error adding to any lazy cached lookups
      * @throws CacheException if error doing lookup
      * @throws ConfigException if error doing lookup
      *
      */
    public SEQ_GeneTrapState resolve(SeqGeneTrapRawAttributes raw, 
	    Integer seqKey ) throws  DBException, CacheException, 
		ConfigException, ALOResolvingException  {

        // get raw attributes and make sure they are not null
        String t = raw.getSeqTagMethod();
        String v = raw.getVectorEnd();
        String r = raw.getReverseComp();
        String s = raw.getSeqID();
        // none of these values may be null
        if (seqKey == null || t == null || v == null || r == null) {
            ALOResolvingException e = new ALOResolvingException();
            e.bindRecordString("SeqGeneTrapResolver one or more attributes null:  " +
            "seqKey,seqTagMethod,vectorEnd,reverseComp/" + seqKey + "," +
            t + "," + v + "," + r);
            throw e;
        }

        // start building the state object
        SEQ_GeneTrapState state = new SEQ_GeneTrapState();

        // set the sequence key
        state.setSequenceKey(seqKey);

        // set the goodHitCount - currently interpreter sets to zero
        Integer goodHitCount = hitCountLookup.lookup(s);
        if (goodHitCount == null) {
            goodHitCount = new Integer(0);
        }
        state.setGoodHitCount(goodHitCount);

        // resolve and set the sequence tag method
        try {
            state.setTagMethodKey(tagMethodLookup.lookup(t));
        } catch (KeyNotFoundException e) {
            ALOResolvingException re = new ALOResolvingException();
            re.bindRecordString("SeqGeneTrapResolver seqTagMethod/" + t);
            throw re;
        } catch (TranslationException e) { // not actually thrown, lookup has
                           // no translator
            ALOResolvingException re = new ALOResolvingException();
            re.bindRecordString("SeqGeneTrapResolver seqTagMethod/" + t);
            throw re;
        }

        // resolve and set the vector end
        try {
            state.setVectorEndKey(vectorEndLookup.lookup(v));
        } catch (KeyNotFoundException e) {
            ALOResolvingException re = new ALOResolvingException();
            re.bindRecordString("SeqGeneTrapResolver vectorEnd/" + v);
            throw re;
        } catch (TranslationException e) { // not actually thrown, lookup has
                           // no translator
            ALOResolvingException re = new ALOResolvingException();
            re.bindRecordString("SeqGeneTrapResolver vectorEnd/" + v);
            throw re;
        }

        // resolve and set the reverse complement
        try {
            state.setReverseCompKey(reverseComplimentLookup.lookup(r));
        } catch (KeyNotFoundException e) {
            ALOResolvingException re = new ALOResolvingException();
            re.bindRecordString("SeqGeneTrapResolver reverseComp/" + r);
            throw re;
        } catch (TranslationException e) { // not actually thrown, lookup has
                           // no translator
            ALOResolvingException re = new ALOResolvingException();
            re.bindRecordString("SeqGeneTrapResolver reverseComp/" + r);
            throw re;
        }
          
	return state;
    }
}

