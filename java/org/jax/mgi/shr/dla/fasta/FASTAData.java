package org.jax.mgi.shr.dla.fasta;

import org.jax.mgi.shr.ioutils.RecordDataInterpreter;

/**
 * @is a class which represents one record from a FASTA file
 * @has fasta data
 * @does provides accessors for the fasta data
 * @company The Jackson Laboratory</p>
 * @author M Walker
 *
 */

public class FASTAData
{
    // the identity string from the fasta record
    protected String id = null;

    // the sequence from the fasta record
    protected String seq = null;

    // the sequence length
    protected int seqlen = 0;

    /**
     * set the identity string which includes the sequence id plus the
     * description but does not include the beginning '>' chararcter
     * @assumes nothing
     * @effects the internal value will be changed
     * @param s the identity string
     */
    public void setIdentity(String s)
    {
        this.id = s;
    }

    /**
     * set the sequence string
     * @assumes nothing
     * @effects the internal value will be changed
     * @param s the sequence string
     */
    public void setSeq(String s)
    {
        this.seq = s;
        seqlen = s.length();
    }

    /**
     * get the sequence string
     * @assumes nothing
     * @effects nothing
     * @return the sequence string
     */
    public String getSeq()
    {
        return this.seq;
    }

    /**
     * get the accid
     * @assumes nothing
     * @effects nothing
     * @return the accid
     */
    public String getAccid()
    {
        String accid = null;
        int accidEndPos = this.id.indexOf(" ");
        if (accidEndPos == -1)
            accid = this.id;
        else
            accid = this.id.substring(0, accidEndPos);
        return accid;
    }

    /**
     * get the description
     * @assumes nothing
     * @effects nothing
     * @return the description
     */
    public String getDescription()
    {
      String desc = null;
      int accidEndPos = this.id.indexOf(" ");
      if (accidEndPos == -1)
          desc = null;
      else
          desc = this.id.substring(accidEndPos + 1);
      return desc;

    }


    /**
     * get the sequence length
     * @assumes nothing
     * @effects nothing
     * @return the sequence length
     */
    public int getSeqLength()
    {
        return this.seqlen;
    }

    /**
     * get a RecordDataInterpreter for interpreting fasta data from an input
     * file
     * @assumes nothing
     * @effects nothing
     * @return the RecordDataInterpreter
     */
    public static RecordDataInterpreter getRecordInterpreter()
    {
        return new RecdInterpreter();
    }

    // the new line character
    protected static String NEWLINE = System.getProperty("line.separator");

    /**
     *
     * @is a RecordDataInterpreter for interpreting fasta records from a fasta
     * file
     * @has nothing
     * @does creates new FASTAData objects based on a given fasta record from
     * an input file
     * @company Jackson Laboratory
     * @author M Walker
     *
     */
    public static class RecdInterpreter implements RecordDataInterpreter
    {
        public Object interpret(String s)
        {
            FASTAData f = new FASTAData();
            int nl_index = s.indexOf(NEWLINE);
            f.setIdentity(s.substring(1, nl_index));
            f.setSeq(s.substring(nl_index + 1).replaceAll(NEWLINE, ""));
            return f;
        }

        public boolean isValid(String s)
        {
            return true;
        }
    }

    /**
     * override the toString from Object class
     * @assumes nothing
     * @effects nothing
     * @return the string representation of this instance
     */
    public String toString()
    {
        return this.id + "\n" + this.seq;
    }


}