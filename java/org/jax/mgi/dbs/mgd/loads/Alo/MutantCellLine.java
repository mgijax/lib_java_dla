package org.jax.mgi.dbs.mgd.loads.Alo;

import java.sql.Timestamp;

import org.jax.mgi.dbs.mgd.dao.ALL_CellLineState;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * An object that represents a denormalized ALL_CellLine object representing a
 * mutant cell line
 * @has set of mutant attributes for a ALL_CellLine
 *   
 * @does
 *   <UL>
 *   <LI>Provides getters and setters for its attributes
 *   <LI>Provides compare method to compare itself to another
 *       instance of itself, reporting differences
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class MutantCellLine {

	private DLALogger logger;
	// may be null if instance represents incoming mutant cell line (MCL) values
	private Integer mclKey = null;
	// MCL name
	private String cellLine = null;
	private String cellLineType = null;
	private Integer cellLineTypeKey = null;
	private String strain = null;
	private Integer strainKey = null;
	// may be null if instance represents incoming MCL values
	private Integer derivationKey = null;
	private Boolean isMutant = null;
	// the ID associated with the MCL via the accession table, may be
	// same as 'cellLine'
	private String accID;
	// logicalDB namd and key for 'accID' association to this MCL
	private String ldbName;
	private Integer ldbKey;
	private Timestamp creationDate;
	private Timestamp modificationDate;
	private Integer createdByKey;
	private Integer modifiedByKey;

	public MutantCellLine() throws DLALoggingException {
		logger = DLALogger.getInstance();
	}

	/*
	 * setters
	 */
	public void setMCLKey(Integer key) {
		this.mclKey = key;
	}

	public void setCellLine(String cellLine) {
		this.cellLine = cellLine;
	}

	public void setCellLineType(String cellLineType) {
		this.cellLineType = cellLineType;
	}

	public void setCellLineTypeKey(Integer cellLineTypeKey) {
		this.cellLineTypeKey = cellLineTypeKey;
	}

	public void setStrain(String strain) {
		this.strain = strain;
	}

	public void setStrainKey(Integer strainKey) {
		this.strainKey = strainKey;
	}

	public void setDerivationKey(Integer derivationKey) {
		this.derivationKey = derivationKey;
	}

	public void setIsMutant(Boolean isMutant) {
		this.isMutant = isMutant;
	}

	public void setAccID(String accID) {
		this.accID = accID;
	}

	public void setLdbName(String ldbName) {
		this.ldbName = ldbName;
	}

	public void setLdbKey(Integer ldbKey) {
		this.ldbKey = ldbKey;
	}
	public void setCreationDate(Timestamp cd) {
	    this.creationDate = cd;
	}
	public void setModificationDate(Timestamp md) {
		this.modificationDate = md;
	}
	public void setCreatedByKey(Integer cbk) {
		this.createdByKey = cbk;
	}
	public void setModifiedByKey(Integer mbk) {
		this.modifiedByKey = mbk;
	}

	/*
	 * getters
	 */
	public Integer getMCLKey() {
		return mclKey;
	}

	public String getCellLine() {
		return cellLine;
	}

	public String getCellLineType() {
		return cellLineType;
	}

	public Integer getCellLineTypeKey() {
		return cellLineTypeKey;
	}

	public String getStrain() {
		return strain;
	}

	public Integer getStrainKey() {
		return strainKey;
	}

	public Integer getDerivationKey() {
		return derivationKey;
	}

	public Boolean getIsMutant() {
		return isMutant;
	}

	public String getAccID() {
		return accID;
	}

	public String getLogicalDBName() {
		return ldbName;
	}

	public Integer getLogicalDBKey() {
		return ldbKey;
	}
	public Timestamp getCreationDate() {
	    return creationDate;
	}
	public Timestamp getModificationDate() {
            return modificationDate;
        }
	public Integer getCreatedByKey() {
            return createdByKey;
        }
        public Integer getModifiedByKey() {
            return modifiedByKey;
        }

	/**
	 * @assumes all non-null database attributes are set
	 */
	public ALL_CellLineState getState() {
		// create a state object from attributes
		ALL_CellLineState state = new ALL_CellLineState();
		state.setCellLine(cellLine);
		state.setCellLineTypeKey(cellLineTypeKey);
		state.setDerivationKey(derivationKey);
		state.setIsMutant(isMutant);
		state.setStrainKey(strainKey);
		state.setCreationDate(creationDate);
		state.setModificationDate(modificationDate);
		state.setCreatedByKey(createdByKey);
		state.setModifiedByKey(modifiedByKey);
		return state;
	}

	/**
	 * compares values of attributes between this MutantCellLine instance and
	 * another MutantCellLine instance
	 * @assumes So we may correctly label attributes when reporting the
	 *           assumption is that the current instance represents
	 *           incoming mutant cell line values and 'fromDB' represents
	 *           a mutant cell line in the database.
	 * @assumes incoming null (this) attributes are represented as 0 (zero)
	 *       for Integers and "null" for Strings. This is because we may be
	 *       missing some incoming mutant cell line attributes. Only derivation
	 *	     key  may be null when coming from the database
	 * @param fromDB the mutant cell line instance, from the database, to compare to
	 */
	public void compare(MutantCellLine fromDB) {
		//System.out.println("In MCL.compare");
		//System.out.println("this.accID: " + this.accID);
		//System.out.println("db.accID: " + fromDB.getAccID());

		// if database attribute different than incoming attribute, then report
		String dbCellLine = fromDB.getCellLine();
		if (!cellLine.equals(dbCellLine)) {
			logger.logcInfo("MCL_COMPARE: IncomingMCL=" + cellLine +
					" dbMCL=" + dbCellLine, false);
		}

		String dbCellLineType = fromDB.getCellLineType();
		if (!cellLineType.equals("null") && !cellLineType.equals(dbCellLineType)) {
			logger.logcInfo("MCL_COMPARE: IncomingMCL=" + cellLine +
					" IncomingMCLType=" + cellLineType + " dbCellLineType=" +
					dbCellLineType, false);
		}
		String dbStrain = fromDB.getStrain();
		if (!strain.equals("null") && !strain.equals(dbStrain)) {
			logger.logcInfo("MCL_COMPARE: IncomingMCL=" + cellLine +
					" IncomingStrain=" + strain + " dbStrain=" + dbStrain, false);
		}
		Integer dbDerivationKey = fromDB.getDerivationKey();
		if (dbDerivationKey == null) {
			dbDerivationKey = new Integer(0);
		}
		if (!derivationKey.equals(new Integer(0)) &&
				!derivationKey.equals(dbDerivationKey)) {
			logger.logcInfo("MCL_COMPARE: IncomingMCL=" + cellLine +
					" IncomingDerivKey=" + derivationKey + " dbDerivationKey=" +
					dbDerivationKey, false);
		}
		Boolean dbIsMutant = fromDB.getIsMutant();
		if (!isMutant.equals(dbIsMutant)) {
			logger.logcInfo("MCL_COMPARE: IncomingMCL=" + cellLine +
					" IncomingIsMutant=" + isMutant + " dbIsMutant=" + dbIsMutant, false);
        }
        String dbAccID = fromDB.getAccID();
		if (!accID.equals("null") && !accID.equals(dbAccID)) {
			logger.logcInfo("MCL_COMPARE: IncomingMCL=" + cellLine +
					" IncomingAccID=" + accID + " dbAccID=" + dbAccID, false);
		}
		String dbLdbName = fromDB.getLogicalDBName();
		if (!ldbName.equals("null") && !ldbName.equals(dbLdbName)) {
			logger.logcInfo("MCL_COMPARE: IncomingMCL=" + cellLine +
					" IncomingLdbName=" + ldbName + " dbLdbName=" + dbLdbName, false);
		}
	}
}

