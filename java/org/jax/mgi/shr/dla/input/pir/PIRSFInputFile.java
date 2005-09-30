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


public class PIRSFInputFile extends InputXMLDataFile
{

    private String TAG =  "iProClassEntry";
    private String filename = null;

    public PIRSFInputFile(String filename) throws ConfigException, IOUException
    {
        super(filename);
        this.filename = filename;
    }

    public XMLDataIterator getIterator()
    {
        return super.getIterator(TAG, new PIRSFInterpreter());
    }

    public class PIRSFInterpreter
        implements XMLDataInterpreter
    {
        private String TARGET_SOURCE = "Mus musculus(house mouse)";
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

    public class PIRSFSuperFamily
      {
        public String recordID = "unset";
        public String mgiID = "unset";
        public String source = "unset";
        public String locusID = "unset";
        public String locusName = "unset";
        public String pirID = "unset";
        public String pirName = "unset";
        public String pirsfID = "unset";
        public String pirsfName = "unset";
        public HashSet refseqID = new HashSet();
        public HashSet trembl = new HashSet();
        public HashSet sprot = new HashSet();

        public String toString()
        {
            return recordID + "\t" + mgiID + "\t" + source + "\t" +
                pirID + "\t" + pirName + "\t" + pirsfID + "\t" +
                pirsfName + "\t" + locusID + "\t" + locusName + "\t" +
                refseqID.toString() + "\t" + sprot.toString() + "\t" +
                trembl.toString();
        }

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

        public int hashCode()
        {
            return this.pirsfID.hashCode();
        }
    }

}
