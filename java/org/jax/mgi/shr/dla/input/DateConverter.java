//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.input;

import java.sql.Timestamp;
import java.util.*;

public class DateConverter {
    /**
     * An object that provides a static method to convert GenBank and EMBL
     * sequence record dates to a Java Timestamp.
     * @has
     *   <UL>
     *   <LI>A map to convert the month
     *   <LI>
     *   </UL>
     * @does
     *   <UL>
     *   <LI>Parses a GenBank or EMBL date string to create a Java Timestamp
     *       object. hmsf are set to 0.
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

    private static final String hmsf = " 00:00:00.000000000";
    private static String day;
    private static String month;
    private static String year;
    private static String dash = "-";
    private static HashMap gbMonthMap = new HashMap();
    static {
        gbMonthMap.put("JAN","01");
        gbMonthMap.put("FEB","02");
        gbMonthMap.put("MAR","03");
        gbMonthMap.put("APR","04");
        gbMonthMap.put("MAY","05");
        gbMonthMap.put("JUN","06");
        gbMonthMap.put("JUL","07");
        gbMonthMap.put("AUG","08");
        gbMonthMap.put("SEP","09");
        gbMonthMap.put("OCT","10");
        gbMonthMap.put("NOV","11");
        gbMonthMap.put("DEC","12");
    }

    /**
     * Converts a GenBank or EMBL date to a Java Timestamp
     * @assumes Nothing
     * @effects Nothing
     * @param date A GenBank or EMBL date
     * @return date converted to Timestamp
     * @throws Nothing
     */

    public static Timestamp convertDate(String date) {
        // converts: 29-JAN-2002
        // to: yyyy-mm-dd hh:mm:ss.fffffffff
        // to: Timestamp
        StringTokenizer st = new StringTokenizer(date, "-");
        day = st.nextToken();
        month = (String)gbMonthMap.get(st.nextToken());
        year = st.nextToken();

        return Timestamp.valueOf(year + dash + month + dash + day + hmsf);
    }
}
//  $Log$
//  Revision 1.2  2004/07/08 15:03:48  sc
//  javdocs changes
//
//  Revision 1.1  2004/01/06 20:09:35  mbw
//  initial version imported from lib_java_seqloader
//
//  Revision 1.1  2003/12/08 18:40:37  sc
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
