package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashSet;

import org.jax.mgi.dbs.mgd.lookup.CellLineNameLookupByKey;
import org.jax.mgi.dbs.mgd.lookup.JNumberLookup;
import org.jax.mgi.dbs.mgd.lookup.ParentCellLineKeyLookupByParent;
import org.jax.mgi.dbs.mgd.lookup.StrainKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.StrainNameLookup;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.VocabTermLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.shr.cache.CacheConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config. ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * An object that resolves raw mutant derivation attributes to a 
 *   Derivation object. Where the resolved value is a foreign key we include
 *   the term or object attribute that foreign key represents. For example
 *   this class stores the vector key and the vector name, the derivation type
 *   key and the derivation type, etc
 * @has various lookups to resolve incoming attributes to controlled vocabulary
 *      and object keys. Where vocabularies have translators we must then take
 *      the term key or object key and resolve it to the correct term/object
 *      attribute
 * @does
 * <UL>
 * <LI>Resolves a DerivationRawAttributes object to a Derivation
 * </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class DerivationResolver {

    // get a term given a term key
    private VocabTermLookup termLookup;

    // get vector key given a raw vector name
    private VocabKeyLookup vectorKeyLookup;

    // get vector type key given vector type
    private VocabKeyLookup vectorTypeKeyLookup;

    // get parent cell line key given parent cell line name
    private ParentCellLineKeyLookupByParent parentCellLineKeyLookup;

    // get  cell line name given  cell line key   
    private CellLineNameLookupByKey cellLineNameLookup;

    // get strain key given a strain  name - uses a translator
    private StrainKeyLookup strainKeyLookup;
	
    // get strain name given a strain key
    private StrainNameLookup strainNameLookup;

    // get derivation type key given a derivation type - no translator
    private VocabKeyLookup derivationTypeKeyLookup;

    // get cell line derivation creator key given a creator name uses translator
    private VocabKeyLookup creatorKeyLookup;
 
    // get the reference key given a J Number - no translator
    private JNumberLookup jNumberLookup;
   
    
     /**
     * Constructs a DerivationResolver
     * @assumes Nothing
     * @effects queries a database to load each lookup cache
     * @throws TranslationException - if translation error creating or using
     *              strain lookup
     * @throws ConfigException - if configuration error creating a lookup
     * @throws DBException - if database error creating a lookup
     * @throws CacheException - if caching error creating a lookup
     */

    public DerivationResolver() throws TranslationException,
        ConfigException, DBException, CacheException {

        termLookup = new VocabTermLookup();
        vectorKeyLookup = new VocabKeyLookup(
			VocabularyTypeConstants.CELLLINE_VECTOR_NAME,
				CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
		vectorTypeKeyLookup = new VocabKeyLookup(
			VocabularyTypeConstants.CELLLINE_VECTOR_TYPE,
			CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
		parentCellLineKeyLookup = new ParentCellLineKeyLookupByParent();
		cellLineNameLookup = new CellLineNameLookupByKey();
		strainKeyLookup = new StrainKeyLookup();
		strainNameLookup = new StrainNameLookup();
		strainNameLookup.initCache();
		// we use the allele typ vocab for the derivation type
		derivationTypeKeyLookup = new VocabKeyLookup(
			VocabularyTypeConstants.ALLELE_TYPE,
				CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
		creatorKeyLookup = new VocabKeyLookup(
			VocabularyTypeConstants.CELLLINE_CREATOR,
				CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
		jNumberLookup = new JNumberLookup();
    }

     /**
      * Resolves a DerivationRawAttributes object to a Derivation object.
      * To do this it:
      * <UL>
      * <LI>Resolves raw attributes using Lookups to get foreign keys
      * <LI>Resolves the foreign key to a term or object attribute using 
      *     another Lookup
      * </UL> 
      * @assumes Nothing
      * @effects Nothing
      * @param raw the DerivationRawAttributes object to resolve
      * @return A Derivation
      * @throws KeyNotFoundException if any of the lookups fail to find a key
      * @throws TranslationException if lookups have errors using their 
      *    translators
      * @throws DBException if error adding to any lazy cached lookups
      * @throws CacheException if error using lookup cache
      * @throws ConfigException if error using lookup
      *
      */
    public Derivation resolve(DerivationRawAttributes raw) 
	    throws DBException, 
		CacheException, ConfigException, DLALoggingException {
      // the object we are building
      Derivation resDeriv = new Derivation();
     /**
      * resolve derivation name, use raw if defined, set to String "null" 
      * if not
      */
      String derivName = raw.getName();
      if (derivName == null) {
	  derivName = "null";
      }
      resDeriv.setName(derivName);

     /**
      * resolve derivation description, use raw if defined, set to String "null"
      * if not
      */
      String description = raw.getDescription();    
      if (description ==  null) {
          description = "null";
      }
      resDeriv.setDescription(description);

     /**
      * resolve vector name if defined, set key to 0 and to String "null" 
      * if not
      */
      String rawVectorName = raw.getVectorName();
      /** Set the default vector name and key, which is the value we want if we
       *  can't resolve it. These values let us know the attribute did not
       *  resolve when we report differences
       */
      String resVectorName = "raw_" + rawVectorName;
      Integer vectorKey = new Integer(0);
      //System.out.println("DerivationResolver.resolve - rawVectorName:" + vectorName);
      if (rawVectorName != null ) {
		  // throws KeyNotFoundException, this Lookup has a translator
		  try {
			  vectorKey = vectorKeyLookup.lookup(rawVectorName);
		  } catch (KeyNotFoundException e) {
			   // we set the default above, so just catch the exception
		  } catch (TranslationException e) {
			  // we set the default above, so just catch the exception
		 }
		  // Now get the term for this key; term different than incoming
		  // term if translator was used. If we have the key, term will be found
		  if (vectorKey.intValue() != 0) {
			resVectorName = termLookup.lookup(vectorKey);
		  }
		  // set in resolved derivation object
		  resDeriv.setVectorKey(vectorKey);
		  resDeriv.setVectorName(resVectorName);
      }
      else {
		  //System.out.println("DerivationResolver.resolve - setting vector key to zero and vector name to 'null;");
		  resDeriv.setVectorKey(vectorKey);
		  // vector not in input, set to String "null"
		  resDeriv.setVectorName("null");
      }

     /**
      * resolve vector type if defined, set key to 0 and String version to 
      * "null" if not
      */
      String rawVectorType = raw.getVectorType();
       /** Set the default vector name and key, which is the value we want if we
        * can't resolve it. These values let us know the attribute did not
        * resolve when we report differences
        */
      Integer vectorTypeKey = new Integer(0);
      if (rawVectorType != null) {
	  // throws KeyNotFoundException, this Lookup does not have a translator
		  try {
			  vectorTypeKey = vectorTypeKeyLookup.lookup(rawVectorType);
		  } catch (KeyNotFoundException e) {
			  // we set the default above, so just catch the exception
		  } catch (TranslationException e) { // won't happen, no translator for this vocab
			  // we set the default above, so just catch the exception
		  }
		  // set in resolved derivation object
		  resDeriv.setVectorTypeKey(vectorTypeKey);
		  // no translator, so raw vector type is the bona fide term
		  resDeriv.setVectorType(rawVectorType);
      }
      else {
		  resDeriv.setVectorTypeKey(vectorTypeKey);
		  // vector type not in input, set to String "null"
		  resDeriv.setVectorType("null");
      }

	  /**
      * resolve parent cell line strain if defined, set key to 0 and
      * String version to "null" if not
      */
      String rawParentStrain = raw.getParentCellLineStrain();

      /**
	   * Set the default strain name and key, which is the value we want if we
       *  can't resolve it. These values let us know the attribute did not
       *  resolve when we report differences
       */
      String resParentStrain = "raw_" + rawParentStrain;
      Integer strainKey = new Integer(0);
      if (rawParentStrain != null ) {
		  // throws KeyNotFoundException, this Lookup has a translator
		  try {
			  strainKey = strainKeyLookup.lookup(rawParentStrain);
		  } catch (KeyNotFoundException e) {
			  // we set the default above, so just catch the exception
		  } catch (TranslationException e) {
			  // we set the default above, so just catch the exception
		  }
		  resDeriv.setParentStrainKey(strainKey);
		  // Now get the term for this key; term different than incoming
		  // term if translator was used. Lookup throws KeyNotFoundException
		  try {
				  resParentStrain = strainNameLookup.lookup(strainKey);
		  } catch (KeyNotFoundException e) {
			 // we set the default above, so just catch the exception
		  }
		  // set in resolved derivation object
		  resDeriv.setParentStrainKey(strainKey);
		  resDeriv.setParentStrain(resParentStrain);
      }
      else {
		  resDeriv.setParentStrainKey(strainKey);
		  // parent strain not in input, set to String "null"
		  resDeriv.setParentStrain("null");
      }

     /**
      * resolve parent cell line if defined, set key to 0 and String version 
      * to "null" if not
      */
      String rawParentCellLine = raw.getParentCellLine();

      /** Set the default parent cell line name and key, which is the value
	   *  we want if we can't resolve it. These values let us know the attribute
	   *  did not resolve when we report differences
       */
      String resParentCellLine = "raw_" + rawParentCellLine;
      Integer cellLineKey = new Integer(0);
	  HashSet clKeys = new HashSet();
      if (rawParentCellLine != null ) {
		  try {
			  // throws KeyNotFoundException, this Lookup has a translator which
			  // throws TranslationException
			  clKeys = parentCellLineKeyLookup.lookup(rawParentCellLine);

			  // if we get only one key we're golden, but must determine resolved
			  // cell line name in case the translator was used
			  if (clKeys.size() == 1) {
				  cellLineKey = (Integer)clKeys.iterator().next();
				  // Lookup throws KeyNotFoundException
				  resParentCellLine = cellLineNameLookup.lookup(cellLineKey);
			  }
			  // if we get more then one key then report it. currently the
			  // database has multiple "Not Specified" and "Other (see notes)"
			  // parent cell lines, but we don't currently have any dbGSS gene
			  // traps that will
			  else if (clKeys.size() > 1 ) {
				  // When the parent cell line name lookup returns > 1 cell line
				  // key then the translator was not used so
				  // resParentCellLine is the same as rawParentCellLine but
				  // we cannot determine which parent cell line it is even
				  // if we use strain to disambiguate, because incoming strain
				  // may not be the same as that in the proper derivation for this
				  // mutant cell line
				  resParentCellLine = rawParentCellLine;
				  // cellLineKey remains the default
			  }
		  } catch (KeyNotFoundException e) {
			  // we set the default above, so just catch the exception
		  } catch (TranslationException e) {
			  // we set the default above, so just catch the exception
		  }

		  // Now get the cell line name for this key; name different than incoming
		  // name if translator was used. Lookup throws KeyNotFoundException
		  // if key not found
		  if (cellLineKey.intValue() != 0) {
			  try {
			  resParentCellLine = cellLineNameLookup.lookup(cellLineKey);
			  } catch (KeyNotFoundException e) {
				  // we set the default above, so just catch the exception
			  }
		  }
		  // set in resolved derivation object
		  resDeriv.setParentCellLineKey(cellLineKey);
		  resDeriv.setParentCellLine(resParentCellLine);
      }
      else {
	     resDeriv.setParentCellLineKey(cellLineKey);
	     // parent not in input, set to String "null"
		 resDeriv.setParentCellLine("null");
      }

     /**
      * resolve derivation type if defined, set key to 0 and
      * String version to "null" if not
      */
      String rawDerivType = raw.getDerivationType();
      
      /** Set the default deriv type and key, which is the value we want if we
       *  can't resolve it. These values let us know the attribute did not
       *  esolve when we report differences
       */
      Integer derivTypeKey = new Integer(0);
      if (rawDerivType != null ) {
		  // throws KeyNotFoundException, this Lookup does not have a translator
		  try {
			  derivTypeKey = derivationTypeKeyLookup.lookup(rawDerivType);
		  } catch (KeyNotFoundException e) {
			 // we set the default above, so just catch the exception
		  } catch (TranslationException e) { // won't happen, no translator for this vocab
			  // we set the default above, so just catch the exception
		  }
		  // set in resolved derivation object
		  resDeriv.setDerivationTypeKey(derivTypeKey);
		   // no translator, so raw derivation type is the bona fide term
			  resDeriv.setDerivationType(rawDerivType);
      }
      else {
          resDeriv.setDerivationTypeKey(derivTypeKey);
		  // derivationType not in input, set to String "null"
          resDeriv.setDerivationType("null");
      }

     /**
      * resolve creator if defined, set key to 0 and
      * String version to "null" if not
      */
      String rawCreator = raw.getCreator();
      
       /** Set the default creator and key, which is the value we want if we
        * can't resolve it. These values let us know the attribute did not
        * resolve when we report differences
        */
      String resCreator = "raw_" + rawCreator;
      Integer creatorKey = new Integer(0);
      
      if (rawCreator != null ) {
		  // throws KeyNotFoundException, this Lookup has a translator
		  try {
			  creatorKey = creatorKeyLookup.lookup(rawCreator);
		  } catch (KeyNotFoundException e) {
			  // we set the default above, so just catch the exception
		  } catch (TranslationException e) {
			  // we set the default above, so just catch the exception
		  }
		  // Now get the term for this key; term different than incoming
		  // term if translator was used. If we have the key, term will be found
		  if (creatorKey.intValue() != 0) {
			resCreator = termLookup.lookup(creatorKey);
		  }
		  // set in resolved derivation object
		  resDeriv.setCreatorKey(creatorKey);
		  resDeriv.setCreator(resCreator);
      }
      else {
          resDeriv.setCreatorKey(creatorKey);
		  // creator not in input, set to String "null"
          resDeriv.setCreator("null");
      }

     /**
      * resolve jNum if defined, set key to 0 and
      * String version to "null" if not. 
      */
      String jNum = raw.getJNum();
      if (jNum != null ) {
		  // Lookup returns null if key not found
		  Integer refsKey = jNumberLookup.lookup(jNum);
		  if (refsKey ==  null) {
			  refsKey = new Integer(0);
		  }
		   // set in resolved derivation object
		  resDeriv.setRefsKey(refsKey);
		  resDeriv.setJNum(jNum);
      }
      else {
		  resDeriv.setRefsKey(new Integer(0));
		  // jNum not in input, set to String "null"
		  resDeriv.setJNum("null");
      }
      return resDeriv;
    }
}
