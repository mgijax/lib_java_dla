package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * <p>IS: An ExceptionFactory.</p>
 * <p>HAS: a hashmap of predefined DLAExceptions stored by a name key</p>
 * <p>DOES: looks up DLAExceptions by name</p>
 * <p>Company: The Jackson Laboratory</p>
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
        "Exception occured while cleaning up loader resources", false));
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


}