package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.lookup.StrainKeyLookup;
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
 * An object that resolves raw mutant cell line attributes to a 
 *   MutantCellLine
 * @has
 * <UL>
 * <LI> StrainKeyLookup - resolves strain name to key
 * <LI> VocabKeyLookup - resolves cell line type to key e.g. 'ES Cell Line'
 * <LI> A MutantCellLine
 * <LI> A CellLineRawAttributes
 * </UL>
 * @does
 * <UL>
 * <LI>Resolves a CellLineRawAttributes object to a MutantCellLine
 * </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MutantCellLineResolver {
    // get cell line type term key given a cell line type
    private VocabKeyLookup typeLookup;

    // get a strain key given a strain  name - uses a translator
    private StrainKeyLookup strainLookup;
    
    // get logicaldb key given a ldb name
    private LogicalDBLookup ldbLookup;

     /**
     * Constructs a MutantCellLineResolver
     * @assumes Nothing
     * @effects queries a database to load each lookup cache
     * @throws TranslationException - if translation error creating or using
     *              strain lookup
     * @throws ConfigException - if configuration error creating a lookup
     * @throws DBException - if database error creating a lookup
     * @throws CacheException - if caching error creating a lookup
     */

    public MutantCellLineResolver() throws TranslationException,
        ConfigException, DBException, CacheException {

        typeLookup = new VocabKeyLookup(VocabularyTypeConstants.CELLLINE_TYPE,
	    CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
	
        strainLookup = new StrainKeyLookup();
	
	ldbLookup = new LogicalDBLookup();
    }

    /**
      * resolves a CellLineRawAttributes object to a ALL_CellLineState
      * @assumes Derivation creator name is the same as logicalDB name
      * @effects Nothing
      * @param rawAttributes the CellLineRawAttributes object to resolve
      * @param dbDerivation the derivation object for this mutant cell line
      * @return A ALL_CellLineState
      * @throws ALOResolvingException if any of the lookups fail to find a key
      * @throws DBException if error adding to any lazy cached lookups
      * @throws CacheException if error doing lookup
      * @throws ConfigException if error doing lookup
      *
      */
    public MutantCellLine resolve(CellLineRawAttributes raw, 
	Derivation dbDerivation) throws ALOResolvingException, DBException, 
		CacheException, ConfigException, DLALoggingException  {
      // the object we are building
      MutantCellLine resMCL = new MutantCellLine();
      
      /**
       * resolve cell line name 
       */
      String cellLine = raw.getCellLine();
      if (cellLine == null) {
	  ALOResolvingException resE = new ALOResolvingException();
	  resE.bindRecordString("For MCL: " + cellLine + 
		" MCL Name/null");
	  throw resE;
      }
      resMCL.setCellLine(cellLine);
      
      /**
       * resolve cell line type
       */
      String type = raw.getType();
      if (type != null) {
	  Integer typeKey = null;
	  try {
	      typeKey = typeLookup.lookup(type);
	  } catch (KeyNotFoundException e) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("For MCL: " + cellLine + 
		" MCL Type/" + type);
	      throw resE;
	  } catch (TranslationException e) { // won't happen, no translator 
						  //for this vocab
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("For MCL: " + cellLine + 
		" MCL Type/" + type);
	      throw resE;
	  }
	  resMCL.setCellLineType(type);
	  resMCL.setCellLineTypeKey(typeKey);
      }
      else {
	  ALOResolvingException resE = new ALOResolvingException();
	  resE.bindRecordString("For MCL: " + cellLine + 
		" MCL Type/null");
	  throw resE;
      }
      /**
       * resolve cell line strain 
       */
      String strain = raw.getStrain();
      if (strain != null) {
	  Integer strainKey = null;
	  try {
	      strainKey = strainLookup.lookup(raw.getStrain());
	  } catch (KeyNotFoundException e) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("For MCL: " + cellLine + 
		" MCL Strain/" + strain );
	      throw resE;
          } catch (TranslationException e) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString(" For MCL: " + cellLine + 
		" MCL Strain/" + strain );
	      throw resE;
	  }
	  resMCL.setStrain(strain);
	  resMCL.setStrainKey(strainKey);
      }
      else { 
	 resMCL.setStrain(dbDerivation.getParentStrain());
         resMCL.setStrainKey(dbDerivation.getParentStrainKey());
      }
      /**
       * set the derivation key
       */
      resMCL.setDerivationKey(dbDerivation.getDerivationKey());
      
      /**
       * set Boolean flags
       */
     
      Boolean isMutant = raw.getIsMutant();
      if (isMutant == null ) {
	  ALOResolvingException resE = new ALOResolvingException();
	  resE.bindRecordString("For MCL: " + cellLine + 
		" MCL isMutant/null");
	  throw resE;
      }
      resMCL.setIsMutant(isMutant);
      
      /**
       * resolve accID 
       */
      String accID = raw.getCellLineID();
      if (accID == null) {
	  ALOResolvingException resE = new ALOResolvingException();
	  resE.bindRecordString("For MCL: " + cellLine + 
		" MCL accID/null");
	  throw resE;
      }
      resMCL.setAccID(accID);
      
      /**
       * resolve logical db name 
       */
      String ldbName = raw.getLogicalDB();
      if (ldbName != null) {
	  Integer ldbKey = null;
	  try {
	      ldbKey = ldbLookup.lookup(ldbName);
	  } catch (KeyNotFoundException e) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("For MCL: " + cellLine + 
		  " MCL LDB/" + ldbName );
	      throw resE;
	  }
	  resMCL.setLdbName(ldbName);
	  resMCL.setLdbKey(ldbKey);
      }
      else {
	  ALOResolvingException resE = new ALOResolvingException();
	  resE.bindRecordString("For MCL: " + cellLine + 
		" MCL logical DB/null");
	  throw resE;
      }      
      return resMCL;
    }
}

