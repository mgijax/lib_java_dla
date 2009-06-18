package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.dbs.mgd.loads.Alo.*;
import org.jax.mgi.dbs.mgd.dao.SEQ_GeneTrapDAO;
import org.jax.mgi.dbs.mgd.dao.SEQ_GeneTrapState;
import org.jax.mgi.dbs.mgd.loads.Alo.ALO;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.dao.SQLStream;

    /**
     * An object that manages a set of DAOs representing an DBGSS Gene Trap
     * Allele Like Object.
     * @has
     *   <UL>
     *   <LI>see superclass
     *   <LI>SEQ_GeneTrapDAO - additional gene trap sequence info 
     *   <LI>Vector of ACC_AccessionDAOs will have the following additional
     *       accessions
     *       <UL> 
     *           <LI>mutant cell line ID to cell line
     *           <LI>sequence tag ID to sequence
     *       </UL>
     *   </UL>
     * @does
     *   <UL>
     *   <LI>creates DAO objects for sequence gene trap information
     *   <LI>Adds  sequence gene trap information to a database
     *   <LI>Provides methods to get *copies only* of States for each of its
     *       DAO's
     *   </UL>
     * 
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class DBGSSGeneTrapALO extends ALO {

    // The sequence gene trap information
    private SEQ_GeneTrapDAO seqGeneTrapDAO;

    /**
     * Constructs a DBGSSGeneTrapALO object 
     * @param stream the stream which to pass the DAO objects to perform
     * database inserts, updates, and deletes
     */

    public DBGSSGeneTrapALO(SQLStream stream) {
        super(stream);
	//System.out.println("Just called DBGSSGeneTrapALO constructor");
    }
  
    /**
     * sets the sequence gene trap info
     * @effects Queries a database for the next key
     * @param state - the state object from which to create the DAO
     * @throws ConfigException if error creating the DAO object
     * @throws DBException if error creating the DAO object
     */

    public void setSeqGeneTrap(SEQ_GeneTrapState state)
        throws ConfigException, DBException {
	//System.out.println("setSeqGeneTrap(SEQ_GeneTrapState state)");
        seqGeneTrapDAO = new SEQ_GeneTrapDAO(state);
    }

    /**
     * get copy of sequence genetrap information
     */
    public SEQ_GeneTrapDAO getSeqGeneTrapDAO() {
        return (SEQ_GeneTrapDAO)seqGeneTrapDAO.clone();
    }
    /**
     * Determines the stream methods for and passes to those methods each of
     * its DAO objects.
     * Inserts the sequence gene trap information
     * @assumes Nothing
     * @effects Performs database Inserts, updates, and deletes.
     * @override to send SEQ_GeneTrapDAO to stream
     * @throws DBException if error inserting, updating, or deleting in the
     * database
     */
    public void sendToStream() throws DBException {
		//System.out.println("DBGSSGeneTrapALO sendToStream() calling  super.sendToStream()");
		super.sendToStream();
		// insert sequence gene trap information
		//System.out.println("DBGSSGeneTrapALO sendToStream() inserting stream.insert(seqGeneTrapDAO)");
		if (seqGeneTrapDAO != null) {
			stream.insert(seqGeneTrapDAO);
		}
    } 
}

