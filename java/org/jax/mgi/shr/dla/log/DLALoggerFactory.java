package org.jax.mgi.shr.dla.log;

import org.jax.mgi.shr.log.LoggerFactory;
import org.jax.mgi.shr.log.Logger;


/**
 * A LoggerFactory for creating a DLALogger
 * @has nothing
 * @does creates an instance of a DLALogger
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public class DLALoggerFactory implements LoggerFactory
{

    /**
     * get the Logger implementation of the DLALogger
     * @assumes nothing
     * @effects nothing
     * @return this instance as an implementation of Logger
     */
    public Logger getLogger()
    {
        Logger instance = null;
        try
        {
            instance = (Logger)DLALogger.getInstance();
        }
        catch (DLALoggingException e)
        {
            // just return null without throwing an exception
        }
        return instance;
    }


}