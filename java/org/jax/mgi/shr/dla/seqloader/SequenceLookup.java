//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dla.DLALogger;
import org.jax.mgi.shr.dbutils.MultiRowInterpreter;
import org.jax.mgi.shr.dbutils.MultiRowIterator;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceDAO;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.SchemaConstants;

import java.util.*;
import java.sql.Timestamp;

/**
 * @is an object for looking up Sequence objects from the database
 * @has
 *   <UL>
 *   <LI> a query and an interpretor to build a Sequence object
 *   </UL>
 * @does
 *   <UL>
 *   <LI>looks up a Sequence by seqid
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SequenceLookup {
    // an SQL stream with which to build the Sequence
    private SQLStream stream;

    // sql manager for performing queries
    private static SQLDataManager sqlMgr;

    // results navigator for stepping through rows returned from a query
    private ResultsNavigator resultsNav;

    // iterator to iterate through rows representing a Sequence
    private MultiRowIterator multiIterator;

    // interpretor for the query
    private SequenceInterpreter interpreter;

    public SequenceLookup(SQLStream stream) throws DBException, ConfigException {
        // the stream with which to build the Sequence
        this.stream = stream;

        // get an SQL manager for the MGD database
        sqlMgr = SQLDataManagerFactory.getShared(SchemaConstants.MGD);
        interpreter = new SequenceInterpreter();
    }


    /**
     * create a Sequence object by querying the database by seqid
     * @assumes nothing
     * @effects a new connection could be opened to the database if one does
     * not already exist
     * @param seqid the seqid to use in the query
     * @return the Sequence object represented by the database query
     */
    public Sequence findBySeqId(String seqId, int logicalDBKey)
        throws DBException {
        // build the query in multiple Strings concatenating them at the end
        // The compiler can't handle the 1000 +- concatenations on one String
        String query1 = "SELECT distinct " +
            " a." + MGD.acc_accession._accession_key +
            " as ACC_Accession_key, " +
            " a." + MGD.acc_accession.accid +
            " as ACC_accId, " +
            " a." + MGD.acc_accession.prefixpart +
            " as ACC_prefixPart, " +
            " a." + MGD.acc_accession.numericpart +
            " as ACC_numericPart, " +
            " a." + MGD.acc_accession._logicaldb_key +
            " as ACC_LogicalDB_key, " +
            " a." + MGD.acc_accession._object_key +
            " as ACC_Object_key, " +
            " a." + MGD.acc_accession._mgitype_key +
            " as ACC_MGIType_key, " +
            " a." + MGD.acc_accession.privateJ +
            " as ACC_private, " +
            " a." + MGD.acc_accession.preferred +
            " as ACC_preferred, " +
            " a." + MGD.acc_accession._createdby_key +
            " as ACC_CreatedBy_key, " +
            " a." + MGD.acc_accession._modifiedby_key +
            " as ACC_ModifiedBy_key, " +
            " a." + MGD.acc_accession.creation_date +
            " as ACC_creation_date, " +
            " a." + MGD.acc_accession.modification_date +
            " as ACC_modification_date, " +
            " m." + MGD.mgi_reference_assoc._assoc_key +
            " as RefAssoc_Assoc_key, " +
            " m." + MGD.mgi_reference_assoc._refs_key + ", " +
            " m." + MGD.mgi_reference_assoc._object_key +
            " as RefAssoc_Object_key, " +
            " m." + MGD.mgi_reference_assoc._refassoctype_key + ", " +
            " m." + MGD.mgi_reference_assoc._createdby_key +
            " as RefAssoc_CreatedBy_key, " +
            " m." + MGD.mgi_reference_assoc._modifiedby_key +
            " as RefAssoc_ModifiedBy_key, " +
            " m." + MGD.mgi_reference_assoc.creation_date +
            " as RefAssoc_creation_date, " +
            " m." + MGD.mgi_reference_assoc.modification_date +
            " as RefAssoc_modification_date, " +
            " sa." + MGD.seq_source_assoc._assoc_key +
            " as SeqSrc_Assoc_key, " +
            " sa." + MGD.seq_source_assoc._sequence_key +
            " as SeqSrc_Sequence_key, " +
            " sa." + MGD.seq_source_assoc._source_key + ", " +
            " sa." + MGD.seq_source_assoc._createdby_key +
            " as SeqSrc_CreatedBy_key, " +
            " sa." + MGD.seq_source_assoc._modifiedby_key +
            " as SeqSrc_ModifiedBy_key, " +
            " sa." + MGD.seq_source_assoc.creation_date +
            " as SeqSrc_creation_date, " +
            " sa." + MGD.seq_source_assoc.modification_date +
            " as SeqSrc_modification_date, " +
            " s." + MGD.seq_sequence._sequence_key +
            " as SEQ_Sequence_key, " +
            " s." + MGD.seq_sequence._sequencetype_key + ", " +
            " s." + MGD.seq_sequence._sequencequality_key + ", " +
            " s." + MGD.seq_sequence._sequencestatus_key + ", " +
            " s." + MGD.seq_sequence._sequenceprovider_key + ", " +
            " s." + MGD.seq_sequence.length + ", " +
            " s." + MGD.seq_sequence.description + ", " +
            " s." + MGD.seq_sequence.version + ", " +
            " s." + MGD.seq_sequence.division + ", " +
            " s." + MGD.seq_sequence.virtual + ", " +
            " s." + MGD.seq_sequence.rawtype + ", " +
            " s." + MGD.seq_sequence.rawlibrary + ", " +
            " s." + MGD.seq_sequence.raworganism + ", " +
            " s." + MGD.seq_sequence.rawstrain + ", " +
            " s." + MGD.seq_sequence.rawtissue + ", " +
            " s." + MGD.seq_sequence.rawage + ", " +
            " s." + MGD.seq_sequence.rawsex + ", " +
            " s." + MGD.seq_sequence.rawcellline + ", " +
            " s." + MGD.seq_sequence.numberoforganisms + ", " +
            " s." + MGD.seq_sequence.seqrecord_date +
            " as SEQ_seqrecord_date, " +
            " s." + MGD.seq_sequence.sequence_date +
            " as SEQ_sequence_date, " +
            " s." + MGD.seq_sequence._createdby_key +
            " as SEQ_CreatedBy_key, " +
            " s." + MGD.seq_sequence._modifiedby_key +
            " as SEQ_ModifiedBy_key, " +
            " s." + MGD.seq_sequence.creation_date +
            " as SEQ_creation_date, " +
            " s." + MGD.seq_sequence.modification_date +
            " as SEQ_modification_date, " +
            " aa." + MGD.acc_accession._accession_key +
            " as ACC2_Accession_key, " +
            " aa." + MGD.acc_accession.accid +
            " as ACC2_accId, " +
            " aa." + MGD.acc_accession.prefixpart +
            " as ACC2_prefixPart, " +
            " aa." + MGD.acc_accession.numericpart +
            " as ACC2_numericPart, " +
            " aa." + MGD.acc_accession._logicaldb_key +
            " as ACC2_LogicalDB_key, " +
            " aa." + MGD.acc_accession._object_key +
            " as ACC2_Object_key, " +
            " aa." + MGD.acc_accession._mgitype_key +
            " as ACC2_MGIType_key, " +
            " aa." + MGD.acc_accession.privateJ +
            " as ACC2_private, " +
            " aa." + MGD.acc_accession.preferred +
            " as ACC2_preferred, " +
            " aa." + MGD.acc_accession._createdby_key +
            " as ACC2_CreatedBy_key, " +
            " aa." + MGD.acc_accession._modifiedby_key +
            " as ACC2_ModifiedBy_key, " +
            " aa." + MGD.acc_accession.creation_date +
            " as ACC2_creation_date, " +
            " aa." + MGD.acc_accession.modification_date +
            " as ACC2_modification_date " +
            " FROM " +
            MGD.acc_accession._name + " a, " +
            MGD.mgi_reference_assoc._name + " m, " +
            MGD.seq_source_assoc._name + " sa, " +
            MGD.seq_sequence._name + " s,  " +
            MGD.acc_accession._name + " aa " +
            " WHERE s." + MGD.seq_sequence._sequence_key + " = " +
            " a." + MGD.acc_accession._object_key +
            " AND s." + MGD.seq_sequence._sequence_key + " = " +
            " aa." + MGD.acc_accession._object_key +
            " AND s." + MGD.seq_sequence._sequence_key + " = " +
            " sa." + MGD.seq_source_assoc._sequence_key +
            " AND s." + MGD.seq_sequence._sequence_key + " = " +
            " m." + MGD.mgi_reference_assoc._object_key +
            " AND a." + MGD.acc_accession._logicaldb_key + " = " +
            logicalDBKey +
            " AND a." + MGD.acc_accession._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND a." + MGD.acc_accession.preferred + " = " +
            AccessionLib.PREFERRED +
            " AND a." + MGD.acc_accession.accid + " = " +
            "'" + seqId + "'" +
            " AND aa." + MGD.acc_accession._logicaldb_key + " = " +
            logicalDBKey +
            " AND aa." + MGD.acc_accession._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND aa." + MGD.acc_accession.preferred + " = " +
            AccessionLib.NO_PREFERRED +
            " AND m." + MGD.mgi_reference_assoc._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND m." + MGD.mgi_reference_assoc._refassoctype_key + " = " +
            MGIRefAssocTypeConstants.PROVIDER;

        String query2 = " UNION SELECT distinct " +
            " a." + MGD.acc_accession._accession_key +
            " as ACC_Accession_key, " +
            " a." + MGD.acc_accession.accid +
            " as ACC_accId, " +
            " a." + MGD.acc_accession.prefixpart +
            " as ACC_prefixPart, " +
            " a." + MGD.acc_accession.numericpart +
            " as ACC_numericPart, " +
            " a." + MGD.acc_accession._logicaldb_key +
            " as ACC_LogicalDB_key, " +
            " a." + MGD.acc_accession._object_key +
            " as ACC_Object_key, " +
            " a." + MGD.acc_accession._mgitype_key +
            " as ACC_MGIType_key, " +
            " a." + MGD.acc_accession.privateJ +
            " as ACC_private, " +
            " a." + MGD.acc_accession.preferred +
            " as ACC_preferred, " +
            " a." + MGD.acc_accession._createdby_key +
            " as ACC_CreatedBy_key, " +
            " a." + MGD.acc_accession._modifiedby_key +
            " as ACC_ModifiedBy_key, " +
            " a." + MGD.acc_accession.creation_date +
            " as ACC_creation_date, " +
            " a." + MGD.acc_accession.modification_date +
            " as ACC_modification_date, " +
            " RefAssoc_Assoc_key=null, " +
            MGD.mgi_reference_assoc._refs_key + "=null, " +
            " RefAssoc_Object_key=null, " +
           MGD.mgi_reference_assoc._refassoctype_key + "=null, " +
            " RefAssoc_CreatedBy_key=null, " +
            " RefAssoc_ModifiedBy_key=null, " +
            " RefAssoc_creation_date=null, " +
            " RefAssoc_modification_date=null, " +
            " sa." + MGD.seq_source_assoc._assoc_key +
            " as SeqSrc_Assoc_key, " +
            " sa." + MGD.seq_source_assoc._sequence_key +
            " as SeqSrc_Sequence_key, " +
            " sa." + MGD.seq_source_assoc._source_key + ", " +
            " sa." + MGD.seq_source_assoc._createdby_key +
            " as SeqSrc_CreatedBy_key, " +
            " sa." + MGD.seq_source_assoc._modifiedby_key +
            " as SeqSrc_ModifiedBy_key, " +
            " sa." + MGD.seq_source_assoc.creation_date +
            " as SeqSrc_creation_date, " +
            " sa." + MGD.seq_source_assoc.modification_date +
            " as SeqSrc_modification_date, " +
            " s." + MGD.seq_sequence._sequence_key +
            " as SEQ_Sequence_key, " +
            " s." + MGD.seq_sequence._sequencetype_key + ", " +
            " s." + MGD.seq_sequence._sequencequality_key + ", " +
            " s." + MGD.seq_sequence._sequencestatus_key + ", " +
            " s." + MGD.seq_sequence._sequenceprovider_key + ", " +
            " s." + MGD.seq_sequence.length + ", " +
            " s." + MGD.seq_sequence.description + ", " +
            " s." + MGD.seq_sequence.version + ", " +
            " s." + MGD.seq_sequence.division + ", " +
            " s." + MGD.seq_sequence.virtual + ", " +
            " s." + MGD.seq_sequence.rawtype + ", " +
            " s." + MGD.seq_sequence.rawlibrary + ", " +
            " s." + MGD.seq_sequence.raworganism + ", " +
            " s." + MGD.seq_sequence.rawstrain + ", " +
            " s." + MGD.seq_sequence.rawtissue + ", " +
            " s." + MGD.seq_sequence.rawage + ", " +
            " s." + MGD.seq_sequence.rawsex + ", " +
            " s." + MGD.seq_sequence.rawcellline + ", " +
            " s." + MGD.seq_sequence.numberoforganisms + ", " +
            " s." + MGD.seq_sequence.seqrecord_date +
            " as SEQ_seqrecord_date, " +
            " s." + MGD.seq_sequence.sequence_date +
            " as SEQ_sequence_date, " +
            " s." + MGD.seq_sequence._createdby_key +
            " as SEQ_CreatedBy_key, " +
            " s." + MGD.seq_sequence._modifiedby_key +
            " as SEQ_ModifiedBy_key, " +
            " s." + MGD.seq_sequence.creation_date +
            " as SEQ_creation_date, " +
            " s." + MGD.seq_sequence.modification_date +
            " as SEQ_modification_date, " +
            " aa." + MGD.acc_accession._accession_key +
            " as ACC2_Accession_key, " +
            " aa." + MGD.acc_accession.accid +
            " as ACC2_accId, " +
            " aa." + MGD.acc_accession.prefixpart +
            " as ACC2_prefixPart, " +
            " aa." + MGD.acc_accession.numericpart +
            " as ACC2_numericPart, " +
            " aa." + MGD.acc_accession._logicaldb_key +
            " as ACC2_LogicalDB_key, " +
            " aa." + MGD.acc_accession._object_key +
            " as ACC2_Object_key, " +
            " aa." + MGD.acc_accession._mgitype_key +
            " as ACC2_MGIType_key, " +
            " aa." + MGD.acc_accession.privateJ +
            " as ACC2_private, " +
            " aa." + MGD.acc_accession.preferred +
            " as ACC2_preferred, " +
            " aa." + MGD.acc_accession._createdby_key +
            " as ACC2_CreatedBy_key, " +
            " aa." + MGD.acc_accession._modifiedby_key +
            " as ACC2_ModifiedBy_key, " +
            " aa." + MGD.acc_accession.creation_date +
            " as ACC2_creation_date, " +
            " aa." + MGD.acc_accession.modification_date +
            " as ACC2_modification_date " +
            " FROM " +
            MGD.acc_accession._name + " a, " +
            MGD.seq_source_assoc._name + " sa, " +
            MGD.seq_sequence._name + " s,  " +
            MGD.acc_accession._name + " aa " +
            " WHERE s." + MGD.seq_sequence._sequence_key + " = " +
            " a." + MGD.acc_accession._object_key +
            " AND s." + MGD.seq_sequence._sequence_key + " = " +
            " aa." + MGD.acc_accession._object_key +
            " AND s." + MGD.seq_sequence._sequence_key + " = " +
            " sa." + MGD.seq_source_assoc._sequence_key  +
            " AND a." + MGD.acc_accession._logicaldb_key + " = " +
            logicalDBKey +
            " AND a." + MGD.acc_accession._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND a." + MGD.acc_accession.preferred + " = " +
            AccessionLib.PREFERRED +
            " AND a." + MGD.acc_accession.accid + " = " +
            "'" + seqId + "'" +
            " AND aa." + MGD.acc_accession._logicaldb_key + " = " +
            logicalDBKey +
            " AND aa." + MGD.acc_accession._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND aa." + MGD.acc_accession.preferred + " = " +
            AccessionLib.NO_PREFERRED +
            " AND NOT EXISTS (SELECT 1 FROM " +
            MGD.mgi_reference_assoc._name + " r " +
            " WHERE r." + MGD.mgi_reference_assoc._object_key + " = " +
            " s." + MGD.seq_sequence._sequence_key +
            " AND r." + MGD.mgi_reference_assoc._refassoctype_key + " = " +
            MGIRefAssocTypeConstants.PROVIDER + ") ";
        String query3 = " UNION SELECT distinct " +
            " a." + MGD.acc_accession._accession_key +
            " as ACC_Accession_key, " +
            " a." + MGD.acc_accession.accid +
            " as ACC_accId, " +
            " a." + MGD.acc_accession.prefixpart +
            " as ACC_prefixPart, " +
            " a." + MGD.acc_accession.numericpart +
            " as ACC_numericPart, " +
            " a." + MGD.acc_accession._logicaldb_key +
            " as ACC_LogicalDB_key, " +
            " a." + MGD.acc_accession._object_key +
            " as ACC_Object_key, " +
            " a." + MGD.acc_accession._mgitype_key +
            " as ACC_MGIType_key, " +
            " a." + MGD.acc_accession.privateJ +
            " as ACC_private, " +
            " a." + MGD.acc_accession.preferred +
            " as ACC_preferred, " +
            " a." + MGD.acc_accession._createdby_key +
            " as ACC_CreatedBy_key, " +
            " a." + MGD.acc_accession._modifiedby_key +
            " as ACC_ModifiedBy_key, " +
            " a." + MGD.acc_accession.creation_date +
            " as ACC_creation_date, " +
            " a." + MGD.acc_accession.modification_date +
            " as ACC_modification_date, " +
            " m." + MGD.mgi_reference_assoc._assoc_key +
            " as RefAssoc_Assoc_key, " +
            " m." + MGD.mgi_reference_assoc._refs_key + ", " +
            " m." + MGD.mgi_reference_assoc._object_key +
            " as RefAssoc_Object_key, " +
            " m." + MGD.mgi_reference_assoc._refassoctype_key + ", " +
            " m." + MGD.mgi_reference_assoc._createdby_key +
            " as RefAssoc_CreatedBy_key, " +
            " m." + MGD.mgi_reference_assoc._modifiedby_key +
            " as RefAssoc_ModifiedBy_key, " +
            " m." + MGD.mgi_reference_assoc.creation_date +
            " as RefAssoc_creation_date, " +
            " m." + MGD.mgi_reference_assoc.modification_date +
            " as RefAssoc_modification_date, " +
            " sa." + MGD.seq_source_assoc._assoc_key +
            " as SeqSrc_Assoc_key, " +
            " sa." + MGD.seq_source_assoc._sequence_key +
            " as SeqSrc_Sequence_key, " +
            " sa." + MGD.seq_source_assoc._source_key + ", " +
            " sa." + MGD.seq_source_assoc._createdby_key +
            " as SeqSrc_CreatedBy_key, " +
            " sa." + MGD.seq_source_assoc._modifiedby_key +
            " as SeqSrc_ModifiedBy_key, " +
            " sa." + MGD.seq_source_assoc.creation_date +
            " as SeqSrc_creation_date, " +
            " sa." + MGD.seq_source_assoc.modification_date +
            " as SeqSrc_modification_date, " +
            " s." + MGD.seq_sequence._sequence_key +
            " as SEQ_Sequence_key, " +
            " s." + MGD.seq_sequence._sequencetype_key + ", " +
            " s." + MGD.seq_sequence._sequencequality_key + ", " +
            " s." + MGD.seq_sequence._sequencestatus_key + ", " +
            " s." + MGD.seq_sequence._sequenceprovider_key + ", " +
            " s." + MGD.seq_sequence.length + ", " +
            " s." + MGD.seq_sequence.description + ", " +
            " s." + MGD.seq_sequence.version + ", " +
            " s." + MGD.seq_sequence.division + ", " +
            " s." + MGD.seq_sequence.virtual + ", " +
            " s." + MGD.seq_sequence.rawtype + ", " +
            " s." + MGD.seq_sequence.rawlibrary + ", " +
            " s." + MGD.seq_sequence.raworganism + ", " +
            " s." + MGD.seq_sequence.rawstrain + ", " +
            " s." + MGD.seq_sequence.rawtissue + ", " +
            " s." + MGD.seq_sequence.rawage + ", " +
            " s." + MGD.seq_sequence.rawsex + ", " +
            " s." + MGD.seq_sequence.rawcellline + ", " +
            " s." + MGD.seq_sequence.numberoforganisms + ", " +
            " s." + MGD.seq_sequence.seqrecord_date +
            " as SEQ_seqrecord_date, " +
            " s." + MGD.seq_sequence.sequence_date +
            " as SEQ_sequence_date, " +
            " s." + MGD.seq_sequence._createdby_key +
            " as SEQ_CreatedBy_key, " +
            " s." + MGD.seq_sequence._modifiedby_key +
            " as SEQ_ModifiedBy_key, " +
            " s." + MGD.seq_sequence.creation_date +
            " as SEQ_creation_date, " +
            " s." + MGD.seq_sequence.modification_date +
            " as SEQ_modification_date, " +
            " ACC2_Accession_key=null, " +
            " ACC2_accId=null, " +
            " ACC2_prefixPart=null, " +
            " ACC2_numericPart=null, " +
            " ACC2_LogicalDB_key=null, " +
            " ACC2_Object_key=null, " +
            " ACC2_MGIType_key=null, " +
            " ACC2_private=0, " +
            " ACC2_preferred=0, " +
            " ACC2_CreatedBy_key=null, " +
            " ACC2_ModifiedBy_key=null, " +
            " ACC2_creation_date=null, " +
            " ACC2_modification_date=null " +
            " FROM " +
            MGD.acc_accession._name + " a, " +
            MGD.mgi_reference_assoc._name + " m, " +
            MGD.seq_source_assoc._name + " sa, " +
            MGD.seq_sequence._name + " s  " +
            " WHERE s." + MGD.seq_sequence._sequence_key + " = " +
            " a." + MGD.acc_accession._object_key +
            " AND s." + MGD.seq_sequence._sequence_key + " = " +
            " sa." + MGD.seq_source_assoc._sequence_key +
            " AND s." + MGD.seq_sequence._sequence_key + " = " +
            " m." + MGD.mgi_reference_assoc._object_key +
            " AND a." + MGD.acc_accession._logicaldb_key + " = " +
            logicalDBKey +
            " AND a." + MGD.acc_accession._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND a." + MGD.acc_accession.preferred + " = " +
            AccessionLib.PREFERRED +
            " AND a." + MGD.acc_accession.accid + " = " +
            "'" + seqId + "'" +
            " AND m." + MGD.mgi_reference_assoc._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND m." + MGD.mgi_reference_assoc._refassoctype_key + " = " +
            MGIRefAssocTypeConstants.PROVIDER +
            " AND NOT EXISTS (SELECT 1 FROM " +
            MGD.acc_accession._name + " ac " +
            " WHERE s." + MGD.seq_sequence._sequence_key + " = " +
            " ac." + MGD.acc_accession._object_key +
            " AND ac." + MGD.acc_accession._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND ac." + MGD.acc_accession._logicaldb_key + " = " +
            logicalDBKey +
            " AND ac." + MGD.acc_accession.preferred + " = " +
            AccessionLib.NO_PREFERRED + ")";
        String query4 = " UNION " +
        " SELECT distinct " +
            " a." + MGD.acc_accession._accession_key +
            " as ACC_Accession_key, " +
            " a." + MGD.acc_accession.accid +
            " as ACC_accId, " +
            " a." + MGD.acc_accession.prefixpart +
            " as ACC_prefixPart, " +
            " a." + MGD.acc_accession.numericpart +
            " as ACC_numericPart, " +
            " a." + MGD.acc_accession._logicaldb_key +
            " as ACC_LogicalDB_key, " +
            " a." + MGD.acc_accession._object_key +
            " as ACC_Object_key, " +
            " a." + MGD.acc_accession._mgitype_key +
            " as ACC_MGIType_key, " +
            " a." + MGD.acc_accession.privateJ +
            " as ACC_private, " +
            " a." + MGD.acc_accession.preferred +
            " as ACC_preferred, " +
            " a." + MGD.acc_accession._createdby_key +
            " as ACC_CreatedBy_key, " +
            " a." + MGD.acc_accession._modifiedby_key +
            " as ACC_ModifiedBy_key, " +
            " a." + MGD.acc_accession.creation_date +
            " as ACC_creation_date, " +
            " a." + MGD.acc_accession.modification_date +
            " as ACC_modification_date, " +
            " RefAssoc_Assoc_key=null, " +
            MGD.mgi_reference_assoc._refs_key + " =null, " +
            " RefAssoc_Object_key=null, " +
            MGD.mgi_reference_assoc._refassoctype_key + " =null, " +
            " RefAssoc_CreatedBy_key=null, " +
            " RefAssoc_ModifiedBy_key=null, " +
            " RefAssoc_creation_date=null, " +
            " RefAssoc_modification_date=null, " +
            " sa." + MGD.seq_source_assoc._assoc_key +
            " as SeqSrc_Assoc_key, " +
            " sa." + MGD.seq_source_assoc._sequence_key +
            " as SeqSrc_Sequence_key, " +
            " sa." + MGD.seq_source_assoc._source_key + ", " +
            " sa." + MGD.seq_source_assoc._createdby_key +
            " as SeqSrc_CreatedBy_key, " +
            " sa." + MGD.seq_source_assoc._modifiedby_key +
            " as SeqSrc_ModifiedBy_key, " +
            " sa." + MGD.seq_source_assoc.creation_date +
            " as SeqSrc_creation_date, " +
            " sa." + MGD.seq_source_assoc.modification_date +
            " as SeqSrc_modification_date, " +
            " s." + MGD.seq_sequence._sequence_key +
            " as SEQ_Sequence_key, " +
            " s." + MGD.seq_sequence._sequencetype_key + ", " +
            " s." + MGD.seq_sequence._sequencequality_key + ", " +
            " s." + MGD.seq_sequence._sequencestatus_key + ", " +
            " s." + MGD.seq_sequence._sequenceprovider_key + ", " +
            " s." + MGD.seq_sequence.length + ", " +
            " s." + MGD.seq_sequence.description + ", " +
            " s." + MGD.seq_sequence.version + ", " +
            " s." + MGD.seq_sequence.division + ", " +
            " s." + MGD.seq_sequence.virtual + ", " +
            " s." + MGD.seq_sequence.rawtype + ", " +
            " s." + MGD.seq_sequence.rawlibrary + ", " +
            " s." + MGD.seq_sequence.raworganism + ", " +
            " s." + MGD.seq_sequence.rawstrain + ", " +
            " s." + MGD.seq_sequence.rawtissue + ", " +
            " s." + MGD.seq_sequence.rawage + ", " +
            " s." + MGD.seq_sequence.rawsex + ", " +
            " s." + MGD.seq_sequence.rawcellline + ", " +
            " s." + MGD.seq_sequence.numberoforganisms + ", " +
            " s." + MGD.seq_sequence.seqrecord_date +
            " as SEQ_seqrecord_date, " +
            " s." + MGD.seq_sequence.sequence_date +
            " as SEQ_sequence_date, " +
            " s." + MGD.seq_sequence._createdby_key +
            " as SEQ_CreatedBy_key, " +
            " s." + MGD.seq_sequence._modifiedby_key +
            " as SEQ_ModifiedBy_key, " +
            " s." + MGD.seq_sequence.creation_date +
            " as SEQ_creation_date, " +
            " s." + MGD.seq_sequence.modification_date +
            " as SEQ_modification_date, " +
            " ACC2_Accession_key=null, " +
            " ACC2_accId=null, " +
            " ACC2_prefixPart=null, " +
            " ACC2_numericPart=null, " +
            " ACC2_LogicalDB_key=null, " +
            " ACC2_Object_key=null, " +
            " ACC2_MGIType_key=null, " +
            " ACC2_private=0, " +
            " ACC2_preferred=0, " +
            " ACC2_CreatedBy_key=null, " +
            " ACC2_ModifiedBy_key=null, " +
            " ACC2_creation_date=null, " +
            " ACC2_modification_date=null " +
            " FROM " +
            MGD.acc_accession._name + " a, " +
            MGD.seq_source_assoc._name + " sa, " +
            MGD.seq_sequence._name + " s  " +
            " WHERE s." + MGD.seq_sequence._sequence_key + " = " +
            " a." + MGD.acc_accession._object_key +
            " AND s." + MGD.seq_sequence._sequence_key + " = " +
            " sa." + MGD.seq_source_assoc._sequence_key +
            " AND a." + MGD.acc_accession._logicaldb_key + " = " +
            logicalDBKey +
            " AND a." + MGD.acc_accession._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND a." + MGD.acc_accession.preferred + " = " +
            AccessionLib.PREFERRED +
            " AND a." + MGD.acc_accession.accid + " = " +
            "'" + seqId + "'" +
            " AND NOT EXISTS (SELECT 1 FROM " +
            MGD.acc_accession._name + " acc " +
            " WHERE s." + MGD.seq_sequence._sequence_key + " = " +
            " acc." + MGD.acc_accession._object_key +
            " AND acc." + MGD.acc_accession._mgitype_key + " = " +
            MGITypeConstants.SEQUENCE +
            " AND acc." + MGD.acc_accession._logicaldb_key + " = " +
            logicalDBKey +
            " AND acc." + MGD.acc_accession.preferred + " = " +
            AccessionLib.NO_PREFERRED + ") " +
            " AND NOT EXISTS (SELECT 1 FROM " +
            MGD.mgi_reference_assoc._name + " mr " +
            " WHERE mr." + MGD.mgi_reference_assoc._object_key + " = " +
            " s." +  MGD.seq_sequence._sequence_key +
            " AND mr." + MGD.mgi_reference_assoc._refassoctype_key + " = " +
            MGIRefAssocTypeConstants.PROVIDER + ") " +
        " order by s." + MGD.seq_sequence._sequence_key;

        // create one query
        String query = query1 + query2 + query3 + query4;

        // execute the query

        resultsNav = sqlMgr.executeQuery(query);

        // get a multi row iterator
        multiIterator = new MultiRowIterator(resultsNav, interpreter);

        // this iterator will have only one object
        return (Sequence)multiIterator.next();

       //return null;
    }
    /**
     * @is an object that knows how to build a Sequence object from
     * multiple rows of a result set. All rows with the same sequence key
     * belong to the same Sequence
     * @has
     *   <UL>
     *   <LI> a query and an interpretor to build a Sequence object
     *   </UL>
     * @does
     *   <UL>
     *   <LI>looks up a Sequence by seqid
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

    private class SequenceInterpreter
        implements MultiRowInterpreter {

        // Instance variables

        // the Sequence and its reusable components we are building from a set
        // of data rows. Not all Sequences have references, secondary seqids
        // or multiple sources; we'll create objects for them as needed.
        private Sequence sequence;
        private SEQ_SequenceState seqState;
        private ACC_AccessionState accState;

        // the set of source assoc keys (Integer) already processed
        private HashSet sourceSet = new HashSet();

        // the set of ref association keys (Integer) already processed
        private HashSet refSet = new HashSet();

        // the set of 2ndary accession keys (Integer) already processed
        private HashSet accSet = new HashSet();

        // a row of data
        private RowData rowData;

        /**
         * Create a RowData object from the given RowReference
         * @assumes Nothing
         * @effects Nothing
         * @param row the current RowReference
         * @throws DBException
         */

        public Object interpret(RowReference row) throws DBException {
            return new RowData(row);
        }

        /**
         * gets the object representing the key to a set of row references
         * @assumes Nothing
         * @effects Nothing
         * @param row the current RowReference
         * @return the key to the given RowReference
         * @throws DBException
         */

        public Object interpretKey(RowReference row) throws DBException {
            // The SEQ_Sequence._Sequence_key
            return row.getInt(29);
        }

        /**
         * Build a Sequence object from a Vector of RowData objects
         * @assumes The caller is not cacheing the Sequence object
         * @effects Nothing
         * @param v the Vector of RowData objects
         * @return a Sequence object, null if v is empty
         * @throws DBException
         */

        public Object interpretRows(Vector v) throws DBException {
            // reset the sequence and its reusable components
            sequence = null;
            seqState = null;
            accState = null;


            Iterator i = v.iterator();
            // Create the sequence, primary accession, source assoc
            // and any references and 2ndary accessions from the first row
            if(i.hasNext()) {
                // get the first row
                rowData = (RowData)i.next();

                // the sequence state we are building
                seqState = new SEQ_SequenceState();

                // set the sequence state
                seqState.setSequenceTypeKey(rowData.SEQ_SequenceType_key);
                seqState.setSequenceQualityKey(rowData.SEQ_SequenceQuality_key);
                seqState.setSequenceStatusKey(rowData.SEQ_SequenceStatus_key);
                seqState.setSequenceProviderKey(rowData.SEQ_SequenceProvider_key);
                seqState.setLength(rowData.SEQ_length);
                seqState.setDescription(rowData.SEQ_description);
                seqState.setVersion(rowData.SEQ_version);
                seqState.setDivision(rowData.SEQ_division);
                seqState.setVirtual(rowData.SEQ_virtual);
                seqState.setRawType(rowData.SEQ_rawType);
                seqState.setRawLibrary(rowData.SEQ_rawLibrary);
                seqState.setRawOrganism(rowData.SEQ_rawOrganism);
                seqState.setRawStrain(rowData.SEQ_rawStrain);
                seqState.setRawTissue(rowData.SEQ_rawTissue);
                seqState.setRawAge(rowData.SEQ_rawAge);
                seqState.setRawSex(rowData.SEQ_rawSex);
                seqState.setRawCellLine(rowData.SEQ_rawCellLine);
                seqState.setNumberOfOrganisms(rowData.SEQ_numberOfOrganisms);
                seqState.setSeqrecordDate(rowData.SEQ_seqrecord_date);
                seqState.setSequenceDate(rowData.SEQ_sequence_date);
                seqState.setCreatedByKey(rowData.SEQ_CreatedBy_key);
                seqState.setModifiedByKey(rowData.SEQ_ModifiedBy_key);
                seqState.setCreationDate(rowData.SEQ_creation_date);
                seqState.setModificationDate(rowData.SEQ_modification_date);

                // create a Sequence
                sequence = new Sequence (seqState, new SEQ_SequenceKey(
                    rowData.SEQ_Sequence_key), stream);

                // the primary accession state we are building
                accState = new ACC_AccessionState();

                // set the primary accession state
                accState.setAccID(rowData.ACC_accId);
                accState.setPrefixPart(rowData.ACC_prefixPart);
                accState.setNumericPart(rowData.ACC_numericPart);
                accState.setLogicalDBKey(rowData.ACC_LogicalDB_key);
                accState.setObjectKey(rowData.ACC_Object_key);
                accState.setMGITypeKey(rowData.ACC_MGIType_key);
                accState.setPrivateVal(rowData.ACC_private);
                accState.setPreferred(rowData.ACC_preferred);
                accState.setCreatedByKey(rowData.ACC_CreatedBy_key);
                accState.setModifiedByKey(rowData.ACC_ModifiedBy_key);
                accState.setCreationDate(rowData.ACC_creation_date);
                accState.setModificationDate(rowData.ACC_modification_date);

                // set the primary accession in the Sequence
                sequence.setAccPrimary(new ACC_AccessionKey(
                    rowData.ACC_Accession_key), accState);

                // create the first seq source association
                createSeqSrcAssoc();

                // create 2ndary accession if there is one
                if(rowData.ACC2_Accession_key != null) {
                   create2ndaryAccession();
                }
                // create a reference if there is one
                if(rowData.RefAssoc_Assoc_key != null) {
                    createRefAssoc();
                }

            }
            // get additional source, ref assoc, 2ndary accessions
            while (i.hasNext()) {
                rowData = (RowData)i.next();

                // create another seq source association if there is one
                if(rowData.SeqSrc_Assoc_key != null &&
                   ! sourceSet.contains(rowData.SeqSrc_Assoc_key)) {
                    createSeqSrcAssoc();
                }
                // create another 2ndary accession if there is one
                if(rowData.ACC2_Accession_key != null &&
                   ! accSet.contains(rowData.ACC2_Accession_key)) {
                   create2ndaryAccession();
                }
                // create another reference association if there is one
                if(rowData.RefAssoc_Assoc_key != null &&
                   ! refSet.contains(rowData.RefAssoc_Assoc_key)) {
                    createRefAssoc();
                }

            }

            return sequence;
        }

        /**
         * creates a seq source association object (from the current rowData
         * object) and sets it in the Sequence
         * we are building
         * @assumes Nothing
         * @effects Nothing
         * @param None
         * @return Nothing
         * @throws DBException
         */

        private void createSeqSrcAssoc() throws DBException {

            // the seq source association state we are building
            SEQ_Source_AssocState state = new SEQ_Source_AssocState();

            // set the seq source association state from the rowDatat
            state.setSequenceKey(rowData.SeqSrc_Sequence_key);
            state.setSourceKey(rowData.SeqSrc_Source_key);
            state.setCreatedByKey(rowData.SeqSrc_CreatedBy_key);
            state.setModifiedByKey(rowData.SeqSrc_ModifiedBy_key);
            state.setCreationDate(rowData.SeqSrc_creation_date);
            state.setModificationDate(rowData.SeqSrc_modification_date);

            // set the seq source association in the Sequence
            sequence.addSeqSrcAssoc(new SEQ_Source_AssocKey(
                  rowData.SeqSrc_Assoc_key), state);

            // add the source association key to the set
            sourceSet.add(rowData.SeqSrc_Assoc_key);

        }

        /**
         * creates an accession state object (from the current rowData
         * object) and sets it in the Sequence
         * we are building
         * @assumes Nothing
         * @effects Nothing
         * @param None
         * @return Nothing
         * @throws DBException
         */

        private void create2ndaryAccession()
            throws DBException {

            // the state we are building
            ACC_AccessionState state = new ACC_AccessionState();

            // set the primary accession state from the rowData
            state.setAccID(rowData.ACC2_accId);
            state.setPrefixPart(rowData.ACC2_prefixPart);
            state.setNumericPart(rowData.ACC2_numericPart);
            state.setLogicalDBKey(rowData.ACC2_LogicalDB_key);
            state.setObjectKey(rowData.ACC2_Object_key);
            state.setMGITypeKey(rowData.ACC2_MGIType_key);
            state.setPrivateVal(rowData.ACC2_private);
            state.setPreferred(rowData.ACC2_preferred);
            state.setCreatedByKey(rowData.ACC2_CreatedBy_key);
            state.setModifiedByKey(rowData.ACC2_ModifiedBy_key);
            state.setCreationDate(rowData.ACC2_creation_date);
            state.setModificationDate(rowData.ACC2_modification_date);

            // set the secondary accession in the Sequence
            sequence.addAccSecondary(new ACC_AccessionKey(
                rowData.ACC2_Accession_key), state);

            // add the 2ndary accession key to the set
            accSet.add(rowData.ACC2_Accession_key);
        }

        /**
         * creates a reference association object (from the current rowData
         * object) and sets it in the Sequence
         * we are building
         * @assumes Nothing
         * @effects Nothing
         * @param None
         * @return Nothing
         * @throws DBException
         */

        private void createRefAssoc() throws DBException {

            // the reference association we are building
            MGI_Reference_AssocState state = new MGI_Reference_AssocState();

            // set the reference association from the rowData
            state.setRefsKey(rowData.RefAssoc_Refs_key);
            state.setObjectKey(rowData.RefAssoc_Object_key);
            state.setRefAssocTypeKey(rowData.RefAssoc_RefAssocType_key);
            state.setCreatedByKey(rowData.RefAssoc_CreatedBy_key);
            state.setModifiedByKey(rowData.RefAssoc_ModifiedBy_key);
            state.setCreationDate(rowData.RefAssoc_creation_date);
            state.setModificationDate(rowData.RefAssoc_modification_date);

            // set the reference association in the Sequence
            sequence.addRefAssoc(new MGI_Reference_AssocKey(
                rowData.RefAssoc_Assoc_key), state);

            // add the ref association key to the set
            refSet.add(rowData.RefAssoc_Assoc_key);
        }

        /**
         * @is an object that represents a row of data from the query we are
         * interpreting
         * @has
         *   <UL>
         *   <LI> attributes representing each column selected in the query
         *   </UL>
         * @does
         *   <UL>
         *   <LI> assigns its attributes from a RowReference object
         *   </UL>
         * @company The Jackson Laboratory
         * @author sc
         * @version 1.0
         */

        private class RowData {
            protected Integer ACC_Accession_key;
            protected String ACC_accId;
            protected String ACC_prefixPart;
            protected Integer ACC_numericPart;
            protected Integer ACC_LogicalDB_key;
            protected Integer ACC_Object_key;
            protected Integer ACC_MGIType_key;
            protected Boolean ACC_private;
            protected Boolean ACC_preferred;
            protected Integer ACC_CreatedBy_key;
            protected Integer ACC_ModifiedBy_key;
            protected Timestamp ACC_creation_date;
            protected Timestamp ACC_modification_date;
            protected Integer RefAssoc_Assoc_key;
            protected Integer RefAssoc_Refs_key;
            protected Integer RefAssoc_Object_key;
            protected Integer RefAssoc_RefAssocType_key;
            protected Integer RefAssoc_CreatedBy_key;
            protected Integer RefAssoc_ModifiedBy_key;
            protected Timestamp RefAssoc_creation_date;
            protected Timestamp RefAssoc_modification_date;
            protected Integer SeqSrc_Assoc_key;
            protected Integer SeqSrc_Sequence_key;
            protected Integer SeqSrc_Source_key;
            protected Integer SeqSrc_CreatedBy_key;
            protected Integer SeqSrc_ModifiedBy_key;
            protected Timestamp SeqSrc_creation_date;
            protected Timestamp SeqSrc_modification_date;
            protected Integer SEQ_Sequence_key;
            protected Integer SEQ_SequenceType_key;
            protected Integer SEQ_SequenceQuality_key;
            protected Integer SEQ_SequenceStatus_key;
            protected Integer SEQ_SequenceProvider_key;
            protected Integer SEQ_length;
            protected String SEQ_description;
            protected String SEQ_version;
            protected String SEQ_division;
            protected Boolean SEQ_virtual;
            protected String SEQ_rawType;
            protected String SEQ_rawLibrary;
            protected String SEQ_rawOrganism;
            protected String SEQ_rawStrain;
            protected String SEQ_rawTissue;
            protected String SEQ_rawAge;
            protected String SEQ_rawSex;
            protected String SEQ_rawCellLine;
            protected Integer SEQ_numberOfOrganisms;
            protected Timestamp SEQ_seqrecord_date;
            protected Timestamp SEQ_sequence_date;
            protected Integer SEQ_CreatedBy_key;
            protected Integer SEQ_ModifiedBy_key;
            protected Timestamp SEQ_creation_date;
            protected Timestamp SEQ_modification_date;
            protected Integer ACC2_Accession_key;
            protected String ACC2_accId;
            protected String ACC2_prefixPart;
            protected Integer ACC2_numericPart;
            protected Integer ACC2_LogicalDB_key;
            protected Integer ACC2_Object_key;
            protected Integer ACC2_MGIType_key;
            protected Boolean ACC2_private;
            protected Boolean ACC2_preferred;
            protected Integer ACC2_CreatedBy_key;
            protected Integer ACC2_ModifiedBy_key;
            protected Timestamp ACC2_creation_date;
            protected Timestamp ACC2_modification_date;

            /**
             * Constructs a RowData object from a RowReference
             * @assumes Nothing
             * @effects Nothing
             * @param row a RowReference
             * @throws ConfigException if can't find the Configuration file
             */

            public RowData(RowReference row) throws DBException {
                ACC_Accession_key = row.getInt(1);
                ACC_accId = row.getString(2);
                ACC_prefixPart = row.getString(3);
                ACC_numericPart = row.getInt(4);
                ACC_LogicalDB_key = row.getInt(5);
                ACC_Object_key = row.getInt(6);
                ACC_MGIType_key = row.getInt(7);
                ACC_private = row.getBoolean(8);
                ACC_preferred = row.getBoolean(9);
                ACC_CreatedBy_key = row.getInt(10);
                ACC_ModifiedBy_key = row.getInt(11);
                ACC_creation_date = row.getTimestamp(12);
                ACC_modification_date = row.getTimestamp(13);
                RefAssoc_Assoc_key = row.getInt(14);
                RefAssoc_Refs_key = row.getInt(15);
                RefAssoc_Object_key = row.getInt(16);
                RefAssoc_RefAssocType_key = row.getInt(17);
                RefAssoc_CreatedBy_key = row.getInt(18);
                RefAssoc_ModifiedBy_key = row.getInt(19);
                RefAssoc_creation_date = row.getTimestamp(20);
                RefAssoc_modification_date = row.getTimestamp(21);
                SeqSrc_Assoc_key = row.getInt(22);
                SeqSrc_Sequence_key = row.getInt(23);
                SeqSrc_Source_key = row.getInt(24);
                SeqSrc_CreatedBy_key = row.getInt(25);
                SeqSrc_ModifiedBy_key = row.getInt(26);
                SeqSrc_creation_date = row.getTimestamp(27);
                SeqSrc_modification_date = row.getTimestamp(28);
                SEQ_Sequence_key = row.getInt(29);
                SEQ_SequenceType_key = row.getInt(30);
                SEQ_SequenceQuality_key = row.getInt(31);
                SEQ_SequenceStatus_key = row.getInt(32);
                SEQ_SequenceProvider_key = row.getInt(33);
                SEQ_length = row.getInt(34);
                SEQ_description = row.getString(35);
                SEQ_version = row.getString(36);
                SEQ_division = row.getString(37);
                SEQ_virtual = row.getBoolean(38);
                SEQ_rawType = row.getString(39);
                SEQ_rawLibrary = row.getString(40);
                SEQ_rawOrganism = row.getString(41);
                SEQ_rawStrain = row.getString(42);
                SEQ_rawTissue = row.getString(43);
                SEQ_rawAge = row.getString(44);
                SEQ_rawSex = row.getString(45);
                SEQ_rawCellLine = row.getString(46);
                SEQ_numberOfOrganisms = row.getInt(47);
                SEQ_seqrecord_date = row.getTimestamp(48);
                SEQ_sequence_date = row.getTimestamp(49);
                SEQ_CreatedBy_key = row.getInt(50);
                SEQ_ModifiedBy_key = row.getInt(51);
                SEQ_creation_date = row.getTimestamp(52);
                SEQ_modification_date = row.getTimestamp(53);
                ACC2_Accession_key = row.getInt(54);
                ACC2_accId = row.getString(55);
                ACC2_prefixPart = row.getString(56);
                ACC2_numericPart = row.getInt(57);
                ACC2_LogicalDB_key = row.getInt(58);
                ACC2_Object_key = row.getInt(59);
                ACC2_MGIType_key = row.getInt(60);
                ACC2_private = row.getBoolean(61);
                ACC2_preferred = row.getBoolean(62);
                ACC2_CreatedBy_key = row.getInt(63);
                ACC2_ModifiedBy_key = row.getInt(64);
                ACC2_creation_date = row.getTimestamp(65);
                ACC2_modification_date = row.getTimestamp(66);
            }
        }
    }
}