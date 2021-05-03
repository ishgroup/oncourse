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

import java.util.stream.Stream

@CompileStatic
class DateFormatterTest2 {

    static Stream<Arguments> values() {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault())

        // pick 26 Dec for our tests because these tests will fail when run on that day
        // (or the day before or after)
        int currentYear = cal.get(Calendar.YEAR)
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.DAY_OF_MONTH, 26)
        String dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH)

        return Stream.of(
                Arguments.of("26-12-" + currentYear, dayOfWeek + " 26 Dec"),
                Arguments.of("26/12/" + currentYear, dayOfWeek + " 26 Dec"),
                Arguments.of("26\\12\\" + currentYear, dayOfWeek + " 26 Dec"),
                Arguments.of("26.12." + currentYear, dayOfWeek + " 26 Dec"),
                Arguments.of("26,12," + currentYear, dayOfWeek + " 26 Dec"),
                Arguments.of("26|12|" + currentYear, dayOfWeek + " 26 Dec"),

                Arguments.of("2/10/2004", "Sat 2 Oct 2004"),
                Arguments.of("2\\10\\2004", "Sat 2 Oct 2004"),
                Arguments.of("2-10-2004", "Sat 2 Oct 2004"),
                Arguments.of("2|10|2004", "Sat 2 Oct 2004"),
                Arguments.of("2.10.2004", "Sat 2 Oct 2004"),
                Arguments.of("2,10,2004", "Sat 2 Oct 2004"),

                Arguments.of("15-1-1975", "Wed 15 Jan 1975"),
                Arguments.of("15/1/1975", "Wed 15 Jan 1975"),
                Arguments.of("15\\1\\1975", "Wed 15 Jan 1975"),
                Arguments.of("15.1.1975", "Wed 15 Jan 1975"),
                Arguments.of("15,1,1975", "Wed 15 Jan 1975"),
                Arguments.of("15|1|1975", "Wed 15 Jan 1975"),

                Arguments.of("1-7-2014", "Tue 1 Jul 2014"),
                Arguments.of("1/7/2014", "Tue 1 Jul 2014"),
                Arguments.of("1\\7\\2014", "Tue 1 Jul 2014"),
                Arguments.of("1.7.2014", "Tue 1 Jul 2014"),
                Arguments.of("1,7,2014", "Tue 1 Jul 2014"),
                Arguments.of("1|7|2014", "Tue 1 Jul 2014"),

                Arguments.of("1-7-2011", "Fri 1 Jul 2011"),
                Arguments.of("1/7/2011", "Fri 1 Jul 2011"),
                Arguments.of("1\\7\\2011", "Fri 1 Jul 2011"),
                Arguments.of("1.7.2011", "Fri 1 Jul 2011"),
                Arguments.of("1,7,2011", "Fri 1 Jul 2011"),
                Arguments.of("1|7|2011", "Fri 1 Jul 2011"),

                Arguments.of("1-7-2010", "Thu 1 Jul 2010"),
                Arguments.of("1/7/2010", "Thu 1 Jul 2010"),
                Arguments.of("1\\7\\2010", "Thu 1 Jul 2010"),
                Arguments.of("1.7.2010", "Thu 1 Jul 2010"),
                Arguments.of("1,7,2010", "Thu 1 Jul 2010"),
                Arguments.of("1|7|2010", "Thu 1 Jul 2010"),

                Arguments.of("1-7-1911", "Sat 1 Jul 1911"),
                Arguments.of("1/7/1911", "Sat 1 Jul 1911"),
                Arguments.of("1\\7\\1911", "Sat 1 Jul 1911"),
                Arguments.of("1.7.1911", "Sat 1 Jul 1911"),
                Arguments.of("1,7,1911", "Sat 1 Jul 1911"),
                Arguments.of("1|7|1911", "Sat 1 Jul 1911"),

                Arguments.of("1-7-1913", "Tue 1 Jul 1913"),
                Arguments.of("1/7/1913", "Tue 1 Jul 1913"),
                Arguments.of("1\\7\\1913", "Tue 1 Jul 1913"),
                Arguments.of("1.7.1913", "Tue 1 Jul 1913"),
                Arguments.of("1,7,1913", "Tue 1 Jul 1913"),
                Arguments.of("1|7|1913", "Tue 1 Jul 1913"),

                Arguments.of("2/10/04", "Sat 2 Oct 2004"),
                Arguments.of("2-10-04", "Sat 2 Oct 2004"),
                Arguments.of("1-3-05", "Tue 1 Mar 2005"),
                Arguments.of("2/3/08", "Sun 2 Mar 2008"),
                Arguments.of("2-3/08", "Sun 2 Mar 2008"),

                Arguments.of("2-3 -1 2008", "Sat 1 Mar 2008"),

                Arguments.of("now", "today"),
                Arguments.of("today", "today"),
                Arguments.of("tomorrow", "tomorrow"),
                Arguments.of("yesterday", "yesterday"),

                Arguments.of("YestErday", "yesterday")
        )
    }

    @ParameterizedTest
    @MethodSource("values")
    void parseAndFormat(String input, String expected) throws Exception {
        String result = DateFormatter.formatDate(DateFormatter.parseDate(input, TimeZone.getDefault()), Boolean.FALSE, TimeZone.getDefault())
        Assertions.assertEquals(input, expected, result)
    }

}