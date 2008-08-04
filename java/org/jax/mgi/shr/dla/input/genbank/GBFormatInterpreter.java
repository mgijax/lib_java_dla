package org.jax.mgi.shr.dla.input.genbank;

import java.util.*;
import java.sql.*;

import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSRawAttributes;
import org.jax.mgi.dbs.mgd.loads.Acc.*;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.*;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.*;
import org.jax.mgi.dbs.mgd.loads.Seq.*;
import org.jax.mgi.shr.dla.input.SequenceInterpreter;
import org.jax.mgi.shr.dla.input.SequenceInput;
import org.jax.mgi.shr.dla.input.DateConverter;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;


    /**
     * An object that parses a GenBank format sequence records and obtains
     *     values from a Configurator to create a SequenceInput data object.
     * <BR>
     *     Determines if a GenBank format sequence record is valid.
     * <BR>
     *     Note that GenBank and RefSeq data providers both use this format, but
     *     RefSeq's do not require a reference section and protein RefSeq's
     *     have sequence type in a different column range in the LOCUS line than
     *     GenBank provider sequences
     * @has
     *   <UL>
     *   <LI>A SequenceInput object into which it bundles:
     *   <LI>A SequenceRawAttributes object
     *   <LI>An AccessionRawAttributes object for its primary seqid
     *   <LI>One AccessionRawAttributes object for each secondary seqid
     *   <LI> A RefAssocRawAttributes object for each PubMed reference 
     *   <LI> A MSRawAttributes
     *   <LI> A set of String constants for parsing
     *   </UL>
     * @does
     *   <UL>
     *   <LI>Determines if a GenBank format sequence record is valid (is a
     *       mouse, human, or rat)
     *   <LI>Parses a GenBank format sequence record
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class GBFormatInterpreter extends SequenceInterpreter {

    // String constants to find GB seq record TAGS
    private static String LOCUS = "LOCUS";
    private static String DEFINITION = "DEFINITION";
    private static String ACCESSION = "ACCESSION";
    private static String VERSION = "VERSION";
    private static String ORGANISM = "ORGANISM";
    private static String REFERENCE = "REFERENCE";
    private static String MEDLINE = "MEDLINE";
    private static String PUBMED = "PUBMED";
    private static String COMMENT = "COMMENT";
    private static String FEATURES = "FEATURES";
    private static String KEYWORDS = "KEYWORDS";
    private static String ORIGIN = "ORIGIN";

    // int constants to indicate the current
    // record section we are looking for
    private static int LOCUS_SECTION = 1;
    private static int DEFINITION_SECTION = 2;
    private static int ANOTHER_DEF_LINE = 3;
    private static int ACCESSION_SECTION = 4;
    private static int ANOTHER_ACCESSION_LINE = 5;
    private static int ORGANISM_SECTION = 6;
    private static int REFERENCE_SECTION = 7;
    private static int ANOTHER_REFERENCE_LINE = 8;
    private static int SOURCE_SECTION = 9;
    private static int ANOTHER_SOURCE_LINE = 10;
    private static int KEYWORDS_SECTION = 11;
    private static int ANOTHER_KEYWORDS_LINE = 12;
    private static int COMMENT_SECTION = 13;
    private static int SOURCE_SECTION2 = 14;
    private static int ANOTHER_SOURCE_LINE2 = 15;

    // this is the FEATURES sub-section keyword 'source' as opposed to tag
    // 'SOURCE'
    private static String SOURCE = "source";

    // Strings to find GB seq record source qualifiers
    private static String LIBRARY = "/clone_lib";
    private static String STRAIN = "/strain";
    private static String TISSUE= "/tissue_type";
    private static String AGE = "/dev_stage";
    private static String SEX = "/sex";
    private static String CELLINE = "/cell_line";
    // added 2/07 for gene traps
    private static String CLONE = "/clone";
    // added 6/15 for gene traps
    private static String NOTE = "/note";
    // added 9/27 for gene traps (TIGM vector is found here!?)
    private static String FEATURE_ORGANISM = "/organism";

    ////////////////////////////////////////////////////////////////////////
    // A SequenceInput, rawSeq and ms declared here so multiple methods
    // may have access to them
    ////////////////////////////////////////////////////////////////////////

    // The object we are building. Represents raw attributes for a sequence,
    // its source, references, and accessions
    protected SequenceInput sequenceInput;

    // raw attributes for a sequence
    private SequenceRawAttributes rawSeq;

    // raw attributes for a sequences source
    private MSRawAttributes ms;

    // checks a sequence record to see if it is an organism we want to load
    private GBOrganismChecker organismChecker;

    // vars that hold sequence record sections for later parsing
    private String locus;
    private StringBuffer definition;
    private StringBuffer accession;
    private String version;
    private String organism;
    private StringBuffer classification;
    private StringBuffer reference;
    private StringBuffer comment;
    private StringBuffer source;
    private StringBuffer secondarySource;
    private StringBuffer keywords;

    /**
    * Constructs a GenBankFormatInterpreter
    * @assumes Nothing
    * @effects Nothing
    * @param oc a GBOrganismChecker
    * @throws ConfigException if can't find configuration file
    */

    public GBFormatInterpreter(GBOrganismChecker oc) throws ConfigException {

        // Create an organism checker for GenBank
        this.organismChecker = oc;

        // Initialize vars that hold sequence record sections for later parsing
        locus = null;
        definition = new StringBuffer();
        accession  = new StringBuffer();
        version = null;
        organism = null;
        classification  = new StringBuffer();
        reference = new StringBuffer();
        comment = new StringBuffer();
        source = new StringBuffer();
        secondarySource = new StringBuffer();
        keywords = new StringBuffer();
    }

    /**
     * Determines whether this sequence is for
     * an organism we want to load
     * @assumes Nothing
     * @effects Nothing
     * @param record A GenBank format sequence record
     * @return true if we want to load this sequence
     * @throws Nothing
     */

    public boolean isValid(String record) {
        return (organismChecker.checkOrganism(record));
    }

    /**
     * Parses a sequence record and  creates a SequenceInput object from
     * Configuration and parsed values
     * @assumes Expects SequenceRawAttributes.quality and SequenceRawAttributes.
     * type to be set by a subclass OR by caller of this method calling
     * seqInput.getSeq().setQuality(quality)
     * and seqInput.getSeq().setType(type)
     * @effects Nothing
     * @param rcd A sequence record
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
        if (locus != null ) {
            parseLocus(locus);
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The LOCUS section is empty:\n" + rcd);
            throw e;
        }

        if (version !=  null) {
            parseVersion(version);
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The VERSION section is empty:\n" + rcd);
            throw e;
        }
        if(organism != null ) {
            parseOrganism(organism);
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The ORGANISM section is empty:\n" + rcd);
            throw e;
        }
	if(comment.length() > 0 ) {
	    parseComment(comment.toString());
 	}
        if (source.length() > 0 ) {
            parseSource(source.toString()  );
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The SOURCE section is empty:\n" + rcd);
            throw e;
        }

        if (definition.length() > 0) {
            parseDefinition(definition.toString());
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The DEFINITION section is empty:\n" + rcd);
            throw e;
        }

        // this method also adds AccessionRawAttributes objects
        // for the primary seqid and any secondary seqids to the SequenceInput object
        if (accession.length() > 0 ) {
            parseAccession(accession.toString());
        }
        else {
            RecordFormatException e = new RecordFormatException();
            e.bindRecord("The ACCESSION section is empty:\n" + rcd);
            throw e;
        }

        // this method also adds RefAssocRawAttributes objects to the
        // SequenceInput object
        // Note: Not all RefSeq sequences have REFERENCE sections
        if(reference.length() > 0) {
            parseReference(reference.toString());
        }
        // Note: unannotated sequences do not have a KEYWORDS section
        if (keywords.length() >0 )
          parseKeywords(keywords.toString());

        // set attributes from Configuration (super class hold the values
        // of virtual, provider, and seqStatus)
        rawSeq.setVirtual(virtual);
        rawSeq.setProvider(provider);
        rawSeq.setStatus(seqStatus);

        // add 'ms' to 'sequenceInput'
        sequenceInput.addMSource(ms);

        // set rawSeq in 'sequenceInput
        sequenceInput.setSeq(rawSeq);

        return sequenceInput;
    }

    /**
     * Parses a sequence record into individual sections for later parsing
     * @assumes Nothing
     * @effects Nothing
     * @param rcd A sequence record
     */

    protected void parseRecord(String rcd) {
        // objects we are building
        sequenceInput = new SequenceInput();
        rawSeq = new SequenceRawAttributes();
        ms = new MSRawAttributes();

        // the current section we are looking for
        int currentSection = LOCUS_SECTION;

        // re-initialize vars that hold sequence record sections for later parsing
        locus = null;
        definition = new StringBuffer();
        accession = new StringBuffer();
        version = null;
        organism = null;
        classification = new StringBuffer();
        reference = new StringBuffer();
        source = new StringBuffer();
        keywords = new StringBuffer();
        comment = new StringBuffer();

        // set the record attribute of the SequenceRawAttributes - we use this
        // to write out repeated sequences
        rawSeq.setRecord(rcd);

        // split the record into lines
        StringTokenizer lineSplitter = new StringTokenizer(rcd, SeqloaderConstants.CRT);
        String line;

        // iterate through each line getting individual sections of the sequence
        while (lineSplitter.hasMoreTokens()) {
          // be sure to trim each line so String.startsWith works properly
          line = lineSplitter.nextToken().trim();
          // if we are currently looking for the LOCUS line check to see if
          // 'line' is the LOCUS line
          if (currentSection== LOCUS_SECTION) {
            if (line.startsWith(LOCUS)) {
              // get the LOCUS line
              locus = line;
              currentSection = DEFINITION_SECTION;
            }
          }
          // if we are currently looking for the DEFINITION line check to see
          // if 'line' is the DEFINITION line
          else if (currentSection == DEFINITION_SECTION) {
            if (line.startsWith(DEFINITION)) {
              // get the first DEFINITION line
              definition.append(line);

              // > 1 def line if first line does not end w/PERIOD
              if (!line.endsWith(SeqloaderConstants.PERIOD)) {
                // Now were looking for another definition line
                currentSection = ANOTHER_DEF_LINE;
              }
              else {
                // now we are looking for the ACCESSION line
                currentSection = ACCESSION_SECTION;
              }
            }
          }
          // if we are currently looking for another definition line, then get
          // it
          else if (currentSection == ANOTHER_DEF_LINE) {
            //get another DEFINITION line
            definition.append(SeqloaderConstants.SPC + line);

            // period indicates last def line
            if (line.endsWith(SeqloaderConstants.PERIOD)) {
              // now we are looking for the ACCESSION line
              currentSection = ACCESSION_SECTION;
            }
          }
          // if we are currently looking for the first ACCESSION line check to see
          // if 'line' is the ACCESSION line
          else if (currentSection == ACCESSION_SECTION) {
            if (line.startsWith(ACCESSION)) {
              // get the first ACCESSION line
              accession.append(line + SeqloaderConstants.CRT);
              // now we are looking for another accession line
              currentSection = ANOTHER_ACCESSION_LINE;
            }
          }
          // if we are currently looking for another accession line
          else if (currentSection == ANOTHER_ACCESSION_LINE) {
            // if 'line' is not the VERSION line, it is another accession line
            if (!line.startsWith(VERSION)) {
              accession.append(line + SeqloaderConstants.CRT);
            }
            // if 'line' is the VERSION line get it. Now we are looking for the
            // KEYWORDS line
            else {
              // now we are looking for KEYWORDS
              version = line;
              currentSection = KEYWORDS_SECTION;
            }
          }
          else if (currentSection == KEYWORDS_SECTION) {
             if (line.startsWith(KEYWORDS)) {
                 // get the first keywords line
                 keywords.append(line);
                 // > 1 def line if first line does not end w/PERIOD
                 if (!line.endsWith(SeqloaderConstants.PERIOD)) {
                     currentSection = ANOTHER_KEYWORDS_LINE;
                 }
                 else {
                     currentSection = ORGANISM_SECTION;
                 }
              }
          }
          // if we are currently looking for another keyword line
          else if (currentSection == ANOTHER_KEYWORDS_LINE) {
              keywords.append(SeqloaderConstants.SPC + line);
              if (line.endsWith(SeqloaderConstants.PERIOD)) {
                  currentSection = ORGANISM_SECTION;
              }
          }

          // if we are currently looking for the ORGANISM line check to see if
          // 'line is the ORGANISM line
          else if (currentSection == ORGANISM_SECTION) {
            if (line.startsWith(ORGANISM)) {
              // get the ORGANISM line
              organism = line;

	      // now we are looking for the first REFERENCE line
              currentSection = REFERENCE_SECTION;
	    }
          }

          // if we are currently looking for the first REFERENCE line check to
          // see if 'line' is the REFERENCE line
          else if (currentSection == REFERENCE_SECTION ) {
            if (line.startsWith(REFERENCE)) {
              // get the first REFERENCE line
              reference.append(line + SeqloaderConstants.CRT);
              currentSection = ANOTHER_REFERENCE_LINE;
            }
            // records without references won't have a reference line
            // the next line we want is the COMMENT line
            else if (line.startsWith(COMMENT)) {
		  comment.append(line  + SeqloaderConstants.CRT);
                  currentSection = COMMENT_SECTION;
            }
	    // added after source section not found when no COMMENT section
	    else if(line.startsWith(FEATURES)) {
		 currentSection = SOURCE_SECTION;
	    }
          }
	   // if we are looking for another REFERENCE line
	  else if (currentSection ==  ANOTHER_REFERENCE_LINE) {
	  // check to see if the 'line' is the COMMENT line
	      if (line.startsWith(COMMENT)){
                  comment.append(line  + SeqloaderConstants.CRT);
                    currentSection = COMMENT_SECTION;
               }
	       else if (line.startsWith(FEATURES)) {
			 currentSection = SOURCE_SECTION;
		}
		else {
		    reference.append(line + SeqloaderConstants.CRT);
	       }
          }
	  else if(currentSection == COMMENT_SECTION) {
		 if (!line.startsWith(FEATURES)) {
		    comment.append(line  + SeqloaderConstants.CRT);
		  }
		  // records without comments won't have a COMMENT line
            	  // the next line we want is the FEATURES line where we will start
                  // looking for first features source line
		 else {
		     currentSection = SOURCE_SECTION;
		 }
	  }
          // if we are looking for the first FEATURES source line, check to
          // see if 'line' is a FEATURES source line
          else if (currentSection == SOURCE_SECTION) {
            if (line.startsWith(SOURCE)) {
              // get the first features source line
              source.append(line + SeqloaderConstants.CRT);

              // now we are looking for another features source line
              currentSection = ANOTHER_SOURCE_LINE;
            }
          }
          // if we are looking for another features source line
          else if (currentSection == ANOTHER_SOURCE_LINE) {
            // get the rest of the FIRST features source section; if we find
            // the ORIGIN line we are done
            if (!line.startsWith(SOURCE) && !line.startsWith(ORIGIN)) {
              source.append(line + SeqloaderConstants.CRT);
            }
            // if there is a second feature source section get it
            // added for gene traps 10/01/07
            else if (line.startsWith (SOURCE) ) {
                secondarySource.append(line + SeqloaderConstants.CRT);
                currentSection = ANOTHER_SOURCE_LINE2;
            }
            else {
              break;
            }
          }
          else if (currentSection == ANOTHER_SOURCE_LINE2) {
              // get the rest of the SECOND features source section; if we find
              // another source section or the ORIGIN line we are done
              if (!line.startsWith(SOURCE) && !line.startsWith(ORIGIN)) {
                  secondarySource.append(line + SeqloaderConstants.CRT);
            }
            else {
                break;
            }
          }
        }
    }

    /**
    * Parses molecular source attributes from the first FEATURES source
    * section of a GenBank sequence record and sets them in MSRawAttributes
    * and SequencRawAttributes objects
    * @assumes Nothing
    * @effects Nothing
    * @param source first FEATURES source section parsed from a GenBank
    *        * sequence record<BR>
    * FEATURES source section example, note that the first and last lines
    * (FEATURES and ORIGIN are not included in the text of 'source' but added
    *  here for clarity. Source 'qualifiers' start with '/'. Only '/organism' and
    *  '/mol_type' are required. There is no guarantee of order for source
    *  qualifiers. Qualifier values may span multiple lines.The following is is a contrived source section used in
    *  testing:<BR>
    * <PRE>
    * FEATURES             Location/Qualifiers
    *      source          1..3133
    *                      /organism="Mus musculus"
    *                      /db_xref="taxon:10090"
    *                      /mol_type="mRNA"
    *                      /tissue_type="placenta day 20"
    *                      /dev_stage="dpc 14.5"
    *                      /strain="129SJVMus"
    *                      /sex="Female"
    *                      /cell_line="MS-1"
    *       CDS            155..1597
    *                      /codon_start=1
    *                      /product="GATA-2 protein"
    *                      /protein_id="BAA19053.1"
    *                      /db_xref="GI:1754586"
    *                      /translation="MEVAPEQPRWMAHPAVLNAQHPDSHHPGLAHNYMEPAQLLPPDE
    *                      VDVFFNHLDSQGNPYYANPAHARARVSYSPAHARLTGGQMCRPHLLHSPGLPWLDGGK
    *      polyA_site      3133
    *                      /note="16 A nucleotides"
    *      BASE COUNT      681 a    949 c    828 g    675 t
    *      ORIGIN
    * </PRE>
    */

    protected void parseSource(String source) {
        // Split the source section into individual lines
        StringTokenizer lineSplitter = new StringTokenizer(
            source, SeqloaderConstants.CRT);
        String line;

        // a qualifier line split into qualifier and value on '='
        ArrayList splitLine = null;

        // the source qualifier
        String qualifier = null;

        // value of the source qualifier
        String value = null;
        while(lineSplitter.hasMoreTokens()) {
            line = lineSplitter.nextToken().trim();

            // all qualifiers start with '/'
            if (line.startsWith(SeqloaderConstants.SLASH)) {
                // split the qualifier line on '='
                splitLine = StringLib.split(line, SeqloaderConstants.EQUAL);

                // not all qualifiers require values (no '=') and not all
                // qualifiers that require values have values (these do have '=').
                // The qualifiers we are looking for *should* all have values.
                if (splitLine.size() == 2) {
                    // get the qualifier e.g. "/strain" and value e.g. "BALB/c"
                    qualifier = (String) splitLine.get(0);
                    value = (String) splitLine.get(1);
                    // The values we are interested are surrounded by double quotes
                    // Note some values don't have dbl quotes
                    if(value.charAt(0) != '"' ) {
                      continue;
                    }
                    // get the first line which is all we originally parsed
                    // need to map new translations
                    // covers this case:
                    //       /strain="
                    //       BALB/c"
                    char lastchar;
                    if (value.length() == 1) {
                       // set lastchar to anything but '"' for while test below
                       lastchar = 'x';
                    }
                    // Note: using char because testing for doubleQuote did not work
                    // if last char is not double quote - value is multi-line
                    else {
                        lastchar = value.charAt(value.length()-1);
                    }

                    // double quote signals end of value, but just in case there
                    // is no ending double quote set a ctr to avoid infinite loop
                    int ctr = 0;
                    while( lastchar != '"' && ctr < 10) {
                        ctr++;
                        if(lineSplitter.hasMoreTokens()) {
                             value = value + " " + lineSplitter.nextToken().trim();
                             lastchar = value.charAt(value.length()-1);
                        }
                    }

                    // remove leading and trailing quote
                    value = value.substring(1, value.length()-1);

                    // set source and raw sequence attributes
                    if (qualifier.startsWith(LIBRARY)) {
                        ms.setLibraryName(value);
                        rawSeq.setLibrary(value);
                    }
                    else if (qualifier.startsWith(STRAIN)) {
                      ms.setStrain(value);
                      rawSeq.setStrain(value);
                    }
                    else if (qualifier.startsWith(TISSUE)) {
                        ms.setTissue(value);
                        rawSeq.setTissue(value);
                    }
                    else if (qualifier.startsWith(AGE)) {
                        rawSeq.setAge(value);
                    }
                    else if (qualifier.startsWith(SEX)) {
                        ms.setGender(value);
                        rawSeq.setSex(value);
                    }
                    else if (qualifier.startsWith(CELLINE)) {
                        ms.setCellLine(value);
                        rawSeq.setCellLine(value);
                    }
                    else if (qualifier.startsWith(CLONE)) {
                        rawSeq.setCloneId(value);
                    }
                    else if (qualifier.startsWith(NOTE)) {
                        rawSeq.setNote(value);
                    }
                    else if (qualifier.startsWith(FEATURE_ORGANISM)) {
                        rawSeq.setFeatureOrganism(value);
                    }
                }

            }
        }
        // 2ndary source - this is the SECOND source section which we will not
        // parse furthur.
        /* For TIGM gene traps looks like:
               source          1..30
                     /organism="Gene trapping vector VICTR76"
                     /mol_type="genomic DNA"
                     /db_xref="taxon:447635"
              LTR             <1..30
        */
       rawSeq.set2ndarySource(secondarySource.toString());

    }

    /**
     * Parses PubMed ids from all REFERENCE sections in a
     * GenBank sequence record where they exist. Creates a RefAssocRawAttributes
     * object for each id and sets them in the
     * SequenceInput object.
     * @assumes Nothing
     * @effects Nothing
     * @param reference All REFERENCE sections parsed from a GenBank sequence record
     * <BR>
     * REFERENCE section example, note that this is a contrived source section
     * used in testing. I added reference ids found in MGI: <BR>
     * <PRE>
     * REFERENCE   1  (bases 1 to 3133)
     *   AUTHORS   Yamamoto,M.
     *   TITLE     Direct Submission
     *   JOURNAL   Submitted (25-DEC-1996) Masayuki Yamamoto, Institute of Basic
     *             Medical Sciences, University of Tsukuba, Molecular and
     *             Developmental Biology; Tennoudai, Tsukuba, Ibaragi 305, Japan
     *             (E-mail:masiya@igaku.md.tsukuba.ac.jp, Tel:81-0298-53-3111,
     *             Fax:81-0298-53-6965)
     * REFERENCE   2  (sites)
     *   AUTHORS   Suwabe,N., Minegishi,N. and Yamamoto,M.
     *   TITLE     mouse GATA-2 cDNA
     *   JOURNAL   Unpublished (1996)
     *   MEDLINE   92239361
     *   PUBMED    1571281
     * </PRE>
     */

    protected void parseReference(String reference) {
        // a pubmed id
        String pubmed = null;

        // split the REFERENCE section into individual lines
        StringTokenizer lineSplitter = new StringTokenizer(
            reference, SeqloaderConstants.CRT);
        String line;
        // get the first REFERENCE line and throw it away
        line = lineSplitter.nextToken().trim();
        
	while(lineSplitter.hasMoreTokens()) {
            line = lineSplitter.nextToken().trim();

            // get pubmed id for one reference
            while (!line.startsWith(REFERENCE) ){
                if (line.startsWith(PUBMED)) {
                    pubmed = ( (String) StringLib.split(line).get(1)).trim();
		    createReference(pubmed);
                }
                if (lineSplitter.hasMoreTokens()) {
                  line = lineSplitter.nextToken().trim();
                }
                // this was the last line of 'reference'
                else {
                  break;
                }
            }
        }
    }

    /**
     * Parses the organism from the ORGANISM line of a GenBank sequence record.
     * Sets the organism in the SequenceRawAttributes and MSRawAttributes objects
     * Sets numberOfOrganisms to 0
     * @assumes Nothing
     * @effects Nothing
     * @param organism The ORGANISM line from a GenBank sequence record
     * <BR>
     * ORGANISM line example:<BR>
     * <PRE>
     *   ORGANISM  Mus musculus
     * </PRE>
     * @throws RecordFormatException if 'organism contains just the tag
     * "ORGANISM  "
     */

    protected void parseOrganism(String organism)  throws RecordFormatException {
    // 'organism' not long enough to accomodate tag AND species/genus
        if (organism.length() < 10) {
            throw new RecordFormatException();
        }

        String rawOrganism = organism.substring(9).trim();
        // set the organism field of raw sequence and raw molecular source
        rawSeq.setRawOrganisms(rawOrganism);
        rawSeq.setNumberOfOrganisms(0);
        ms.setOrganism(rawOrganism);
    }


    /**
     * Parses the version number from the VERSION line of a GenBank sequence
     * record. Sets the version in the SequenceRawAttributes object
     * @assumes Nothing
     * @effects Nothing
     * @param version The VERSION line parsed from a GenBank sequence record
     * <BR>
     * VERSION line example:<BR>
     * VERSION     AB000096.1  GI:1754585
     */

    protected void parseVersion(String version) {
        // split the VERSION line into individual fields
        StringTokenizer fieldSplitter = new StringTokenizer(version);
        String field;
        // discard the VERSION tag field
        field = fieldSplitter.nextToken();

        // get the version number by splitting the token on '.' and
        // taking the second token e.g. given AC002397.1 the version we
        // are setting is "1"
        String vers = ((String)StringLib.split(
            fieldSplitter.nextToken(), SeqloaderConstants.PERIOD).get(1)).trim();
        rawSeq.setVersion(vers);
    }

    /**
    * Parses the keywords from the KEYWORDS section of a GenBank sequence
    * record. Sets the misc attribute in the SequenceRawAttributes object.
    * @assumes Nothing
    * @effects Nothing
    * @param keywords The KEYWORDS section of a GenBank sequence record
    * <BR>
    * Example of KEYWORDS section, note that this section can span multiple lines
    * in a record, but 'keywords' does not contain newlines:
    * KEYWORDS    Third Party Annotation; TPA.
    * <BR>
    */

    protected void parseKeywords(String keywords) {
         // remove the KEYWORDS tag
         rawSeq.setMisc(keywords.substring(12));
    }


    /**
     * Parses seqids from the ACCESSION section of a GenBank sequence record.
     * Creates an AccessionRawAttributes object for each seqid.
     * The first seqid on first line is primary, any remaining seqids
     * are secondary.
     * Sets the Primary and Secondary AccessionRawAttributes in the SequenceInput
     * object
     * @assumes Nothing
     * @effects Nothing
     * @param accession The ACCESSION section of a GenBank sequence record.
     * <BR>
     * example of ACCESSION section, note this a contrived section used in testing
     * secondary ids that span multiple lines:<BR>
     * ACCESSION   AB000098 AB000097 AB000096 AB000095 AB000094 AB000093 AB000092
     *             AB000091
     */

    protected void parseAccession(String accession) {
        // split the ACCESSION section into individual lines
        StringTokenizer lineSplitter = new StringTokenizer(
            accession, SeqloaderConstants.CRT);


        String line = lineSplitter.nextToken().trim();

        // split the accession line into individual tokens
        StringTokenizer fieldSplitter = new StringTokenizer(line);
        String field;

        // discard the ACCESSION tag field
        field = fieldSplitter.nextToken();

        // create a primary accession object
        field = fieldSplitter.nextToken().trim();
        createAccession(field, Boolean.TRUE);

        // create 2ndary accessions from first ACCESSION line
         while(fieldSplitter.hasMoreTokens())
         {
             field = fieldSplitter.nextToken().trim();
             // add a secondary accession to SequenceInput
             createAccession(field, Boolean.FALSE);
         }

         // create 2ndary accessions from remaining ACCESSION lines
         while(lineSplitter.hasMoreTokens()) {
             line = lineSplitter.nextToken().trim();
             fieldSplitter = new StringTokenizer(line);
             while(fieldSplitter.hasMoreTokens()) {
                 field = fieldSplitter.nextToken().trim();
                 // add a secondary accession to SequenceInput
                 createAccession(field, Boolean.FALSE);

             }
         }
    }

    /**
     * Parses the definition from the DEFINITION section of a GenBank sequence
     * record. Sets the definition in the SequenceRawAttributes object.
     * Truncates the definition to 255 characters if needed
     * @assumes Nothing
     * @effects Nothing
     * @param definition The definition section of a GenBank sequence record
     * <BR>
     * Example of DEFINITION section, note that this section can span multiple lines
     * in a record, but 'definition' does not contain newlines:
     * DEFINITION  Mus musculus mRNA for GATA-2 protein, complete cds
     * <BR>
     */

    protected void parseDefinition(String definition) {
        if (definition.length() > 255) {
            rawSeq.setDescription(definition.substring(12, 255));

        }
        else {
            rawSeq.setDescription(definition.substring(12));
        }
    }
    protected void parseComment(String comment) {
            rawSeq.setComment(comment);
    }


    /**
     * Parses the LOCUS line of a GenBank sequence record. Sets the length,
     * type, division, sequence record date and sequence date in the
     * SequenceRawAttributes object<BR>
     * Note that parsing of LOCUS line reflects that  protein RefSeqs have
     * sequence type in a different column range than GenBank/RefSeq DNA/RNA
     * @assumes Nothing
     * @effects Nothing
     * @param locus The LOCUS line of a GenBank sequence record.
     * <BR>
     * Example of GenBank/RefSeq DNA/RNA LOCUS line:<BR>
     * LOCUS       AB000096                3133 bp     DNA    linear   ROD 05-FEB-1999
     * <BR>
     * Example of protein RefSeq LOCUS line: <BR>
     * LOCUS       NP_035670                167 aa            linear   ROD 07-JAN-2002
     * <BR>
     */

    protected void parseLocus(String locus) {
        // unlike other sections of a GenBank record we must use columns as
        // there are a small number of cases where fields abutt
        rawSeq.setLength(locus.substring(29, 40).trim());

        // get the sequence type (DNA?RNA sequences)
        String type = locus.substring(47, 53).trim();

        // if this range doesn't get anything then it is a protein
        if (type.equals("")) {
            type = locus.substring(41 ,43);
        }
        rawSeq.setType(type);

        // get the Genbank division code
        rawSeq.setDivision(locus.substring(64, 67));

        // convert date and get the set sequence record date
        Timestamp date = DateConverter.convertDate(locus.substring(68, 79));
        rawSeq.setSeqRecDate(date);
        // the initial sequence date is the same as the sequence record date
        rawSeq.setSeqDate(date);
    }

    /**
     * Creates a PubMed RefAssocRawAttributes object, then sets in the 
     * SequenceInput object. If 'pubmed' is null, then the
     * RefAssociationRawAttribute is null
     * @assumes Nothing
     * @effects Nothing
     * @param pubmed Pubmed id for a reference or null
     */

    protected void createReference (String pubmed) {
        // create a pubmed object
        RefAssocRawAttributes pm = null;
        if(pubmed != null) {
            pm = new RefAssocRawAttributes();
            pm.setRefId(pubmed);
            pm.setRefAssocType(this.refAssocType);
            pm.setMgiType(this.seqMGIType);
        }
        // create a pair and add to the SequenceInput references
        sequenceInput.addRef(pm);
    }

    /**
     * Creates an AccessionRawAttributes object and sets it in the SequenceInput
     * object.
     * @assumes Nothing
     * @effects Nothing
     * @param accid Accession id of a sequence
     * @param preferred If true, 'accid' is primary, else 'accid' is 2ndary
     */

    protected void createAccession(String accid, Boolean preferred) {

        AccessionRawAttributes seqid = new AccessionRawAttributes();

        // set attributes of AccessionRawAttributes
        seqid.setAccid(accid);
        seqid.setIsPreferred(preferred);

        // GenBank seqids are public
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
