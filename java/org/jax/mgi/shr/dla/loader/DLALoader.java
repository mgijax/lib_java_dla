package org.jax.mgi.shr.dla.loader;

import org.jax.mgi.shr.config.DatabaseCfg;
import org.jax.mgi.shr.config.DLALoaderCfg;
import org.jax.mgi.shr.config.InputDataCfg;
import org.jax.mgi.shr.dbutils.bcp.BCPManager;
import org.jax.mgi.shr.config.BCPManagerCfg;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.dao.Inline_Stream;
import org.jax.mgi.shr.dbutils.dao.Batch_Stream;
import org.jax.mgi.shr.dbutils.dao.Script_Stream;
import org.jax.mgi.shr.dbutils.dao.BCP_Stream;
import org.jax.mgi.shr.dbutils.dao.BCP_Inline_Stream;
import org.jax.mgi.shr.dbutils.dao.BCP_Batch_Stream;
import org.jax.mgi.shr.dbutils.dao.BCP_Script_Stream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.DBSchema;
import org.jax.mgi.shr.dbutils.ScriptWriter;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dla.log.DLALogger;

/**
 * a base class which implements the DLA standards for database loaders.
 * @abstract this class provides the 'basic needs' objects for performing
 * database loads such as SQLDataManagers, Loggers, BCPManagers, SQLStreams
 * and ExceptionHandlers
 * Sub classes would be required to implement the following
 * methods:<br>
 * <UL>
 *   <LI>initialize - for initializing sub class instance variables
 *   <LI>preprocess - for performing pre processing
 *   <LI>run - for performing a load
 *   <LI>postprocess - for closing resources and other finalizations
 * </UL>
 * @has a set of 'basic-needs' objects for doing DLA loads<br>
 * <UL>
 *   <LI>A DLALogger
 *   <LI>A DLAExceptionHandler
 *   <LI>A configurator for configuring the input file
 *   <LI>A SQLDataManager for the qc database
 *   <LI>A SQLDataManager for the load database
 *   <LI>A BCPManager for the qc database
 *   <LI>A BCPManager for the load database
 *   <LI>A SQLStream for the qc database
 *   <LI>A SQLStream for the radar database
 * </UL>
 *
 * @does performs initialization of 'basic-needs' objects and calls
 * the subclass initialize, preprocess, run, and postprocess methods
 * @author mbw, dbm
 * @version 1.0
 */


public abstract class DLALoader {

  /**
   * A DataLoadLogger instance for sending messages to the three standard
   * log files.
   */
  protected DLALogger logger = null;

  /**
   * An SQL data manager for providing database access to the radar database
   */
  protected SQLDataManager qcDBMgr;

  /**
   * An SQL data manager for providing database access to the radar database
   */
  protected SQLDataManager loadDBMgr;

  /**
   * A bcp manager for controlling the bcp writers for the radar database.
   */
  protected BCPManager qcBCPMgr;

  /**
   * A bcp manager for controlling the bcp writers for the mgd database.
   */
  protected BCPManager loadBCPMgr;

  /**
   * the SQLStream used for loading data
   */
  protected SQLStream loadStream = null;

  /**
   * the SQLStream used for loading qc data
   */
  protected SQLStream qcStream = null;

  /**
   * configurator for the input file
   */
  protected InputDataCfg inputConfig = null;

  /**
   * configurator for all DLA parameters
   */
  protected DLALoaderCfg dlaConfig = null;

  /**
   * An exception handler for handling MGIExceptions
   */
  protected DLALoaderExceptionHandler exceptionHandler =
      new DLALoaderExceptionHandler();

  /**
   * An exception factory for generating DLAExceptions
   */
  private DLALoaderExceptionFactory dlaExceptionFactory =
      new DLALoaderExceptionFactory();

  /**
   * A DLASystemExit object for managing application exiting as specified
   * within the DLA standards
   */
  private DLASystemExit systemExit = new DLASystemExit();

  // The following constant definitions are exceptions thrown by this class
  private static final String InitException =
      DLALoaderExceptionFactory.InitException;
  private static final String RunException =
      DLALoaderExceptionFactory.RunException;
  private static final String PostProcessException =
      DLALoaderExceptionFactory.PostProcessException;
  private static final String SQLStreamNotSupported =
      DLALoaderExceptionFactory.SQLStreamNotSupported;
  private static final String PreProcessException =
      DLALoaderExceptionFactory.PreProcessException;

  /**
   * a default constructor
   * @has nothing
   * @does instantiates the 'basic-needs' classes for performing
   * database loads in accordance with the DLA standards
   */
  public DLALoader() {
    try {
      this.logger = DLALogger.getInstance();
      this.dlaConfig = new DLALoaderCfg();
      String loadPrefix = dlaConfig.getLoadPrefix();
      this.qcDBMgr = new SQLDataManager(new DatabaseCfg("RADAR"));
      this.qcDBMgr.setLogger(logger);
      this.loadDBMgr = new SQLDataManager(new DatabaseCfg(loadPrefix));
      this.loadDBMgr.setLogger(logger);
      this.qcBCPMgr = new BCPManager(new BCPManagerCfg("RADAR"));
      this.qcBCPMgr.setLogger(logger);
      this.loadBCPMgr = new BCPManager(new BCPManagerCfg(loadPrefix));
      this.loadBCPMgr.setLogger(logger);
      this.loadStream = createSQLStream(dlaConfig.getLoadStreamName(),
                                        loadDBMgr, loadBCPMgr);
      this.qcStream = createSQLStream(dlaConfig.getQCStreamName(),
                                      qcDBMgr, qcBCPMgr);
      this.inputConfig = new InputDataCfg();
    }
    catch (Exception e) {
      DLALoaderException e2 = (DLALoaderException)
          dlaExceptionFactory.getException(InitException, e);
      DLALoaderExceptionHandler.handleException(e2);
      DLASystemExit.fatalExit();
    }
  }

  /**
   * executes the initialize(), run() and post() methods
   * of the subclass loader and performs standard logging and
   * system exiting.
   * @assumes nothing.
   * @effects the four dla standrad log files and records within the
   * RADAR and/or MGD database. If bcp is being used then bcp files may be
   * available if they were configured to remain after executing them.
   */
  public void load() {
    String[] loadTables = this.dlaConfig.getTruncateLoadTables();
    String[] qcTables = this.dlaConfig.getTruncateQCTables();
    try {
      logger.logdInfo("Performing load initialization",true);
      if (loadTables != null)
          DLALoaderHelper.truncateTables(loadTables,
                                         this.loadDBMgr.getDBSchema(),
                                         logger);
      if (qcTables != null)
          DLALoaderHelper.truncateTables(qcTables,
                                         this.qcDBMgr.getDBSchema(),
                                         logger);
      initialize();
    }
    catch (Exception e) {
      DLALoaderException e2 = (DLALoaderException)
          dlaExceptionFactory.getException(InitException, e);
      DLALoaderExceptionHandler.handleException(e2);
      DLASystemExit.fatalExit();
    }
    try {
      logger.logdInfo("Performing load pre processing",true);
      preprocess();
    }
    catch (Exception e) {
      DLALoaderException e2 = (DLALoaderException)
          dlaExceptionFactory.getException(PreProcessException, e);
      DLALoaderExceptionHandler.handleException(e2);
      DLASystemExit.fatalExit();
    }
    try {
      logger.logdInfo("Performing load processing",true);
      run();
    }
    catch (Exception e) {
      DLALoaderException e2 = (DLALoaderException)
          dlaExceptionFactory.getException(RunException, e);
      DLALoaderExceptionHandler.handleException(e2);
      DLASystemExit.fatalExit();
    }
    try {
      logger.logdInfo("Performing post processing",true);
      postprocess();
      qcDBMgr.closeResources();
      loadDBMgr.closeResources();
    }
    catch (Exception e) {
      DLALoaderException e2 = (DLALoaderException)
          dlaExceptionFactory.getException(PostProcessException, e);
      DLALoaderExceptionHandler.handleException(e2);
      DLASystemExit.fatalExit();
    }
    logger.logdInfo("Load completed",true);
    logger.logpInfo("Load completed",false);
    DLASystemExit.exit();
  }

  /**
   * to initialize instance variable of the subclass
   * @assumes nothing
   * @effects instance variables will be instantiated
   * @throws MGIException if errors occur during initialization
   */
  protected abstract void initialize() throws MGIException;

  /**
   * to perform load pre processing
   * @assumes nothing
   * @effects any preprocessing will be performed
   * @throws MGIException if errors occur during preprocessing
   */
  protected abstract void preprocess() throws MGIException;


  /**
   * to perform a database load into the RADAR and/or MGD
   * database
   * @assumes nothing
   * @effects database records created within the RADAR and/or MGD
   * database and possible bcp files if doing bcp processing and the system
   * is configured to keep bcp files after executing them
   * @throws MGIException throw if an error occurs while performing the
   * load
   */
  protected abstract void run() throws MGIException;

  /**
   * to perform any finalization routines such as closing
   * SQLStreams
   * @assumes nothing
   * @effects post processing will be performed
   * @throws MGIException throw if an error occurs while performing
   * post processing
   */
  protected abstract void postprocess() throws MGIException;

  /**
   * create a new SQLStream based on the given name
   * @param name the name of the SQLStream to create
   * @param DBMgr the database manager
   * @param BCPMgr the bcp manager
   * @return the new SQLStream
   */
  protected SQLStream createSQLStream(String name, SQLDataManager DBMgr,
                                      BCPManager BCPMgr) throws MGIException
  {
      if (name.equals("org.jax.mgi.shr.dbutils.dao.Inline_Stream"))
          return new Inline_Stream(DBMgr);
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.Batch_Stream"))
          return new Batch_Stream(DBMgr);
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.Script_Stream"))
          return new Script_Stream(DBMgr.getScriptWriter());
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.BCP_Stream"))
          return new BCP_Stream(DBMgr, BCPMgr);
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.BCP_Inline_Stream"))
          return new BCP_Inline_Stream(DBMgr, BCPMgr);
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.BCP_Batch_Stream"))
          return new BCP_Batch_Stream(DBMgr, BCPMgr);
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.BCP_Script_Stream"))
          return new BCP_Script_Stream(DBMgr.getScriptWriter(), DBMgr, BCPMgr);
      else
      {
          DLALoaderExceptionFactory factory = new DLALoaderExceptionFactory();
          DLALoaderException e =
              (DLALoaderException)factory.getException(SQLStreamNotSupported);
          e.bind(name);
          throw e;
      }

  }



}
