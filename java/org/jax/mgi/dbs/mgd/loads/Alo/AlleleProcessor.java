package org.jax.mgi.dbs.mgd.loads.Alo;

import java.util.HashSet;
import java.util.Vector;

import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.SeqRefAssocProcessor;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.shr.config.ALOLoadCfg;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that provides the basic needs objects for Allele Processors and an
 *  abstract process method to be implemented by subclasses
 * @has
 * <UL>
 * <LI>a configurator
 * <LI>a logger
 * <LI>ALOLoaderAbstractFactory  
 * <LI>AlleleMutantCellLineProcessor
 * <LI>AlleleResolver 
 * <LI>AlleleMutationResolver
 * <LI>SeqRefAssocProcessor
 * </UL>
 * @does provides the basic needs objects for Allele Processors
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public abstract class AlleleProcessor{

    // get a ALO load configurator
    protected ALOLoadCfg config;

    // logger for the load - for qc
    protected DLALogger logger;
    
    // Factory from which we get some objects
    protected ALOLoaderAbstractFactory factory;

    // resolves raw allele attributes to a state
    protected AlleleResolver alleleResolver;
    
    // Creates allele to mutant cell line association DAOs
    protected AlleleMutantCellLineProcessor alleleMclProcessor;
    
    // creates molecular mutation DAOs
    protected AlleleMutationProcessor mutationProcessor;
    
    // creates reference association DAOs
    protected SeqRefAssocProcessor refAssocProcessor;
    
    
    
    /**
     * Constructs a AlleleProcessor 
     * @throws MGIException
     */

    public AlleleProcessor() 
	    throws MGIException {
        config = new ALOLoadCfg();
        logger = DLALogger.getInstance();
        factory = ALOLoaderAbstractFactory.getFactory();
        alleleMclProcessor = new AlleleMutantCellLineProcessor();
        alleleResolver = new AlleleResolver();
        mutationProcessor = new AlleleMutationProcessor();
        refAssocProcessor = new SeqRefAssocProcessor();
    }

    /**
     * subclasses implement this method to accomplish any preprocessing tasks
     */
    public abstract void preprocess()
            throws MGIException;

    /**
    * subclasses implement this method to process raw allele attributes
    * returns the Integer allele key of the processed allele, which may be
    * new or existing in the database
    */

    public abstract Integer process(ALORawInput aloInput, ALO resolvedALO,
    HashSet objects1, HashSet objects2)
    throws MGIException;

    /**
    * subclasses implement this method to accomplish any postprocessing tasks
    */
    public abstract void postprocess()
            throws MGIException;

    /**
    * Processes MGI ID for an allele
    * All ALOs need to create MGI IDs - handy to have a seperate method to
    * call at the very last so we don't waste MGI IDs
    * @param resolvedALO - the ALO object to which will will add resolved
    *         reference associations
    * @throws MGIException if error creating an ACC_AccessionSatate
    */
    public void processAlleleMGIID(ALO resolvedALO) throws MGIException {

        if(resolvedALO.getAlleleDAO() == null) {
            return;
        }

        // get allele key
        Integer alleleKey = resolvedALO.getAlleleDAO().getKey().getKey();

        // Get an ACC_AccessionState object that contains a new MGI ID
        ACC_AccessionState state = AccessionLib.getNextAccState();

        // Split the accession ID into its prefix and numeric parts.
        Vector vParts = AccessionLib.splitAccID(state.getAccID());

        // Set any remaining required attributes of the ACC_AccessionState
        // object
        state.setPrefixPart((String)vParts.get(0));
        state.setNumericPart((Integer)vParts.get(1));
        state.setLogicalDBKey(new Integer(LogicalDBConstants.MGI));
        state.setObjectKey(alleleKey);
        state.setMGITypeKey(new Integer(MGITypeConstants.ALLELE));
        state.setPrivateVal(Boolean.FALSE);
        state.setPreferred(Boolean.TRUE);
        resolvedALO.addAccession(state);
    }
}

