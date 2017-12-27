package ish.oncourse.services.search;

import ish.oncourse.solr.query.DayOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class DayParser {
    private static final Logger logger = LogManager.getLogger();

    private String parameter;

    public DayOption parse() {
        try {
            return DayOption.valueOf(parameter);
        } catch (IllegalArgumentException e) {
            logger.debug(e);
            return null;
        }
    }

    public static DayParser valueOf(String parameter) {
        DayParser parser = new DayParser();
        parser.parameter = parameter;
        return parser;
    }
}
