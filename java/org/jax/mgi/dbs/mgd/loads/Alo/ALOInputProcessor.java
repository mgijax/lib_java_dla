package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashSet;

import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.config.ALOLoadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;
import org.jax.mgi.shr.dla.loader.alo.*;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that processes ALORawInput objects, one at a time, by resolving,
 * determining object identity in the database, and adding and updating ALO 
 * in a database
 * @has
 * <UL>
 * <LI>a logger
 * <LI>ALOLoaderAbstractFactory  
 * <LI>ALO -  Manages a set of DAOs representing Allele-like objects, 
 *        the object this processor is building by resolving the ALORawInput
 * <LI>MutantCellLineProcessor . processes a mutant cell line, may be null
 * <LI>AlleleProcessor - processes an allele, its accessions, mutation, note, 
 *         and references 
 * <LI>AlleleSequenceProcessor - processes any sequence to allele associations 
 *         and possibly other sequence information, may be null
 * </UL>
 * @does
 * <UL>
 * <LI>resolves ALORawInput attributes to DAOs
 * <LI>determines, if possible, object identity in the database
 * <LI>reports differences in incoming information with respect to the 
 *     data in the database
 * <LI> typically writes to bcp files and/or an sql script (for updates),
 *       however, the type of stream is determined from configuration.
 * </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class ALOInputProcessor {

	// a stream for handling MGD DAO objects
	protected SQLStream mgdStream;

	// get a ALO load configurator
	protected ALOLoadCfg config;

	// logger for the load
	protected DLALogger logger;
	// Factory from which is obtained specific instances of processors
	ALOLoaderAbstractFactory factory;

	// name of the jobtream
	protected String jobStreamName;

	// current number of alos processed i.e. added, updated or determined to
	// be the same as in the database

    // current number of ALOs added (new)
	int newCtr = 0;
    // current number of ALOs found to be in the database; may or may not be updated
    int existingCtr = 0;

	// processors
	MutantCellLineProcessor mclProcessor;
	AlleleProcessor alleleProcessor;
	AlleleSequenceProcessor alleleSeqProcessor;

	/**
	 * Constructs a ALOProcessor that adds and updates ALOs to/in
	 * a database
	 * @param mgdSqlStream stream for adding ALOs to an MGD database
	 * @throws CacheException if
	 * @throws DBException if
	 * @throws ConfigException if error creating/using config object
	 * @throws DLALoggingException if error creating/using a logger
	 */
	public ALOInputProcessor(SQLStream mgdSqlStream)
			throws MGIException, CacheException, DBException, ConfigException,
			DLALoggingException {

		mgdStream = mgdSqlStream;

		logger = DLALogger.getInstance();

		// configurator to lookup logicalDB
		config = new ALOLoadCfg();

		factory = ALOLoaderAbstractFactory.getFactory();

		jobStreamName = config.getJobstreamName();
		mclProcessor = factory.getMCLProcessor();
		alleleProcessor = factory.getAlleleProcessor();
		alleleSeqProcessor = factory.getAlleleSequenceProcessor();
	}

	/**
	 * Processes ALORawInput by passing it and an ALO object to specific
	 * processors which resolve specific attributes to States and place them
	 * in the ALO. Executes the ALO by calling its sendToStream() method.
	 * @assumes Nothing
	 * @effects queries and inserts into a database
	 * @param aloInput ALORawInput object - a set of raw attributes to resolve
	 * and add to the database
	 * @throws ALOLoaderException if there are configuration, cacheing, database,
	 * translation, io, or lookup errors. These errors cause load to fail.
	 * @throws RepeatALOException to indicate repeated ALO in the input
	 * @throws DerivationProcessorException to indicate not able to find a
	 *         derivation in the database
	 * @throws DerivationNameCreatorException to indicate not able to create
	 *         a derivation name to resolve to a derivation in the database
	 * @throws ALOResolvingException to indicate unable to resolve an ALO
	 *         attribute
	 * @throws CacheException if
	 * @throws DBException if
	 * @throws TranslationException if
	 * @throws ConfigException if error creating/using config object
	 * @throws DLALoggingException if error creating/using a logger
	 */
	public void processInput(ALORawInput aloInput) //throws MGIException {
			throws RepeatALOException, DerivationProcessorException,
			DerivationNameCreatorException, ALOResolvingException,
			CacheException, DBException, ConfigException,
			TranslationException, DLALoggingException, MGIException {
		// create an empty ALO object
		ALO incomingALO = factory.getALO(mgdStream);
		if (incomingALO == null) {
			System.out.println("ALOInputProcessor incomingALO is null");
		}

		// the set of Mcls existing in the databse for the current allele
		HashSet existingMclKeySet = null;

		// the allele key - may be new or existing allele
		Integer alleleKey = null;

		if (alleleSeqProcessor != null) {
			//System.out.println("ALOInputProcessor calling alleleSeqProcessor.preprocess");
			alleleSeqProcessor.preprocess(aloInput, incomingALO);
		}
		if (mclProcessor != null) {
			//System.out.println("ALOInputProcessor calling mclProcessor.process");

			// existingMclKeySet - keys of the mutant cell lines in input found
			// in the database
			existingMclKeySet = mclProcessor.process(aloInput, incomingALO);

			// set of resolved MutantCellLine objects processed by last call to
			// mclProcessor.process() -  represents new and existing MCLs
			HashSet incomingMCLs = mclProcessor.getCurrentIncomingMCLs();

			//System.out.println("ALOInputProcessor existingMclKeySet: " + existingMclKeySet.toString());
			//System.out.println("ALOInputProcessor calling alleleProcessor.process");

			// alleleKey - a new or existing allele key representing the current
			// allele we are processing
			alleleKey = alleleProcessor.process(aloInput, incomingALO,
					existingMclKeySet, incomingMCLs);
		} else {
			// not implemented yet - alleles with no mutant cell line
			//alleleProcessor.process(aloInput, incomingALO);
		}
		if (alleleSeqProcessor != null) {
			//logger.logcInfo("ALOInputProcessor calling alleleSeqProcessor.process", false);
			alleleSeqProcessor.process(aloInput, incomingALO, alleleKey);
		}

		//create allele MGI ID
		//System.out.println("ALOInputProcessor calling alleleProcessor.processAlleleMGIID");
		alleleProcessor.processAlleleMGIID(incomingALO);

		// send the ALO to the SQL Stream
		//System.out.println("ALOInputProcessor calling incomingALO.sendToStream");
		incomingALO.sendToStream();
        if(incomingALO.getIsUpdate().equals(Boolean.TRUE)) {
            existingCtr++;
        }
        else {
		    newCtr++;
        }
	//System.out.println("processedCtr: " + processedCtr);
	}

    public void postprocess() throws MGIException {
		System.out.println("Postprocessing ALOInputProcessor");
        alleleProcessor.postprocess();
        alleleSeqProcessor.postprocess();
    }
	/**
	 * Gets counts of ALOs created
	 * @assumes nothing
	 * @effects nothing
	 * @return number of ALOs created
	 */
	
	public int getNewCount() {
		return newCtr;
	}
    
    /**
	 * Gets counts of ALOs found to be in the database (existing) 
	 * @assumes nothing
	 * @effects nothing
	 * @return number of ALOs found to be in the database
	 */
    public int getExistingCount() {
        return existingCtr;
    }
}
