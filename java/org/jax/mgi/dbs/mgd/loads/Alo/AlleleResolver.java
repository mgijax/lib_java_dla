package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.lookup.StrainKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.StrainNameLookup;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.shr.cache.CacheConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.loader.alo.ALOResolvingException;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that resolves raw allele attributes to an ALL_AlleleState
 * @has molecular mutation vocabulary lookup
 * @does Creates an ALL_Allele_MutationState
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class AlleleResolver {

    private VocabKeyLookup inheritModeKeyLookup;
    private VocabKeyLookup typeKeyLookup;
    private VocabKeyLookup statusKeyLookup;
    private VocabKeyLookup transmissionKeyLookup;
    private StrainKeyLookup strainKeyLookup;
    private StrainNameLookup strainNameLookup;

     /**
     * Constructs a AlleleResolver
     * @effects queries a database to load the lookup caches
     * @throws TranslationException - if translation error creating the 
     *     lookup / not thrown because this vocab does not have a translation
     * @throws ConfigException - if configuration error creating a lookup
     * @throws DBException - if database error creating a lookup
     * @throws CacheException - if caching error creating a lookup
    
     */

    public AlleleResolver() throws MGIException {
	
	inheritModeKeyLookup = new VocabKeyLookup(
	    VocabularyTypeConstants.ALLELE_INHERIT_MODE, 
		CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
	typeKeyLookup = new VocabKeyLookup(
	    VocabularyTypeConstants.ALLELE_TYPE, 
		CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
	statusKeyLookup = new VocabKeyLookup(
	    VocabularyTypeConstants.ALLELE_STATUS, 
		CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
	transmissionKeyLookup = new VocabKeyLookup(
            VocabularyTypeConstants.ALLELE_TRANS, 
		CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
	strainKeyLookup = new StrainKeyLookup();
	strainNameLookup = new StrainNameLookup();
	strainNameLookup.initCache();
    }

    
    /**
      * creates a Allele object
      * @param rawAllele - AlleleRawAttributes
      * @note marker key attribute is not set, it will be added 
      * by a cache load from ALL_Marker_Assoc
      * @assumes marker symbol and name is correct in the input i.e. it is the
      * interpreter's responsibility to create correct nomenclature
      * @throws ALOResolvingException if any of the lookups fail to find a key
      * @throws DBException if error adding to any lazy cached lookups
      * @throws CacheException if error doing lookup
      * @throws ConfigException if error doing lookup
      * @return An Allele object
      */
    public Allele resolve(AlleleRawAttributes rawAllele) throws DBException, 
	CacheException, ConfigException, DLALoggingException, 
	ALOResolvingException {
	//System.out.println("AlleleResolver.resolve rawAllele.strain " + rawAllele.getStrain()); 
	Integer strainKey = null;
	try {
	    strainKey = strainKeyLookup.lookup(rawAllele.getStrain());
	} catch (KeyNotFoundException e) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Strain/" + rawAllele.getStrain());
	      throw resE;
	} catch (TranslationException e) { 
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Strain/" + rawAllele.getStrain());
	      throw resE;
	}
	return resolve(rawAllele, strainKey);
    }
    /**
     * create and allele object with a pre-determined strain, we need to pass
     * this in for gene traps and targeted alleles because the allele strain
     * is that of the parent cell line
     * @param rawAllele - AlleleRawAttributes
     * @param strainKey - allele strain, we provide this method to accomodate
     * genetrap/targeted alleles which get their strain from the parent cell line
     * @note GTLF only - marker key attribute is not set, it will be added 
     * by a cache load from ALL_Marker_Assoc
     * @assumes marker symbol and name is correct in the input i.e. it is the
     * interpreter's responsibility to create correct nomenclature
     * @throws ALOResolvingException if any of the lookups fail to find a key
     * @throws DBException if error adding to any lazy cached lookups
     * @throws CacheException if error doing lookup
     * @throws ConfigException if error doing lookup
     * @return An Allele object
     */
    public Allele resolve(AlleleRawAttributes rawAllele, Integer strainKey) 
	throws DBException, CacheException, ConfigException, DLALoggingException, 
	ALOResolvingException {
	
	String strain = null;
	Integer inheritModeKey = null;
	Integer typeKey = null;
	Integer statusKey = null;
	Integer transmissionKey = null;
	
	try {
	strain = strainNameLookup.lookup(strainKey);
	} catch (KeyNotFoundException e) { // we should not get here because
	// we have translated a strain to a key above
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Strain/" + rawAllele.getStrain());
	      throw resE;
	}
	
	try {
	   inheritModeKey =  inheritModeKeyLookup.lookup(
		rawAllele.getInheritMode());
	} catch (KeyNotFoundException e) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Mode/" + rawAllele.getInheritMode());
	      throw resE;
	} catch (TranslationException e) { // won't happen, no translator 
						  //for this vocab
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Mode/" + rawAllele.getInheritMode());
	      throw resE;
	}
	
	try {
	    typeKey = typeKeyLookup.lookup(rawAllele.getType());
	} catch (KeyNotFoundException e) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Type/" + rawAllele.getType());
	      throw resE;
	} catch (TranslationException e) { // won't happen, no translator 
						  //for this vocab
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Type/" + rawAllele.getType());
	      throw resE;
	}
	
	try {
	    statusKey = statusKeyLookup.lookup(rawAllele.getStatus());
	} catch (KeyNotFoundException e) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Status/" + rawAllele.getStatus());
	      throw resE;
	} catch (TranslationException e) { // won't happen, no translator 
						  //for this vocab
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Status/" + rawAllele.getStatus());
	      throw resE;
	}
	try {
            transmissionKey = transmissionKeyLookup.lookup(
		rawAllele.getTransmission());
        } catch (KeyNotFoundException e) {
              ALOResolvingException resE = new ALOResolvingException();
              resE.bindRecordString("Allele Transmission/" + 
		  rawAllele.getTransmission());
              throw resE;
        } catch (TranslationException e) { // won't happen, no translator
                                                  //for this vocab
              ALOResolvingException resE = new ALOResolvingException();
              resE.bindRecordString("Allele Transmission/" + 
		  rawAllele.getTransmission());
              throw resE;
        }
	Allele allele = new Allele();
	
	// allele key remains null
	// marker key/symbol remains null - this attribute will be added by a 
	// cache load from ALL_Marker_Assoc
	
	allele.setStrainKey(strainKey);
	allele.setStrainName(strain);
	allele.setInheritModeKey(inheritModeKey);
	allele.setInheritMode(rawAllele.getInheritMode());
	allele.setAlleleTypeKey(typeKey);
	allele.setAlleleType(rawAllele.getType());
	allele.setAlleleStatusKey(statusKey);
	allele.setAlleleStatus(rawAllele.getStatus());
	allele.setAlleleSymbol(rawAllele.getSymbol());
	allele.setAlleleName(rawAllele.getName());
	allele.setIsWildType(rawAllele.getIsWildType());
	allele.setIsMixed(rawAllele.getIsMixed());
	allele.setIsExtinct(rawAllele.getIsExtinct());
	allele.setTransmissionKey(transmissionKey);
	allele.setTransmission(rawAllele.getTransmission());
	return allele;
    }
}

