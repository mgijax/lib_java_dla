package org.jax.mgi.shr.dla.input;

import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.config.ConfigException;

/**
 * <p>@is </p>
 * <p>@has </p>
 * <p>@does </p>
 * <p>@company The Jackson Laboratory</p>
 * @author not attributable
 *
 */

public class FASTAInput extends InputDataFile
{

    private String filename = null;

    public FASTAInput(String filename) throws IOUException, ConfigException
    {
        super(filename);
        this.setBeginDelimiter("^>");
        this.setEndDelimiter(null);
        this.setOkToUseRegex(true);
        this.filename = filename;
    }

    public RecordDataIterator getIterator() throws IOUException
    {
        RecordDataIterator rdi = super.getIterator();
        rdi.setInterpreter(FASTAData.getRecordInterpreter());
        return rdi;
    }

    public String getFilename()
    {
        return this.filename;
    }


}