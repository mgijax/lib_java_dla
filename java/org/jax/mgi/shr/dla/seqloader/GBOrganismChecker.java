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
 * @is An object that, given a GenBank format sequence record determines if it
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

public class GBOrganismChecker {
    // expression string, pattern, and matcher to find the classification
    // section of a GenBank format sequence record
    // Note the ? forces searching until the FIRST instance of REFERENCE is found
    // without the ? it will search until the LAST instance
    private static final String ORG_EXPRESSION = "ORGANISM([\\s\\S]*?)REFERENCE";
    // this one works; all classifications end with a '.' - Actually it doesn't
    // because in the case of organism being 'Mus sp.' it stops and does not
    // get the full classification
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

    // The logicalDB of the DataProvider that uses GenBank format
    private String logicalDB;

    // DEBUG
    private DLALogger logger;

    /**
    * Constructs an OrganismChecker with a set of deciders
    * @assumes nothing
    * @effects nothing
    * @param None
    * @throws An exception if there are no deciders or unsupported provider
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
    * Determines if a sequence record is an organismrepresented by the set
    * of deciders
    * @assumes Nothing
    * @effects Nothing
    * @param record a sequence record
    * @return true if 'record' is an organism represented by one of
    *         the deciders.
    * @throws Nothing
    */

    public boolean checkOrganism(String record) {
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
        /*
        if (isA == false) {
            logger.logdDebug("Not a valid record: " + record, true);
        }
        */
        return isA;
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
         * @param None
         * @throws Nothing
         */

        private GBMouseDecider() {
          super ("mouse");
        }

        /**
         * Determines if 'classification' represents a mouse. Counts total
         * classifications processed and total for which the predicate is true.
         * @assumes Nothing
         * @effects Nothing
         * @param classification An organism classification string
         * @return true if this predicate is true for 'classification'
         * @throws MGIException if the sequence interrogator does not support
         *         this decider.
         */

        protected boolean is(String classification) {
          return si.isOrganism(classification, name);
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
        * @param None
        * @throws Nothing
        */

       private GBRatDecider() {
         super("rat");
       }

       /**
        * Determines if 'classification' represents a rat. Counts total
        * classifications processed and total for which the predicate is true.
        * @assumes Nothing
        * @effects Nothing
        * @param classification An organism classification string
        * @return true if this predicate is true for 'classification'
        * @throws MGIException if the sequence interrogator does not support
        *         this decider.
        */
        protected boolean is(String classification) {
          return si.isOrganism(classification, name);
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
           * @param None
           * @throws Nothing
           */

          private GBHumanDecider() {
            super("human");
          }

          /**
           * Determines if 'classification' represents a human. Counts total
           * classifications processed and total for which the predicate is true.
           * @assumes Nothing
           * @effects Nothing
           * @param classification An organism classification string
           * @return true if this predicate is true for 'classification'
           * @throws MGIException if the sequence interrogator does not support
           *         this decider.
           */
          protected boolean is(String classification) {
            return si.isOrganism(classification, name);
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

   private class GBSeqInterrogator {

       // a hash map data structure that maps organism controlled vocab
       // to a String expression. All matching is done in lower case.
       private String mouse;
       private String rat;
       private String human;

       // load HashMap with controlled vocab keys and string expression values
       private HashMap expressions = new HashMap();

       private GBSeqInterrogator() {
          mouse = "Muridae; Murinae; Mus".toLowerCase();
          rat = "Rattus".toLowerCase();
          human = "sapiens".toLowerCase();
          expressions.put("mouse", mouse);
          expressions.put("rat", rat);
          expressions.put("human", human);
        }

       /**
        * Determines whether a sequence classification if for a given organism
        * @assumes "organism" is a valid controlled vocabulary for "classification"
        * @effects Nothing
        * @param classification A GenBank sequence classification string
        * @param organism a decider name for determining expression to apply to
        *        classification
        * @return true if "classification" is for "organism"
        * @throws Nothing
        */

         private boolean isOrganism (String classification, String organism) {
            // get the string expression that is mapped to 'organism'
            //String matchString = (String)expressions.get(organism.toLowerCase());

            // return true if the string expression matches organism of 's'
            // don't create the intermediate String
            //if((classification.toLowerCase()).indexOf(matchString) >  -1) {
            if((classification.toLowerCase()).indexOf(
                  (String)expressions.get(organism.toLowerCase())) >  -1) {
                return true;
            }
            else {
               return false;
            }
         }
   }
}

//  $Log$
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
