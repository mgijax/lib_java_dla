//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.loader.seq;

import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.RecordDataInterpreter;

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
// $Log
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
