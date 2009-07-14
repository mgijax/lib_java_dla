package org.jax.mgi.shr.dla.loader.alo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.dbs.mgd.loads.Alo.ALOInputProcessor;
import org.jax.mgi.dbs.mgd.loads.Alo.ALOLoaderAbstractFactory;
import org.jax.mgi.shr.config.ALOLoadCfg;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;
import org.jax.mgi.shr.dla.loader.DLALoader;
import org.jax.mgi.shr.exception.MGIException;

/**
 * @is an object that processes ALO input and resolves raw ALO values to MGI 
 *     values to create ALO objects in a database
 * @has 
 * <UL>
 * <LI>See superclass
 * <LI>ALOLOaderAbstractFactory to get a DataIterator and a ALOInputProcessor
 * <LI>DataIterator to iterate over ALORawInput objects
 * <LI>ALOInputProcessor to process each ALORawInput object
 * <LI>Configurator to get configuration settings
 * </UL>
 * @does
 * <UL>
 * <LI>Iterates over
 * </UL>
 * @author sc
 * @version 1.0
 */
public class ALOLoader extends DLALoader {

	// ALO Factory from which we get the DataIterator
	private ALOLoaderAbstractFactory factory;
	// iterator over  input
	private DataIterator iterator;

	// processor with which to process each ALORawInputobject
	private ALOInputProcessor processor;
	// provides access to Configuration values
	private ALOLoadCfg loadCfg;

	// write out records repeated in the input
	private BufferedWriter repeatWriter;
	// count of ALO records which are repeated in the input
	private int repeatALOCt = 0;

	// count of ALO records with errors
	private int errorCt = 0;

	// current number of valid ALOs looked at
	private int inputRcdCt = 0;

	// total processing time for the load
	private double totalProcessTime = 0;

	// memory debug
	Runtime runTime = Runtime.getRuntime();

	/**
	 * This load has no preprocessing
	 * @effects noting
	 * @throws MGIException if errors occur during preprocessing
	 */
	protected void preprocess() {
	}

	/**
	 * Initializes instance variables
	 * @throws MGIException if errors occur during initialization
	 */
	public void initialize() throws MGIException {
		loadCfg = new ALOLoadCfg();
		//System.out.println("Initializing ALOLoader");
		factory = ALOLoaderAbstractFactory.getFactory();
		if (factory == null) {
			throw new MGIException("Invalid LOAD_PROVIDER configuration setting");
		}
		iterator = factory.getDataIterator();
		processor = new ALOInputProcessor(loadStream);
		try {
			// writes records repeated in the input
			repeatWriter = new BufferedWriter(new FileWriter(loadCfg.getRepeatFileName()));
		} catch (IOException e) {
			MGIException e1 = new MGIException(e.getMessage());
			throw e1;
		}
		logger.logcInfo("Initializing curator log", true);
	}

	/**
	 * Gets objects created from input records, resolves attributes, creates ALO
	 * database objects
	 * @effects database records created. If stream is a BCP stream,
	 *          creates bcp files
	 * @throws MGIException thrown if an error occurs while processing the
	 * load
	 */
	public void run() throws MGIException {
		logger.logdInfo("ALOLoader running", true);
        logger.logcInfo("ALOLoader running", true);
		
		// Data object representing the raw values of the current input record
		ALORawInput rawInput;
		logger.logdInfo("FreeMem After loading lookups: " + runTime.freeMemory(), false);
		// iterate thru the records and process them
		while (iterator.hasNext()) {
			
			// get the next input object
			rawInput = (ALORawInput) iterator.next();

			//HashSet set = rawInput.getCellLines();
			//System.out.println("CellLineSet.size: " + set.size());
			try {
				processor.processInput(rawInput);
			} catch (RepeatALOException e) {
				try {
					repeatALOCt++;
					repeatWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
				} catch (IOException e1) {
					throw new MGIException(e1.getMessage());
				}
			} catch (ALOResolvingException e) {
				errorCt++;
				logger.logcInfo(e.getMessage(), false);
			} catch (DerivationProcessorException e) {
				errorCt++;
				logger.logcInfo(e.getMessage(), false);
			} catch (DerivationNameCreatorException e) {
				errorCt++;
				logger.logcInfo(e.getMessage(), false);
			} catch (SeqAssocWithAlleleException e) {
				errorCt++;
				logger.logcInfo(e.getMessage(), false);
			} catch (SeqAssocWithMarkerException e) {
				errorCt++;
				logger.logcInfo(e.getMessage(), false);
			} catch (MutantCellLineAlleleException e) {
				errorCt++;
				logger.logcInfo(e.getMessage(), false);
			} catch (SequenceNotInDatabaseException e) {
				errorCt++;
				logger.logcInfo(e.getMessage(), false);
			} catch (CellLineIDInAlleleNomenException e) {
				errorCt++;
				logger.logcInfo(e.getMessage(), false);
			} catch (AlleleMutationProcessorException e) {
				errorCt++;
				logger.logcInfo(e.getMessage(), false);
			}

			inputRcdCt++;
			int ctr = inputRcdCt;
			if (ctr > 0 && ctr % 100 == 0) {
				logger.logdInfo("Processed " + ctr + " gene traps", true);
				logger.logdInfo("FreeMem: " + runTime.freeMemory(), false);
			}
		}
	}

	/**
	 * close resources, update ACC_AccessionMax, report processed and
	 * added counts
	 * @throws MGIException if errors occur during preprocessing
	 */
	protected void postprocess() throws MGIException {
        System.out.println("Postprocessing ALOLoader");
        processor.postprocess();
		
        // executes bcp and any sql script
		loadStream.close();
		try {
			// close repeat writer
			repeatWriter.close();
		} catch (IOException e) {
			throw new MGIException(e.getMessage());
		}

		// If any new MGI IDs have been generated during processing, the
		// ACC_AccessionMax table needs to be updated with the new maximum
		// value.
		AccessionLib.commitAccessionMax();

		/**
		 * report counts
		 */
		int newCt = processor.getNewCount();
        int existingCt = processor.getExistingCount();

		//processedCt = processedCt - repeatALOCt;

		logger.logdInfo("Total input records processed: " + inputRcdCt, false);
		logger.logdInfo("Total ALOs added: " + newCt, false);
        logger.logdInfo("Total ALOs in database (may have been updated): " + existingCt, false);
		logger.logdInfo("Total ALOs repeated in the input: " + repeatALOCt, false);
		logger.logdInfo("Total ALOs Not Loaded. Discrepancies written to curator " +
				"log: " + errorCt, false);

		logger.logpInfo("Total input records processed: " + inputRcdCt, false);
        logger.logpInfo("Total ALOs added: " + newCt, false);
		logger.logpInfo("Total ALOs in database (may have been updated): " + existingCt, false);
		logger.logpInfo("Total ALOs repeated in the input: " + repeatALOCt, false);
		logger.logpInfo("Total ALOs Not Loaded. Discrepancies written to curator " +
				"log: " + errorCt, false);
	}
}
