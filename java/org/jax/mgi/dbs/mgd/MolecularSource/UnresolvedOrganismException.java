package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.shr.exception.MGIException;

/**
 * is an exception for not being able to resolve an organism name to an
 * organism key.
 * @has nothing
 * @does nothing
 * @company The Jackson Laboratory
 * @author not attributable
 *
 */

public class UnresolvedOrganismException extends MSException
{
    String unresolvedOrganism = null;

    public UnresolvedOrganismException(String organism)
    {
        super("Could not resolve organism value " + organism, true);
        this.unresolvedOrganism = organism;
    }

    public String getOrganism()
    {
        return this.unresolvedOrganism;
    }

}