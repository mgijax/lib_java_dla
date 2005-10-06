package org.jax.mgi.shr.dla.ext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.File;

import org.jax.mgi.shr.unix.AbstractCommand;
import org.jax.mgi.shr.unix.CommandException;
import org.jax.mgi.shr.config.CommandsCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.ioutils.*;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.dla.log.*;

/**
 * Is a class for executing the annotload command from a Java application
 * @has a Logger for logging command output and a CommandCfg for accessing
 * configuration settings
 * @does sets up the runtime environment and executes the annotload command
 * and logs output to the application logs
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public class AnnotationLoad extends AbstractCommand {

    public static final String MODE_NEW = "new";
    public static final String MODE_APPEND = "append";
    public static final String MODE_PREVIEW = "preview";

    private CommandsCfg cfg = null;
    private SQLDataManager sqlMgr = null;
    private String path = null;
    private String server = null;
    private String database = null;
    private String user = null;
    private String passwordFile = null;
    private String mode = null;
    private String filename = null;
    private String annotationTypeName = null;
    private String jnumber = null;
    private boolean okToLoadObsolete = false;

    /**
     * constructor
     * @param filename the name of the file containing the input data for the
     * annotload command
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     */
    public AnnotationLoad(String filename, SQLDataManager sqlMgr)
    throws ConfigException
    {
        super();
        this.cfg = new CommandsCfg();
        this.filename = filename;
        this.sqlMgr = sqlMgr;
    }

    /**
     * constructor
     * @param filename the name of the file containing the input data for the
     * annotload command
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     */
    public AnnotationLoad(String filename, SQLDataManager sqlMgr,
                          CommandsCfg commandsCfg)
    throws ConfigException
    {
        super();
        this.cfg = commandsCfg;
        this.filename = filename;
        this.sqlMgr = sqlMgr;
    }


    /**
     * get the command line for executing the annotload. This is called by the
     * super class during the run() method
     * @return the command line string
     */
    protected String getCommandLine()
    throws ConfigException
    {
        configure();
        String cmd = this.path + " -S" + this.server + " -D" + this.database +
                     " -U" + this.user + " -P" + this.passwordFile +
                     " -M" + this.mode + " -I" + this.filename +
                     " -R" + this.jnumber + " -A" + this.annotationTypeName;
        return cmd;
    }

    /**
     * configure any instance variables using the given configuration object.
     * This method is called by the superclass during the run() method prior
     * to calling getCommandLine() in order for this instance to access any
     * configuration settings it needs to create the command line string.
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     */
    protected void configure()
    throws ConfigException
    {
        this.path = this.cfg.getAnnotLoadPath();
        this.server = this.sqlMgr.getServer();
        this.database = this.sqlMgr.getDatabase();
        this.user = this.sqlMgr.getUser();
        this.passwordFile = this.sqlMgr.getPasswordFile();
        this.mode = this.cfg.getAnnotLoadMode();
        this.annotationTypeName = this.cfg.getAnnotLoadType();
        this.jnumber = this.cfg.getAnnotLoadReference();
    }

    public void postrun()
    throws CommandException
    {
        try
        {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(
                "MMddyyyy");
            Calendar c = Calendar.getInstance();
            String date = dateFormatter.format(c.getTime());
            Logger logger = super.getLogger();
            File file = new File(this.filename);
            String errorFilename = file.getName() + "." + date + "." +
                "error";
            InputDataFile errorsFile = new InputDataFile(errorFilename);
            RecordDataIterator i = errorsFile.getIterator();
            boolean foundMessage = false;
            while (i.hasNext())
            {
                String line = (String) i.next();
                if (line.startsWith("Start Date"))
                    continue;
                if (line.startsWith("End Date"))
                    continue;
                if (line.startsWith(System.getProperty("line.separator")))
                    continue;
                foundMessage = true;
                break;
            }

            if (foundMessage)
            {
                String message = "Detected possible errors while " +
                    "running annotation load. See " +
                    errorFilename + " for details";

                if (logger instanceof DLALogger)
                {
                    DLALogger dlaLogger = (DLALogger)logger;
                    dlaLogger.logpInfo(message, false);
                }
                else
                {
                    logger.logError(message);
                }
            }
        }
        catch (Exception e)
        {
            throw new CommandException("Error running post method in " +
                                       "AnnotationLoad", e);
        }
    }

}
