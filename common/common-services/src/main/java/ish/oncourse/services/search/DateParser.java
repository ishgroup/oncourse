package ish.oncourse.services.search;

import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class DateParser {
    private static final Logger logger = LogManager.getLogger();

    public static final String DATE_FORMAT_FOR_AFTER_BEFORE = "yyyyMMdd";

    private String parameter;
    private TimeZone clientTimezone;

    public Date parse() {
        try {
            if (StringUtils.isNumeric(parameter) && Integer.valueOf(parameter) < 1000) {
                return parseDays();
            } else {
                return parseDate();
            }
        } catch (ParseException e) {
            return null;
        }
    }

    private Date parseDays() {
        Calendar calendar;
        if (clientTimezone == null) {
            calendar = Calendar.getInstance(TimeZone.getTimeZone(FormatUtils.TIME_ZONE_UTC));
        } else {
            calendar = Calendar.getInstance(clientTimezone);
        }
        return DateUtils.addDays(calendar.getTime(), Integer.valueOf(parameter));
    }

    private Date parseDate() throws ParseException {
        if (clientTimezone == null) {
            return FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, FormatUtils.TIME_ZONE_UTC).parse(parameter);
        } else {
            return FormatUtils.getDateFormat(DATE_FORMAT_FOR_AFTER_BEFORE, clientTimezone).parse(parameter);
        }
    }

    public static DateParser valueOf(String parameter, TimeZone clientTimezone) {
        DateParser dateParser = new DateParser();
        dateParser.parameter = parameter;
        dateParser.clientTimezone = clientTimezone;
        return dateParser;
    }
}
