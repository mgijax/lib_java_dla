package org.jax.mgi.dbs.mgd.MolecularSource;

import org.jax.mgi.shr.exception.MGIException;

/**
 * <p>@is </p>
 * <p>@has </p>
 * <p>@does </p>
 * <p>@company The Jackson Laboratory</p>
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