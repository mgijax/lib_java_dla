package org.jax.mgi.shr.dla.coordloader;

import org.jax.mgi.dbs.mgd.dao.MAP_CoordinateState;
import org.jax.mgi.dbs.mgd.dao.MRK_ChromosomeLookup;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;

public class CoordMapResolver {

    private VocabKeyLookup mapTypeLookup;
    private VocabKeyLookup unitLookup;
    private MRK_ChromosomeLookup chrLookupByKey;

    public CoordMapResolver() throws TranslationException, ConfigException,
            DBException, CacheException {
        mapTypeLookup = new VocabKeyLookup(VocabularyTypeConstants.MAPTYPE);
        unitLookup = new VocabKeyLookup(VocabularyTypeConstants.MAPUNIT);
        chrLookupByKey = new MRK_ChromosomeLookup();
    }
    public MAP_CoordinateState resolve(CoordMapRawAttributes rawAttr,
            Integer collectionKey, Integer objectKey, Integer seqNum)
                throws TranslationException, ConfigException, DBException,
                CacheException, KeyNotFoundException{
        MAP_CoordinateState state = new MAP_CoordinateState();

        state.setCollectionKey(collectionKey);
        state.setObjectKey(objectKey);
        state.setMGITypeKey(rawAttr.getMapMGIType());
        state.setMapTypeKey(mapTypeLookup.lookup(rawAttr.getMapType()));
        state.setUnitsKey(unitLookup.lookup(rawAttr.getUnitType()));
        state.setLength(new Integer(rawAttr.getLength()));
        state.setSequenceNum(seqNum);
        state.setName(rawAttr.getMapName());

        // set mapAbbrev to mapName if not defined
        String mapAbbrev = rawAttr.getMapAbbrev();
        if (mapAbbrev == null || mapAbbrev.equals("")) {
            state.setAbbreviation(rawAttr.getMapName());
        }
        else state.setAbbreviation(mapAbbrev);

        state.setVersion(rawAttr.getMapVersion());

        return state;

    }
}