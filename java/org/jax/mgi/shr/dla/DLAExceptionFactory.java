package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * @is An ExceptionFactory.
 * @has a hashmap of predefined DLAExceptions stored by a name key
 * @does looks up DLAExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLAExceptionFactory extends ExceptionFactory {

  public DLAExceptionFactory() {
  }

  /**
   * Could not initialize the loader
   */
  public static final String InitException =
      "org.jax.mgi.shr.shrdbutils.InitException";
  static {
    exceptionsMap.put(InitException, new DLAException(
        "Could not initialize loader", false));
  }

  /**
   * Error occurred while trying to run loader
   */
  public static final String RunException =
      "org.jax.mgi.shr.shrdbutils.RunException";
  static {
    exceptionsMap.put(RunException, new DLAException(
        "Exception occured during loader run() method", false));
  }

  /**
   * Error occurred while trying to run loader
   */
  public static final String FinalizeException =
      "org.jax.mgi.shr.shrdbutils.FinalizeException";
  static {
    exceptionsMap.put(FinalizeException, new DLAException(
        "Exception occured during post processing", false));
  }

  /**
   * Could not instantiate a new DLALoader object from configuration
   */
  public static final String InstanceException =
      "org.jax.mgi.shr.shrdbutils.InstanceException";
  static {
    exceptionsMap.put(InstanceException, new DLAException(
        "Could not instantiate a new DLALoader object from configuration",
        false));
  }

  /**
   * The named SQLStream is not supported
   */
  public static final String SQLStreamNotSupported =
      "org.jax.mgi.shr.shrdbutils.SQLStreamNotSupported";
  static {
    exceptionsMap.put(SQLStreamNotSupported, new DLAException(
        "The SQLStream ?? is not supported",
        false));
  }



}