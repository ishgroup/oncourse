/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@CompileStatic
class TimeFormatterTest2 {

    private static TimeZone sydney = TimeZone.getTimeZone("Australia/Sydney")
    private static TimeZone perth = TimeZone.getTimeZone("Australia/Perth")
    private static TimeZone brisbane = TimeZone.getTimeZone("Australia/Brisbane")

    private static Collection<Arguments> values() {
        Object[][] data = [
                ["11", "11am", "9am", "8am", "11am", "10am"],
                ["11p", "11pm", "9pm", "8pm", "11pm", "10pm"],
                ["11pm", "11pm", "9pm", "8pm", "11pm", "10pm"],
                ["11a", "11am", "9am", "8am", "11am", "10am"],
                ["23", "11pm", "9pm", "8pm", "11pm", "10pm"],

                ["11:10", "11:10am", "9:10am", "8:10am", "11:10am", "10:10am"],
                ["11:10a", "11:10am", "9:10am", "8:10am", "11:10am", "10:10am"],
                ["11:10p", "11:10pm", "9:10pm", "8:10pm", "11:10pm", "10:10pm"],
                ["11:10AM", "11:10am", "9:10am", "8:10am", "11:10am", "10:10am"],
                ["11:10 AM", "11:10am", "9:10am", "8:10am", "11:10am", "10:10am"],
                ["23:10", "11:10pm", "9:10pm", "8:10pm", "11:10pm", "10:10pm"]
        ]

        Collection<Arguments> dataList = new ArrayList<>()
        for (Object[] test : data) {
            dataList.add(Arguments.of(test[0], test[1]))
        }
        return dataList

    }

    @ParameterizedTest
    @MethodSource("values")
    void parseAndFormat(String inputSydney, String expectedSydney, String expectedPerth, String expectedPerthDST, String expectedBrisbane, String expectedBrisbaneDST) throws Exception {
        // check sydney input sydney output
        String result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(inputSydney, getTestCalendar(), sydney).getTime(), sydney)
        Assertions.assertEquals(inputSydney, expectedSydney, result)

        // check sydney input perth output
        result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(inputSydney, getTestCalendar(), sydney).getTime(), perth)
        Assertions.assertEquals(inputSydney, expectedPerth, result)

        // check sydney input brisbane output
        result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(inputSydney, getTestCalendar(), sydney).getTime(), brisbane)
        Assertions.assertEquals(inputSydney, expectedBrisbane, result)

        // DST

        // check sydney input sydney output
        result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(inputSydney, getTestCalendarDST(), sydney).getTime(), sydney)
        Assertions.assertEquals(inputSydney, expectedSydney, result)

        // check sydney input perth output
        result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(inputSydney, getTestCalendarDST(), sydney).getTime(), perth)
        Assertions.assertEquals(inputSydney, expectedPerthDST, result)

        // check sydney input brisbane output
        result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(inputSydney, getTestCalendarDST(), sydney).getTime(), brisbane)
        Assertions.assertEquals(inputSydney, expectedBrisbaneDST, result)
    }

    private Calendar getTestCalendar() {
        Calendar cal = Calendar.getInstance(sydney)
        cal.clear()
        cal.set(Calendar.YEAR, 2013)
        cal.set(Calendar.MONTH, Calendar.JULY)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal
    }

    private Calendar getTestCalendarDST() {
        Calendar cal = Calendar.getInstance(sydney)
        cal.clear()
        cal.set(Calendar.YEAR, 2013)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal
    }
}