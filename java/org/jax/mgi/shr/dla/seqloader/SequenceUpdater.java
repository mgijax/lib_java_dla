// $Header
// $Name

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.dao.MGI_AttributeHistoryState;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dla.DLALogger;
import org.jax.mgi.shr.dla.DLALoggingException;
import org.jax.mgi.shr.dla.DLALoggingExceptionFactory;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;



import java.sql.Timestamp;

/**
 * @is An object that updates a SEQ_SequenceState representing an existing sequence
 *     and an MGI_AttributeHistoryState object representing the sequence type
 *     attribute history
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

    SeqAttrHistory attrHistory;
    DLALogger logger;
    LogicalDBLookup logicalDBLookup;
    Integer logicalDB;

    // configurator for the sequence load
    private SequenceLoadCfg loadCfg;

    /**
     * constructs a SequenceUpdater
     * @assumes Nothing
     * @effects Queries a database
     * @param None
     * @throws DBException if problem querying the database for attribute history
     * @throws ConfigException from creation of loadCfg
     * @throws CacheException from call to logicalDBLookup.lookup()
     * @throws KeyNotFoundException from creation of logicalDBLookup
     */

    public SequenceUpdater()
        throws DBException, ConfigException, DLALoggingException,
            KeyNotFoundException, CacheException {
        attrHistory = new SeqAttrHistory();
        logger = DLALogger.getInstance();
        loadCfg = new SequenceLoadCfg();
        logicalDBLookup = new LogicalDBLookup();
        logicalDB = logicalDBLookup.lookup(loadCfg.getLogicalDB());
    }

    /**
     * updates the attributes of a SEQ_SequenceState representing an existing
     * SEQ_Sequence from a SEQ_SequenceState representing the updated sequence.
     * @assumes Nothing
     * @effects Queries a database
     * @param existingSeqState SEQ_SequenceState representing an existing sequence
     * @param existingSeqKey SEQ_SequenceKey of the existing sequence
     * @param inputSeqState SEQ_SequenceState represeting the updated sequence
     * @return true if any sequence attributes were updated in 'existingSeqState'
     * @throws DBException if problem querying the database for attribute history
     * @throws ConfigException
     */

    public boolean updateSeq(SEQ_SequenceState existingSeqState,
                             Integer existingSeqKey,
                             SEQ_SequenceState inputSeqState)
        throws DBException, ConfigException {

        boolean update = false;
        // get updateable input sequence attributes
        Integer inputSeqTypeKey = inputSeqState.getSequenceTypeKey();
        String inputSeqRawType = inputSeqState.getRawType();
        Timestamp inputSeqDate = inputSeqState.getSequenceDate();
        Timestamp inputSeqrecordDate = inputSeqState.getSeqrecordDate();
        Integer inputSeqLength = inputSeqState.getLength();
        String inputSeqVersion = inputSeqState.getVersion();
        String inputSeqDivision = inputSeqState.getDivision();
        String inputSeqRawLibrary = inputSeqState.getRawLibrary();
        String inputSeqRawOrganism = inputSeqState.getRawOrganism();
        String inputSeqRawStrain = inputSeqState.getRawStrain();
        String inputSeqRawTissue = inputSeqState.getRawTissue();
        String inputSeqRawAge = inputSeqState.getRawAge();
        String inputSeqRawSex = inputSeqState.getRawSex();
        String inputSeqRawCellLine = inputSeqState.getRawCellLine();
        String inputSeqDescription = inputSeqState.getDescription();

        // get updateable existing sequence attributes
        Integer existingSeqTypeKey = existingSeqState.getSequenceTypeKey();
        String existingSeqRawType = existingSeqState.getRawType();
        Timestamp existingSeqDate = existingSeqState.getSequenceDate();
        Timestamp existingSeqrecordDate = existingSeqState.getSeqrecordDate();
        Integer existingSeqLength = existingSeqState.getLength();
        String existingSeqVersion = existingSeqState.getVersion();
        String existingSeqDivision = existingSeqState.getDivision();
        String existingSeqRawLibrary = existingSeqState.getRawLibrary();
        String existingSeqRawOrganism = existingSeqState.getRawOrganism();
        String existingSeqRawStrain = existingSeqState.getRawStrain();
        String existingSeqRawTissue = existingSeqState.getRawTissue();
        String existingSeqRawAge = existingSeqState.getRawAge();
        String existingSeqRawSex = existingSeqState.getRawSex();
        String existingSeqRawCellLine = existingSeqState.getRawCellLine();
        String existingSeqDescription = existingSeqState.getDescription();

        // update sequence type key only if not curator edited
        if (! inputSeqTypeKey.equals(existingSeqTypeKey) ) {
            // check type attribute history
            if( ! attrHistory.isTypeCurated(existingSeqKey)) {

                existingSeqState.setSequenceTypeKey(inputSeqTypeKey);
            }
            else {
               // log the sequence key, seqid, new rawType existing rawType
               logger.logvInfo("Cannot update sequence type key due to curation. " +
                               "Existing _Sequence_key: " + existingSeqKey +
                               "has rawType: " + existingSeqState.getRawType() +
                               ". input rawType is: " +
                               inputSeqState.getRawType(), true);
            }
            existingSeqState.setRawType(inputSeqRawType);
            update = true;
        }

        // update sequence record date
        if ( inputSeqrecordDate.after(existingSeqrecordDate) ) {
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
              existingSeqState.setVersion(inputSeqVersion);

              // if GenBank also update seqeunceDate from seqrecord date
              // The GenBank sequenceRecord is determined by inputSeqrecordDate
              // > existingSeqRecordDate AND inputVersion > existingVersion
              if ( (logicalDB.intValue()) == (LogicalDBConstants.SEQUENCE)) {
                  existingSeqState.setSequenceDate(inputSeqrecordDate);
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
               existingSeqState.setLength(inputSeqLength);
               update = true;
             }
        }

        // update sequence division. Check for null; chema supports null division
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
              existingSeqState.setDivision(inputSeqDivision);
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
                  existingSeqState.setDescription(inputSeqDescription);
                  update = true;
                }
            }

	return update;
    }
}
// $Log