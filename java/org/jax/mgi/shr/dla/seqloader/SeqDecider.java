//  $Header$
//  $Name$

package org.jax.mgi.shr.dla.seqloader;

/**
 * @is an abstract class that expects its subclasses to implement the protected
 *   is method to apply a predicate to a String. Keeps counter of total records
 *   for which the predicate is true.
 * @has
 *   <UL>
 *   <LI>a name
 *   <LI>counter for total records for which the predicate is true
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Calls the subclass isValid method to determines if the predicate is true
 *   <LI>maintains and provides access to the counter
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public abstract class SeqDecider {

    // number of records for which the predicate is true
    private int trueCtr = 0;

    // This decider's name
    protected String name;

    /**
     * Constructs a SeqDecider object initializing its name
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @throws Nothing
     */

    public SeqDecider(String name)
    {
        this.name = name;
    }

    /**
     * Decides if a predicate is true for 's'. Increments counter
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return boolean true or false
     * @throws Nothing
     * this decider
     */

    public boolean isA(String s) {
        boolean answer = is(s);
        if(answer == true) {
             trueCtr++;
        }
        return answer;
    }


    /**
     * gets counter of records for which the predicate is true
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return trueCtr number of Strings for which the predicate is true
     * @throws Nothing
     */

     public int getTrueCtr()
    {
        return trueCtr;
    }

    /**
     * get the name of this decider
     * @assumes Nothing
     * @effects Nothing
     * @param None
     * @return name name of this decider
     * @throws Nothing
     */

    public String getName( )
    {
        return name;
    }

    protected abstract boolean is(String classification);
}

//  $Log$
//  Revision 1.1  2004/01/06 20:09:39  mbw
//  initial version imported from lib_java_seqloader
//
//  Revision 1.2  2003/12/20 16:25:19  sc
//  changes made from code review~
//
//  Revision 1.1  2003/12/08 18:40:40  sc
//  initial commit
//
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
