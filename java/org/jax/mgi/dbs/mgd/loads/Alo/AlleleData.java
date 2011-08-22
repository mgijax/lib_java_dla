package org.jax.mgi.dbs.mgd.loads.Alo;

/**
 * An object that represents a subset of allele attributes
 * @has
 *   Subset of attributes of an ALL_Allele 
 * @does
 *   Provides getters and setters for its attributes
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class AlleleData {
    
    private Integer mutCellLineKey = null;
    private Integer alleleKey = null;
    private Integer strainKey = null;
    private String alleleSymbol = null;

    /*
     * Setters
     */   
    public void setMCLKey(Integer key) {
	mutCellLineKey = key;
    }
    public void setAlleleKey(Integer key) {
	alleleKey = key;
    }
    public void setStrainKey(Integer key) {
	strainKey = key;
    }
    public void setAlleleSymbol(String symbol) {
       alleleSymbol = symbol;
    }

    /*
    * Getters
    *
    */

    
    public Integer getMCLKey() {
	return mutCellLineKey;
    }
    public Integer getAlleleKey() {
	return alleleKey;
    }
    public Integer getStrainKey() {
	return strainKey;
    }
    public String getAlleleSymbol() {
	return alleleSymbol;
    }
}
