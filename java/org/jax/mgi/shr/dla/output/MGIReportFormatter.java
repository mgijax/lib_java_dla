package org.jax.mgi.shr.dla.output;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jax.mgi.shr.ioutils.OutputFormatter;
import org.jax.mgi.shr.ioutils.OutputDataFile;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.ioutils.IOUExceptionFactory;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.HTMLFormatterCfg;
import org.jax.mgi.shr.config.DLALoaderCfg;
import org.jax.mgi.shr.config.DatabaseCfg;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.stringutil.Sprintf;

/**
 * is an extension of OutputFormatter used for formatting runtime reports
 * from a load application using a standard mgi report format
 * @has a Configurator for reading configuration settings
 * @does formats output to meet MGI reporting standards
 * @abstract the format method must be implemented at the base class level
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public abstract class MGIReportFormatter implements OutputFormatter
{

    private String server = null;
    private String database = null;
    private int linecnt = 0;
    private ColumnHeader[] headers;

    // The following constant definitions are exceptions thrown by this class
    private static final String ConfigErr = IOUExceptionFactory.ConfigErr;


    /**
     * run processes prior to formatting the output
     * @assumes nothing
     * @effects empty method call which can be overriden by the base class
     * @throws IOUException thrown if there is an error accessing the
     * file system
     */
    public void preprocess() throws IOUException
    {
        DLALoaderCfg dlaCfg = null;
        DatabaseCfg dbCfg = null;
        try
        {
            dlaCfg = new DLALoaderCfg();
            String dbPrefix = dlaCfg.getLoadPrefix();
            dbCfg = new DatabaseCfg(dbPrefix);
        }
        catch (ConfigException e)
        {
            IOUExceptionFactory exceptionFactory = new IOUExceptionFactory();
           IOUException e2 = (IOUException)
               exceptionFactory.getException(ConfigErr, e);
           e2.bind("configure a report formatter with the name of the " +
                   "database and server");
           throw e2;

        }
        this.server = dbCfg.getServer();
        this.database = dbCfg.getDatabase();
        this.headers = getColumnHeaders();
    }

    /**
     * run processes after formatting the output
     * @assumes nothing
     * @effects empty method call which can be overriden by the base class
     */
    public void postprocess() {}

    /**
     * get the names of columns for this report
     * @return the column names
     */
    public abstract ColumnHeader[] getColumnHeaders();

    /**
     * get the report description for this report
     * @return the report description
     */
    public abstract String getReportDescription();

    /**
     * formats a data item
     * @param data the data item to format
     * @return the formatted string
     */
    public String format(Object data)
    {
        this.linecnt++;
        String s= (String)data;
        String[] fields = s.split(OutputDataFile.TAB);
        String format = this.getFormatString();
        String line = Sprintf.sprintf(format, fields);
        return line.trim() + OutputDataFile.CRT;
    }


    /**
     * get the report header text
     * @assumes nothing
     * @effects nothing
     * @return the report header text
     */
    public String getHeader()
    {
        String format = this.getFormatString();
        String[] headText = new String[this.headers.length];
        for (int i = 0; i < this.headers.length; i++)
            headText[i] = this.headers[i].name;

        String head = Sprintf.sprintf(format.toString(), headText);
        StringBuffer headerSeparator = new StringBuffer();
        for (int i = 0; i < this.headers.length; i++)
        {
            for (int j = 0; j < this.headers[i].size - 1; j++)
                headerSeparator.append("-");
            headerSeparator.append(" ");
        }
        headerSeparator.deleteCharAt(headerSeparator.length() - 1);
        String newline = OutputDataFile.CRT;
        String description = getReportDescription();
        return this.getStandardHeader() + newline + newline + description +
            newline + newline + head.trim() + newline +
            headerSeparator.toString().trim();
    }

    /**
     * get the standard report header text
     * @assumes nothing
     * @effects nothing
     * @return the standard report header
     */
    protected String getStandardHeader()
    {
        String newline = System.getProperty("line.separator");
        SimpleDateFormat formatter =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss yyy");
        String date = formatter.format(new Date());
        String header =
            "The Jackson Laboratory - Mouse Genome Informatics - " +
            "Mouse Genome Database (MGD)" + newline + "Copyright 1996, " +
            "1999, 2002 The Jackson Laboratory" + newline +
            "All Rights Reserved" + newline +
            "Date Generated:  " + date + newline;

        header = header + newline + "(SERVER=" + server +
            ";DATABASE=" + database + ")" + newline;

        return header;
    }

    /**
     * get the trailer text for this report
     * @assumes nothing
     * @effects nothing
     * @return the trailer text
     */
    public String getTrailer()
    {
        return getStandardTrailer();
    }

    /**
     * get the standard trailer text for this report
     * @assumes nothing
     * @effects nothing
     * @return the standard trailer text
     */
    protected String getStandardTrailer()
    {
        String newline = System.getProperty("line.separator");
        String trailer = "(" + linecnt + " rows affected)" + newline +
            newline + "WARRANTY DISCLAIMER AND COPYRIGHT NOTICE" +
            newline +
            "THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE " +
            "SUITABILITY OR" + newline +
            "ACCURACY OF THIS SOFTWARE OR " +
            "DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES," + newline +
            "EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND " +
            "FITNESS FOR A" + newline +
            "PARTICULAR PURPOSE OR THAT THE " +
            "USE OF THIS SOFTWARE OR DATA WILL NOT" + newline +
            "INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, " +
            "OR OTHER RIGHTS." + newline +
            "THE SOFTWARE AND DATA ARE " +
            "PROVIDED 'AS IS'" + newline + newline +
            "This software and data are provided to enhance knowledge " +
            "and encourage" + newline + "progress in the scientific " +
            "community and are to be used only for research" + newline +
            "and educational purposes.  Any reproduction or use for " +
            "commercial purpose" + newline +
            "is prohibited without the " +
            "prior express written permission of the Jackson" +
            newline + "Laboratory." + newline + newline + "Copyright © 1996, " +
            "1999, 2002 by The Jackson Laboratory";

        return trailer;
    }


   /**
     * return the string 'rpt' to be used as the report name sufix
     * @assumes nothing
     * @effects nothing
     * @return the string 'rpt'
     */

    public String getFileSuffix()
    {
        return "rpt";
    }

    /**
     *
     * is a class which represents a column header
     * @has a column name and a column width
     * @does nothing
     * @author M Walker
     * @version 1.0
     */
    public class ColumnHeader
    {
        /**
         * name of the column
         */
        public String name = null;
        /**
         * the column width
         */
        public int size;

        /**
         * constructor
         * @param name the name of the column
         * @param width the width of the column
         */
        public ColumnHeader(String name, int width)
        {
            this.name = name;
            this.size = width;
        }
    }

    private String getFormatString()
    {
        StringBuffer format = new StringBuffer();
        for (int i = 0; i < this.headers.length; i++)
        {
            format.append("%-");
            format.append(this.headers[i].size);
            format.append("s ");
        }
        format.deleteCharAt(format.length() - 1);
        return format.toString();
    }
}
