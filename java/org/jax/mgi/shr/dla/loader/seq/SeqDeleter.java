//  $Header
//  $Name

package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.dla.loader.DLALoader;
import org.jax.mgi.shr.timing.Stopwatch;
import org.jax.mgi.shr.config.SeqDeleterCfg;
import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.dbs.mgd.loads.Seq.SeqDeleterProcessor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashSet;

/**
 * a base class which extend DLALoader and implements the DLALoader methods
 * 'initialize', 'preprocess', 'run', and 'postprocess' to accomplish statusing
 * Sequence objects as deleted
 * @has s<br>
 * <UL>
 *   <LI>A DataIterator for iterating over an input file -  seenote below.
 *   <LI>A SeqDeleterProcessor for processing the deletes
 *   <LI>A BufferedWriter for writing out repeated seqids
 * </UL>
 *
 * @does performs initialization of objects for sequence deleters, and
 *       statuses Sequences as deleted. Keeps count of repeated seqids in the input
 *       and writes them out to a file.
 * @note assumes it is iterating over a file; could subclass to set a different
 *       kind of iterator e.g. a RowDataIterator over a ResultSet.
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

    //  set of seqids of sequence records we have already processed
    private HashSet deletesAlreadyProcessed;

    // current count of seqids we have already processed
    private int deletesAlreadyLookedAtCtr;

    // writer for all seqids repeated in the input
    private BufferedWriter repeatSeqWriter;

    // processes deletes
    private SeqDeleterProcessor delProcessor;

    /**
     * Initializes instance variables
     * @throws MGIException if errors occur during initialization
     */
    public void initialize() throws MGIException {
        loadCfg = new SeqDeleterCfg();

        // Create a DataInput File
        InputDataFile inData = new InputDataFile();

        // get an iterator for the InputDataFile with a configured interpreter
        iterator = inData.getIterator(
                (RecordDataInterpreter)loadCfg.getInterpreterClass());

        // writes repeated seqids to a file
       try {
             repeatSeqWriter = new BufferedWriter(new FileWriter(loadCfg.
                 getRepeatFileName()));
        }
        catch (IOException e) {
             throw new MGIException(e.getMessage());
        }
        // init count of total records looked at
        recordCtr = 0;

        // create the delete processor
        delProcessor = new SeqDeleterProcessor(loadStream);

        // create the set for storing deletes  we have already processed
        deletesAlreadyProcessed = new HashSet();

         // initcount of seqids already processed
        deletesAlreadyLookedAtCtr = 0;
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
       String seqIdToDelete;

       // iterate thru the records and process them
       while(iterator.hasNext()) {
           // get the next seqid
           seqIdToDelete = (String)iterator.next();
           logger.logdDebug(seqIdToDelete, true);

           // determine if repeated delete
           try {
               if (deletesAlreadyProcessed.contains(seqIdToDelete)) {
                   // we have a repeated delete; count it, write it out,
                   // go on to next delete record in the input
                   deletesAlreadyLookedAtCtr++;
                   repeatSeqWriter.write(seqIdToDelete +"\n");
                   logger.logdDebug("Repeat Sequence: " + seqIdToDelete);
                   continue;
               }
               else {
                   // add the seqid to the set we have processed
                   deletesAlreadyProcessed.add(seqIdToDelete);
               }
           }
           catch (IOException e) {
               throw new MGIException(e.getMessage());
           }

           // process the delete and count it
           delProcessor.processDelete(seqIdToDelete);
           recordCtr++;
           // report every 100 sequences looked at
           if (recordCtr  > 0 && recordCtr % 100 == 0) {
               logger.logdInfo("Looked at " + recordCtr + " delete records", false);
           }
       }

       // process the last batch
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

        // close the repeat coordinate writer
        logger.logdInfo("Closing repeat delete writer", false);
        try {
            repeatSeqWriter.close();
        }
        catch (IOException e) {
            throw new MGIException(e.getMessage());
        }

        reportLoadStatistics();
        logger.logdInfo("SeqDeleter complete", true);
    }

    /**
    * Reports load statistics; load time, # delete records looked at, #
    * repeated deletes in the input, # actual deletes, etc
    */
    private void reportLoadStatistics() {
        String message = "Total Load time in minutes: " +
            (totalProcessTime/60);
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        // report the total deletes looked at (not all are in MGI)
        message = "Total Delete Records Looked at = " + recordCtr;
        logger.logdInfo(message, false);
        logger.logpInfo(message, false);

        logger.logdInfo("Total Repeated Deletes written to repeat file: "
                        + deletesAlreadyLookedAtCtr, false);
        logger.logpInfo("Total Repeated Deletes written to repeat file: "
                        + deletesAlreadyLookedAtCtr, false);

        logger.logdInfo("MGI delete counts: ", false);
        Vector deleteReports = delProcessor.getProcessedReport();
        for(Iterator i = deleteReports.iterator(); i.hasNext();) {
            String line = (String)i.next();
            logger.logpInfo(line, false);
            logger.logdInfo(line, false);
        }
    }
}
//  $Log

 /**************************************************************************
 *
 * Warranty Disclaimer and Copyright Notice
 *
 *  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
 *  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
 *  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
 *  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
 *
 *  This software and data are provided to enhance knowledge and encourage
 *  progress in the scientific community and are to be used only for research
 *  and educational purposes.  Any reproduction or use for commercial purpose
 *  is prohibited without the prior express written permission of The Jackson
 *  Laboratory.
 *
 * Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
 *
 * All Rights Reserved
 *
 **************************************************************************/
