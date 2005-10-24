package org.jax.mgi.shr.dla.input.pir;

import java.util.ArrayList;
import java.util.HashSet;


import org.jax.mgi.shr.ioutils.InputXMLDataFile;
import org.jax.mgi.shr.ioutils.XMLDataIterator;
import org.jax.mgi.shr.ioutils.XMLDataInterpreter;
import org.jax.mgi.shr.ioutils.XMLTagIterator;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.ioutils.InterpretException;
import org.jax.mgi.shr.config.ConfigException;

/**
 * A Representation of the iproclass XML from PIR which contains data from
 * the PIRSF project currently being used in the pirsfload
 * @has a pointer to the input file
 * @does provides an itertaor to iterate over PIRSFSuperFamily objects
 * from the input file
 * @company The Jackson Laboratory
 * @author M Walker
 */


public class PIRSFInputFile extends InputXMLDataFile
{

    private String TAG =  "iProClassEntry";
    private String filename = null;

    /**
     * default constructor which obtains the name of the input file from
     * the configuration
     * @throws ConfigException thrown if there is an error accessing
     * the configuration
     * @throws IOUException thrown if there is an error accessing the
     * file system
     */
    public PIRSFInputFile() throws ConfigException, IOUException
    {
        super();
        this.filename = super.getFilename();
    }

    /**
     * constructor which takes the name of the input file as an argument
     * @param filename the name of the input file
     * @throws ConfigException thrown if there is an error accessing
     * the configuration
     * @throws IOUException thrown if there is an error accessing the
     * file system
     */
    public PIRSFInputFile(String filename) throws ConfigException, IOUException
    {
        super(filename);
        this.filename = filename;
    }

    /**
     * get the iterator for this file
     * @return an XMLDataIterator instance which provideds iteration over
     * PIRSFSuperFamily objects found within the file
     */
    public XMLDataIterator getIterator()
    {
        return super.getIterator(TAG, new PIRSFInterpreter());
    }

    /**
     * The XMLDataInterpreter for interpreting instances of PIRSFSuperFamily
     * objects based on the input file
     * @has nothing
     * @does implements the XMLDataInterpreter interface to interpret input
     * xml data as PIRSFSuperFamily instances
     * @company The Jackson Laboratory
     * @author M Walker
     */

    public class PIRSFInterpreter
        implements XMLDataInterpreter
    {
        private String TARGET_SOURCE = "Mus musculus(house mouse)";
        /**
         * interprets the xml input as a PIRSFSuperFamily instance
         * @param it the XMLTagIterator from which to obtain the xml data used
         * to create the PIRSFSuperFamily instance
         * @return the newly created PIRSFSuperFamily instance
         * @throws InterpretException thrown if there is an error during
         * interpreteration
         */
        public Object interpret(XMLTagIterator it)
        throws InterpretException
        {
            PIRSFSuperFamily sf = new PIRSFSuperFamily();
            try
            {
                sf.recordID = it.getAttributeValue(0);
                it.nextTag();
                String store = null;
                while (it.getState() != it.TAG_END)
                {
                    if ("pir-id".equals(it.getTagName()))
                        sf.pirID = it.getText();
                    else if ("pir-name".equals(it.getTagName()))
                        sf.pirName = it.getText();
                    else if ("pirsupfam".equals(it.getTagName()))
                    {
                        store = it.getText();
                        String[] fields = store.split(":");
                        if (!fields[0].startsWith("SF5") &&
                            !fields[0].startsWith("SF8"))
                        {
                            sf.pirsfID = fields[0];
                            sf.pirsfName = fields[1].trim();
                            for (int i = 2; i < fields.length; i++)
                                sf.pirsfName = sf.pirsfName + ":" +
                                    fields[i].trim();
                        }
                    }
                    else if ("mgi-id".equals(it.getTagName()))
                        sf.mgiID = it.getText();
                    else if ("trembl-ac".equals(it.getTagName()))
                        sf.trembl.add(it.getText());
                    else if ("sprot-ac".equals(it.getTagName()))
                        sf.sprot.add(it.getText());
                    else if ("refseq-ac".equals(it.getTagName()))
                    {
                        store = it.getText();
                        if (store.indexOf(";") > 0)
                        {
                            String[] fields = store.split(";");
                            if (!fields[0].startsWith("YP"))
                                sf.refseqID.add(fields[0]);
                        }
                        else if (!store.startsWith("YP"))
                        {
                            sf.refseqID.add(store);
                        }

                    }
                    else if ("locus-id".equals(it.getTagName()))
                        sf.locusID = it.getText();
                    else if ("locus-name".equals(it.getTagName()))
                        sf.locusName = it.getText();
                    else if ("source-org".equals(it.getTagName()))
                    {
                        sf.source = it.getText();
                        if (!sf.source.equals(TARGET_SOURCE))
                            return null;
                    }
                    it.nextTag();
                }
            }
            catch (IOUException e)
            {
                throw new InterpretException("Cannot read data from xml", e);
            }

            return sf;
        }

    }

    /**
     * A plain old java object for storing PIRSF superfamily information
     * as obtained from the iproclass input file from PIR. The input file has
     * multiple entries for any given superfamily. This class represents one
     * record from the input file, not an aggregrate for the entire superfamily.
     * @has instance variables pertaining to PIR superfamilies
     * @does nothing
     * @company The Jackson Laboratory
     * @author M Walker
     */

    public class PIRSFSuperFamily
      {
          /**
           * the record id from the iproclass data file
           */
        public String recordID = "unset";
        /**
         * the mgi id
         */
        public String mgiID = "unset";
        /**
         * the source organism
         */
        public String source = "unset";
        /**
         * the Entrez Gene id
         */
        public String locusID = "unset";
        /**
         * the Entrez Gene name
         */
        public String locusName = "unset";
        /**
         * the pir id
         */
        public String pirID = "unset";
        /**
         * the pir name
         */
        public String pirName = "unset";
        /**
         * the PIRSF superfamily id which we load into the database
         */
        public String pirsfID = "unset";
        /**
         * the PIRSF superfamily name which we load into the database
         */
        public String pirsfName = "unset";
        /**
         * the refseq ids associated to this superfamily record
         */
        public HashSet refseqID = new HashSet();
        /**
         * the tremble ids associated to this superfamily record
         */
        public HashSet trembl = new HashSet();
        /**
         * the swissprot ids associated to this superfamily record
         */
        public HashSet sprot = new HashSet();

        /**
         * override of the toString() method in the Object class
         * @return a string representation of this instance
         */
        public String toString()
        {
            return recordID + "\t" + mgiID + "\t" + source + "\t" +
                pirID + "\t" + pirName + "\t" + pirsfID + "\t" +
                pirsfName + "\t" + locusID + "\t" + locusName + "\t" +
                refseqID.toString() + "\t" + sprot.toString() + "\t" +
                trembl.toString();
        }

        /**
         * override of the equals method in the Object class
         * @return true if the object is the same as this instance
         */
        public boolean equals(Object o)
        {
            if (!(o instanceof PIRSFSuperFamily))
                return false;
            PIRSFSuperFamily sf = (PIRSFSuperFamily)o;
            if (sf.pirsfID.equals(this.pirsfID))
                return true;
            else
                return false;
        }

        /**
         * override of the hashCode() method in the Object class
         * @return a hashcode representation of this instance
         */
        public int hashCode()
        {
            return this.pirsfID.hashCode();
        }
    }

}
