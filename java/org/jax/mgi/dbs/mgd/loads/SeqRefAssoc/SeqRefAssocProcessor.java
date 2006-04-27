package org.jax.mgi.dbs.mgd.loads.SeqRefAssoc;

import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.dao.MGI_Reference_AssocState;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;



/**
 * An object that resolves PubMed and MedLine RefAssocRawAttributes
 *     to a MGI_Reference_AssocState
 * @has
 *   <UL>
 *   <LI>Lookups to resolve attributes
 *   <LI>A RefAssocRawAttributes
 *   <LI>A MGI_Reference_AssocState
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Uses lookups to resolve raw reference association attributes
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqRefAssocProcessor {
    // resolves reference associations
    private RefAssocAttributeResolver resolver;

    // a raw reference association to resolve
    private RefAssocRawAttributes raw;

    // a resolved reference assoc
    private MGI_Reference_AssocState state;

    /**
     * Constructs a SeqRefAssocProcessor object
     * @assumes Nothing
     * @effects Queries a database
     * @throws CacheException if error creating RefAssocAttributeResolver
     * @throws ConfigException if error creating RefAssocAttributeResolver
     * @throws DBException if error creating RefAssocAttributeResolver
     */
    public SeqRefAssocProcessor()
        throws DBException, ConfigException, CacheException
    {
        resolver = new RefAssocAttributeResolver();
    }

    /**
     * Processes a set of pubmed and medline associations for a sequence reference
     * By first resolving the pubmed reference, then the medline reference if
     * pubmed does not resolve.
     * @assumes Nothing
     * @effects Nothing
     * @param seqRefAssocPair A pair of RefAssocRawAttributes -  one
     *        pubmed, one medline. One may be null.
     * @param objectKey The sequence key with which to associate the reference
     * @return MGI_ReferenceAssocState an object representing resolved reference
     *         association attributes. This object is null if no references
     *         were resolved.
     * @throws CacheException if error resolving references
     * @throws DBException if error resolving references
     * @throws KeyNotFoundException if error resolving references
     */

    public MGI_Reference_AssocState process(
                SeqRefAssocPair seqRefAssocPair,
                Integer objectKey)
        throws KeyNotFoundException, DBException, CacheException
    {
        // reset state
        state = null;

        // try to get the pubmed state first
        raw = seqRefAssocPair.getPubmed();
        if (raw != null)
        {
            // see if MGI has this reference, if not state=null;
            state = resolver.resolveAttributes(
                raw, objectKey, LogicalDBConstants.PUBMED);
        }
        // either no pubmed id or pubmed id not in MGI, so try to get the medline id
        if (state == null)
        {
            raw = seqRefAssocPair.getMedline();
            if (raw != null)
            {
                // see if MGI has this reference, if not state=null;
                state = resolver.resolveAttributes(
                    raw, objectKey, LogicalDBConstants.MEDLINE);

            }
        }
        // state could be null
        return state;
    }

    /**
     * Processes a jnumber association for a sequence reference
     * @assumes Nothing
     * @effects Nothing
     * @param rawRef raw reference association attributes
     * @param objectKey The object key with which to associate the reference
     * @return MGI_ReferenceAssocState an object representing resolved reference
     *         association attributes. This object may be null.
     * @throws CacheException if error resolving references
     * @throws DBException if error resolving references
     * @throws KeyNotFoundException if error resolving references
     */
    public MGI_Reference_AssocState process(
        RefAssocRawAttributes rawRef, Integer objectKey)
        throws KeyNotFoundException, DBException, CacheException
    {
        // reset state
        state = null;
        if (rawRef != null)
        {
            // see if MGI has this reference, if not state=null;
            state = resolver.resolveAttributes(
                rawRef, objectKey, LogicalDBConstants.MGI);
        }
        // state could be null
        return state;
    }
}

