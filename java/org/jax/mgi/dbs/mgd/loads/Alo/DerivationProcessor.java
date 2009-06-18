package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.loader.alo.ALOResolvingException;
import org.jax.mgi.shr.dla.loader.alo.DerivationNameCreator;
import org.jax.mgi.shr.dla.loader.alo.DerivationNameCreatorException;
import org.jax.mgi.shr.dla.loader.alo.DerivationProcessorException;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * an object that determines object identity between an incoming set of raw
 * derivation values and a derivation in the database
 *
 * Note: it is not the responsibility of this class to *create* derivation
 * objects to be added to a database 
 * @has
 * <UL>
 * <LI>ALOLoaderAbstractFactory - for getting a specific DerivationNameCreator
 * <LI>DerivationNameCreator - creates a derivation name from incoming
 *     derivation attributes according to a set of rules
 * <LI>DerivationDAOLookupByName - to get a derivation DAO object for a 
 *     database
 * <LI>DerivationResolver - to resolve a DerivationRawAttributes object
 *     to a Derivation object 
 * </UL>
 * @does
 * <UL>
 * <LI>processes raw cell line derivation information
 * by resolving the derivation name to a ALL_DerivationDAO from the
 * database, if it can, and reports differences in resolved incoming
 * <LI>If it can't resolve th derivatio name it uses its DerivationNameCreator
 * to create a 'name' that might translate to a cerivation in the database
 * </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class DerivationProcessor {
    // factory from which to get ??
    private ALOLoaderAbstractFactory factory;
    
    // calculates a derivation name from raw attributes
    private DerivationNameCreator nameCreator;
    // resolves a DerivationRawAttributes to a Derivation
    private DerivationResolver resolver;
    
    // This lookup has a translator as well as a cache of Derivation names
    // mapped to Derivation objects
    private DerivationLookupByName lookup;
    
    private DLALogger logger;

    /** construct a DerivationProcessor */
    public DerivationProcessor() throws MGIException {
	factory = ALOLoaderAbstractFactory.getFactory();
	nameCreator = factory.getDerivationNameCreator();
	resolver = new DerivationResolver();
	lookup = new DerivationLookupByName();
	logger = DLALogger.getInstance();
    }

  /**
   * Find a derivation object in the database and return its database key
   * resolve incoming derivation attributes and compare to those found in
   * the database
   * @param rawInput - cell line raw attributes  which contain derivation
   *         raw attributes
   * @returns Derivation object from the database
   * @throws CacheException if error accessing lookup cache
   * @throws DBException if error using lookup 
   * @throws TranslationException if error accessing the translation cache
   */

   public Derivation process(CellLineRawAttributes mutCellLineRaw) 
	throws CacheException, DBException, ConfigException, TranslationException,
	    DerivationProcessorException, 
	    DerivationNameCreatorException, DLALoggingException {

	String mutCellLine = mutCellLineRaw.getCellLine();
	DerivationRawAttributes derivRaw = mutCellLineRaw.getDerivation();
	
	// resolve the incoming derivation attributes 
	Derivation incomingDeriv = resolver.resolve(derivRaw);
	
	// this is the raw derivation name (mutant cell line library) 
	// or the term "null" if not specified in the input
	String derivName = incomingDeriv.getName();
	// lookup the raw derivation name to see if it can translate to a 
	// derivation in the database
	Derivation dbDeriv = null;
	dbDeriv = lookup.lookup(derivName);
	// if the raw derivation does not translate, calculate a new name and
	// try to translate that
	String newDerivName = null;
	if (dbDeriv == null) {
		newDerivName = nameCreator.create(incomingDeriv);
		logger.logdDebug(newDerivName);
		dbDeriv = lookup.lookup(newDerivName);
		
	}
	// if the calculated name does not translate throw exception
	if ( dbDeriv == null) {
	    DerivationProcessorException e =  new DerivationProcessorException();
	    e.bindRecordString("Can't resolve calculated derivation name: " + newDerivName);
	    throw e;
	}
		
	/*
	 * if we get here we have identified a Derivation in the database.
	 * Report any differences between resolved incoming and existing 
	 * derivation attributes and return the database derivation key
	 */
	incomingDeriv.compare(dbDeriv, mutCellLine);
	return dbDeriv;
    }
}
