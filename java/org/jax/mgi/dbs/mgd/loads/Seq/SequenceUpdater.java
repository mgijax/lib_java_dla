// $Header
// $Name

package org.jax.mgi.dbs.mgd.loads.Seq;

import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
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

public class SequenceUpdater {
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
     private static SequenceUpdater instance = null;

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
      protected static SequenceUpdater getInstance()
          throws DBException, DLALoggingException, ConfigException,
            KeyNotFoundException, CacheException {
        if (instance == null) {
          instance = new SequenceUpdater();
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

    private SequenceUpdater()
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

    public boolean updateSeq(SEQ_SequenceState existingSeqState,
                             Integer existingSeqKey,
                             SEQ_SequenceState inputSeqState)
      throws DBException, CacheException {

        boolean update = false;
        // get updateable input sequence attributes
        Integer inputSeqTypeKey = inputSeqState.getSequenceTypeKey();
        Timestamp inputSeqDate = inputSeqState.getSequenceDate();
        Timestamp inputSeqrecordDate = inputSeqState.getSeqrecordDate();
        Integer inputSeqLength = inputSeqState.getLength();
        String inputSeqVersion = inputSeqState.getVersion();
        String inputSeqDivision = inputSeqState.getDivision();
        String inputSeqDescription = inputSeqState.getDescription();
        Integer inputSeqStatusKey = inputSeqState.getSequenceStatusKey();

        // get updateable existing sequence attributes
        Integer existingSeqTypeKey = existingSeqState.getSequenceTypeKey();
        Timestamp existingSeqDate = existingSeqState.getSequenceDate();
        Timestamp existingSeqrecordDate = existingSeqState.getSeqrecordDate();
        Integer existingSeqLength = existingSeqState.getLength();
        String existingSeqVersion = existingSeqState.getVersion();
        String existingSeqDivision = existingSeqState.getDivision();
        String existingSeqDescription = existingSeqState.getDescription();
        Integer existingSeqStatusKey = existingSeqState.getSequenceStatusKey();


        // update sequence record date
        if ( inputSeqrecordDate.after(existingSeqrecordDate) ) {
            logger.logdDebug("Updating Sequence record date");
            existingSeqState.setSeqrecordDate(inputSeqrecordDate);
            update = true;
        }

        // if SwissProt or TrEMBL, update sequence date
        // GenBank only updated if the version has changed
        if ( (logicalDB.intValue() == LogicalDBConstants.SWISSPROT ||
               logicalDB.intValue() == LogicalDBConstants.TREMBL) &&
               inputSeqDate.after(existingSeqDate) ) {
            existingSeqState.setSequenceDate(inputSeqDate);
            update = true;
        }

        // update sequence version. Check for null; schema supports null version
        // if either version is null we don't want to do a String compare
        // Maybe we should convert to an int and use inputSeqversion > existingSeqVersion
        if (inputSeqVersion == null || existingSeqVersion == null) {
              // if just one is null - update
              if ( ! (inputSeqVersion == null && existingSeqVersion == null) ) {
                  existingSeqState.setVersion(inputSeqVersion);
                  update = true;
              }
        }
        // Do a string compare, if not equal - update
        else {
            if ( ! inputSeqVersion.equals(existingSeqVersion) ) {
              logger.logdDebug("Updating Sequence Version");
              existingSeqState.setVersion(inputSeqVersion);

              // if GenBank also update seqeunceDate from seqrecord date
              // The GenBank sequenceRecord is determined by inputSeqrecordDate
              // > existingSeqRecordDate AND inputVersion > existingVersion
              if ( (logicalDB.intValue()) == (LogicalDBConstants.SEQUENCE)) {
                  existingSeqState.setSequenceDate(inputSeqrecordDate);
                  logger.logdDebug("Updating Sequence Date");
              }
              update = true;
            }
        }

        // update sequence length. Check for null; schema supports null length
        // if either length is null we don't want to do a String compare
        if ( inputSeqLength == null || existingSeqLength == null) {
            // if just one is null - update
            if (! (inputSeqLength == null && existingSeqLength == null)) {
                existingSeqState.setLength(inputSeqLength);
                update = true;
            }
        }
        // Do a string compare, if not equal - update
        else {
             if( !inputSeqLength.equals(existingSeqLength)) {
               logger.logdDebug("Updating Sequence Length");
               existingSeqState.setLength(inputSeqLength);
               update = true;
             }
        }

        // update sequence division. Check for null; schema supports null division
        // if either division is null we don't want to do a String compare
        if (inputSeqDivision == null || existingSeqDivision == null) {
              // if just one is null - update
              if ( ! (inputSeqDivision == null && existingSeqDivision == null) ) {
                  existingSeqState.setDivision(inputSeqDivision);
                  update = true;
              }
        }
        // Do a string compare, if not equal - update
        else {
            if ( ! inputSeqDivision.equals(existingSeqDivision) ) {
              logger.logdDebug("Updating Sequence Division");
              existingSeqState.setDivision(inputSeqDivision);
              update = true;
            }
        }

        // update description Check for null; schema supports null description
        // if either description is null we don't want to do a String compare
            if (inputSeqDescription == null || existingSeqDescription == null) {
                  // if just one is null - update
                  if ( ! (inputSeqDescription == null && existingSeqDescription == null) ) {
                      existingSeqState.setDescription(inputSeqDescription);
                      update = true;
                  }
            }
            // Do a string compare, if not equal - update
            else {
                if ( ! inputSeqDescription.equals(existingSeqDescription) ) {
                  logger.logdDebug("Updating Sequence Description");
                  existingSeqState.setDescription(inputSeqDescription);
                  update = true;
                }
            }
            // update sequence status key
            // incoming ACTIVE can update existing DELETED
            // incoming DELETED can update existing ACTIVE
            // e.g. incoming refseq ACTIVE should update existing refseq DELETED
            // because deleted refseqs can become active again.
            if(! inputSeqStatusKey.equals(existingSeqStatusKey)) {
                String statusString = termNameLookup.lookup(existingSeqStatusKey);
                // don't update split or not loaded status
                if ( ! (statusString.equals(SeqloaderConstants.SPLIT_STATUS) ||
                        statusString.equals(SeqloaderConstants.DUMMY_SEQ_STATUS))) {
                    logger.logdDebug("Updating Sequence Status to " + inputSeqStatusKey);
                    existingSeqState.setSequenceStatusKey(inputSeqStatusKey);
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
