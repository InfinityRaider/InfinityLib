package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.handler.ConfigurationHandler;
import com.infinityraider.infinitylib.reference.Reference;
import java.text.MessageFormat;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

@SuppressWarnings("unused")
public abstract  class LogHelper {
    /**
     * Logs an object (normally a string), to the {@link FMLLog}.
     *
     * Please use {@link #debug(Object)} for logs of {@link Level#DEBUG}.
     *
     * @param logLevel the level at which to log the object, for filtering purposes.
     * @param object the object to be logged (interpreted into a string).
     */
	public static void log(Level logLevel, String format, Object... objects) {
		try {
			FMLLog.log(Reference.MOD_NAME, logLevel, MessageFormat.format(format, objects));
		} catch (IllegalArgumentException ex) {
			// This is bad...
			FMLLog.log(Reference.MOD_NAME, logLevel, format);
		}
	}

    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#ALL}.
     *
     * @param object the object to be logged (interpreted into a string).
     */
    public static void all(String format, Object... objects) {
        log(Level.ALL, format, objects);
    }

    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#ALL}.
     *
     * @param object the object to be logged (interpreted into a string).
     */
    public static void debug(String format, Object... objects) {
        if(ConfigurationHandler.getInstance().debug) {
            log(Level.INFO, "[DEBUG]: " + format, objects);
        }
    }

    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#ERROR}.
     *
     * @param object the object to be logged (interpreted into a string).
     */
    public static void error(String format, Object... objects) {
        log(Level.ERROR, format, objects);
    }

    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#FATAL}.
     *
     * @param object the object to be logged (interpreted into a string).
     */
    public static void fatal(String format, Object... objects) {
        log(Level.FATAL, format, objects);
    }

    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#INFO}.
     *
     * @param object the object to be logged (interpreted into a string).
     */
    public static void info(String format, Object... objects) {
        log(Level.INFO, format, objects);
    }

    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#INFO}.
     *
     * @param object the object to be logged (interpreted into a string).
     */
    public static void off(String format, Object... objects) {
        log(Level.OFF, format, objects);
    }

    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#TRACE}.
     *
     * @param object the object to be logged (interpreted into a string).
     */
    public static void trace(String format, Object... objects) {
        log(Level.TRACE, format, objects);
    }

    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#WARN}.
     *
     * @param object the object to be logged (interpreted into a string).
     */
    public static void warn(String format, Object... objects) {
        log(Level.WARN, format, objects);
    }

    /**
     * Logs an exception via {@link Exception#printStackTrace()} if debug mode is turned on in the configuration.
     *
     * @param e an exception to log.
     */
    public static void printStackTrace(Exception e) {
        if(ConfigurationHandler.getInstance().debug) {
            e.printStackTrace();
        }
    }
}
