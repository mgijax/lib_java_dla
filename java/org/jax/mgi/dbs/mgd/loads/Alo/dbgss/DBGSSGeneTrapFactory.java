package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.dbs.mgd.loads.Alo.*;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.lookup.MarkerKeyLookupBySeqKey;
import org.jax.mgi.dbs.mgd.lookup.SequenceKeyLookupBySeqID;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.dla.input.genbank.GBInputFileNoSeq;
import org.jax.mgi.shr.dla.input.genbank.GBOrganismChecker;
import org.jax.mgi.shr.dla.loader.alo.DerivationNameCreator;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that implements ALOLoaderAbstractFactory methods to return
 * objects specific to the DBGSS Gene Trap ALO Load

 * @does
 *   <UL>
 *   <LI>Implements the super class abstract methods to return objects 
 *       specific to the DBGSS Gene Trap ALO Load
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class DBGSSGeneTrapFactory extends ALOLoaderAbstractFactory {

	public DataIterator getDataIterator() throws MGIException {
		GBInputFileNoSeq inputData = new GBInputFileNoSeq();

		GBOrganismChecker oc = new GBOrganismChecker();
		return inputData.getIterator(new DBGSSGeneTrapInterpreter(oc));
	}

	/* subclasses of allele processor, i.e.e DBGSSGeneTrapAlleleProcessor
     * now get their own allele lookup
     public FullCachedLookup getAlleleLookup() throws MGIException {
		return null; //new AlleleLookupByMutantCellLineKey();
	}*/

	public DerivationNameCreator getDerivationNameCreator()
			throws MGIException {
		return new DBGSSGeneTrapDerivationNameCreator();
	}

	public MutantCellLineProcessor getMCLProcessor()
			throws MGIException {
		return new MutantCellLineProcessor();
	}

	public AlleleProcessor getAlleleProcessor() throws MGIException {
		return new DBGSSGeneTrapAlleleProcessor();
	}

	public AlleleSequenceProcessor getAlleleSequenceProcessor()
			throws MGIException {
		return new DBGSSGeneTrapAlleleSequenceProcessor();
	}

	public SequenceKeyLookupBySeqID getSequenceKeyLookupBySeqID()
			throws MGIException {
		return new SequenceKeyLookupBySeqID(LogicalDBConstants.SEQUENCE, "GSS");
	}

	public MarkerKeyLookupBySeqKey getMarkerKeyLookupBySeqKey()
			throws MGIException {
		return new MarkerKeyLookupBySeqKey(LogicalDBConstants.SEQUENCE, "GSS");
	}

	public ALO getALO(SQLStream loadStream) throws MGIException {
		//System.out.println("DBGSSGeneTrapFactory returning DBGSSGeneTrapALO");
		return new DBGSSGeneTrapALO(loadStream);
	}
	/*
	public MolecularNoteProcessor getMolecularNoteProcessor() throws MGIException {
	}*/
}
