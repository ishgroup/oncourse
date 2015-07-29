package ish.oncourse.services.search;

import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

public class DateParserTest {

    @Test
    public void testDate() throws ParseException {
        DateParser dateParser = DateParser.valueOf("20150101", null);
        Date date = dateParser.parse();
        assertEquals(FormatUtils.getDateFormat(DateParser.DATE_FORMAT_FOR_AFTER_BEFORE, FormatUtils.TIME_ZONE_UTC).parse("20150101"), date);

        dateParser = DateParser.valueOf("20150101", TimeZone.getTimeZone("Australia/Tasmania"));
        date = dateParser.parse();
        assertEquals(FormatUtils.getDateFormat(DateParser.DATE_FORMAT_FOR_AFTER_BEFORE, TimeZone.getTimeZone("Australia/Tasmania")).parse("20150101"), date);

        dateParser = DateParser.valueOf("20150101", new SimpleTimeZone(8 * 60 * 60000, "GMT"));
        date = dateParser.parse();
        assertEquals(FormatUtils.getDateFormat(DateParser.DATE_FORMAT_FOR_AFTER_BEFORE, new SimpleTimeZone(8 * 60 * 60000, "GMT")).parse("20150101"), date);
    }

    @Test
    public void testDays() throws ParseException {
        DateParser dateParser = DateParser.valueOf("14", null);
        Date date = DateUtils.truncate(dateParser.parse(), Calendar.DATE);
        assertEquals(DateUtils.truncate(DateUtils.addDays(Calendar.getInstance(TimeZone.getTimeZone(FormatUtils.TIME_ZONE_UTC)).getTime(), 14), Calendar.DATE), date);


        dateParser = DateParser.valueOf("14", TimeZone.getTimeZone("Australia/Tasmania"));
        date = DateUtils.truncate(dateParser.parse(), Calendar.DATE);
        assertEquals(DateUtils.truncate(DateUtils.addDays(Calendar.getInstance(TimeZone.getTimeZone("Australia/Tasmania")).getTime(), 14), Calendar.DATE), date);


        dateParser = DateParser.valueOf("14", new SimpleTimeZone(8 * 60 * 60000, "GMT"));
        date = DateUtils.truncate(dateParser.parse(), Calendar.DATE);
        assertEquals(DateUtils.truncate(DateUtils.addDays(Calendar.getInstance(new SimpleTimeZone(8 * 60 * 60000, "GMT")).getTime(), 14), Calendar.DATE), date);
    }

}
