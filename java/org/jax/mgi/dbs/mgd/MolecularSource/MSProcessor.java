package org.jax.mgi.dbs.mgd.MolecularSource;

import java.util.Vector;
import java.util.Iterator;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.ConsoleLogger;
import org.jax.mgi.dbs.mgd.lookup.AssocClonesLookup;
import org.jax.mgi.shr.exception.MGIException;

/**
 * @IS an object for processing raw molecular source attributes for a given
 * sequence object.
 * @has
 * <ul>
 * <li>a SQLStream for doing database inserts of new MolecularSources or
 * updating existing MolecularSources</li>
 * <li>a MSResolver for resolving molecular source raw attributes to a
 * MolecularSource represented by a record in the database</li>
 * <li>a MSQCReporter for reporting various discrepancies</li>
 * </ul>
 * @does discovers the proper MolecularSource for a set of molecular source
 * raw attributes and associates it with a new sequence or determines if any
 * updates are to be made on a MolecularSource for a existing Sequence and
 * performs those updates.
 *
 */

public class MSProcessor
{

    /**
     * a SQLStream for inserting new MolecularSource objects into the database
     */
    protected SQLStream stream = null;
    /**
     * a SQLStream for adding qc items to the radar qc tables
     */
    protected SQLStream qcStream = null;
    /**
     * used for resolving ms raw attributes to a source object
     */
    protected MSResolver resolver = null;
    /**
     * an object for performing qc reporting
     */
    protected MSQCReporter qcReporter = null;
    /**
     * the library lookup object for looking up named sources
     */
    protected MSLookup libLookup = null;

    /**
     * the library lookup for associated clones
     */
    protected AssocClonesLookup assocClonesLookup = null;

    /**
     * the maximum limit of rows allowed to be return from the
     * associated clone lookup
     */
    private int MAXCLONES = 50;

    /**
     * the logger to use
     */
    private Logger logger = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String LookupErr = MSExceptionFactory.LookupErr;
    private static String SQLStreamErr = MSExceptionFactory.SQLStreamErr;
    private static String NoSourceFound = MSExceptionFactory.NoSourceFound;

    /**
     * constructor
     * @throws MSException thrown if there is an error instantiating the
     * MSResolver
     */
    public MSProcessor(SQLStream stream, SQLStream qcStream) throws MSException
    {
        this.stream = stream;
        this.qcStream = qcStream;
        this.resolver = new MSResolver();
        this.qcReporter = new MSQCReporter(qcStream);
        this.logger = new ConsoleLogger();
        this.libLookup = new MSLookup();
    }

    /**
     * constructor
     * @throws MSException thrown if there is an error instantiating the
     * MSResolver
     */
    public MSProcessor(SQLStream stream, SQLStream qcStream,
                       Logger logger) throws MSException
    {
        this.stream = stream;
        this.qcStream = qcStream;
        this.resolver = new MSResolver(logger);
        this.qcReporter = new MSQCReporter(qcStream);
        this.logger = logger;
        this.libLookup = new MSLookup();
    }


    /**
     * discovers the MolecularSource object to use for the given sequence by
     * resolving the raw attributes. if the attributes are a named source
     * then it looks up the source in the database by name. if it is
     * annonymous then it will look at the sources for the associated clones
     * to see if it can discover a named source that way. if the attributes
     * are annonymous and no named source was found through clone association
     * then a collapsing algorithm is used for sharing molecular source
     * objects.
     * @param accid the sequence accid
     * @param attr the ms raw attributes
     * @return the MolecularSource that is resolved
     * @throws MSException thrown if there is an error resolving attributes
     */
    public MolecularSource processNewSeqSrc(String accid, MSRawAttributes attr)
        throws MSException
    {
        // MolecularSource object to be returned
        MolecularSource ms = null;

        logger.logDebug("processing the following raw attributes: " + attr);
        if (attr.getLibraryName() != null) // this is a named source
        {
            logger.logDebug("looking up source by name: " +
                            attr.getLibraryName());
            ms = findByLibraryName(attr);
            if (logger.isDebug())
            {
                if (ms != null)
                {
                    logger.logDebug("Named source found");
                }
                else
                {
                    logger.logDebug("Named source not found");
                }
            }
        }
        if (ms == null) // this is an annonymous source or a named
                        // which was not found in the database
        {
            // look for a source from the associated clones that is named
            // and use that one instead
            logger.logDebug("looking up named associated clones");
            ms = findByCachedAssociatedClones(accid);
        }
        if (logger.isDebug())
        {
            if (ms != null)
            {
                logger.logDebug("found named source: " + ms.getName());
            }
            else
            {
                logger.logDebug("no named source from assoiciated clones found");
            }
        }
        /**
         * if no molecular source was found then just resolve the raw
         * attributes to a new source or an existing source (using the
         * source collapsing algorithm)
         */

        if (ms == null) // then just use the MSResolver
        {
            logger.logDebug("resolving raw attributes for unamed source");
            ms = this.resolver.resolve(attr);
            try
            {
                if (!ms.isInDatabase && !ms.isInBatch)
                    ms.insert(this.stream);
            }
            catch (MGIException e)
            {
                MSExceptionFactory eFactory = new MSExceptionFactory();
                MSException e2 = (MSException)
                    eFactory.getException(SQLStreamErr, e);
                e2.bind(ms.toString());
                throw e2;
            }
        }

        return ms;
    }

    /**
     * determines if the existing molecular source for the given sequence
     * requires updating and reports any discrepancies between curated edited
     * attributes and those that have changed in the molecular source.
     * @param seqid the sequence accid
     * @param seqKey the sequence key
     * @param attr the raw attributes for molecular source
     * @throws MSException thrown if there is an error resolving attributes
     */
    public void processExistingSeqSrc(String seqid,
                                      Integer seqKey,
                                      MSRawAttributes attr)
    throws MSException
    {
        MolecularSource incomingSrc = this.resolver.resolveAttrsOnly(attr);
        /**
         * first find the existing source for this sequence that
         * has an organism that matches the organism from the incoming
         * attributes
         */
        MSSeqAssoc existingSrcAssoc = null;
        MolecularSource existingSrc = null;
        try
        {
            existingSrcAssoc =
                MSSeqAssoc.findBySeqKeyOrganism(seqKey,
                                                incomingSrc.getOrganismKey());
            if (existingSrcAssoc == null)
            {
                MSExceptionFactory eFactory = new MSExceptionFactory();
                MSException e = (MSException)
                    eFactory.getException(NoSourceFound);
                e.bind(seqid);
                e.bind(incomingSrc.getOrganismKey().intValue());
                throw e;
            }
            existingSrc = existingSrcAssoc.getMolecularSource();
        }
        catch (MSException e)
        {
            throw e;
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(MSLookup.class.getName());
            throw e2;
        }
        if (existingSrc.getName() == null) // annonymous source
        {
            // compare incoming source to existing source, perform qc reporting
            // and make changes to existing source if appropriate
            boolean srcHasChanged = false; // track if the existing ms changes
            if (existingSrc.getStrainKey().intValue() !=
                incomingSrc.getStrainKey().intValue())
            {
                if (!existingSrc.isStrainCurated())
                {
                    existingSrc.setStrainKey(incomingSrc.getStrainKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy(
                         existingSrc.getMSKey(),
                         "strain",
                         incomingSrc.getStrainKey(),
                         attr.getStrain());
            }

            if (existingSrc.getCellLineKey().intValue() !=
                incomingSrc.getCellLineKey().intValue())
            {
                if (!existingSrc.isCellLineCurated())
                {
                    existingSrc.setCellLineKey(incomingSrc.getCellLineKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy(
                         existingSrc.getMSKey(),
                         "cellLine",
                         incomingSrc.getCellLineKey(),
                         attr.getCellLine());

            }
            /* age doesnt support qc reports since it has no controlled voc...
               leaving out until resolved
            if (!existingSrc.getAge().equals(incomingSrc.getAge()))
            {
                if (!existingSrc.isAgeCurated())
                {
                    existingSrc.setAge(incomingSrc.getAge());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }
            */

            if (existingSrc.getGenderKey().intValue() !=
                incomingSrc.getGenderKey().intValue())
            {
                if (!existingSrc.isGenderCurated())
                {
                    existingSrc.setGenderKey(incomingSrc.getGenderKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy(
                         existingSrc.getMSKey(),
                         "gender",
                         incomingSrc.getGenderKey(),
                         attr.getGender());

            }

            if (existingSrc.getTissueKey().intValue() !=
                incomingSrc.getTissueKey().intValue())
            {
                if (!existingSrc.isTissueCurated())
                {
                    existingSrc.setTissueKey(incomingSrc.getTissueKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy(
                         existingSrc.getMSKey(),
                         "tissue",
                         incomingSrc.getTissueKey(),
                         attr.getTissue());

            }
            if (srcHasChanged)
            {
              try
              {
                stream.update(existingSrcAssoc);
              }
              catch (MGIException e)
              {
                  MSExceptionFactory eFactory = new MSExceptionFactory();
                  MSException e2 = (MSException)
                      eFactory.getException(SQLStreamErr, e);
                  e2.bind(existingSrc.getClass().getName());
                  throw e2;
              }
            }
        }
    }

    /**
     * set the limit on how many associated clones can be processed for a
     * given sequence
     * @assumes nothing
     * @effects more or less associated clones will be processed before an
     * MSException is thrown
     * @param max the limit value
     */
    public void setMaxAssociatedClones(int max)
    {
        this.MAXCLONES = max;
    }

    /**
     * tries to find a MolecularSource object by using the library name
     * and returns null if not found. The raw library name is obtained from the
     * MSRawAttributes and translated.
     * @param attr the MolecularSource raw attributes object which has the
     * raw library name
     * @return the found MolecularSource object for the given library or
     * null if not found
     * @throws MSException thrown if there is an error with the database,
     * configuration, or an error occuring during lookup or vocabulary
     * translation
     */

    private MolecularSource findByLibraryName(MSRawAttributes attr) throws
        MSException
    {
        MolecularSource ms = null; // the MolecularSource object to return
        // the source key found from the LibraryNameLookup
        Integer sourceKey = null;
        try
        {
            ms = libLookup.findByName(attr.getLibraryName());
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(libLookup.getClass().getName());
            throw e2;
        }
        if (ms == null)
        {
            attr.setLibraryName(null); // change to anonymous
        }
        return ms;
    }

    /**
     * finds a MolecularSource object from cache for one or more of the
     * clones associated to the given sequence.
     * @assumes nothing
     * @effects a new entry could be added to the qc reports if more than one
     * named source is found and they have conflicting names
     * @param accid the given sequence
     * @return the MolecularSource for a named source of one of the associated
     * clones of the given sequence
     * @throws MSException thrown if there is an error with the database or
     * configuration or if more named sources are found than expected which
     * can be changed by calling MSProcessor.setMaxAssociatedClones(int)
     */
    private MolecularSource findByAssociatedClones(String accid) throws
        MSException
    {
        /**
         * get the sources for the associated clones of this sequence
         */
        Vector v = null;
        try
        {
            v = MSLookup.findAssocClonesByAccid(accid, MAXCLONES);
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(MSLookup.class.getName());
            throw e2;
        }
        // try and find a named source from the associated clones.
        // all the names must agree...if they dont then send to qc report.
        MolecularSource ms = null; // the MolecularSource object to return
        String agreedUponName = null;
        for (Iterator i = v.iterator(); i.hasNext(); )
        {
            MolecularSource thisMS = (MolecularSource) i.next();
            String thisName = thisMS.getName();
            if (thisName != null) // it is a named source
            {
                if (agreedUponName == null) // then agree on this name
                {
                    agreedUponName = thisName;
                    ms = thisMS; // this is the molecular source to return
                }
                else // compare this name to the agreed upon name
                {
                    if (!thisName.equals(agreedUponName))
                    {
                        qcReporter.reportCloneNameDiscrepancy(accid,
                                                              agreedUponName,
                                                              thisName);
                        // do not use any of the sources
                        return null;
                    }
                }
            }
        }
        return ms; // can be null if a named source was never found or
                   // two named sources were found with different names
    }

    /**
     * finds a MolecularSource object from cache for one or more of the
     * clones associated to the given sequence.
     * @assumes nothing
     * @effects a new entry could be added to the qc reports if more than one
     * named source is found and they have conflicting names
     * @param accid the given sequence
     * @return the MolecularSource for a named source of one of the associated
     * clones of the given sequence
     * @throws MSException thrown if there is an error with the database or
     * configuration or if more named sources are found than expected which
     * can be changed by calling MSProcessor.setMaxAssociatedClones(int)
     */
    private MolecularSource findByCachedAssociatedClones(String accid)
        throws
        MSException
    {
        /**
         * get the sources for the associated clones of this sequence
         */
        String clones = null;
        try
        {
            if (assocClonesLookup == null) {
                assocClonesLookup = new AssocClonesLookup();
            }
            clones = assocClonesLookup.lookup(accid);
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(AssocClonesLookup.class.getName());
            throw e2  ;
        }
        if (clones == null)
            return null;
        // try and find a named source from the associated clones.
        // all the names must agree...if they dont then send to qc report.
        String agreedUponName = null;
        String[] cloneArray = clones.split(AssocClonesLookup.DELIMITER);
        for (int i = 0; i < cloneArray.length; i++)
        {
            String thisName = cloneArray[i];
            if (agreedUponName == null)
            { // then agree on this name
                agreedUponName = thisName;
            }
            else
            { // compare this name to the agreed upon name
                if (!thisName.equals(agreedUponName))
                {
                    qcReporter.reportCloneNameDiscrepancy(accid,
                        agreedUponName,
                        thisName);
                    // do not use any of the sources
                    return null;
                }
            }
        }
        // now obtain the named MolecularSource
        MolecularSource ms = null;
        try
        {
            ms = libLookup.findByName(agreedUponName);
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(libLookup.getClass().getName());
            throw e2  ;
        }
        return ms;
    }


}