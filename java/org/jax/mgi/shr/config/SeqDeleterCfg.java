package org.jax.mgi.shr.config;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * An object that retrieves Configuration pararmeters for the SeqDeleter
 * @has
 *   <UL>
 *   <LI> a configuration manager
 *   </UL>
 * @does
 *   <UL>
 *   <LI> provides methods to retrieve Configuration parameters that are
 *        specific to the sequence deleter
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SeqDeleterCfg extends Configurator {

    /**
    * Constructs a SeqDeleterLoadCfg object
    * @throws ConfigException if a configuration manager cannot be obtained
    */

    public SeqDeleterCfg() throws ConfigException {
    }


    /**
     * get the interpreter object to use.
     * @return interpreter object.
     * @throws ConfigException thrown if "SEQ_INTERPRETOR" not found in configuration
     *   file or interpreter object could not be created from the value
     */
    public Object getInterpreterClass() throws ConfigException {
        return getConfigObject("SEQ_INTERPRETER");
    }

    /**
     * get the logicalDB name for the provider
     * @return logicalDB name
     * @assumes nothing
     * @effects nothing
     * @throws ConfigException thrown if "SEQ_LOGICALDB" not found in
     *    configuration file
     */
    public String getLogicalDB() throws ConfigException {
        return getConfigString("SEQ_LOGICALDB");
    }

   /**
    * get the repeat file name
    * @return the repeat file name
    * @throws ConfigException thrown if "SEQ_REPEAT_FILE" not found in
    *    configuration file
    */
    public String getRepeatFileName() throws ConfigException {
        return getConfigString("SEQ_REPEAT_FILE");
    }

    /**
     * get the SequenceLookup batch size, default is 400
     * @return number of seqids to batch in one query.
     * @assumes nothing
     * @effects nothing
     * @throws ConfigException won't be actually throw because there is a default valueS
     * from the configuration
     */
    public String getQueryBatchSize() throws ConfigException {
      return getConfigString("SEQ_QUERY_BATCHSIZE", "400");
    }


}
