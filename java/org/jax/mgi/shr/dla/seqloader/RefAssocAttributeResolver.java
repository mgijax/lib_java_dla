//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.dla.seqloader.RefAssocRawAttributes;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.dao.MGI_Reference_AssocState;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;

/**
 * @is An object that resolves a RefAssocRawAttributes object to a
 *  MGI_Reference_AssocState
 * @has
 *   <UL>
 *   <LI>Lookups to resolve attributes
 *   <LI>A MGI_Reference_AssocState
 *   <LI>A RefAssocRawAttributes
 *   <LI>A logical db for the reference
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Uses lookups to resolve raw reference association attributes
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class RefAssocAttributeResolver {
    // a lookup to resolve pubmed ids
    private AccessionLookup pubmedLookup;

    // a lookup to resolve medline ids
    private AccessionLookup medlineLookup;

    /**
     * Constructs a RefAssociationResolver object by creating the necessary
     * lookups
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @throws CacheException
     * @throws ConfigException
     * @throws TranslationException
     * @throws DBException
     */
    public RefAssocAttributeResolver ()
        throws DBException, ConfigException, CacheException {
        pubmedLookup = new AccessionLookup(LogicalDBConstants.PUBMED,
                                           MGITypeConstants.REF,
                                           AccessionLib.PREFERRED);
        medlineLookup = new AccessionLookup(LogicalDBConstants.MEDLINE,
                                           MGITypeConstants.REF,
                                           AccessionLib.PREFERRED);
    }

    /**
     * resolves RefAssocRawAttribute object to a MGI_Reference_AssocState
     * @assumes refLogicalDB is for medline or pubmed; returns null otherwise
     * @effects Nothing
     * @param raw A RefAssocRawAttributes object
     * @param objectKey The object key with which to associate the reference
     * @param refLogicalDb - logicalDB key for the reference
     * @return an MGI_ReferenceAssocState which may be null if the reference is
     *         not in MGI
     * @throws CacheException
     * @throws ConfigException
     * @throws DBException
     * @throws TranslationException
     * @throws KeyNotFoundException - doesn't actually throw this because the
     * lookup has an option to return null instead, and we are using this option.
     */

    public MGI_Reference_AssocState resolveAttributes(
        RefAssocRawAttributes raw, Integer objectKey, int refLogicalDB)
                throws KeyNotFoundException, DBException, CacheException {
        // the reference key with which to create a state
        Integer refKey = null;
        // the state we are building
        MGI_Reference_AssocState state = null;

        // if the logical db indicates a pubmed id try to resolve it
        if(refLogicalDB == LogicalDBConstants.PUBMED) {
            refKey = pubmedLookup.lookup(raw.getRefId());
        }

        // the logical db indicates a medline id try to resolve it
        else if(refLogicalDB == LogicalDBConstants.MEDLINE) {

            refKey = medlineLookup.lookup(raw.getRefId());
        }

        // if we were able to resolve a reference id create a state
        if(refKey != null) {
            state = new MGI_Reference_AssocState();
            state.setRefsKey(refKey);
            state.setObjectKey(objectKey);
            state.setMGITypeKey(raw.getmgiType());
            state.setRefAssocTypeKey(raw.getRefAssocType());
        }
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

