package org.jax.mgi.dbs.mgd.MolecularSource;

import java.util.Vector;
import java.util.Iterator;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.ConsoleLogger;
import org.jax.mgi.shr.cache.CacheConstants;
import org.jax.mgi.dbs.mgd.lookup.AssocClonesLookup;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.config.MSProcessorCfg;
import org.jax.mgi.shr.config.ConfigException;

/**
 * an object for processing raw molecular source attributes for a given
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
    protected MSLookup msLookup = null;

    /**
     * the configurator for the MSProcessor
     */
    protected MSProcessorCfg cfg = null;

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

    /*foundByCloneAssociation
     * the following constant definitions are exceptions thrown by this class
     */
    private static String LookupErr = MSExceptionFactory.LookupErr;
    private static String SQLStreamErr = MSExceptionFactory.SQLStreamErr;
    private static String NoSourceFound = MSExceptionFactory.NoSourceFound;
    private static String ConfigErr = MSExceptionFactory.ConfigErr;

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
        this.msLookup = new MSLookup();
        try
        {
          this.cfg = new MSProcessorCfg();
        }
        catch (ConfigException e)
        {
          MSExceptionFactory eFactory = new MSExceptionFactory();
          MSException e2 = (MSException)
              eFactory.getException(ConfigErr, e);
          throw e2;
        }
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
        this.msLookup = new MSLookup();
        try
        {
          this.cfg = new MSProcessorCfg();
        }
        catch (ConfigException e)
        {
          MSExceptionFactory eFactory = new MSExceptionFactory();
          MSException e2 = (MSException)
              eFactory.getException(ConfigErr, e);
          throw e2;
        }

    }


    /**
     * discovers the MolecularSource object to use for the given sequence by
     * resolving the raw attributes.
     * if the attributes are a named source then it looks up the source in the
     * database by name. if it i annonymous then it will look at the sources
     * for the associated clones to see if it can discover a named source that
     * way. if the attributes are annonymous and no named source was found
     * through clone association then a collapsing algorithm is used for
     * sharing molecular source objects.
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

        if (attr.getLibraryName() != null &&
            attr.getLibraryName().toLowerCase().equals("not applicable"))
        {
            // change to anonymous
            attr.setLibraryName(null);
        }
        if (attr.getLibraryName() != null) // this is a named source
        {
            ms = findByLibraryName(attr);
        }
        if (ms == null)
        {
            /**
             * this is an annonymous source or a named
             * which was not found in the database.
             * look for a named source from the associated clones
             * and use that one instead
             **/
            ms = findByAssociatedClones(accid);
        }

        /**
         * if no molecular source was found then just resolve the raw
         * attributes to a new source or an existing source (using the
         * source collapsing algorithm)
         */

        if (ms == null) // then just use the MSResolver
        {
            logger.logDebug("resolving raw attributes for unamed source");
            try
            {
                ms = this.resolver.resolve(attr);
            }
            catch (MSException e)
            {
                if (e instanceof UnresolvedOrganismException)
                {
                    // qc report this one
                    UnresolvedOrganismException e2 =
                        (UnresolvedOrganismException)e;
                    this.qcReporter.reportUnresolvedOrganism(accid,
                        e2.getOrganism());
                    throw e;
                }
                else
                {
                    throw e;
                }
            }
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
     * @param attr the raw attributes for incoming molecular source
     * @param rawLibName the raw library name for the existing sequence
     * @throws MSException thrown if there is an error resolving attributes
     */
    public void processExistingSeqSrc(String accid,
                                      Integer seqKey,
                                      String existingRawLibrary,
                                      MSRawAttributes attr)
    throws MSException
    {
        /**
         * this variable will be set to true if the library name was resolved
         * by looking at the associated clone sources.
         * it will be set to false if the the library name was resolved by
         * a direct library lookup
         */
        Boolean foundByCloneAssociation = null;

        MolecularSource incomingSrc = null;

        if (attr.getLibraryName() != null &&
            attr.getLibraryName().toLowerCase().equals("not applicable"))
        {
            // change to anonymous
            attr.setLibraryName(null);
        }
        if (attr.getLibraryName() != null) // this is a named source
        {
            incomingSrc = findByLibraryName(attr);
        }
        if (incomingSrc != null) // ms found
        {
            foundByCloneAssociation = new Boolean(false);
        }
        if (incomingSrc == null) // ms was not found
        {
            /**
             * this is an annonymous source or a named source
             * which was not found in the database.
             * look for a named source from the associated clones
             **/
            incomingSrc = findByAssociatedClones(accid);
            if (incomingSrc != null) // ms found
            {
                foundByCloneAssociation = new Boolean(true);
            }
        }
        if (incomingSrc == null) // ms was not found
        {
            // find MolecularSource by resolving raw source attributes
            try
            {
                incomingSrc = this.resolver.resolveAttrsOnly(attr);
            }
            catch (MSException e)
            {
                if (e instanceof UnresolvedOrganismException)
                {
                    // qc report this
                    UnresolvedOrganismException e2 =
                        (UnresolvedOrganismException)e;
                    this.qcReporter.reportUnresolvedOrganism(accid,
                        e2.getOrganism());
                }
                throw e;
            }
        }

        /**
         * find the existing source for this sequence that
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
                e.bind(accid);
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
        if (incomingSrc.getName() == null) // annonymous source
            processIncomingAnonymousSource(existingSrc, incomingSrc,
                                           existingSrcAssoc, attr,
                                           existingRawLibrary);
        else // named source
            processIncomingNamedSource(existingSrc, incomingSrc,
                                       existingSrcAssoc, attr,
                                       existingRawLibrary, foundByCloneAssociation);
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
     * checks the configuration to see if this feature is enabled and if so,
     * it will lookup up MolecularSource objects via looking at the sources
     * for the associated clones to the given sequence
     * @param accid the given sequence
     * @return the MolecularSource object or null if not found
     * @throws MSException thrown if there is an error in configuration or if
     * there is an error during lookup
     */
    private MolecularSource findByAssociatedClones(String accid)
        throws MSException
    {
        MolecularSource ms = null;
        boolean okToSearchAssocClones;
        try
        {
          okToSearchAssocClones =
              cfg.getOkToSearchAssocClones().booleanValue();
        }
        catch (ConfigException e)
        {
          MSExceptionFactory eFactory = new MSExceptionFactory();
          MSException e2 = (MSException)
             eFactory.getException(ConfigErr, e);
          throw e2;
        }
        if (okToSearchAssocClones)
        {
          logger.logDebug("looking up named associated clones");
          //ms = findByNonCachedAssociatedClonesLookup(accid);
          ms = findByCachedAssociatedClones(accid);
        }
        else
          logger.logDebug("looking up named associated clones is disabled");
        return ms; // can be null
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
            ms = msLookup.findByName(attr.getLibraryName());
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(msLookup.getClass().getName());
            throw e2;
        }
        if (ms == null)
        {
            attr.setLibraryName(null); // change to anonymous
        }
        return ms;
    }

    /**
     * finds a MolecularSource object by db lookup for one or more of the
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
    private MolecularSource findByNonCachedAssociatedClones(String accid)
        throws MSException
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
         * get the source names for the associated clones of this sequence
         */
        String[] cloneSrcNames = null;
        try
        {
            if (assocClonesLookup == null) {
                // look in the configuration to see if caching of
                // associated clones should be a lazy or full cache
                int cacheType;
                if (cfg.getUseAssocClonesFullCache().booleanValue())
                    cacheType = CacheConstants.FULL_CACHE;
                else
                    cacheType = CacheConstants.LAZY_CACHE;
                assocClonesLookup = new AssocClonesLookup(cacheType);
            }
            cloneSrcNames = assocClonesLookup.lookup(accid);
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(AssocClonesLookup.class.getName());
            throw e2  ;
        }
        if (cloneSrcNames == null)
            return null;
        // try and find a named source from the associated clones.
        // all the names must agree...if they dont then send to qc report
        // and return null.
        String agreedUponName = null;
        for (int i = 0; i < cloneSrcNames.length; i++)
        {
            String thisName = cloneSrcNames[i];
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
            ms = msLookup.findByName(agreedUponName);
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(msLookup.getClass().getName());
            throw e2  ;
        }
        return ms;
    }

    /**
     * processes an existing anonymous source by updating attribute fields
     * if they differ from the incoming values and the existing data has not
     * been curator edited
     * @param incomingSrc the incoming molecular source
     * @param existingSrc the existing molecular source
     * @param existingSrcAssoc the existing sequence/source association
     * @param incomingRaw the incoming raw molecular source values
     * @throws MSException thrown if there is an error getting the changed
     * values onto the SQLStream
     */
    protected void processIncomingAnonymousSource(MolecularSource existingSrc,
                                                  MolecularSource incomingSrc,
                                                  MSSeqAssoc existingSrcAssoc,
                                                  MSRawAttributes incomingRaw,
                                                  String existingRawLibrary)
    throws MSException
    {
        if (existingSrc.getName() != null) // named source
        {
            // call the qc reporter since the named source is changing
            String foundMethod = null;
            qcReporter.reportChangedLibrary(existingSrcAssoc.getSeqKey(),
                                            existingSrc.getMSKey(),
                                            existingRawLibrary,
                                            existingSrc.getName(),
                                            incomingSrc.getMSKey(),
                                            incomingRaw.getLibraryName(),
                                            incomingSrc.getName(),
                                            null);
        }

        // compare incoming source to existing source
        // and make changes to existing source if attr is not curator edited
        // and qc if attr has changed but is curator edited
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
                     incomingRaw.getStrain());
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
                     incomingRaw.getCellLine());

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
                     incomingRaw.getGender());

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
                     incomingRaw.getTissue());

        }
        if (existingSrc.getName() != null)
        {
            // changing a named source to anonymous
            // set the name to null
            existingSrc.setName(null);
            srcHasChanged = true;
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

    /**
     * processes an existing named molecular source by updating the library
     * name if changed and reporting this to a qc report
     * @param incomingSrc the incoming molecular source
     * @param existingSrc the existing molecular source
     * @param existingSrcAssoc the existing sequence/source association
     * @param incomingRaw the incoming raw molecular source attributes
     * @param existingRawLibrary the existing raw molecular source library name
     * @throws MSException thrown if there is an error putting the changed data
     * onto an SQLStream
     */
    protected void processIncomingNamedSource(MolecularSource existingSrc,
                                              MolecularSource incomingSrc,
                                              MSSeqAssoc existingSrcAssoc,
                                              MSRawAttributes incomingRaw,
                                              String existingRawLibrary,
                                              Boolean foundByCloneAssociation)
    throws MSException
    {
        if (existingSrc.getName() != null) // named source
        {
            if (existingSrc.getName().equals(incomingSrc.getName()))
            {
                // they are the same source so do nothing
                return;
            }
            // call the qc reporter since the named source is changing
            String foundMethod = null;
            if (foundByCloneAssociation != null)
            {
                if (foundByCloneAssociation.booleanValue())
                    foundMethod = "found by associated clones";
                else
                    foundMethod = "found by library lookup";

            }
            qcReporter.reportChangedLibrary(existingSrcAssoc.getSeqKey(),
                                            existingSrc.getMSKey(),
                                            existingRawLibrary,
                                            existingSrc.getName(),
                                            incomingSrc.getMSKey(),
                                            incomingRaw.getLibraryName(),
                                            incomingSrc.getName(),
                                            foundMethod);
        }
        // change the association to the incoming source
        existingSrcAssoc.changeMolecularSource(incomingSrc);
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
