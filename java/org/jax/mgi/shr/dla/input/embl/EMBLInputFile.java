package org.jax.mgi.shr.dla.input.embl;

import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dla.log.DLALoggingException;

/**
  * An InputFile for GenBank format record
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
      * @throws DLALoggingException
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
