//  $Header$
//  $Name$

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
//  $Log$
//  Revision 1.2.10.1  2005/03/02 21:04:44  sc
//  added two new seqeunce statuses
//
//  Revision 1.2  2004/12/07 20:10:46  mbw
//  merged tr6047 onto the trunk
//
//  Revision 1.1.2.1  2004/11/05 16:06:44  mbw
//  classes were renamed and reloacated as part of large refactoring effort (see tr6047)
//
//  Revision 1.14  2004/10/21 12:45:41  sc
//  commented out ALREADY_ADDED
//
//  Revision 1.13  2004/07/19 18:03:38  sc
//  added DNA constant
//
//  Revision 1.12  2004/07/08 15:03:49  sc
//  javdocs changes
//
//  Revision 1.11  2004/06/30 17:25:36  sc
//  merging sc2 branch to trunk
//
//  Revision 1.10.4.1  2004/05/18 15:32:47  sc
//  updated class/method headers
//
//  Revision 1.10  2004/04/26 12:11:16  sc
//  added TAB
//
//  Revision 1.9  2004/04/15 14:13:01  sc
//  added ORGANISM and LIBRARY as controlled vocab for QC_SEQ_RawSourceConflict.attrName
//
//  Revision 1.8  2004/04/13 15:19:30  sc
//  added ACTIVE_STATUS
//
//  Revision 1.7  2004/03/31 18:39:50  sc
//  added OTHER - translatable value for organism - Other
//
//  Revision 1.6  2004/03/29 13:47:28  sc
//  added SGL_QUOTE constant
//
//  Revision 1.5  2004/03/24 18:33:30  sc
//  added constants for open paren, comma,semi-colon, and Not Applicable
//
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