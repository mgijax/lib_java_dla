package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashSet;
import java.util.Iterator;
import org.jax.mgi.dbs.mgd.lookup.AlleleMutationLookupByAlleleKey;
import org.jax.mgi.dbs.mgd.dao.ALL_Allele_MutationState;
import org.jax.mgi.shr.dla.loader.alo.AlleleMutationProcessorException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.loader.alo.ALOResolvingException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that processes an allele to mutant cell line association
 * @has AlleleMutationResolver
 * @does creates a ALL_Allele_MutationState and sets it in the ALO
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class AlleleMutationProcessor {

    // creates an ALL_Allele_MutationState
    AlleleMutationResolver alleleMutResolver;
    // looks up existing mutations for an allele in the database
    private AlleleMutationLookupByAlleleKey mutationLookup;

    /**
     * Constructs a AlleleMutationProcessor 
     * @throws MGIException
     */

    public AlleleMutationProcessor() 
	    throws MGIException {
	alleleMutResolver = new AlleleMutationResolver();
	mutationLookup = new AlleleMutationLookupByAlleleKey();
    }

  /**
   * processes an allele to molecular mutation association for a new allele
   * @param aloRawInput - raw attibutes for an ALO
   * @param resolvedALO - the ALO we are building
   * @assumes the allele has already been processed i.e. resolvedALO contains
   *   the allele key
   */

   public void processMutationForNewAllele(HashSet incomingMutations, 
	    ALO resolvedALO) throws ConfigException, DBException, CacheException, 
		ALOResolvingException {
       //System.out.println("In AlleleMutationProcessor.processMutationsForNewAllele");
	Integer alleleKey = resolvedALO.getAlleleDAO().getKey().getKey();
        for (Iterator i = incomingMutations.iterator(); i.hasNext();) {
	   
	    String m = (String)i.next();
	    //System.out.println("MutationProcessor processMutationForNewAllele mutation: " + m);
	    ALL_Allele_MutationState state = alleleMutResolver.resolve(
		m, alleleKey);
	    resolvedALO.addMutation(state);
	}
   }   
   
  /**
   * processes an allele to molecular mutation association for an existing allele
   * @param incomingMutations - set of Strings; the incoming mutations for the allele
   * @param alleleKey - the existing allele key
   * @param symbol - allele symbol for alleleKey
   * @assumes the allele exists in the database
   */
   public void processMutationsForExistingAllele (HashSet incomingMutations, 
       Integer alleleKey, String symbol, ALO resolvedALO) throws ConfigException, DBException, 
	    CacheException, ALOResolvingException,  
       AlleleMutationProcessorException {
       HashSet dbMutations = (HashSet)mutationLookup.lookup(alleleKey);
       StringBuffer mutsNotInDatabase = new StringBuffer();
              Boolean somethingToReport = Boolean.FALSE;
       //System.out.println("In AlleleMutationProcessor.processMutationsForExistingAllele");
       if (dbMutations == null) {
	   //throw exception indicating allele in db without a mutation
	   AlleleMutationProcessorException e = 
	       new AlleleMutationProcessorException();
	   e.bindRecordString(alleleKey + " does not have a mutation in the database");
	   throw e;
       }
       
       // check db for mutation of each incoming type, if not throw exception
       for (Iterator i = incomingMutations.iterator();i.hasNext();) {
	   String incMut = (String)i.next();
	   //System.out.println("MutationProcessor processMutationForExistingAllele mutation: " + incMut);
	   if (!dbMutations.contains(incMut)) {
	       mutsNotInDatabase.append(incMut);
	       mutsNotInDatabase.append(" ");
	       somethingToReport = Boolean.TRUE;
	   }
       }
 
       if (somethingToReport.equals(Boolean.TRUE)) {
	   // throw exception indicating muts not in database
	   AlleleMutationProcessorException e = 
	       new AlleleMutationProcessorException();
	   e.bindRecordString(alleleKey + " is not associated with the following " +
	       "incoming mutation(s): " + mutsNotInDatabase);
	   throw e;
       }
   }
}
