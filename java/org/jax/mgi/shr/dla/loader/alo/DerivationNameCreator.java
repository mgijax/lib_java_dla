package org.jax.mgi.shr.dla.loader.alo;

import org.jax.mgi.dbs.mgd.loads.Alo.Derivation;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;


/**
 * An interface which defines the method to create a Derivation name
 * from a Derivation object
 * @has nothing
 * @does provides an interface for creating a Derivation Name
 * @author sc
 */
public interface DerivationNameCreator {
    public String create(Derivation Derivation)
	throws DerivationNameCreatorException, CacheException, DBException;
}
