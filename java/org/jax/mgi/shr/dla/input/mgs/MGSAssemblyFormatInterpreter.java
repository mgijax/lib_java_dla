package org.jax.mgi.shr.dla.input.mgs;

import java.util.*;
import java.sql.Timestamp;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSRawAttributes;
import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.shr.dla.input.SequenceInterpreter;
import org.jax.mgi.shr.dla.input.SequenceInput;
import org.jax.mgi.dbs.mgd.loads.Seq.SequenceRawAttributes;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.RefAssocRawAttributes;
import org.jax.mgi.dbs.mgd.loads.Acc.AccessionRawAttributes;

/**
 * An object that creates a SequenceInput object representing a MGS Assembly
 * Sequence<BR>
 * @has
 *   <UL>
 *   <LI>A SequenceInput object into which it bundles:
 *   <LI>A SequenceRawAttributes object
 *   <LI>An AccessionRawAttributes object for its seqid
 *   <LI>A RefAssocRawAttributes object for its reference
 *   <LI>A MSRawAttributes
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Parses a MGS Assembly format sequence record and obtains
 *     values from a Configurator to create a SequenceInput data object.
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
    private String seqidNoVersion;
    private String organism;
    private String strain;
    private String tissue;
    private String age;
    private String gender;
    private String cellLine;

    private Boolean preferredAcc = Boolean.TRUE;
    private Boolean notPreferredAcc = Boolean.FALSE;
    private Boolean privateAcc = Boolean.FALSE;

    // for seqDate and seqRecordDate
    private Timestamp seqDate;

    // File record attributes
    String record;
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

    // raw attributes for a sequences source, once set-same for all sequences
    private MSRawAttributes rawMS = new MSRawAttributes();

    // raw attributes for a sequences reference, once set-same for all sequences
    private RefAssocRawAttributes rawRefAssoc = new RefAssocRawAttributes();

    // raw attributes for the sequence's primary seqid,reused by calling reset()
    private AccessionRawAttributes rawAcc = new AccessionRawAttributes();

    // raw attributes for the sequence's 2ndary seqid, reused by calling reset()
    private AccessionRawAttributes rawAcc2 = new AccessionRawAttributes();

    /**
     * Constructs a MGSAssemblyFormatInterpreter
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
  * a predicate that returns false if 'record' starts with '#' (indicating a
  * comment), else true
  * @param record A MGS assembly format record
  * @return true if record does not start with '#'
  */
 public boolean isValid(String record) {
     if (record != null && !record.startsWith("#")) {
         return true;
     }
     else {
         return false;
     }
 }

    /**
    * Parses a MGS assembly format sequence record and  creates a SequenceInput
    * object from Configuration and parsed values
    * @param rcd a sequence record<BR>
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
	rawAcc2.reset();

        // get values from the record
        parseRecord(rcd);

        // add rawMS and rawRefAssoc to SequenceInput object
        sequenceInput.addMSource(rawMS);
        sequenceInput.addRef(rawRefAssoc);

        // create a SequenceRawAttributes; set in SequenceInput object
        createRawSequence();
        // create an AccessionRawAttributes for primary and secondary seqids
	// set in SequenceInput object
        createRawAccession();
	createRawSecondaryAccession();

       return sequenceInput;
   }
   /**
   * gets configurable MGS sequence attributes
   * @throws ConfigException if attribute not configured
   */

   private void getConfig() throws ConfigException {
       // sequence and reference attributes
       seqType = sequenceCfg.getSeqType();
       seqQuality = sequenceCfg.getQuality();
       jNum = sequenceCfg.getJnumber();
       if (provider.equals("NCBI Gene Model")) {
	   version = sequenceCfg.getReleaseNo();
       }
       seqDate = sequenceCfg.getReleaseDate();
       // source attributes
       organism = sequenceCfg.getOrganism();
       strain = sequenceCfg.getStrain();
       tissue = sequenceCfg.getTissue();
       age = sequenceCfg.getAge();
       gender = sequenceCfg.getGender();
       cellLine = sequenceCfg.getCellLine();
   }
   /**
    * sets source attributes in the MSRawAttributes object
    */

    private void createRawSource() {
        rawMS.setOrganism(organism);
        rawMS.setStrain(strain);
        rawMS.setTissue(tissue);
        rawMS.setAge(age);
        rawMS.setGender(gender);
        rawMS.setCellLine(cellLine);
    }

    /**
     * sets reference attributes in the RefAssocRawAttributes object
     * @throws ConfigException if there is an error accessing the configuration
     */

    private void createRawReference() throws ConfigException {
        rawRefAssoc.setRefId(jNum);
        rawRefAssoc.setMgiType(seqMGIType);
        rawRefAssoc.setRefAssocType(refAssocType);
    }
    /**
     * parses attributes from the record
     * @param rcd  a sequence record
     * @throws RecordFormatException if we can't parse an attribute because of
     *         record formatting errors
     */

   private void  parseRecord(String rcd) throws RecordFormatException {
       // save the record
       record = rcd;
       System.out.println(record);
       // split record into tokens
       String [] splitLine = rcd.split(SeqloaderConstants.TAB);
       if (splitLine.length != 6) {
           RecordFormatException e = new RecordFormatException();
               e.bindRecord("The sequence record is not formatted correctly, " +
                   "6 tab delimited elements expected.\n" + rcd);
            throw e;
        }
        // get the attributes
        seqid = ((String)splitLine[0]).trim();
	System.out.println("seqid " + seqid);
	// default seqId with no version number for NCBI
	seqidNoVersion = "";

	// Ensembl and Vega get version from seqid
	if (! provider.equals("NCBI Gene Model")){
	    String [] sArray = seqid.split("\\.");
	    System.out.println("sArray.length" + new Integer(sArray.length));
	    for (int i = 0; i <= sArray.length -1; i++) {
		System.out.println(sArray[i]);
	    
	    }

	    seqidNoVersion = sArray[0];
	    System.out.println(seqidNoVersion);

	    //seqidNoVersion = seqid.split(".")[0];
	    version = seqid.split("\\.")[1];

	}
	System.out.println("seqidNoVersion " + seqidNoVersion);
	System.out.println("version " + version);
        startBP = new Integer( ((String)splitLine[2]).trim() );
        endBP = new Integer( ((String)splitLine[3]).trim() );
        description = ( (String)splitLine[5]).trim();
   }

   /**
    * sets sequence attributes in the SequenceRawAttributes object; set
    * the SequenceRawAttributes in the SequenceInput object
    */

   private void createRawSequence() {
       rawSeq.setRecord(record);
       rawSeq.setType(seqType);
       rawSeq.setQuality(seqQuality);
       rawSeq.setStatus(seqStatus);
       rawSeq.setProvider(provider);
       rawSeq.setLength(
          (new Integer(endBP.intValue() - startBP.intValue() + 1)).toString());
       rawSeq.setDescription(description);
       rawSeq.setVersion(version);
       // Note: uses default null value for division
       rawSeq.setVirtual(virtual);
       // Note: uses default null value for rawType, rawLibrary, rawOrganism, rawStrain,
       // rawTissue, rawAge, rawSex, rawCellLine, numberOf Organisms
       rawSeq.setSeqRecDate(seqDate);
       rawSeq.setSeqDate(seqDate);
       // set rawSeq in the SequenceInput object
       sequenceInput.setSeq(rawSeq);
   }

   /**
    * sets accession attributes in the AccessionRawAttributes object; set
    * the AccessionRawAttributes in the SequenceInput object
    */

   private void createRawAccession() {
       rawAcc.setAccid(seqid);
       rawAcc.setIsPreferred(preferredAcc);
       rawAcc.setIsPrivate(privateAcc);
       rawAcc.setLogicalDB(seqLogicalDB);
       rawAcc.setMgiType(seqMGIType);
       sequenceInput.setPrimaryAcc(rawAcc);
   }

   /**
    * sets secondary accession attributes in the AccessionRawAttributes object
    * sets the AccessionRawAttributes in the SequenceInput object
    */

   private void createRawSecondaryAccession() {
       rawAcc2.setAccid(seqidNoVersion);
       rawAcc2.setIsPreferred(notPreferredAcc);
       rawAcc2.setIsPrivate(privateAcc);
       rawAcc2.setLogicalDB(seqLogicalDB);
       rawAcc2.setMgiType(seqMGIType);
       Vector v = new Vector();
       v.add(rawAcc2);
       sequenceInput.setSecondary(v);
   }

}
