package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.lookup.MarkerKeyLookupBySeqKey;
import org.jax.mgi.dbs.mgd.lookup.SequenceKeyLookupBySeqID;
import org.jax.mgi.shr.config.ALOLoadCfg;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;
import org.jax.mgi.shr.dla.loader.alo.SequenceNotInDatabaseException;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that provides basic needs objects for processing allele 
 * sequence information
 * @has
 * <UL>
 * <LI>sequence key
 * <LI>SequenceKeyLookupBySeqID
 * <LI>AlleleLookupBySeqKey 
 * <LI>MarkerKeyLookupBySeqKey 
 * <LI>SequenceKeyLookupBySeqID
 * <LI>SeqAlleleAssocResolver 
 * </UL>
 * @does provides abstract process and postprocess methods
 * <UL>
 * <LI>provides abstract process and postprocess methods 
 * <LI>implements the preprocess method which simply determines whether a 
 *     sequence exists in MGI as an object
 * </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public abstract class AlleleSequenceProcessor {

    protected DLALogger logger;
    
    protected ALOLoadCfg config;
    protected Integer sequenceKey;
    protected ALOLoaderAbstractFactory factory;

    protected AlleleLookupBySeqKey alleleLookup;
    protected SeqAlleleAssocResolver seqAlleleResolver;
    // factory sets next two 
    protected SequenceKeyLookupBySeqID sequenceLookup;
    protected MarkerKeyLookupBySeqKey markerLookup;
 
    /**
     * Constructs a AlleleSequenceProcessor
     * @throws MGIException
     */

    public AlleleSequenceProcessor()
            throws MGIException {
        logger = DLALogger.getInstance();
        config = new ALOLoadCfg();
        alleleLookup = new AlleleLookupBySeqKey();
        alleleLookup.initCache();
        seqAlleleResolver = new SeqAlleleAssocResolver();

        factory = ALOLoaderAbstractFactory.getFactory();
        sequenceLookup = factory.getSequenceKeyLookupBySeqID();
        markerLookup = factory.getMarkerKeyLookupBySeqKey();
    }
    public void setSequenceKey(Integer s) {
        sequenceKey = s;
    }
    public Integer getSequencekey() {
        return sequenceKey;
    }
     /**
     * does preprocessing tasks
     * @throws MGIException, including SequenceNotInDatabaseException
     *
     */
    public void preprocess(ALORawInput aloInput, ALO resolvedALO) 
	   throws MGIException  {
        String seqID = aloInput.getSequenceAssociation().getSeqID();
        this.sequenceKey = sequenceLookup.lookup(seqID);
        if(this.sequenceKey == null) {
            SequenceNotInDatabaseException e =
            new SequenceNotInDatabaseException();
            e.bindRecordString(seqID);
            throw e;
        }
    }

  /**
   * 
   * @param aloInput ALORawInput object - a set of raw attributes to resolve
   * and add to the database
   * @param resolvedALO - the ALO object to which will will add resolved
   *         sequence information
    * @param alleleKey of the allele sequence association we are processing
   * @throws ALOResolvingException if errors resolving derivation or mutant
   *         cell line attributes
   * @throws MGIException if errors
   * <UL>
   * <LI>accessing a Lookup cache
   * <LI>adding to LazyCached lookups
   * <LI>if resolvers have errors accessing configuration
   * <LI> if resolvers have issues resolving translated
   *         attributes
   * </OL
   */

   public abstract void process(ALORawInput aloInput, ALO resolvedALO,
        Integer alleleKey)
                throws  MGIException;

    /**
   * subclasses implement this method to accomplish any post processing tasks
   */
   public abstract void postprocess() throws MGIException;
}
