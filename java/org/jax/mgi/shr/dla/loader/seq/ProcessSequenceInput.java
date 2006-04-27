package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSException;
import java.util.Vector;
import org.jax.mgi.shr.dla.input.SequenceInput;
import org.jax.mgi.dbs.mgd.loads.Seq.SequenceResolverException;

/**
 * An interface which defines methods to process a SequenceInput object, get
 * processing information about SequenceInput objects processed and delete Sequences.
 * @has nothing
 * @does provides an interface for processing SequenceInputs
 * @author sc
 */
public interface ProcessSequenceInput {
    /**
     * process the sequence represented by seqInput
     * @assumes nothing
     * @effects the given SequenceInput will be resolved to DAO classes which
     *  will be inserted or updated into the database or
     * batched up to be inserted  or updated into the database, depending on the
     * specific implementation
     * @param seqInput a SequenceInput object
     * @throws Seqloader Exception if error occurs while processing the
     * SequenceInput object
     */
    public void processInput(SequenceInput seqInput)
        throws SeqloaderException, ChangedOrganismException,
           SequenceResolverException, MSException;
    /**
    * method definition to get processing information about the SequenceInput
    * objects processed
    * @assumes nothing
    * @effects Noting
    */
    public Vector getProcessedReport();

    /**
    * method definition for deleting sequences
    * @assumes nothing
    * @effects Noting
    * @throws Seqloader Exception if errors deleting sequences
    */
    public void deleteSequences() throws SeqloaderException;
}
