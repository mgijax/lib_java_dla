package org.jax.mgi.shr.dla.input;

import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;

    /**
     * An abstract Sequence Interpreter class that gets the configurable
     *     attributes needed by all Sequence Interpretors
     * @has
     *   <UL>
     *   <LI>A configurator /
     *   <LI>sequence virtual bit /
     *   <LI>sequence status
     *   <LI>sequence MGI type /
     *   <LI>sequence logical db /
     *   <LI>reference association type for the load /
     *   <LI>provider name /
     *   </UL>
     * @does
     *   <UL>
     *   <LI>Sets default values needed by all sequence interpreters and
     *       gets configurable values from a configurator
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */


public abstract class SequenceInterpreter implements RecordDataInterpreter {
    // a configurator
    protected SequenceLoadCfg sequenceCfg;

    /**
     * The set of attributes with values common to all sequences
     */

    // whether this provider has virtual sequences
    protected String virtual;

    // MGI type for SEQ_Sequence
    protected Integer seqMGIType;

    // the Provider logical db
    protected String seqLogicalDB;

    // The reference association type always 'Provider'
    protected Integer refAssocType;

    // The sequence provider
    protected String provider;

    // the status of a sequence always 'active'
    protected String seqStatus;

    /**
     * Constructs a SequenceInterpreter getting the configurable attributes needed by all
     * implementing subclasses
     * @assumes Nothing
     * @effects Nothing
     * @throws ConfigException if can't find the Configuration file
     */

    public SequenceInterpreter() throws ConfigException {
        sequenceCfg = new SequenceLoadCfg();

        // get values from config
        virtual = sequenceCfg.getVirtual();
        provider = sequenceCfg.getProvider();
        seqLogicalDB = sequenceCfg.getLogicalDB();

        // get MGIType for SEQ_Sequence table
        seqMGIType = new Integer(MGITypeConstants.SEQUENCE);

        // get Reference association type for sequence references
        refAssocType = new Integer(MGIRefAssocTypeConstants.PROVIDER);

        // status value for all loaded sequences
        seqStatus = SeqloaderConstants.ACTIVE_STATUS;
    }
}
