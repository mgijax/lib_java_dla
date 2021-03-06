package org.jax.mgi.shr.dla.loader;

import org.jax.mgi.shr.config.DLALoaderCfg;
import org.jax.mgi.shr.exception.MGIException;



/**
 * A class which can be invoked on the command line to start a DLALoader.
 * @has nothing
 * @does starts the given DLALoader by instantiating the class and running the
 * load() method
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public class DLAStart
{

    // The following constant definitions are exceptions thrown by this class
    private static final String InstanceException =
      DLALoaderExceptionFactory.InstanceException;

    /**
     * The main routine which dynamically instantiates an instance
     * of the configured loader class and executes the load() method.
     * @assumes a specific loader has been specified on the command line
     * or within a configuration file or java system properties.
     * @effects the four dla standrad log files and records within the
     * RADAR and/or MGD database. If bcp is being used then bcp files may be
     * available if they were configured to remain after executing them.
     * @param args command line argument specifying which loader to run. This
     * argument can be alternatively placed in the configuration file.
     */
    public static void main(String[] args) throws MGIException {
      DLALoaderExceptionFactory eFactory = new DLALoaderExceptionFactory();
      DLALoader loader = null;
      try
      {
          DLALoaderCfg cfg = new DLALoaderCfg();
          loader = (DLALoader)cfg.getLoaderClass();
      }
      catch (Exception e)
      {
          DLALoaderException e2 = (DLALoaderException)
              eFactory.getException(InstanceException, e);
          DLALoaderExceptionHandler.handleException(e2);
          DLASystemExit.fatalExit();
      }
      try
      {
          loader.load();
      }
      catch (DLALoaderException e)
      {
          DLALoaderExceptionHandler.handleException(e);
          DLASystemExit.fatalExit();
      }
      DLASystemExit.exit();
    }


}