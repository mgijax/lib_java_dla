/* Generated by Together */

package org.jax.mgi.shr.dla.seqloader;
/**
 * Debug stuff
 */
import org.jax.mgi.shr.timing.Stopwatch;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.shr.dla.DLALogger;
import org.jax.mgi.shr.dla.DLALoggingException;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.MolecularSource.MSProcessor;
import org.jax.mgi.dbs.mgd.MolecularSource.MSException;
import org.jax.mgi.dbs.mgd.MolecularSource.MSRawAttributes;
import org.jax.mgi.dbs.mgd.MolecularSource.MolecularSource;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.exception.MGIException;

import java.util.Vector;
import java.util.Iterator;

public class SeqProcessor implements ProcessSequenceInput  {
   /**
   * Debug stuff - public so I have easy access
   */
    protected Stopwatch stopWatch;
    public double runningLookupTime;
    public int sequenceCtr;
    public double highLookupTime;
    public double lowLookupTime;

    public double runningMSPTime;
    public double highMSPTime;
    public double lowMSPTime;

    // a stream for handling MGD DAO objects
    protected SQLStream mgdStream;

    // resolves GenBank sequence attributes to MGI values
    protected SequenceAttributeResolver seqResolver;

    // resolves accession attributes to MGI value
    protected AccAttributeResolver accResolver;

    // Resolves sequence reference attributes to MGI values
    protected SeqRefAssocProcessor refAssocProcessor;

    // logger for the load
    protected DLALogger logger;

    // resolves molecular source attributes for a sequence to MGI values
    protected MSProcessor msProcessor;

    // get a sequence load configurator
    protected SequenceLoadCfg config;

    // logicalDB_key for the load
    protected int logicalDBKey;

    // exception factory for seqloader exceptions
    protected SeqloaderExceptionFactory eFactory;

    /**
     * Constructs a SeqProcessor that adds and deleting sequence to/from
     * a database
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @throws CacheException
     * @throws DBException
     * @throws ConfigException
     * @throws MSException
     * @throws DLALoggingException
     * @throws KeyNotFoundException
     */

    public SeqProcessor(SQLStream mgdSqlStream,
                              SQLStream radarSqlStream,
                              SequenceAttributeResolver sar)
        throws CacheException, DBException, ConfigException, MSException,
               DLALoggingException, KeyNotFoundException {
      /**
      * Debug stuff
      */
      stopWatch = new Stopwatch();
      runningLookupTime = 0.0;
      highLookupTime = 0.0;
      lowLookupTime = 0.0;
      runningMSPTime = 0.0;
      highMSPTime = 0.0;
      lowMSPTime = 999.0;
      sequenceCtr = 0;

      mgdStream = mgdSqlStream;
      seqResolver = sar;

      // Create an Accession Attribute Resolver
       accResolver = new AccAttributeResolver();

       // Create a Reference Association Processor
       refAssocProcessor = new SeqRefAssocProcessor();
       logger = DLALogger.getInstance();

        // Create a Molecular Source Processor
       msProcessor = new MSProcessor (mgdSqlStream, radarSqlStream, logger);

       // configurator to lookup logicalDB
       config = new SequenceLoadCfg();
       logicalDBKey = new LogicalDBLookup().lookup(config.getLogicalDB()).intValue();
    }

    /**
    * deletes all Sequences from a given logical db from a database
    * @assumes Nothing
    * @effects deletes sequences from a database
    * @param None
    * @return nothing
    * @throws SeqloaderException if error getting SQLDataManager or
    */

    public void deleteSequences() throws SeqloaderException {

      String spCall = "SEQ_deleteByLogicalDB " + logicalDBKey;
      try {
        SQLDataManager sqlMgr = SQLDataManagerFactory.getShared(SchemaConstants.MGD);
        sqlMgr.executeSimpleProc(spCall);
      }
      catch (MGIException e) {
        SeqloaderException e1 =
            (SeqloaderException) eFactory.getException(
         SeqloaderExceptionFactory.CreateSequenceErr, e);
        throw e1;
      }
    }


   /**
   * Adds a sequence to the database
   * @assumes Nothing
   * @effects queries and inserts into a database
   * @param seqInput SequenceInput object - a set of raw attributes to resolve
   *        and add to the database
   * @return nothing
   * @throws SeqloaderException if there are configuration, cacheing, database,
   *         translation, or lookup errors. These errors cause load to fail
   * @throws RepeatSequenceException errors writing to repeat sequence file
   * @throws ChangedLibrary if raw library for existing sequence is different
   *         than for current sequence being processed. This exception is thrown
   *         by subclass
   * @throws ChangedOrganismException if raw organism for existing sequence is
   *         different than for current sequence being processed. This exception
   *         is thrown by subclass
   * @throws SequenceResolverException if errors resolving a sequence
   * @throws MSException is errors resolving a sequences source
   */

   public void processInput(SequenceInput seqInput)
       throws SeqloaderException, RepeatSequenceException,
          ChangedLibraryException, ChangedOrganismException,
          SequenceResolverException, MSException {

       SEQ_SequenceState inputSequenceState;
       // resolve raw sequence
       try {
         inputSequenceState = resolveRawSequence(seqInput.getSeq());
       }
       catch (MGIException e) {
         SeqloaderException e1 =
             (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.SeqResolverErr, e);
         throw e1;
       }


       // create the compound sequence; a sequence with its ref,
       // source association(s)and seqid(s)
       Sequence inputSequence;
       try {
         inputSequence = new Sequence(inputSequenceState, mgdStream);
       }
       catch (MGIException e) {
         SeqloaderException e1 =
             (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.CreateSequenceErr, e);
         throw e1;
       }


       // create MGI_AttributesHistory on the sequence type if stream is
       // using bcp
       if(mgdStream.isBCP()) {
           MGI_AttributeHistoryState typeHistoryState = new
               MGI_AttributeHistoryState();
       }

       // resolve primary accession attributes and set the accession state
       // in the Sequence
       try {
         inputSequence.setAccPrimary(
             accResolver.resolveAttributes(
             seqInput.getPrimaryAcc(), inputSequence.getSequenceKey()));
       }
       catch (MGIException e) {
         SeqloaderException e1 =
             (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.CreatePrimaryAccessionErr, e);
         throw e1;
       }

       logger.logdDebug("Add Event Primary: " +
                      ( (MSRawAttributes) seqInput.getMSources().get(0)).
                      getOrganism() +
                      " " + seqInput.getPrimaryAcc().getAccID());

       // resolve secondary accessions and set the accession states in the
       // Sequence
       Iterator secondaryIterator = seqInput.getSecondary().iterator();
       while (secondaryIterator.hasNext()) {
           AccessionRawAttributes ara =
               (AccessionRawAttributes) secondaryIterator.next();
           logger.logdDebug("Add Event Secondary: " + (ara).getAccID(), false);
           try {
             inputSequence.addAccSecondary(
                 accResolver.resolveAttributes(ara,
                     inputSequence.getSequenceKey()));
           }
           catch (MGIException e) {
             SeqloaderException e1 =
                 (SeqloaderException) eFactory.getException(
              SeqloaderExceptionFactory.CreateSecondaryAccessionErr, e);
             throw e1;
           }

       }
       // resolve sequence reference associations and set the states
       // in the Sequence
       Vector references = seqInput.getRefs();
       if (!references.isEmpty()) {
           try {
             processReferences(inputSequence, references);
           }
           catch (MGIException e) {
             SeqloaderException e1 =
                 (SeqloaderException) eFactory.getException(
              SeqloaderExceptionFactory.CreateRefAssocErr, e);
             throw e1;
           }

       }

       // process Molecular Source then create SEQ_Source
       // associations
       Iterator msIterator = seqInput.getMSources().iterator();

       while (msIterator.hasNext()) {
           // should get the primary accid from 'sequence' when we implement
           // the copy in sequence.getAccPrimary()

           // process the molecular source
           stopWatch.start();
           MolecularSource inputMSSource = msProcessor.processNewSeqSrc(
               seqInput.getPrimaryAcc().getAccID(),
               (MSRawAttributes) msIterator.next());
           stopWatch.stop();
           double time = stopWatch.time();
           stopWatch.reset();
           if (highMSPTime < time) {
             highMSPTime = time;
           }
           else if (lowMSPTime > time) {
             lowMSPTime = time;
           }
           runningMSPTime += time;

           // create a new source association state
           SEQ_Source_AssocState sourceAssocState = new SEQ_Source_AssocState();

           // set the sequence key and set in the source association
           sourceAssocState.setSequenceKey(inputSequence.getSequenceKey());

           // set the source key and set it in the source association
           //sourceAssocState.setSourceKey(msSourceSC.getMSKey());
           sourceAssocState.setSourceKey(inputMSSource.getMSKey());

           // set the source association in the Sequence
           try {
             inputSequence.addSeqSrcAssoc(sourceAssocState);
           }
           catch (MGIException e) {
             SeqloaderException e1 =
                 (SeqloaderException) eFactory.getException(
              SeqloaderExceptionFactory.CreateSrcAssocErr, e);
             throw e1;
           }
       }
       // send the new sequence to its stream
       try {
         inputSequence.sendToStream();
       }
       catch (MGIException e) {
         SeqloaderException e1 =
             (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.SequenceSendToStreamErr, e);
         throw e1;
       }
   }


   /**
    * Processes sequence references associations and sets them in the Sequence
    * @assumes
    * @effects
    * @param sequence the Sequence we to which to add references
    * @param references Vector of SeqRefAssocPairs for 'sequence'
    * @return Nothing
    * @throws ConfigException - from Sequence.addRefAssoc
    * @throws CacheException - from SeqRefAssocProcessor
    * @throws DBException - from SeqRefAssocProcessor and Sequence.addRefAssoc
    * @throws KeyNotFoundException - from SeqRefAssocProcessor
    */

   protected void processReferences(Sequence sequence, Vector references)
       throws KeyNotFoundException, DBException, CacheException, ConfigException {
     // resolve sequence reference associations and set the states
     // in the Sequence

     // The MGI_Reference_Assoc state for a given reference
     MGI_Reference_AssocState refAssocState = null;

     Iterator referenceIterator = references.iterator();
     while(referenceIterator.hasNext()) {
           refAssocState = refAssocProcessor.process(
               (SeqRefAssocPair)referenceIterator.next(),
               sequence.getSequenceKey());

           // null if reference not in MGI
           if(refAssocState != null) {
               sequence.addRefAssoc(refAssocState);
           }
     }

   }

   /**
   * Resolves SequenceRawAttributes to SEQ_SequenceState and handles wrapping
   * of KeyNotFoundException in a SequenceResolverException
   * @assumes Nothing
   * @effects queries a database
   * @param rawSeq the raw sequence to resolve
   * @return seqState a SEQ_SequenceState
   * @throws ConfigException
   * @throws CacheException
   * @throws DBException
   * @throws TranslationException
   */

   protected SEQ_SequenceState resolveRawSequence(SequenceRawAttributes rawSeq)
          throws SequenceResolverException, ConfigException, CacheException,
              DBException, TranslationException {
        // resolve raw sequence; catch KeyNotFoundException and
        // throw SeqloaderException if we can't translate/resolve any
        // of the raw sequence attributes

        SEQ_SequenceState seqState = null;
        try {
          seqState = seqResolver.resolveAttributes(rawSeq);
        }
        catch (KeyNotFoundException e) {
          // report existing sequence key with input Sequence attributes

          // throw an exception
          throw new SequenceResolverException(e);
        }
        return seqState;
   }

}
