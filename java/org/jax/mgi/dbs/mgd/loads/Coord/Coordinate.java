package org.jax.mgi.dbs.mgd.loads.Coord;

import org.jax.mgi.dbs.mgd.dao.MAP_Coord_CollectionDAO;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_CollectionState;
import org.jax.mgi.dbs.mgd.dao.MAP_CoordinateDAO;
import org.jax.mgi.dbs.mgd.dao.MAP_CoordinateState;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureDAO;
import org.jax.mgi.dbs.mgd.dao.MAP_Coord_FeatureState;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.dao.SQLStream;

/**
  * An object that manages a set of DAOs representing a coordinate collection,
  * a coordinate map, and a coordinate feature.
  * @has
  *   <UL>
  *   <LI>MAP_Coord_CollectionDAO
  *   <LI>MAP_CoordinateDAO
  *   <LI>MAP_Coord_FeatureDAO
  *   </UL>
  * @does
  *   <UL>
  *   <LI>Creates DAO objects for a coordinate's collection, map, and feature
  *   <LI>Adds a coordinate feature to the database and its map and collection
  *       if necessary
  *   <LI>Provides methods to get *copies only* of States for each of its DAO's
  *   </UL>
  * @company The Jackson Laboratory
  * @author sc
  * @version 1.0
  */

public class Coordinate {
    private MAP_Coord_CollectionDAO coordMapCollectionDAO;
    private MAP_CoordinateDAO coordMapDAO;
    private MAP_Coord_FeatureDAO coordMapFeatureDAO;
    private SQLStream stream;


    /**
     * Constructs a Coordinate object
     * @param stream the stream which to pass the DAO objects to perform database
     *        inserts
     */
    public Coordinate (SQLStream stream) {
        this.stream = stream;
    }

    /**
      * sets the MAP_Coord_CollectionState
      * @effects Queries a database for the next collection key
      * @param state a MAP_Coord_CollectionState
      * @throws ConfigException if error creating the DAO object
      * @throws DBException if error creating the DAO object
      */

     public void setCoordMapCollectionState(MAP_Coord_CollectionState state)
         throws ConfigException, DBException {
         coordMapCollectionDAO = new MAP_Coord_CollectionDAO(state);
     }

     /**
       * gets a copy of the MAP_Coord_CollectionState
       * @return a copy of the MAP_Coord_CollectionState
       */

     public MAP_Coord_CollectionState getCoordMapCollectionState () {
         return coordMapCollectionDAO.getState();
     }

     /**
       * gets the MAP_Coord_Collection key
       * @return Integer the MAP_Coord_Collection key
       */

     public Integer getCoordMapCollectionKey () {
         return coordMapCollectionDAO.getKey().getKey();
     }

     /**
       * sets the MAP_CoordinateState
       * @effects Queries a database for the next collection key
       * @param state a MAP_CoordinateState
       * @throws ConfigException if error creating the DAO object
       * @throws DBException if error creating the DAO object
       */

      public void setCoordinateMapState(MAP_CoordinateState state)
          throws ConfigException, DBException {
          coordMapDAO = new MAP_CoordinateDAO(state);
      }

      /**
        * gets a copy of the MAP_CoordinateState
        * @return a copy of the MAP_CoordinateState
        */

      public MAP_CoordinateState getCoordinateMapState () {
          return coordMapDAO.getState();
      }

      /**
        * gets the MAP_Coordinate key
        * @return Integer the MAP_Coordinate key
        */

      public Integer getCoordinateMapKey () {
          return coordMapDAO.getKey().getKey();
      }

      /**
        * sets the MAP_Coord_FeatureState
        * @effects Queries a database for the next collection key
        * @param state a MAP_Coord_FeatureState
        * @throws ConfigException if error creating the DAO object
        * @throws DBException if error creating the DAO object
        */

       public void setCoordMapFeatureState(MAP_Coord_FeatureState state)
           throws ConfigException, DBException {
           coordMapFeatureDAO = new MAP_Coord_FeatureDAO(state);
       }

       /**
         * gets a copy of the MAP_Coord_FeatureState
         * @return a copy of the MAP_Coord_FeatureState
         */

       public MAP_Coord_FeatureState getCoordMapFeatureState () {
           return coordMapFeatureDAO.getState();
       }

       /**
         * gets the MAP_Coord_Feature key
         * @return Integer the MAP_Coord_Feature key
         */

       public Integer getCoordMapFeatureKey () {
           return coordMapFeatureDAO.getKey().getKey();
       }

       /**
        * Determines the stream methods for and passes to those methods each of
        * its DAO objects. Inserts MAP_Coord_Feature.
        * May insert MAP_Coord_Collection
        * May insert MAP_Coordinate
        * @effects Performs database Inserts
        * @throws DBException if error inserting
        */

       public void sendToStream() throws DBException {

           if(coordMapCollectionDAO != null) {
               stream.insert(coordMapCollectionDAO);
           }
           if (coordMapDAO != null) {
               stream.insert(coordMapDAO);
           }
           stream.insert(coordMapFeatureDAO);
       }
}
