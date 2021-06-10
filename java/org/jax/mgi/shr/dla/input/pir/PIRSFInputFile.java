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
 * A Representation of the iproclass XML file from PIR which contains data from
 * the PIRSF project currently being used in the pirsfload
 * @has a pointer to the input file
 * @does provides an itertaor to iterate over PIRSFSuperFamily objects
 * from the input file
 * @company The Jackson Laboratory
 * @author M Walker
 */


public class PIRSFInputFile extends InputXMLDataFile
{

    //private String TAG =  "iProClassEntry";
    private String TAG =  "entry";
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
     * get the iterator for this file which will iterate over
     * PIRSFSuperFamily instances
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
        private String TARGET_SOURCE = "Mus musculus (Mouse)";
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
                int foundID = 0;
                sf.recordID = it.getAttributeValue(0); // no idea what this is used for. In the new xml it is either TrEMBL or Swiss-Prot
                //System.out.println(it.getAttributeValue(0));
                it.nextTag();
                String store = null;
                while (it.getState() != it.TAG_END)
                {
                    // we only want the first accession, subsequent are secondary
                    // <accession>A3KML3</accession>
                    
                    // get all attribute names and the count of attributes 
                    String[] atts = it.getAttributeNames();
                
                    int attsCt = it.getAttributeCount();

		    //  skip SF5 and SF8 PIRSF terms

		    //  multiple PIRSF tags may exist per record
		    //  just grab the first one

		    //if ("PIRSF_ID".equals(it.getTagName()))
		    sf.source = "Mus musculus";
		    if ("dbReference".equals(it.getTagName())) {
                        //for( int i = 0; i <= atts.length - 1; i++) {
                        //    System.out.println(atts[i]);
                        //}

                        if (sf.pirsfID.equals("unset")) {       
                            //store = it.getText();
                            // <dbReference type="PIRSF" id="PIRSF000868">
                            // <property type="entry name" value="14-3-3"/>
                            // <property type="match status" value="1"/>
                            // </dbReference>
                            //  
                            // <dbReference type="MGI" id="MGI:891963"/>
                            //
                            // <dbReference type="RefSeq" id="NP_035869.1">
                            //
                            // <dbReference type="GeneID" id="22630"/>   
                            for (int i = 0; i < attsCt; i++) {
                                if (atts[i] != null && atts[i].equals("type") && it.getAttributeValue(i).equals("PIRSF")) {
                                    foundID = 1;
                                    store = it.getAttributeValue(i+1); // "PIRSF000868"
                                    if (!store.startsWith("PIRSF5") &&
                                        !store.startsWith("PIRSF8")) {
                                        sf.pirsfID = store;
                                    }
                                }
                                else if (atts[i] != null && atts[i].equals("type") && it.getAttributeValue(i).equals("MGI")) {
                                    if (sf.mgiID.equals("unset")) {
                                        sf.mgiID  = it.getAttributeValue(i + 1); // "MGI:891963"
                                    }
                                }
                                else if (atts[i] != null && atts[i].equals("type") && it.getAttributeValue(i).equals("RefSeq")) {    
                                   // translate XXXXXX.v to XXXXX
                                   store  = it.getAttributeValue(i + 1); // "NP_035869.1"
                                    if (store.indexOf("\\.") > 0) {
                                            String[] fields = store.split("\\.");
                                            if (!fields[0].startsWith("YP"))
                                                sf.refseqID.add(fields[0]);
                                        }
                                    else if (!store.startsWith("YP")) {
                                        sf.refseqID.add(store);
                                    }
                                }
                                else if (atts[i] != null && atts[i].equals("type") && it.getAttributeValue(i).equals("GeneID")) {
                                     sf.entrezID = it.getAttributeValue(i + 1); // 22630
                                }
                            }
		        }
                    }


                    else if (foundID == 1 && "property".equals(it.getTagName()) ) {
                        String[] props = it.getAttributeNames();
                        int propsCt = it.getAttributeCount();
                        for (int i = 0; i < propsCt; i++) {
                            if (  props[i] != null && props[i].equals("value")) {
                                sf.pirsfName = it.getAttributeValue(i);
                                System.out.println("pirsfName: " + sf.pirsfName);
                                foundID = 0;
                            }
                        }
                    }                                                                                                   

                    // get uniprot accessions <accession>A3KML3</accession>
                    else if ("accession".equals(it.getTagName())) { 
                        sf.uniprot.add(it.getText());
                    }
                    else if("property type".equals(it.getTagName())) {
                         System.out.println("found property type");
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
        public String entrezID = "unset";
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
         * the uniprot ids associated to this superfamily record
         */
        public HashSet uniprot = new HashSet();

        /**
         * override of the toString() method in the Object class
         * @return a string representation of this instance
         */
        public String toString()
        {
            return recordID + "\t" + mgiID + "\t" + source + "\t" +
                pirsfID + "\t" + pirsfName + "\t" + entrezID + "\t" +
                refseqID.toString() + "\t" + uniprot.toString();
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
