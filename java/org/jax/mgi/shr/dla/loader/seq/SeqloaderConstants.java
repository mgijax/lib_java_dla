package org.jax.mgi.shr.dla.loader.seq;

/**
* An object that contains constant definitions for sequence loaders.
* @has
*   <UL>
*   <LI> Constant definitions for:
*   <UL>
*       <LI> characters, e.g. tab
*       <LI> lode modes
 *      <LI> events
 *      <LI> sequence quality and status vocabularies
*   </UL>
*   </UL>
* @does Nothing
* @company The Jackson Laboratory
* @author sc
* @version 1.0
*/

public class SeqloaderConstants {

    // character Constants
    public static final String PERIOD = ".";
    public static final String CRT = "\n";
    public static final String TAB = "\t";
    public static final String SPC = " ";
    public static final String SLASH = "/";
    public static final String EQUAL = "=";
    public static final String OPEN_PAREN = "(";
    public static final String COMMA = ",";
    public static final String SEMI_COLON = ";";

    // Yes, THREE escapes! your not seeing things - isn't Java fun
    public static final String DBL_QUOTE = "\\\"";
    public static final String SGL_QUOTE = "'";
    public static final String EMPTY_STRING = "";

    // misc constants
    public static final String NOT_APPLICABLE = "Not Applicable";

    // Name organism of non-mouse molecular source
    public static final String OTHER = "Other";

    // load mode constants
    public static final String INCREM_LOAD_MODE = "incremental";
    public static final String INCREM_INITIAL_LOAD_MODE = "incremental_initial";
    public static final String DELETE_RELOAD_MODE = "delete_reload";

    // Event constants
    public static final int ADD = 1;
    public static final int UPDATE = 2;
    // No longer an event
    //public static final int ALREADY_ADDED = 3;
    public static final int NON_EVENT = 4;
    public static final int MERGE = 6;
    public static final int SPLIT = 7;
    public static final int DUMMY = 8;

    // GenBank Third Party Annotation (TPA) KEYWORD String
    public static final String TPA = "Third Party Annotation";

    // Sequence quality VOC_Vocab term
    public static final String HIGH_QUAL = "High";
    public static final String MED_QUAL = "Medium";
    public static final String LOW_QUAL = "Low";

    // Sequence type VOC_Vocab terms
    public static final String DNA = "DNA";

    // Sequence status constants
    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String DUMMY_SEQ_STATUS = "Not Loaded";
    public static final String SPLIT_STATUS  = "SPLIT";
    public static final String DELETE_STATUS = "DELETED";

    // QC_SEQ_SourceConflict attrName values
    public static final String LIBRARY = "library";
    public static final String ORGANISM = "organism";

}
