/* Generated by Together */

package org.jax.mgi.shr.dla.seqloader;
/**
 * Debug stuff
 */
import org.jax.mgi.shr.timing.Stopwatch;
import java.util.HashMap;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.shr.dla.seqloader.SequenceAttributeResolver;
import org.jax.mgi.shr.dla.seqloader.AccAttributeResolver;
import org.jax.mgi.shr.dla.seqloader.SeqRefAssocProcessor;
import org.jax.mgi.shr.dla.DLALogger;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.mgd.MolecularSource.MSProcessor;
import org.jax.mgi.dbs.mgd.MolecularSource.MSException;
import org.jax.mgi.dbs.mgd.MolecularSource.MSRawAttributes;
import org.jax.mgi.dbs.mgd.MolecularSource.MolecularSource;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.trans.TranslationException;

import java.util.Vector;
import java.util.Iterator;

abstract public class SeqProcessor  {
   /**
   * Debug stuff
   */
    protected Stopwatch stopWatch;
    public double runningLookupTime;
    public int sequenceCtr;
    public double highLookupTime;
    public double lowLookupTime;

    public double runningMSPTime;
    public double highMSPTime;
    public double lowMSPTime;


    // resolves GenBank sequence attributes to MGI values
    protected SequenceAttributeResolver seqResolver;

    // resolves molecular source attributes for a sequence to MGI values
    protected MSProcessor msProcessor;

    // Resolves sequence reference attributes to MGI values
    protected SeqRefAssocProcessor refAssocProcessor;

    // a stream for handling MGD DAO objects
    protected SQLStream mgdStream;

    // a stream for handling RADAR DAO objects
    protected SQLStream qcStream;

    // get a sequence load configurator
    protected SequenceLoadCfg config;

    // resolves accession attributes to MGI value
    protected AccAttributeResolver accResolver;

    // logicalDB_key for the load
    protected int logicalDBKey;

    // logger for the load
    protected DLALogger logger;

    // exception factory for seqloader exceptions
    protected SeqloaderExceptionFactory eFactory;

    public abstract void processSequence(SequenceInput sequenceInput)
        throws ConfigException, CacheException, DBException, TranslationException,
           KeyNotFoundException, MSException, SeqloaderException,
           RepeatSequenceException, SequenceResolverException,
           ChangedOrganismException, ChangedLibraryException;

       /**
   * processes Add events
   * @assumes Nothing
   * @effects queries and inserts into a database
   * @param seqInput SequenceInput object - a set of raw attributes to resolve
   *        and add to the database
   * @return nothing
   * @throws ConfigException
   * @throws CacheException
   * @throws DBException
   * @throws TranslationException
   * @throws KeyNotFoundException
   * @throws MSException
   * @throws SequenceResolverException
   */

   protected void processAddEvent(SequenceInput seqInput)
       throws ConfigException, CacheException, DBException, TranslationException,
         KeyNotFoundException, MSException, SequenceResolverException {
       // resolve raw sequence
       SEQ_SequenceState inputSequenceState = resolveRawSequence(seqInput.getSeq());

       // create the compound sequence; a sequence with its ref,
       // source association(s)and seqid(s),
       Sequence inputSequence = new Sequence(inputSequenceState, mgdStream);

       // create MGI_AttributesHistory on the sequence type if stream is
       // using bcp
       if(mgdStream.isBCP()) {
           MGI_AttributeHistoryState typeHistoryState = new
               MGI_AttributeHistoryState();
       }

       // resolve primary accession attributes and set the accession state
       // in the Sequence
       // Note: Exceptions thrown resolving accessions are thrown
       // out to main; indicates a bad LogicalDB value in the config file
       inputSequence.setAccPrimary(
         accResolver.resolveAttributes(
         seqInput.getPrimaryAcc(), inputSequence.getSequenceKey()));
       logger.logdDebug("Primary: " +
                      ( (MSRawAttributes) seqInput.getMSources().get(0)).
                      getOrganism() +
                      " " + seqInput.getPrimaryAcc().getAccID(), false);

       // resolve secondary accessions and set the accession states in the
       // Sequence
       Iterator secondaryIterator = seqInput.getSecondary().iterator();
       while (secondaryIterator.hasNext()) {
           AccessionRawAttributes ara = (AccessionRawAttributes) secondaryIterator.
             next();
           logger.logdDebug("Secondary: " + (ara).getAccID(), false);
           inputSequence.addAccSecondary(
               accResolver.resolveAttributes(ara,
                                         inputSequence.getSequenceKey()));
       }
       // resolve sequence reference associations and set the states
       // in the Sequence
       Vector references = seqInput.getRefs();
       if (!references.isEmpty()) {
           processReferences(inputSequence, references);
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
               //existingSequence.getAccPrimary().getAccID(),
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
           inputSequence.addSeqSrcAssoc(sourceAssocState);
       }
       // send the new sequence to its stream
       inputSequence.sendToStream();
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
