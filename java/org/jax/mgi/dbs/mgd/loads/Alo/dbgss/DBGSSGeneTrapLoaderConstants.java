package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

/**
* An object that contains constant definitions DBGSS Gene Trap Loader
* @has
* <UL>
* <LI> Constant definitions for DBGSS Gene Trap Loader
* </UL>
* @company The Jackson Laboratory
* @author sc
* @version 1.0
*/

public class DBGSSGeneTrapLoaderConstants {
   
    // creator badnames
    public static final String BAYGENOMICS = "BayGenomics";
    public static final String SIGTR = 
	"Sanger Institute Gene Trap Resource - SIGTR";
    public static final String ESDB = "Hicks GG";
    public static final String CMHD = "Stanford WL";
    public static final String FHCRC = "Soriano P";
    public static final String TIGEM = "TIGEM";
    public static final String EGTC = "Exchangeable Gene Trap Clones";
    public static final String LEXICON = "Zambrowicz BP";
    public static final String GGTC = "GGTC";
    public static final String TIGM = 
	"Richard H. Finnell at Texas Institute for Genomic Medicine";
    public static final String EUCOMM =
            "European Conditional Mouse Mutagenesis Program (EUCOMM)";
    public static final String RULEY = "Ruley HE";
    public static final String WURST = "Wurst W";
    public static final String ISHIDA = "Ishida Y";
    public static final String ISHIDA_2 = "Kaoru Fukami-Kobayashi RIKEN,";

    // reverse complement values
    public static final String REVERSE_COMP_TRUE = "yes";
    public static final String REVERSE_COMP_FALSE = "no";
    public static final String REVERSE_COMP_NS = "Not Specified";
    public static final String REVERSE_COMP_NA = "Not Applicable";
    
    // seqTagId association to sequence logical db names
    public static final String IGTC_SEQ_LDB = "IGTC";
    public static final String LEXICON_SEQ_LDB = "Lexicon Genetics";
    public static final String TIGM_SEQ_LDB = "TIGM";
    public static final String EUCOMM_SEQ_LDB = "EUCOMM-GTtag";
    public static final String RULEY_SEQ_LDB = "Ruley HE";
    public static final String ISHIDA_SEQ_LDB = "Ishida Y";

    // cell line ID association to cell line logical db names
    public static final String TIGM_CL_LDB = "TIGM Cell Line";
    public static final String LEXICON_CL_LDB = "Lexicon";
    public static final String BAYGENOMICS_LDB = "BayGenomics";
    public static final String SIGTR_LDB = "SIGTR";
    public static final String ESDB_LDB = "ESDB";
    public static final String CMHD_LDB = "CMHD";
    public static final String FHCRC_LDB = "FHCRC";
    public static final String TIGEM_LDB = "TIGEM";
    public static final String EGTC_LDB = "EGTC";
    public static final String GGTC_LDB = "GGTC";
    public static final String EUCOMM_CL_LDB = "EUCOMM-GTcellline";
    public static final String RULEY_CL_LDB = "Ruley HE Cell Line";
    public static final String ISHIDA_CL_LDB = "Ishida Y Cell Line";

    // sequence tag method bad names in lower case (i.e. how they are in dbGSS)
    public static final String INVERSEPCR = "inverse pcr";
    public static final String PLASMRESCUE = "plasmid rescue";
    public static final String SPLINK5 = "5'splinkerette pcr";
    public static final String SPLINK3 = "3'splinkerette pcr";
    public static final String SPLINK5_EUCOMM = "5spk";
    public static final String SPLINK3_EUCOMM = "3spk";
    public static final String RACE5_1 = "5' race";
    public static final String RACE3_1 = "3' race";
    public static final String RACE5_2 = "5'race";
    public static final String RACE3_2 = "3'race";
    public static final String ADAPTORPCR = "adaptor-mediated pcr";
    public static final String LIGATIONPCR = "ligation-mediated pcr";
    
    // vector end
    public static String UPSTREAM = "upstream";
    public static String DOWNSTREAM = "downstream";
    public static String NOT_SPECIFIED = "Not Specified";
    public static String NOT_APPLICABLE = "Not Applicable";

    // strand
    public static String STRAND_PLUS = "+";
    public static String STRAND_MINUS = "-";

}
