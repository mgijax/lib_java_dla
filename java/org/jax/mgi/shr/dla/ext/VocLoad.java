package org.jax.mgi.shr.dla.ext;

import java.io.IOException;
import java.util.Properties;

import org.jax.mgi.shr.unix.AbstractCommand;
import org.jax.mgi.shr.config.VocloadCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.ConsoleLogger;
import org.jax.mgi.dbs.mgd.lookup.VocVocabLookup;
import org.jax.mgi.dbs.mgd.lookup.VocVocabLookup.VocVocab;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.exception.MGIException;


/**
 * Is a class for executing the vocload command from a Java application
 * @has a Logger for logging command output and a CommandCfg for accessing
 * configuration settings
 * @does sets up the runtime environment and executes the vocload command
 * and logs output to the application logs
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public class VocLoad extends AbstractCommand {

    private SQLDataManager sqlMgr = null;
    private String vocabName = null;
    private String path = null;
    private VocloadCfg config = null;
    private Boolean okToPreventUpdate = null;
    private String rcdfile = null;
    private String filename = null;
    private Boolean isFull = null;
    private String outputdir = null;


    /**
     * constructor
     * @param filename the name of the file containing the input data for the
     * vocload command
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     */
    public VocLoad(String filename, SQLDataManager sqlMgr)
    throws ConfigException
    {
        this.filename = filename;
        this.sqlMgr = sqlMgr;
        this.config = new VocloadCfg();
    }

    /**
     * constructor
     * @param filename the name of the file containing the input data for the
     * vocload command
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     */
    public VocLoad(String filename, SQLDataManager sqlMgr, VocloadCfg config)
    throws ConfigException
    {
        this.filename = filename;
        this.sqlMgr = sqlMgr;
        this.config = config;
    }


    /**
     * get the command line for executing the vocload. This is called by the
     * super class during the run() method
     * @return the command line string
     */
    protected String getCommandLine()
    throws ConfigException
    {
        configure();
        String updateFlag = "";
        String fullmode = "";
        if (this.okToPreventUpdate.booleanValue())
            updateFlag = " -n ";
        if (this.isFull.booleanValue())
            fullmode = " -f ";
        String cmd = this.path + fullmode + updateFlag + " " +
            this.rcdfile + " " + filename;
        return cmd;
    }

    public void setCommandPath(String path)
    {
        this.path = path;
    }

    public void setRCDFile(String rcdfile)
    {
        this.rcdfile = rcdfile;
    }

    public void setVocabName(String vocabName)
    {
        this.vocabName = vocabName;
    }

    public void setOkToPreventUpdate(Boolean bool)
    {
        this.okToPreventUpdate = bool;
    }

    public void setIsFull(Boolean bool)
    {
        this.okToPreventUpdate = bool;
    }

    public String getCommandPath()
    {
        return this.path;
    }

    public String getRCDFile()
    {
        return this.rcdfile;
    }

    public String getVocabName()
    {
        return this.vocabName;
    }

    public Boolean getOkToPreventUpdate()
    {
        return this.okToPreventUpdate;
    }

    public Boolean getIsFull()
    {
        return this.isFull;
    }

    private void configure()
    throws ConfigException
    {
        if (this.path == null)
            this.path = this.config.getCommandPath();
        if (this.rcdfile == null)
            this.rcdfile = this.config.getRCDFilename();
        if (this.okToPreventUpdate == null)
            this.okToPreventUpdate = this.config.getOkToPreventUpdate();
        if (this.isFull == null)
            this.isFull = this.config.getIsFull();
        if (this.vocabName == null)
            this.vocabName = this.config.getVocabName();
        if (this.outputdir == null)
            this.outputdir = this.config.getOutputDirectory();
        setEnv();
    }

    private void setEnv()
    throws ConfigException
    {
        VocVocab vocab = null;
        try
        {
            VocVocabLookup vocabLookup = new VocVocabLookup(this.sqlMgr);
            vocab = vocabLookup.lookup(this.vocabName);
        }
        catch (MGIException e)
        {
            ConfigException e2 =
                new ConfigException("Could not access data from VOC_Vocab", e);
        }
        Properties props = System.getProperties();
        props.put("TERM_FILE", "vocterms");
        props.put("VOCAB_NAME", this.vocabName);
        props.put("VOCAB_COMMENT_KEY", "0");
        props.put("DAG_ROOT_ID", "");
        props.put("FULL_MODE_DATA_LOADER", "bcp");
        props.put("MGITYPE", "13");
        props.put("ANNOT_TYPE_KEY", "0");
        if (vocab.isSimple)
            props.put("IS_SIMPLE", "1");
        else
            props.put("IS_SIMPLE", "0");
        if (vocab.isPrivate)
            props.put("IS_PRIVATE", "1");
        else
            props.put("IS_PRIVATE", "0");

        props.put("JNUM", vocab.jnumber);
        props.put("LOGICALDB_KEY", new Integer(vocab.logicaldbKey).toString());
        String outputPath = this.outputdir + java.io.File.separator;
        props.put("DISCREP_FILE", outputPath + "discrepancy.html");
        props.put("TERM_TERM_BCP_FILE", outputPath + "termTerm.bcp");
        props.put("TERM_TEXT_BCP_FILE", outputPath + "termText.bcp");
        props.put("TERM_NOTE_BCP_FILE", outputPath + "termNote.bcp");
        props.put("TERM_NOTECHUNK_BCP_FILE", outputPath + "termNoteChunk.bcp");
        props.put("TERM_SYNONYM_BCP_FILE", outputPath + "termSynonym.bcp");
        props.put("ACCESSION_BCP_FILE", outputPath + "accAccession.bcp");
        props.put("BCP_LOG_FILE", outputPath + "bcpLog.bcp");
        props.put("BCP_ERROR_FILE", outputPath + "bcpError.bcp");
        props.put("DBPASSWORD_FILE", this.sqlMgr.getPasswordFile());
        props.put("DBUSER", this.sqlMgr.getUser());
        props.put("DATABASE", this.sqlMgr.getDatabase());
        props.put("DBSERVER", this.sqlMgr.getServer());
    }

}
