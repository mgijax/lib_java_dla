package org.jax.mgi.shr.dla.loader;

import org.jax.mgi.shr.exception.ExceptionFactory;


/**
 * An ExceptionFactory.
 * @has a hashmap of predefined DLAExceptions stored by a name key
 * @does looks up DLAExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DLALoaderExceptionFactory extends ExceptionFactory {

  public DLALoaderExceptionFactory() {
  }

  /**
   * Could not initialize the loader
   */
  public static final String InitException =
      "org.jax.mgi.shr.shrdbutils.InitException";
  static {
    exceptionsMap.put(InitException, new DLALoaderException(
        "Could not initialize loader", false));
  }

  /**
   * Error occurred while trying to run loader
   */
  public static final String RunException =
      "org.jax.mgi.shr.shrdbutils.RunException";
  static {
    exceptionsMap.put(RunException, new DLALoaderException(
        "Exception occured during loader run() method", false));
  }

  /**
   * Error occurred while trying to run loader
   */
  public static final String PreProcessException =
      "org.jax.mgi.shr.shrdbutils.PreProcessException";
  static {
    exceptionsMap.put(PreProcessException, new DLALoaderException(
        "Exception occured during pre processing", false));
  }

  /**
   * Error occurred while trying to run loader
   */
  public static final String PostProcessException =
      "org.jax.mgi.shr.shrdbutils.PostProcessException";
  static {
    exceptionsMap.put(PostProcessException, new DLALoaderException(
        "Exception occured during post processing", false));
  }

  /**
   * Could not instantiate a new DLALoader object from configuration
   */
  public static final String InstanceException =
      "org.jax.mgi.shr.shrdbutils.InstanceException";
  static {
    exceptionsMap.put(InstanceException, new DLALoaderException(
        "Could not instantiate a new DLALoader object from configuration",
        false));
  }

  /**
   * The named SQLStream is not supported
   */
  public static final String SQLStreamNotSupported =
      "org.jax.mgi.shr.shrdbutils.SQLStreamNotSupported";
  static {
    exceptionsMap.put(SQLStreamNotSupported, new DLALoaderException(
        "The SQLStream ?? is not supported",
        false));
  }



}