package org.jax.mgi.shr.dla.loader.seq.genetrap;

/**
* An object that contains constant definitions for gene traps
* @has
*   <UL>
*   <LI> Constant definitions for:
*   <UL>
*       <LI> Gene trap providers
*   </UL>
*   </UL>
* @does Nothing
* @company The Jackson Laboratory
* @author sc
* @version 1.0
*/

public class GeneTrapConstants {

    // creator constants
    public static final String BAYGENOMICS = "BayGenomics";
    public static final String CMHD = "CMHD";
    public static final String EGTC = "EGTC";
    public static final String ESDB = "ESDB";
    public static final String FHCRC = "FHCRC";
    public static final String GGTC = "GGTC";
    public static final String LEXICON = "Lexicon";
    public static final String SIGTR = "SIGTR";
    public static final String TIGEM = "TIGEM";
    public static final String TIGM = "TIGM";

    // sequence tag method constants
    public static final String INVERSEPCR = "inverse pcr";
    public static final String PLASMRESCUE = "plasmid rescue";
    public static final String SPLINK5 = "5'splinkerette pcr";
    public static final String SPLINK3 = "3'splinkerette pcr";
    
    // vector end 
    public static String UPSTREAM = "F";
    public static String DOWNSTREAM = "R";
    public static String NOT_APPLICABLE = "Not Applicable";

    // strand
    public static String STRAND_PLUS = "+";
    public static String STRAND_MINUS = "-";
}
