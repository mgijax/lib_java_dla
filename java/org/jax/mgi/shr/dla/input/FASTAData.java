package org.jax.mgi.shr.dla.input;

import org.jax.mgi.shr.ioutils.RecordDataInterpreter;

/**
 * <p>@is </p>
 * <p>@has </p>
 * <p>@does </p>
 * <p>@company The Jackson Laboratory</p>
 * @author not attributable
 *
 */

public class FASTAData
{
    protected String id = null;

    protected String seq = null;

    protected int seqlen = 0;

    public void setIdent(String s)
    {
        this.id = s;
    }

    public void setSeq(String s)
    {
        this.seq = s;
        seqlen = s.length();
    }

    public String getIdent()
    {
        return this.id;
    }

    public String getSeq()
    {
        return this.seq;
    }

    public String getAccid()
    {
        String accid = null;
        int accidEndPos = this.id.indexOf(" ");
        if (accidEndPos == -1)
            accid = this.id;
        else
            accid = this.id.substring(0, accidEndPos);
        return accid.substring(1);
    }

    public int getLength()
    {
        return this.seqlen;
    }

    public static RecordDataInterpreter getRecordInterpreter()
    {
        return new RecdInterpreter();
    }

    public static class RecdInterpreter implements RecordDataInterpreter
    {
        public Object interpret(String s)
        {
            FASTAData f = new FASTAData();
            int nl = s.indexOf(System.getProperty("line.separator"));
            f.setIdent(s.substring(0, nl));
            f.setSeq(s.substring(nl + 1));
            return f;
        }

        public boolean isValid(String s)
        {
            return true;
        }
    }

    public String toString()
    {
        return this.id + "\n" + this.seq;
    }


}