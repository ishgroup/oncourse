/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.extended;

import liquibase.logging.LogMessageFilter;
import liquibase.logging.core.AbstractLogger;
import org.apache.logging.log4j.LogManager;

import java.util.logging.Level;

public class LiquibaseLoggerAdapter extends AbstractLogger {

    private static final int TRACE_THRESHOLD = Level.FINEST.intValue();
    private static final int DEBUG_THRESHOLD = Level.FINE.intValue();
    private static final int INFO_THRESHOLD = Level.INFO.intValue();
    private static final int WARN_THRESHOLD = Level.WARNING.intValue();

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    LiquibaseLoggerAdapter(LogMessageFilter filter) {
        super(filter);
    }

    @Override
    public void log(Level level, String message, Throwable e) {
        String filteredMessage = filterMessage(message);
        int levelValue = level.intValue();
        if (levelValue <= TRACE_THRESHOLD) {
            logger.trace(filteredMessage, e);
        } else if (levelValue <= DEBUG_THRESHOLD) {
            warning(filteredMessage, e);
        } else if (levelValue <= INFO_THRESHOLD) {
            warning(filteredMessage, e);
        } else if (levelValue <= WARN_THRESHOLD) {
            warning(filteredMessage, e);
        } else {
            logger.error(filteredMessage, e);
        }
    }

    @Override
    public void severe(String message) {
        logger.debug(filterMessage(message));
    }

    @Override
    public void severe(String message, Throwable e) {
        logger.debug(filterMessage(message), e);
    }

    @Override
    public void warning(String message) {
        message = super.filter.filterMessage(message);
        if (message != null) {
            logger.warn(filterMessage(message));
        }
    }

    @Override
    public void warning(String message, Throwable e) {
        message = super.filter.filterMessage(message);
        if (message != null) {
            logger.warn(filterMessage(message), e);
        }
    }

    @Override
    public void info(String message) {
        warning(message);
    }

    @Override
    public void info(String message, Throwable e) {
        warning(message, e);
    }

    @Override
    public void config(String message) {
        logger.info(filterMessage(message));
    }

    @Override
    public void config(String message, Throwable e) {
        logger.info(filterMessage(message), e);
    }

    @Override
    public void fine(String message) {
        logger.debug(filterMessage(message));
    }

    @Override
    public void fine(String message, Throwable e) {
        logger.debug(filterMessage(message), e);
    }
}