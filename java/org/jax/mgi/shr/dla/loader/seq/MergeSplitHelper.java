//  $Header$
//  $Name$


package org.jax.mgi.shr.dla.loader.seq;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.dbs.mgd.loads.Acc.AccessionRawAttributes;
import org.jax.mgi.shr.dla.input.SequenceInput;

/**
 * A helper class for the MergeSplitProcessor that:
 * <UL>
 * <LI> determines which seqids in a given seqid set are in MGI as primary seqids.
 * <LI> remaps each secondary seqid to its set of primary seqids from a map of
 *      primary input seqids mapped to their 2ndary seqids that are
 *      in MGI as primary
 * </UL>
 * @has
 *   <UL>
 *   <LI>A lookup for primary seqids in MGI for a given logical db
 *   </UL>
 * @does
 *   <UL>
 *   <LI> Determines the set of secondary ids of a SequenceInput object that
 *        are primary in MGI
 *   <LI> knows how to remap a HashMap of primary seqid keys to secondary seqid values
 *        to a HashMap of secondary seqid keys to their primary seqid values<BR>
 *        e.g.this map: <BR>
 *        primary1 : [secondary1]
 *        primary2 : [secondary1]
 *        primary3 : [secondary2, secondary3]
 * <BR>
 *        becomes this map: <BR>
 *        secondary1 : [primary1, primary2]
 *        secondary2 : [primary3]
 *        secondary3 : [primary3]
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MergeSplitHelper {

    // lookup _object key for preferred seqids
    private AccessionLookup seqIdLookup;

    // resolved logicalDB for creating an Accession lookup
    private Integer logicalDBKey;

    // 2ndary seqids of the sequence we are processing
    private Vector secondaries;

    // the sequence key of a 2ndary seqid found in MGI as primary
    Integer seqKey;

    public MergeSplitHelper(AccessionLookup seqidLookup) throws KeyNotFoundException,
        DBException, CacheException, ConfigException {
        // configurator to get the logicalDB for the load
        SequenceLoadCfg config = new SequenceLoadCfg();

        // lookup the key for logical db
        LogicalDBLookup lookup = new LogicalDBLookup();

        // should we use an uncached accession lookup instead?
        this.seqIdLookup = seqidLookup;
            //new AccessionLookup(lookup.lookup(
            //config.getLogicalDB()).intValue(),
            //    MGITypeConstants.SEQUENCE, AccessionLib.PREFERRED);
    }

    /**
    * Determine which 2ndary seqids from the current sequence being processed
    * are in MGI as primary
    * @assumes nothing
    * @effects queries a database
    * @param seqInput object representing raw values of a Sequence
    * @throws KeyNotFoundException
    * @throws DBException
    * @throws CacheException
    * @return the set of merge/splits
    */

    public Vector getMergeSplitSeqs(SequenceInput seqInput)
        throws KeyNotFoundException, DBException, CacheException {

        // the set of merge/splits for this sequence
        Vector v = new Vector();
        // this sequences secondary seqids
        secondaries = seqInput.getSecondary();
        Iterator i = secondaries.iterator();
        while(i.hasNext()) {
            String seqId = ((AccessionRawAttributes)i.next()).getAccID();
            seqKey = seqIdLookup.lookup(seqId);

            // this secondary is primary in MGI, add it the the merge/split set
            if (seqKey != null) {
                v.add(seqId);
            }
        }
        return v;
        }

        /**
         * Create a HashMap of secondary seqid keys (that are primary in MGI) with
         *  primary seqids values from a HashMap of Primary seqid keys
         *  and 2ndary seqid values (that are primary in MGI)
         * @param primaryMap the HashMap of primary seqid keys
         * @assumes nothing
         * @effects nothing
         * @return the HashMap of secondary seqid keys
         */
        public HashMap createHash(HashMap primaryMap) {
            HashMap secondaryMap = new HashMap();

            // for each primary seqid
            for (Iterator mapI = primaryMap.keySet().iterator(); mapI.hasNext();) {
               // get the key
               String primary = (String) mapI.next();
               // get the value; a Vector of 2ndary ids that are primary in MGI
               Vector currentV = (Vector) primaryMap.get(primary);

               // for each secondary map it to its primary
               for (Iterator vecI = currentV.iterator(); vecI.hasNext(); ) {
                   // add each secondary to the newMap with value=primary
                   String secondary = (String) vecI.next();
                   //System.out.println("mapValue: " + secondary);
                   // add a new mapping
                   if (!secondaryMap.containsKey(secondary)) {
                       Vector x = new Vector();
                       x.add(primary);
                       secondaryMap.put(secondary, x);
                   }
                   // add a new primary to the mapping
                   else {
                       Vector y = (Vector) secondaryMap.get(secondary);
                       y.add(primary);
                   }
               }
           }
           return secondaryMap;
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

