package org.jax.mgi.shr.dla.assemblyloader;

import java.util.*;
import java.sql.Timestamp;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.dbs.mgd.MolecularSource.MSRawAttributes;
import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.shr.dla.seqloader.SequenceInterpreter;
import org.jax.mgi.shr.dla.seqloader.SequenceInput;
import org.jax.mgi.shr.dla.seqloader.SequenceRawAttributes;
import org.jax.mgi.shr.dla.seqloader.SeqloaderConstants;
import org.jax.mgi.shr.dla.seqloader.RefAssocRawAttributes;
import org.jax.mgi.shr.dla.seqloader.AccessionRawAttributes;

/**
 * An object that parses an MGS Assembly format record and obtains
 *     values from a Configurator to create a SequenceInput data object.<BR>
 *     Determines if a record is the header record (1st record in file)
 *     or a sequence record<BR>
 * @has
 *   <UL>
 *   <LI>A SequenceInput object into which it bundles:
 *   <LI>A SequenceRawAttributes object
 *   <LI>An AccessionRawAttributes object for its seqid
 *   <LI> A RefAssocRawAttributes object for its reference
 *   <LI> A MSRawAttributes
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Determines if a MGS Assembly format sequence record is the header record
 *       or a sequence record
 *   <LI>Parses a MGS Assembly format sequence record
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MGSAssemblyFormatInterpreter extends SequenceInterpreter {

  /**
   * The set of attributes with values common to all assembly sequences
   */

    private String seqType;
    private String seqQuality;
    private Boolean preferredAcc = Boolean.TRUE;
    private Boolean privateAcc = Boolean.FALSE;

    // for seqDate and seqRecordDate
    private Timestamp timeStamp;

    // Header record attributes
    String header;
    //String logicalDBName - configurator needs access as other classes (MSP) use
    // configurator to get this value
    String version;
    String jNum;
    //String loadName; Note jobstream needs this too so no good to have in file
    String source;

    // non-header record attributes
    String seqid;
    Integer startBP;
    Integer endBP;
    String description;

    /**
     * A SequenceInput and its parts
     */

    // The object we are building. Represents raw attributes for a sequence,
    // its source, references, and accessions
    protected SequenceInput sequenceInput = new SequenceInput();

    // raw attributes for a sequence - reused by calling reset()
    private SequenceRawAttributes rawSeq = new SequenceRawAttributes();

    // raw attributes for a sequences source - reused by calling reset()
    private MSRawAttributes rawMS = new MSRawAttributes();

    // raw attributes for a sequences reference = reusded by calling reset()
    private RefAssocRawAttributes rawRefAssoc = new RefAssocRawAttributes();

    // raw attributes for the sequence's seqid = reused by calling reset()
    private AccessionRawAttributes rawAcc = new AccessionRawAttributes();

    // true if we have found the header record
    private boolean headerFound = false;
    // true if we have processed header record
    private boolean headerProcessed = false;

    /**
     * Constructs a MGSAssemblyFormatInterpreter
     * @assumes Nothing
     * @effects Nothing
     * @param Nothing
     * @throws ConfigException if can't find configuration file
     */

    public MGSAssemblyFormatInterpreter() throws ConfigException {
        // these values common to all Assembly sequences
        seqType = sequenceCfg.getType();
        seqQuality = sequenceCfg.getQuality();
        // override superclass setting
        refAssocType = new Integer(MGIRefAssocTypeConstants.LOAD);
        timeStamp = new Timestamp(new Date().getTime());
  }

    /**
     * returns false if 'record' is the header record, else true
     * @assumes Nothing
     * @effects Nothing
     * @param record A MGSAssembly format sequence record
     * @return true if we want to load this sequence
     * @throws Nothing
     */

    public boolean isValid(String record) {
        if (headerFound == false) {
            // this is the header record - get it and return false
            header = record;
            headerFound = true;
            return false;
        }
        else {
          // this is a non-header record
          return true;
        }
    }

    /**
    * Parses a MGS assembly formatsequence record and  creates a SequenceInpu
    * object from Configuration and parsed values
    * @assumes Nothing
    * @effects Nothing
    * @param rcd A header record or a sequence record<BR>
    * header record example: <BR>
    * NCBI Gene \t NCBI Build 32 \t J:90438 NCBI Gene \t ncbi_assemblyseqload \t
    *         mouse, laboratory;C57BL/6J;Not Specified;Not Specified;Pooled;Not Specified \n
    * sequence record example: <BR>
    * 240677 \t 1 \t 3068294 \t 3069180 \t + \t GENE \n
    * @return A SequenceInput object representing 'rcd'
    * @throws RecordFormatException if we can't parse an attribute because of
    *         record formatting errors
    */
    public Object interpret(String rcd) throws RecordFormatException {
        // reset objects - don't reset rawMS, rawRefAssoc, it is same for all sequences
        sequenceInput.reset();
        rawSeq.reset();
        rawAcc.reset();

        // process header line if we haven't already
        if (headerProcessed == false) {
          // get the header information - this method creates the
          // MSRawAttributes object and the RefAssocRawAttributes object for
          // all sequences
           parseHeader(header);
           headerProcessed = true;
        }
        // get seqid, start/end BP,  and description from the record
        parseRecord(rcd);

        // add rawMS and rawRefAssoc to SequenceInput object
        sequenceInput.addMSource(rawMS);
        sequenceInput.addRef(rawRefAssoc);

        // create a SequenceRawAttributes; set in SequenceInput object
        createRawSequence();
        // create an AccessionRawAttributes; set in SequenceInput object
        createRawAccession();

       return sequenceInput;
   }
   private void parseHeader(String header) throws RecordFormatException {
     ArrayList splitHeader = StringLib.split(header, SeqloaderConstants.TAB);
           if (splitHeader.size() != 6) {
               RecordFormatException e = new RecordFormatException();
               e.bindRecord("The header record is not formatted correctly, " +
                      "6 tab delimited elements expected.\n" + header);
               throw e;
           }
           // we need this in the config file for Seqloader and MSP
           //logicalDBName = (String) splitHeader.get(0);
           version = ((String) splitHeader.get(1)).trim();
           jNum = ((String) splitHeader.get(2)).trim();
           //loadName = (String) splitHeader.get(4);
           String source = ((String) splitHeader.get(5)).trim();

           // parse the source and create the MSRawAttributes object
           ArrayList splitSource = StringLib.split(source,
               SeqloaderConstants.SEMI_COLON);
           if (splitSource.size() != 6) {
               RecordFormatException e = new RecordFormatException();
               e.bindRecord("The header source is not formatted correctly, " +
                            "6 semi-colon delimited elements expected.\n" + source);
               throw e;
           }
           //MSProcessor resolves based on logicalDB 'Sequence DB' and 'Refseq'
           // use GBMSAttributeResolver. The rest use NonGBMSAttributeResolver

           // must be resolved.
           rawMS.setOrganism( ((String)splitSource.get(0)).trim() );

           // GBMSAttributeResolver may use organism for strain other values
           // 'Not Resolved or Not Specified'.
           // NonGBMSAttribute resolver assumes 'Not Applicable'
           rawMS.setStrain( ((String)splitSource.get(1)).trim() );
           // GBMSAttributeResolver resolves else Not Resolved or Not Specified
          // NonGBMSAttribute resolver assumes 'Not applicable'
           rawMS.setTissue( ((String)splitSource.get(2)).trim() );

           rawMS.setAge( ((String)splitSource.get(3)).trim() );
           //System.out.println("AssemblyFormatInterpreter raw age: " + rawMS.getAge());

           // GBMSAttributeREsolver either resolves or 'Not Specified' or
           // 'Not Resolved'. NonGBMSAttributeResolver assumes "Not Applicable'
           rawMS.setGender( ((String)splitSource.get(4)).trim() );
           rawMS.setCellLine( ((String)splitSource.get(5)).trim() );

           // library is null, don't need to set anything

           // create the RefAssocRawAttributes object
           // set the jnumber in the RefAssocRawAttributes object
           rawRefAssoc.setRefId(jNum);
           rawRefAssoc.setMgiType(seqMGIType);
           rawRefAssoc.setRefAssocType(refAssocType);

   }
   private void  parseRecord(String rcd) throws RecordFormatException {
       // get the seqid and description from the non-header record
       ArrayList splitLine = StringLib.split(rcd, SeqloaderConstants.TAB);
       if (splitLine.size() != 6) {
           RecordFormatException e = new RecordFormatException();
               e.bindRecord("The sequence record is not formatted correctly, " +
                   "6 tab delimited elements expected.\n" + rcd);
            throw e;
        }

        seqid = ((String)splitLine.get(0)).trim();
        startBP = new Integer( ((String)splitLine.get(2)).trim() );
        endBP = new Integer( ((String)splitLine.get(3)).trim() );
        // strip of the CRT
        description = ( (String)splitLine.get(5)).trim();
   }
   private void createRawSequence() {
       rawSeq.setType(seqType);
       rawSeq.setQuality(seqQuality);
       rawSeq.setStatus(seqStatus);
       rawSeq.setProvider(provider);
       rawSeq.setLength(
          (new Integer(endBP.intValue() - startBP.intValue() + 1)).toString());
       rawSeq.setDescription(description);
       rawSeq.setVersion(version);
       // use default null value for division
       rawSeq.setVirtual(virtual);
       // use default null value for rawType, rawLibrary, rawOrganism, rawStrain,
       // rawTissue, rawAge, rawSex, rawCellLine, numberOf Organisms
       rawSeq.setSeqRecDate(timeStamp);
       rawSeq.setSeqDate(timeStamp);
       sequenceInput.setSeq(rawSeq);
   }
   private void createRawAccession() {
       rawAcc.setAccid(seqid);
       rawAcc.setIsPreferred(preferredAcc);
       rawAcc.setIsPrivate(privateAcc);
       rawAcc.setLogicalDB(seqLogicalDB);
       rawAcc.setMgiType(seqMGIType);
       sequenceInput.setPrimaryAcc(rawAcc);
   }
}

