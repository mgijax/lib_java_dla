package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * @is An ExceptionFactory.
 * @has a hashmap of predefined MSExceptions stored by a name key
 * @does looks up MSExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class MSExceptionFactory extends ExceptionFactory {

  /**
   * an error occurred while trying to instantiate a MSAttrResolver
   */
  public static final String MSAttrResolverInitErr =
      "org.jax.mgi.dbs.mgd.MSAttrResolverInitErr";
  static {
    exceptionsMap.put(MSAttrResolverInitErr, new MSException(
        "Could not instantiate a new MSAttrResolver due to " +
        "a resource error", false));
  }
  /**
   * an error occurred while trying to instantiate a MSResolver
   */
  public static final String MSResolverInitErr =
      "org.jax.mgi.dbs.mgd.MSResolverInitErr";
  static {
    exceptionsMap.put(MSResolverInitErr, new MSException(
        "Could not instantiate a new MSResolver due to " +
        "a resource error", false));
  }
  /**
   * a resource error was thrown from a Lookup class
   */
  public static final String LookupErr =
      "org.jax.mgi.dbs.mgd.LookupErr";
  static {
    exceptionsMap.put(LookupErr, new MSException(
        "A resource error was thrown from an instance of the lookup " +
        "class ?? ", false));
  }
  /**
   * an error occurred while trying to resolve an attribute
   */
  public static final String AttrResolveErr =
      "org.jax.mgi.dbs.mgd.AttrResolveErr";
  static {
    exceptionsMap.put(AttrResolveErr, new MSException(
        "Could not resolve attrubute ?? due to application error.", false));
  }
  /**
   * organism raw attribute was found to be null
   */
  public static final String NullOrganism =
      "org.jax.mgi.dbs.mgd.NullOrganism";
  static {
    exceptionsMap.put(NullOrganism, new MSException(
        "Organism raw attribute was found to be null.", false));
  }
  /**
   * an error occurred while trying to resolve an MolecularSource
   */
  public static final String ResolveErr =
      "org.jax.mgi.dbs.mgd.ResolveErr";
  static {
    exceptionsMap.put(ResolveErr, new MSException(
        "Could not resolve the following MolecularSource attributes due " +
        "to a resource error: ??", false));
  }
  /**
   * an error occurred while trying to resolve an MolecularSource
   */
  public static final String KeyErr =
      "org.jax.mgi.dbs.mgd.KeyErr";
  static {
    exceptionsMap.put(KeyErr, new MSException(
        "Could not obtain a new primary key value for a Molecular Source due " +
        "to a resource error", false));
  }
  /**
   * too many rows were returned on a query
   */
  public static final String TooManyRows =
      "org.jax.mgi.dbs.mgd.TooManyRows";
  static {
    exceptionsMap.put(TooManyRows, new MSException(
        "More rows than the allocated limit of ?? were returned from the " +
        "following query:\n ??", false));
  }

  /**
   * could not add the molecular source object to the SQLStream
   */
  public static final String SQLStreamErr =
      "org.jax.mgi.dbs.mgd.SQLStreamErr";
  static {
    exceptionsMap.put(SQLStreamErr, new MSException(
        "Could not add the following MolecularSource to an SQLStream: " +
        "\n ??", false));
  }

  /**
   * the MolecularSource object is being added to an SQLStream but it has
   * already been added to the stream
   */
  public static final String AlreadyOnSQLStream =
      "org.jax.mgi.dbs.mgd.AlreadyOnSQLStream";
  static {
    exceptionsMap.put(AlreadyOnSQLStream, new MSException(
        "Could not add the following MolecularSource to an SQLStream " +
        "because is has already been previously added:\n ??", false));
  }

  /**
   * the MolecularSource object is being inserted onto an SQLStream but
   * it already exist within the database
   */
  public static final String AlreadyInDatabase =
      "org.jax.mgi.dbs.mgd.AlreadyInDatabase";
  static {
    exceptionsMap.put(AlreadyInDatabase, new MSException(
        "Could not add the following MolecularSource to an SQLStream " +
        "because it already exists within the database:\n ??", false));
  }

  /**
   * There was an error accessing a resource when looking up whether
   * or not the MolecularSource attribute had been curator edited
   */
  public static final String AttrHistoryErr =
      "org.jax.mgi.dbs.mgd.AttrHistoryErr";
  static {
    exceptionsMap.put(AttrHistoryErr, new MSException(
        "Could not establish whether the attribute ?? was curator " +
        "edited due to a resource error", false));
  }
  /**
   * an error occurred while trying to add a MolecularSource to the cache
   */
  public static final String CacheErr =
      "org.jax.mgi.dbs.mgd.CacheErr";
  static {
    exceptionsMap.put(CacheErr, new MSException(
        "Could not add the following MolecularSource to the cache due " +
        "to a resource error: ??", false));
  }
  /**
   * no source was found for the given sequence and organism
   */
  public static final String NoSourceFound =
      "org.jax.mgi.dbs.mgd.NoSourceFound";
  static {
    exceptionsMap.put(NoSourceFound, new MSException(
        "Could not find molecular source for existing sequence with " +
        "accid = ?? and organism key = ??", false));
  }

  /**
   * no key has been assigned to the MolecularSource object and a call to
   * update() was made
   */
  public static final String NoKeyFound =
      "org.jax.mgi.dbs.mgd.NoKeyFound";
  static {
    exceptionsMap.put(NoKeyFound, new MSException(
        "The MolecularSource could not be updated since it has no key " +
        "assigned for the given state:\n ??", false));
  }

  /**
   * could not add qc dao object to stream
   */
  public static final String QCErr =
      "org.jax.mgi.dbs.mgd.QCErr";
  static {
    exceptionsMap.put(QCErr, new MSException(
        "Could not add a new qc item to the qc reporting table named ??",
        false));
  }

  /**
   * could not access the configuration file
   */
  public static final String ConfigErr =
      "org.jax.mgi.dbs.mgd.ConfigErr";
  static {
    exceptionsMap.put(ConfigErr, new MSException(
        "The MSProcessor could not access the configuration file",
        false));
  }









}