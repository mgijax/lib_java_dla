//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.dla.seqloader.RefAssocRawAttributes;
import org.jax.mgi.shr.dla.seqloader.RefAssocAttributeResolver;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;

import org.jax.mgi.dbs.mgd.dao.MGI_Reference_AssocState;
import org.jax.mgi.shr.dla.seqloader.SeqRefAssocPair;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;

/**
 * @is An object that resolves raw MGI_Reference_Assoc attributes that knows
 *     how to resolve both PubMed and Medline ids
 * @has
 *   <UL>
 *   <LI>Lookups to resolve attributes
 *   <LI>A MGI_Reference_AssocState
 *   <LI>A RefAssocRawAttributes
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
     * Constructs a SeqRefAssocProcessor object by creating a resolver
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @throws CacheException
     * @throws ConfigException
     * @throws TranslationException
     * @throws DBException
     */
    public SeqRefAssocProcessor ()
        throws DBException, ConfigException, CacheException {
        resolver = new RefAssocAttributeResolver();
    }
    /**
     * Processes a set of pubmed and medline associations for a sequence reference
     * If no pubmed reference or if can't resolve pubmed then attempt to
     * resolve medline.
     * @assumes Nothing
     * @effects Nothing
     * @param seqRefAssocPair A pair of raw reference association attributes, one
     *        pubmed, one medline. One may be null.
     * @param objectKey The object key with which to associate the reference
     * @return MGI_ReferenceAssocState an object representing resolved reference
     *         association attributes. This object may be null.
     * @throws CacheException
     * @throws ConfigException
     * @throws DBException
     * @throws TranslationException
     * @throws KeyNotFoundException
     */

    public MGI_Reference_AssocState process(
        SeqRefAssocPair seqRefAssocPair, Integer objectKey)
        throws KeyNotFoundException, DBException, CacheException {
        // reset state
        state = null;

        // try to get the pubmed state first
        raw = seqRefAssocPair.getPubmed();
        if(raw != null) {
            // see if MGI has this reference, if not state=null;
            state = resolver.resolveAttributes(
                raw, objectKey, LogicalDBConstants.PUBMED);
        }
        // either no pubmed id or pubmed id not in MGI, so try to get the medline id
        if(state == null) {
            raw = seqRefAssocPair.getMedline();
            if(raw != null) {
                // see if MGI has this reference, if not state=null;
                state = resolver.resolveAttributes(
                    raw, objectKey, LogicalDBConstants.MEDLINE);

            }
        }
        // state could be null
        return state;
        }
    }

//  $Log

/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
