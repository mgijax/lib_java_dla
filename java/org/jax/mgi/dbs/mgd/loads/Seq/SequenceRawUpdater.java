package org.jax.mgi.dbs.mgd.loads.Seq;

import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.dao.SEQ_Sequence_RawState;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.lookup.VocabTermLookup;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.hist.Seq_SequenceAttrHistory;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;

import java.sql.Timestamp;

/**
 * An object that updates a SEQ_SequenceState representing an existing sequence
 *     and an MGI_AttributeHistoryState object representing the sequence type
 *     attribute history in preparation for a database update based on input
 *     sequence values
 * @has
 *   <UL>
 *   <LI> A SeqAttrHistory object for determining if updates allowed on sequence
 *        type
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>Updates a SEQ_SequenceState representing an existing sequence from
 *        a SEQ_SequenceState representing the updated sequence. Knows which
 *        attributes may be updated.
 *   <LI>Updates a MGI_AttributeHistoryState representing the sequence type
 *       attributes history
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SequenceRawUpdater {
    // object to determine the attribute History of a SEQ_Sequence
    // presently history is tracked for sequence type
    Seq_SequenceAttrHistory attrHistory;

    // an instance of the load logger
    DLALogger logger;

    // lookup a logicalDB key - this is a full cache lookup
    LogicalDBLookup logicalDBLookup;

    // the logicalDB key for this load
    Integer logicalDB;

    // to lookup sequence status by key
    private VocabTermLookup termNameLookup;

    // configurator for the sequence load
    private SequenceLoadCfg loadCfg;

    // the singleton instance of the SequenceUpdater. It is returned by
     // the getInstance() method
     private static SequenceRawUpdater instance = null;

     /**
       * get the singleton instance of the SequenceUpdater.
       * @assumes nothing
       * @effects the Singleton instance is created if it didnt already
       * exist and the configuration files and system properties are read into
       * memory
       * @return a reference to the ConfigurationManagement instance
       * @throws DBException if error creating instance of this class
       * @throws ConfigException  if error creating instance of this class
       * @throws DLALoggingException if error creating instance of this class
       * @throws KeyNotFoundException if error creating instance of this class
       * @throws CacheException if error creating instance of this class
       */
      protected static SequenceRawUpdater getInstance()
          throws DBException, DLALoggingException, ConfigException,
            KeyNotFoundException, CacheException {
        if (instance == null) {
          instance = new SequenceRawUpdater();
        }
        return instance;
      }

    /**
     * constructs a SequenceUpdater
     * @assumes Nothing
     * @effects Queries a database
     * @throws DBException if error creating Seq_SequenceAttrHistory,
     * or a LogicalDBLookup object
     * @throws ConfigException if error creating a SequenceLoadCfg object,
     *         a Seq_SequenceAttrHistory object, a LogicalDBLookup, or getting
     *         the logicalDB from the SequenceLoadCfg object
     * @throws CacheException if error creating a LogicalDBLookup
     * @throws KeyNotFoundException if logicalDB is not configured
     * @throws DLALoggingException if error getting instance of a logger
     */

    private SequenceRawUpdater()
        throws DBException,  DLALoggingException, ConfigException,
            KeyNotFoundException, CacheException {
        attrHistory = new Seq_SequenceAttrHistory();
        logger = DLALogger.getInstance();
        loadCfg = new SequenceLoadCfg();
        logicalDBLookup = new LogicalDBLookup();
        logicalDB = logicalDBLookup.lookup(loadCfg.getLogicalDB());
        termNameLookup = new VocabTermLookup();
    }

    /**
     * updates the attributes of a SEQ_SequenceState representing an existing
     * SEQ_Sequence from a SEQ_SequenceState representing the updated sequence.
     * @assumes Nothing
     * @effects Queries a database to determine type attributeHistory
     * @param existingSeqState SEQ_SequenceState representing an existing sequence
     * @param existingSeqKey SEQ_SequenceKey of the existing sequence
     * @param inputSeqState SEQ_SequenceState representing the updated sequence
     * @return true if any sequence attributes were updated in 'existingSeqState'
     * @throws DBException if error querying the database for attribute history
     * @throws CacheException if error using a lookup
     */

    public boolean updateSeq(SEQ_Sequence_RawState existingSeqState,
                             Integer existingSeqKey,
                             SEQ_Sequence_RawState inputSeqState)
      throws DBException, CacheException {

        boolean update = false;
        // get updateable input sequence attributes
        String inputSeqRawType = inputSeqState.getRawType();
        String inputSeqRawLibrary = inputSeqState.getRawLibrary();
        String inputSeqRawOrganism = inputSeqState.getRawOrganism();
        String inputSeqRawStrain = inputSeqState.getRawStrain();
        String inputSeqRawTissue = inputSeqState.getRawTissue();
        String inputSeqRawAge = inputSeqState.getRawAge();
        String inputSeqRawSex = inputSeqState.getRawSex();
        String inputSeqRawCellLine = inputSeqState.getRawCellLine();

        // get updateable existing sequence attributes
        String existingSeqRawType = existingSeqState.getRawType();
        String existingSeqRawLibrary = existingSeqState.getRawLibrary();
        String existingSeqRawOrganism = existingSeqState.getRawOrganism();
        String existingSeqRawStrain = existingSeqState.getRawStrain();
        String existingSeqRawTissue = existingSeqState.getRawTissue();
        String existingSeqRawAge = existingSeqState.getRawAge();
        String existingSeqRawSex = existingSeqState.getRawSex();
        String existingSeqRawCellLine = existingSeqState.getRawCellLine();


        // update raw sequence types. Check for null; schema supports null rawType
        // if either rawType is null we don't want to do a String compare
        if (inputSeqRawType == null || existingSeqRawType == null) {
              // if just one is null - update
              if ( ! (inputSeqRawType == null && existingSeqRawType == null) ) {
                  existingSeqState.setRawType(inputSeqRawType);
                  update = true;
              }
        }
        // Do a string compare, if not equal - update
        else {
            if ( ! inputSeqRawType.equals(existingSeqRawType) ) {
              existingSeqState.setRawType(inputSeqRawType);
              update = true;
            }
        }

        // update raw library. Check for null; schema supports null rawLibrary
        // if either library is null we don't want to do a String compare
        if (inputSeqRawLibrary == null || existingSeqRawLibrary == null) {
              // if just one is null - update
              if ( ! (inputSeqRawLibrary == null && existingSeqRawLibrary == null) ) {
                  existingSeqState.setRawLibrary(inputSeqRawLibrary);
                  update = true;
              }
        }
        // Do a string compare, if not equal - update
        else {
            if ( ! inputSeqRawLibrary.equals(existingSeqRawLibrary) ) {
              existingSeqState.setRawLibrary(inputSeqRawLibrary);
              update = true;
            }
        }

        // update raw strain. Check for null; schema supports null rawStrain
        // if either strain is null we don't want to do a String compare
        if (inputSeqRawStrain == null || existingSeqRawStrain == null) {
              // if just one is null - update
              if ( ! (inputSeqRawStrain == null && existingSeqRawStrain == null) ) {
                  existingSeqState.setRawStrain(inputSeqRawStrain);
                  update = true;
              }
        }
        // Do a string compare, if not equal - update
        else {
            if ( ! inputSeqRawStrain.equals(existingSeqRawStrain) ) {
              existingSeqState.setRawStrain(inputSeqRawStrain);
              update = true;
            }
        }

        // update raw tissue. Check for null; schema supports null rawTissue
        // if either tissue is null we don't want to do a String compare
        if (inputSeqRawTissue == null || existingSeqRawTissue == null) {
              // if just one is null - update
              if ( ! (inputSeqRawTissue == null && existingSeqRawTissue == null) ) {
                  existingSeqState.setRawTissue(inputSeqRawTissue);
                  update = true;
              }
        }
        // Do a string compare, if not equal - update
        else {
            if ( ! inputSeqRawTissue.equals(existingSeqRawTissue) ) {
              existingSeqState.setRawTissue(inputSeqRawTissue);
              update = true;
            }
        }

            // update raw age. Check for null; schema supports null rawAge
            // if either age is null we don't want to do a String compare
            if (inputSeqRawAge == null || existingSeqRawAge == null) {
                  // if just one is null - update
                  if ( ! (inputSeqRawAge == null && existingSeqRawAge == null) ) {
                      existingSeqState.setRawAge(inputSeqRawAge);
                      update = true;
                  }
            }
            // Do a string compare, if not equal - update
            else {
                if ( ! inputSeqRawAge.equals(existingSeqRawAge) ) {
                  existingSeqState.setRawAge(inputSeqRawAge);
                  update = true;
                }
            }

            // update raw sex. Check for null; schema supports null rawSex
            // if either sex is null we don't want to do a String compare
            if (inputSeqRawSex == null || existingSeqRawSex == null) {
                  // if just one is null - update
                  if ( ! (inputSeqRawSex == null && existingSeqRawSex == null) ) {
                      existingSeqState.setRawSex(inputSeqRawSex);
                      update = true;
                  }
            }
            // Do a string compare, if not equal - update
            else {
                if ( ! inputSeqRawSex.equals(existingSeqRawSex) ) {
                  existingSeqState.setRawSex(inputSeqRawSex);
                  update = true;
                }
            }

            // update raw cellLine. Check for null; schema supports null rawCellLine
            // if either cellLine is null we don't want to do a String compare
            if (inputSeqRawCellLine == null || existingSeqRawCellLine == null) {
                  // if just one is null - update
                  if ( ! (inputSeqRawCellLine == null && existingSeqRawCellLine == null) ) {
                      existingSeqState.setRawCellLine(inputSeqRawCellLine);
                      update = true;
                  }
            }
            // Do a string compare, if not equal - update
            else {
                if ( ! inputSeqRawCellLine.equals(existingSeqRawCellLine) ) {
                  existingSeqState.setRawCellLine(inputSeqRawCellLine);
                  update = true;
                }
            }

        return update;
    }
}
// $Log
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


