package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.HashMap;

import org.jax.mgi.shr.config.GeneTrapLoadCfg;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.exception.MGIException;

    /**
     * An object that determines the good blat hit count for a gene trap sequence
     * @has
     *   <UL>
     *   <LI>cache of seqids mapped to the number of good blat hits
     *   <LI>File Reader for Best Blat Hits file 
     *   <LI>File Reader for Single Blat Hits file
     *   </UL>
     * @does
     *   <UL>
     *   <LI>creates a cache of seqids to number of blat hits
     *   <UL>
     *     <LI> If seqID is in the Single Best BLAT Hit File (SBBHF): count = 1
     *     <LI> If seqID is in the Best BLAT Hits File (BBHF), and not in SBBHF: count = # in BBHF
     *     <LI> If it's not in BBHF: count = 0
     *   </UL>
     *   <LI>Provides a lookup method by gene trap seqID
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */
public class GoodHitCountLookup {
    // maps gene trap seqIDs to good hit count {String:Integer, ...}
    private HashMap cache;
    
    // Reader for best blat hits psl file
    private BufferedReader bestHitsReader;
    
    // Reader for best blat hits single GFF file
    private BufferedReader singleHitsReader;
    
    // Gene trap load configurator
    private GeneTrapLoadCfg config;

	public BufferedReader getBestHitsReader() {
		return bestHitsReader;
	}
    
    private DLALogger logger = DLALogger.getInstance();
    
    // constants
    private static final String TAB = "\t";
    private static final String PIPE =  "|";
    
    /** Creates a new instance of GoodHitCountLookup */
    public GoodHitCountLookup() throws MGIException {
	// create file readers
        try {
	    config = new GeneTrapLoadCfg();
            bestHitsReader = new BufferedReader(new FileReader(
		config.getBestHitsFile()));
            singleHitsReader = new BufferedReader(new FileReader(
		config.getSingleHitsFile() ));
	    cache = new HashMap();
	    loadCache();
        }
        catch (IOException e) {
            throw new MGIException(e.getMessage());
        }
    }
    
    /**
   * lookup the number of good hits counts for a seqID
   * @param seqID
   * @returns number of good hit counts, null if no hits
   */
    public Integer lookup(String seqID) {
	return (Integer)cache.get(seqID);
    }
    /**
     * loads the lookup cache according to these rules:
     *   <UL>
     *   <LI> If seqID is in the Single Best BLAT Hit File (SBBHF): count = 1
     *   <LI> If seqID is in the Best BLAT Hits File (BBHF), and not in SBBHF: count = # in BBHF
     *   <LI> If it's not in BBHF: count = 0
     *   </UL>
     *  file formats:
     *   <UL>
     *   <LI>See http://genome.ucsc.edu/FAQ/FAQformat#format2 for psl format
     *   <LI>See http://www.sequenceontology.org/gff3.shtml for gff format
     *   </UL>
     */
    private void loadCache() throws MGIException {
	// first process best hits, column 10 contains seqid and
	// looks like "gi|53838793|gb|CW509288.1|CW509288"
	//logger.logvInfo("Loading Best Hits", false);
        try {
            String line = bestHitsReader.readLine();
            // iterate through file
            while (line != null) {
                StringTokenizer lt = new StringTokenizer(line, TAB);
                for (int i = 1; i < 10; i++) {
		    lt.nextToken();
		}
		
		// get column 10 and tokenize
                String querySeqInfo = lt.nextToken();
		StringTokenizer st = new StringTokenizer(querySeqInfo, PIPE);
		for (int i = 1; i < 5;i++ ) {
		    st.nextToken();
		}
		
		// get the seqID
		String seqID = st.nextToken();
		//logger.logvInfo(seqID, false);
		Integer numHits = (Integer)cache.get(seqID);
		if (numHits == null) {
		    numHits = new Integer(1);
		    //logger.logvInfo("new seqid, adding to cache",false);
		}
		else {
		    int x = numHits.intValue();
		    numHits = new Integer(++x);
		    //logger.logvInfo("existing seqid, updating cache value to " + numHits, false);
		    
		}
	    
		cache.put(seqID, numHits);		
                line = bestHitsReader.readLine();
            }
        }
       catch (IOException e) {
            throw new MGIException(e.getMessage());
        }
	loadSingleHits();
    }
    /**
     * single hits trumps best hits, so overwrite any values loaded from 
     * best hits with '1' 
     */
    private void loadSingleHits() throws MGIException {
	try {
	    String line = singleHitsReader.readLine();
	    // we are looking for seqIDs prefixed with "Gene " in column 9
	    String identity = "Gene ";
	    // index of where seqID starts in column 9
	    int index = 5;
	    
	    String seqID;
	    // iterate through file
	    while (line != null) {
		StringTokenizer lt = new StringTokenizer(line, TAB);
		for (int i = 1; i < 9; i++) {
		    lt.nextToken();
		}
		// get seqID
		String column9= lt.nextToken();
		if (column9.startsWith(identity)) {
		    
		    seqID = column9.substring (index);
		    cache.put(seqID, new Integer("1"));
		}
		line = singleHitsReader.readLine();
	    }
	} catch (IOException e) {
            throw new MGIException(e.getMessage());
        }
    }
    
}
