// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.dbs.mgd.MolecularSource.MSException;
import java.util.Vector;

/**
 * @is an interface which defines methods to process a SequenceInput object, get
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
        throws SeqloaderException, RepeatSequenceException,
           ChangedOrganismException,
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
