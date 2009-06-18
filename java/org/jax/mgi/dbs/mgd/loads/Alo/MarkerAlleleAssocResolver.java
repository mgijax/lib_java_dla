package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.dao.ALL_Marker_AssocState;
import org.jax.mgi.dbs.mgd.lookup.MarkerKeyLookupByMGIID;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.shr.cache.CacheConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.config. ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.loader.alo.ALOResolvingException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An object that resolves raw allele attributes to an ALL_Marker_AssocState
 * @has molecular mutation vocabulary lookup
 * @does Creates an ALL_Marker_Assoctate
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MarkerAlleleAssocResolver {

    private MarkerKeyLookupByMGIID markerKeyLookupByMGIID;
    private VocabKeyLookup qualifierKeyLookup;
    

     /**
     * Constructs a MarkerAlleleAssocResolver
     * @effects queries a database to load the lookup cache
     * @throws MGIException-  error creating the lookup
     */

    public MarkerAlleleAssocResolver() throws MGIException {
	
	markerKeyLookupByMGIID = new MarkerKeyLookupByMGIID();
	
	qualifierKeyLookup = new VocabKeyLookup(
	    VocabularyTypeConstants.MKR_ALLELE_ASSOC_QUAL, 
		CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
	    
    }

    /**
      * creates a ALL_Marker_AssocState
      * @param marker a String representing a marker, e.g. MGI ID
      * @throws ALOResolvingException if a lookup fails to find a key
      * @throws DBException if error adding to any lazy cached lookups
      * @throws CacheException if error doing lookup
      * @throws ConfigException if error doing lookup
      * @return An ALL_Allele_MutationState
      */
    public ALL_Marker_AssocState resolve(String mgiID, Integer alleleKey, String
	    qualifier) 
	throws DBException, CacheException, ConfigException, 
	ALOResolvingException {
	
	Integer markerKey = (Integer)markerKeyLookupByMGIID.lookup(mgiID);
	if (markerKey == null) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Marker MGI ID/" + mgiID);
	      throw resE;
	} 
	Integer qualifierKey = null;
	try {
	    qualifierKey = qualifierKeyLookup.lookup(qualifier);
	} catch (KeyNotFoundException e) {
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Qualifier/" + qualifier);
	      throw resE;
	  } catch (TranslationException e) { // won't happen, no translator 
						  //for this vocab
	      ALOResolvingException resE = new ALOResolvingException();
	      resE.bindRecordString("Allele Qualifier/" + qualifier);
	      throw resE;
	  }
	ALL_Marker_AssocState state = new ALL_Marker_AssocState();
	state.setAlleleKey(alleleKey);
	state.setMarkerKey(markerKey);
	state.setRefsKey(null);
	state.setQualifierKey(qualifierKey);
	
	return state;
    }
}
