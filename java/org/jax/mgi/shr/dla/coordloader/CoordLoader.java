package org.jax.mgi.shr.dla.coordloader;

import org.jax.mgi.shr.dla.DLALoader;
import org.jax.mgi.shr.timing.Stopwatch;
import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.dbutils.ScriptWriter;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.config.ScriptWriterCfg;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.dbs.mgd.MolecularSource.UnresolvedAttributeException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.shr.dla.seqloader.SeqloaderConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.shr.ioutils.InputDataFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;

/**
 * a base class which extend DLALoader and implements the DLALoader methods
 * 'initialize' and 'run' to accomplish load initialization and processing.
 * It expects its subclasses to implement the DLALoader method
 * 'preprocess'. In addition it defines the abstract method
 * 'appPostProcess' so subclasses can add post-processing as desired.
 * @abstract this class provides the 'basic needs' objects for performing
 * Incremental OR delete/reload sequence loads such as a SeqProcessor (or
 * IncremSeqProcessor), MergeSplitProcessor, SequenceAttributeResolver,
 * SeqQCReporter, and a Writer for repeated sequences. <BR>
 * It implements the superclass 'initialize' and 'run' methods. <BR>
 * Sub classes would be required to implement the following
 * methods:<br>
 * <UL>
 *   <LI>preprocess - for performing pre processing
 *   <LI>getRecordDataIterator - to create an iterator for its input file with
 *       an appropriate interpreter. And create an optional OrganismChecker.
 *   <LI>appPostProcess - for any application specific post-processing
 * </UL>
 * @has a set of 'basic-needs' objects for doing DLA loads<br>
 * <UL>
 *   <LI>A RecordDataIterator
 *   <LI>The load mode
 *   <LI>A SeqProcessor
 *   <LI>A SequenceAttributeResolver
 *   <LI>A SeqQCReporter
 *   <LI>A SeqloaderExceptionFactory for Sequence load specific exceptions
 * </UL>
 * In addition, if incremental processing (loadMode=INCREMENTAL)
 * <UL>
 *   <LI>A ScriptWriter for updates
 *   <LI>A ScriptWriter for merges and splits
 *   <LI>A MergeSplitProcessor
 *   <LI>A BufferedWriter for repeated sequences
 *   <LI>A SequenceLookup for creating Sequence objects from a database query
 * </UL>
 *
 * @does performs initialization of 'basic-needs' objects for sequence loads, and
 *       processes sequences. Subclasses must provide a RecordDataIterator with
 *       an appropriate interpreter.
 * @author sc
 * @version 1.0
 */

public class CoordLoader extends DLALoader {
    /**
    * must be defined and set by subclass by implementing the
    * getRecordDataIterator method
    */

    // iterator over an input file
    protected DataIterator iterator;


    // provides access to Configuration values
    protected CoordLoadCfg loadCfg;

    // the number of valid sequences WITHOUT processing errors
    int processedCtr;

    // total processing time for the load
    double totalProcessTime;

    // coordinate processor
    CoordProcessor coordProcessor;


    /**
     * Initializes instance variables depending on load mode
     * @assumes Nothing
     * @effects instance variables will be instantiated
     * @throws MGIException if errors occur during initialization
     */
    public void initialize() throws MGIException {
        loadCfg = new CoordLoadCfg();

        // Create a DataInput File
        InputDataFile inData = new InputDataFile();

        // get an iterator for the InputDataFile with a configured interpreter
        iterator = inData.getIterator(
                (RecordDataInterpreter)loadCfg.getInterpreterClass());

        // number of valid sequences WITHOUT processing errors:
        processedCtr = 0;

        // create a CoordProcessor
        coordProcessor = new CoordProcessor(loadStream);

    }

    /**
     * Deletes this load's collection and all members of that collection
     * @assumes nothing
     * @effects deletes collection, map, and feature objects from a database
     * @throws MGIException if errors occur while deleting
     */

    public void preprocess() throws MGIException {
        // delete the collection and all its members
        coordProcessor.deleteCoordinates();
    }

    /**
     * to perform a database load into the RADAR and/or MGD
     * database
     * @assumes nothing
     * @effects database records created within the RADAR and/or MGD
     * database. If stream is a BCP stream, creates bcp files which may be
     * temporary or persistent depending on configuration
     * @throws MGIException throw if an error occurs while performing the
     * load
     */
    public void run()  throws MGIException {

       // report the load mode we are running under
       logger.logdInfo("CoordLoader running", true);

       // Timing the load
       Stopwatch loadStopWatch = new Stopwatch();
       loadStopWatch.start();

       // Data object representing the raw values of the current input record
       CoordinateInput input;

       // iterate thru the records and process them
       while(iterator.hasNext()) {
           input = (CoordinateInput)iterator.next();
           logger.logdDebug(input.getCoordMapFeatureRawAttributes().getObjectId());
           coordProcessor.processInput(input);

           processedCtr++;
           if (processedCtr  > 0 && processedCtr % 100 == 0) {
               logger.logdInfo("Processed " + processedCtr + " coordinates", false);
           }
       }
       loadStopWatch.stop();
       totalProcessTime = loadStopWatch.time();
    }


    /**
     * closes the load and qc SQLStreams. Reports load statistics.
     * In incremental_mode processes merges and splits, closes repeat sequence
     * writer.
     * @throws MGIException
     */
    public void postprocess() throws MGIException
    {
        logger.logdInfo("CoordLoader beginning post process", true);
        logger.logdInfo("Closing load stream", false);
        this.loadStream.close();

        reportLoadStatistics();
        logger.logdInfo("CoordLoader complete", true);
    }   /**

    /**
    * Reports load statistics; event counts, organism counts, valid sequence count
    * etc.
    * @assumes nothing
    * @effects nothing
    * @throws Nothing
    */
    private void reportLoadStatistics() {
        String message = "Total Load time in minutes: " +
            (totalProcessTime/60);
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        message = "Total Coordinates Processed = " + processedCtr;
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

    }

}
