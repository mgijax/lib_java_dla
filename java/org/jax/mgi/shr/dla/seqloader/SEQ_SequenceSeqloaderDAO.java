// $Header
// $Name
package org.jax.mgi.shr.dla.seqloader;

import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceDAO;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceState;
import org.jax.mgi.dbs.mgd.dao.SEQ_SequenceKey;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.types.Converter;

public class SEQ_SequenceSeqloaderDAO extends SEQ_SequenceDAO{

    /**
     * Constructor which accepts a given SEQ_SequenceState object and
     * will generate a new SEQ_SequenceKey object
     * @assumes Nothing
     * @effects Nothing
     * @param state The SEQ_SequenceState object
     */
     public SEQ_SequenceSeqloaderDAO (SEQ_SequenceState state)
       throws ConfigException, DBException
     {
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
            SEQ_SequenceState state)
     {
         super(key, state);

     }

     /**
       * Build the SQL statements needed to delete records from the database
       * for this object.
       * @assumes Nothing
       * @effects Nothing
       * @param None
       * @return A string representing the SQL statement.
       * @throws Nothing
       */
       public String getDeleteSQL() throws DBException
       {
            StringBuffer sql = new StringBuffer("SEQ_DeleteDummy ");
            // Following for BCP_Inline_Stream
            //StringBuffer sql = new StringBuffer ("call SEQ_DeleteDummy " );
            sql.append(Converter.toSQLString(this.getKey().getKey()));
            return new String(sql);
       }
}
// $Log