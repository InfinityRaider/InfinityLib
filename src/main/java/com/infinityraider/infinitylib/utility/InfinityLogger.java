package com.infinityraider.infinitylib.utility;

import com.infinityraider.infinitylib.InfinityMod;
import java.text.MessageFormat;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class InfinityLogger {
    private final InfinityMod mod;
    private final Logger logger;

    public InfinityLogger(InfinityMod mod) {
        this.mod = mod;
        this.logger = LogManager.getLogger();
    }

    /**
     * Logs an object (normally a string), to the log.
     *
     * Please use {@link #debug(String, Object...)} for logs of {@link Level#DEBUG}.
     *
     * @param logLevel the level at which to log the object, for filtering purposes.
     * @param objects the objects to be logged (interpreted into a string).
     */
	public void log(Level logLevel, String format, Object... objects) {
		try {
			logger.log(logLevel, "[" + this.mod.getModId() + "]" + MessageFormat.format(format, objects));
		} catch (IllegalArgumentException ex) {
			// This is bad...
            logger.log(logLevel, "[" + this.mod.getModId() + "]" + format);
		}
	}

    /**
     * Logs an object to the log at the level {@link Level#ALL}.
     *
     * @param objects the object to be logged (interpreted into a string).
     */
    public void all(String format, Object... objects) {
        log(Level.ALL, format, objects);
    }

    /**
     * Logs an object to the log at the level {@link Level#ALL}.
     *
     * @param objects the objects to be logged (interpreted into a string).
     */
    public void debug(String format, Object... objects) {
        log(Level.DEBUG, "[DEBUG]: " + format, objects);
    }

    /**
     * Logs an object to the log at the level {@link Level#ERROR}.
     *
     * @param objects the objects to be logged (interpreted into a string).
     */
    public void error(String format, Object... objects) {
        log(Level.ERROR, format, objects);
    }

    /**
     * Logs an object to the log at the level {@link Level#FATAL}.
     *
     * @param objects the objects to be logged (interpreted into a string).
     */
    public void fatal(String format, Object... objects) {
        log(Level.FATAL, format, objects);
    }

    /**
     * Logs an object to the log at the level {@link Level#INFO}.
     *
     * @param objects the objects to be logged (interpreted into a string).
     */
    public void info(String format, Object... objects) {
        log(Level.INFO, format, objects);
    }

    /**
     * Logs an object to the log at the level {@link Level#INFO}.
     *
     * @param objects the objects to be logged (interpreted into a string).
     */
    public  void off(String format, Object... objects) {
        log(Level.OFF, format, objects);
    }

    /**
     * Logs an object to the log at the level {@link Level#TRACE}.
     *
     * @param objects the objects to be logged (interpreted into a string).
     */
    public void trace(String format, Object... objects) {
        log(Level.TRACE, format, objects);
    }

    /**
     * Logs an object to the log at the level {@link Level#WARN}.
     *
     * @param objects the objects to be logged (interpreted into a string).
     */
    public void warn(String format, Object... objects) {
        log(Level.WARN, format, objects);
    }

    /**
     * Logs an exception via {@link Exception#printStackTrace()} if debug mode is turned on in the configuration.
     *
     * @param e an exception to log.
     */
    public void printStackTrace(Exception e) {
        //TODO: log correctly
        e.printStackTrace();
    }
}
