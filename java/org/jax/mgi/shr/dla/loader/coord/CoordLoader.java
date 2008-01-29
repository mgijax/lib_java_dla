package org.jax.mgi.shr.dla.loader.coord;

import org.jax.mgi.shr.dla.loader.DLALoader;
import org.jax.mgi.shr.timing.Stopwatch;
import org.jax.mgi.shr.config.CoordLoadCfg;
import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.dla.input.CoordinateInput;
import org.jax.mgi.shr.dla.loader.DLALoaderException;
import org.jax.mgi.dbs.mgd.loads.Coord.CoordinateInputProcessor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

/**
 * A base class which extend DLALoader and implements the DLALoader methods
 * 'initialize', 'preprocess', 'run', and 'postprocess'
 * @has a set of  objects for doing Coordinate loads<br>
 * <UL>
 *   <LI>A DataIterator for iterating over an input file - see note below.
 *   <LI>A CoordinateInputProcessor for processing a CoordInput object
 *   <LI>A BufferedWriter for writing out repeated coordinate features
 * </UL>
 *
 * @does performs initialization of objects for coordinate loads, and
 *       processes coordinates. Keeps count of repeated coordinates in the input
 *       and writes them out to a file.
 * @notes assumes a delete/reload strategy - see preprocess method
 * @notes assumes it is iterating over a file; could subclass to set a different
 *       kind of iterator e.g. a RowDataIterator over a ResultSet.
 * @author sc
 * @version 1.0
 */

public class CoordLoader extends DLALoader {

    // iterator over an input file
    protected DataIterator iterator;

    // provides access to Configuration values
    protected CoordLoadCfg loadCfg;

    // current number of coordinate features processed
    int processedCtr;

    // total processing time for the load
    double totalProcessTime;

    // coordinate processor
    CoordinateInputProcessor coordProcessor;

    //  cache of ids of coordinate records we have already processed
    private HashSet coordIdsAlreadyProcessed;

    // count of coordinate records we have already processed
    private int coordIdsAlreadyProcessedCtr;

    // true if we are going to load multiple coordinates for an object
    private String loadMultiCoord;

    // writer for all coordinates repeated in the input
    private BufferedWriter multiCoordWriter;

    /**
     * constructor
     * @throws DLALoaderException thrown from the base class
     */
    public CoordLoader() throws DLALoaderException
    {
        super();
    }

    /**
     * Initializes instance variables
     * @throws MGIException if errors occur during initialization
     */
    public void initialize() throws MGIException {

        loadCfg = new CoordLoadCfg();
        loadMultiCoord = loadCfg.getCoordRepeatsOk();
        // Create a DataInput File
        InputDataFile inData = new InputDataFile();

        // get an iterator for the InputDataFile with a configured interpreter
        iterator = inData.getIterator(
                (RecordDataInterpreter)loadCfg.getInterpreterClass());

        // writes repeated input coordinates to a file
	if (loadMultiCoord.equals("false")) {
	    try {
		multiCoordWriter = new BufferedWriter(new FileWriter(loadCfg.
		    getRepeatFileName()));
	    }
	    catch (IOException e) {
		throw new MGIException(e.getMessage());
	    }
	}
        // number of valid coordinates WITHOUT processing errors:
        processedCtr = 0;

        // create a CoordinateInputProcessor
        coordProcessor = new CoordinateInputProcessor(loadStream);

        // create the set for storing coordinate ids we have already processed
        coordIdsAlreadyProcessed = new HashSet();

         // count of coordinate records whose coordinate ids we have already processed
        coordIdsAlreadyProcessedCtr = 0;
    }

    /**
     * depending on load mode does:
     * 1) deletes Collection, all maps and features, creates new collection
     * 2) adds to an existing collection, creating collection object if it
     *    does not exist 
     * @effects deletes collection, map, and feature objects from a database
     *  depending on mode
     * @throws MGIException if errors occur while deleting
     */

    public void preprocess() throws MGIException {
            
    }

    /**
     * Gets records from input file, resolves attributes, creates coordinate
     * database objects
     * @effects database records created. If stream is a BCP stream,
     * creates bcp files
     * @throws MGIException thrown if an error occurs while processing the
     * load
     */
    public void run()  throws MGIException {

       // Timing the load
       Stopwatch loadStopWatch = new Stopwatch();
       loadStopWatch.start();

       // Data object representing the raw values of the current input record
       CoordinateInput input;

       // iterate thru the records and process them
       while(iterator.hasNext()) {
           // get the next CoordinateInput object
           input = (CoordinateInput)iterator.next();
           logger.logdDebug(input.getCoordMapFeatureRawAttributes().getObjectId());
	   if (loadMultiCoord.equals("false")) {
	       try {
		   // determine if repeated coordinate
		   String currentCoordId = input.getCoordMapFeatureRawAttributes().
		       getObjectId();
		   if (coordIdsAlreadyProcessed.contains(currentCoordId)) {
		       // we have a repeated coordinate; count it, write it out,
		       // go on to next coordinate record in the input
		       coordIdsAlreadyProcessedCtr++;
		       multiCoordWriter.write(input.
					     getCoordMapFeatureRawAttributes().
					     getRecord() + "\n");
		       logger.logdDebug("Repeat Coordinate: " + currentCoordId);
		       continue;
		   }
		   else {
		       // add the coordinate id to the set we have processed
		       coordIdsAlreadyProcessed.add(currentCoordId);
		   }
	       }
	       catch (IOException e) {
		   throw new MGIException(e.getMessage());
	       }
	   }
           // process the coordinate any exceptions stop the load
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
        if (loadMultiCoord.equals("false")) {
            // close the repeat coordinate writer
            logger.logdInfo("Closing repeat coordinate writer", false);
            try {
                multiCoordWriter.close();
            }
            catch (IOException e) {
                throw new MGIException(e.getMessage());
            }
        }

        reportLoadStatistics();
        logger.logdInfo("CoordLoader complete", true);
    }

    /**
    * Reports load statistics; load time, # coordinates processed, #
    * repeated coordinates in the input etc.
    */
    private void reportLoadStatistics() {
        String message = "Total Load time in minutes: " +
            (totalProcessTime/60);
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        message = "Total Features Looked at: " + processedCtr;
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

	message = "Total Features Repeated in Input (written to repeat file): " + coordIdsAlreadyProcessedCtr;
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

	int featuresInMGICount = coordProcessor.getFeatureInMGICount();
	message = "Total Coordinates already in MGI - see Curation Log: " + featuresInMGICount;
	logger.logdInfo(message, false );
        logger.logpInfo(message, false );

	int featuresDiffInMGICount = coordProcessor.getFeatureDiffInMGICount();
        message = "Total Features in MGI with different attributes (diff start, end, chr, or strand) - see Curation Log: " + featuresDiffInMGICount;
        logger.logdInfo(message, false );
        logger.logpInfo(message, false );

	int featureObjectNotInMGICount = 
		coordProcessor.getFeatureObjectNotInMGICount();
	message = "Total Feature Objects (e.g. SEQ_Sequence or MRK_Marker) not in MGI  - see Validation Log: " + featureObjectNotInMGICount;
	logger.logdInfo(message, false );
        logger.logpInfo(message, false );

	int totalLoaded = processedCtr - coordIdsAlreadyProcessedCtr - featuresInMGICount - featuresDiffInMGICount - featureObjectNotInMGICount;
	message = "Total Coordinates Loaded: " + totalLoaded;
	logger.logdInfo(message, false );
        logger.logpInfo(message, false );

    }
}
