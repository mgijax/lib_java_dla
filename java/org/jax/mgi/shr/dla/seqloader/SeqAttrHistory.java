// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.dbs.mgd.MGITypeConstants;

public class SeqAttrHistory extends AttrHistory {

    // constants for SEQ_Sequence columns for which curation is tracked
    protected static final String TYPE_KEY_COL =
        MGD.seq_sequence._sequencetype_key;

    /**
     * constructor
     * @throws ConfigException thrown if there is an error with the database
     * @throws DBException thrown if there is an error with configuration
     */

    public SeqAttrHistory ()
    throws ConfigException, DBException {
        super(MGITypeConstants.SEQUENCE);
    }

    /**
     * return whether or not the type key attribute is curator edited
     * assumes nothing
     * @effects nothing
     * @param sequenceKey - the sequence key whose type attribute we are testing
     * @return true if the type key attribute is curator edited
     * @throws DBEXception thrown if there is an error with the database
     */
    public boolean isTypeCurated(Integer sequenceKey)
    throws DBException
    {
      return isCurated(TYPE_KEY_COL, sequenceKey);
    }
}
// $Log