package org.jax.mgi.shr.config;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * An object that retrieves Configuration parameters for the
 *     OrganismChecker
 * @has Nothing
 *   <UL>
 *   <LI> a configuration manager
 *   </UL>
 * @does
 *   <UL>
 *   <LI> provides methods to retrieve Configuration parameters to determine
 *        which organisms to load
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */


public class OrganismCheckerCfg extends Configurator {
    /**
     * Constructs a OrganismCheckerCfg
     * @assumes Nothing
     * @effects Nothing
     * @throws ConfigException if a configuration manager cannot be obtained
     */

    public OrganismCheckerCfg() throws ConfigException {

    }

    /**
     * Gets whether to load mouse sequence or not
     * @assumes Nothing
     * @effects Nothing
     * @return Boolean true if we are loading mouse
     * @throws ConfigException if "SEQ_LOAD_MOUSE" not found in configuration file
     */

    public Boolean getMouse() throws ConfigException {
        return new Boolean(getConfigString("SEQ_LOAD_MOUSE", "true"));
    }

    /**
     * Gets whether to load rat sequence or not
     * @assumes Nothing
     * @effects Nothing
     * @return Boolean true if we are loading rat
     * @throws ConfigException if "SEQ_LLOAD_RAT" not found in configuration file
     */

    public Boolean getRat() throws ConfigException {
        return new Boolean(getConfigString("SEQ_LOAD_RAT", "false"));
    }
    /**
     * Gets whether to load human sequence or not
     * @assumes Nothing
     * @effects Nothing
     * @return Boolean true if we are loading human
     * @throws ConfigException if "LOAD_HUMAN" not found in configuration file
     */

    public Boolean getHuman() throws ConfigException {
        return new Boolean(getConfigString("SEQ_LOAD_HUMAN", "false"));
    }

}
