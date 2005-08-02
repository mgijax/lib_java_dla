package org.jax.mgi.dbs.mgd.loads.SeqSrc;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An exception for not being able to resolve a molecular source attribute
 * to a key.
 * @has nothing
 * @does nothing
 * @company The Jackson Laboratory
 * @author sc
 *
 */

public class UnresolvedAttributeException extends MSException
{
  String unresolvedAttribute = null;

  public UnresolvedAttributeException(String attribute) {
    super("Could not resolve attribute value " + attribute, true);
    this.unresolvedAttribute = attribute;
  }

  public String getOrganism() {
    return this.unresolvedAttribute;
  }
}