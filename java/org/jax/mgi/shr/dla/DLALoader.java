package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.config.DatabaseCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.DLALoaderCfg;
import org.jax.mgi.shr.dbutils.BCPManager;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.exception.MGIException;

/**
 * <p>IS: a base class which implements the DLA standards for all loaders.<p>
 * <p>ABSTRACT: this class provides the instantiation of the 'basic needs'
 * objects for performing database loads such as SQLDataManagers, loggers,
 * BCPManagers, DLA exception handlers and factories. It also provides the
 * main method. Sub classes would be required to implement the following
 * methods:<br>
 * <UL>
 *   <LI>initialize - for initializing sub class instance variables
 *   <LI>run - for performing a specific load
 *   <LI>finale - for closing resources and other finalizations
 * </UL>
 * <p>HAS: a set of 'basic-needs' objects for doing DLA loads<br>
 * <UL>
 *   <LI>A DLALogger
 *   <LI>A DLAExceptionHandler
 *   <LI>A primary input file for loading into the database
 *   <LI>A SQLDataManager for the RADAR database
 *   <LI>A SQLdataManager for the MGD database
 *   <LI>A BCPManager for the RADAR database
 *   <LI>A BCPManager for the MGD database
 * </p>
 * <p>DOES: performs initialization of 'basic-needs' and instantiates
 * the subclass and calls the initialize(), run() and finale() methods
 * on the subclass.</p>
 * <pre>
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
   * The primary input data file for database loading
   */
  protected InputDataFile primaryInputFile;

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
  private static final String FinalizeException =
      DLAExceptionFactory.FinalizeException;
  private static final String InstanceException =
      DLAExceptionFactory.InstanceException;

  /**
   * <p>IS: a default constructor</p>
   * <p>HAS: nothing</p>
   * <p>DOES: instantiates the 'basic-needs' classes for performing
   * database loads in accordance with the DLA standards</p>
   */
  public DLALoader() {
    try {
      logger = DLALogger.getInstance();
      logger.logdInfo("Performing initialization",true);
      logger.logpInfo("Beginning load processing",true);
      radarDBMgr = new SQLDataManager(new DatabaseCfg("RADAR"));
      radarDBMgr.setLogger(logger);
      mgdDBMgr = new SQLDataManager(new DatabaseCfg("MGD"));
      mgdDBMgr.setLogger(logger);
      radarBCPMgr = new BCPManager();
      radarBCPMgr.setLogger(logger);
      radarBCPMgr.setSQLDataManager(radarDBMgr);
      mgdBCPMgr = new BCPManager();
      mgdBCPMgr.setLogger(logger);
      mgdBCPMgr.setSQLDataManager(mgdDBMgr);
      primaryInputFile = new InputDataFile();
      initialize();
    }
    catch (MGIException e) {
      DLAException e2 = (DLAException)
          dlaExceptionFactory.getException(InitException);
      e2.setParent(e);
      exceptionHandler.handleException(e2);
      systemExit.fatalExit();
    }
  }

  /**
   * <p>Purpose: The main routine which dynamically instantiates an instance
   * of the specific loader class and executes the load() method. See javadocs
   * for the load() method for further details.
   * <p>Assumes: a specific loader has been specified on the command line
   * or within a configuration file or java system properties.</p>
   * <p>Effects: the four dla standrad log files and records within the
   * RADAR and/or MGD database. If bcp is being used then bcp files may be
   * available if they were configured to remain after executing them.</p>
   * @param args command line argument specifying which loader to run. This
   * argument can be alternatively placed in the configuration file.
   */
  public static void main(String[] args) {
    DLAExceptionHandler eHandler = new DLAExceptionHandler();
    DLAExceptionFactory eFactory = new DLAExceptionFactory();
    String loaderName = null;
    DLALoader loader = null;
    if (args.length > 0)
      loaderName = args[0];
    if (loaderName == null) {
      try {
        DLALoaderCfg config = new DLALoaderCfg();
        loaderName = config.getLoaderClass();
        Class cls = Class.forName(loaderName);
        loader = (DLALoader)cls.newInstance();
      }
      catch (Exception e) {
        DLAException e2 = (DLAException)
            eFactory.getException(InstanceException, e);
        e2.setParent(e);
        eHandler.handleException(e2);
        DLASystemExit.fatalExit();
      }
    }
    loader.load();
    DLASystemExit.exit();
  }

  /**
   * <p>Purpose: executes the initialize(), run() and finale() methods
   * of the subclass loader and performs standard logging and
   * system exiting.</p>
   * <p>Assumes: nothing.</p>
   * <p>Effects: the four dla standrad log files and records within the
   * RADAR and/or MGD database. If bcp is being used then bcp files may be
   * available if they were configured to remain after executing them.</p>
   * @param args command line argument specifying which loader to run. This
   * argument can be placed in the configuration file instead.
   */
  public void load() {
    try {
      logger.logdInfo("Beginning load processing",true);
      run();
    }
    catch (MGIException e) {
      DLAException e2 = (DLAException)
          dlaExceptionFactory.getException(RunException);
      e2.setParent(e);
      exceptionHandler.handleException(e2);
      systemExit.fatalExit();
    }
    try {
      logger.logdInfo("Performing finalization",true);
      radarDBMgr.closeResources();
      mgdDBMgr.closeResources();
      finale();
    }
    catch (MGIException e) {
      DLAException e2 = (DLAException)
          dlaExceptionFactory.getException(FinalizeException);
      e2.setParent(e);
      exceptionHandler.handleException(e2);
      systemExit.fatalExit();
    }
    logger.logdInfo("Load completed",true);
    logger.logpInfo("Load completed",true);
  }

  /**
   * <p>Purpose: to initialize instance variable of the subclass</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: instance variables will be instantiated</p>
   * @throws MGIException if errors occur during initialization
   */
  protected abstract void initialize() throws MGIException;

  /**
   * <p>Purpose: to perform a database load into the RADAR and/or MGD
   * database</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: database records created within the RADAR and/or MGD
   * database and possible bcp files if doing bcp processing and the system
   * is configured to keep bcp files after executing them</p>
   * @throws MGIException throw if an error occurs while performing the
   * load</p>
   */
  protected abstract void run() throws MGIException;

  /**
   * <p>Purpose: to perform any finalization routines such as closing
   * resources opened during initialization</p>
   * <p>Assumes: nothing</p>
   * <p>Effects: all resources will be closed</p>
   * @throws MGIException throw if an error occurs while performing
   * finalization</p>
   */
  protected abstract void finale() throws MGIException;

}