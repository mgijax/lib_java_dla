package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * An object that represents a denormalized ALL_Allele object
 * @has
 *   Attributes of an ALL_Allele with foreign keys denormalized
 * @does
 *   Provides getters and setters for its attributes
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class Allele {
    
    private DLALogger logger;
    // ALL_Allele attributes
    // alleleKey is null if it represents an incoming allele
    private Integer alleleKey = null;
    private Integer markerKey = null;
    private String markerSymbol = null;
    private Integer strainKey = null;
    private String strain = null;
    private Integer inheritModeKey = null;
    private String inheritMode = null;
    private Integer alleleTypeKey = null;
    private String alleleType = null;
    private Integer alleleStatusKey = null;
    private String alleleStatus = null;
    private String alleleSymbol = null;
    private String alleleName = null;
    private Boolean isWildType = null;
    private Boolean isExtinct = null;
    private Boolean isMixed = null;
    private Integer transmissionKey = null;
    private String transmission = null;

    public Allele() throws DLALoggingException {
        logger = DLALogger.getInstance();
    }
    /*
     * Setters
     */   
    public void setAlleleKey(Integer key) {
	alleleKey = key;
    }
    public void setMarkerKey(Integer key) {
	markerKey = key;
    }
    public void setMarkerSymbol(String symbol) {
	markerSymbol = symbol;
    }    
    public void setStrainKey(Integer key) {
	strainKey = key;
    }
    public void setStrainName(String name) {
       strain = name;
    }
    public void setInheritModeKey(Integer key) {
	inheritModeKey = key;
    }
    public void setInheritMode(String mode) {
	inheritMode = mode;
    }
    public void setAlleleTypeKey(Integer key) {
	alleleTypeKey = key;
    }
    public void setAlleleType(String type) {
	alleleType = type;
    }
    public void setAlleleStatusKey(Integer key) {
       alleleStatusKey = key;
    }
    public void setAlleleStatus(String status) {
       alleleStatus = status;
    }
    public void setAlleleSymbol(String symbol) {
       alleleSymbol = symbol;
    }
    public void setAlleleName(String name) {
       alleleName = name;
    }
    public void setIsWildType(Boolean b) {
       isWildType = b;
    }
    public void setIsExtinct(Boolean b) {
       isExtinct = b;
    }
    public void setIsMixed(Boolean isMixed) {
       this.isMixed = isMixed;
    }
    public void setTransmissionKey(Integer key) {
       transmissionKey = key;
    }
    public void setTransmission(String t) {
       transmission = t;
    }

    /*
    * Getters
    *
    */
    public Integer getAlleleKey() {
	return alleleKey;
    }
    public Integer getMarkerKey() {
	return markerKey;
    }
    public String getMarkerSymbol() {
	return markerSymbol;
    }
    public Integer getStrainKey() {
	return strainKey;
    }
    public String getStrainName() {
	return strain;
    }
    public Integer getInheritModeKey() {
	return inheritModeKey;
    }
    public String getInheritMode() {
	return inheritMode;
    }
    public Integer getAlleleTypeKey() {
	return alleleTypeKey;
    }
    public String getAlleleType() {
	return alleleType;
    }
    public Integer getAlleleStatusKey() {
	return alleleStatusKey;
    }
    public String getAlleleStatus() {
	return alleleStatus;
    }
    public String getAlleleSymbol() {
	return alleleSymbol;
    }
    public String getAlleleName() {
	return alleleName;
    }
    public Boolean getIsWildType() {
	return isWildType;
    }
    public Boolean getIsExtinct() {
	return isExtinct;
    }
    public Boolean getIsMixed() {
        return isMixed;
    }
    public Integer getTransmissionKey() {
        return transmissionKey;
    }
    public String getTransmission() {
        return transmission;
    }
    /**
    * @assumes all non-null database attributes are set
    */
    public ALL_AlleleState getState() {
        ALL_AlleleState state = new ALL_AlleleState();

        state.setMarkerKey(markerKey);
        state.setStrainKey(strainKey);
        state.setModeKey(inheritModeKey);
        state.setAlleleTypeKey(alleleTypeKey);
        state.setAlleleStatusKey(alleleStatusKey);
        state.setSymbol(alleleSymbol);
        state.setName(alleleName);
        state.setIsWildType(isWildType);
        state.setIsExtinct(isExtinct);
        state.setIsMixed(isMixed);
        state.setTransmissionKey(transmissionKey);
        return state;
    }

    /**
     * compares values of attributes between this Allele instance and
     * another Allele instance
     * @assumes So we may correctly label attributes when reporting the 
     *           assumption is that the current instance represents 
     *           incoming allele values and 'fromDB' represents
     *           an allele in the database.
     * @assumes Null attributes have been converted to 0 (zero) for Integers
     *          and 'null' for Strings
     * @param fromDB the allele instance, from the database, to compare to
     */
    public void compare (Allele fromDB) {
	// if incoming attribute (this) not "null", and database attribute 
	// different than incoming attribute, then report
	/* commented out per Richard 8/12/2010
	String dbMarkerSymbol = fromDB.getMarkerSymbol();
	if (markerSymbol != null && dbMarkerSymbol != null && 
	        !markerSymbol.equals(dbMarkerSymbol)) {
	    logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol + 
		" markerSymbol=" + markerSymbol + " dbMarkerSymbol=" + 
		dbMarkerSymbol, false);
	} 
	String dbStrain = fromDB.getStrainName();
	if (!strain.equals("null") && !strain.equals(dbStrain)) {
	    logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol + 
		" strain=" + strain + " dbStrain=" + 
		dbStrain, false);
	}
	String dbInheritMode = fromDB.getInheritMode();
	if (!inheritMode.equals("null") && !inheritMode.equals(dbInheritMode)) {
	    logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol + 
		" inheritMode=" + inheritMode + " dbInheritMode=" + 
		dbInheritMode, false);
	}
	String dbAlleleType = fromDB.getAlleleType();
	if (!alleleType.equals("null") && !alleleType.equals(dbAlleleType)) {
	    logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol + 
		" alleleType=" + alleleType + " dbAlleleType=" + 
		dbAlleleType, false);
	}
	String dbTransmission = fromDB.getTransmission();
        if (!transmission.equals("null") && !transmission.equals(
	        dbTransmission)) {
            logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol +
                " transmission=" + transmission + " dbTransmission=" +
                dbTransmission, false);
        }
	String dbAlleleStatus = fromDB.getAlleleStatus();
	if (!alleleStatus.equals("null") && !alleleStatus.equals(
	        dbAlleleStatus)) {
	    logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol + 
		" alleleStatus=" + alleleStatus + " dbAlleleStatus=" + 
		dbAlleleStatus, false);
	}
	
	String dbAlleleSymbol = fromDB.getAlleleSymbol();
	if (!alleleSymbol.equals("null") && !alleleSymbol.equals(
		dbAlleleSymbol)) {
	    logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol + 
		" dbAlleleSymbol=" + dbAlleleSymbol, false);
	}
	String dbAlleleName = fromDB.getAlleleName();
	if (!alleleName.equals("null") && !alleleName.equals(dbAlleleName)) {
	    logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol + 
		" alleleName=" + alleleName + " dbAlleleName=" + 
		dbAlleleName, false);
	}
	Boolean dbIsWildType = fromDB.getIsWildType();
	if (isWildType != null && dbIsWildType != null && 
		!isWildType.equals(dbIsWildType)) {
	    logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol + 
		" isWildType=" + isWildType + " dbIsWildType=" + 
		dbIsWildType, false);
	 }
	Boolean dbIsExtinct = fromDB.getIsExtinct();
	if (isExtinct != null && dbIsExtinct != null && 
		!isExtinct.equals(dbIsExtinct)) {
	    logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol + 
		" isExtinct=" + isExtinct + " dbIsExtinct=" + 
		dbIsExtinct, false);
	 }
	  Boolean dbIsMixed = fromDB.getIsMixed();
	// isMixed may be null, so check, incoming gene trap alleles will always
	// be false 
	if (isMixed != null && dbIsMixed != null && !isMixed.equals(dbIsMixed)) {
	logger.logcInfo("ALLELE_COMPARE: allSymbol=" + alleleSymbol +
	    " IncomingIsMixed=" + isMixed + " dbIsMixed=" + dbIsMixed, false);
	}
	*/
        String dbAlleleName = fromDB.getAlleleName();
        if (!alleleName.equals("null") && !alleleName.equals(dbAlleleName)) {
            logger.logcInfo("ALLELE_COMPARE: alleleSymbol=" + alleleSymbol +
                " alleleName=" + alleleName + " dbAlleleName=" +
                dbAlleleName, false);
        }

    }

    public void reset() {
        alleleKey = null;
        markerKey = null;
        markerSymbol = null;
        strainKey = null;
        strain = null;
        inheritModeKey = null;
        inheritMode = null;
        alleleTypeKey = null;
        alleleType = null;
        alleleStatusKey = null;
        alleleStatus = null;
        alleleSymbol = null;
        alleleName = null;
        isWildType = null;
        isExtinct = null;
        isMixed = null;
        transmissionKey = null;
        transmission = null;
    }
}
