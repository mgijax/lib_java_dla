package org.jax.mgi.dbs.mgd.loads.Alo.dbgss;

import org.jax.mgi.dbs.mgd.loads.Alo.Derivation;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dla.loader.alo.*;
import org.jax.mgi.shr.dla.loader.alo.ALOLoaderConstants;
import org.jax.mgi.dbs.mgd.lookup.ParentStrainLookupByParentKey;


/**
 * An object that creates a Derivation Name String for a dbGSS Gene Trap 
 * Derivation 
 * @has
 *   <UL>
 *   <LI>Template for a dbGSS Gene Trap Derivation Name
 *   <LI>Lookup to get the parent cell line strain from the database
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Provides getters and setters for its attributes
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class DBGSSGeneTrapDerivationNameCreator implements DerivationNameCreator {

	private String template = "~~creator~~ ~~derivType~~ Library " +
			"~~parent~~ ~~strain~~ ~~vectorName~~";
	private ParentStrainLookupByParentKey parentStrainLookup;

	public DBGSSGeneTrapDerivationNameCreator() throws CacheException,
			DBException, ConfigException {
		parentStrainLookup = new ParentStrainLookupByParentKey();
	}

	public String create(Derivation incomingDeriv)
			throws DerivationNameCreatorException {
		String derivName = template;
		/*
		 * get the attributes, look them up where necessary, and
		 * replace in the template
		 */

		/**
		 * creator
		 */
		String creator = incomingDeriv.getCreator();
		//System.out.println("DerivNameCreator: " + creator);
		if (creator.equals("null")) {
			DerivationNameCreatorException e =
					new DerivationNameCreatorException();
			e.bindRecordString("Creator Not Specified");
			throw e;
		}
		else if (creator.startsWith("raw_")) {
			DerivationNameCreatorException e =
					new DerivationNameCreatorException();
			e.bindRecordString("Creator Not Resolved: " + creator);
			throw e;
		}
		derivName = derivName.replaceAll("~~creator~~", creator);


		/**
		 * derivation type
		 */
		String derivType = ALOLoaderConstants.DERIV_TYPE_GENETRAP;
		//System.out.println("Derivation Type: " + derivType);
		derivName = derivName.replaceAll("~~derivType~~", derivType);

		/**
		* parent cell line
		*/
		String parentCellLine = incomingDeriv.getParentCellLine();
		//System.out.println("Derivation parent: " + parentCellLine);
		if (parentCellLine.equals("null")) {
			DerivationNameCreatorException e =
					new DerivationNameCreatorException();
			e.bindRecordString("Parent Cell Line Not Specified");
			throw e;
		}
		else if (parentCellLine.startsWith("raw_")) {
			DerivationNameCreatorException e =
					new DerivationNameCreatorException();
			e.bindRecordString("Parent Cell Line Not Resolved: " + parentCellLine);
			throw e;
		}

		derivName = derivName.replaceAll("~~parent~~",
				parentCellLine);

		/**
		 * parent cell line strain
		 *
		 * Here we want the strain associated with the parent in the database
		 * NOT what the incoming derivation says is the parent strain as they
		 * may not be the same. Lookup the parent key to get its strain
		 */
		Integer parentKey = incomingDeriv.getParentCellLineKey();
		String parentCellLineStrain = null;
		try {
		    parentCellLineStrain = parentStrainLookup.lookup(parentKey);
		} catch (DBException e) {
			DerivationNameCreatorException e1 =
					new DerivationNameCreatorException();
			e1.bindRecordString(e.getMessage());
			throw e1;
		} catch (ConfigException e) {
			DerivationNameCreatorException e1 =
					new DerivationNameCreatorException();
			e1.bindRecordString(e.getMessage());
			throw e1;
		} catch (CacheException e) {
			DerivationNameCreatorException e1 =
					new DerivationNameCreatorException();
			e1.bindRecordString(e.getMessage());
			throw e1;
		}
		derivName = derivName.replaceAll("~~strain~~", parentCellLineStrain);
		//System.out.println("Derivation strain: " + parentCellLineStrain);

		/**
		 * vector name
		 */
		String vectorName = incomingDeriv.getVectorName();
		//System.out.println("Derivation vector name: " + vectorName);
		if (vectorName.equals("null")) {
			DerivationNameCreatorException e =
					new DerivationNameCreatorException();
			e.bindRecordString("Vector Name Not Specified" );
			throw e;
		}
		else if (vectorName.startsWith("raw_")) {
			DerivationNameCreatorException e =
					new DerivationNameCreatorException();
			e.bindRecordString("Vector Name Not Resolved: "  + vectorName);
			throw e;
		}
		derivName = derivName.replaceAll("~~vectorName~~", vectorName);

		return derivName;
	}
}
