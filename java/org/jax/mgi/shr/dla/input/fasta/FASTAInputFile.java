package org.jax.mgi.shr.dla.input.fasta;

import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.config.ConfigException;


/**
 * an InputFile for FASTA data
 * @has a FASTA file
 * @does provides iteration of the records from the file
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class FASTAInputFile extends InputDataFile
{
    // the fasta file
    private String filename = null;

    /**
     * constructor
     * @param filename the fasta input file
     * @throws IOUException thrown if there is an error accessing the file
     * @throws ConfigException thrown if there is an error accessing the
     * configuration file
     */
    public FASTAInputFile(String filename) throws IOUException, ConfigException
    {
        super(filename);
        this.setBeginDelimiter("^>");
        this.setEndDelimiter(null);
        this.setOkToUseRegex(true);
        this.filename = filename;
    }

    /**
     * get the default RecordDataIterator for the file which returns a set of
     * FASTAData objects
     * @return RecordDataIterator containing FASTAData objects
     * @throws IOUException thrown if there is an error accessing the file
     */
    public RecordDataIterator getIterator() throws IOUException
    {
        RecordDataIterator rdi = super.getIterator();
        rdi.setInterpreter(FASTAData.getRecordInterpreter());
        return rdi;
    }

    /**
     * get the name of the fasta input file
     * @return the name of the fasta input file
     */
    public String getFilename()
    {
        return this.filename;
    }


}