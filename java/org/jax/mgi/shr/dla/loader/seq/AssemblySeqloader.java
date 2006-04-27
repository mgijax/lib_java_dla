package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.dla.loader.DLALoaderException;

/**
 * An object which extends Seqloader and implements the Seqloader
 * getDataIterator method to set the DataIterator with a InputDataFileIterator
 * @has See superclass
 * @does
 * <UL>
 * <LI>implements superclass (Seqloader) getDataIterator to set superclass
 *      DataIterator
 * <LI>It has an empty implementation of the superclass (DLALoader)
 *     preProcess method
 * <LI>It has an empty implementation of the superclass (Seqloader)
 *     appPostProcess method.
 * </UL>
 * @author sc
 * @version 1.0
 */

public class AssemblySeqloader extends SeqLoader {

    /**
     * constructor
     * @throws DLALoaderException thrown from the base class
     */
    public AssemblySeqloader() throws DLALoaderException
    {
        super();
    }

    /**
     * This load has no preprocessing
     */

     protected void preprocess() { }

    /**
     * creates and sets the superclass OrganismChecker and RecordDataIterator
     * with a GBOrganismChecker and creates and creates a GBInputFile
     * with a GBSequenceInterpretor; gets an iterator from the GBInputFile
     * @throws MGIException if errors creating InputDatafile, or getting
     *  iterator over the InputDataFile
     */
    protected void getDataIterator() throws MGIException {

        // Create a DataInput File
        InputDataFile inData = new InputDataFile();
        // get an iterator over the input file
        super.iterator = inData.getIterator((RecordDataInterpreter)loadCfg.getInterpreterClass());

    }

    /**
     * This loader has no application specific post processing
     */

    protected void appPostProcess() {

   }
}
