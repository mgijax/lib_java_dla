package org.jax.mgi.shr.dla;

import org.jax.mgi.shr.dla.DLALoader;
import org.jax.mgi.shr.dla.input.FASTAInput;
import org.jax.mgi.shr.dla.input.FASTAData;
import org.jax.mgi.shr.dla.seqloader.SequenceInput;
import org.jax.mgi.shr.dla.seqloader.SeqProcessor;
import org.jax.mgi.shr.dla.seqloader.SequenceAttributeResolver;
import org.jax.mgi.shr.dla.seqloader.AccessionRawAttributes;
import org.jax.mgi.shr.config.InputDataCfg;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.dbs.mgd.MolecularSource.MSRawAttributes;

/**
 * <p>@is </p>
 * <p>@has </p>
 * <p>@does </p>
 * <p>@company The Jackson Laboratory</p>
 * @author not attributable
 */

public abstract class FASTALoader extends DLALoader
{
  protected InputDataFile inputFile = null;

  protected SeqProcessor seqProcessor = null;

  public FASTALoader()
  {
      super();
  }

  protected void initialize() throws MGIException
  {
      seqProcessor = new SeqProcessor(this.loadStream, this.qcStream, new SequenceAttributeResolver());
  }

  protected void run() throws MGIException
  {
      if (inputFile == null)
      {
          inputFile = new FASTAInput(super.inputConfig.getInputFileName());
      }
      RecordDataIterator it = inputFile.getIterator();
      while (it.hasNext())
      {
          FASTAData fasta = (FASTAData)it.next();
          load(fasta);
      }
      this.loadStream.close();
      this.qcStream.close();
  }

  protected void post() throws MGIException
  {

  }


  public void load(FASTAInput input) throws MGIException
  {
      this.inputFile = input;
      run();
  }

  abstract public void load(FASTAData data)
      throws MGIException;


}
