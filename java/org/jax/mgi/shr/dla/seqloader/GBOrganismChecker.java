//  $Header
//  $Name

package org.jax.mgi.shr.dla.seqloader;

import java.util.*;
import java.util.regex.*;

import org.jax.mgi.shr.config.OrganismCheckerCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.exception.MGIException;

// DEBUG
import org.jax.mgi.shr.dla.DLALogger;
import org.jax.mgi.shr.dla.DLALoggingException;
import org.jax.mgi.shr.timing.Stopwatch;

/**
 * An object that, given a GenBank format sequence record determines if it
 *     is an organism we are interested in. It uses deciders that represent
 *     a configurable set of organisms.  e.g. Given three deciders, mouse, human,
 *     and rat, determines if the sequence record is a mouse, or a human, or rat.
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

public class GBOrganismChecker implements OrganismChecker {
    // expression string, pattern, and matcher to find the classification
    // section of a GenBank format sequence record
    // Note the ? forces searching until the FIRST instance of REFERENCE is found
    // without the ? it will search until the LAST instance
    private static final String ORG_EXPRESSION = "ORGANISM([\\s\\S]*?)REFERENCE";

    // this one doesn't work because in the case of organism being 'Mus sp.'
    // it stops at 'Mus sp.' does not get the full classification
    //private static final String EXPRESSION = "ORGANISM([^.]+).*";

    private Pattern orgPattern;
    private Matcher orgMatcher;

    // not all RefSeqs have a REFERENCE section, but they do have a FEATURES
    // section
    private static final String ORG_ALT_EXPRESSION = "ORGANISM([\\s\\S]*?)FEATURES";
    private Pattern orgAltPattern;
    private Matcher orgAltMatcher;

    // true if any decider returns true
    private boolean isA;

    // count of total records looked at
    private int totalCtr = 0;

    // count of records for which GBOrganismChecker.checkOrganism returns true
    private int trueCtr = 0;

    // the set of organism deciders to query
    private Vector deciders;

    // Configurator to determine organisms to check
    private OrganismCheckerCfg config;

    // returns true if a given classification is for a given species
    private GBSeqInterrogator si;

    // The logicalDB name of the DataProvider that uses GenBank format
    private String logicalDB;

    // DEBUG
    private DLALogger logger;
    Stopwatch stopWatch = new Stopwatch();
    Runtime runTime = Runtime.getRuntime();

    /**
    * Constructs an OrganismChecker with a set of deciders
    * @assumes nothing
    * @effects nothing
    * @throws ConfigException if config file does not define mouse human and rat
    * decider vars
    */

    public GBOrganismChecker () throws ConfigException, DLALoggingException {
        // create a configurator
        config = new OrganismCheckerCfg();

        // create an interrogator to determine a record's organism
        si = new GBSeqInterrogator();

        // Get the deciders from configuration
        deciders = new Vector();
        if (config.getMouse().equals(Boolean.TRUE)) {
          deciders.add(new GBMouseDecider());
        }
        if (config.getRat().equals(Boolean.TRUE)) {
          deciders.add(new GBRatDecider());
        }
        if (config.getHuman().equals(Boolean.TRUE)) {
          deciders.add(new GBHumanDecider());
        }

        // compile expression to find the classification section of a record
        orgPattern = Pattern.compile(ORG_EXPRESSION, Pattern.MULTILINE);
        orgAltPattern = Pattern.compile(ORG_ALT_EXPRESSION, Pattern.MULTILINE);
        logger = DLALogger.getInstance();
    }

    /**
    * Determines if a sequence record is an organism represented by the set
    * of deciders
    * @assumes Nothing
    * @effects Nothing
    * @param record a sequence record
    * @return true if 'record' is an organism represented by one of
    *         the deciders.
    * @throws Nothing
    */

    public boolean checkOrganism(String record) {
        //DEBUG
        stopWatch.reset();
        stopWatch.start();

        totalCtr++;
        // reset
        isA = false;

        // find the classification section of this record
        orgMatcher = orgPattern.matcher(record);
        orgAltMatcher = orgAltPattern.matcher(record);
        Iterator i = deciders.iterator();

        if (orgMatcher.find() == true) {
            // Determine if we are interested in this sequence
            while (i.hasNext()) {
                SeqDecider currentDecider = (SeqDecider)i.next();
                // m.group(1) is the classification
                if(currentDecider.isA(orgMatcher.group(1))) {
                    trueCtr++;
                    isA = true;
                    break;
                }
            }
        }

	// if we don't find the classification, try a different Matcher - this
	// may be a refseq record
        else if (orgAltMatcher.find() == true) {
            // Determine if we are interested in this sequence
            while (i.hasNext()) {
                SeqDecider currentDecider = (SeqDecider)i.next();
                // m.group(1) is the classification
                if(currentDecider.isA(orgAltMatcher.group(1))) {
                    trueCtr++;
                    isA = true;
                    break;
                }
            }
        }

        if (isA == false) {
            logger.logdDebug("Not a valid record", true);
        }

	// DEBUG
        stopWatch.stop();
        logger.logdDebug("checkOrganism time: " + stopWatch.time());
        logger.logdDebug("Free memory: " + runTime.freeMemory());

        return isA;
      }

    /**
    * Gets the total records looked at, the total records for which checkOrganism
    *  returned true and the count of records for which each decider returned true.
    * @assumes Nothing
    * @effects Nothing
    * @return Vector of Strings, each String contains the decider name
    *         and the count of records for which the decider returned true
    */
    public Vector getDeciderCounts () {
      Vector v = new Vector();
      v.add("Total Sequences looked at: " + totalCtr + SeqloaderConstants.CRT);

      // get the set of organisms we are loading
      StringBuffer organisms = new StringBuffer();
      Iterator i = deciders.iterator();
      while (i.hasNext()) {
          organisms.append( ((SeqDecider)i.next()).getName() + ", ");
      }
      // StringBuffer.substring removes the trailing ', '
      v.add("Total records for organism(s) " +
            organisms.toString().substring(0,organisms.length()-1) + "  found: " +
            trueCtr + SeqloaderConstants.CRT);
      i = deciders.iterator();
            while (i.hasNext()) {
              SeqDecider d = (SeqDecider)i.next();
              String s = "    Total " + d.getName() + ": " +
                  d.getTrueCtr() + SeqloaderConstants.CRT;
              v.add(s);
            }
       return v;
    }

    /**
     * @is an object that applies this predicate to the classification section
     * of a GenBank sequence record
     * "Does this classification string represent a mouse?"
     * @has a name, see also superclass
     * @does Determines if a classification string represents a mouse
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

      private class GBMouseDecider extends SeqDecider {

        /**
         * Constructs a GBMouseDecider object with the name "mouse" which
         * is controlled vocabulary used by the GBSeqInterrogator
         * @assumes Nothing
         * @effects Nothing
         */

        private GBMouseDecider() {
          super ("mouse");
        }

        /**
         * Determines if 'classification' represents a mouse. Matching is done
         * in lower case. Counts total
         * classifications processed and total for which the predicate is true.
         * @assumes Nothing
         * @effects Nothing
         * @param classification An organism classification string
         * @return true if this predicate is true for 'classification'
         * @throws MGIException if the sequence interrogator does not support
         *         this decider.
         */

        protected boolean is(String classification) {
          return si.isMatch(classification.toLowerCase(), name);
       }
     }
     /**
      * @is an object that applies this predicate to the classification section
      * of a GenBank sequence record
      * "Does this classification string represent a rat?"
      * @has A name, See also superclass
      * @does Returns true if a classification string represents a rat
      * @company The Jackson Laboratory
      * @author sc
      * @version 1.0
      */

     private class GBRatDecider extends SeqDecider {

       /**
        * Constructs a GBRatDecider object with the name "rat" which
         * is controlled vocabulary used by the GBSeqInterrogator
        * @assumes Nothing
        * @effects Nothing

        */

       private GBRatDecider() {
         super("rat");
       }

       /**
        * Determines if 'classification' represents a rat. Matching is done
        * in lower case. Counts total
        * classifications processed and total for which the predicate is true.
        * @assumes Nothing
        * @effects Nothing
        * @param classification An organism classification string
        * @return true if this predicate is true for 'classification'
        * @throws MGIException if the sequence interrogator does not support
        *         this decider.
        */
        protected boolean is(String classification) {
          return si.isMatch(classification.toLowerCase(), name);
        }
      }
      /**
       * @is an object that applies this predicate to the classification section
       * of a GenBank sequence record
       * "Does this classification string represent a human?"
       * @has A name, See also superclass
       * @does Returns true if a classification string represents a human
       * @company The Jackson Laboratory
       * @author sc
       * @version 1.0
       */

      private class GBHumanDecider extends SeqDecider {

          /**
           * Constructs a GBHumanDecider object with the name "human" which
           * is controlled vocabulary used by the GBSeqInterrogator
           * @assumes Nothing
           * @effects Nothing

           */

          private GBHumanDecider() {
            super("human");
          }

          /**
           * Determines if 'classification' represents a human. Matching is done
           * in lower case. Counts total
           * classifications processed and total for which the predicate is true.
           * @assumes Nothing
           * @effects Nothing
           * @param classification An organism classification string
           * @return true if this predicate is true for 'classification'
           * @throws MGIException if the sequence interrogator does not support
           *         this decider.
           */
          protected boolean is(String classification) {
            return si.isMatch(classification.toLowerCase(), name);
          }
        }

     /**
        * @is an object that queries a classification string to determine if
        *     it is for a given organism
        * @has mapping of controlled vocabulary
        *       to string expressions e.g. "mouse" : "Muridae; Murinae; Mus"
        * @does Given a classification string, <BR>e.g.
        *       " Eukaryota; Metazoa; Chordata; Craniata; Vertebrata; Euteleostomi;
                  Mammalia; Eutheria; Rodentia; Sciurognathi; Muridae;
                  Murinae; Mus." <BR>
                and a controlled vocabulary string, e.g. "mouse",
                  determine if the classification is for a mouse.
        * @company The Jackson Laboratory
        * @author sc
        * @version 1.0
        */

   private class GBSeqInterrogator extends Interrogator {

       // expressions, matching to be done in lower case
       private String mouse = "Muridae; Murinae; Mus".toLowerCase();
       private String rat = "Rattus".toLowerCase();
       private String human  = "sapiens".toLowerCase();

       /**
        * Constructs a GBSeqInterrogator by loading a mapping of organism
        * controlled vocab keys to GB organism expressions
        * @assumes Nothing
        * @effects Nothing
        */
       private GBSeqInterrogator() {
          loadExpressions();
        }

        /**
        * loads the hashmap with organism controlled vocab keys and
        * organism expression values
        * @assumes Nothing
        * @effects Nothing
        */
        protected void loadExpressions() {
            expressions.put("mouse", mouse);
            expressions.put("rat", rat);
            expressions.put("human", human);
        }
   }
}

//  $Log$
//  Revision 1.5  2004/06/30 19:28:46  mbw
//  javadocs only
//
//  Revision 1.4  2004/06/30 17:25:35  sc
//  merging sc2 branch to trunk
//
//  Revision 1.2.4.2  2004/06/30 13:04:47  sc
//  now implements OrganismChecker interface, changed some statistic reporting.
//
//  Revision 1.2.4.1  2004/05/18 15:07:24  sc
//  class/method headers updated. inner class GBSeqInterrogator now extends Interrogator
//  Revision 1.3  2004/05/17 13:37:13  sc
//  commented out some debug code

//  Revision 1.2  2004/03/12 14:13:22  sc
//  HISTORY
//
//  Revision 1.1  2004/02/27 14:32:36  sc
//  initial commit having been moved from gbseqload
//
//  Revision 1.4  2004/02/19 20:01:50  sc
//  Added debug logging for sequence which do not pass the isValid test
//
//  Revision 1.3  2004/02/11 15:47:16  sc
//  changed reg expression, added DEBUG logging, removed creation of a string
//
//  Revision 1.2  2003/12/20 16:31:59  sc
//  Changed from code review
//
//  Revision 1.1  2003/12/17 18:25:40  sc
//  initial commit
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
**************************************************************************/
