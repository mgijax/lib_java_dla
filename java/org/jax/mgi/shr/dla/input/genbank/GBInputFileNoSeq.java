package org.jax.mgi.shr.dla.input.genbank;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.IOUException;

/**
 * An object that represents a GenBank input file that contains sequence
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

public class GBInputFileNoSeq extends InputDataFile
{
    /**
     * Constructs a CloneTreeInfo object.
     * @assumes Nothing
     * @effects Nothing
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws IOUException thrown if there is an error opening the
     * configuration file
     */
    public GBInputFileNoSeq()
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
        setEndDelimiter("\nORIGIN");

        // Do not use regular expressions to find the records.
        //
        setOkToUseRegex(false);
    }
}
