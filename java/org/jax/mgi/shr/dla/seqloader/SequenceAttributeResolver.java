//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.config. ConfigException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.SequenceTypeKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.SequenceProviderKeyLookup;

import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.dbs.mgd.lookup.Translator;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;

/**
 * @is a class that defines an abstract resolveAttributes method to resolve a
 * SequenceRawAttributes object to a SEQ_SequenceState.
 * Provides lookups and common to all Sequence Attribute Resolvers
 * @has
 *   <UL>
 *   <LI> Sequence Type Lookup (uses a translator)
 *   <LI> Sequence Quality lookup
 *   <LI> User lookup
 *   <LI> Provider lookup (uses a translator)
 *   <LI>Sequence Status  Lookup
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Expects concrete subclasses to implement the resolveAttributes method
 *   <LI>Initializes lookups
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

abstract public class SequenceAttributeResolver {
    // typeLookup and providerLookup use a translator
    protected SequenceTypeKeyLookup typeLookup;
    protected VocabKeyLookup qualityLookup;
    protected SequenceProviderKeyLookup providerLookup;
    protected VocabKeyLookup statusLookup;

    /**
     * Constructs lookups common to all SequenceAttributeResolvers
     * @assumes Nothing
     * @effects queries a database
     * @param None
     * @throws TranslationException - If a translation error occurs in the type Lookup
     * @throws ConfigException - if there  is an error accessing the
     * configuration file
     * throws@ DBException - if there is an error accessing the database
     * throws@ CacheException - if there is an error with the
     * vocabulary cache
     */

    protected SequenceAttributeResolver() throws TranslationException,
        ConfigException, DBException, CacheException {

        typeLookup = new SequenceTypeKeyLookup();
        qualityLookup = new VocabKeyLookup(
            VocabularyTypeConstants.SEQUENCEQUALITY);
        providerLookup = new SequenceProviderKeyLookup();
        statusLookup = new VocabKeyLookup(
            VocabularyTypeConstants.SEQUENCESTATUS);
    }
    /**
     * Concrete subclasses implement this method to resolve a
     * SequenceRawAttributes to a SEQ_SequenceState
     * @assumes Nothing
     * @effects Nothing
     * @param rawAttributes A SequenceRawAttributes object
     * @return a SEQ_SequenceState object
     * @throws Nothing
     */

    public abstract SEQ_SequenceState resolveAttributes(
        SequenceRawAttributes rawAttributes) throws KeyNotFoundException,
        TranslationException, DBException, CacheException, ConfigException;
}

//  $Log$
//  Revision 1.1  2004/01/06 20:09:44  mbw
//  initial version imported from lib_java_seqloader
//
//  Revision 1.2  2003/12/20 16:25:21  sc
//  changes made from code review~
//
//  Revision 1.1  2003/12/08 18:40:44  sc
//  initial commit
//
//
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