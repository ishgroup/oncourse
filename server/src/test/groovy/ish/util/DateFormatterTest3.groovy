/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@CompileStatic
class DateFormatterTest3 {

    private static Collection<Arguments> values() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR)
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        int currentDate = Calendar.getInstance().get(Calendar.DATE)

        def data = [
                ["15-1-1975", new GregorianCalendar(1975, Calendar.JANUARY, 15)],
                ["15/1/1975", new GregorianCalendar(1975, Calendar.JANUARY, 15)],
                ["15.1.1975", new GregorianCalendar(1975, Calendar.JANUARY, 15)],
                ["15,1,1975", new GregorianCalendar(1975, Calendar.JANUARY, 15)],
                ["15|1|1975", new GregorianCalendar(1975, Calendar.JANUARY, 15)],
                ["15\\1\\1975", new GregorianCalendar(1975, Calendar.JANUARY, 15)],

                ["2/10/2004", new GregorianCalendar(2004, Calendar.OCTOBER, 2)],

                ["2/10/04", new GregorianCalendar(2004, Calendar.OCTOBER, 2)],
                ["2\\10\\04", new GregorianCalendar(2004, Calendar.OCTOBER, 2)],
                ["2:10:04", new GregorianCalendar(2004, Calendar.OCTOBER, 2)],
                ["2,10,04", new GregorianCalendar(2004, Calendar.OCTOBER, 2)],
                ["2.10.04", new GregorianCalendar(2004, Calendar.OCTOBER, 2)],
                ["2|10|04", new GregorianCalendar(2004, Calendar.OCTOBER, 2)],

                ["12/6/2010", new GregorianCalendar(2010, Calendar.JUNE, 12)],
                ["12/6/2011", new GregorianCalendar(2011, Calendar.JUNE, 12)],
                ["12/6/2012", new GregorianCalendar(2012, Calendar.JUNE, 12)],

                ["12/6/1910", new GregorianCalendar(1910, Calendar.JUNE, 12)],
                ["12/6/1911", new GregorianCalendar(1911, Calendar.JUNE, 12)],

                ["12061942", new GregorianCalendar(1942, Calendar.JUNE, 12)],
                ["01061942", new GregorianCalendar(1942, Calendar.JUNE, 1)],
                ["120642", new GregorianCalendar(1942, Calendar.JUNE, 12)],

                ["12/6/10", new GregorianCalendar(2010, Calendar.JUNE, 12)],
                ["12/6/11", new GregorianCalendar(2011, Calendar.JUNE, 12)],
                ["12/6/12", new GregorianCalendar(2012, Calendar.JUNE, 12)],
                ["12/6/13", new GregorianCalendar(2013, Calendar.JUNE, 12)],
                ["12/6/14", new GregorianCalendar(2014, Calendar.JUNE, 12)],
                ["12/6/15", new GregorianCalendar(2015, Calendar.JUNE, 12)],
                ["12/6/16", new GregorianCalendar(2016, Calendar.JUNE, 12)],
                ["12/6/17", new GregorianCalendar(2017, Calendar.JUNE, 12)],
                ["12/6/18", new GregorianCalendar(2018, Calendar.JUNE, 12)],
                ["12/6/19", new GregorianCalendar(2019, Calendar.JUNE, 12)],
                ["12/6/20", new GregorianCalendar(1920, Calendar.JUNE, 12)],
                ["12/6/21", new GregorianCalendar(1921, Calendar.JUNE, 12)],

                ["12 June 2004", new GregorianCalendar(2004, Calendar.JUNE, 12)],
                ["12 Jun 2004", new GregorianCalendar(2004, Calendar.JUNE, 12)],
                ["12-June-2004", new GregorianCalendar(2004, Calendar.JUNE, 12)],
                ["12 Feb 04", new GregorianCalendar(2004, Calendar.FEBRUARY, 12)],

                ["12Feb04", new GregorianCalendar(2004, Calendar.FEBRUARY, 12)],
                ["12Feb 04", new GregorianCalendar(2004, Calendar.FEBRUARY, 12)],

                ["1-3-05", new GregorianCalendar(2005, Calendar.MARCH, 1)],
                ["2/3", new GregorianCalendar(currentYear, Calendar.MARCH, 2)],
                ["2-3", new GregorianCalendar(currentYear, Calendar.MARCH, 2)],
                ["+10y", new GregorianCalendar(currentYear + 10, currentMonth, currentDate)],
                ["-1y", new GregorianCalendar(currentYear - 1, currentMonth, currentDate)],

                ["now", Calendar.getInstance()],
                ["today", Calendar.getInstance()],
                ["tomorrow", new GregorianCalendar(currentYear, currentMonth, currentDate + 1)],
                ["yesterday", new GregorianCalendar(currentYear, currentMonth, currentDate - 1)],

                ["+11", new GregorianCalendar(currentYear, currentMonth, currentDate + 11)],
                ["-21", new GregorianCalendar(currentYear, currentMonth, currentDate - 21)],
                ["12 June 2004 -10", new GregorianCalendar(2004, Calendar.JUNE, 2)],
                ["12-6-2004 -10", new GregorianCalendar(2004, Calendar.JUNE, 2)],
                ["12-6-2004 -1m", new GregorianCalendar(2004, Calendar.MAY, 12)],
                ["12-6-04 -1m", new GregorianCalendar(2004, Calendar.MAY, 12)],
                ["12 6 2004 -1m", new GregorianCalendar(2004, Calendar.MAY, 12)],

                ["Wed 12 Feb 04", new GregorianCalendar(2004, Calendar.FEBRUARY, 12)],
                ["Thu 12 Feb 04", new GregorianCalendar(2004, Calendar.FEBRUARY, 12)],
                ["Friday 12 Feb 04", new GregorianCalendar(2004, Calendar.FEBRUARY, 12)]
        ]

        Collection<Arguments> dataList = new ArrayList<>()
        for (List test : data) {
            dataList.add(Arguments.of(test[0], test[1]))
        }

        Calendar temp = Calendar.getInstance()
        temp.add(Calendar.MONTH, 4)
        dataList.add(Arguments.of("+4m", temp))

        temp = Calendar.getInstance()
        temp.add(Calendar.MONTH, -5)
        dataList.add(Arguments.of("-5m", temp))

        temp = Calendar.getInstance()
        temp.add(Calendar.WEEK_OF_YEAR, 6)
        dataList.add(Arguments.of("+6w", temp))

        temp = Calendar.getInstance()
        temp.add(Calendar.WEEK_OF_YEAR, -3)
        dataList.add(Arguments.of("-3w", temp))

        temp = Calendar.getInstance()
        temp.add(Calendar.DATE, -4)
        dataList.add(Arguments.of("-4", temp))

        temp = Calendar.getInstance()
        dataList.add(Arguments.of("now", temp))

        temp = Calendar.getInstance()
        temp.add(Calendar.DATE, 3)
        dataList.add(Arguments.of("+3d", temp))

        return dataList
    }

    @ParameterizedTest
    @MethodSource("values")
    void parseAndFormat(String input, Calendar expected) throws Exception {
        String result1 = DateFormatter.formatDate(DateFormatter.parseDate(input, TimeZone.getDefault()))
        String result2 = DateFormatter.formatDate(expected.getTime())
        Assertions.assertEquals(result1, result2)
    }
}