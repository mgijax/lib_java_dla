//  $Header
//  $Name:

package org.jax.mgi.shr.dla.embl;

import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dla.DLALoggingException;

/**
  * @is an InputFile for GenBank format record
  * @has a regex begin and end delimiter and an EMBL format file
  * @does provides iteration over the records of the file
  * @company The Jackson Laboratory
  * @author sc
  */


public class EMBLInputFile extends InputDataFile {

     /**
      * constructor
      * @throws IOUException thrown if there is an error accessing the file
      * @throws ConfigException thrown if there is an error accessing the
      * configuration file
      */
     public EMBLInputFile() throws IOUException, ConfigException,
         DLALoggingException
     {

         // Indicate that a record begins when regex "^ID" is found
         this.setBeginDelimiter("^ID");

        // Indicate that a record ends when "//" is found at the
        // beginning of a line.
        setEndDelimiter("^//");

         // indicate that our begin and delimiters are regular expressions
         this.setOkToUseRegex(true);
     }
 }

// $Log

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

