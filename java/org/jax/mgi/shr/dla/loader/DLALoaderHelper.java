package org.jax.mgi.shr.dla.loader;

import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.DBSchema;
import org.jax.mgi.shr.dla.log.DLALogger;

/**
 * A collection of helper functions for the DLALoader class
 * @has a collection of loader helper functions
 * @does nothing
 * @company The Jackson Laboratory</p>
 * @author MWalker
 *
 */

public class DLALoaderHelper
{

      /**
       * truncate the list of tables
       * @param list the list of tables to truncate
       * @assumes nothing
       * @effects the list of tables will be truncated
       * @throws DBException thrown if there is an error accessing the database
       */
      static public void truncateTables(String[] list, DBSchema schema,
                                        DLALogger logger)
      throws DBException
      {
          for (int i = 0; i < list.length; i++)
          {
              if (list[i].equals(""))
                  continue;
              logger.logdInfo("truncating table ... " + list[i], true);
              schema.truncateTable(list[i]);
          }
      }


}