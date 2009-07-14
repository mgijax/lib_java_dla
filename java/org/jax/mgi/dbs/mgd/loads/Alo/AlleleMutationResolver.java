package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.dao.ALL_Allele_MutationState;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.shr.cache.CacheConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config. ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.loader.alo.ALOResolvingException;

/**
 * An object that resolves molecular mutation strings to a 
 *   ALL_Allele_MutationState
 * @has molecular mutation vocabulary lookup
 * @does Creates an ALL_Allele_MutationState
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class AlleleMutationResolver {
     private VocabKeyLookup mutationKeyLookup;

     /**
     * Constructs a AlleleMutationResolver
     * @effects queries a database to load the lookup cache
     * @throws TranslationException - if translation error creating the 
     *     lookup / not thrown because this vocab does not have a translation
     * @throws ConfigException - if configuration error creating a lookup
     * @throws DBException - if database error creating a lookup
     * @throws CacheException - if caching error creating a lookup
    
     */

    public AlleleMutationResolver() throws CacheException, DBException,
	    ConfigException, TranslationException {
	 mutationKeyLookup = new VocabKeyLookup(
	     VocabularyTypeConstants.ALLELE_MOL_MUT, CacheConstants.FULL_CACHE,
		CacheConstants.FULL_CACHE);
    }

    /**
      * creates a ALL_Allele_MutationState
      * @param mutation - a mutation string
      * @param alleleKey - an allele key
      * @throws ALOResolvingException if any of the lookups fail to find a key
      * @throws DBException if error adding to any lazy cached lookups
      * @throws CacheException if error doing lookup
      * @throws ConfigException if error doing lookup
      * @return An ALL_Allele_MutationState
      */
    public ALL_Allele_MutationState resolve(String mutation,
        Integer alleleKey) throws DBException, CacheException,
            ConfigException, ALOResolvingException {
        Integer mutKey = null;
        try {
            mutKey = mutationKeyLookup.lookup(mutation);
        } catch (KeyNotFoundException e) {
           ALOResolvingException resE = new ALOResolvingException();
           resE.bindRecordString("Mutation/" + mutation);
           throw resE;
        } catch (TranslationException e) { // this vocab does not have a trans
               ALOResolvingException resE = new ALOResolvingException();
               resE.bindRecordString("Mutation/" + mutation);
               throw resE;
        }

        ALL_Allele_MutationState state = new ALL_Allele_MutationState();
        state.setAlleleKey(alleleKey);
        state.setMutationKey(mutKey);
        return state;
    }
}
