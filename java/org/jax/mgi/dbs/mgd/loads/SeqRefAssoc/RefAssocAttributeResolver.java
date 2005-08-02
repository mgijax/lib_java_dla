package org.jax.mgi.dbs.mgd.loads.SeqRefAssoc;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.JNumberLookup;
import org.jax.mgi.dbs.mgd.dao.MGI_Reference_AssocState;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;
//import org.jax.mgi.shr.dla.seqloader.*;

/**
 * An object that resolves a RefAssocRawAttributes object to a
 *  MGI_Reference_AssocState
 * @has
 *   <UL>
 *   <LI>Lookups to resolve raw attributes
 *   <LI>A RefAssocRawAttributes
 *   <LI>A MGI_Reference_AssocState
 *   <LI>PubMed, Medline, and JNumber AccessionLookups
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
    // a full cache lookup of pubmed ids
    private AccessionLookup pubmedLookup;

    // a full cache lookup of  medline ids
    private AccessionLookup medlineLookup;

    // a lookup to resolve mgi references
    private JNumberLookup jnumberLookup;

    /**
     * Constructs a RefAssociationResolver object by creating the necessary
     * lookups
     * @assumes Nothing
     * @effects Queries a database to load the lookup caches (full cache)
     * @throws CacheException if problem creating a lookup
     * @throws ConfigException if problem creating a lookup
     * @throws DBException if problem creating a lookup
     */
    public RefAssocAttributeResolver ()
        throws DBException, ConfigException, CacheException {
        pubmedLookup = new AccessionLookup(LogicalDBConstants.PUBMED,
                                           MGITypeConstants.REF,
                                           AccessionLib.PREFERRED);
        medlineLookup = new AccessionLookup(LogicalDBConstants.MEDLINE,
                                           MGITypeConstants.REF,
                                           AccessionLib.PREFERRED);
       jnumberLookup = new JNumberLookup();
    }

    /**
     * resolves RefAssocRawAttribute object to a MGI_Reference_AssocState
     * @assumes refLogicalDB is for medline, pubmed, or MGI;
     * return null MGI_Reference_AssocState otherwise
     * @effects Nothing
     * @param raw the RefAssocRawAttributes object to be resolved
     * @param objectKey The object key with which to associate the reference
     * @param refLogicalDB - logicalDB key for the reference
     * @return an MGI_ReferenceAssocState which may be null if the reference is
     *         not in MGI
     * @throws CacheException if error using lookup
     * @throws DBException if error using lookup
     * @throws KeyNotFoundException - doesn't actually throw this because the
     * lookup has an option to return null instead, and we are using this option.
     */

    public MGI_Reference_AssocState resolveAttributes(
                RefAssocRawAttributes raw,
                Integer objectKey,
                int refLogicalDB)
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
        // the logical db indicates a mgi jnumber
        else if(refLogicalDB == LogicalDBConstants.MGI) {
            refKey = jnumberLookup.lookup(raw.getRefId());
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

