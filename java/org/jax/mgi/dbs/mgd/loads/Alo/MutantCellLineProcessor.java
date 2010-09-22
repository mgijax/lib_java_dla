package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.dbs.mgd.dao.ACC_AccessionState;
import org.jax.mgi.dbs.mgd.dao.ALL_CellLineDAO;
import org.jax.mgi.dbs.mgd.dao.ALL_CellLineState;
import org.jax.mgi.dbs.mgd.dao.ALL_CellLineKey;
import org.jax.mgi.dbs.mgd.loads.Acc.*;
import org.jax.mgi.dbs.mgd.lookup.MGIUserKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config.ALOLoadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;
import org.jax.mgi.shr.dla.loader.alo.ALOResolvingException;
import org.jax.mgi.shr.dla.loader.alo.DerivationNameCreatorException;
import org.jax.mgi.shr.dla.loader.alo.DerivationProcessorException;
import org.jax.mgi.shr.dla.loader.alo.RepeatALOException;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that processes raw mutant cell line information 
 * @has
 * <UL>
 * <LI>a logger
 * <LI>ALOLoaderAbstractFactory  
 * <LI>MutantCellLineLookupByCellLineID
 * <LI>DerivationProcessor
 * <LI>AccAttributeResolver - for associating the cell line ID w/cell line
 * <LI>Set of mutant cell line IDs already processed
 * </UL>
 * @does resolves incoming mutant cell line information to states representing
 * <UL>
 * <LI>mutant cell line
 * <LI>mutant cell line ID association to the cell line object
 * </UL>
 * Determines, if possible, object identity in the database, reporting 
 * differences in incoming information with respect to the data in the database 
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class MutantCellLineProcessor {

	// get a ALO load configurator
	private ALOLoadCfg config;

	// logger for the load - for qc
	private DLALogger logger;
	// Factory from which we get ? QC Reporter?
	private ALOLoaderAbstractFactory factory;
	// resolve raw MCL to cell line state
	private MutantCellLineResolver mclResolver;
	// resolve a mutant cell line id to a ALL_CellLineDAO
	private MutantCellLineLookupByCellLineID mclLookupByID;
	// finds a derivation in the database and qc's incoming vs db attributes
	private DerivationProcessor derivProcessor;
	// resolves raw accession attributes to accession state
	private AccAttributeResolver accResolver;
	// if we've already seen this MCL ID in the input , write out the record
	// for a subsequent load invocation
	private HashSet mclIdsAlreadyProcessed;
	// the set of incoming MutantCellLine objects processed after the last 
	// call to the process method
	private HashSet currentIncomingMCLs;
	// if true update MCL derivation key
	private Boolean updateMCLDerivation;
	// the load jobstream user key
	private Integer userKey;

	/**
	 * Constructs a MutantCellLineProcessor
	 * @throws MGIException
	 */
	public MutantCellLineProcessor()
			throws MGIException {
		logger = DLALogger.getInstance();
		config = new ALOLoadCfg();
		factory = ALOLoaderAbstractFactory.getFactory();
		mclResolver = new MutantCellLineResolver();
		mclLookupByID = new MutantCellLineLookupByCellLineID();
		derivProcessor = new DerivationProcessor();
		accResolver = new AccAttributeResolver();
		mclIdsAlreadyProcessed = new HashSet();
		currentIncomingMCLs = new HashSet();
		updateMCLDerivation = config.getUpdateMCLDerivation();
		MGIUserKeyLookup userKeyLookup = new MGIUserKeyLookup();
		String jobstreamName = config.getJobstreamName();
		userKey = userKeyLookup.lookup(jobstreamName);
	}

	/**
	 * resolves raw cell line attributes to states and places them in the passed
	 * ALO object.
	 * @param aloInput ALORawInput object - a set of raw attributes to resolve
	 * and add to the database
	 * @param resolvedALO - the ALO object to which will will add resolved
	 *         cell line information
	 * @throws RepeatALOException to indicate repeated mutant cell line in
	 *   the input
	 * @throws DerivationProcessorException if cannot find a derivation in the db
	 * @throws DerivationNameCreatorException if missing attributes when trying
	 *         to create a derivation name
	 * @throws ALOResolvingException if errors resolving derivation or mutant
	 *         cell line attributes
	 * @throws CacheException if errors accessing a Lookup cache
	 * @throws DBException if errors adding to LazyCached lookups
	 * @throws ConfigException if resolvers have errors accessing configuration
	 * @throws TranslationException if resolvers have issues resolving translated
	 *         attributes
	 * @throws DLALoggingException if error reporting derivation or mutant cell
	 *         line discrepancies
	 */
	public HashSet process(ALORawInput aloInput, ALO resolvedALO)
			//throws MGIException {
			throws RepeatALOException, DerivationProcessorException,
			DerivationNameCreatorException, ALOResolvingException,
			CacheException, DBException, ConfigException, TranslationException,
			DLALoggingException {
		HashSet rawSet = aloInput.getCellLines();
		// set of existing mutant cell lines found
		HashSet existingMCLKeySet = new HashSet();

		// ALOs may have multiple cell lines (MCL)
		for (Iterator i = rawSet.iterator(); i.hasNext();) {
			CellLineRawAttributes mclRaw = (CellLineRawAttributes) i.next();
			String accID = mclRaw.getCellLineID();
			//System.out.println("MutantCellLineProcessor.process: accID: " + accID);
			// if we have already seen this mutant cell line ID in the input
			// skip this record
			if (mclIdsAlreadyProcessed.contains(accID)) {
				RepeatALOException e = new RepeatALOException();
				e.bindRecordString(" Mutant Cell Line ID " + accID);
				throw e;
			}
			// find a derivation object in the database for this MCL
			Derivation derivation = null;
			try {
				//System.out.println("MutantCellLineProcessor.process: calling derivProcessor.process(mclRaw)");
				derivation = derivProcessor.process(mclRaw);
			} catch (DerivationProcessorException e) {
				e.bindRecordString(" Mutant Cell Line ID " + accID);
				throw e;
			} catch (DerivationNameCreatorException e) {
				e.bindRecordString(" Mutant Cell Line ID " + accID);
				throw e;
			}
			// resolve raw mutant cell line attributes to a MutantCellLine object
			MutantCellLine incomingMCL = mclResolver.resolve(mclRaw, derivation);
			currentIncomingMCLs.add(incomingMCL);
			Integer incomingDerivKey = incomingMCL.getDerivationKey();
			Integer ldbKey = incomingMCL.getLogicalDBKey();
			// create lookup key to determine if MCL in db by MCL ID/ldbKey
			StringBuffer lookupKey = new StringBuffer();
			lookupKey.append(accID);
			lookupKey.append("|");
			lookupKey.append(ldbKey);
			//logger.logcInfo("lookupKey: " + lookupKey.toString(), false);
			// lookup the mutant cell line ID in  the database
			try {
			//logger.logcInfo("MutantCellLineProcessor looking up MCL/creator: " + lookupKey, false);
				MutantCellLine dbMCL = mclLookupByID.lookup(lookupKey.toString());
				// if we get here we've found it in the database:
				// add it to the set of existing MCLs to return
				existingMCLKeySet.add(dbMCL.getMCLKey());
				Integer dbDerivKey = dbMCL.getDerivationKey();
				// Update the MCL derivation if configured to do so
				if (updateMCLDerivation.equals(Boolean.TRUE) &&
				    ! incomingDerivKey.equals(dbDerivKey)) {
					ALL_CellLineState state = dbMCL.getState();
					state.setDerivationKey(incomingDerivKey);
					state.setModifiedByKey(userKey);
					Integer key = dbMCL.getMCLKey();
					ALL_CellLineDAO dao = new ALL_CellLineDAO(
							new ALL_CellLineKey(key), state);
					resolvedALO.addCellLineUpdate(dao);
					logger.logcInfo("Updating MCL derivation from " +
					    dbDerivKey + " to " + incomingDerivKey, false);
				}
				// compare incoming mutant cell line to one found in database
				// do this after updating any derivations
				incomingMCL.compare(dbMCL);

			} catch (KeyNotFoundException e) {
				// if we don't find a mutant cell line in the database we create
				// a new one, set the resolved MCL state in the ALO
				// System.out.println("MutantCellLineProcessor.process adding MCL to ALO");
				mclIdsAlreadyProcessed.add(accID);
				ALL_CellLineDAO mclDAO = resolvedALO.addCellLine(
						incomingMCL.getState());
				Integer mclKey = mclDAO.getKey().getKey();
				
				ACC_AccessionState accState = createMCLAccessionState(mclKey, accID, ldbKey);
				// set in resolved ALO
				resolvedALO.addAccession(accState);
			}
		}
		return existingMCLKeySet;
	}

	/**
	 * provides access to the incoming MCL objects from the last call to the
	 * process method
	 */
	public HashSet getCurrentIncomingMCLs() {
		return currentIncomingMCLs;
	}

	private ACC_AccessionState createMCLAccessionState(Integer mclKey, String mclID, Integer ldbKey) {
		ACC_AccessionState state = new ACC_AccessionState();
		state.setAccID(mclID);
		Vector idParts = AccessionLib.splitAccID(mclID);
		state.setPrefixPart((String) idParts.get(0));
		state.setNumericPart((Integer) idParts.get(1));
		state.setLogicalDBKey(ldbKey);
		state.setMGITypeKey(new Integer(MGITypeConstants.CELLLINE));
		state.setObjectKey(mclKey);
		state.setPrivateVal(Boolean.FALSE);
		state.setPreferred(Boolean.TRUE);
		return state;
	}
}
