package org.jax.mgi.shr.dla.coordloader;

import java.util.*;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.shr.dla.seqloader.SeqloaderConstants;
import org.jax.mgi.shr.ioutils.RecordDataInterpreter;

import org.jax.mgi.dbs.mgd.dao.MRK_ChromosomeLookup;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;


/**
 * An object that represents the raw values of Map Coordinate Feature, including
 *     its Map and its Collection. Coordinate MGS Assembly format record and obtains
 *     values from a Configurator to create a CoordInput data object.<BR>
 *     Determines if a record is a comment record (starts with "#")
 *     or a coordinate record<BR>
 * @has
 *   <UL>
 *   <LI>A CoordInput object into which it bundles:
 *   <LI>A CoordMapRawAttributes object
 *   <LI>An CoordMapFeatureRawAttributes object for its seqid
 *   <LI>Coordinate map collection
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Determines if a MGS Assembly Coordinate format record is a commented
 *       record or a coordinate record
 *   <LI>Parses a MGS Assembly Coordinate format sequence record and gets values
 *        from Configuration
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MGSCoordMapFormatInterpreter extends CoordMapInterpreter {

    /**
     * The set of attributes parsed from an input record
     */
    private String record;
    private String coordId;
    private String chromosome;
    private String startBP;
    private String endBP;
    private String strand;

    /**
     * A CoordinateMapInput and its parts
     */

    // The object we are building. Represents raw coordinate map feature,
    // a coordinate map and a coordinate map collection
    private CoordinateInput input = new CoordinateInput();

    // raw attributes for a coordinate map - reused by calling reset()
    private CoordMapRawAttributes rawMap = new CoordMapRawAttributes();

    // raw attributes for a coordinate map feature- reused by calling reset()
    private CoordMapFeatureRawAttributes rawMapFeature = new CoordMapFeatureRawAttributes();

    // MGS Configurator to get chromosome
    //private MGSCoordLoadCfg MGSCfg;

    /**
     * Constructs a MGSCoordMapFormatInterpreter
     * @assumes Nothing
     * @effects Nothing
     * @param Nothing
     * @throws ConfigException if can't find configuration file
     */

    public MGSCoordMapFormatInterpreter() throws ConfigException {
        //MGSCfg = new MGSCoordLoadCfg();
  }

    /**
     * a predicate that returns false if 'record' is a commented record, else true
     * @assumes Nothing
     * @effects Nothing
     * @param record A MGS coordinate map format record
     * @return true if we want to load this record
     * @throws Nothing
     */
    public boolean isValid(String record) {
        if (!record.startsWith("#")) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
    * Parses a MGS assembly formatsequence record and  creates a SequenceInpu
    * object from Configuration and parsed values
    * @assumes Nothing
    * @effects Nothing
    * @param rcd A header record or a sequence record<BR>
    * coordinate record example: <BR>
    * coordId \t chromosome \t startBP \t endBP \g strand
    * e.g.
    * 240677 \t 1 \t 3068294 \t 3069180 \t + \t GENE \n
    * @return A SequenceInput object representing 'rcd'
    * @throws RecordFormatException if we can't parse an attribute because of
    *         record formatting errors
    */
    public Object interpret(String rcd) throws RecordFormatException, ConfigException {
        // reset objects - don't reset rawMS, rawRefAssoc, it is same for all sequences
        input.reset();

        // get coordId, chromosome, start/end BP, and strand from the record
        parseRecord(rcd);

        // set map collection is CoordMapInput object
        input.setMapCollectionName(mapCollection);

        // create CoordMapRawAttributes and set in CoordMapInput object
        setMapRawAttributes();

        // create CoordMapFeatureRawAttributes and set in CoordMapInput object
        setFeatureRawAttributes();

        return input;
   }

   /**
    * parses an MGS coordinate map format record
    * @assumes Nothing
    * @effects Nothing
    * @param record A MGSAssembly format sequence record
    * @return true if we want to load this sequence
    * @throws RecordFormatException if there are less than 5 columns in the file
    */

   private void  parseRecord(String rcd) throws RecordFormatException {

       // get the seqid and description from the non-header record
       ArrayList splitLine = StringLib.split(rcd, SeqloaderConstants.TAB);
       // there are actually 6, but the seqloader uses the 6th element
       if (splitLine.size() < 5) {
           RecordFormatException e = new RecordFormatException();
               e.bindRecord("The coordinate record is not formatted correctly, " +
                   "5 tab delimited elements expected.\n" + rcd);
            throw e;
        }
        record = rcd;
        coordId = ((String)splitLine.get(0)).trim();
        chromosome = ((String)splitLine.get(1)).trim();
        startBP = ((String)splitLine.get(2)).trim();
        endBP = ((String)splitLine.get(3)).trim();
        strand = ((String)splitLine.get(4)).trim();

   }


   /**
     * sets values in the CoordMapRawAttributes object and sets the
     * CoordMapRawAttributes object in the CoordInput object
     * @assumes this coordinate map represents a chromosome and that another
     * class ( resolver) will determine the sequence number of the chromsome
     * @effects Nothing
     * @param record Nothing
     * @return Nothing
     * @throws ConfigException if the chromosome length cannot be determined
     */

   private void setMapRawAttributes() throws ConfigException {
       // set the map collection
       rawMap.setMapCollection(mapCollection);

       // set the coordinate map object - in this case it is a chromosome
       rawMap.setCoordMapObject(chromosome);

       // set the coordinate map MGI type for the coord map object
       rawMap.setMapMGIType(new Integer(MGITypeConstants.CHROMOSOME));

       // set the map type
       rawMap.setMapType(mapType);

       // set the map unit
       rawMap.setUnitType(mapUnits);
       // this uses configuration to set length
       //rawMap.setLength(MGSCfg.getChrLength("CHR_" + chromosome));

       // for now we will just set to 0
       rawMap.setLength("0");

       // resolver must set sequence number
       // we aren't setting map name, the default is null
       // we aren't setting (map name) abbreviation, the default is null

       // set the map version
       rawMap.setMapVersion(version);

       // set the sequence retrieval parameter
       rawMap.setSeqRetParam(retrievalParam);

       // set CoordMapRawAttributes in the CoordinateMapInput object
       input.setCoordMapRawAttributes(rawMap);

   }

   /**
     * sets values in the CoordMapFeatureRawAttributes object and sets the
     * CoordMapFeatureRawAttributes object in the CoordInput object
     * @assumes Nothing
     * @effects Nothing
     * @param record Nothing
     * @return Nothing
     * @throws Nothing
     */
   private void setFeatureRawAttributes() {
       rawMapFeature.setRecord(record);
       // not setting coord map attribute, resolver will derive from
       // CoordMapRawAttributes
       // set the MGIType
       rawMapFeature.setMGIType(new Integer(MGITypeConstants.SEQUENCE));

       // set the ID
       rawMapFeature.setObjectId(coordId);

       // set the logicalDB of ID
       rawMapFeature.setLogicalDB(logicalDB);

       // set the start coordinate
       rawMapFeature.setStartCoord(startBP);

       // set the end coordinate
       rawMapFeature.setEndCoord(endBP);

       // set the strand
       rawMapFeature.setStrand(strand);

       // set CoordMapFeatureRawAttributes in the CoordinateMapInput object
       input.setCoordMapFeatureRawAttributes(rawMapFeature);
   }
}
