package org.jax.mgi.dbs.mgd.loads.Seq;


import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.config.SeqDeleterCfg;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderExceptionFactory;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.lookup.VocabTermLookup;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

/**
 * an object that given a seqid, statuses a SEQ_Sequence as deleted if that
 * seqid is its primary seqid in MGI
 * @has
 *   <UL>
 *   <LI>a logger
 *   <LI>AccessionLookup to determine if the seqid is primary in MGI
 *   <LI>SequenceLookup to get the Sequence to update.
 *   <LI>A stream to process the updates
 *   </UL>
 * @does
 *   <UL>
 *   <LI>given a seqid, statuses its SEQ_Sequence objectsas deleted in MGI if it
 *       is a primary seqid
 *   <LI>counts sequences statused as deleted
 *   <LI>counts sequences not statused as deleted because currently statused
 *       as split or deleted.
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqDeleterProcessor {

    // a stream for handling MGD DAO objects
    protected SQLStream mgdStream;

    // logger for the load
    protected DLALogger logger;

    // get a sequence load configurator
    protected SeqDeleterCfg config;

    // lookup a seqid in MGI ( to get a _Sequence_key)
    private AccessionLookup seqIdLookup;

    // Lookup for LogicalDB key
    LogicalDBLookup logicalDBLookup;

    // logicalDB_key for the load
    private int logicalDBKey;

    // lookup a Sequence in MGI ( to get a Sequence object)
    private SequenceLookup seqLookup;

    // to lookup sequence status by key
    private VocabTermLookup termNameLookup;

   // lookup sequence status by term
   private VocabKeyLookup statusKeyLookup;

    // a cache of SeqInput object with seqid keys that have been determined
     // to be in MGI (possible updates)
     private HashMap batchMap;

     // status key for delete status
     private Integer deleteStatus;

     private HashMap repeatMap;
    // The following are public to provide easy access for debug logging
    // the number of seqids for the SequenceLookup to query for at one time
    public int batchSize;

    // current number of sequences in the "In MGI" batch
    // this will always be <= batchSize
    private int batchCtr;

    // exception factory for seqloader exceptions
    protected SeqloaderExceptionFactory eFactory;

    // current number of sequences deleted
    int deleteCtr = 0;
    // current number of sequences not deleted because their status is 'split'
    int notDelCtr = 0;
    
    /**
     * Constructs a SeqProcessor that adds and deletes sequence to/from
     * a database
     * @effects querie a database
     * @param mgdSqlStream stream for updating Sequences in MGI
     * @throws CacheException if error creating a SEQRefAssocProcessor or a
     *      AccAttributeResolver
     * @throws DBException if error creating a SEQRefAssocProcessor or a
     *      AccAttributeResolver
     * @throws ConfigException if error creating a SEQRefAssocProcessor or a
     *      AccAttributeResolver or error creating a SequenceLoadCfg or
     *         reading config file
     * @throws KeyNotFoundException
     * @throws TranslationException
     * @throws DLALoggingException if error creating a logger
     */

    public SeqDeleterProcessor(SQLStream mgdSqlStream)
        throws CacheException, DBException, ConfigException,
        KeyNotFoundException, TranslationException, DLALoggingException {

        mgdStream = mgdSqlStream;
        // create an exception factory
        eFactory = new SeqloaderExceptionFactory();
        // create configurator and get the logicalDB key
        config = new SeqDeleterCfg();
        logicalDBKey = new LogicalDBLookup().lookup(config.getLogicalDB()).
            intValue();
        // get an instance of the logger
        logger = DLALogger.getInstance();
        // create an accession lookup for this logical DB
        seqIdLookup = new AccessionLookup(logicalDBKey,
                                          MGITypeConstants.SEQUENCE,
                                          AccessionLib.PREFERRED);
        // get the configured batch size for the SequenceLookup
        batchSize = new Integer(config.getQueryBatchSize()).intValue();

        // get object to lookup a Sequence in MGI
        seqLookup = new SequenceLookup(mgdStream, batchSize);
        // create a lookup to get the delete status key
        statusKeyLookup = new VocabKeyLookup(VocabularyTypeConstants.SEQUENCESTATUS);
        // lookup to get the delete status key
        deleteStatus = statusKeyLookup.lookup(SeqloaderConstants.DELETE_STATUS);
        // lookup to get the status name given a status key
        termNameLookup = new VocabTermLookup();
        // batch the seqids to query
        batchMap = new HashMap();
	repeatMap = new HashMap();
        // current count of sequences in the batch
        batchCtr = 0;
    }

   /**
   * Determine if a sequence should be statused as deleted and update sequence
   * status in MGI
   * @effects queries and updates a database
   * @param seqIdToDelete seqid of sequence to status as deleted
   * @throws SeqloaderException
   * @throws CacheException if error using lookups
   * @throws DBException if error using lookup or updating a sequence
   */

   public void processDelete(String seqIdToDelete) throws SeqloaderException,
       CacheException, DBException {
       // must declare outside try block
       Integer seqKey;
       // do quick lookup to see if the primary is in MGI
       try {
           seqKey = seqIdLookup.lookup(seqIdToDelete);
       }
       catch (MGIException e) {
           SeqloaderException e1 =
               (SeqloaderException) eFactory.getException(
               SeqloaderExceptionFactory.SeqKeyQueryErr, e);
           e1.bind(seqIdToDelete);
           throw e1;
       }
       // seqid is in MGI, add seqId to the batch and process the batch
       // when it reaches the configured batch size
       if (seqKey != null) {
           // add the seqId to the batch
	   if (!batchMap.keySet().contains(seqIdToDelete)) {
	       batchMap.put(seqIdToDelete, seqIdToDelete);
	       batchCtr++;
	   }
	   else {
	       if (!repeatMap.keySet().contains(seqIdToDelete)) {
		   repeatMap.put(seqIdToDelete, new Integer(2));
	       }
	       else {
		   Integer count = (Integer) repeatMap.get(seqIdToDelete);
		   int i = count.intValue();
		   i++;
		   repeatMap.put(seqIdToDelete, new Integer(i));
	       }
	   }
           // process the batch
           if (batchCtr == batchSize) {
               processDeleteBatch();
               // reset the batch counter and the batch map
               batchCtr = 0;
               batchMap = new HashMap();
           }
       }
   }
       /**
        * processes the last batch
        * @assumes nothing
        * @effects queries and updates a database
        * @throws SeqloaderException if error using seqLookup, detecting or
        * processing an event
        * @throws SeqloaderException
        * @throws CacheException if error using lookups
        * @throws DBException if error using lookup or updating a sequence
        */
       public void finishDeleteBatch() throws SeqloaderException, CacheException,
          DBException {
            if(batchMap.size() > 0 ) {
                try {
                    processDeleteBatch();
                }
                catch (SeqloaderException e) {
                     SeqloaderException e1 =
                         (SeqloaderException) eFactory.getException(
                         SeqloaderExceptionFactory.ProcessUpdateErr, e);
                     throw e1;
                 }
            }
        }

      /**
       * Processes a batch of seqids, determining whether to status them as delete
       * @effects Writes updates to an sql script or batch, or performs inline updates,
       * depending on the type of stream
       * @throws SeqloaderException if error using seqLookup, detecting or
       * processing an event
       * @throws CacheException if error using lookups
       * @throws DBException if error using lookup or updating a sequence
       */

      private void processDeleteBatch() throws  SeqloaderException,
          CacheException, DBException  {
          // get the set of seqids to pass to the SequenceLookup
          Set seqIdSet = batchMap.keySet();
          Vector sequences;
          // get Sequence objects for the batch
          try {
              sequences = seqLookup.findBySeqId(seqIdSet, logicalDBKey);
          }
          catch (MGIException e) {
              SeqloaderException e1 =
                  (SeqloaderException) eFactory.getException(
                  SeqloaderExceptionFactory.SeqQueryErr, e);
              e1.bind(seqIdSet.toString());
              throw e1;
          }
          // iterate thru the Sequence objects processing deletes
          for (Iterator i = sequences.iterator(); i.hasNext(); ) {
              Sequence s = (Sequence) i.next();
              processDelete(s);
          }
      }

      /**
        * Status a Sequence as deleted if it is not already deleted or statused
        * as split
        * @effects Writes updates to an sql script or batch, or performs inline updates,
        * depending on the type of stream
        * @throws CacheException if error using lookups
        * @throws DBException if error using lookup or statusing the sequence as deleted
        */

      private void processDelete(Sequence s) throws DBException, CacheException {
          String statusString = termNameLookup.lookup(s.getSequenceState().getSequenceStatusKey());
          // don't update split, delete, or dummy status - SequenceUpdater has this logic, but
          // I want to count the number of sequences actually deleted.
          if ( ! (statusString.equals(SeqloaderConstants.SPLIT_STATUS) ||
                                     statusString.equals(SeqloaderConstants.DELETE_STATUS)
                                     || statusString.equals(SeqloaderConstants.DUMMY_SEQ_STATUS))) {
                  // increment the delete counter
                  deleteCtr++;
                  // get a copy of the sequence state
                  SEQ_SequenceState newState = s.getSequenceState();
                  // set the new status
                  newState.setSequenceStatusKey(deleteStatus);
                  // set new state in Sequence
                  s.updateSequenceState(newState);
                  //log the seqid
                  logger.logcInfo("DELETED " + SeqloaderConstants.TAB +
                                  s.getAccPrimary().getAccID(), false);
                  // write out the update
                  s.sendToStream();
         }
         else {
             // increment the split/del/notloaded counter
             notDelCtr++;
         }
      }
   /**
   * Gets a Vector containing a String reporting count of Sequences deleted
   * and sequences not deleted because statused as split
   * @assumes nothing
   * @effects nothing
   * @return Vector containing single string for reporting delete statistics
   */
   public Vector getProcessedReport() {
      logger.logcInfo("Total Deleted: " + deleteCtr, false);
       Vector report = new Vector();
       report.add("Total sequences deleted: " + deleteCtr);
       report.add("Total sequences not deleted because statused as " +
                  "'split' 'not loaded' or 'deleted': " + notDelCtr);
       report.add("Reporting repeated sequences:");
       report.add("seqId\tcount");
       for (Iterator i = repeatMap.keySet().iterator();i.hasNext();) {
	   String id = (String)i.next();
	   report.add(id + "\t " + ((Integer)repeatMap.get(id)).toString());
       }
       return report;
   }
}
