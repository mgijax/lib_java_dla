package org.jax.mgi.dbs.mgd.MolecularSource;

import java.util.Vector;
import java.util.Iterator;

import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.LibraryKeyLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.shr.config.ConfigException;
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
     * a SQLStream for writing writing insert statements for molecular sources
     */
    protected SQLStream stream = null;
    /**
     * used for resolving ms raw attributes to a source object
     */
    protected MSResolver resolver = null;

    /**
     * an object for performing qc reporting
     */
    protected MSQCReporter qcReporter = null;

    /**
     * the maximum limit of rows allowed to be return from the
     * associated clone lookup
     */
    private static final int MAXRESULTS = 25;

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
    public MSProcessor(SQLStream stream) throws MSException
    {
        this.stream = stream;
        this.resolver = new MSResolver();
        this.qcReporter = new MSQCReporter();
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

        if (attr.getLibraryName() != null) // represents a named source
        {
            ms = processByLibraryName(attr);
        }
        if (ms == null) // represents an annonymous source or a named
                        // which was not found in the database
        {
            // look for a source from the associated clones that is named
            // and use that one instead
            ms = processByAssociatedClones(accid);
        }
        /**
         * if no molecular source was found then just resolve the raw
         * attributes to a new source or an existing source (using the
         * source collapsing algorithm)
         */

        if (ms == null) // then just use the MSResolver
        {
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
     * @return a resolved MolecularSource object
     * @throws MSException thrown if there is an error resolving attributes
     */
    public MolecularSource processExistingSeqSrc(String seqid,
                                                 Integer seqKey,
                                                 MSRawAttributes attr)
    throws MSException
    {
        MSAttrResolver attrResolver = new MSAttrResolver();
        MolecularSource incomingSrc = attrResolver.resolveAttributes(attr);
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
            if (existingSrc.getStrainKey() != incomingSrc.getStrainKey())
            {
                if (!existingSrc.isStrainCurated())
                {
                    existingSrc.setStrainKey(incomingSrc.getStrainKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }

            if (existingSrc.getCellLineKey() != incomingSrc.getCellLineKey())
            {
                if (!existingSrc.isCellLineCurated())
                {
                    existingSrc.setCellLineKey(incomingSrc.getCellLineKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }

            if (existingSrc.getAge() != incomingSrc.getAge())
            {
                if (!existingSrc.isAgeCurated())
                {
                    existingSrc.setAge(incomingSrc.getAge());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }

            if (existingSrc.getGenderKey() != incomingSrc.getGenderKey())
            {
                if (!existingSrc.isGenderCurated())
                {
                    existingSrc.setGenderKey(incomingSrc.getGenderKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }

            if (existingSrc.getTissueKey() != incomingSrc.getTissueKey())
            {
                if (!existingSrc.isTissueCurated())
                {
                    existingSrc.setTissueKey(incomingSrc.getTissueKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }
            if (srcHasChanged)
            {
                //existingSrc.update(stream);
            }
        }
        return null;

    }

    private MolecularSource processByLibraryName(MSRawAttributes attr) throws
        MSException
    {
        MolecularSource ms = null; // the MolecularSource object to return
        LibraryKeyLookup lookup = null;
        // the source key found from the LibraryNameLookup
        Integer sourceKey = null;
        try
        {
            lookup = new LibraryKeyLookup();
            sourceKey = lookup.lookup(attr.getLibraryName());
        }
        catch (KeyNotFoundException e)
        {
            qcReporter.reportLibraryNameNotFound(attr);
            return null;
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(LibraryKeyLookup.class.getName());
            throw e2;
        }
        // now get a molecular source with this library key
        try
        {
            ms = MSLookup.findBySourceKey(sourceKey);
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException)
                eFactory.getException(LookupErr, e);
            e2.bind(MSLookup.class.getName());
            throw e2;
        }
        return ms;
    }

    private MolecularSource processByAssociatedClones(String accid) throws
        MSException
    {
        /**
         * get the sources for the associated clones of this sequence
         */
        Vector v = null;
        try
        {
            v = MSLookup.findAssocClonesByAccid(accid, MAXRESULTS);
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
                        qcReporter.reportCloneNameDiscrepancy(v);
                        // do not use any of the sources
                        return null;
                    }
                }
            }
        }
        return ms; // can be null if a named source was never found or
                   // two named sources were found with different names
    }

}