//  $Header
//  $Name

package org.jax.mgi.shr.dla.input.embl;

import java.util.*;
import java.sql.Timestamp;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSRawAttributes;
import org.jax.mgi.dbs.mgd.loads.Acc.*;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.*;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.*;
import org.jax.mgi.dbs.mgd.loads.Seq.*;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;
import org.jax.mgi.shr.dla.input.SequenceInterpreter;
import org.jax.mgi.shr.dla.input.SequenceInput;
import org.jax.mgi.shr.dla.input.DateConverter;


    /**
     * An object that parses SwissProt sequence records and obtains
     *     values from a Configurator to create a SequenceInput data object.
     * <BR>
     *     Determines if a SwissProt sequence record is valid.
     * <BR>
     * @has
     *   <UL>
     *   <LI>A SequenceInput object into which it bundles:
     *   <LI>A SequenceRawAttributes object
     *   <LI>An AccessionRawAttributes object for its primary seqid
     *   <LI>One AccessionRawAttributes object for each secondary seqid
     *   <LI> A RefAssocRawAttributes object for each reference that has a
     *        PubMed and/or Medline id
     *   <LI> A MSRawAttributes for each organism represented by the sequence
     *   <LI> A set of String constants for parsing
     *   </UL>
     * @does
     *   <UL>
     *   <LI>Determines if a SwissProt sequence record is valid (is a
     *       mouse, human, or rat)
     *   <LI>Parses a SwissProt sequence record
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class EMBLFormatInterpreter extends SequenceInterpreter {

    /////////////////////////////////////////////////////////
    // String constants to find EMBL format seq record TAGS
    /////////////////////////////////////////////////////////

    // for length
    private static String ID = "ID";

    // for seqids
    private static String AC = "AC";

    // for dates
    private static String DT = "DT";

    // for description
    private static String DE = "DE";

    // for organism(s)
    private static String OS = "OS";

    // for reference(s)
    private static String RX = "RX";

    ///////////////////////////////////////////////
    // A SequenceInput and its parts            //
    //////////////////////////////////////////////

    // The object we are building. Represents raw attributes for a sequence,
    // its source, references, and accessions
    protected SequenceInput sequenceInput = new SequenceInput();

    // raw attributes for a sequence - reused by calling reset()
    private SequenceRawAttributes rawSeq = new SequenceRawAttributes();

    // checks a sequence record to see if it is an organism we want to load
    private EMBLOrganismChecker organismChecker;

    // vars that hold sequence record line(s) for later parsing
    private String idSection;
    private StringBuffer acSection;
    private StringBuffer dtSection;
    private StringBuffer deSection;
    private StringBuffer osSection;
    private StringBuffer rxSection;

    /**
    * Constructs a EMBL FormatInterpreter
    * @assumes Nothing
    * @effects Nothing
    * @param oc an EMBLFormatOrganismChecker
    * @throws ConfigException if can't find configuration file
    */

    public EMBLFormatInterpreter(EMBLOrganismChecker oc) throws ConfigException {
        // Create an organism checker for EMBL format
        this.organismChecker = oc;

        // Initialize vars that hold sequence record sections
        idSection = null;
        acSection = new StringBuffer();
        dtSection = new StringBuffer();
        deSection = new StringBuffer();
        osSection = new StringBuffer();
        rxSection = new StringBuffer();
      }

    /**
     * Determines whether this sequence is for an organism we want to load
     * @assumes Nothing
     * @effects Nothing
     * @param record A EMBL format sequence record
     * @return true if organism of the sequence is an organism we want to load
     * @throws Nothing
     */

    public boolean isValid(String record) {
        return (organismChecker.checkOrganism(record));
    }

    /**
     * Parses a sequence record and  creates a SequenceInput object from
     * Configuration and parsed values
     * @assumes Expects SequenceRawAttributes.quality and to be set by a subclass
     * or by caller calling seqInput.getSeq().setQuality();
     * @effects Nothing
     * @param rcd An EMBL format sequence record
     * @return A SequenceInput object representing 'rcd'
     * @throws RecordFormatException if we can't parse an attribute because of
     *         record formatting errors
     */

    public Object interpret(String rcd) throws RecordFormatException {

        // parse 'rcd' into individual sections
        parseRecord(rcd);

        //////////////////////////////////////////////////////////
        // Now parse the individual sections, set values in     //
        // *RawAttributes objects, and set *RawAttributes in    //
        // SequenceInput object                                 //
        //////////////////////////////////////////////////////////
        if (idSection != null ) {
            parseID(idSection);
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The ID section is empty");
            throw e;
        }
        if (acSection.length() > 0 ) {
            parseAC(acSection.toString());
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The AC section is empty");
            throw e;
        }
        if (dtSection.length() > 0 ) {
            parseDT(dtSection.toString());
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The DT section is empty");
            throw e;
        }
        if (deSection.length() > 0 ) {
            parseDE(deSection.toString());
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The DE section is empty");
            throw e;
        }
        if (osSection.length() > 0 ) {
            parseOS(osSection.toString());
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The OS section is empty");
            throw e;
        }

        // this method call also adds RefAssocRawAttributes objects to the
        // SequenceInput object
        if( rxSection.length() > 0) {
            parseRX(rxSection.toString());
        }

        // set attributes from Configuration (super class hold the values
        // of virtual, provider, and seqStatus)
        rawSeq.setVirtual(virtual);
        rawSeq.setProvider(provider);
        rawSeq.setStatus(seqStatus);
        // set rawSeq in 'sequenceInput
        sequenceInput.setSeq(rawSeq);

        return sequenceInput;
    }

    /**
     * Parses a sequence record into individual sections for later parsing
     * @assumes Nothing
     * @effects Nothing
     * @param rcd An EMBL format sequence record
     */

    protected void parseRecord(String rcd) {
        // re-initialize vars that hold sequence record sections
        idSection = null;
        acSection = new StringBuffer();
        dtSection = new StringBuffer();
        deSection = new StringBuffer();
        osSection = new StringBuffer();
        rxSection = new StringBuffer();

        // reset reused instance variables
        sequenceInput.reset();
        rawSeq.reset();

        // set the record attribute of the SequenceRawAttributes
        rawSeq.setRecord(rcd);

        // split the record into lines
        StringTokenizer lineSplitter = new StringTokenizer(rcd,
            SeqloaderConstants.CRT);
        String line;

        // iterate through each line getting individual sections of the sequence
        while (lineSplitter.hasMoreTokens()) {
            // be sure to trim each line so String.startsWith works properly
            line = lineSplitter.nextToken().trim();

            if (line.startsWith(ID)) {
                idSection = line;
            }
            //
            else if (line.startsWith(AC)) {
                acSection.append(line + SeqloaderConstants.CRT);
            }
            else if (line.startsWith(DT)) {
                dtSection.append(line + SeqloaderConstants.CRT);
            }
            else if (line.startsWith(DE)) {
                deSection.append(line + SeqloaderConstants.CRT);
            }
            else if (line.startsWith(OS)) {
                osSection.append(line + SeqloaderConstants.CRT);
            }
            else if (line.startsWith(RX)) {
                rxSection.append(line + SeqloaderConstants.CRT);
            }
        }
    }

    /**
     * Parses the annotation update date and the sequence update date from the
     * DT section of a EMBL format record
     * @assumes Nothing
     * @effects Nothing
     * @param dtSection The DT section of an EMBL format record
     * <BR>
     * Example of DT section:<BR>
     * DT   16-OCT-2001 (Rel. 40, Created)
     * DT   16-OCT-2001 (Rel. 40, Last sequence update)
     * DT   16-OCT-2001 (Rel. 40, Last annotation update)
     * <BR>
     * Note that SequenceRawAttributes.seqRecDate is set to the later of
     * sequence update and annotation update
     */

    protected void parseDT (String dtSection) {
        // split the DT section into lines
        StringTokenizer lineSplitter = new StringTokenizer(dtSection,
            SeqloaderConstants.CRT);

        // discard first DT line
        lineSplitter.nextToken();

        // tokenize the sequence update line
        StringTokenizer fieldSplitter = new StringTokenizer(lineSplitter.nextToken());

        // discard the DT tag field
        fieldSplitter.nextToken();

        // get the sequence update date and set in the raw sequence object
        Timestamp sequenceDate = DateConverter.convertDate(fieldSplitter.nextToken());
        rawSeq.setSeqRecDate(sequenceDate);

       // get the sequence annotation update line and tokenize it
       fieldSplitter = new StringTokenizer(lineSplitter.nextToken());

       // discard the DT tag field
       fieldSplitter.nextToken();

       // the sequence record date is the greater of the annotation data and
       // the sequence date
       Timestamp sequenceAnnotDate = DateConverter.convertDate(fieldSplitter.nextToken());

       // set the sequence date
       rawSeq.setSeqDate(sequenceDate);

       // set the sequence record date to the greater of the two dates
       if (sequenceAnnotDate.after(sequenceDate)) {
           rawSeq.setSeqRecDate(sequenceAnnotDate);
       }
       else {
           rawSeq.setSeqRecDate(sequenceDate);
       }
    }

    /**
     * Parses sets of MedLine and PubMed ids from the RX section of a EMBL
     * format sequence record if they exist. Creates a RefAssocRawAttributes
     * object for each id, bundles them in a pair, then sets the pair in the
     * SequenceInput object.
     * If a reference has only one type of id, the other in the SeqRefAssocPair is null.
     * @assumes Nothing
     * @effects Nothing
     * @param rxSection the RX section parsed from a EMBL format record
     * <BR>
     * RX section example, Note there can be multiple RX lines: <BR>
     * <PRE>
     * RX   MEDLINE=95372385; PubMed=7644510;
     * </PRE>
     */

    protected void parseRX(String rxSection) {
        // pubmed and medline ids
        String pubmed = null;
        String medline = null;

        // split the RX section into individual lines
        StringTokenizer lineSplitter = new StringTokenizer(
            rxSection, SeqloaderConstants.CRT);

        // get each RX line and split it into tokens on ';' then split each token
        // on '='
        while(lineSplitter.hasMoreTokens()) {
            // get the first RX line without the RX field tag e.g.
            // line = "MEDLINE=95372385; PubMed=7644510;"
            String line = lineSplitter.nextToken().trim().substring(5);

            // first token from rxSplitter looks like: "MEDLINE=95372385;"
            // second token from rxSplitter looks like: " PubMed=7644510;"
            StringTokenizer rxSplitter = new StringTokenizer(
               line, SeqloaderConstants.SEMI_COLON);
            if(rxSplitter.hasMoreTokens()) {
                // medline looks like: "95372385"
                medline = ((String) StringLib.split(rxSplitter.nextToken(), SeqloaderConstants.EQUAL).get(1)).trim();
            }
            if(rxSplitter.hasMoreTokens()) {
                // pubmed looks like: "7644510"
                pubmed = rxSplitter.nextToken();
           }

            // if we got any ids for this reference create reference objects and
            // add them to SequenceInput object
            if (pubmed != null || medline != null) {
                createReference(pubmed, medline);
                pubmed = null;
                medline = null;
            }
        }
    }

    /**
     * Parses the organism from the OS line of a EMBL format record.
     * Sets the organism in the SequenceRawAttributes and MSRawAttributes objects
     * Sets numberOfOrganisms in the SequenceRawAttributes object. Sets each
     * MSRawAttributes in the SequenceInput object.
     * @assumes Nothing
     * @effects Nothing
     * @param osSection The OS section from a EMBL format record
     * <BR>
     * OS line example:<BR>
     * <PRE>
     * OS   Homo sapiens (Human),
     * OS   Mus musculus (Mouse),
     * OS   Rattus norvegicus (Rat),
     * OS   Bos taurus (Bovine), and
     * OS   Ovis aries (Sheep).
     * </PRE>
     */

    protected void parseOS(String osSection) {
        StringTokenizer lineTokenizer = new StringTokenizer(osSection,
            SeqloaderConstants.CRT);
        String line;
        String organism;
        StringBuffer rawOrganism = new StringBuffer();
        int otherCtr = 0;

        // Flags to create only 1 each MSRawAttributes for human, mouse and rat
        // - e.g. some TrEMBL records have two mouse species:
        // OS   Mus musculus castaneus (Southeastern Asian house mouse), and
        // OS   Mus musculus musculus (eastern European house mouse).
        boolean mouse = false;
        boolean human = false;
        boolean rat = false;

        while (lineTokenizer.hasMoreTokens()) {
            // MSRawAttributes ms = new MSRawAttributes();
            // e.g. 'line' looks like "Homo sapiens (Human),"
            line = lineTokenizer.nextToken().substring(5);
            StringTokenizer fieldTokenizer = new StringTokenizer(line,
                SeqloaderConstants.OPEN_PAREN);
            if (fieldTokenizer.hasMoreTokens()) {
                // e.g. 'organism' looks like "Homo sapiens"
                organism = fieldTokenizer.nextToken().trim();
                // append organism to rawOrganism string with a comma separater
                rawOrganism.append(organism + SeqloaderConstants.COMMA);

                // check for human/mouse/rat
                // count the number of non-human/mouse/rat organisms
                if ( ! organismChecker.isHumanMouseOrRat(organism)) {
                    otherCtr++;
                }
                else {
                    if (organismChecker.isMouse(organism) && mouse == false) {
                        mouse = true;
                        setOrganism(organism);
                    }
                    else if (organismChecker.isRat(organism) && rat == false) {
                        rat = true;
                        setOrganism(organism);
                    }
                    else if (organismChecker.isHuman(organism) && human == false) {
                        human = true;
                        setOrganism(organism);
                    }
                }
            }
        }
        // create an 'Other' molecular source if we found a non-human/mouse/rat
        // organism
        if(otherCtr > 0) {
            MSRawAttributes ms = new MSRawAttributes();
            ms.setOrganism(SeqloaderConstants.OTHER);
            // add the MS to the SequenceInput object
            sequenceInput.addMSource(ms);
        }

        // set rawOrganism string in the raw sequence
        rawSeq.setRawOrganisms(rawOrganism.toString());

        // set the count of non-human/mouse/rat organisms the raw sequence
        rawSeq.setNumberOfOrganisms(otherCtr);
    }

    /**
     * creates an MSRawAttributes for 'organism' and sets it in the sequenceInput
     * @assumes Nothing
     * @effects Nothing
     * @param organism an organism for the SequenceInput being created
     */

    private void setOrganism(String organism) {
        MSRawAttributes ms = new MSRawAttributes();
        ms.setOrganism(organism);
        sequenceInput.addMSource(ms);
    }

    /**
     * Parses seqids from the AC section of a EMBL format sequence record.
     * Creates an AccessionRawAttributes object for each seqid.
     * The first seqid on first line is primary, any remaining seqids
     * are secondary.
     * Sets the Primary and Secondary AccessionRawAttributes in the SequenceInput
     * object
     * @assumes Nothing
     * @effects Nothing
     * @param acSection The AC section of a EMBL format sequence record.
     * <BR>
     * example of AC section. Note: secondary ids can span multiple AC lines:<BR>
     * AC   Q9CQV8; O70455;
     */

    protected void parseAC(String acSection) {
        // split the AC section into individual lines
        StringTokenizer lineSplitter = new StringTokenizer(
            acSection, SeqloaderConstants.CRT);

        // get first AC line
        String line = lineSplitter.nextToken().trim();

        // tokenize the first line
        StringTokenizer fieldSplitter = new StringTokenizer(line);

        // discard the AC tag field
        fieldSplitter.nextToken();

        // get the primary seqid
        String seqid = fieldSplitter.nextToken().trim();

        // remove the trailing ';'
        seqid = seqid.substring(0, seqid.length() - 1);

        // create a primary accession object
        createAccession(seqid, Boolean.TRUE);

        // create 2ndary accessions from first AC line
         while(fieldSplitter.hasMoreTokens())
         {
             seqid = fieldSplitter.nextToken().trim();

             // remove the trailing ';'
             seqid = seqid.substring(0, seqid.length() - 1);

             // add a secondary accession to SequenceInput
             createAccession(seqid, Boolean.FALSE);
         }

         // create 2ndary accessions from remaining ACCESSION lines
          while(lineSplitter.hasMoreTokens()) {
              line = lineSplitter.nextToken().trim();
              fieldSplitter = new StringTokenizer(line);

              // discard the AC tag field
              fieldSplitter.nextToken();
              while(fieldSplitter.hasMoreTokens()) {
                  seqid = fieldSplitter.nextToken().trim();

                  // remove the trailing ';'
                  seqid = seqid.substring(0, seqid.length() - 1);

                  // add a secondary accession to SequenceInput
                  createAccession(seqid, Boolean.FALSE);

                }
          }
    }

    /**
     * Parses the description from the DE section of a EMBL format record
     * Sets the description in the SequenceRawAttributes object.
     * Truncates the definition to 255 characters if needed
     * @assumes Nothing
     * @effects Nothing
     * @param deSection The DE section of a EMBL format record
     * <BR>
     * Example of DE section:<BR>
     * DE   14-3-3 protein beta/alpha (Protein kinase C inhibitor protein-1)
     * DE   (KCIP-1).
     * <BR>
     */

    protected void parseDE(String deSection) {
        StringTokenizer lineTokenizer = new StringTokenizer(deSection, SeqloaderConstants.CRT);
        StringBuffer descript = new StringBuffer();

        // create the description removing the DE tags
        while(lineTokenizer.hasMoreTokens()){
            descript.append(lineTokenizer.nextToken().substring(5));
        }
        // set description in the raw sequence
        rawSeq.setDescription(descript.toString());
    }

    /**
     * Parses length from the ID section (always 1 line) of a EMBL format
     * sequence record and sets length in the SequenceRawAttributes object<BR>
     * @assumes Nothing
     * @effects Nothing
     * @param idLine The ID line of a EMBL format sequence record.
     * <BR>
     * Example of EMBL format ID line:<BR>
     * ID   143B_MOUSE     STANDARD;      PRT;   245 AA.
     * <BR>
     * Parse the ID line
     * There is only one ID line per record. There are
     * always 6 tokens in an EMBL-format PROTEIN sequence<BR>
     * token 0 = "ID", the tag for this line<BR>
     * token 1 = Entry Name<BR>
     * token 2 = Data Class<BR>
     * token 3 = Molecule type (PRT for protein records)<BR>
     * token 4 = sequence length<BR>
     * token 5 = sequence type (AA (amino acid) for protein)<BR>
     */

    protected void parseID(String idLine) {
      StringTokenizer fieldSplitter = new StringTokenizer(idLine);

       // we want the 5th token
       for (int i = 0; i < 4; i++) {
           fieldSplitter.nextToken();
       }
       // set length in the raw sequence object
       rawSeq.setLength(fieldSplitter.nextToken().trim());
    }

    /**
     * Creates one RefAssocRawAttributes object each for a pubmed id and a
     * medline id,  bundles them in a SeqRefAssocPair, then sets the pair in the
     * SequenceInput object. If 'pubmed' or 'medline' is null, then the
     * RefAssociationRawAttribute for that id is null
     * @assumes Nothing
     * @effects Nothing
     * @param pubmed Pubmed id for a reference or null
     * @param medline Medline id for the same reference or null
     */

    protected void createReference (String pubmed, String medline) {
        // create a pubmed object
        RefAssocRawAttributes pm = null;
        if(pubmed != null) {
            pm = new RefAssocRawAttributes();
            pm.setRefId(pubmed);
            pm.setRefAssocType(this.refAssocType);
            pm.setMgiType(this.seqMGIType);
        }
        // create a medline object
        RefAssocRawAttributes ml = null;
        if(medline != null) {
            ml = new RefAssocRawAttributes();
            ml.setRefId(medline);
            ml.setRefAssocType(this.refAssocType);
            ml.setMgiType(this.seqMGIType);
        }
        // create a pair and add to the SequenceInput references
        sequenceInput.addRef(new SeqRefAssocPair(pm, ml));
    }

    /**
     * Creates an AccessionRawAttributes object and sets it in the SequenceInput
     * object.
     * @assumes Nothing
     * @effects Nothing
     * @param accid Accession id of a sequence
     * @param preferred true, 'accid' is primary. false 'accid' is 2ndary
     */

    protected void createAccession(String accid, Boolean preferred) {

        AccessionRawAttributes seqid = new AccessionRawAttributes();

        // set attributes of AccessionRawAttributes
        seqid.setAccid(accid);
        seqid.setIsPreferred(preferred);

        //  seqids are public
        seqid.setIsPrivate(Boolean.FALSE);

        // set attributes from Configuration
        seqid.setLogicalDB(seqLogicalDB);
        seqid.setMgiType(seqMGIType);

        // set in SequenceInput
        if(preferred.equals(Boolean.TRUE)) {
            sequenceInput.setPrimaryAcc(seqid);
        }
        else {
            sequenceInput.addSecondary(seqid);
        }
    }
}

//  $Log

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
