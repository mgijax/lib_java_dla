//  $Header$
//  $Name$

package org.jax.mgi.dbs.mgd.loads.Seq;

import org.jax.mgi.shr.config. ConfigException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.SequenceTypeKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.SequenceProviderKeyLookup;

import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;


 /**
 * An object that resolves a SequenceRawAttributes to a SEQ_SequenceState
 * @has
 *   <UL>
 *   <LI> Sequence Type Lookup (uses a translator)
 *   <LI> Sequence Quality lookup
 *   <LI> Provider lookup (uses a translator)
 *   <LI> Sequence Status  Lookup
 *   <LI> A SEQ_SequenceState
 *   <LI> A SequenceRawAttributes
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Resolves a SequenceRawAttributes object to a SEQ_SequenceState
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SequenceAttributeResolver {
    // typeLookup and providerLookup use a translator
    // all are full cached lookups
    protected SequenceTypeKeyLookup typeLookup;
    protected VocabKeyLookup qualityLookup;
    protected SequenceProviderKeyLookup providerLookup;
    protected VocabKeyLookup statusLookup;

     /**
     * Constructs a SequenceAttributeResolver
     * @assumes Nothing
     * @effects queries a database to load each lookup cache
     * @throws TranslationException - if error creating type or provider lookups
     *   (these lookups have translators)
     * @throws ConfigException - if there a configuration error creating a lookup
     * @throws DBException - if there is a database error creating a lookup
     * @throws CacheException - if there is a caching error creating a lookup
     */

    public SequenceAttributeResolver() throws TranslationException,
        ConfigException, DBException, CacheException {

        typeLookup = new SequenceTypeKeyLookup();
        qualityLookup = new VocabKeyLookup(
            VocabularyTypeConstants.SEQUENCEQUALITY);
        providerLookup = new SequenceProviderKeyLookup();
        statusLookup = new VocabKeyLookup(
            VocabularyTypeConstants.SEQUENCESTATUS);
    }

    /**
      * resolves a SequenceRawAttributes object to a SEQ_SequenceState
      * @assumes Nothing
      * @effects Nothing
      * @param rawAttributes the SequenceRawAttributes object to resolve
      * @return sequenceState A SEQ_SequenceState
      * @throws KeyNotFoundException if any of the lookups fail to find a key
      * @throws TranslationException if type or provider lookups have errors using
      * their translators
      * @throws DBException - since these lookups are full cache this exception
      *     is not thrown
      * @throws CacheException if error doing lookup
      * @throws ConfigException - if error doing lookup
      *
      */
    public SEQ_SequenceState resolveAttributes(
        SequenceRawAttributes rawAttributes) throws KeyNotFoundException,
        TranslationException, DBException, CacheException, ConfigException {
      // the state we are building
      SEQ_SequenceState state = new SEQ_SequenceState();

      //////////////////////////////////
      // lookup all the foreign keys  //
      //////////////////////////////////

      // set the foreign keys
      state.setSequenceTypeKey(typeLookup.lookup(rawAttributes.getType()));
      state.setSequenceQualityKey(qualityLookup.lookup(rawAttributes.getQuality()));
      state.setSequenceStatusKey(statusLookup.lookup(rawAttributes.getStatus()));
      state.setSequenceProviderKey(providerLookup.lookup(rawAttributes.getProvider()));
      //
      // cleanse raw organisms
      // Needed for sequences that may have >1 organism e.g. SwissProt
      String organisms = rawAttributes.getRawOrganisms();
      if (organisms != null) {
          organisms = organisms.replaceAll("'", "''");
          if (organisms.length() > 255) {
              organisms = organisms.substring(0,254);
          }
      }
      state.setRawOrganism(organisms);

      //
      // cleanse decription data
      //
      String desc = rawAttributes.getDescription();
      if (desc != null) {
        desc = desc.replaceAll("'", "''");
        if (desc.length() > 255) {
          desc = desc.substring(0, 254);
        }
      }
      state.setDescription(desc);
      //
      // cleanse library
      //
      String lib = rawAttributes.getLibrary();
      if (lib != null) {
        lib = lib.replaceAll("'", "''");
        if (lib.length() > 255) {
          lib = lib.substring(0, 254);
        }
      }
      state.setRawLibrary(lib);
      //
      // cleanse strain
      //
      String strain = rawAttributes.getStrain();
      if (strain != null) {
        strain = strain.replaceAll("'", "''");
        if (strain.length() > 255) {
          strain = strain.substring(0, 254);
        }
      }
      state.setRawStrain(strain);
      //
      // cleanse tissue
      //
      String tissue = rawAttributes.getTissue();
      if (tissue != null) {
        tissue = tissue.replaceAll("'", "''");
        if (tissue.length() > 255) {
          tissue = tissue.substring(0, 254);
        }
      }
      state.setRawTissue(tissue);
      //
      // cleanse age
      //
      String age = rawAttributes.getAge();
      if (age != null) {
        age = age.replaceAll("'", "''");
        if (age.length() > 100) {
          age = age.substring(0, 99);
        }
      }
      state.setRawAge(age);
      //
      // cleanse sex
      //
      String sex = rawAttributes.getSex();
      if(sex != null) {
        sex = sex.replaceAll("'", "''");
        if (sex.length() > 100) {
          sex = sex.substring(0, 99);
        }
      }
      state.setRawSex(sex);
      //
      // cleanse cell line
      //
      String cell = rawAttributes.getCellLine();
      if(cell != null) {
        cell = cell.replaceAll("'", "''");
        if (cell.length() > 100) {
          cell = cell.substring(0, 99);
        }
      }
      state.setRawCellLine(cell);

      // copy remaining raw attributes to the sequence state
      state.setLength(new Integer(rawAttributes.getLength()));
      state.setVersion(rawAttributes.getVersion());
      state.setDivision(rawAttributes.getDivision());
      state.setVirtual(rawAttributes.getVirtual());
      state.setRawType(rawAttributes.getType());
      state.setNumberOfOrganisms(new Integer(rawAttributes.getNumberOfOrganisms()));
      state.setSeqrecordDate(rawAttributes.getSeqRecDate());
      state.setSequenceDate(rawAttributes.getSeqDate());
      return state;

    }
}

//  $Log$
//  Revision 1.1.2.1  2004/11/05 16:10:15  mbw
//  classes were renamed and reloacated as part of large refactoring effort (see tr6047)
//
//  Revision 1.11  2004/10/13 12:11:02  sc
//  added code to truncate length of raw organism value
//
//  Revision 1.10.2.1  2004/07/27 18:24:51  sc
//  Changed RecordDataIterator to interface DataIterator. run() method catch block now tests MSException to see if it is an instance of UnresolvedAttributeException - new exception for assembly load as all source attributes must resolve; other seqloads simply skip the sequence and reportjava/org/jax/mgi/shr/dla/seqloader/SeqLoader.java
//
//  Revision 1.10  2004/07/08 15:03:49  sc
//  javdocs changes
//
//  Revision 1.9  2004/06/30 19:35:01  mbw
//  javadocs only
//
//  Revision 1.8  2004/06/30 17:25:36  sc
//  merging sc2 branch to trunk
//
//  Revision 1.7.4.1  2004/05/18 15:32:48  sc
//  updated class/method headers
//
//  Revision 1.7  2004/04/26 12:21:08  sc
//  Added code to truncate all attributes to table length and escape single quotes
//
//  Revision 1.6  2004/04/14 17:08:24  mbw
//  added functionality to cleanse decscription data of single quotes and of lengths that are greater than 255
//
//  Revision 1.4  2004/03/29 17:20:12  sc
//  no longer abstract - this class now doe the interpreting
//
//  Revision 1.3  2004/02/25 21:42:40  mbw
//  fixed compiler warnings only
//
//  Revision 1.2  2004/02/17 15:19:02  sc
//  Changed to Specific Lookups for SequenceType and SeuqenceProvider, new package import for TranslationException
//
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