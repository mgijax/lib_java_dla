package org.jax.mgi.dbs.mgd.loads.Seq;
/**
 * Debug stuff
 */
import org.jax.mgi.shr.timing.Stopwatch;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSProcessor;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSException;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSRawAttributes;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MolecularSource;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.exception.MGIException;

import java.util.Vector;
import java.util.Iterator;
import org.jax.mgi.dbs.mgd.loads.Acc.*;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.*;

import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.*;
import org.jax.mgi.shr.dla.input.*;
import org.jax.mgi.shr.dla.loader.seq.*;

/**
 * An object that resolves raw sequence attributes and adds a sequence, its
 * accession ids, references, and molecular sources to a database.
 * Deletes all sequences created by a given load.
 * @has
 *   <UL>
 *   <LI>a logger
 *   <LI>various lookups for resolving
 *   </UL>
 * @does
 *   <UL>
 *   <LI>deletes all sequences created by a given load
 *   <LI>adds a sequence to a database
 *   <LI>counts sequences added to the database
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SequenceInputProcessor implements ProcessSequenceInput  {
   /**
   * Debug stuff - public so I have easy access
   */
    protected Stopwatch stopWatch;
   // public double runningLookupTime;
   // public double highLookupTime;
   // public double lowLookupTime;

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

    // name of the jobtream
    protected String jobStreamName;

    // true if sequence references should be loaded
    Boolean okToLoadRefs;

    // exception factory for seqloader exceptions
    protected SeqloaderExceptionFactory eFactory;

    // current number of sequences added
    int addCtr = 0;

    /**
     * Constructs a SeqProcessor that adds and deletes sequence to/from
     * a database
     * @assumes Nothing
     * @effects Nothing
     * @param mgdSqlStream stream for adding Sequences to an  MGD database
     * @param radarSqlStream stream for adding QC information to a RADAR database
     * @param sar a SequenceAttributeResolver for resolving Sequences attributes
     * @throws CacheException if error creating a SEQRefAssocProcessor or a
     *      AccAttributeResolver
     * @throws DBException if error creating a SEQRefAssocProcessor or a
     *      AccAttributeResolver
     * @throws ConfigException if error creating a SEQRefAssocProcessor or a
     *      AccAttributeResolver or error creating a SequenceLoadCfg or
     *         reading config file
     * @throws MSException if error creating an MSProcessor
     * @throws DLALoggingException if error creating a logger
     */

    public SequenceInputProcessor(SQLStream mgdSqlStream,
                              SQLStream radarSqlStream,
                              SequenceAttributeResolver sar)
        throws CacheException, DBException, ConfigException, MSException,
               DLALoggingException {
      /**
      * Debug stuff
      */
      stopWatch = new Stopwatch();
      //runningLookupTime = 0.0;
      //highLookupTime = 0.0;
      //lowLookupTime = 0.0;
      runningMSPTime = 0.0;
      highMSPTime = 0.0;
      lowMSPTime = 999.0;

      mgdStream = mgdSqlStream;
      seqResolver = sar;

      // create an exception factory
      eFactory = new SeqloaderExceptionFactory();

      // Create an Accession Attribute Resolver
       accResolver = new AccAttributeResolver();

       // Create a Reference Association Processor
       refAssocProcessor = new SeqRefAssocProcessor();
       logger = DLALogger.getInstance();

        // Create a Molecular Source Processor
       msProcessor = new MSProcessor (mgdSqlStream, radarSqlStream, logger);

       // configurator to lookup logicalDB
       config = new SequenceLoadCfg();
       jobStreamName = config.getJobstreamName();
       okToLoadRefs = config.getOkToLoadReferences();
    }

    /**
    * deletes all Sequences loaded by a given loader from a database
    * @assumes Nothing
    * @effects deletes sequences from a database
    * @throws SeqloaderException if error getting SQLDataManager or executing
    *         a delete.
    */

    public void deleteSequences() throws SeqloaderException {

      String spCall = "select * from SEQ_deleteByCreatedBy('" + jobStreamName + "')";
      try {
        SQLDataManager sqlMgr =
            SQLDataManagerFactory.getShared(SchemaConstants.MGD);
        sqlMgr.execute(spCall);
      }
      catch (MGIException e) {
        SeqloaderException e1 =
            (SeqloaderException) eFactory.getException(
         SeqloaderExceptionFactory.ProcessDeletesErr, e);
        throw e1;
      }
    }

   /**
   * Adds a sequence to the database
   * @assumes Nothing
   * @effects queries and inserts into a database
   * @param seqInput SequenceInput object - a set of raw attributes to resolve
   *        and add to the database
   * @throws SeqloaderException if there are configuration, cacheing, database,
   *         translation, io, or lookup errors. These errors cause load to fail
   * @throws ChangedOrganismException provided for subclass
   * @throws SequenceResolverException if errors resolving a sequence
   * @throws MSException if errors resolving a sequences source
   */

   public void processInput(SequenceInput seqInput)
       throws SeqloaderException,  ChangedOrganismException,
       SequenceResolverException, MSException {

       SEQ_SequenceState inputSequenceState;
       SEQ_Sequence_RawState inputSequenceRawState;
       // resolve raw sequence
       try {
         inputSequenceState =
             resolveRawSequenceToSequenceState(seqInput.getSeq());
         inputSequenceRawState =
           resolveRawSequenceToSequenceRawState(seqInput.getSeq());
       }
        catch (ConfigException e) {
          SeqloaderException e1 =
              (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.ProcessAddErr, e);
          throw e1;
        }
       catch (CacheException e) {
          SeqloaderException e1 =
              (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.ProcessAddErr, e);
          throw e1;
        }
       catch (DBException e) {
          SeqloaderException e1 =
              (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.ProcessAddErr, e);
          throw e1;
        }
        catch (TranslationException e) {
          SeqloaderException e1 =
              (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.ProcessAddErr, e);
          throw e1;
        }

       // create the compound sequence; a sequence with its reference(s),
       // source association(s) and seqid(s)
       Sequence inputSequence;
       try {
         inputSequence =
             new Sequence(inputSequenceState,
                          inputSequenceRawState, mgdStream);
       }
       catch (MGIException e) {
         SeqloaderException e1 =
             (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.CreateSequenceErr, e);
          e1.bind(seqInput.getPrimaryAcc().getAccID());
         throw e1;
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
         e1.bind(seqInput.getPrimaryAcc().getAccID());
         throw e1;
       }

       logger.logdDebug("Add Event Primary: "  +
                        seqInput.getPrimaryAcc().getAccID());
       for (Iterator i = seqInput.getMSources().iterator(); i.hasNext(); ) {
          logger.logdDebug("    " + ((MSRawAttributes) i.next()).getOrganism());
       }

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
             e1.bind(seqInput.getPrimaryAcc().getAccID());
             throw e1;
           }

       }
       if (okToLoadRefs.equals(Boolean.TRUE)) {
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
       }

       // process Molecular Source then create sequence-source
       // associations
       Iterator msIterator = seqInput.getMSources().iterator();

       // additionally keep track of preferred organism by the following
       // preference: mouse, human, rat (currently using
       // lowest _organism_key to do this)
       int preferredOrganismKey = 0;

       while (msIterator.hasNext()) {
           // process the molecular source
           stopWatch.start();
           MolecularSource inputMSSource = msProcessor.processNewSeqSrc(
               seqInput.getPrimaryAcc().getAccID(),
               (MSRawAttributes) msIterator.next());
          if (preferredOrganismKey == 0 ||
              inputMSSource.getOrganismKey().intValue() < preferredOrganismKey)
              preferredOrganismKey =
                  inputMSSource.getOrganismKey().intValue();
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

           // create a new sequence-source association state
           SEQ_Source_AssocState sourceAssocState = new SEQ_Source_AssocState();

           // set the sequence key in the sequence-source association
           sourceAssocState.setSequenceKey(inputSequence.getSequenceKey());

           // set the source key in the sequence-source association
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
       // set the preferred organism key in the sequence object
       if (preferredOrganismKey == 0)
       {
           SeqloaderException e1 =
               (SeqloaderException) eFactory.getException(
                SeqloaderExceptionFactory.UnallowedOrganismKeyErr);
           e1.bind(preferredOrganismKey);
           throw e1;
       }
       inputSequence.setPrefferedOrganismKey(preferredOrganismKey);
       // send the new sequence to its stream
       try {
         inputSequence.sendToStream();
         addCtr++;
       }
       catch (MGIException e) {
         SeqloaderException e1 =
             (SeqloaderException) eFactory.getException(
          SeqloaderExceptionFactory.SequenceSendToStreamErr, e);
         throw e1;
       }
   }

   /**
   * Gets a Vector containing a String reporting count of Sequences added
   * @assumes nothing
   * @effects nothing
   * @return Vector containing single string with count of Sequences added
   */
   public Vector getProcessedReport() {
       Vector report = new Vector();
       report.add("Total sequences added: " + addCtr);
       return report;
   }
   /**
    * Processes sequence-reference associations and sets them in the Sequence
    * @assumes nothing
    * @effects nothing
    * @param sequence the Sequence which to add references
    * @param references Vector of RefAssocRawAttributes (
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
            Object ref = referenceIterator.next();
        refAssocState = refAssocProcessor.process(
                  (RefAssocRawAttributes)ref, sequence.getSequenceKey());

        // null if reference not in MGI
            if(refAssocState != null) {
                sequence.addRefAssoc(refAssocState);
        }
     }
   }

   /**
   * Resolves SequenceRawAttributes to SEQ_SequenceState
   * @assumes Nothing
   * @effects queries a database
   * @param rawSeq the raw sequence to resolve
   * @return seqState a SEQ_SequenceState
   * @throws SequenceResolverException if any SequenceRawAttributes attributes
   *          cannot be resolved
   * @throws ConfigException from SequenceAttributeResolver.resolveAttributes
   * @throws CacheException from SequenceAttributeResolver.resolveAttributes
   * @throws DBException from SequenceAttributeResolver.resolveAttributes
   * @throws TranslationException from SequenceAttributeResolver.resolveAttributes
   */

   protected SEQ_SequenceState
       resolveRawSequenceToSequenceState(SequenceRawAttributes rawSeq)
          throws SequenceResolverException, ConfigException, CacheException,
              DBException, TranslationException {

        // resolve raw sequence
        SEQ_SequenceState seqState = null;
        try {
          seqState = seqResolver.resolveAttributes(rawSeq);
        }
        catch (KeyNotFoundException e) {
          // throw an exception
          throw new SequenceResolverException(e);
        }
        return seqState;
   }

   /**
   * Resolves SequenceRawAttributes to SEQ_Sequence_RawState
   * @assumes Nothing
   * @effects queries a database
   * @param rawSeq the raw sequence to resolve
   * @return seqRawState a SEQ_Sequence_RawState
   * @throws SequenceResolverException if any SequenceRawAttributes attributes
   *          cannot be resolved
   * @throws ConfigException from SequenceAttributeResolver.resolveAttributes
   * @throws CacheException from SequenceAttributeResolver.resolveAttributes
   * @throws DBException from SequenceAttributeResolver.resolveAttributes
   * @throws TranslationException from SequenceAttributeResolver.resolveAttributes
   */

   protected SEQ_Sequence_RawState
       resolveRawSequenceToSequenceRawState(SequenceRawAttributes rawSeq)
          throws SequenceResolverException, ConfigException, CacheException,
              DBException, TranslationException {

        // resolve raw sequence
        SEQ_Sequence_RawState seqRawState = null;
        try {
          seqRawState = seqResolver.resolveRawAttributes(rawSeq);
        }
        catch (KeyNotFoundException e) {
          // throw an exception
          throw new SequenceResolverException(e);
        }
        return seqRawState;
   }

}
