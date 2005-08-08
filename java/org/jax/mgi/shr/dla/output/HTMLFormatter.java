package org.jax.mgi.shr.dla.output;

import org.jax.mgi.shr.ioutils.OutputFormatter;
import org.jax.mgi.shr.ioutils.OutputDataFile;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.ioutils.IOUExceptionFactory;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.OutputDataCfg;
import org.jax.mgi.shr.config.HTMLFormatterCfg;
import org.jax.mgi.shr.exception.MGIException;

/**
 * is an extension of OutputFormatter used for creating HTML formatted 
 * output
 * @has a Configurator for reading configuration settings
 * @does formats HTML output files
 * @abstract the format method must be implemented at the base class level
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public abstract class HTMLFormatter implements OutputFormatter
{

    private String pageTitle = null;
    private String webServerURL = null;

    // The following are the exceptions that are thrown.
    //
    private static final String FileReadErr =
        IOUExceptionFactory.FileReadErr;

    public HTMLFormatter()
    throws ConfigException
    {
      configure(new HTMLFormatterCfg());
    }

    public HTMLFormatter(HTMLFormatterCfg cfg)
    throws ConfigException
    {
      configure(cfg);
    }

    public void preprocess() {}

    public void postprocess() {}

    public void setPageTitle(String pageTitle)
    {
        this.pageTitle = pageTitle;
    }

    public void setWebServerURL(String url)
    {
        this.webServerURL = url;
    }

    public String getPageTitle()
    {
        return this.pageTitle;
    }

    public String getWebServerURL()
    {
        return this.webServerURL;
    }

    public String getHeader()
    {
        return this.getStandardHeader();
    }

    public String getStandardHeader()
    {
       return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n\"http://www.w3.org/TR/html4/loose.dtd\">\n<html>\n<head>\n<title>" + this.pageTitle + "</title>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head>\n<body <FONT STYLE=\"'Times New Roman', Times, serif\">\n";
    }
    public String getTrailer()
    {
        return "\n</body>\n</html>";

    }
    public String getFileSuffix()
    {
        return "html";
    }

    public String formatAccidAnchorTag(String accid)
    {
        return "<a href=\"http://" + this.webServerURL + "/searches/accession_report.cgi?id=" + accid + "\">" + accid + "</a>";
    }

    public String formatEntrezGeneAnchorTag(String egID)
    {
        return "<a href=\"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=" + egID + "\">" + egID + "</a>";
    }

    public String formatAccidList(String accidList)
    {
      StringBuffer buff = new StringBuffer();
      String[]accids = accidList.split(",");
      for (int i = 0; i < accids.length; i++)
      {
          String accid = accids[i];
          buff.append(this.formatAccidAnchorTag(accid) + ", ");
      }
      buff.deleteCharAt(buff.length() - 1);
      buff.deleteCharAt(buff.length() - 1);
      return buff.toString();
    }

    public String formatEntrezGeneList(String egList)
    {
      StringBuffer buff = new StringBuffer();
      String[]accids = egList.split(",");
      for (int i = 0; i < accids.length; i++)
      {
          String accid = accids[i];
          buff.append(this.formatEntrezGeneAnchorTag(accid) + ", ");
      }
      buff.deleteCharAt(buff.length() - 1);
      buff.deleteCharAt(buff.length() - 1);
      return buff.toString();
    }


    public String formatAccidSVA(String sva)
    {
        // need to parse sva format for sequences
        // and {MGIID=[MGI:1916101], GenBank=[AK004400, AK083883, AK042549]}
        String[] fields = sva.split(",");
        int size = fields.length;
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < fields.length; i++)
        {
            String field = fields[i];
            if (field.indexOf("[") > -1)
            {
                String[] subfields = field.split("\\[");
                String seqfield = subfields[1];
                if (seqfield.indexOf("]") > -1)
                {
                    String[] subsubfields = seqfield.split("\\]");
                    String acc = subsubfields[0];
                    if (!acc.equals("-"))
                        acc =  formatAccidAnchorTag(acc);
                    else
                        acc = "-";
                    buff.append(subfields[0] + "[" + acc + "]");
                }
                else
                {
                    String acc = subfields[1];
                    if (!acc.equals("-"))
                        acc =  formatAccidAnchorTag(acc);
                    else
                        acc = "-";

                    buff.append(subfields[0] + "[" + acc);
                }
            }
            else // does not contain "[" character
            {
                if (field.indexOf("]") > 0)
                {
                    int length = field.length();
                    String acc = field.substring(1, length - 2);
                    buff.append(" " + formatAccidAnchorTag(acc) + "]");
                }
                else
                {
                    String acc = field.substring(1);
                    buff.append(" " + formatAccidAnchorTag(acc));
                }

            }
            if (i < size - 1)
            {
                buff.append(",");
            }
        }
        buff.append("}");
        return buff.toString();
    }

    private void configure(HTMLFormatterCfg cfg)
    throws ConfigException
    {
      this.webServerURL = cfg.getWebServerUrl();
    }


}
