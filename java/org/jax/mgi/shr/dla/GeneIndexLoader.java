package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.dla.FASTALoader;
import org.jax.mgi.shr.dla.input.FASTAData;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.MolecularSource.MSRawAttributes;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGIRefAssocTypeConstants;
import org.jax.mgi.shr.dla.seqloader.AccessionRawAttributes;
import org.jax.mgi.shr.dla.seqloader.SequenceInput;
import org.jax.mgi.shr.dla.seqloader.RefAssocRawAttributes;
import org.jax.mgi.shr.dla.seqloader.SequenceRawAttributes;
import org.jax.mgi.shr.dla.seqloader.SeqRefAssocPair;
import org.jax.mgi.shr.dla.seqloader.SeqloaderConstants;
import org.jax.mgi.shr.config.SequenceLoadCfg;

/**
 * <p>@is </p>
 * <p>@has </p>
 * <p>@does </p>
 * <p>@company The Jackson Laboratory</p>
 * @author not attributable
 */

public class GeneIndexLoader extends FASTALoader
{

    private MSRawAttributes msAttr = null;

    private AccessionRawAttributes accAttr = null;

    private SequenceRawAttributes seqAttr = null;

    private RefAssocRawAttributes refAttr = null;

    private SequenceLoadCfg cfg = null;

    public void initialize() throws MGIException
    {
        super.initialize();
        cfg = new SequenceLoadCfg();
        // set molecular source attributes
        msAttr = new MSRawAttributes();
        msAttr.setCellLine(SeqloaderConstants.NOT_APPLICABLE);
        msAttr.setGender(SeqloaderConstants.NOT_APPLICABLE);
        msAttr.setLibraryName(null);
        msAttr.setOrganism(cfg.getOrganism());
        msAttr.setStrain(SeqloaderConstants.NOT_APPLICABLE);
        msAttr.setTissue(SeqloaderConstants.NOT_APPLICABLE);
        // set accession attributes
        accAttr = new AccessionRawAttributes();
        accAttr.setIsPreferred(new Boolean(true));
        accAttr.setIsPrivate(new Boolean(false));
        accAttr.setLogicalDB(cfg.getLogicalDB());
        accAttr.setMgiType(new Integer(MGITypeConstants.SEQUENCE));
        // set reference attributes
        refAttr.setMgiType(new Integer(MGITypeConstants.REF));
        refAttr.setRefAssocType(new Integer(MGIRefAssocTypeConstants.PROVIDER));
        // set reusable sequence attributes
        // other record base attributes are set in the load method
        seqAttr = new SequenceRawAttributes();
        seqAttr.setAge(null);
        seqAttr.setCellLine(null);
        seqAttr.setDivision(null);
        seqAttr.setLibrary(null);
        seqAttr.setNumberOfOrganisms(0);
        seqAttr.setProvider(cfg.getProvider());
        seqAttr.setQuality(cfg.getQuality());
        seqAttr.setRawOrganisms(cfg.getOrganism());
        seqAttr.setRecord(null);
        seqAttr.setSeqDate(cfg.getReleaseDate());
        seqAttr.setSeqRecDate(cfg.getReleaseDate());
        seqAttr.setSex(null);
        seqAttr.setStrain(null);
        seqAttr.setTissue(null);
        seqAttr.setStatus(cfg.getStatus());
        seqAttr.setType(cfg.getType());
        seqAttr.setVersion(cfg.getReleaseNo());
        seqAttr.setVirtual("true");

    }

    public void load(FASTAData data)
        throws MGIException
    {
        SequenceInput seqin = new SequenceInput();
        seqAttr.setDescription(data.getIdent());
        seqAttr.setLength(new Integer(data.getLength()).toString());
        accAttr.setAccid(data.getAccid());
        seqin.addMSource(msAttr);
        seqin.setPrimaryAcc(accAttr);
        seqin.setSeq(seqAttr);
        super.seqProcessor.processInput(seqin);
  }


}
