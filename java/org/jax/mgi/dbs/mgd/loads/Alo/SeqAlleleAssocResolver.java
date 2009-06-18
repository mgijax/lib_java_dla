package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.lookup.JNumberLookup;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.shr.cache.CacheConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config. ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.loader.alo.ALOResolvingException;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * An object that resolves a set of attributes to a SEQ_Allele_AssocState
 * @has
 * <UL>
 * <LI> VocabKeyLookup of association qualifier vocabulary terms
 * <LI> JNumberLookup - to resolve a Jnumber to a reference key
 * </UL>
 * @does
 * <UL>
 * <LI>Resolves a set of attributes to SEQ_Allele_AssocState
 * </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqAlleleAssocResolver {
    // get qualifier key given term
    private VocabKeyLookup qualifierLookup;

    // get refs key given a JNumber
    private JNumberLookup jNumLookup;

     /**
     * Constructs a SeqAlleleAssocResolver
     * @assumes Nothing
     * @effects queries a database to load each lookup cache
     * @throws TranslationException - if translation error creating or using
     *              strain lookup
     * @throws ConfigException - if configuration error creating a lookup
     * @throws DBException - if database error creating a lookup
     * @throws CacheException - if caching error creating a lookup
     */

    public SeqAlleleAssocResolver() throws TranslationException,
        ConfigException, DBException, CacheException {

        qualifierLookup = new VocabKeyLookup(
	    VocabularyTypeConstants.SEQ_ALLELE_ASSOC_QUAL, 
		CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
	jNumLookup = new JNumberLookup();
    }

    /**
      * resolves a set of attributes to a SEQ_Allele_AssocState
      * @assumes Nothing
      * @effects Nothing
      * @param alleleKey allele key to be associated with 'seqKey'
      * @param seqKey sequence key to be associated with 'alleleKey'
      * @param qualifier association qualifier term
      * @param refsKey reference key for the association
      * @return SEQ_Allele_AssocState
      * @throws ALOResolvingException if any of the lookups fail to find a key
      * @throws TranslationException if lookups have errors using their 
      *    translators
      * @throws DBException if error adding to any lazy cached lookups
      * @throws CacheException if error doing lookup
      * @throws ConfigException if error doing lookup
      *
      */
    public SEQ_Allele_AssocState resolve(Integer alleleKey, Integer seqKey,
	String qualifier, Integer refsKey) throws KeyNotFoundException, 
	    TranslationException, DBException, CacheException, ConfigException,
		 DLALoggingException  {
        return resolveAttributes(alleleKey, seqKey, qualifier, refsKey);
    }

    public SEQ_Allele_AssocState resolve(Integer alleleKey, Integer seqKey,
        String qualifier, String jNum) throws KeyNotFoundException,
            TranslationException, DBException, CacheException, ConfigException,
                 DLALoggingException  {
	Integer refsKey = jNumLookup.lookup(jNum);
	return resolveAttributes(alleleKey, seqKey, qualifier, refsKey);
    }	
    private SEQ_Allele_AssocState resolveAttributes(Integer alleleKey, Integer seqKey,
	String qualifier, Integer refsKey) throws KeyNotFoundException, 
	    TranslationException, DBException, CacheException, ConfigException,
		 DLALoggingException  {
	// the object we are building
        SEQ_Allele_AssocState state = new SEQ_Allele_AssocState();
        Integer qualKey = qualifierLookup.lookup(qualifier);

        state.setAlleleKey(alleleKey);
	state.setSequenceKey(seqKey);
	state.setRefsKey(refsKey);
	state.setQualifierKey(qualKey);
        return state;
    }
}

