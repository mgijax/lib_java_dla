// $Header
// $Name

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dla.DLALogger;
import org.jax.mgi.shr.dla.DLALoggingException;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.hist.Seq_SequenceAttrHistory;

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

    // configurator for the sequence load
    private SequenceLoadCfg loadCfg;

    /**
     * constructs a SequenceUpdater
     * @assumes Nothing
     * @effects Queries a database
     * @throws DBException if error creating Seq_SequenceAttrHistory,
     * or a LogicalDBLookup object
     * @throws ConfigException if error creating a SequencLoadCfg object,
     *         a Seq_SequenceAttrHistory object, a LogicalDBLookup, or getting
     *         the logicalDB from the SequenceLoadCfg object
     * @throws CacheException if error creating a LogicalDBLookup
     * @throws KeyNotFoundException if logicalDB is not configured
     */

    public SequenceUpdater()
        throws DBException,  DLALoggingException, ConfigException,
            KeyNotFoundException, CacheException {
        attrHistory = new Seq_SequenceAttrHistory();
        logger = DLALogger.getInstance();
        loadCfg = new SequenceLoadCfg();
        logicalDBLookup = new LogicalDBLookup();
        logicalDB = logicalDBLookup.lookup(loadCfg.getLogicalDB());
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
     */

    public boolean updateSeq(SEQ_SequenceState existingSeqState,
                             Integer existingSeqKey,
                             SEQ_SequenceState inputSeqState)
      throws DBException {

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
                logger.logdDebug("Updating Sequence Type");
                existingSeqState.setSequenceTypeKey(inputSeqTypeKey);
            }
            else {
               // log the sequence key, seqid, new rawType existing rawType
               logger.logcInfo("Cannot update sequence type key due to curation. " +
                               "Existing _Sequence_key: " + existingSeqKey +
                               " has rawType: " + existingSeqState.getRawType() +
                               ". Input rawType is: " +
                               inputSeqState.getRawType(), true);
            }
            existingSeqState.setRawType(inputSeqRawType);
            update = true;
        }

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
              logger.logdDebug("Updating Sequence RawType");
              existingSeqState.setRawType(inputSeqRawType);
              update = true;
            }
        }

        // update raw library. Check for null; schema supports null rawLibrary
        // if either library is null we don't want to do a String compare
        if (inputSeqRawLibrary == null || existingSeqRawLibrary == null) {
              // if just one is null - update
              if ( ! (inputSeqRawLibrary == null && existingSeqRawLibrary == null) ) {
                  logger.logdDebug("Updating Sequence Raw Library - one value null");
                  existingSeqState.setRawLibrary(inputSeqRawLibrary);
                  update = true;
              }
        }
        // Do a string compare, if not equal - update
        else {
            if ( ! inputSeqRawLibrary.equals(existingSeqRawLibrary) ) {
              logger.logdDebug("Updating Sequence Raw Library");
              existingSeqState.setRawLibrary(inputSeqRawLibrary);
              update = true;
            }
        }

        // update raw strain. Check for null; schema supports null rawStrain
        // if either strain is null we don't want to do a String compare
        if (inputSeqRawStrain == null || existingSeqRawStrain == null) {
              // if just one is null - update
              if ( ! (inputSeqRawStrain == null && existingSeqRawStrain == null) ) {
                  logger.logdDebug("Updating Sequence Raw Strain - one value null");
                  existingSeqState.setRawStrain(inputSeqRawStrain);
                  update = true;
              }
        }
        // Do a string compare, if not equal - update
        else {
            if ( ! inputSeqRawStrain.equals(existingSeqRawStrain) ) {
              logger.logdDebug("Updating Sequence Raw strain");
              existingSeqState.setRawStrain(inputSeqRawStrain);
              update = true;
            }
        }

        // update raw tissue. Check for null; schema supports null rawTissue
        // if either tissue is null we don't want to do a String compare
        if (inputSeqRawTissue == null || existingSeqRawTissue == null) {
              // if just one is null - update
              if ( ! (inputSeqRawTissue == null && existingSeqRawTissue == null) ) {
                  logger.logdDebug("Updating Sequence Raw Tissue - one value null");
                  existingSeqState.setRawTissue(inputSeqRawTissue);
                  update = true;
              }
        }
        // Do a string compare, if not equal - update
        else {
            if ( ! inputSeqRawTissue.equals(existingSeqRawTissue) ) {
              logger.logdDebug("Updating Sequence Raw Tissue");
              existingSeqState.setRawTissue(inputSeqRawTissue);
              update = true;
            }
        }

        // update raw age. Check for null; schema supports null rawAge
        // if either age is null we don't want to do a String compare
            if (inputSeqRawAge == null || existingSeqRawAge == null) {
                  // if just one is null - update
                  if ( ! (inputSeqRawAge == null && existingSeqRawAge == null) ) {
                      logger.logdDebug("Updating Sequence Raw Age - one value null");
                      existingSeqState.setRawAge(inputSeqRawAge);
                      update = true;
                  }
            }
            // Do a string compare, if not equal - update
            else {
                if ( ! inputSeqRawAge.equals(existingSeqRawAge) ) {
                  logger.logdDebug("Updating Sequence Raw Age");
                  existingSeqState.setRawAge(inputSeqRawAge);
                  update = true;
                }
            }

        // update raw sex. Check for null; schema supports null rawSex
        // if either sex is null we don't want to do a String compare
            if (inputSeqRawSex == null || existingSeqRawSex == null) {
                  // if just one is null - update
                  if ( ! (inputSeqRawSex == null && existingSeqRawSex == null) ) {
                      logger.logdDebug("Updating Sequence Raw Sex - one value null");
                      existingSeqState.setRawSex(inputSeqRawSex);
                      update = true;
                  }
            }
            // Do a string compare, if not equal - update
            else {
                if ( ! inputSeqRawSex.equals(existingSeqRawSex) ) {
                  logger.logdDebug("Updating Sequence Raw Sex");
                  existingSeqState.setRawSex(inputSeqRawSex);
                  update = true;
                }
            }

        // update raw cellLine. Check for null; schema supports null rawCellLine
        // if either cellLine is null we don't want to do a String compare
            if (inputSeqRawCellLine == null || existingSeqRawCellLine == null) {
                  // if just one is null - update
                  if ( ! (inputSeqRawCellLine == null && existingSeqRawCellLine == null) ) {
                      logger.logdDebug("Updating Sequence Raw CellLine - one value null");
                      existingSeqState.setRawCellLine(inputSeqRawCellLine);
                      update = true;
                  }
            }
            // Do a string compare, if not equal - update
            else {
                if ( ! inputSeqRawCellLine.equals(existingSeqRawCellLine) ) {
                  logger.logdDebug("Updating Sequence Raw CellLine");
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
                  logger.logdDebug("Updating Sequence Description");
                  existingSeqState.setDescription(inputSeqDescription);
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
