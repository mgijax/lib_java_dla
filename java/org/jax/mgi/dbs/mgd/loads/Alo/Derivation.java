package org.jax.mgi.dbs.mgd.loads.Alo;

import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
 * An object that represents a denormalized ALL_CellLine_Derivation object
 * @has Attributes of an ALL_CellLine with foreign keys denormalized
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

public class Derivation {
    
    private DLALogger logger;
    
    // may be null if instance represents incoming derivation values
    private Integer derivationKey = null;
    // may be a raw name if instance represents incoming derivation values
    private String name = null; 
    private String description = null;
    private Integer vectorKey = null;
    private String vectorName =  null;
    private Integer vectorTypeKey = null;
    private String vectorType = null;
    private Integer parentCellLineKey = null;
    private String parentCellLine = null;
    private Integer parentStrainKey = null;
    private String parentStrain = null;
    private Integer derivationTypeKey = null;
    private String derivationType = null;
    private Integer creatorKey = null;
    private String creator = null;
    private Integer refsKey = null;
    private String jNum = null;

    public Derivation() throws DLALoggingException {
        logger = DLALogger.getInstance();
    }
    /**
     * set the derivation key attribute
     * @param derivationKey
     */
    public void setDerivationKey (Integer derivationKey) { 
        this.derivationKey = derivationKey; }
    
    /**
     * set the cell line library name attribute (may be null and is raw
     * value if instance represents incoming derivation as opposed to 
     * derivation in the database
     * @param name raw cell line library  name
     */
    public void setName (String name) { this.name = name; }

    /**
     * set the description attribute
     * @param description cell line library description
     */
    public void setDescription (String description) { 
        this.description = description; }

    /**
     * set the vector key attribute 
     * @param vectorKey the vector key
     */
    public void setVectorKey (Integer vectorKey) { this.vectorKey = vectorKey; }

    /**
     * set the vector name attribute corresponding to 'vectorKey'
     * @param vectorName the vector name corresponding to 'vectorKey'
     */
    public void setVectorName (String vectorName) { 
        this.vectorName = vectorName; }

    /**
     * set the vector type key attribute 
     * @param vectorTypeKey the vector key
     */
    public void setVectorTypeKey (Integer vectorTypeKey) { 
        this.vectorTypeKey = vectorTypeKey; }

    /**
     * set the vector type attribute corresponding to 'vectorTypeKey'
     * @param vectorType the vector type  corresponding to 'vectorTypeKey' 
     */
    public void setVectorType (String vectorType) {
        this.vectorType = vectorType; }
 
    /**
     * set the parent cell line key attribute 
     * @param parentCellLineKey the parent cell line key
     */
    public void setParentCellLineKey (Integer parentCellLineKey) { 
	this.parentCellLineKey = parentCellLineKey; }

    /**
     * set the parent cell line attribute corresponding to 'parentCellLineKey'
     * @param parentCellLine the parent cell line corresponding to 
     * 'parentCellLineKey'
     */
    public void setParentCellLine (String parentCellLine) {
        this.parentCellLine = parentCellLine; }
 
    /**
     * set the parent cell line strain key attribute 
     * @param parentStrainKey the parent cell line strain key
     */
    public void setParentStrainKey (Integer parentStrainKey) { 
        this.parentStrainKey = parentStrainKey; }
   
    /**
     * set the parent cell line strain attribute corresponding to 
     * 'parentStrainKey'
     * @param parentStrain the parent cell line strain corresponding to
     * 'parentStrainKey'
     */
    public void setParentStrain (String parentStrain) {
        this.parentStrain = parentStrain; }
 
    /**
     * set the derivation type key attribute 
     * @param derivationTypeKey the derivation type key
     */
    public void setDerivationTypeKey (Integer derivationTypeKey) { 
        this.derivationTypeKey = derivationTypeKey; }
   
    /**
     * set the derivation type attribute corresponding to
     * 'derivationTypeKey'
     * @param derivationType the derivation type corresponding to
     * 'derivationTypeKey'
     */
    public void setDerivationType (String derivationType) {
        this.derivationType = derivationType; }
 
    /**
     * set the derivation creator key attribute 
     * @param creatorKey the derivation creator key
     */
    public void setCreatorKey (Integer creatorKey) { 
        this.creatorKey = creatorKey; }
   
    /**
     * set the derivation creator attribute corresponding to
     * 'creatorKey'
     * @param creator the derivation creator  corresponding to
     * 'creatorKey'
     */
    public void setCreator (String creator) {
        this.creator = creator; }
 
    /**
     * set the derivation reference key attribute 
     * @param refsKey the derivation reference key
     */
    public void setRefsKey (Integer refsKey) { 
        this.refsKey = refsKey; }

    /**
     * set the derivation J-Number attribute corresponding to
     * 'refsKey'
     * @param jNum MGI JNumber corresponding to 'refsKey'
     */
    public void setJNum (String jNum) {
        this.jNum = jNum; }

    /**
     * get the derivation key (may be null)
     */
    public Integer getDerivationKey () { return derivationKey; }
    
    /**
     * get the cell line library name attribute (may be null)
     */
    public String getName () { return name; }
    
    /**
     * get the derivation description attribute
     */
    public String getDescription () { return description; }
    
    /**
     * get the vector key attribute
     */
    public Integer getVectorKey () { return vectorKey; }

    /**
     * get the vector name attribute
     */
    public String getVectorName () { return vectorName; } 

    /**
     * get the vector type key attribute
     */
    public Integer getVectorTypeKey () { return vectorTypeKey; }

    /**
     * get the vector type attribute
     */
    public String getVectorType () { return vectorType; }
    
    /**
     * get the parent cell line key attribute
     */
    public Integer getParentCellLineKey () { return parentCellLineKey; }
   
    /**
     * get the parent cell line attribute
     */
    public String getParentCellLine () { return parentCellLine; }
 
    /**
     * get the parent cell line strain key attribute
     */
    public Integer getParentStrainKey () { 
	return parentStrainKey; }
   
    /**
     * get the parent cell line strain attribute
     */
    public String getParentStrain () {
        return parentStrain; }
 
    /**
     * get the derivation type key attribute
     */
    public Integer getDerivationTypeKey () { 
	return derivationTypeKey; }
   
    /**
     * get the derivation type  attribute
     */
    public String getDerivationType () {
        return derivationType; }
 
    /**
     * get the derivation creator key attribute
     */
    public Integer getCreatorKey () { 
	return creatorKey; }
   
    /**
     * get the derivation creator attribute
     */
    public String getCreator () {
        return creator; }
 
    /**
     * get the derivation reference key attribute
     */
    public Integer getRefsKey () { 
        return refsKey; }

    /**
     * get the derivation J-Number attribute
     */
    public String getJNum () {
        return jNum; }

    /**
     * compares values of attributes between this Derivation instance and
     * another Derivation instance
     * @assumes So we may correctly label attributes when reporting the 
     *           assumption is that the current instance represents 
     *           incoming derivation values and 'fromDB' represents
     *           a derivation in the database. 
     * @assumes incoming null (this) attributes are represented as 0 (zero) 
     *       for Integers and "null" for Strings. This is because we may be
     *       missing some incoming derivation attributes.
     * @param fromDB the derivation instance, from the database, to compare to
     * @param mutCellLine name of the mutant cell line for reporting purposes
     */
    public void compare (Derivation fromDB, String mutCellLine) {
        // if incoming attribute (this) not "null", and database attribute
        // different than incoming attribute, then report
        
        /*
         * Don't compare, it may not be the same if it needs to be resolved ....
         * we only compare when we have ALREADY identified a derivation by its
         * name in the DerivationProcessor
        String dbName = fromDB.getName();
        if (dbName == null) {
            dbName = "null";
        }
        if ( !name.equals("null") && !name.equals(dbName)) {
            logger.logcInfo("DERIV_COMPARE: IncomingMutCL=" + mutCellLine +
            " DerivName=" + name + " dbDerivName=" + dbName, false);
        }*/
        String dbDescription = fromDB.getDescription();
        //System.out.println("Derivation.compare - incDescription: " + description);
        if (dbDescription == null) {
            dbDescription  = "null";
        }
        //System.out.println("Derivation.compare - dbDescription: " + dbDescription);
        if ( !description.equals("null") &&
                !description.equals(dbDescription) ) {
            logger.logcInfo("DERIV_COMPARE: IncomingMutCL=" + mutCellLine +
            " description=" + description + " dbDescription=" +
                dbDescription, false);
        }
        String dbVectorName = fromDB.getVectorName();

        if (!vectorName.equals("null") &&
        !vectorName.equals(dbVectorName) ) {
        logger.logcInfo("DERIV_COMPARE: IncomingMutCL=" + mutCellLine +
        " vectorName=" + vectorName + " dbVectorName=" +
            dbVectorName, false);
        }
        String dbVectorType = fromDB.getVectorType();
        if (!vectorType.equals("null") && !vectorType.equals(dbVectorType)) {
        logger.logcInfo("DERIV_COMPARE: IncomingMutCL=" + mutCellLine +
        " vectorType=" + vectorType + " dbVectorType=" +
            dbVectorType, false);
        }
        String dbParentCellLine = fromDB.getParentCellLine();
        if (!parentCellLine.equals("null") &&
        !parentCellLine.equals(dbParentCellLine) ) {
        logger.logcInfo("DERIV_COMPARE: mutCellLine=" + mutCellLine +
        " parentCellLine=" + parentCellLine +
            " dbParentCellLine=" + dbParentCellLine, false);
        }
        String dbParentStrain = fromDB.getParentStrain();
        if (!parentStrain.equals("null") &&
                !parentStrain.equals(dbParentStrain) ) {
            logger.logcInfo("DERIV_COMPARE: mutCellLine=" + mutCellLine +
                " parentStrain=" + parentStrain +
                    " dbParentStrain=" + dbParentStrain, false);
        }
        String dbDerivationType = fromDB.getDerivationType();
        if (!derivationType.equals("null") &&
        !derivationType.equals(dbDerivationType)) {
        logger.logcInfo("DERIV_COMPARE: mutCellLine=" + mutCellLine +
        " derivationType=" + derivationType +
            " dbDerivationType=" + dbDerivationType, false);
        }
        String dbCreator = fromDB.getCreator();
        if (dbCreator == null) {
            dbCreator = "null";
        }
        if (!creator.equals("null") && !creator.equals(dbCreator) ) {
        logger.logcInfo("DERIV_COMPARE: mutCellLine=" + mutCellLine +
        " creator=" + creator + " dbCreator=" +
            dbCreator, false);
        }
        String dbJNum = fromDB.getJNum();
        if (dbJNum == null) {
            dbJNum = "null";
        }
        if (!jNum.equals("null") && !jNum.equals(dbJNum) ) {
            logger.logcInfo("DERIV_COMPARE: mutCellLine=" + mutCellLine +
            " jNum=" + jNum + " dbJNum=" + dbJNum, false);
        }
    }
    
   /**
    * set all attributes to null
    */
    public void reset()  {
        derivationKey = null;
         name = null;
        description = null;
        vectorKey = null;
        vectorName = null;
        vectorTypeKey = null;
        vectorType = null;
        parentCellLineKey = null;
        parentCellLine = null;
        parentStrainKey = null;
        parentStrain = null;
        derivationTypeKey = null;
        derivationType = null;
        creatorKey = null;
        creator = null;
        refsKey = null;
        jNum =  null;
    }
}
