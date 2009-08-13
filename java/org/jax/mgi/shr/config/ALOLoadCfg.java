package org.jax.mgi.shr.config;

/**
 * An object that retrieves Configuration pararmeters ALO Loads
 * @does
 *   <UL>
 *   <LI> provides methods to retrieve Configuration parameters that are
 *        specific to ALO Loads
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class ALOLoadCfg extends Configurator {

	/**
	 * Constructs a ALO Load configurator
	 * @throws ConfigException if a configuration manager cannot be obtained
	 */
	public ALOLoadCfg() throws ConfigException {
	}

	/**
	 * Gets whether to update existing Mutant Cell Line derivations
	 * @return true or false
	 * @throws ConfigException if UPDATE_MCLDERIVATIONnot found in configuration
	 */
	public Boolean getUpdateMCLDerivation() throws ConfigException {
		return getConfigBoolean("UPDATE_MCLDERIVATION");
	}

	/**
	 * Gets ALO Load provider
	 * @return The ALO Load provider
	 * @throws ConfigException if "LOAD_REFERENCE" not found in configuration
	 */
	public String getLoadProvider() throws ConfigException {
		return getConfigString("LOAD_PROVIDER");
	}

	/**
	 * Gets ALO Load reference jNumber
	 * @return The ALO Load reference jNumber
	 * @throws ConfigException if "LOAD_REFERENCE" not found in configuration
	 */
	public String getLoadReference() throws ConfigException {
		return getConfigString("LOAD_REFERENCE");
	}

	/**
	 * Gets allele inheritance mode
	 * @return The allele inheritance mode
	 * @throws ConfigException if "ALLELE_INHERIT_MODE" not found in configuration
	 */
	public String getAlleleInheritMode() throws ConfigException {
		return getConfigString("ALLELE_INHERIT_MODE");
	}

	/**
	 * Gets allele type
	 * @return The allele type
	 * @throws ConfigException if "ALLELE_TYPE" not found in configuration
	 */
	public String getAlleleType() throws ConfigException {
		return getConfigString("ALLELE_TYPE");
	}

	/**
	 * Gets allele status
	 * @return The allele status
	 * @throws ConfigException if "ALLELE_STATUS" not found in configuration
	 */
	public String getAlleleStatus() throws ConfigException {
		return getConfigString("ALLELE_STATUS");
	}

	/**
	 * Gets cell line derivation type
	 * @return The cell line derivation type
	 * @throws ConfigException if "CELLLINE_DERIV_TYPE" not found in configuration
	 */
	public String getCellLineDerivType() throws ConfigException {
		return getConfigString("CELLLINE_DERIV_TYPE");
	}

	/**
	 * Gets cell line  type
	 * @return The cell line  type
	 * @throws ConfigException if "CELLLINE_TYPE" not found in configuration
	 */
	public String getCellLineType() throws ConfigException {
		return getConfigString("CELLLINE_TYPE");
	}

	/**
	 * Gets molecular mutation
	 * @return The molecular mutation
	 * @throws ConfigException if "MOLECULAR_MUTATION" not found in configuration
	 */
	public String getMolecularMutation() throws ConfigException {
		return getConfigString("MOLECULAR_MUTATION");
	}

	/**
	 * Gets name of repeated allele record output file
	 * @return The name of the repeated allele record output file
	 * @throws ConfigException if "REPEAT_FILE_NAME" not found in configuration
	 */
	public String getRepeatFileName() throws ConfigException {
		return getConfigString("REPEAT_FILE_NAME");
	}
/**
	 * Gets name file which to write records which did not resolve
	 * @return The name of the repeated allele record output file
	 * @throws ConfigException if "UNRESOLVED_FILE_NAME" not found in configuration
	 */
	public String getUnresolvedRecordFileName() throws ConfigException {
		return getConfigString("UNRESOLVED_FILE_NAME");
	}

	/**
	 * Gets the Jobstream name
	 * @return the Jobstream name
	 * @throws ConfigException if "SEQ_JOBSTREAM" not found in configuration file
	 */
	public String getJobstreamName() throws ConfigException {
		return getConfigString("JOBSTREAM");
	}

	/**
	 * Gets the allele symbol template
	 * @return the allele symbol template
	 * @throws ConfigException if "ALLELE_SYMBOL_TEMPLATE" not found in configuration file
	 */
	public String getAlleleSymbolTemplate() throws ConfigException {
		return getConfigString("ALLELE_SYMBOL_TEMPLATE");
	}

	/**
	 * Gets the allele name template
	 * @return the allele name template
	 * @throws ConfigException if "ALLELE_NAME_TEMPLATE" not found in configuration file
	 */
	public String getAlleleNameTemplate() throws ConfigException {
		return getConfigString("ALLELE_NAME_TEMPLATE");
	}

	/**
	 * @return the allele to marker association qualifier
	 * @throws ConfigException if "ALLELE_MARKER_ASSOC_QUAL" not found in configuration file
	 */
	public String getAlleleMarkerAssocQualifier() throws ConfigException {
		return getConfigString("ALLELE_MARKER_ASSOC_QUAL");
	}
}
