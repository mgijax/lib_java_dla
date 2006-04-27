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

    /**
     * default constructor
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     */
    public HTMLFormatter()
    throws ConfigException
    {
      configure(new HTMLFormatterCfg());
    }

    /**
     * constructor for controlling runrime configuration
     * @param cfg a HTMLFormatterCfg object for controlling configuration
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     */
    public HTMLFormatter(HTMLFormatterCfg cfg)
    throws ConfigException
    {
      configure(cfg);
    }


    /**
     * set the title of the html page
     * @assumes nothing
     * @effects html page created by this instance will have the given title
     * @param pageTitle title to use for html page
     */
    public void setPageTitle(String pageTitle)
    {
        this.pageTitle = pageTitle;
    }

    /**
     * set the name of the web server to use for calls to the MGD database
     * @assumes nothing
     * @effects html page created will use the given web server when formatting
     * urls for the mgd database
     * @param url the url of the web server to use for calls to MGD database
     */
    public void setWebServerURL(String url)
    {
        this.webServerURL = url;
    }

    /**
     * get the title of the html page
     * @assumes nothing
     * @effects nothing
     * @return the title of the html page
     */
    public String getPageTitle()
    {
        return this.pageTitle;
    }

    /**
     * get the name of the web server for making calls to the MGD database
     * @assumes nothing
     * @effects nothing
     * @return the name of the web server
     */
    public String getWebServerURL()
    {
        return this.webServerURL;
    }

    /**
     * get the html header text for this html page which calls
     * the getStandardHeader method
     * @assumes nothing
     * @effects nothing
     * @return the html header text
     */
    public String getHeader()
    {
        return this.getStandardHeader();
    }

    /**
     * get the standard html header text for this html page
     * @assumes nothing
     * @effects nothing
     * @return the standard html header text
     */
    public String getStandardHeader()
    {
       return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional" +
              "//EN\"\n\"http://www.w3.org/TR/html4/loose.dtd\">\n<html>" +
              "\n<head>\n<title>" + this.pageTitle + "</title>\n<meta " +
              "http-equiv=\"Content-Type\" content=\"text/html; charset=" +
              "iso-8859-1\"></head>\n<body <FONT STYLE=\"'Times New Roman'" +
              ", Times, serif\">\n";
    }

    /**
     * get the html trailer text for this html page
     * @assumes nothing
     * @effects nothing
     * @return the html header text
     */
    public String getTrailer()
    {
        return "\n</body>\n</html>";
    }

    /**
     * return the string 'html'
     * @assumes nothing
     * @effects nothing
     * @return the string 'html'
     */
    public String getFileSuffix()
    {
        return "html";
    }

    /**
     * format an anchor tag for the accession report cgi
     * @param accid the accession id to use in formatting the tag
     * @return the anchor tag
     */
    public String formatAccidAnchorTag(String accid)
    {
        return "<a href=\"http://" + this.webServerURL + "/searches/" +
            "accession_report.cgi?id=" + accid + "\">" + accid + "</a>";
    }

    /**
     * format an anchor tag for a call to Entrez Gene
     * @param egID EntrezGene accession id to use in formatting the tag
     * @return the anchor tag
     */
    public String formatEntrezGeneAnchorTag(String egID)
    {
        return "<a href=\"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi" +
            "?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=" + egID + "\">" +
            egID + "</a>";
    }

    /**
     * format an anchor tag for a call to PIRSF
     * @param superfamilyID superfamily id to use in formatting the tag
     * @return the anchor tag
     */
    public String formatPIRSFAnchorTag(String superfamilyID)
    {
        return "<a href=\"http://pir.georgetown.edu/cgi-bin/ipcSF?id=" +
            superfamilyID + "\">" + superfamilyID + "</a>";
    }


    /**
     * formats anchor tags to the MGI accession report cgi for a list of
     * comma separated list of accids
     * @param accidList string containing a comma separated list of accids
     * @return the string newly formated with anchor tags replacing the
     * given accids
     */
    public String formatAccidList(String accidList)
    {
      StringBuffer buff = new StringBuffer();
      String[]accids = accidList.split(",");
      for (int i = 0; i < accids.length; i++)
      {
          String accid = accids[i];
          accid = accid.trim();
          buff.append(this.formatAccidAnchorTag(accid) + ", ");
      }
      buff.deleteCharAt(buff.length() - 1);
      buff.deleteCharAt(buff.length() - 1);
      return buff.toString();
    }

    /**
     * formats anchor tags to the Entrez Gene database for a list of
     * comma separated list of Entrez Gene accession ids
     * @param egList string containing a comma separated list of Entrez
     * Gene accession ids
     * @return the string newly formated with anchor tags replacing the
     * given Entrez Gene accession ids
     */
    public String formatEntrezGeneList(String egList)
    {
      StringBuffer buff = new StringBuffer();
      String[]accids = egList.split(",");
      for (int i = 0; i < accids.length; i++)
      {
          String accid = accids[i];
          accid = accid.trim();
          buff.append(this.formatEntrezGeneAnchorTag(accid) + ", ");
      }
      buff.deleteCharAt(buff.length() - 1);
      buff.deleteCharAt(buff.length() - 1);
      return buff.toString();
    }

    /**
     * formats anchor tags to the PIRSF database for a list of
     * comma separated list of PIRSF accession ids
     * @param pirsfList string containing a comma separated list of PIRSF
     * accession ids
     * @return the string newly formated with anchor tags replacing the
     * given PIRSF accession ids
     */
    public String formatPIRSFList(String pirsfList)
    {
      StringBuffer buff = new StringBuffer();
      String[]accids = pirsfList.split(",");
      for (int i = 0; i < accids.length; i++)
      {
          String accid = accids[i];
          accid = accid.trim();
          buff.append(this.formatPIRSFAnchorTag(accid) + ", ");
      }
      buff.deleteCharAt(buff.length() - 1);
      buff.deleteCharAt(buff.length() - 1);
      return buff.toString();
    }


    /**
     * formats anchor tags for a given SVASet string representation
     * @param sva SVASet string representation such as
     * {MGIID=[MGI:1916101], GenBank=[AK004400, AK083883, AK042549]}
     * @return newly formatted string where the accession ids have been
     * replaced with urls to the MGI database
     */
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
                    String acc = field.substring(1, length - 1);
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

    /**
     * configure this instance
     * @param cfg the configuration
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     */
    private void configure(HTMLFormatterCfg cfg)
    throws ConfigException
    {
      this.webServerURL = cfg.getWebServerUrl();
    }
}
