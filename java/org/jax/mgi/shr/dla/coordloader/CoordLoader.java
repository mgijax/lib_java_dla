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
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.shr.ioutils.InputDataFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashSet;

/**
 * a base class which extend DLALoader and implements the DLALoader methods
 * 'initialize', 'preprocess', 'run', and 'postprocess'
 * @has a set of  objects for doing Coordinate loads<br>
 * <UL>
 *   <LI>A DataIterator for iterating over an input file
 *   <LI>A CoordProcessor for processing a CoordInput object
 *   <LI>A BufferedWriter for writing out repeated coordinates
 *   <LI>A SeqQCReporter
 * </UL>
 *
 * @does performs initialization of objects for coordinate loads, and
 *       processes coordinates. Keeps count of repeated coordinates in the input
 *       and writes them out to a file.
 * @note assumes it is iterating over a file; could subclass to set a different
 *       kind of iterator e.g. a RowDataIterator over a ResultSet.
 * @author sc
 * @version 1.0
 */

public class CoordLoader extends DLALoader {

    // iterator over an input file
    protected DataIterator iterator;

    // provides access to Configuration values
    protected CoordLoadCfg loadCfg;

    // current number of coordinates processed
    int processedCtr;

    // total processing time for the load
    double totalProcessTime;

    // coordinate processor
    CoordProcessor coordProcessor;

    //  cache of seqids we have already processed
    private HashSet coordIdsAlreadyProcessed;

    // count of sequence records whose seqids we have already processed
    private int coordIdsAlreadyProcessedCtr;


    // writer for all coordinates repeated in the input
    private BufferedWriter repeatSeqWriter;

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

        // writes repeated input coordinates to a file
       try {
             repeatSeqWriter = new BufferedWriter(new FileWriter(loadCfg.
                 getRepeatFileName()));
        }
        catch (IOException e) {
             throw new MGIException(e.getMessage());
        }

        // number of valid coordinates WITHOUT processing errors:
        processedCtr = 0;

        // create a CoordProcessor
        coordProcessor = new CoordProcessor(loadStream);

        // create the set for storing coordinate ids we have already processed
        coordIdsAlreadyProcessed = new HashSet();

         // count of coordinate records whose coordinate ids we have already processed
        coordIdsAlreadyProcessedCtr = 0;
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
           try {

               String currentSeqid = input.getCoordMapFeatureRawAttributes().getObjectId();
               logger.logdDebug(currentSeqid, false);
               if (coordIdsAlreadyProcessed.contains(currentSeqid)) {
                   // we have a repeated coordinate; count it, write it out,
                   // go on to next coordinate record in the input
                   coordIdsAlreadyProcessedCtr++;
                   repeatSeqWriter.write(input.getCoordMapFeatureRawAttributes().getRecord() +"\n");
                   logger.logdDebug("Repeat Sequence: " + currentSeqid);
                   continue;
               }
               else {
                   // add the coordinate id to the set we have processed
                   coordIdsAlreadyProcessed.add(currentSeqid);
               }
           }
           catch (IOException e) {
               throw new MGIException(e.getMessage());
           }

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
     * closes the load SQLStreams, closes repeat coordinate writer.
     * Reports load statistics.
     * @throws MGIException
     */
    public void postprocess() throws MGIException
    {
        logger.logdInfo("CoordLoader beginning post process", true);
        logger.logdInfo("Closing load stream", false);
        this.loadStream.close();

        // close the repeat coordinate writer
        logger.logdInfo("Closing repeat coordinate writer", false);
        try {
            repeatSeqWriter.close();
        }
        catch (IOException e) {
            throw new MGIException(e.getMessage());
        }

        reportLoadStatistics();
        logger.logdInfo("CoordLoader complete", true);
    }

    /**
    * Reports load statistics; load time, # coordinates processed, #
    * repeated coordinates in the input etc.
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
        logger.logdInfo("Total Repeat Coordinates written to repeat file: "
                        + coordIdsAlreadyProcessedCtr, false);
        logger.logpInfo("Total Repeat Coordinates written to repeat file: "
                        + coordIdsAlreadyProcessedCtr, false);


    }

}
