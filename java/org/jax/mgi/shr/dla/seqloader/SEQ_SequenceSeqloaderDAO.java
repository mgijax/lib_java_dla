// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceDAO;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceKey;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.types.Converter;

/**
 * @is An object that represents a record in the SEQ_Sequence table. Overrides
 *     the superclass getDeleteSQL() method
 * @has
 *   <UL>
 *   <LI> SEQ_SequenceKey object
 *   <LI> SEQ_SequenceState object
 *   </UL>
 * @does
 *   <UL>
 *   <LI> Overrides the superclass getDeleteSQL() method with a call
 *       to a stored procedure for deleting Dummy sequences
 *   </UL>
 */

public class SEQ_SequenceSeqloaderDAO extends SEQ_SequenceDAO{

    /**
     * Constructor which accepts a given SEQ_SequenceState object and
     * will generate a new SEQ_SequenceKey object
     * @assumes Nothing
     * @effects Queries a database for next _Sequence_key
     * @param state The SEQ_SequenceState object
     * @throws ConfigException if error creating SEQ_SequenceKey
     * @throws DBException if error creating SEQ_SequenceKey
     */
     public SEQ_SequenceSeqloaderDAO (SEQ_SequenceState state)
       throws ConfigException, DBException {
       super(new SEQ_SequenceKey(), state);

     }

    /**
     * Constructor which accepts a given SEQ_SequenceState object and
     * a SEQ_SequenceKey object
     * @assumes Nothing
     * @effects Nothing
     * @param key The SEQ_SequenceKey object
     * @param state The SEQ_SequenceState object
     */
     public SEQ_SequenceSeqloaderDAO (SEQ_SequenceKey key,
            SEQ_SequenceState state) {
         super(key, state);
     }

     /**
       * Build the SQL statements needed to delete records from the database
       * for this object.
       * @assumes Nothing
       * @effects Nothing
       * @param None
       * @return A string representing the SQL statement.
       * @throws DBException
       */
       public String getDeleteSQL() {
            StringBuffer sql = new StringBuffer("SEQ_DeleteDummy ");
            // Following for BCP_Inline_Stream
            //StringBuffer sql = new StringBuffer ("call SEQ_DeleteDummy " );
            sql.append(Converter.toSQLString(this.getKey().getKey()));
            return new String(sql);
       }
}
// $Log
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
