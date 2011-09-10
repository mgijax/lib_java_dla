package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.loads.Alo.*;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.*;
import org.jax.mgi.dbs.mgd.loads.Alo.AlleleLookupByMutantCellLineKey;
import org.jax.mgi.dbs.mgd.lookup.PubMedIDLookupByAlleleKey;
import org.jax.mgi.dbs.mgd.lookup.LabNameAndCodeLookupByRawCreator;
import org.jax.mgi.dbs.mgd.query.AlleleSymbolQuery;
import org.jax.mgi.dbs.mgd.query.AlleleSynonymQuery;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;
import org.jax.mgi.shr.dla.loader.alo.*;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.exception.MGIException;

import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * An object that processes dbGSS Gene Trap allele information by resolving
 * dbGSS Gene Trap attributes to a set of DAOs
 * @does resolves and sets in the ALO the following DAOs
 * <UL>
 * <LI>Allele
 * <LI>Allele MGI ID
 * <LI>Allele Mutant Cell Line association 
 * <LI>Allele Mutation
 * <LI>Allele Reference Associations
 * </UL>
 * Determines, if possible, object identity in the database, reporting 
 * differences in incoming information with respect to the data in the database 
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 * 8/11 sc - changed allele*InDB from HashSet to HashMap, mapping mcl ID to list of 
 * allele symbols/synonyms that contain that ID to avoid iterating over  the whole
 * set each time
 */
public class DBGSSGeneTrapAlleleProcessor extends AlleleProcessor {

	private AlleleLookupByMutantCellLineKey alleleLookup;
	private MutantCellLineLookupByAlleleKey mclLookup;
	private PubMedIDLookupByAlleleKey pubMedLookup;
	// we'll get the cache from pubMedLookup and use it instead
	private Map pubMedMap;
	private HashMap alleleSymbolsInDB;
	private HashMap alleleSynonymsInDB;
	private LabNameAndCodeLookupByRawCreator labCodeLookup;

	/**
	 * Constructs a DBGSSGeneTrapAlleleProcessor
	 * @throws MGIException
	 */
	public DBGSSGeneTrapAlleleProcessor()
			throws MGIException {

		// we'll get this from the factory ultimately when we determine an
		// appropriate parent object
		alleleLookup = new AlleleLookupByMutantCellLineKey();
		alleleLookup.initCache();
		mclLookup = new MutantCellLineLookupByAlleleKey();
		pubMedLookup = new PubMedIDLookupByAlleleKey();
		pubMedLookup.initCache();
		// get the cache from the pubMedLookup
		pubMedMap = pubMedLookup.getCache();
		labCodeLookup = 
		    new LabNameAndCodeLookupByRawCreator();
		labCodeLookup.initCache();
		
		initSymbolMaps();
	}

    /**
     * no preprocessing at this time
     */
    public void preprocess() throws MGIException {

    }
	/**
	 * Processes DBGSS Gene Trap allele, cell line association, mutation and
	 * reference information
	 * @param aloInput ALORawInput object - a set of raw attributes to resolve
	 * and add to the database
	 * @param resolvedALO - the ALO object to which will will add resolved
	 *         allele information
	 * @param existingMCLKeys - the set of existing mutant cell line objects
	 * that have been determined to be in the database (or null) For dbGSS Gene
	 * Traps there is only one MCL per allele.
	 * @param incomingMCLs - the set of incoming MCL objects. For gene traps 
	 *  This is a set of one. If MCL found in the database (i.e. existingMCLKeys
	 *  has a value) then we compare the incoming attributes with that in the
	 *  database
	 * @return Integer alleleKey of the processed allele - may be new or existing
	 *   in the database
	 * @assumes mutant cell lines have already been resolved, i.e. existing MCL
	 *   keys passed in, new MCL set in resolvedALO
	 * @throws ALOResolvingException if errors resolving derivation or mutant
	 *         cell line attributes
	 * @throws DLALoggingException if logging error
	 * @throws CacheException if errors accessing a Lookup cache
	 * @throws DBException if errors adding to LazyCached lookups
	 * @throws ConfigException if resolvers have errors accessing configuration
	 * @notes CASES:
	 * 1) MCL in database
	 *    a) allele in database - compare resolved allele, mutation, reference
	 *               associations, and cell line associations  with info
	 *			     in the database, report discrepancies
	 *
	 *    b) allele NOT in database - report, for dbGSS Gene Traps should not 
	 *			     have mutant cell line in db w/o allele
	 * 2) MCL NOT in database
	 *    a) allele in database - compare resolved allele, mutation & reference
	 *			     associations and report; report existing cell line
	 *			     associations. We detect this case by checking the allele
	 *			     nomen for the incoming mutant cell line ID. This
	 *			     case is odd because we think we have a new allele
	 *			     (because the MCL is notin the database), but we
	 *			     find the MCL ID in the nomenclature of an allele in
	 *			     the database
	 *    b) allele NOT in database - if mutant cell line ID not in allele nomen 
	 *              (See 2a),create allele, allele MGI ID, mutation, reference
	 *   			associations and cell line association
	 * </UL>
	 */
	public Integer process(ALORawInput aloInput, ALO resolvedALO,
			HashSet existingMCLKeys, HashSet incomingMCLs) throws DBException, CacheException,
			ConfigException, DLALoggingException, ALOResolvingException,
			MutantCellLineAlleleException, CellLineIDInAlleleNomenException,
			AlleleMutationProcessorException {

		// the allele key of the processed allele - may be new or existing in the
		// database
		Integer alleleKey = null;
		if (existingMCLKeys.size() > 1) {
			// we shouldn't have  >1 cell line per gene trap allele
			// throw an exception
			

		} else if (existingMCLKeys.size() == 0) {
			alleleKey = processAlleleWithNewMCL(aloInput, resolvedALO); // CASE 2

		} else {
			Integer existingMCLKey = (Integer) existingMCLKeys.iterator().next();
			MutantCellLine incomingMCL = (MutantCellLine) incomingMCLs.iterator().next();
    		alleleKey = processAlleleWithExistingMCL(aloInput, resolvedALO,
					existingMCLKey, incomingMCL); // CASE 1
		}
		return alleleKey;
	}

    /**
     * no postprocessing at this time
     */
    public void postprocess() throws MGIException {

    }
	/**
	 * Processes DBGSS Gene Trap allele, mutant cell line association, mutation,
	 * reference information when the incoming cell line is NOT found to be in the
	 * database. We will create a new allele as long as there are no alleles in
	 * the database which have the cell line id in their nomenclature
	 * @param aloInput ALORawInput object - a set of raw attributes to resolve
	 * and add to the database
	 * @param resolvedALO - the ALO object to which will will add resolved
	 *         allele information
	 * @param existingMCLKey - the mutant cell line key of the incoming ALO
	 *              which was found in the database.NOTE: this my NOT the same
	 *              as the cell line associated with the Allele if the allele is
	 *              found to be in the database
	 * @assumes one MCL per allele
	 * @return Integer alleleKey of the processed allele - may be new or existing
	 *   in the database
	 * @throws ALOResolvingException if errors resolving derivation or mutant
	 *         cell line attributes
	 * @throws DLALoggingException if logging error
	 * @throws CacheException if errors accessing a Lookup cache
	 * @throws DBException if errors adding to LazyCached lookups
	 * @throws ConfigException if resolvers have errors accessing configuration
	 * @throws CellLineIDInAlleleNomenException if MCL ID found in allele
	 *          symbol or synonym
	 */
	private Integer processAlleleWithNewMCL(ALORawInput aloInput, ALO resolvedALO)
			throws DBException, CacheException, ConfigException, DLALoggingException,
			ALOResolvingException, CellLineIDInAlleleNomenException {

		// There is only one MCL per dbGSS gene trap allele, get it
		HashSet newMCLs = resolvedALO.getCellLineDAOs();
		ALL_CellLineDAO clDAO = (ALL_CellLineDAO) newMCLs.iterator().next();

		// genetrap alleles strain is same as the mutant
		// cell line strain (which comes from the parent which is attribute of the
		// Derivation).
		Integer alleleStrainKey = clDAO.getState().getStrainKey();
		
		// if this method is called we know there is only one cl in the set; get
		// it. Also get the LDB name for reporting when wefind the mcl ID in
		// allele nomen because it may be same MCL id, but a different
		// ldb (MCL ID/LDB is object identity for MCL in database). When
		// the MCL ID is in the allele nomen, a MCL and Allele will not be
		// created by the load. A Curator will need to create them. 
		CellLineRawAttributes cl = (CellLineRawAttributes)aloInput.
				getCellLines().iterator().next();
		String ldbName = cl.getLogicalDB();
		String rawCreator = cl.getDerivation().getCreator();
		KeyValue kv = labCodeLookup.lookup(rawCreator);
		String labCode = (String)kv.getValue();

		// check for mclID in allele nomenclature
		// search for this exact string in allele synonym
		String mclID = clDAO.getState().getCellLine();
		String nomenString = "(" + mclID + ")";

		// The symbols which contain "(mclID)"
		StringBuffer mclIdInSymbol = new StringBuffer();
		// the synonyms which contain "(mclID)" or ARE mclID
		StringBuffer mclIdInSynonym = new StringBuffer();

		// get allele symbols in which are found the mcl ID
		HashSet symbols = (HashSet)alleleSymbolsInDB.get(nomenString);
		if(symbols != null) {
		     for (Iterator i = symbols.iterator(); i.hasNext();) {
			mclIdInSymbol.append((String) i.next());
			mclIdInSymbol.append(" ");
		    }
		}

		// get allele synonyms in which are found the mcl ID
		HashSet synonyms = (HashSet)alleleSynonymsInDB.get(nomenString);
		if(synonyms != null) {
                     for (Iterator i = synonyms.iterator(); i.hasNext();) {
			String synonym = (String) i.next();
			if (synonym.indexOf(labCode) != -1 || synonym.equals(mclID)) {
			    mclIdInSynonym.append(synonym);
			    mclIdInSynonym.append(" ");
			}
		    }
                }

		// report both symbols and synonyms
		if (mclIdInSymbol.length()!= 0 && mclIdInSynonym.length() != 0) {
		    CellLineIDInAlleleNomenException e =
				new CellLineIDInAlleleNomenException();
				e.bindRecordString("MCL ID: " + mclID + " LDB Name: " + ldbName +
						" ALLELE SYMBOLS: " + mclIdInSymbol.toString() +
						" ALLELE SYNONYMS: " + mclIdInSynonym.toString());
				throw e;
		}
		// Just synonyms, report them
		else if (mclIdInSynonym.length() != 0) {
		    CellLineIDInAlleleNomenException e = new
			CellLineIDInAlleleNomenException();
				e.bindRecordString("MCL ID: " + mclID +
						" LDB Name: " + ldbName +
						" ALLELE SYNONYMS: " + mclIdInSynonym.toString());
				throw e;
		}
		// Just symbols, report them
		else if (mclIdInSymbol.length()!= 0) {
		    CellLineIDInAlleleNomenException e =
				new CellLineIDInAlleleNomenException();
				e.bindRecordString("MCL ID: " + mclID +
						" LDB Name: " + ldbName +
						" ALLELE SYMBOLS: " + mclIdInSymbol.toString());
		    throw e;
		}

		// mclID was not found in allele nomenclature, so continue ...
        
		// resolve allele and set in resolvedALO
		Allele allele = alleleResolver.resolve(aloInput.getAllele(), alleleStrainKey);

		// by setting a state in the ALO an allele key is created
		resolvedALO.setAllele(allele.getState());

		// now get the new allele key
		Integer alleleKey = resolvedALO.getAlleleDAO().getKey().getKey();

		// process MCL association
		Integer mclKey = clDAO.getKey().getKey();
		alleleMclProcessor.process(mclKey, resolvedALO);

		// process molecular mutation associations
		HashSet mutations = aloInput.getMutations();
		mutationProcessor.processMutationForNewAllele(mutations, resolvedALO);

		// process all reference associations
		processReferencesForNewAllele(aloInput, resolvedALO, alleleKey);
		return alleleKey;

	}

	/**
	 * Processes DBGSS Gene Trap allele, cell line association, mutation and
	 * reference information when the incoming cell line is found to be in the db
	 * we may or may not find an allele in the database
	 * @return Integer alleleKey of the processed allele - may be new or existing
	 *   in the database
	 * @param aloInput ALORawInput object - a set of raw attributes to resolve
	 * and add to the database
	 * @param resolvedALO - the ALO object to which will will add resolved
	 *         allele information
	 * @param existingMCLKey - the mutant cell line key of the incoming ALO
	 *              which was found in the database.NOTE: this my NOT the same
	 *              as the cell line associated with the Allele if the allele is
	 *              found to be in the database
	 * @param incomingMCL the incoming MCL resolved
	 * @assumes all parameters are not null
	 * @throws ALOResolvingException if errors resolving derivation or mutant
	 *         cell line attributes
	 * @throws DLALoggingException if logging error
	 * @throws CacheException if errors accessing a Lookup cache
	 * @throws DBException if errors adding to LazyCached lookups
	 * @throws ConfigException if resolvers have errors accessing configuration
	 */
	private Integer processAlleleWithExistingMCL(ALORawInput aloInput,
			ALO resolvedALO, Integer existingMCLKey, MutantCellLine incomingMCL)
			throws DBException, CacheException, ConfigException,
			DLALoggingException, ALOResolvingException,
			MutantCellLineAlleleException, AlleleMutationProcessorException {
		// System.out.println("In GeneTrapAlleleProcessor.processAlleleWithExistingMCL");
		// get the incoming cell line name

		String incomingCellLineName = incomingMCL.getCellLine();

		HashSet dbAlleles = alleleLookup.lookup(existingMCLKey);
		if (dbAlleles == null) {
			// throw exception - no allele for this cell line in the database
			// shouldn't be a gene trap cell line in the db without an allele
			MutantCellLineAlleleException e =
					new MutantCellLineAlleleException();
			e.bindRecordString("MCL Key: " + existingMCLKey +
					" not associated " + " with an allele in the database");
			throw e;

		} else if (dbAlleles.size() > 1) {
			// throw exception - shouldn't be > 1 allele for gene trap cell line
			MutantCellLineAlleleException e =
					new MutantCellLineAlleleException();
			// get the set of alleles as a string
			StringBuffer assocAlleles = new StringBuffer();
			for (Iterator i = dbAlleles.iterator(); i.hasNext();) {
                                AlleleData a = (AlleleData) i.next();
                                String s = a.getAlleleSymbol();
                                assocAlleles.append(s);
                                assocAlleles.append(" ");
                        }
			e.bindRecordString("MCL Key: " + existingMCLKey +
					" associated " + " with multiple alleles in the database: " +
					assocAlleles.toString());
			throw e;
		}

		// from here down we assume the incoming allele is in the database
		// this is a set of 1
		AlleleData existingAllele = (AlleleData)dbAlleles.iterator().next();
        	resolvedALO.setIsUpdate(Boolean.TRUE);
		Integer existingAlleleKey = existingAllele.getAlleleKey();
		String symbol = existingAllele.getAlleleSymbol();

		/**
		 * resolve incoming allele so we may compare to allele in the database
		 * Note, dbGSS gene trap alleles have same strain as Derivation (i.e.
		 * parent cell lin, this is why we pass existingAllele.getStrainKey()
		 */
		Allele incomingAllele = alleleResolver.resolve(aloInput.getAllele(),
				existingAllele.getStrainKey());
		//incomingAllele.compare(existingAllele);

		/**
		 * if there are > 1 cell lines associated with this allele in the db (i.e.
		 * this one plus other(s)) then report
		 */
		// find MCL(s) associated with the allele in the database
		HashMap dbMCLs = mclLookup.lookup(existingAlleleKey);

		// gather and report the set of MCLs associated with the allele in the database
		// other than the incoming MCL
		Boolean somethingToReport = Boolean.FALSE;
		StringBuffer mclDiffReport = new StringBuffer();
		mclDiffReport.append("MCLs in database and not in input for allele symbol " +
				incomingAllele.getAlleleSymbol());
		// should be only one MCL i.e. the incoming one from which we determined
		// this allele
		if (dbMCLs.size() > 1) {
			Iterator i = dbMCLs.keySet().iterator();
			while (i.hasNext()) {
			    Integer dbMclKey = (Integer)i.next();
			    String dbCellLineName = (String)dbMCLs.get(dbMclKey);
			    if (!dbCellLineName.equals(incomingCellLineName)) {
				mclDiffReport.append(" : ");
				mclDiffReport.append(dbCellLineName);
				somethingToReport = Boolean.TRUE;
			    }
			}
		}

		if (somethingToReport.equals(Boolean.TRUE)) {
			logger.logcInfo(mclDiffReport.toString(), false);
		}

		// compare molecular mutation associations, report 1. no mutation
		// 2. no mutation of this type
		HashSet mutations = aloInput.getMutations();
		mutationProcessor.processMutationsForExistingAllele(mutations,
				existingAlleleKey, symbol, resolvedALO);
		// compare reference associations, create new, report any in db
		// not in input
		processReferencesForExistingAllele(aloInput, resolvedALO,
				existingAlleleKey);

		return existingAlleleKey;
	}

	/**
	 * Processes DBGSS Gene Trap reference information by associating them with
	 * an allele
	 * @param aloInput ALORawInput object - a set of raw attributes to resolve
	 * and add to the database
	 * @param resolvedALO - the ALO object to which will will add resolved
	 *         reference associations
	 * @param newAlleleKey - with which to associate the references
	 * @throws CacheException if errors accessing a Lookup cache
	 * @throws DBException if errors adding to LazyCached lookups
	 * @throws ConfigException if resolvers have errors accessing configuration
	 */
	private void processReferencesForNewAllele(ALORawInput aloInput, ALO resolvedALO,
			Integer newAlleleKey) throws DBException, CacheException, ConfigException {
		// resolve references and set in resolvedALO; RefAssocProcessor is an old
		// class which takes one raw instance and returns a state - this is diff
		// erent paradigm than new processors for the ALO load
		HashSet rawRefs = aloInput.getReferenceAssociations();
		for (Iterator i = rawRefs.iterator(); i.hasNext();) {
			RefAssocRawAttributes rawRef = (RefAssocRawAttributes) i.next();
			MGI_Reference_AssocState refState =
					refAssocProcessor.process(rawRef, newAlleleKey);
			if (refState != null) {
				resolvedALO.addRefAssociation(refState);
			}
		}
	}

    /**
	 * Adds references to existing alleles where needed
	 * @param aloInput ALORawInput object - a set of raw attributes to resolve
	 * and add to the database
	 * @param resolvedALO - the ALO object to which will will add resolved
	 *         reference associations
	 * @param existingAlleleKey - with which to associate the references
	 * @throws CacheException if errors accessing a Lookup cache
	 * @throws DBException if errors adding to LazyCached lookups
	 * @throws ConfigException if resolvers have errors accessing configuration
	 */
	private void processReferencesForExistingAllele(ALORawInput aloInput,
			ALO resolvedALO, Integer existingAlleleKey) throws DBException,
			CacheException, ConfigException {

		// get references associated with allele in database, if none
		// HashSet will be empty set
		//HashSet existingPubMedIDSet = pubMedLookup.lookup(existingAlleleKey);
		HashSet existingPubMedIDSet = (HashSet)pubMedMap.get(existingAlleleKey);
		
		// get incoming references
		HashSet rawRefs = aloInput.getReferenceAssociations();
		//logger.logcInfo("ExistingPubMedIDSet: " + existingPubMedIDSet.toString(), false);
		for (Iterator i = rawRefs.iterator(); i.hasNext();) {
			RefAssocRawAttributes incomingRawRef = (RefAssocRawAttributes) i.next();
			String incomingRefID = incomingRawRef.getRefId();

			// if incomingRefId is not a JNumber and is not in the set of
			// pubMed IDs associated with the allele, create association
			if (!incomingRefID.startsWith("J:") && existingPubMedIDSet != null && !existingPubMedIDSet.contains(incomingRefID)) {
			    RefAssocRawAttributes newRawRef = new RefAssocRawAttributes();
			    newRawRef.setMgiType(new Integer(MGITypeConstants.ALLELE));
			    newRawRef.setRefId(incomingRefID);
			    newRawRef.setRefAssocType(new Integer(MGIRefAssocTypeConstants.ALLELE_SEQUENCE));
			    MGI_Reference_AssocState refState =
					    refAssocProcessor.process(newRawRef, existingAlleleKey);
			    if (refState != null) {
				    resolvedALO.addRefAssociation(refState);
				    // Add the reference to the lookup cache; we may have more than one
				    // sequence tag in the input representing a single allele already in 
				    // the database. Example: CZ169816/CZ169616/b3p1a6
				    // add the ID to the lookup in case we see it again in the input.
				    existingPubMedIDSet.add(incomingRefID);
				    pubMedMap.put(existingAlleleKey, existingPubMedIDSet);
				    
			    }
			    // just in case same reference listed twice in sequence record
			    existingPubMedIDSet.add(incomingRefID);
			}
		}
	}
	/**
	 * Loads sets of allele symbols and synonyms from the database
	 * @throws MGIException
	 */
	private void initSymbolMaps() throws MGIException {
		// Compile regular expression
		String patternStr = "(\\(.*)\\)";
		Pattern pattern = Pattern.compile(patternStr);

		// initialize the symbol set
		alleleSymbolsInDB = new HashMap();
		AlleleSymbolQuery symbolQuery = new AlleleSymbolQuery();
		DataIterator i = symbolQuery.execute();
		String symbol = null;
		while (i.hasNext()) {
		    symbol = (String) i.next(); 
		    Matcher matcher = pattern.matcher(symbol);
		    if(matcher.find() == true) {
			String match = matcher.group();
			if(!alleleSymbolsInDB.containsKey(match)) {
				HashSet s = new HashSet();
				s.add(symbol);
				alleleSymbolsInDB.put(match, s);
			}
			else {
			    HashSet s =  (HashSet)alleleSymbolsInDB.get(match);
			    s.add(symbol);
			    alleleSymbolsInDB.put(match, s);
			}
		    }
		}

		// initialize the synonym set
		alleleSynonymsInDB = new HashMap();
		AlleleSynonymQuery synonymQuery = new AlleleSynonymQuery();
		i = synonymQuery.execute();
		String synonym = null;
		while (i.hasNext()) {
		    synonym = (String) i.next();
                    Matcher matcher = pattern.matcher(synonym);
                    if(matcher.find() == true) {
                        String match = matcher.group();
                        if(!alleleSynonymsInDB.containsKey(match)) {
                                HashSet s = new HashSet();
                                s.add(synonym);
                                alleleSynonymsInDB.put(match, s);
                        }
                        else {
                            HashSet s =  (HashSet)alleleSynonymsInDB.get(match);
                            s.add(synonym);
                            alleleSynonymsInDB.put(match, s);
                        }
                    }
		}
	}
}


