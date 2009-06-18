package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.mgd.loads.Alo.*;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.*;
import org.jax.mgi.dbs.mgd.lookup.AlleleLookupByMutantCellLineKey;
import org.jax.mgi.dbs.mgd.lookup.MutantCellLineLookupByAlleleKey;
import org.jax.mgi.dbs.mgd.lookup.PubMedIDLookupByAlleleKey;
import org.jax.mgi.dbs.mgd.query.AlleleSymbolQuery;
import org.jax.mgi.dbs.mgd.query.AlleleSynonymQuery;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.input.alo.ALORawInput;
import org.jax.mgi.shr.dla.loader.alo.*;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.shr.exception.MGIException;

import java.util.Iterator;
import java.util.HashSet;

/**
 * An object that process allele information by resolving allele attributes to 
 * DAOs representing
 * <UL>
 * <LI>Allele
 * <LI>Allele MGI ID
 * <LI>Allele Mutant Cell Line association 
 * <LI>Allele Mutation
 * <LI>Allele Reference Associations
 * </UL>
 * @does provides the basic needs objects for Allele Processors
 * <UL>
 * <LI>mutant cell line
 * <LI>mutant cell line ID association to the cell line object
 * </UL>
 * Determines, if possible, object identity in the database, reporting 
 * differences in incoming information with respect to the data in the database 
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class DBGSSGeneTrapAlleleProcessor extends AlleleProcessor {

	private AlleleLookupByMutantCellLineKey alleleLookup;
	private MutantCellLineLookupByAlleleKey mclLookup;
	private PubMedIDLookupByAlleleKey pubMedLookup;
	private HashSet alleleSymbolsInDB;
	private HashSet alleleSynonymsInDB;

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
		initSymbolSets();
	}

	/**
	 * Processes DBGSS Gene Trap allele, cell line association, mutation and
	 * reference information
	 * @param aloInput ALORawInput object - a set of raw attributes to resolve
	 * and add to the database
	 * @param resolvedALO - the ALO object to which will will add resolved
	 *         allele information
	 * @param existingMCLKeys - the set of existing mutant cell line objects
	 * that have been determined to be in the database. For dbGSS Gene Traps
	 * there is only one MCL per allele.
	 * @param incomingMCLs - the set of incoming MCL objects. For gene traps this
	 *  will be a set of one. If MCL found in the database (i.e. existingMCLKeys
	 * has a value) then we compare the incoming attributes with that in the database
	 * @return Integer alleleKey of the processed allele - may be new or existing
	 *   in the database
	 * @assumes mutant cell lines have already been resolved, existing MCL keys
	 *   passed in, new MCL set in resolvedALO
	 * @throws ALOResolvingException if errors resolving derivation or mutant
	 *         cell line attributes
	 * @throws DLALoggingException if logging error
	 * @throws CacheException if errors accessing a Lookup cache
	 * @throws DBException if errors adding to LazyCached lookups
	 * @throws ConfigException if resolvers have errors accessing configuration
	 * @notes CASES:
	 * 1) MCL in database
	 *    a) allele in database - compare resolved allele, mutation, reference
	 *                         associations, and cell line associations  with info
	 *			     in the database, report discrepancies
	 *
	 *    b) allele NOT in database - report, for dbGSS Gene Traps should not have mutant
	 *			 cell line in db w/o allele
	 * 2) MCL NOT in database
	 *    a) allele in database - compare resolved allele, mutation & reference
	 *			     associations and report; report existing cell line
	 *			     associations. We detect this by checking the allele
	 *			     nomen for the incoming mutant cell line ID. This
	 *			     case is odd because we think we have a new allele
	 *			     (because the MCL is notin the database), but we
	 *			     find the MCL ID in the nomenclature of an allele in
	 *			     the database
	 *    b) allele NOT in database - if mutant cell line ID not in allele nomen (See 2a),
	 *                    create allele, allele MGI ID, mutation, reference
	 *			associations and cell line association
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
		// System.out.println("GeneTrapAlleleProcessor.process existingMCLKeys.size: "
		//   + existingMCLKeys.size());
		if (existingMCLKeys.size() > 1) {
			// we shouldn't have  >1 cell line per gene trap allele
			// throw an exception
		} else if (existingMCLKeys.size() == 0) {
			//System.out.println("Calling processAlleleWithNewMCL SeqID: " + aloInput.getSequenceAssociation().getSeqID());

			alleleKey = processAlleleWithNewMCL(aloInput, resolvedALO); // CASE 2

		} else {
			Integer existingMCLKey = (Integer) existingMCLKeys.iterator().next();
			MutantCellLine incomingMCL = (MutantCellLine) incomingMCLs.iterator().next();
			//System.out.println("Calling processAlleleWithExistingMCL SeqID: " + aloInput.getSequenceAssociation().getSeqID());
			alleleKey = processAlleleWithExistingMCL(aloInput, resolvedALO,
					existingMCLKey, incomingMCL); // CASE 1
		}
		return alleleKey;
	}

	/**
	 * Processes DBGSS Gene Trap allele, cell line association, mutation and
	 * reference information when the incoming cell line is NOT found to be in the
	 * database. We will create  new allele as long as there are no alleles in
	 * the database which have the cell line id in their nomenclature
	 * @param aloInput ALORawInput object - a set of raw attributes to resolve
	 * and add to the database
	 * @param resolvedALO - the ALO object to which will will add resolved
	 *         allele information
	 * @param existingMCLKey - the mutant cell line key of the incoming ALO
	 *              which was found in the database.NOTE: this my NOT the same
	 *              as the cell line associated with the Allele if the allele is
	 *              found to be in the database
	 * @return Integer alleleKey of the processed allele - may be new or existing
	 *   in the database
	 * @throws ALOResolvingException if errors resolving derivation or mutant
	 *         cell line attributes
	 * @throws DLALoggingException if logging error
	 * @throws CacheException if errors accessing a Lookup cache
	 * @throws DBException if errors adding to LazyCached lookups
	 * @throws ConfigException if resolvers have errors accessing configuration
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

		// check for mclID in allele nomenclature
        // search for this exact string in allele synonym
		String mclID = clDAO.getState().getCellLine();
        // search for this string in allele symbol AND synonym
        String nomenString = "(" + mclID + ")";
        // The symbols which contain "(mclID)"
        StringBuffer mclIdInSymbol = new StringBuffer();
        // the synonyms which contain "(mclID)" or ARE mclID
        StringBuffer mclIdInSynonym = new StringBuffer();

        // check all allele symbols
		for (Iterator i = alleleSymbolsInDB.iterator(); i.hasNext();) {
			String symbol = (String) i.next();
			// if "(mclID)" found in symbol, report and skip
			if (symbol.indexOf(nomenString) != -1 ) {
				mclIdInSymbol.append(symbol);
                mclIdInSymbol.append(" ");
			}
		}
        // check all allele synonyms
		for (Iterator i = alleleSynonymsInDB.iterator(); i.hasNext();) {
			String synonym = (String) i.next();
			// if mcl ID is the synonym, or  "(mclID)" found in synonym report and skip
			if (synonym.indexOf(nomenString) != -1 || synonym.equals(mclID)) {
                mclIdInSynonym.append(synonym);
                mclIdInSynonym.append(" ");
			}
		}
        // report both symbols and synonyms
        if (mclIdInSymbol.length()!= 0 && mclIdInSynonym.length() != 0) {
            CellLineIDInAlleleNomenException e =
                        new CellLineIDInAlleleNomenException();
			e.bindRecordString("MCL ID: " + mclID + " ALLELE SYMBOLS: " +
                    mclIdInSymbol.toString() + " ALLELE SYNONYMS: " +
                        mclIdInSynonym.toString());
			throw e;
        }
        // Just synonyms, report them
        else if (mclIdInSynonym.length() != 0) {
            CellLineIDInAlleleNomenException e = new
                CellLineIDInAlleleNomenException();
			e.bindRecordString("MCL ID: " + mclID + " ALLELE SYNONYMS: " +
                    mclIdInSynonym.toString());
			throw e;
        }
        // Just symbols, report them
        else if (mclIdInSymbol.length()!= 0) {
            CellLineIDInAlleleNomenException e =
                        new CellLineIDInAlleleNomenException();
			e.bindRecordString("MCL ID: " + mclID + " ALLELE SYMBOLS: " +
                    mclIdInSymbol.toString());
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
		//System.out.println("GeneTrapAlleleProcessor.processAlleleWithNewCellLine MUTATIONS: " + mutations.toString() + " SIZE: " + mutations.size());
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
				Allele a = (Allele) i.next();
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
		Allele existingAllele = (Allele) dbAlleles.iterator().next();
        resolvedALO.setIsUpdate(Boolean.TRUE);
		Integer existingAlleleKey = existingAllele.getAlleleKey();
		//System.out.println("GeneTrapAlleleProcessor.processAlleleWithExistingMCL existing allele key: " + existingAlleleKey);
		String symbol = existingAllele.getAlleleSymbol();
		//System.out.println("GeneTrapAlleleProcessor.processAlleleWithExistingMCL existing allele symbol: " + symbol);
		/**
		 * resolve incoming allele so we may compare to allele in the database
		 * Note, dbGSS gene trap alleles have same strain as Derivation (i.e.
		 * parent cell lin, this is why we pass existingAllele.getStrainKey()
		 */
		Allele incomingAllele = alleleResolver.resolve(aloInput.getAllele(),
				existingAllele.getStrainKey());
		incomingAllele.compare(existingAllele);

		/**
		 * if there are > 1 cell lines associated with this allele in the db (i.e.
		 * this one plus other(s)) then report
		 */
		// find MCL(s) associated with the allele in the database
		HashSet dbMCLs = mclLookup.lookup(existingAlleleKey);

		// gather and report the set of MCLs associated with the allele in the database
		// other than the incoming MCL
		Boolean somethingToReport = Boolean.FALSE;
		StringBuffer mclDiffReport = new StringBuffer();
		mclDiffReport.append("MCLs in database and not in input for allele symbol " +
				incomingAllele.getAlleleSymbol());
		// should be only one MCL i.e. the incoming one from which we determined
		// this allele
		if (dbMCLs.size() > 1) {
			for (Iterator i = dbMCLs.iterator(); i.hasNext();) {
				MutantCellLine dbMCL = (MutantCellLine) i.next();
				String dbCellLineName = dbMCL.getCellLine();
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
		//System.out.println("GeneTrapAlleleProcessor.processAlleleWithExistingCellLine MUTATIONS: " + mutations.toString() + " SIZE: " + mutations.size());
		mutationProcessor.processMutationsForExistingAllele(mutations,
				existingAlleleKey, symbol, resolvedALO);
		// compare reference associations, create new, report any in db
		// not in input
		//System.out.println("Calling processRefrencesForExsitingAllele SeqID " + aloInput.getSequenceAssociation().getSeqID());
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
	 * @param alleleKey - with which to associate the references
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

	private void processReferencesForExistingAllele(ALORawInput aloInput,
			ALO resolvedALO, Integer existingAlleleKey) throws DBException,
			CacheException, ConfigException {

		// get references associated with allele in database, if none
		// HashSet will be empty set
		HashSet existingPubMedIDSet = pubMedLookup.lookup(existingAlleleKey);
        //System.out.println("existingPubMedIDSet for alleleKey: " +
                //existingAlleleKey + " " + existingPubMedIDSet.toString());


		// get incoming references
		HashSet rawRefs = aloInput.getReferenceAssociations();

		for (Iterator i = rawRefs.iterator(); i.hasNext();) {
			RefAssocRawAttributes incomingRawRef = (RefAssocRawAttributes) i.next();
			String incomingRefID = incomingRawRef.getRefId();
			// if incomingRefId is not a JNumber and is not in the set of
			// pubMed IDs associated with the allele, create association
			//System.out.println("RefID: " + incomingRefID + " for existingAlleleKey: " + existingAlleleKey);
			if (!incomingRefID.startsWith("J:") && !existingPubMedIDSet.contains(incomingRefID)) {
				//System.out.println("Adding incomingRefId: " + incomingRefID);
				RefAssocRawAttributes newRawRef = new RefAssocRawAttributes();
				newRawRef.setMgiType(new Integer(MGITypeConstants.ALLELE));
				newRawRef.setRefId(incomingRefID);
				newRawRef.setRefAssocType(new Integer(MGIRefAssocTypeConstants.ALLELE_SEQUENCE));
				MGI_Reference_AssocState refState =
						refAssocProcessor.process(newRawRef, existingAlleleKey);
				if (refState != null) {
					resolvedALO.addRefAssociation(refState);
				}
                // just in case same reference listed twice in sequence record
                existingPubMedIDSet.add(incomingRefID);
			}
		}
	}

	private void initSymbolSets() throws MGIException {

		// initialize the symbol set
		alleleSymbolsInDB = new HashSet();
		AlleleSymbolQuery symbolQuery = new AlleleSymbolQuery();
		DataIterator i = symbolQuery.execute();
		while (i.hasNext()) {
			this.alleleSymbolsInDB.add((String) i.next());
		}

		// initialize the synonym set
		alleleSynonymsInDB = new HashSet();
		AlleleSynonymQuery synonymQuery = new AlleleSynonymQuery();
		i = synonymQuery.execute();
		while (i.hasNext()) {
			this.alleleSynonymsInDB.add((String) i.next());
		}
	}
}


