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
import org.jax.mgi.shr.ioutils.IOUException;
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

	// write out records skipped because couldn't be resolved
	// this does NOT included those that have record format errors
	private BufferedWriter unresolvedWriter;
	// count of ALO records with resolving errors
	private int unresCt = 0;

	// count of records with format errors
	private int formatErrorCt = 0;

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
		//System.out.println("Getting data iterator");
		iterator = factory.getDataIterator();
		//System.out.println("Initializing ALOInputProcessor");
		processor = new ALOInputProcessor(loadStream);
		try {
			//System.out.println("Creating repeatWriter");
			// writes records repeated in the input
			repeatWriter = new BufferedWriter(new FileWriter(loadCfg.getRepeatFileName()));

			//System.out.println("Creating unresolved writer");
			// writes records  with errors so they may be processed later
			unresolvedWriter = new BufferedWriter(new FileWriter(loadCfg.getUnresolvedRecordFileName()));
		} catch (IOException e) {
			MGIException e1 = new MGIException(e.getMessage());
			throw e1;
		}
		//System.out.println("Initializing curator log");
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
		ALORawInput rawInput = null;
		logger.logdInfo("FreeMem After loading lookups: " + runTime.freeMemory(), false);
		// iterate thru the records and process them
		while (iterator.hasNext()) {
			try {

			    // get the next input object
			    rawInput = (ALORawInput) iterator.next();
			} catch (IOUException e) {
                                formatErrorCt++;
                                logger.logcInfo("This sequence was NOT written to the unresolved file" , false);
				continue;
			}
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
				unresCt++;
				logger.logcInfo(e.getMessage(), false);
			    try {
				unresolvedWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
			    } catch (IOException e1) {
					throw new MGIException(e1.getMessage());
			    }
			} catch (DerivationProcessorException e) {
				    unresCt++;
				    logger.logcInfo(e.getMessage(), false);
			    try {
				unresolvedWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
			    } catch (IOException e1) {
					throw new MGIException(e1.getMessage());
			    }
			} catch (DerivationNameCreatorException e) {
				    unresCt++;
				    logger.logcInfo(e.getMessage(), false);
			    try {
				unresolvedWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
			    } catch (IOException e1) {
					throw new MGIException(e1.getMessage());
			    }
			} catch (SeqAssocWithAlleleException e) {
				    unresCt++;
				    logger.logcInfo(e.getMessage(), false);
				try {
				    unresolvedWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
				} catch (IOException e1) {
					throw new MGIException(e1.getMessage());
				}
			} catch (SeqAssocWithMarkerException e) {
				    unresCt++;
				    logger.logcInfo(e.getMessage(), false);
				try {
				    unresolvedWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
				} catch (IOException e1) {
					throw new MGIException(e1.getMessage());
				}
			} catch (MutantCellLineAlleleException e) {
				    unresCt++;
				    logger.logcInfo(e.getMessage(), false);
				try {
				    unresolvedWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
				} catch (IOException e1) {
							throw new MGIException(e1.getMessage());
				}
			} catch (SequenceNotInDatabaseException e) {
				    unresCt++;
				    logger.logcInfo(e.getMessage(), false);
				try {
				    unresolvedWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
				} catch (IOException e1) {
							throw new MGIException(e1.getMessage());
				}
			} catch (CellLineIDInAlleleNomenException e) {
				    unresCt++;
				    logger.logcInfo(e.getMessage(), false);
				try {
				    unresolvedWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
				} catch (IOException e1) {
							throw new MGIException(e1.getMessage());
				}
			} catch (AlleleMutationProcessorException e) {
						unresCt++;
						logger.logcInfo(e.getMessage(), false);
				try {
				    unresolvedWriter.write(rawInput.getInputRecord() + ALOLoaderConstants.CRT);
				} catch (IOException e1) {
					throw new MGIException(e1.getMessage());
				}
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
		//System.out.println("Postprocessing ALOLoader");
		processor.postprocess();
			
		// executes bcp and any sql script
		loadStream.close();
		try {
			// close repeat writer
			repeatWriter.close();
		unresolvedWriter.close();
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
                logger.logdInfo("Total ALOs with format errors. Discrepancies written to curator log: " +
                    formatErrorCt, false);
		logger.logdInfo("Total input records processed: " + inputRcdCt, false);
		logger.logdInfo("Total ALOs added: " + newCt, false);
		logger.logdInfo("Total ALOs in database (may have been updated): " + existingCt, false);
		logger.logdInfo("Total ALOs repeated in the input: " + repeatALOCt, false);
		logger.logdInfo("Total unresolved ALOs. Sequences written to " +
                "Unresolved file, discrepancies written to curator " +
				"log: " + unresCt, false);

		logger.logpInfo("Total ALOs with format issues. Discrepancies written to curator log: " +
                    formatErrorCt, false);
		logger.logpInfo("Total input records processed: " + inputRcdCt, false);
		logger.logpInfo("Total ALOs added: " + newCt, false);
		logger.logpInfo("Total ALOs in database (may have been updated): " + existingCt, false);
		logger.logpInfo("Total ALOs repeated in the input: " + repeatALOCt, false);
		logger.logpInfo("Total unresolved ALOs. Sequences written to " +
                "Unresolved file, discrepancies written to curator " +
				"log: " + unresCt, false);
	}
}
