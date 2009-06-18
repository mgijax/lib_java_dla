package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.loads.Alo.dbgss.DBGSSGeneTrapFactory;
import org.jax.mgi.dbs.mgd.lookup.MarkerKeyLookupBySeqKey;
import org.jax.mgi.dbs.mgd.lookup.SequenceKeyLookupBySeqID;
import org.jax.mgi.shr.config.ALOLoadCfg;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.dla.loader.alo.ALOLoaderConstants;
import org.jax.mgi.shr.dla.loader.alo.DerivationNameCreator;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that provides specific factories, based onthe configured provider, 
 * via the getFactory() method. For example if configured provider is 'dbGSS' 
 * getFactory() returns an instance of DBGSSGeneTrapFactory
 
 * @does
 *   <UL>
 *   <LI>Implements the static method getFactory() which will return a specific
 *       factory based on a configured ALO provider.
 *   <LI>Expects subclasses to implement its abstract methods
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public abstract class ALOLoaderAbstractFactory {
    
    public static ALOLoaderAbstractFactory getFactory() 
     throws MGIException {
	
	// get a configuration instance
	ALOLoadCfg config = new ALOLoadCfg();
	String provider = config.getLoadProvider();
	if (provider.equals(ALOLoaderConstants.DBGSS_GENETRAP)) {
	    return new DBGSSGeneTrapFactory();
	}
	return null;
    }
    
    public abstract DataIterator getDataIterator() throws MGIException;
    // subclasses of AlleleProcessor now get their own allele lookup
    //public abstract FullCachedLookup getAlleleLookup() throws MGIException;
    public abstract DerivationNameCreator getDerivationNameCreator()
	throws MGIException;
    public abstract MutantCellLineProcessor getMCLProcessor() 
	throws MGIException;
    public abstract AlleleProcessor getAlleleProcessor() throws MGIException;
    public abstract AlleleSequenceProcessor getAlleleSequenceProcessor() 
	throws MGIException;
    public abstract SequenceKeyLookupBySeqID getSequenceKeyLookupBySeqID()
	throws MGIException;
    public abstract MarkerKeyLookupBySeqKey getMarkerKeyLookupBySeqKey()
	 throws MGIException;
    public abstract ALO getALO(SQLStream loadStream) throws MGIException;
    //public abstract MolecularNoteProcessor getMolecularNoteProcessor()
	//throws MGIException;
}
