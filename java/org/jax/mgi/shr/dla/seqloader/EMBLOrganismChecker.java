//  $Header
//  $Name

package org.jax.mgi.shr.dla.seqloader;

import java.util.*;
import java.util.regex.*;

import org.jax.mgi.shr.dla.seqloader.SeqDecider;
import org.jax.mgi.shr.dla.seqloader.SeqloaderConstants;
import org.jax.mgi.shr.config.OrganismCheckerCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dla.DLALogger;
import org.jax.mgi.shr.dla.DLAException;
import org.jax.mgi.shr.dla.DLAExceptionHandler;

// DEBUG
import org.jax.mgi.shr.dla.DLALoggingException;

/**
 * @is An object that determines if a EMBL sequence record is an organism represented
 *      by a configurable set of organisms represented by deciders. e.g.
 *     Given a set of deciders, mouse, human, and rat, returns true if the sequence
 *      record is for an organism in that set.<BR>
 *     Also provides methods to query a string to see if it is a specific organism.
 * @has
 *   <UL>
 *   <LI>A sequence record
 *   <LI>A set of SeqDeciders created by querying a configurator;
 *       each a predicate to identify a given organism
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Finds the classification section of a sequence record
 *   <LI>Determines the set of organisms from an OrganismCheckerCfg configurator
 *   <LI>Queries each decider; Determines if the classification is for an
 *       organism represented by that decider
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class EMBLOrganismChecker {
    // expression string, pattern, and matcher to find the OS
    // section of a EMBL format sequence record
    // Note the ? forces searching until the FIRST instance of OC is found
    // without the ? it will search until the LAST instance
    private static final String ORG_EXPRESSION = "OS([\\s\\S]*?)OC";
    private Pattern orgPattern;
    private Matcher orgMatcher;

    // true if any decider returns true
    private boolean isA;

    // current count of total records looked at
    private int totalCtr = 0;

    // current number of records for which EMBLOrganismChecker.checkOrganism returns true
    private int trueCtr = 0;

    // the set of organism deciders to query
    private Vector deciders;

    // Configurator to determine organisms to check
    private OrganismCheckerCfg config;

    // returns true if a given classification is for a given species
    private EMBLSeqInterrogator si;

    // create a decider for each organism supported by this class
    private EMBLMouseDecider mouseDecider = new EMBLMouseDecider();
    private EMBLHumanDecider humanDecider = new EMBLHumanDecider();
    private EMBLRatDecider ratDecider = new EMBLRatDecider();

    // The logicalDB of the DataProvider that uses EMBL format
    private String logicalDB;

    // DEBUG
    private DLALogger logger;

    /**
    * Constructs an EMBLOrganismChecker with a set of deciders
    * @assumes nothing
    * @effects nothing
    * @param None
    * @throws An exception if there are no deciders or unsupported provider
    */

    public EMBLOrganismChecker () throws ConfigException, DLALoggingException {
        // create a configurator
        config = new OrganismCheckerCfg();

        // create an interrogator to determine a record's organism
        si = new EMBLSeqInterrogator();

        // Get the deciders from configuration
        deciders = new Vector();
        if (config.getMouse().equals(Boolean.TRUE)) {
          deciders.add(mouseDecider);
        }
        if (config.getRat().equals(Boolean.TRUE)) {
          deciders.add(ratDecider);
        }
        if (config.getHuman().equals(Boolean.TRUE)) {
          deciders.add(humanDecider);
        }

        // compile expression to find the OS section of a record
        orgPattern = Pattern.compile(ORG_EXPRESSION, Pattern.MULTILINE);
        logger = DLALogger.getInstance();
    }

    /**
    * Determines if a sequence record is an organism represented by the set
    * of deciders
    * @assumes Nothing
    * @effects Nothing
    * @param record a EMBL format sequence record
    * @return true if 'record' is an organism represented by one of
    *         the deciders.
    * @throws Nothing
    */

    public boolean checkOrganism(String record) {
        // increment the total record looked at
        totalCtr++;

        // reset
        isA = false;

        // find the OS section of this record
        orgMatcher = orgPattern.matcher(record);
        Iterator i = deciders.iterator();

        // if we've found the OS section -
        if (orgMatcher.find() == true) {
            // Determine if we are interested in this sequence
            while (i.hasNext()) {
                SeqDecider currentDecider = (SeqDecider)i.next();
                // m.group(1) is the classification
                if(currentDecider.isA(orgMatcher.group(1))) {
                    trueCtr++;
                    isA = true;
                }
            }
        }
        /*
        if (isA == false) {
            logger.logdDebug("Not a valid record: " + record, true);
        }
        */
        return isA;
      }

      /**
       * returns true if 'OSString' is for a mouse
       * @assumes Nothing
       * @effects Nothing
       * @param organism - an OS line from an EMBL format sequence record
       * @return true if OSString is for a mouse
       * @throws Nothing
       */

      public boolean isMouse(String OSString) {
        return mouseDecider.is(OSString);
      }

      /**
       * returns true if 'OSString' is for a rat
       * @assumes Nothing
       * @effects Nothing
       * @param organism - an OS line from an EMBL format sequence record
       * @return true if OSString is for a rat
       * @throws Nothing
       */

      public boolean isRat(String OSString) {
        return ratDecider.is(OSString);
      }

      /**
       * returns true if 'OSString' is for a human
       * @assumes Nothing
       * @effects Nothing
       * @param organism - an OS line from an EMBL format sequence record
       * @return true if OSString is for a human
       * @throws Nothing
       */

      public boolean isHuman(String OSString) {
        return humanDecider.is(OSString);
      }

      public boolean isHumanMouseOrRat(String OSString) {
        return (mouseDecider.is(OSString) ||
                ratDecider.is(OSString) ||
                    humanDecider.is(OSString));
      }
    /**
    * Gets the total records looked at, the total records for which checkOrganism
    *  returned true and the count of records for which each decider returned true.
    * @assumes Nothing
    * @effects Nothing
    * @param None
    * @return Vector of Strings, each String contains the decider name
    *         and the count of records for which the decider returned true
    * @throws Nothing
    */
    public Vector getDeciderCounts () {
      Vector v = new Vector();
      v.add("Total records looked at: " + totalCtr + SeqloaderConstants.CRT);
      v.add("Total records processed: " + trueCtr + SeqloaderConstants.CRT);
      Iterator i = deciders.iterator();
            while (i.hasNext()) {
              SeqDecider d = (SeqDecider)i.next();
              String s = "Total " + d.getName() + " records processed: " +
                  d.getTrueCtr() + SeqloaderConstants.CRT;
              v.add(s);
            }
            return v;
    }

    /**
     * @is an object that applies this predicate to the OS section
     * of a EMBL format sequence record
     * "Does this OS section represent a mouse?"
     * @has A name, See also superclass
     * @does Returns true if a OS section represents a mouse
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

      private class EMBLMouseDecider extends SeqDecider {

        /**
         * Constructs a EMBLMouseDecider object with the name "mouse" which
         * is controlled vocabulary used by the EMBLSeqInterrogator
         * @assumes Nothing
         * @effects Nothing
         * @param None
         * @throws Nothing
         */

        private EMBLMouseDecider() {
          super ("mouse");
        }

        /**
         * Determines if 'OSString' represents a mouse. Matching is done
         * in lower case. Counts total
         * OSStrings processed and total for which the predicate is true.
         * @assumes Nothing
         * @effects Nothing
         * @param OSString A EMBL format OS section string
         * @return true if this predicate is true for 'OSString'
         * @throws MGIException if the sequence interrogator does not support
         *         this decider.
         */

        protected boolean is(String OSString) {
          return si.isMatch(OSString.toLowerCase(), name);
       }
     }
     /**
     * @is an object that applies this predicate to the OS section
     * of a EMBL format sequence record
     * "Does this OS section represent a rat?"
     * @has A name, See also superclass
     * @does Returns true if a OS section represents a rat
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */


     private class EMBLRatDecider extends SeqDecider {

       /**
        * Constructs a EMBLRatDecider object with the name "rat" which
         * is controlled vocabulary used by the EMBLSeqInterrogator
        * @assumes Nothing
        * @effects Nothing
        * @param None
        * @throws Nothing
        */

       private EMBLRatDecider() {
         super("rat");
       }

       /**
        * Determines if 'OSString' represents a rat. Matching is done
        * in lower case. Counts total
        * OSStrings processed and total for which the predicate is true.
        * @assumes Nothing
        * @effects Nothing
        * @param OSString A EMBL format OS section string
        * @return true if this predicate is true for 'OSString'
        * @throws MGIException if the sequence interrogator does not support
        *         this decider.
        */

        protected boolean is(String OSString) {
          return si.isMatch(OSString.toLowerCase(), name);
        }
      }

      /**
       * @is an object that applies this predicate to the OS section
       * of a EMBL format sequence record
       * "Does this OS section represent a human?"
       * @has A name, See also superclass
       * @does Returns true if a OS section represents a human
       * @company The Jackson Laboratory
       * @author sc
       * @version 1.0
       */

      private class EMBLHumanDecider extends SeqDecider {

          /**
           * Constructs a EMBLHumanDecider object with the name "human" which
           * is controlled vocabulary used by the EMBLSeqInterrogator
           * @assumes Nothing
           * @effects Nothing
           * @param None
           * @throws Nothing
           */

          private EMBLHumanDecider() {
            super("human");
          }

          /**
           * Determines if 'OSString' represents a human. Matching is done
           * in lower case. Counts total
           * OSStrings processed and total for which the predicate is true.
           * @assumes Nothing
           * @effects Nothing
           * @param OSString A EMBL format OS section string
           * @return true if this predicate is true for 'OSString'
           * @throws MGIException if the sequence interrogator does not support
           *         this decider.
           */
          protected boolean is(String OSString) {
            return si.isMatch(OSString.toLowerCase(), name);
          }

        }

     /**
        * @is an object that queries the OS lines of a EMBL format record
        * @has mapping of controlled vocabulary
        *       to string expressions e.g. "mouse" : "Mus musculus"
        * @does Given a set of OS lines, <BR>e.g. OS   Mus musculus (Mouse),
        * OS   Rattus norvegicus (Rat), and OS   Bos taurus (Bovine). <BR>
                and a controlled vocabulary string, e.g. "mouse",
                  determine if record is for a mouse.
        * @company The Jackson Laboratory
        * @author sc
        * @version 1.0
        */

   private class EMBLSeqInterrogator extends Interrogator {

       // expressions, matching to be done in lower case
       private String mouse = "Mus musculus".toLowerCase();
       private String rat = "Rattus".toLowerCase();
       private String human = "sapiens".toLowerCase();

       /**
        * Constructs a EMBLSeqInterrogator by loading a mapping of controlled
        * organism vocab keys to EMBL organism expressions
        * @assumes Nothing
        * @effects Nothing
        * @param None
        * @throws Nothing
        */
       private EMBLSeqInterrogator() {
           loadExpressions();
       }

        /**
        * loads the hashmap with organism controlled vocab keys and
        * organism expression values
        * @assumes Nothing
        * @effects Nothing
        * @param None
        * @return Nothing
        * @throws Nothing
        */
        protected void loadExpressions() {
            expressions.put("mouse", mouse);
            expressions.put("rat", rat);
            expressions.put("human", human);
        }
   }
}

//  $Log
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
**************************************************************************/
