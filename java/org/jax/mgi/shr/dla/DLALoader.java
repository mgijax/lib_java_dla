package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.config.DatabaseCfg;
import org.jax.mgi.shr.config.DLALoaderCfg;
import org.jax.mgi.shr.config.InputDataCfg;
import org.jax.mgi.shr.dbutils.bcp.BCPManager;
import org.jax.mgi.shr.config.BCPManagerCfg;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.dao.Inline_Stream;
import org.jax.mgi.shr.dbutils.dao.Batch_Stream;
import org.jax.mgi.shr.dbutils.dao.Script_Stream;
import org.jax.mgi.shr.dbutils.dao.BCP_Inline_Stream;
import org.jax.mgi.shr.dbutils.dao.BCP_Batch_Stream;
import org.jax.mgi.shr.dbutils.dao.BCP_Script_Stream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.ScriptWriter;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.exception.MGIException;

/**
 * @is a base class which implements the DLA standards for all loaders.
 * @abstract this class provides the instantiation of the 'basic needs'
 * objects for performing database loads such as SQLDataManagers, loggers,
 * BCPManagers, SQLJobstreams and DLA exception handlers and factories.
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
 *   <LI>A primary input file for loading into the database
 *   <LI>A SQLDataManager for the RADAR database
 *   <LI>A SQLdataManager for the MGD database
 *   <LI>A BCPManager for the RADAR database
 *   <LI>A BCPManager for the MGD database
 *
 * @does performs initialization of 'basic-needs' and instantiates
 * the subclass and calls the initialize(), run() and finale() methods
 * on the subclass.
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
  protected SQLDataManager radarDBMgr;

  /**
   * An SQL data manager for providing database access to the radar database
   */
  protected SQLDataManager mgdDBMgr;

  /**
   * A bcp manager for controlling the bcp writers for the radar database.
   */
  protected BCPManager radarBCPMgr;

  /**
   * A bcp manager for controlling the bcp writers for the mgd database.
   */
  protected BCPManager mgdBCPMgr;

  /**
   * the SQLStream used for loading data
   */
  protected SQLStream loadStream = null;

  /**
   * the SQLStream used for loading qc data
   */
  protected SQLStream qcStream = null;

  protected InputDataCfg inputConfig = null;

  /**
   * An exception handler for handling MGIExceptions
   */
  protected DLAExceptionHandler exceptionHandler =
      new DLAExceptionHandler();

  /**
   * An exception factory for generating DLAExceptions
   */
  private DLAExceptionFactory dlaExceptionFactory =
      new DLAExceptionFactory();

  /**
   * A DLASystemExit object for managing application exiting as specified
   * within the DLA standards
   */
  private DLASystemExit systemExit = new DLASystemExit();

  // The following constant definitions are exceptions thrown by this class
  private static final String InitException =
      DLAExceptionFactory.InitException;
  private static final String RunException =
      DLAExceptionFactory.RunException;
  private static final String PostProcessException =
      DLAExceptionFactory.PostProcessException;
  private static final String SQLStreamNotSupported =
      DLAExceptionFactory.SQLStreamNotSupported;
  private static final String PreProcessException =
      DLAExceptionFactory.PreProcessException;

  /**
   * @is a default constructor
   * @has nothing
   * @does instantiates the 'basic-needs' classes for performing
   * database loads in accordance with the DLA standards
   */
  public DLALoader() {
    try {
      this.logger = DLALogger.getInstance();
      DLALoaderCfg config = new DLALoaderCfg();
      String loadPrefix = config.getLoadPrefix();
      this.radarDBMgr = new SQLDataManager(new DatabaseCfg("RADAR"));
      this.radarDBMgr.setLogger(logger);
      this.mgdDBMgr = new SQLDataManager(new DatabaseCfg(loadPrefix));
      this.mgdDBMgr.setLogger(logger);
      this.radarBCPMgr = new BCPManager(new BCPManagerCfg("RADAR"));
      this.radarBCPMgr.setLogger(logger);
      this.radarBCPMgr.setSQLDataManager(radarDBMgr);
      this.mgdBCPMgr = new BCPManager(new BCPManagerCfg(loadPrefix));
      this.mgdBCPMgr.setLogger(logger);
      this.mgdBCPMgr.setSQLDataManager(mgdDBMgr);
      this.loadStream = createSQLStream(config.getLoadStreamName());
      this.qcStream = createSQLStream(config.getQCStreamName());
      this.inputConfig = new InputDataCfg();
    }
    catch (Exception e) {
      DLAException e2 = (DLAException)
          dlaExceptionFactory.getException(InitException);
      e2.setParent(e);
      DLAExceptionHandler.handleException(e2);
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
    try {
      logger.logdInfo("Performing load initialization",true);
      initialize();
    }
    catch (Exception e) {
      DLAException e2 = (DLAException)
          dlaExceptionFactory.getException(InitException, e);
      DLAExceptionHandler.handleException(e2);
      DLASystemExit.fatalExit();
    }
    try {
      logger.logdInfo("Performing load pre processing",true);
      preprocess();
    }
    catch (Exception e) {
      DLAException e2 = (DLAException)
          dlaExceptionFactory.getException(PreProcessException, e);
      DLAExceptionHandler.handleException(e2);
      DLASystemExit.fatalExit();
    }
    try {
      logger.logdInfo("Performing load processing",true);
      run();
    }
    catch (Exception e) {
      DLAException e2 = (DLAException)
          dlaExceptionFactory.getException(RunException, e);
      DLAExceptionHandler.handleException(e2);
      DLASystemExit.fatalExit();
    }
    try {
      logger.logdInfo("Performing post processing",true);
      postprocess();
      radarDBMgr.closeResources();
      mgdDBMgr.closeResources();
    }
    catch (Exception e) {
      DLAException e2 = (DLAException)
          dlaExceptionFactory.getException(PostProcessException, e);
      DLAExceptionHandler.handleException(e2);
      DLASystemExit.fatalExit();
    }
    logger.logdInfo("Load completed",true);
    logger.logpInfo("Load completed",false);
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
   * @return the new SQLStream
   */
  private SQLStream createSQLStream(String name) throws MGIException
  {
      if (name.equals("org.jax.mgi.shr.dbutils.dao.Inline_Stream"))
          return new Inline_Stream(this.mgdDBMgr);
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.Batch_Stream"))
          return new Batch_Stream(this.mgdDBMgr);
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.Script_Stream"))
          return new Script_Stream(this.mgdDBMgr.getScriptWriter());
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.BCP_Inline_Stream"))
          return new BCP_Inline_Stream(this.mgdDBMgr, this.mgdBCPMgr);
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.BCP_Batch_Stream"))
          return new BCP_Batch_Stream(this.mgdDBMgr, this.mgdBCPMgr);
      else if (name.equals("org.jax.mgi.shr.dbutils.dao.BCP_Script_Stream"))
          return new BCP_Script_Stream(this.mgdDBMgr.getScriptWriter(),
                                       this.mgdBCPMgr);
      else
      {
          DLAExceptionFactory factory = new DLAExceptionFactory();
          DLAException e =
              (DLAException)factory.getException(SQLStreamNotSupported);
          e.bind(name);
          throw e;
      }

  }

}
