//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

/**
* @is An object that contains constant definitions for sequence loaders.
* @has
*   <UL>
*   <LI> Constant definitions for database field values and parsing
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
    public static final String SPC = " ";
    public static final String SLASH = "/";
    public static final String EQUAL = "=";
    public static final String OPEN_PAREN = "(";
    public static final String COMMA = ",";
    public static final String SEMI_COLON = ";";

    // Yes, THREE escapes! your not seeing things - isn't Java fun
    public static final String DBL_QUOTE = "\\\"";
    public static final String EMPTY_STRING = "";

    // misc constants
    public static final String DUMMY_SEQ_STATUS = "Not Loaded";
    public static final String NOT_APPLICABLE = "Not Applicable";

    // load mode constants
    public static final String INCREM_LOAD_MODE = "incremental";
    public static final String INCREM_INITIAL_LOAD_MODE = "incremental_initial";
    public static final String DELETE_RELOAD_MODE = "delete_reload";

    // Event constants
    public static final int ADD = 1;
    public static final int UPDATE = 2;
    public static final int ALREADY_ADDED = 3;
    public static final int NON_EVENT = 4;
    public static final int MERGE = 6;
    public static final int SPLIT = 7;
    public static final int DUMMY = 8;

    // Sequence quality VOC_Vocab term
    public static final String HIGH_QUAL = "High";
    public static final String MED_QUAL = "Medium";
    public static final String LOW_QUAL = "Low";


}
//  $Log$
//  Revision 1.4  2004/02/27 14:35:23  sc
//  aded VOV_Vocab controlled vocab for sequence quality
//
//  Revision 1.3  2004/02/02 19:45:15  sc
//  development since last tag
//
//  Revision 1.2  2004/01/12 20:14:27  sc
//  ongoing development
//
//  Revision 1.1  2004/01/06 20:09:43  mbw
//  initial version imported from lib_java_seqloader
//
//  Revision 1.2  2003/12/20 16:25:20  sc
//  changes made from code review~
//
//  Revision 1.1  2003/12/08 18:40:41  sc
//  initial commit
//

/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/