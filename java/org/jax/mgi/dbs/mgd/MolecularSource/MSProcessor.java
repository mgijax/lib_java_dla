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
 * <li>a SQLStream for doing database inserts of new MolecularSources</li>
 * <li>a MSResolver for resolving molecular source raw attributes to a
 * record in the database</li>
 * <li>a MSQCReporter for reporting discrepancies between changes to a source
 * attribute for a sequence and the curration of that attribute</li>
 * </ul>
 * @does discovers the proper MolecularSource for a set of molecular source
 * raw attributes to associate with a new sequence and determines if any
 * updates are to to made on existing MolecularSource records for a existing
 * Sequence.
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
     * discovers the MolecularSource object to use for the given sequence. if the
     * attributes are named then it looks up the source in the database by name
     * and if it annonymous then it will look at the sources for the associated
     * clone to see if it can derive the name and then lookup the source. if the
     * attrinutes are annonymous and the name could not be derived then a
     * collapsing algorithm is used for sharing molecular source objects.
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

        if (attr.getLibraryName() != null)
        {
            ms = processByLibraryName(attr);
        }
        else
        {
            // looks for a source from the associated clones
            ms = processByAssociatedClones(accid);
        }
        if (ms == null) // then just use the MSResolver
        {
            ms = this.resolver.resolve(attr);
            //ms.insert(this.stream);
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
        MolecularSource existingSrc = null;
        try
        {
            //existingSrc =
                //MSLookup.findBySeqKeyOrganism(seqKey,
                                              //incomingSrc.getOrganismKey());
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException) eFactory.getException(LookupErr);
            e2.bind(MSLookup.class.getName());
            throw e2;
        }
        if (existingSrc == null)
        {
            // what do I do ??
        }
        if (existingSrc.getName() == null) // annonymous source
        {
            // compare incoming source to existing source, perform qc reporting
            // and make changes to existing source if appropriate
            boolean isCuratorEdited = false;
                //existingSrc.getCuratedEdited().booleanValue();
            boolean srcHasChanged = false; // track if the existing ms changes
            if (existingSrc.getStrainKey() != incomingSrc.getStrainKey())
            {
                if (!isCuratorEdited || !existingSrc.isStrainCurated())
                {
                    existingSrc.setStrainKey(incomingSrc.getStrainKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }

            if (existingSrc.getCellLineKey() != incomingSrc.getCellLineKey())
            {
                if (!isCuratorEdited || !existingSrc.isCellLineCurated())
                {
                    existingSrc.setCellLineKey(incomingSrc.getCellLineKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }

            if (existingSrc.getAge() != incomingSrc.getAge())
            {
                if (!isCuratorEdited || !existingSrc.isAgeCurated())
                {
                    existingSrc.setAge(incomingSrc.getAge());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }

            if (existingSrc.getGenderKey() != incomingSrc.getGenderKey())
            {
                if (!isCuratorEdited || !existingSrc.isGenderCurated())
                {
                    existingSrc.setGenderKey(incomingSrc.getGenderKey());
                    srcHasChanged = true;
                }
                else
                    qcReporter.reportAttributeDiscrepancy();
            }

            if (existingSrc.getTissueKey() != incomingSrc.getTissueKey())
            {
                if (!isCuratorEdited || !existingSrc.isTissueCurated())
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
            MSException e2 = (MSException) eFactory.getException(LookupErr);
            e2.bind(LibraryKeyLookup.class.getName());
            throw e2;
        }
        try
        {
            ms = MSLookup.findBySourceKey(sourceKey);
        }
        catch (MGIException e)
        {
            MSExceptionFactory eFactory = new MSExceptionFactory();
            MSException e2 = (MSException) eFactory.getException(LookupErr);
            e2.bind(MSLookup.class.getName());
            throw e2;
        }
        return ms;
    }

    private MolecularSource processByAssociatedClones(String accid) throws
        MSException
    {
        MolecularSource ms = null; // the MolecularSource object to return
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
            MSException e2 = (MSException) eFactory.getException(LookupErr);
            e2.bind(MSLookup.class.getName());
            throw e2;
        }
        // try and find a name to assign from the associated clones.
        // all the names must agree...if they dont then send to qc report.
        String agreedUponName = null;
        for (Iterator i = v.iterator(); i.hasNext(); )
        {
            MolecularSource thisMS = (MolecularSource) i.next();
            String thisName = thisMS.getName();
            if (thisName != null) // named source
            {
                if (agreedUponName == null) // agree on this name
                {
                    agreedUponName = thisName;
                    ms = thisMS; // this is the molecular source to return
                }
                else // compare this name to the agreed upon name
                {
                    if (!thisName.equals(agreedUponName))
                    {
                        qcReporter.reportCloneNameDiscrepancy(v);
                        return null;
                    }
                }
            }
        }
        return ms; // can be null if a named source was never found
    }

}
