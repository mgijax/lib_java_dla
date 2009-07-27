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
import org.jax.mgi.dbs.mgd.lookup.CoordMapCollectionKeyLookup;

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
 *   <LI>A BufferedWriter for writing out objects with multiple coordinates; first
 *       coordinate found is processed
 * </UL>
 *
 * @does performs initialization of objects for coordinate loads, and
 *       processes coordinates. Keeps count of objects with multiple coordinates 
 *       in the input and writes them out to a file.
 * @notes assumes a delete/reload strategy - see preprocess method
 * @notes assumes it is iterating over a file; could subclass to set a different
 *       kind of iterator e.g. a RowDataIterator over a ResultSet.
 * @author sc
 * @version 1.0
 */

public class CoordLoader extends DLALoader {

    // iterator over an input file
    private DataIterator iterator;

    // provides access to Configuration values
    private CoordLoadCfg loadCfg;

    // current number of coordinates processed (i.e. total in the input)
    private int totalProcessedCtr;

    // current number of coordinates that will be loaded into the database
    private int totalLoadedCtr;
    
    // if we are not loading multiple coordinates per object we use this counter
    // This counter is incremented # times objects in input minus one. So if 
    // object is seqid AA12345 and it is in the input with three sets of 
    // coordinates, this counter is incremented twice
    private int totalMultiplesCtr;

    // current count of coordinates to be added to the database
    private int loadedCtr;
    // total processing time for the load
    private double totalProcessTime;

    // coordinate processor
    private CoordinateInputProcessor coordProcessor;

    //  cache of seqids of sequence records we have already processed
    private HashSet coordIdsAlreadyProcessed;

    
    // true if we are going to load multiple coordinates per object
    private String processMultiples;

    // writer for all objects with multiple coordinates in the input
    private BufferedWriter multipleObjectWriter;

    // load mode - e.g. delete_reload or add
    private String loadMode;
    
    // get the collection key when in add mode
    private CoordMapCollectionKeyLookup collectionLookup;
    
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
		loadMode = loadCfg.getLoadMode();
        processMultiples = loadCfg.getCoordRepeatsOk();
        collectionLookup =  new CoordMapCollectionKeyLookup();
	
        // Create a DataInput File
        InputDataFile inData = new InputDataFile();

        // get an iterator for the InputDataFile with a configured interpreter
        iterator = inData.getIterator(
                (RecordDataInterpreter)loadCfg.getInterpreterClass());

        if (processMultiples.equals("false")) {
	    // write objects with > 1 input coordinate to a QC file rather than create
	    // multiple coordinates
            try {
                multipleObjectWriter = new BufferedWriter(new FileWriter(loadCfg.
                    getRepeatFileName()));
            }
            catch (IOException e) {
                throw new MGIException(e.getMessage());
            }
        }

        totalProcessedCtr = 0;
	totalLoadedCtr = 0;
	totalMultiplesCtr = 0;
	
        // create a CoordinateInputProcessor
        coordProcessor = new CoordinateInputProcessor(loadStream);
	
	// throw exception if unsupported load mode
	if (!loadMode.equals(CoordloaderConstants.DELETE_RELOAD_MODE) &&
	    !loadMode.equals(CoordloaderConstants.ADD_LOAD_MODE)) {
	    // unsupported load mode, throw exception
	    throw new MGIException("Unsupported load mode: " + loadMode);
	} 
	// get collection key if add mode; create if it doesn't exist
	/*if (loadMode.equals(CoordloaderConstants.ADD_LOAD_MODE)){
	    Integer collectionKey = collectionLookup.lookup(
		loadCfg.getMapCollectionName());
	    
	    // will create collection if it is not in the database 
	    // i.e. collectionKey=null
	    coordProcessor.createCollection(collectionKey);
	   
	}*/
	    
        // create the set for storing coordinate ids we have already processed
        coordIdsAlreadyProcessed = new HashSet();

    }

    /**
     * deletes the collection, all coordinate maps and features for the
     * collection and creates a new collection object, if delete_reload mode
     * @effects deletes collection, map, and feature objects from a database 
     * @throws MGIException if errors occur while deleting
     */

    public void preprocess() throws MGIException {
	
	if (loadMode.equals(CoordloaderConstants.DELETE_RELOAD_MODE)) {
	    // delete collection, maps, features (coordinates)
        coordProcessor.deleteCoordinates();
	    // create new collection
	    coordProcessor.createCollection(null);
	}
	// get collection key if add mode; create if it doesn't exist
	if (loadMode.equals(CoordloaderConstants.ADD_LOAD_MODE)){
	    Integer collectionKey = collectionLookup.lookup(
		loadCfg.getMapCollectionName());
	    
	    // will create collection if it is not in the database 
	    // i.e. collectionKey=null
	    coordProcessor.createCollection(collectionKey);
	   
	}
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

       logger.logdInfo("CoordLoader running", true);

       // Timing the load
       Stopwatch loadStopWatch = new Stopwatch();
       loadStopWatch.start();

       // Data object representing the raw values of the current input record
       CoordinateInput input;

       // iterate thru the records and process them
       while(iterator.hasNext()) {
		   totalProcessedCtr++;
		   if (totalProcessedCtr  > 0 && totalProcessedCtr % 100 == 0) {
			   logger.logdInfo("Processed " + totalProcessedCtr + " input records", false);
		   }
		   // get the next CoordinateInput object
		   input = (CoordinateInput)iterator.next();
		   String currentObjectID = input.getCoordMapFeatureRawAttributes().
					   getObjectId();
		   logger.logdDebug(currentObjectID, false);

		   // if we are not loading multiple coordinates per object, write
		   // them out to a file
		   if (processMultiples.equals("false")) {
			   try {
				   // determine if we've already processed this object
				   if (coordIdsAlreadyProcessed.contains(currentObjectID)) {
					   // we have an object with multiple coordinates;
			   // count it, write it out, go on to next record in input
					   totalMultiplesCtr++;
					   multipleObjectWriter.write(input.
											 getCoordMapFeatureRawAttributes().
											 getRecord() + "\n");
					   logger.logdDebug("Object has multiple coordinates/re: " + currentObjectID);
					   continue;
				   }
				   else {
					   // add the coordinate id to the set we have processed
					   coordIdsAlreadyProcessed.add(currentObjectID);
				   }
			   }
			   catch (IOException e) {
				   throw new MGIException(e.getMessage());
			   }
		   }
		   try {
			   coordProcessor.processInput(input);
			   totalLoadedCtr++;
		   } catch (CoordInDatabaseException e) {
			logger.logcInfo("Coordinate already in database for object: " +
				currentObjectID, false);
		   }
       }
       loadStopWatch.stop();
       totalProcessTime = loadStopWatch.time();
       
    }


    /**
     * closes the load SQLStreams, closes multiple coordinate writer.
     * Reports load statistics.
     * @throws MGIException
     */
    public void postprocess() throws MGIException
    {
        logger.logdInfo("CoordLoader beginning post process", true);
	
	// execute bcp
	logger.logdInfo("Closing load stream", false);
        this.loadStream.close();
        if (processMultiples.equals("false")) {
            // close the multiple coordinate writer
            logger.logdInfo("Closing multiple coordinate writer", false);
            try {
                multipleObjectWriter.close();
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
    * of objects with multiple coordinates in the input. 
    */
    private void reportLoadStatistics() {
        String message = "Total Load time in minutes: " +
            (totalProcessTime/60);
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        message = "Total Coordinates in Input = " + totalProcessedCtr;
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);
	message = "Total Coordinates Loaded = " + totalLoadedCtr;
	logger.logdInfo(message, false);
        logger.logpInfo(message, false);
	// if we are not loading multiple coordinates per object, report multi
	if (processMultiples.equals("false")) {
	    message = "Load Configured to load only one coordinate per object." +
		"\nTotal Coordinates written to multiple coordinate file: "
				+ totalMultiplesCtr;
	    logger.logdInfo(message, false);
	    logger.logpInfo(message, false);
	}
        
    }
}
