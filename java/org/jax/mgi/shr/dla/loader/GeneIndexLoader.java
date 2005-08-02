package org.jax.mgi.shr.dla.loader;


import org.jax.mgi.shr.dla.input.fasta.FASTAData;
import org.jax.mgi.shr.dla.input.fasta.FASTAFilter;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSRawAttributes;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.dbs.mgd.loads.Acc.AccessionRawAttributes;
import org.jax.mgi.shr.dla.input.SequenceInput;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.RefAssocRawAttributes;
import org.jax.mgi.dbs.mgd.loads.Seq.SequenceRawAttributes;
import org.jax.mgi.dbs.mgd.loads.SeqRefAssoc.SeqRefAssocPair;
import org.jax.mgi.shr.dla.loader.seq.SeqloaderConstants;
import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.shr.config.GeneIndexLoadCfg;

/**
 * A FASTALoader for loading the TIGR, DoTS and NIA gene indexes
 * @has MSRawAttributes, AccessionRawAttributes, SequenceRawAttributes,
 * RefAssocRawAttributes which are used as input to the SequenceInput object
 * which eventually gets processed by the SequenceProcessor. It also has
 * a SequenceLoadCfg and a GeneIndexLoadCfg for performing configuration and a
 * FASTAFilter for filtering out incoming fasta data
 * @does loads fasta files into the database from TIGR, DoTS and NIA
 * @company The Jackson Laboratory
 * @author not attributable
 */

public class GeneIndexLoader extends FASTALoader
{

    private MSRawAttributes msAttr = null;

    private AccessionRawAttributes accAttr = null;

    private SequenceRawAttributes seqAttr = null;

    private RefAssocRawAttributes refAttr = null;

    private SequenceLoadCfg seqcfg = null;

    private GeneIndexLoadCfg geneIndexCfg = null;

    private FASTAFilter fastaFilter = null;

    /**
     * constructor
     * @throws DLALoaderException thrown from the base class
     */
    public GeneIndexLoader() throws DLALoaderException
    {
        super();
    }

    /**
     * initialize the instance variable
     * @assumes nothing
     * @effects nothing
     * @throws MGIException thrown if any error occurs during initialization
     */
    public void initialize() throws MGIException
    {
        super.initialize();
        logger.logdInfo("GeneIndexLoader initializing", true);
        seqcfg = new SequenceLoadCfg();
        geneIndexCfg = new GeneIndexLoadCfg();
        fastaFilter = geneIndexCfg.getFilter();
        // set molecular source attributes
        msAttr = new MSRawAttributes();
        msAttr.setCellLine(SeqloaderConstants.NOT_APPLICABLE);
        msAttr.setGender(SeqloaderConstants.NOT_APPLICABLE);
        msAttr.setLibraryName(null);
        msAttr.setOrganism(seqcfg.getOrganism());
        msAttr.setStrain(SeqloaderConstants.NOT_APPLICABLE);
        msAttr.setTissue(SeqloaderConstants.NOT_APPLICABLE);
        // set accession attributes
        accAttr = new AccessionRawAttributes();
        accAttr.setIsPreferred(new Boolean(true));
        accAttr.setIsPrivate(new Boolean(false));
        accAttr.setLogicalDB(seqcfg.getLogicalDB());
        accAttr.setMgiType(new Integer(MGITypeConstants.SEQUENCE));
        // set reference attributes
        refAttr = new RefAssocRawAttributes();
        refAttr.setMgiType(new Integer(MGITypeConstants.SEQUENCE));
        refAttr.setRefAssocType(new Integer(MGIRefAssocTypeConstants.PROVIDER));
        refAttr.setRefId(seqcfg.getJnumber());
        // set reusable sequence attributes
        // other record base attributes are set in the load method
        seqAttr = new SequenceRawAttributes();
        seqAttr.setAge(null);
        seqAttr.setCellLine(null);
        seqAttr.setDivision(null);
        seqAttr.setLibrary(null);
        seqAttr.setNumberOfOrganisms(0);
        seqAttr.setProvider(seqcfg.getProvider());
        seqAttr.setQuality(seqcfg.getQuality());
        seqAttr.setRawOrganisms(seqcfg.getOrganism());
        seqAttr.setRecord(null);
        seqAttr.setSeqDate(seqcfg.getReleaseDate());
        seqAttr.setSeqRecDate(seqcfg.getReleaseDate());
        seqAttr.setSex(null);
        seqAttr.setStrain(null);
        seqAttr.setTissue(null);
        seqAttr.setStatus(seqcfg.getStatus());
        seqAttr.setType(seqcfg.getSeqType());
        seqAttr.setVersion(seqcfg.getReleaseNo());
        seqAttr.setVirtual("true");
        logger.logdInfo("GeneIndexLoader completed initialization", true);
    }

    /**
     * runs the delete of existing data for this load
     * @assumes nothing
     * @effects the existing records form previous runs of this load will be
     * deleted
     * @throws MGIException thrown if any MGOException occurs during the delete
     */
    public void preprocess() throws MGIException
    {
      logger.logdInfo("deleting existing load data from previous runs", true);
      seqProcessor.deleteSequences();
    }

    /**
     * loads a fasta record into the database
     * @assumes the instance variables have been initialized
     * @effects the given fast record will be loaded into the database
     * @param data the incoming fasta record
     * @throws MGIException thrown if any error occurs during processing
     */
    public void load(FASTAData data)
        throws MGIException
    {
        if (fastaFilter != null)
          data = fastaFilter.filter(data);
        if (data == null)
          return;
        SequenceInput seqin = new SequenceInput();
        logger.logDebug("Processing sequence " + data.getAccid());
        seqAttr.setDescription(data.getDescription());
        seqAttr.setLength(new Integer(data.getSeqLength()).toString());
        accAttr.setAccid(data.getAccid());
        seqin.addMSource(msAttr);
        seqin.setPrimaryAcc(accAttr);
        seqin.setSeq(seqAttr);
        seqin.addRef(refAttr);
        super.seqProcessor.processInput(seqin);
  }


}
