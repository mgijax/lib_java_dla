package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.dla.loader.DLALoader;
import org.jax.mgi.shr.timing.Stopwatch;
import org.jax.mgi.shr.config.SeqDeleterCfg;
import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.dbs.mgd.loads.Seq.SeqDeleterProcessor;
import org.jax.mgi.shr.dla.input.refseq.RefSeqDeleterInterpreter;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * a base class which extend DLALoader and implements the DLALoader methods
 * 'initialize', 'preprocess', 'run', and 'postprocess' to accomplish statusing
 * Sequence objects as deleted
 * @has s<br>
 * <UL>
 *   <LI>A DataIterator for iterating over an input file -  seenote below.
 *   <LI>A SeqDeleterProcessor for processing the deletes
 * </UL>
 *
 * @does performs initialization of objects for sequence deleters, and
 *       statuses Sequences as deleted. Keeps count of repeated seqids in
 *       the input and writes them out to a file.
 * @notes assumes it is iterating over a file; could subclass to set a
 *       different kind of iterator e.g. a RowDataIterator over a ResultSet.
 * @author sc
 * @version 1.0
 */

public class SeqDeleter extends DLALoader {

    // iterator over an input file
    protected DataIterator iterator;

    // provides access to Configuration values
    protected SeqDeleterCfg loadCfg;

    // current number of delete records looked at (not all are in in MGI)
    int recordCtr;

    // total processing time for the load
    double totalProcessTime;

    // processes deletes
    private SeqDeleterProcessor delProcessor;

    // Are we deleting refseqs?
    private boolean isRefSeq = false;

    /**
     * Initializes instance variables
     * @throws MGIException if errors occur during initialization
     */
    public void initialize() throws MGIException {
        loadCfg = new SeqDeleterCfg();

        // Create a DataInput File
        InputDataFile inData = new InputDataFile();
	// create interpreter - what type is it?
        RecordDataInterpreter interpreter = (RecordDataInterpreter)loadCfg.getInterpreterClass();
        if (interpreter instanceof RefSeqDeleterInterpreter) {
		isRefSeq = true;
	}
        // get an iterator for the InputDataFile with a configured interpreter
        iterator = inData.getIterator(interpreter);

        // init count of total records looked at
        recordCtr = 0;

        // create the delete processor
        delProcessor = new SeqDeleterProcessor(loadStream);

    }

    /**
     * Not implemented.
     * @throws MGIException
     */

    public void preprocess() throws MGIException {

    }

    /**
     * Gets records from input file, determines if they should be statused as
     * deleted. Updates Sequence status to deleted.
     * @effects database records updated.
     * @throws MGIException thrown if an error occurs while processing updates
     */
    public void run()  throws MGIException {

       logger.logdInfo("SeqDeleter running", true);

       // Timing the load
       Stopwatch loadStopWatch = new Stopwatch();
       loadStopWatch.start();

       // Data object representing the raw values of the current input record
       String toDelete;
       String firstToDelete;
       String secondToDelete;

       // iterate thru the records and process them
       while(iterator.hasNext()) {
	   recordCtr++;
           // get the next seqid(s)
           toDelete = (String)iterator.next();
           firstToDelete = null;
           secondToDelete = null;
           if (isRefSeq == true) {
               ArrayList tokens = StringLib.split(toDelete, "/");
               firstToDelete = (String)tokens.get(0);
               if (tokens.size() == 2) {
                   secondToDelete = (String)tokens.get(1);
               }
           }
           else {
               firstToDelete = toDelete;
           }

       // process the delete and count it
       delProcessor.processDelete(firstToDelete);
       if (secondToDelete != null) {
           // process the second delete and count it
           delProcessor.processDelete(secondToDelete);
	   }
       // report every 100 sequences looked at
       if (recordCtr  > 0 && recordCtr % 100 == 0) {
               logger.logdInfo("Looked at " + recordCtr +
                               " delete records", false);
           }
       }

       // process the last batch
       logger.logdDebug("Processing last batch");
       delProcessor.finishDeleteBatch();

       loadStopWatch.stop();
       totalProcessTime = loadStopWatch.time();
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
        logger.logdInfo("SeqDeleter beginning post process", true);
        logger.logdInfo("Closing load stream", false);
        this.loadStream.close();

        reportLoadStatistics();
        logger.logdInfo("SeqDeleter complete", true);
    }

    /**
    * Reports load statistics; load time, # delete records looked at, #
    * repeated deletes in the input, # actual deletes, etc
    */
    private void reportLoadStatistics() {
        String message = "Total Load Time in Minutes: " +
            (totalProcessTime/60);
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        // report the total deletes looked at (not all are in MGI)
        message = "Total Delete Records Looked at = " + recordCtr + "(RefSeq deletes are two per record - 1 each nucleotide and protein)";
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        logger.logdInfo("MGI Delete Counts: ", false);
        Vector deleteReports = delProcessor.getProcessedReport();
        for(Iterator i = deleteReports.iterator(); i.hasNext();) {
            String line = (String)i.next();
            logger.logpInfo(line, false);
            logger.logdInfo(line, false);
        }
    }
}
