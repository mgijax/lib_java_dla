package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.*;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jax.mgi.dbs.mgd.loads.Acc.AccessionRawAttributes;
import org.jax.mgi.dbs.mgd.loads.Alo.AlleleRawAttributes;
import org.jax.mgi.dbs.mgd.loads.Alo.CellLineRawAttributes;
import org.jax.mgi.dbs.mgd.loads.Alo.DerivationRawAttributes;
import org.jax.mgi.dbs.mgd.loads.Alo.SeqAlleleAssocRawAttributes;
import org.jax.mgi.dbs.mgd.loads.Seq.SequenceRawAttributes;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.RefAssocRawAttributes;
import org.jax.mgi.dbs.mgd.lookup.LabNameAndCodeLookupByRawCreator;
import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.GeneTrapLoadCfg;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;
import org.jax.mgi.shr.dla.input.genbank.GBFormatInterpreter;
import org.jax.mgi.shr.dla.input.genbank.GBOrganismChecker;
import org.jax.mgi.shr.dla.input.SequenceInput;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;

/**
 * @is An object that parses GenBank format Mouse Gene Trap sequence record
 * and creates an ALOInput object
 * @has
 *   <UL>
 *   <LI>A SequenceInput object 
 *   <LI>A DBGSSGeneTrapRawInput object into which it bundles a set of
 *       RawAttributes objects
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Determines if a GenBank sequence record is a Gene Trap
 *   <LI>Creates a DBGSSGeneTrapRawInput object from Configuration and parsing
 *       a GenBank Gene Trap sequence record
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class DBGSSGeneTrapInterpreter extends GBFormatInterpreter {
	// the configurator

	GeneTrapLoadCfg config;
	// configured attributes
	String loadReference;
	String alleleInheritMode;
	String alleleType;
	String alleleStatus;
	String cellLineDerivType;
	String molecularMutation;
	String cellLineType;
	String seqTagMethods;
	String alleleSymbolTemplate;
	String alleleNameTemplate;
	// sequence input object; the object we get from superclass 
	// GBFormatInterpreter
	private SequenceInput seqInput;

	// expression string, pattern, and matcher to find the class
	// section of a GenBank format sequence record - to determine a gene trap
	// [\\s\\S]* means 0 or more white space and non white space characters
	//
	private static final String CLASS_EXPRESSION = "Class: ([\\s\\S]*?)\\.";
	private Pattern classPattern;
	private Matcher classMatcher;

	// String to determine that a sequence is a gene trap from the Class line
	private static final String GT_EXPRESSION = "Gene Trap";
	private DLALogger logger;

	// mapping of dbGSS sequence tag methods (in lower case) to vocab term
	private HashMap seqTagMethodMap;

	// the set of dbGSS terms we are looking for, badnames are in lower case
	Set seqTagMethodBadNames;

	// expression string, pattern, and matcher to find the contact
	// section of a GenBank format COMMENT section
	private static final String CONTACT_EXPRESSION = "Contact: ([\\s\\S]*?)\\n";
	private Pattern contactPattern;
	private Matcher contactMatcher;

	// String expression to determine a TIGM gene trap from the Contact line
	private static final String TIGM_EXPRESSION =
			"Richard H. Finnell at Texas Institute for Genomic Medicine";

	// expression string, pattern, and matcher to find the Vector name
	// from a GenBank format source note section
	//private static final String VECTOR_EXPRESSION = ".*Vector: ([\\s\\S]*?)\"";
	//private Pattern vectorPattern;
	//private Matcher vectorMatcher;

	// expression string, pattern, and matcher to find Vector name for TIGM
	private static final String TIGM_VECTOR_EXPRESSION =
			".*Gene trapping vector ([\\s\\S]*?)\"";
	private Pattern tigmVectorPattern;
	private Matcher tigmVectorMatcher;

	// String to find EGTC contact information
	// from section of a GenBank format COMMENT section
	private static final String EGTC_STRING = "Exchangeable Gene Trap Clones";

	// splits a sequence tag id into its cell line id and vector end components
	private VectorEndCellLineIDExtractor veClExtractor;
	// lookup raw Creator to get Cell Line Lab Name(term)/Code(abbrev) vocab
	private LabNameAndCodeLookupByRawCreator labNameCodeLookup;
	// set these when we interpret the mutant cell line so other methods may have
	// easy access
	private String rawCreator = null;
	private String mutantCellLineID = null;

	/**
	 * create and instance of GBGeneTrapInterpreter
	 * @throws DLALoggingException if error creating the logger
	 * @throws ConfigException if error creating or accessing the configurator
	 */
	public DBGSSGeneTrapInterpreter(GBOrganismChecker oc)
			throws DLALoggingException, ConfigException, CacheException, 
				DBException {
		super(oc);
		config = new GeneTrapLoadCfg();
		logger = DLALogger.getInstance();

		// get attributes from configuration
		loadReference = config.getLoadReference();
		alleleInheritMode = config.getAlleleInheritMode();
		alleleType = config.getAlleleType();
		alleleStatus = config.getAlleleStatus();
		cellLineDerivType = config.getCellLineDerivType();
		molecularMutation = config.getMolecularMutation();
		cellLineType = config.getCellLineType();
		seqTagMethods = config.getSeqTagMethods();
		alleleSymbolTemplate = config.getAlleleSymbolTemplate();
		alleleNameTemplate = config.getAlleleNameTemplate();

		// compile expressions to find the class, contact, TIGM vector name
		classPattern = Pattern.compile(CLASS_EXPRESSION, Pattern.MULTILINE);
		contactPattern = Pattern.compile(CONTACT_EXPRESSION, Pattern.MULTILINE);
		tigmVectorPattern = Pattern.compile(
				TIGM_VECTOR_EXPRESSION, Pattern.MULTILINE);
		veClExtractor = new VectorEndCellLineIDExtractor();
		labNameCodeLookup = new LabNameAndCodeLookupByRawCreator();
		labNameCodeLookup.initCache();

		// create lookup of the configured set of sequence tag methods to parse
		seqTagMethodMap = new HashMap();
		createSeqTagMethodMap(seqTagMethodMap);
	}

	/**
	 * Parses a sequence record and  creates a SequenceInput object from
	 * Configuration and parsed values. Further parses Gene Trap 
	 * information from the SequenceInput object to create a
	 * DBGSSGeneTrapRawInput object
	 * @param rcd  GenBank format sequence record
     * @assumes rcd is a mouse gene trap sequence
	 * @return A DBGSSGeneTrapRawInput object representing 'rcd'
	 * @Override GBFormatInterpreter.interpret()
	 * @throws RecordFormatException from super.interpret if there is an error
	 *         parsing because of a bad record format
	 */
	public Object interpret(String rcd) throws RecordFormatException {
		// call superclass to parse the record and get config
		seqInput = (SequenceInput) super.interpret(rcd);
		logger.logcInfo("SEQID: " + seqInput.getPrimaryAcc().getAccID(), false);

		// gene trap input object; the object we create from
		// further parsing of SequenceInput object attributes
		DBGSSGeneTrapRawInput gtInput = new DBGSSGeneTrapRawInput();

		// interpret the cell line; this also interprets the cell line derivation
		// sequence gene trap info and creates a AccessionRawAttributes for the 
		// seqTagId to sequence association
		interpretCellLine(seqInput, gtInput);

		// interpret the allele 
		interpretAllele(seqInput, gtInput);

		// interpret the allele references
		interpretReferenceAssocs(seqInput, gtInput);

		// interpret the sequence to allele associations
		interpretSeqAlleleAssoc(seqInput, gtInput);

		gtInput.setMutation(molecularMutation);
		gtInput.setInputRecord(rcd);
		gtInput.setSeqRecordDate(seqInput.getSeq().getSeqRecDate());
		DerivationRawAttributes deriv = ((CellLineRawAttributes) (gtInput.
				getCellLines().iterator().next())).getDerivation();
		logger.logvInfo("DERIVATION" + "\t" + deriv.getCreator() + "\t" +
				deriv.getParentCellLine() + "\t" +
				seqInput.getSeq().getLibrary() + "\t" +
				seqInput.getSeq().getStrain() + "\t" +
				deriv.getVectorName(), false);
		// return the gene trap input object
		return (ALORawInput) gtInput;
	}

	/**
	 * Determines whether this is a gene trap sequence`
	 * from a configured set of organisms
	 * @param record A GenBank format sequence record
	 * @Override GBFormatInterpreter.isValid
	 * @return true if this is a gene trap sequence  from a
	 * configured set of organisms 
	 */
	public boolean isValid(String record) {
		return true;
        // we now assume that input is mouse and is gene trap
        //return super.isValid(record) && isGeneTrap(record);
	}

	/**
	 * Determine if this is a gene trap sequence by searching for
	 * "Class: Gene Trap" in the COMMENT section of a GenBank gene trap record
	 * @param record - a GenBank format sequence record
	 * @return true if this is a gene trap sequence
     * @note we are not using this but keeping it for now
	 */
	public boolean isGeneTrap(String record) {
		boolean isGT = false;
		if (record != null) {
			// find the class
			classMatcher = classPattern.matcher(record);
			if (classMatcher.find() == true) {
				String s = classMatcher.group(1);
				if (s.equals(GT_EXPRESSION)) {
					isGT = true;
				}
			} else {
				contactMatcher = contactPattern.matcher(record);
				if (contactMatcher.find() == true) {
					String s = contactMatcher.group(1);
					if (s.equals(TIGM_EXPRESSION)) {
						isGT = true;
					}
				}
			}
		}
		
		return isGT;
	}

	/**
	 * load a mapping of dbGSS sequence tag method to MGI controlled vocab terms
	 * from Configuration
	 * @param map - the map to load
	 * @throws ConfigException if error accessing configurator
	 */
	private void createSeqTagMethodMap(HashMap map) {
		StringTokenizer t = new StringTokenizer(this.seqTagMethods, "|");
		while (t.hasMoreTokens()) {
			String s = t.nextToken().trim();
			StringTokenizer r = new StringTokenizer(s, ":");
			String dbGssName = r.nextToken().trim().toLowerCase();
			String mgiName = r.nextToken().trim();
			map.put(dbGssName, mgiName);
		}
		seqTagMethodBadNames = seqTagMethodMap.keySet();
	}

	/**
	 * parse derivation attributes from SequenceInput object,
	 * create DerivationRawAttributes object and place in DBGSSGeneTrapRawInput
	 * Note: vector type and reference are not set (remain null)
	 * @param seqInput - Raw Sequence Data from a GenBank Gene Trap sequence rcd
	 *             the object whose attributes we are parsing
     * @throws RecordFormatException if format errors
	 */
	private DerivationRawAttributes interpretDerivation(
			SequenceInput seqInput) throws RecordFormatException {

		/**
		 * get the sequence from the SequenceInput object, create a 
		 * DerivationRawAttributes object and return it
		 */
		SequenceRawAttributes sequenceRaw = seqInput.getSeq();
		DerivationRawAttributes derivRaw = new DerivationRawAttributes();
		// get the creator - see the getCreator method
		String creator = getCreator(seqInput);
		derivRaw.setCreator(creator);
		// source qualifier example: /clone_lib="GTL_R1_Gen-SD5"
		derivRaw.setName(sequenceRaw.getLibrary());
		// source qualifier example: /cell_line="R1"
		String parentCellLine = sequenceRaw.getCellLine();
		if (parentCellLine == null) {
			parentCellLine = DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED;
		}
		derivRaw.setParentCellLine(parentCellLine);
		//source qualifier example:/strain="129S3"
		String parentStrain = sequenceRaw.getStrain();
		if (parentStrain == null) {
			parentStrain = DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED;
		}
		derivRaw.setParentCellLineStrain(parentStrain);
		// derivation type from configuration "Gene trapped"
		derivRaw.setDerivationType(cellLineDerivType);
		// vector name - see getVectorName method
		derivRaw.setVectorName(getVectorName(seqInput, creator));
		return derivRaw;
	}

	/**
	 * get the name of the gene trap vector from the note section of 
	 * the record
	 * @param seqInput - Raw Sequence Data from a 
	 * GenBank Gene Trap sequence rcd
	 * @return String name of the gene trap vector
	 * Example from CW020141 (CMHD): Vector: Gen-SD5
	 * Example from AB187228 (EGTC): Cell line ID: 21-7 Gene trap Vector: pU-21
	 * Example from EF806820 (TIGM - from the 2nd source section in the 
	 * record for for just the LTR):
     * source          1..30
     *      /organism="Gene trapping vector VICTR76"
	 */
	private String getVectorName(SequenceInput seqInput, String creator)
			throws RecordFormatException {
		String note = seqInput.getSeq().getNote();
		String secondarySource = null;
		String vectorName = null;

		// put if/else here, was doing both as TIGM would not be found in note
		// so would then check 2ndary source
		if (note != null && !creator.equals(DBGSSGeneTrapLoaderConstants.TIGM)) {
			// remove \n in note
			String[] n = note.split("\n");
			note = StringLib.join(n, " ");
			// now find the vector
			int fromIndex = note.indexOf("Vector:");
			if (fromIndex > -1) {
				fromIndex = fromIndex + 8;
				vectorName = note.substring(fromIndex);
			}
		} // use 2ndary source (TIGM)
		else {
			secondarySource = seqInput.getSeq().get2ndarySource();

			if (secondarySource != null) {
				// find the vector
				tigmVectorMatcher = tigmVectorPattern.matcher(secondarySource);
				if (tigmVectorMatcher.find() == true) {
					vectorName = tigmVectorMatcher.group(1); 
				}
			}
		}


		// vector name still not found, set it to "Not Specified" and report
		if (vectorName == null) {
			vectorName = DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED;
		}

		return vectorName;
	}

	/**
	 * parse cell line attributes from SequenceInput object and
	 * create CellLineRawAttributes object, a SeqGeneTrapRawAttributes object,
	 * and an AccessionRawAttributes object for sequence tag id association
	 * to the sequence. Place all three Raw objects in the DBGSSGeneTrapRawInput
	 * object.
	 * @param seqInput - Raw Sequence Data from a GenBank Gene Trap sequence rcd
	 *             the object whose attributes we are parsing
	 * @param gtInput - Raw Gene Trap Data from a GenBank Gene Trap sequence rcd
	 *             the object which we are building
	 */
	private void interpretCellLine(
			SequenceInput seqInput, DBGSSGeneTrapRawInput gtInput)
			throws RecordFormatException {

		// create an empty cell line raw attributes object
		CellLineRawAttributes cellLineRaw = new CellLineRawAttributes();

		// determine the derivation for this cell line
		cellLineRaw.setDerivation(interpretDerivation(seqInput));

		// Now parse the sequence gene trap info. The sequence tag id is made
		// up of the vector end information and the cell line ID. The
		// interpretSeqGeneTrap method splits these two pieces out (it needs
		// the vector end) and returns the cell line ID
		try {
			this.mutantCellLineID = interpretSeqGeneTrap(seqInput, gtInput);
		} catch (NoVectorEndException e) {
			RecordFormatException rfE = new RecordFormatException();
			rfE.bindRecord(e.getMessage());
			throw rfE;
		}
		// set the cell line ID and cell line name
		cellLineRaw.setCellLineID(this.mutantCellLineID);
		cellLineRaw.setCellLine(this.mutantCellLineID);

		// determine cellLineID logicalDB
		String ldb = interpretCellLineLogicalDB(seqInput);
		cellLineRaw.setLogicalDB(ldb);

		// for gene traps mixed status is determined by allele/marker assoc load
		//cellLineRaw.setIsMixed(Boolean.FALSE);

		// is always mutant
		cellLineRaw.setIsMutant(Boolean.TRUE);

		// is 'Gene trapped', but we've configured it in case the term changes
		cellLineRaw.setType(cellLineType);

		// add the raw cell  line to the DBGSSGeneTrapRawInput
		gtInput.addCellLine(cellLineRaw);
	}

	/**
	 * parse allele attributes from SequenceInput object,
	 * create AlleleRawAttributes object and place in DBGSSGeneTrapRawInput
	 * Note: The AlleleRawAttributes set of markerMGI IDs, strain and 
	 * objectIdentity, are not set (remain null)
	 * @param seqInput - Raw Sequence Data from a GenBank Gene Trap sequence rcd
	 *             the object whose attributes we are parsing
	 * @param gtInput - Raw Gene Trap Data from a GenBank Gene Trap sequence rcd
	 *             the object which we are building
	 */
	private void interpretAllele(
			SequenceInput seqInput, DBGSSGeneTrapRawInput gtInput)
			throws RecordFormatException {
		/**
		 * get the sequence from the SequenceInput object, create a 
		 * AlleleRawAttributes object and set its attributes,
		 * set AlleleRawAttributes in the DBGSSGeneTrapRawInput object
		 */
		// NOTE: we don't currently getting anything from the seqInput object
		AlleleRawAttributes rawAllele = new AlleleRawAttributes();
		setAlleleNameAndSymbol(rawAllele);
		// set attributes from configuration
		rawAllele.setInheritMode(alleleInheritMode);
		rawAllele.setType(alleleType);
		rawAllele.setStatus(alleleStatus);
		rawAllele.setIsWildType(Boolean.FALSE);
		rawAllele.setIsExtinct(Boolean.FALSE);
		gtInput.setAllele(rawAllele);
	}

	/**
	 * create allele symbol and name and set in DBGSSGeneTrapRawInput object
	 * @param alleleRaw - The AlleleRawAttributes in which to set the symbol
	 *         and name
	 * @assumes this.mutantCellLineID and this.rawCreator have been set (are
	 *       not null)
	 */
	private void setAlleleNameAndSymbol(AlleleRawAttributes rawAllele)
			throws RecordFormatException {
		// get the symbol and name template
		String symbol = this.alleleSymbolTemplate;
		String name = this.alleleNameTemplate;
		KeyValue labNameAndLabCode = null;

		// translate the rawCreator to a lab name and lab code
		try {
			labNameAndLabCode = labNameCodeLookup.lookup(
					this.rawCreator);
		} catch (DBException e) {
			RecordFormatException rfE = new RecordFormatException();
			rfE.bindRecord("DBException resolving rawCreator: " + this.rawCreator +
					" to lab name/lab code");
			throw rfE;
		} catch (CacheException e) {
			RecordFormatException rfE = new RecordFormatException();
			rfE.bindRecord("CacheException resolving rawCreator: '" + this.rawCreator +
					"' to lab name/lab code");
			throw rfE;
		}
		if (labNameAndLabCode == null) {
			RecordFormatException rfE = new RecordFormatException();
			rfE.bindRecord("Unknown rawCreator: " + this.rawCreator +
					" cannot determine lab name/code");
			throw rfE;
		}

		// add cell line ID and lab name to allele name
		name = name.replaceAll("~~MutCellLineID~~", this.mutantCellLineID);
		name = name.replaceAll("~~LabName~~", (String) labNameAndLabCode.getKey());
		rawAllele.setName(name);
		// add cell line ID and lab code to allele symbol
		symbol = symbol.replaceAll("~~MutCellLineID~~", this.mutantCellLineID);
		symbol = symbol.replaceAll("~~LabCode~~", (String) labNameAndLabCode.getValue());
		rawAllele.setSymbol(symbol);
		rawAllele.setIsMixed(Boolean.FALSE);
		rawAllele.setTransmission("Cell Line");
	}

	/**
	 * parse reference attributes from SequenceInput object,
	 * create RefAssocRawAttributes objects and place in DBGSSGeneTrapRawInput
	 * While we're at it create a RefAssocRawAttributes object for the load
	 * reference
	 * @param seqInput - Raw Sequence Data from a GenBank Gene Trap sequence rcd
	 *             the object whose attributes we are parsing
	 * @param gtInput - Raw Gene Trap Data from a GenBank Gene Trap sequence rcd
	 *             the object which we are building
	 */
	private void interpretReferenceAssocs(SequenceInput seqInput,
			DBGSSGeneTrapRawInput gtInput) {
		/**
		 * get the set of references from the SequenceInput object and
         * create RefAssocRawAttributes for the allele
		 * set RefAssocRawAttributes in the DBGSSGeneTrapRawInput object
		 */
		Vector refs = seqInput.getRefs();
		// these references are PubMed IDs
		for (Iterator i = refs.iterator(); i.hasNext();) {
			RefAssocRawAttributes seqRefRaw = (RefAssocRawAttributes) i.next();
			RefAssocRawAttributes alleleRefRaw = new RefAssocRawAttributes();
			alleleRefRaw.setMgiType(new Integer(MGITypeConstants.ALLELE));
			alleleRefRaw.setRefAssocType(new Integer(
					MGIRefAssocTypeConstants.ALLELE_SEQUENCE));
			alleleRefRaw.setRefId(seqRefRaw.getRefId());
			gtInput.setReferenceAssociation(alleleRefRaw);

		}

		// now create the load reference, Jnum from configuration
		RefAssocRawAttributes loadRef = new RefAssocRawAttributes();
		loadRef.setMgiType(new Integer(MGITypeConstants.ALLELE));
		loadRef.setRefAssocType(new Integer(
				MGIRefAssocTypeConstants.ALLELE_ORIGINAL));
		loadRef.setRefId(loadReference);
		gtInput.setReferenceAssociation(loadRef);
	}

	/**
	 * parse sequence allele attributes from SequenceInput object,
	 * create SeqAlleleAssocRawAttributes object and place in DBGSSGeneTrapRawInput
	 * @param seqInput - Raw Sequence Data from a GenBank Gene Trap sequence rcd
	 *             the object whose attributes we are parsing
	 * @param gtInput - Raw Gene Trap Data from a GenBank Gene Trap sequence rcd
	 *             the object which we are building
	 */
	private void interpretSeqAlleleAssoc(
			SequenceInput seqInput, DBGSSGeneTrapRawInput gtInput) {
		/**
		 * get the sequence from the SequenceInput object, create a 
		 * SeqAlleleAssocRawAttributes object and set its attributes,
		 * set SeqAlleleAssocRawAttributes in the DBGSSGeneTrapRawInput object
		 */
		// create and set sequence allele association raw attributes
		SeqAlleleAssocRawAttributes seqAlleleRaw = new SeqAlleleAssocRawAttributes();

		seqAlleleRaw.setSeqID(seqInput.getPrimaryAcc().getAccID());

		// loadReference from configuration is also the association reference
		seqAlleleRaw.setJNum(loadReference);

		// qualifier is Not Specified
		seqAlleleRaw.setQualifier(DBGSSGeneTrapLoaderConstants.NOT_SPECIFIED);

		gtInput.setSequenceAssociation(seqAlleleRaw);
	}

	/**
	 * parse sequence gene trap attributes from SequenceInput object,
	 * create SeqGeneTrapRawAttributes object and place in DBGSSGeneTrapRawInput
	 * Notes: pointCoordinate is not set.
	 * @param seqInput - Raw Sequence Data from a GenBank Gene Trap sequence rcd
	 *             the object whose attributes we are parsing
	 * @param gtInput - Raw Gene Trap Data from a GenBank Gene Trap sequence rcd
	 *             the object which we are building
	 * @param seqTagID - the sequence tag ID from which we get the vector end
	 *             and the cell line ID
	 * @return cellLineID - this method has parsed it from the sequence tag  id
	 * @throws NoVectorEndException if sequence tag id does not contain Vector
	 *         End when it should (not all creators put VE info in seqTagID)
	 */
	private String interpretSeqGeneTrap(SequenceInput seqInput,
			DBGSSGeneTrapRawInput gtInput)//, String seqTagID) 
			throws RecordFormatException, NoVectorEndException {
		/**
		 * for gene traps the sequence tag id contains the cell line id and the 
		 * cell line id and cell line name are the same. 
		 * the sequence tag ID is parsed from two different places in the dbGSS
		 * record depending on creator. 
		 */
		// create and set sequence allele association raw attributes
		SequenceRawAttributes sequenceRaw = seqInput.getSeq();
		String seqTagID = null;
		if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.BAYGENOMICS) ||

				this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.SIGTR) ||
				this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.TIGEM) ||
				this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.GGTC) ||
                this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.FHCRC)) {
			seqTagID = getDefinitionSeqTagID(seqInput);
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.CMHD) ||
				this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.EGTC) ||
				this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.ESDB) ||
				this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.LEXICON) ||
				this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.TIGM) ||
                this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.EUCOMM)) {
			seqTagID = sequenceRaw.getCloneId();
            
		} else {
			// not a gene trap we are interested in, superclass interpret
			// method only throw RecordFormatException, so we use it here
			RecordFormatException e = new RecordFormatException();
			e.bindRecord("Gene Trap creator: " + this.rawCreator + " seqId: " +
					seqInput.getPrimaryAcc().getAccID());
			throw e;
		}
		
		// create the raw accession object for the sequence tag ID association
		// to the sequence
		interpretSeqTagIdAccession(seqTagID, gtInput);

		// Now parse the sequence gene trap info. The sequence tag id is made
		// up of the vector end information and the cell line ID. The
		// interpretSeqGeneTrap method splits these two pieces out (it needs
		// the vector end) and returns the cell line ID

		/**
		 * create a SeqGeneTrapRawAttributes object and set its attributes,
		 * set SeqGeneTrapRawAttributes in the DBGSSGeneTrapRawInput object
		 */
		// create and set sequence allele association raw attributes
		SeqGeneTrapRawAttributes seqGTRaw = new SeqGeneTrapRawAttributes();
		String seqType = sequenceRaw.getType();

		String seqTagMethod = getSeqTagMethod(seqInput, seqTagID);
		KeyValue kv = veClExtractor.extract(seqTagID, this.rawCreator,
				seqTagMethod, seqType);

		String cellLineID = (String) kv.getKey();
		String vectorEnd = (String) kv.getValue();
				seqGTRaw.setSeqID(seqInput.getPrimaryAcc().getAccID());
		seqGTRaw.setSeqTagMethod(seqTagMethod);
		seqGTRaw.setSeqTagID(seqTagID);
		seqGTRaw.setVectorEnd(vectorEnd);
		seqGTRaw.setGoodHitCount(new Integer(0));

		// if TIGM 'no' all others 'yes' 
		String reverseComp = "yes";
		if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.TIGM) ||
				this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.TIGEM) ||
                this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.EUCOMM)) {
			reverseComp = "no";
		}
		seqGTRaw.setIsReverseComp(reverseComp);
		gtInput.setSeqGeneTrap(seqGTRaw);
		return cellLineID;
	}

	private String interpretCellLineLogicalDB(SequenceInput seqInput)
			throws RecordFormatException {

		if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.TIGM)) {
			return DBGSSGeneTrapLoaderConstants.TIGM_CL_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.LEXICON)) {
			return DBGSSGeneTrapLoaderConstants.LEXICON_CL_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.BAYGENOMICS)) {
			return DBGSSGeneTrapLoaderConstants.BAYGENOMICS_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.SIGTR)) {
			return DBGSSGeneTrapLoaderConstants.SIGTR_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.TIGEM)) {
			return DBGSSGeneTrapLoaderConstants.TIGEM_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.GGTC)) {
			return DBGSSGeneTrapLoaderConstants.GGTC_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.CMHD)) {
			return DBGSSGeneTrapLoaderConstants.CMHD_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.EGTC)) {
			return DBGSSGeneTrapLoaderConstants.EGTC_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.ESDB)) {
			return DBGSSGeneTrapLoaderConstants.ESDB_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.FHCRC)) {
			return DBGSSGeneTrapLoaderConstants.FHCRC_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.EUCOMM)) {
            return DBGSSGeneTrapLoaderConstants.EUCOMM_CL_LDB;
        } else {
			// unknown Gene Trap Creator (this will be caught prior to
			// getting here)
			RecordFormatException e = new RecordFormatException();
			e.bindRecord("Unknown Gene Trap creator: " + this.rawCreator + " seqId: " +
					seqInput.getPrimaryAcc().getAccID());
			throw e;
		}
	}

	/**
	 * create an AccessionRawAttributes object for the sequence tag id 
	 * association to the sequence
	 * @param seqTagID - the sequence tag ID to associate with the sequence
	 * @param gtInput - Raw Gene Trap Data from a GenBank Gene Trap sequence rcd
	 *             the object which we are building
	 */
	private void interpretSeqTagIdAccession(String seqTagID,
			DBGSSGeneTrapRawInput gtInput) {
		String logicalDB = null;
		if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.TIGM)) {
			logicalDB = DBGSSGeneTrapLoaderConstants.TIGM_SEQ_LDB;
		} else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.LEXICON)) {
			logicalDB = DBGSSGeneTrapLoaderConstants.LEXICON_SEQ_LDB;
        } else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.EUCOMM)) {
            logicalDB = DBGSSGeneTrapLoaderConstants.EUCOMM_SEQ_LDB;
		} else {
			logicalDB = DBGSSGeneTrapLoaderConstants.IGTC_SEQ_LDB;
		}
		AccessionRawAttributes raw = new AccessionRawAttributes();
		raw.setAccid(seqTagID);
		raw.setIsPreferred(Boolean.TRUE);
		raw.setIsPrivate(Boolean.FALSE);
		raw.setLogicalDB(logicalDB);
		raw.setMgiType(new Integer(MGITypeConstants.SEQUENCE));
		gtInput.setSeqTagAccession(raw);
	}

	/**
	 * parse sequence tag method from a SequenceInput object, based on creator)
     * @param seqInput - Raw Sequence Data from a GenBank Gene Trap sequence rcd
	 * @return - The sequence tag method
	 * @throws 
	 * Example from CW020141:
	 * COMMENT     Contact: Stanford WL
        Institute of Biomaterials & Biomedical Engineering
        University of Toronto
        407 Rosebrugh Bldg., 4 Taddle Creek Rd., Toronto, Ontario, Canada
        M5S 3G9
        Tel: 416 946 8379
        Fax: 416 978 4317
        Email: william.stanford@utoronto.ca
        Gen-SD5 Gene trap insertion. The sequence tag is generated by 3'
        race. The ES cell line harboring this insertion of the target gene
        is available through the following web site:
        http://pokey.ibme.utoronto.ca/sequence_report.php?id=145B3.
        Class: Gene Trap.
	 * Example from TIGM EF806801
	 * /note="strain origin confirmed on the basis of 110 genetic
        markers distributed across 19 autosomes and the X
        chromosome (Charles River Laboratories); sequence tags are
        derived from genomic sequence by inverse PCR (IPCR) and
        represent the gene-trap vector insertion site; sequence
        reads represent either the upstream (F) or downstream (R)
        vector-genomic junction, amplified from circularized
        genomic DNA"
     * Example seqTag ID from EUCOMM
     * EUCG0003e02.p1k3SPK
     * seqTag method is last 4 characters of seqTagID
	 */
	private String getSeqTagMethod(SequenceInput seqInput, String seqTagID) {

		String method = null;

        if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.TIGM)) {
            String note = seqInput.getSeq().getNote();
            if (note != null) {
                String[] n = note.toLowerCase().split("\n");
                note = StringLib.join(n, " ");
                // iterate through all the possible values for sequence tag method
                for (Iterator i = seqTagMethodBadNames.iterator(); i.hasNext();) {
                    String m = ((String) i.next()).toLowerCase();
                    // if we find one set in DBGSSGeneTrapRawInput object and break
                    if (note.indexOf(m) != -1) {
                        method = m;
                        break;
                    }
                }
            }
        }
        else if (this.rawCreator.equals(DBGSSGeneTrapLoaderConstants.EUCOMM)) {
            String s = (seqTagID.substring(seqTagID.length() - 4)).toLowerCase();
            for (Iterator i = seqTagMethodBadNames.iterator(); i.hasNext();) {
                String m = ((String) i.next()).toLowerCase();
                // if we find a match set method and break
                if (s.indexOf(m) != -1) {
                    method = m;
                    break;
                }
            }
        }
        else {
            // all other creators
            String comment = seqInput.getSeq().getComment();
            if (comment != null) {
                String[] c = comment.toLowerCase().split("\n");
                comment = StringLib.join(c, " ");

                // iterate through all the possible dbgss values for sequence tag method
                for (Iterator i = seqTagMethodBadNames.iterator(); i.hasNext();) {
                    String m = ((String) i.next()).toLowerCase();
                    // if we find one set in DBGSSGeneTrapRawInput object and break
                    if (comment.indexOf(m) != -1) {
                        method = m;
                        break;
                    }
                }
            }
        }

		// sequence tag method not found, report it and return nullS
		if (method == null) {
			logger.logcInfo("Sequence Tag Method not found for seqID: " +
					seqInput.getPrimaryAcc().getAccID(), false);
			return method;
		}

		// return method resolved to MGI term
		return (String) seqTagMethodMap.get(method);

	}

	/**
	 * parse creator from the SequenceInput comment; translate to creator vocab
	 * @param seqInput - Raw Sequence Data from a GenBank Gene Trap sequence rcd
	 * @return - raw creator name 
	 * Example from CW020141:
	 * COMMENT     Contact: Stanford WL
	Institute of Biomaterials & Biomedical Engineering
	University of Toronto
	407 Rosebrugh Bldg., 4 Taddle Creek Rd., Toronto, Ontario, Canada
	M5S 3G9
	Tel: 416 946 8379
	Fax: 416 978 4317
	Email: william.stanford@utoronto.ca
	Gen-SD5 Gene trap insertion. The sequence tag is generated by 3'
	race. The ES cell line harboring this insertion of the target gene
	is available through the following web site:
	http://pokey.ibme.utoronto.ca/sequence_report.php?id=145B3.
	Class: Gene Trap.
	
	 */
	private String getCreator(SequenceInput seqInput) {
		String comment = seqInput.getSeq().getComment();
		String creator = null;
		if (comment != null) {
			// find the contact, if it exists
			contactMatcher = contactPattern.matcher(comment);
			if (contactMatcher.find() == true) {
				creator = contactMatcher.group(1);
			} // if we didn't find a contact it is an EGTC gene trap (most likely)
			// search explicitly for the EGTC contact text
			else if (comment.indexOf(EGTC_STRING) != -1) {
				creator = EGTC_STRING;
			} // creator not found, report it
			else {
				logger.logcInfo("Creator not found for seqID: " +
						seqInput.getPrimaryAcc().getAccID() +
						" COMMENT: " + comment, false);
			}
		}
		// set instance variable for other methods to use
		this.rawCreator = creator;
		return creator;
	}

	/**
	 * parse sequence tag id from the SequenceInput definition
	 * @param seqInput - Raw Sequence Data from a GenBank Gene Trap sequence rcd
	 * @return - the sequence tag id from the DEFINITION section 
	 * Example from CW020141:
	 * DEFINITION  CMHD-GT_145B3-3 GTL_R1_Gen-SD5 Mus musculus cDNA clone
	CMHD-GT_145B3-3 3', mRNA sequence.
	 *
	 */
	private String getDefinitionSeqTagID(SequenceInput seqInput) {
		// In MGI we refer to the DEFINITION section as the Sequence Description
		StringTokenizer s = new StringTokenizer(seqInput.getSeq().
				getDescription());
		String seqTagID = null;

		// the sequence tag ID will be the first token for some creators
		// Note: the tag DEFINITION is not included in the description from the 
		// sequence input object
		if (s.hasMoreTokens() == true) {
			seqTagID = s.nextToken();
		}
		// seqTagID will never be null, but it may not be what we want.
		return seqTagID;
	}
}
