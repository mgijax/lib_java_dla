package org.jax.mgi.shr.dla.fasta;

import org.jax.mgi.shr.dla.DLALoader;
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
 * @is a DLALoader class for loading FASTA files
 * @has a FASTAInputFile and a SeqProcessor
 * @does creates SequenceInput objects from the incoming FASTA data and passes
 * them to the SeqProcessor for processing them into the database
 * @abstract the abstract method is load(FASTAData)which is implemented by the
 * specific FASTALoaders
 * @company The Jackson Laboratory
 * @author M Walker
 */

public abstract class FASTALoader extends DLALoader
{
  // the fasta input file
  protected FASTAInputFile inputFile = null;

  // the sequence processor
  protected SeqProcessor seqProcessor = null;

  // constructor
  public FASTALoader()
  {
      super();
  }

  /**
   * initializes the instance data
   * @assumes nothing
   * @effects instance variables will be created and initialized
   * @throws MGIException thrown if any MGIException occurs during
   * initialization
   */
  protected void initialize() throws MGIException
  {
      logger.logdInfo("FASTALoader initializing", true);
      seqProcessor = new SeqProcessor(this.loadStream,
                                      this.qcStream,
                                      new SequenceAttributeResolver());
      logger.logdInfo("FASTALoader completed initialization", true);
  }

  /**
   * the loader run method
   * @assumes all instance variables will be initialized
   * @effects the load will run
   * @throws MGIException thrown if there is an MGIException generated
   * during the load run
   */
  protected void run() throws MGIException
  {
      logger.logdInfo("FASTALoader running", true);
      if (inputFile == null)
      {
          inputFile = new FASTAInputFile(super.inputConfig.getInputFileName());
      }
      RecordDataIterator it = inputFile.getIterator();
      while (it.hasNext())
      {
          FASTAData fasta = (FASTAData)it.next();
          load(fasta);
      }
      logger.logdInfo("FASTALoader run complete", true);
  }

  /**
   * performs post processing such as closing SQLStreams
   * @throws MGIException thrown if an MGIException is thrown during post
   * processing
   */
  protected void post() throws MGIException
  {
      logger.logdInfo("FASTALoader beginning post process", true);
      this.loadStream.close();
      this.qcStream.close();
      logger.logdInfo("FASTALoader complete", true);
  }



  abstract public void load(FASTAData data)
      throws MGIException;


}
