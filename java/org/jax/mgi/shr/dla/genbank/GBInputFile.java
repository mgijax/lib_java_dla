//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.genbank;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.IOUException;

/**
 * @is An object that represents a GenBank input file that contains sequence
 *     records that exclude the actual sequences. This is done by setting the
 *     end delimiter prior to the sequence to make the iterator exclude the
 *     sequence from the records that it returns.
 * @has Nothing
 * @does
 *   <UL>
 *   <LI> Sets its attributes to exclude the sequence.
 *   </UL>
 * @company The Jackson Laboratory
 * @author dbm
 */

public class GBInputFile extends InputDataFile
{
    /**
     * Constructs a CloneTreeInfo object.
     * @assumes Nothing
     * @effects Nothing
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws IOUException thrown if there is an error opening the
     * configuration file
     */
    public GBInputFile()
        throws ConfigException, IOUException
    {
        // Indicate that a record begins when the string "LOCUS" is found with
        // seven spaces following it.
        //
        setBeginDelimiter("LOCUS       ");

        // Indicate that a record ends when the word "ORIGIN" is found at the
        // beginning of a line. This will cause the remainder of the record to
        // be ignored.
        //
        setEndDelimiter("\n//");

        // Do not use regular expressions to find the records.
        //
        setOkToUseRegex(false);
    }
}


//  $Log$
//  Revision 1.2  2004/06/29 17:07:01  mbw
//  fixed class names in constructor to reflect filename
//
//  Revision 1.1  2004/06/29 17:05:39  mbw
//  renamed class
//
//  Revision 1.1  2004/06/29 16:55:09  mbw
//  initial version
//
//
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
* Copyright \251 1996, 1999, 2002, 2004 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
