package org.jax.mgi.shr.dla.assemblyloader;

import java.util.*;
import java.sql.Timestamp;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.shr.config.SequenceLoadCfg;
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
   * Configured attributes
   */

    private String seqType;
    private String seqQuality;
    private String jNum;
    private String version;
    private String organism;
    private String strain;
    private String tissue;
    private String age;
    private String gender;
    private String cellLine;

    private Boolean preferredAcc = Boolean.TRUE;
    private Boolean privateAcc = Boolean.FALSE;

    // for seqDate and seqRecordDate
    private Timestamp seqDate;

    // record attributes
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
        // override superclass setting
        refAssocType = new Integer(MGIRefAssocTypeConstants.LOAD);
        // gets configuration values
        getConfig();
        // creates the single reference
        createRawReference();
        // creates the single source
        createRawSource();
  }

  /**
  * a predicate that returns false if 'record' is a commented record, else true
  * @assumes Nothing
  * @effects Nothing
  * @param record A MGS assembly format record
  * @return true if we want to load this record
  * @throws Nothing
  */
 public boolean isValid(String record) {
     if (!record.startsWith("#")) {
         return true;
     }
     else {
         return false;
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

   private void getConfig() throws ConfigException {
       // misc
       seqType = sequenceCfg.getType();
       seqQuality = sequenceCfg.getQuality();
       jNum = sequenceCfg.getJnumber();
       version = sequenceCfg.getReleaseNo();
       seqDate = sequenceCfg.getReleaseDate();
       // source
       organism = sequenceCfg.getOrganism();
       strain = sequenceCfg.getStrain();
       tissue = sequenceCfg.getTissue();
       age = sequenceCfg.getAge();
       gender = sequenceCfg.getGender();
       cellLine = sequenceCfg.getCellLine();
   }

    private void createRawSource() throws ConfigException {
        rawMS.setOrganism(sequenceCfg.getOrganism());
        rawMS.setStrain(sequenceCfg.getStrain());
        rawMS.setTissue(sequenceCfg.getTissue());
        rawMS.setAge(sequenceCfg.getAge());
        rawMS.setGender(sequenceCfg.getGender());
        rawMS.setCellLine(sequenceCfg.getCellLine());
    }

    private void createRawReference() throws ConfigException {
        rawRefAssoc.setRefId(sequenceCfg.getJnumber());
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
       rawSeq.setSeqRecDate(seqDate);
       rawSeq.setSeqDate(seqDate);
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

