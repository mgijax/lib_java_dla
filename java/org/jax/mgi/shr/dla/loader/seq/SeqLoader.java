package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.dla.loader.DLALoader;
import org.jax.mgi.shr.dla.loader.DLALoaderException;
import org.jax.mgi.shr.timing.Stopwatch;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.dbutils.ScriptWriter;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.dbutils.dao.BCP_Stream;
import org.jax.mgi.shr.dbutils.Table;
import org.jax.mgi.shr.config.ScriptWriterCfg;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSException;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.UnresolvedAttributeException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.shr.cache.CacheConstants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashSet;
import org.jax.mgi.shr.dla.input.OrganismChecker;
import org.jax.mgi.shr.dla.input.SequenceInput;
import org.jax.mgi.dbs.mgd.loads.Seq.SequenceInputProcessor;
import org.jax.mgi.dbs.mgd.loads.Seq.SequenceAttributeResolver;
import org.jax.mgi.dbs.mgd.loads.Seq.SequenceResolverException;
import org.jax.mgi.dbs.rdr.qc.SeqQCReporter;
import org.jax.mgi.dbs.mgd.loads.Seq.*;

/**
 * A base class which extend DLALoader and implements the DLALoader methods
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
 *   <LI>getDataIterator - to create an iterator for its input file with
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

public abstract class SeqLoader extends DLALoader {
    /**
    * must be defined and set by subclass by implementing the
    * getDataIterator method
    */

    // iterator over an input file
    protected DataIterator iterator;

    // optional organism checker for reporting statistics by organism
    protected OrganismChecker organismChecker;

    /**
    * initialized in initialize method
    */

    // Processes SEQ_Sequence attributes
    protected SequenceInputProcessor seqProcessor;

    // provides access to Configuration values
    protected SequenceLoadCfg loadCfg;

    // load mode e.g. incremental_initial, incremental, delete_reload
    private String loadMode;

    // handles all QC reporting to RADAR database
    private SeqQCReporter qcReporter;

    // Exceptions for sequence loads
    private SeqloaderExceptionFactory seqEFactory;

    // Resolves SEQ_Sequence attributes
    private SequenceAttributeResolver seqResolver;

    //  cache of seqids we have already processed
    private HashSet seqIdsAlreadyProcessed;

    // sequence provider
    private String seqProvider;

    // count of sequence records whose seqids we have already processed
    private int seqIdsAlreadyProcessedCtr;

    // the number of valid sequences WITHOUT processing errors
    int processedSeqCtr;

    // the number of valid sequences WITH processing errors
    int errCtr;

    // total processing time for the load
    double totalProcessTime;

    /**
     * initialized if we are in incremental mode
     */
    // writes calls to merge and split stored procedures to a script file
    private ScriptWriter mergeSplitWriter;

    // seqids in MGI for a given logical db
    private AccessionLookup seqIdLookup;

    // handles determining and processing merges and splits
    private MergeSplitProcessor mergeSplitProcessor;

    // writer for all repeated input sequences
    private BufferedWriter repeatSeqWriter;

    /**
     * Initializes instance variables depending on load mode
     * @assumes RecordDataIterator is set by the subclass
     * @effects instance variables will be instantiated
     * @throws MGIException if errors occur during initialization
     * @throws RuntimeException if RecordDataIterator is not set after
     *   call to getDataIterator
     */
    protected void initialize() throws MGIException {
        loadCfg = new SequenceLoadCfg();
        loadMode = loadCfg.getLoadMode();

        // call subclass method to get an iterator for the input file
        getDataIterator();
        if (iterator == null ) {
            throw new RuntimeException("You must set the RecordDataIterator " +
                "in your subclass!!");
        }
        // handles all qc reporting for a sequence load
        qcReporter = new SeqQCReporter(qcStream);

        // create Factory to get seqloader specific exceptions
        seqEFactory = new SeqloaderExceptionFactory();

        // get the provider of the sequences
        seqProvider =  loadCfg.getProvider();
        System.out.println("seqProvider: " + seqProvider);

        // create resolver to resolvea SequenceRawAttribute object to a SEQ_SequenceState
        seqResolver = new SequenceAttributeResolver();
        
        // create the set for storing seqids we have already processed
        seqIdsAlreadyProcessed = new HashSet();

        // count of sequence records whose seqids we have already processed
        seqIdsAlreadyProcessedCtr = 0;

        // number of valid sequences WITHOUT processing errors:
        processedSeqCtr = 0;

        // number of valid sequences WITH processing errors
        errCtr = 0;

        // writes repeated input sequences to a file
        try {
            repeatSeqWriter = new BufferedWriter(new FileWriter(loadCfg.
                getRepeatFileName()));
        }
        catch (IOException e) {
            SeqloaderException e1 =
                (SeqloaderException) seqEFactory.getException(
                SeqloaderExceptionFactory.RepeatFileIOException, e);
            throw e1;
        }

	logger.logdInfo("Preprocessing load\n", true);

	// Build a vector that contains a Table object for each table to be
	// written to in the "load" database.
	//
	Vector loadTables = new Vector();
        loadTables.add(Table.getInstance("SEQ_Sequence", loadDBMgr));
        loadTables.add(Table.getInstance("SEQ_Sequence_Raw", loadDBMgr));
        loadTables.add(Table.getInstance("PRB_Source", loadDBMgr));
        loadTables.add(Table.getInstance("SEQ_Source_Assoc", loadDBMgr));
        loadTables.add(Table.getInstance("ACC_Accession", loadDBMgr));

	// Initialize writers for each table if a BCP stream if being used.
	//                                                             
	if (loadStream.isBCP())
	    ((BCP_Stream)loadStream).initBCPWriters(loadTables);
	logger.logdInfo("Finished preprocessing load\n", true);

        // init objects needed to process in incremental mode
        if (loadMode.equals(SeqloaderConstants.INCREM_LOAD_MODE)) {

            // writes merges and splits to a script file; config var is MGD prefixed
            mergeSplitWriter = new ScriptWriter(new ScriptWriterCfg("MGD"), loadDBMgr);

            LogicalDBLookup lookup = new LogicalDBLookup();
            int logicalDBKey = lookup.lookup(loadCfg.getLogicalDB()).intValue();
            boolean useFullCache =
                loadCfg.getUseAssocClonesFullCache().booleanValue();
            if (useFullCache)
                seqIdLookup = new AccessionLookup(logicalDBKey,
                                       MGITypeConstants.SEQUENCE,
                                       AccessionLib.PREFERRED);
            else
                seqIdLookup = new AccessionLookup(logicalDBKey,
                                       MGITypeConstants.SEQUENCE,
                                       AccessionLib.PREFERRED,
                                       CacheConstants.LAZY_CACHE);

            // lookup of all accession ids in the database for this
            // sequence load's logicalDB
           /* seqidLookup = new AccessionLookup(lookup.lookup(
                loadCfg.getLogicalDB()).intValue(),
                MGITypeConstants.SEQUENCE,
                AccessionLib.PREFERRED);
*/
            // determines and processes merges and splits
            mergeSplitProcessor = new MergeSplitProcessor(seqIdLookup, qcReporter);

            // a SeqProcessor that handles detection and processing of events
            seqProcessor = new IncremSequenceInputProcessor(loadStream,
                   qcStream,
                   qcReporter,
                   seqResolver,
                   mergeSplitProcessor,
                   seqIdLookup);
        }
        // create SeqProcessor that can do deletes and process add events only
        else if (loadMode.equals(SeqloaderConstants.INCREM_INITIAL_LOAD_MODE) ||
            loadMode.equals(SeqloaderConstants.DELETE_RELOAD_MODE)) {
            seqProcessor = new SequenceInputProcessor(loadStream,
                    qcStream,
                    seqResolver);
            // if in delete/reload mode, delete sequences
            if (loadMode.equals(SeqloaderConstants.DELETE_RELOAD_MODE)) {
                seqProcessor.deleteSequences();
            }
       }
    }

    /**
     * to perform a database load into the RADAR and/or MGD
     * database
     * @assumes nothing
     * @effects database records created within the RADAR and/or MGD
     * database. If stream is a BCP stream, creates bcp files which may be
     * temporary or persistent depending on configuration
     * @throws MGIException throw if a fatal error occurs while performing the
     * load.
     */
    protected void run()  throws MGIException {
        // throw an exception if subclass hasn't set the RecordDataIterator
       if (iterator == null) {
           throw new RuntimeException("RecordDataIterator not defined!");
       }
       // report the load mode we are running under
       logger.logdInfo("SeqLoader running in " + loadMode + " mode", true);

       // Timing the load
       Stopwatch loadStopWatch = new Stopwatch();
       loadStopWatch.start();

       // Data object representing the raw values of the current input record
       SequenceInput si;

       // iterate thru the records and process them
       while(iterator.hasNext()) {
           try {
               si = (SequenceInput)
                   iterator.next();
               String currentSeqid = si.getPrimaryAcc().getAccID();

               // for NCBI Gene Model sequences we want to bypass the skipping of
               // repeated sequences because, for PAR, two NCBI Gene Models can 
               // share the same ID - there is one model on the X and on the Y
               
               if(!seqProvider.equals("NCBI Gene Model")) { 
                   if (seqIdsAlreadyProcessed.contains(currentSeqid)) {
                       // we have a repeated sequence; count it, write it out,
                       // go on to next sequence in the input
                       seqIdsAlreadyProcessedCtr++;
                       repeatSeqWriter.write(si.getSeq().getRecord() + SeqloaderConstants.CRT);
                       logger.logdDebug("Repeat Sequence: " + currentSeqid);
                       continue;
                   }
                   else {
                       // add the seqid to the set we have processed
                       seqIdsAlreadyProcessed.add(currentSeqid);
                   }
               } 
           }
           catch (IOException e) {
               throw new MGIException(e.getMessage());
           }
           catch (MGIException e) {
               if (e.getParent().getClass().getName().equals("org.jax.mgi.shr.ioutils.RecordFormatException")) {
                 logger.logdErr(e.getMessage());
                 logger.logcInfo(e.getMessage(), true);
                 errCtr++;
                 continue;
               }
               else {
                   throw e;
               }
           }
           try {
               seqProcessor.processInput(si);
           }

           // If incoming raw organism != existing raw organism -
           // qcReporter handles reporting
           catch (ChangedOrganismException e) {
               String message = e.getMessage() + " Sequence: " +
                   si.getPrimaryAcc().getAccID();
               logger.logdDebug(message, true);
               processedSeqCtr++;
               errCtr++;
               continue;
           }

           // if we can't resolve SEQ_Sequence attributes,  log to curation log
           // go to the next sequence
           catch (SequenceResolverException e) {
               String message = e.getMessage() + " Sequence: " +
                   si.getPrimaryAcc().getAccID();
               logger.logdDebug(message, true);
               logger.logcInfo(message, true);
               processedSeqCtr++;
               errCtr++;
               continue;
           }
           // UnresolvedAttributeException is thrown for those loaders that
           // must resolve *all* source attributes or fail.
           // For other loads if we can't resolve the source for a sequence,
           // log to curation log and go to the next sequence
           catch (MSException e) {
               if (e instanceof UnresolvedAttributeException) {
                   throw new MGIException(e.getMessage(), true);
               }
               String message = e.getMessage() + " Sequence: " +
                   si.getPrimaryAcc().getAccID();
               logger.logdInfo(message, true);
               logger.logcInfo(message, true);
               processedSeqCtr++;
               errCtr++;
               continue;
           }
           processedSeqCtr++;
           int seqCtr = processedSeqCtr;
           if (seqCtr  > 0 && seqCtr % 100 == 0) {
               logger.logdInfo("Processed " + seqCtr + " sequences", false);
           }
       }

       // special handling for Incremental mode
       if (loadMode.equals(SeqloaderConstants.INCREM_LOAD_MODE)) {
           // process the last batch
           ( (IncremSequenceInputProcessor) seqProcessor).finishUpdateBatch();
           // any update errors to errCtr
           errCtr += ( (IncremSequenceInputProcessor) seqProcessor).getCurrentExistingSeqErrCtr();
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
    protected void postprocess() throws MGIException
    {
        logger.logdInfo("SeqLoader beginning post process", true);
        logger.logdInfo("Closing load stream", false);
        this.loadStream.close();
        if (loadMode.equals(SeqloaderConstants.INCREM_LOAD_MODE)) {
            logger.logdInfo("Processing Merge/Splits", false);
            this.mergeSplitProcessor.process(mergeSplitWriter);
            mergeSplitWriter.execute();
        }

        // close the repeat sequence writer
        logger.logdInfo("Closing repeat sequence writer", false);
        try {
            repeatSeqWriter.close();
        }

        catch (IOException e) {
            SeqloaderException e1 =
                (SeqloaderException) seqEFactory.getException(
                SeqloaderExceptionFactory.RepeatFileIOException, e);
            throw e1;
        }

        // close the qc reporting stream after all qc reporting done - Note that
        // mergeSplitProcessor does qc reporting
        logger.logdInfo("Closing qc stream", false);
        this.qcStream.close();
        reportLoadStatistics();
        appPostProcess();
        logger.logdInfo("SeqLoader complete", true);
    }

    /**
     * subclasses implement this method to create an iterator over their particular
     * Input Data and set the optional OrganismChecker
     * @assumes nothing
     * @effects nothing
     * @throws MGIException
     */
    abstract protected void getDataIterator() throws MGIException;

    /**
    * subclasses implement this method to add application specific post processing
    * InputDataFile.
    * @assumes nothing
    * @effects nothing
    * @throws MGIException
    */
    abstract protected void appPostProcess() throws MGIException;

    /**
    * Reports load statistics; event counts, organism counts, valid sequence count
    * etc.
    * @assumes nothing
    * @effects nothing
    * @throws Nothing
    */
    private void reportLoadStatistics() {
        int totalValidSeqs = processedSeqCtr;
        String message = "Total Load time in minutes: " +
            (totalProcessTime/60);
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        message = "Total Valid Sequences Processed = " + processedSeqCtr;
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        message = "Total Valid Sequences Skipped because of errors - " +
            errCtr + " See QC reports and curation log";
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        // Report number of repeated sequences found
        logger.logdInfo("Total Repeat Sequences written to repeat file: " + seqIdsAlreadyProcessedCtr, false);
        logger.logpInfo("Total Repeat Sequences written to repeat file: " + seqIdsAlreadyProcessedCtr, false);

        // following logged in debug mode only
        if (totalValidSeqs > 0) {
            logger.logdDebug(
                "Average Processing Time/Valid Sequence processed = " +
                (totalProcessTime / totalValidSeqs), false);
            // report MSProcessor execution times
            logger.logdDebug("Average MSProcessor time = " +
                             (seqProcessor.runningMSPTime / totalValidSeqs), false);
            logger.logdDebug("Greatest MSProcessor time = " +
                             seqProcessor.highMSPTime, false);
            logger.logdDebug("Least MSProcessor time = " +
                             seqProcessor.lowMSPTime, false);
        }
            // special handling for Incremental mode
            if (loadMode.equals(SeqloaderConstants.INCREM_LOAD_MODE)) {
                logger.logdDebug("Total SequenceLookup time = " +
                                 ( (IncremSequenceInputProcessor) seqProcessor).
                                 getCurrentSequenceLookupTime(), false);
                logger.logdDebug(
                    "Average SequenceLookup time per Sequence in MGI = " +
                    ( (IncremSequenceInputProcessor) seqProcessor).
                    getCurrentAverageSequenceLookupTime(), false);
                logger.logdDebug("Greatest SequenceLookup time = " +
                                 ( (IncremSequenceInputProcessor) seqProcessor).
                                 getCurrentHighSequenceLookupTime(), false);
                logger.logdDebug("Least SequenceLookup time = " +
                                 ( (IncremSequenceInputProcessor) seqProcessor).
                                 getCurrentLowSequenceLookupTime(), false);
            }

        // Report OrganismChecker counts
        logger.logdInfo("\n\nValid Sequences Processed by Organism (includes repeated sequences): ", false);
        if(organismChecker != null) {
            Vector deciderCts = organismChecker.getDeciderCounts();
            for (Iterator i = deciderCts.iterator(); i.hasNext(); ) {
                String line = (String) i.next();
                logger.logdInfo(line, false);
            }
        }

        // report Event counts for sequences processed - Note that all
        // Merge and Split events are also other events. e.g. if two sequences
        // are merged into one new sequence than there will be an add
        // event and two merge events. If a sequence is merged into an existing
        // sequence then there will be an update event and one merge event.
        // if a sequence is split into two new sequences then there will be two
        // add events and one split event.
        logger.logdInfo("Event count (merge and split events are also add or update events): ", false);
        Vector eventReports = seqProcessor.getProcessedReport();
        for(Iterator i = eventReports.iterator(); i.hasNext();) {
            String line = (String)i.next();
            logger.logpInfo(line, false);
            logger.logdInfo(line, false);
        }
    }
}

