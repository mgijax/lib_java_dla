package org.jax.mgi.dbs.mgd.dao;




import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.types.Converter;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;

/**
 * An object that represents a record in the SEQ_Sequence table. Overrides
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
       * @return A string representing the SQL statement.
       * @throws DBException
       */
       public String getDeleteSQL() {
            StringBuffer sql = new StringBuffer("select * from SEQ_deleteDummy ");
            // Following for BCP_Inline_Stream
            //StringBuffer sql = new StringBuffer ("call SEQ_DeleteDummy " );
            //sql.append(Converter.toSQLString(this.getKey().getKey()));
	    sql.append(SeqloaderConstants.OPEN_PAREN);
	    sql.append(SeqloaderConstants.SGL_QUOTE);
	    sql.append(this.getKey().getKey());
	    sql.append(SeqloaderConstants.SGL_QUOTE);
	    sql.append(SeqloaderConstants.CLOSE_PAREN);
            return new String(sql);
       }
}
