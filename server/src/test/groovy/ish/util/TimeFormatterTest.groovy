/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@CompileStatic
class TimeFormatterTest {

    private static HashMap<String, GregorianCalendar> dateList = new HashMap<>()

    private static HashMap<String, String> dateList2 = new HashMap<>()

    @BeforeAll
    static void setUp() throws Exception {
        int currentYear = new GregorianCalendar().get(GregorianCalendar.YEAR)
        int currentMonth = new GregorianCalendar().get(GregorianCalendar.MONTH)
        int currentDate = new GregorianCalendar().get(GregorianCalendar.DATE)

        dateList.put("11:20 AM", new GregorianCalendar(currentYear, currentMonth, currentDate, 11, 20, 0))
        dateList.put("11:20", new GregorianCalendar(currentYear, currentMonth, currentDate, 11, 20, 0))
        dateList.put("11:20 PM", new GregorianCalendar(currentYear, currentMonth, currentDate, 23, 20, 0))
        dateList.put("11.20 AM", new GregorianCalendar(currentYear, currentMonth, currentDate, 11, 20, 0))
        dateList.put("11.20", new GregorianCalendar(currentYear, currentMonth, currentDate, 11, 20, 0))
        dateList.put("11.20 PM", new GregorianCalendar(currentYear, currentMonth, currentDate, 23, 20, 0))
        dateList.put("5p", new GregorianCalendar(currentYear, currentMonth, currentDate, 17, 0, 0))
        dateList.put("5", new GregorianCalendar(currentYear, currentMonth, currentDate, 5, 0, 0))
        dateList.put("5a", new GregorianCalendar(currentYear, currentMonth, currentDate, 5, 0, 0))
        dateList.put("12 p", new GregorianCalendar(currentYear, currentMonth, currentDate, 12, 0, 0))
        dateList.put("12 a", new GregorianCalendar(currentYear, currentMonth, currentDate, 0, 0, 0))
        dateList.put("12", new GregorianCalendar(currentYear, currentMonth, currentDate, 0, 0, 0))
        dateList.put("25", new GregorianCalendar(currentYear, currentMonth, currentDate, 1, 0, 0))

        dateList.put("12:00", new GregorianCalendar(currentYear, currentMonth, currentDate, 0, 0, 0))
        dateList.put("12:00 AM", new GregorianCalendar(currentYear, currentMonth, currentDate, 0, 0, 0))
        dateList.put("12:00 PM", new GregorianCalendar(currentYear, currentMonth, currentDate, 12, 0, 0))
        dateList.put("24:00", new GregorianCalendar(currentYear, currentMonth, currentDate + 1, 0, 0, 0))

        dateList2.put("11:20 AM", "11:20am")
        dateList2.put("11:20", "11:20am")
        dateList2.put("11:20 PM", "11:20pm")
        dateList2.put("11:20p", "11:20pm")
        dateList2.put("11:20pm", "11:20pm")
        dateList2.put("11:20  am", "11:20am")

        dateList2.put("11.20 AM", "11:20am")
        dateList2.put("11.20", "11:20am")
        dateList2.put("11.20 PM", "11:20pm")
        dateList2.put("11.20p", "11:20pm")
        dateList2.put("11.20 pm", "11:20pm")
        dateList2.put("11.20 am", "11:20am")

        dateList2.put("5", "5am")
        dateList2.put("5p", "5pm")
        dateList2.put("5a", "5am")

        dateList2.put("12", "12am")
        dateList2.put("0", "12am")
        dateList2.put("00", "12am")

        dateList2.put("12p", "12pm")
        dateList2.put("12Pm", "12pm")

        dateList2.put("12:00 AM", "12am")
        dateList2.put("12:00 PM", "12pm")
        dateList2.put("12:00", "12am")

        dateList2.put("23:00", "11pm")
        dateList2.put("24:00", "12am")
    }

    @Test
    void testFormatTime() {
        GregorianCalendar gc = new GregorianCalendar()
        Date d = new Date()

        gc.setTime(d)
        gc.set(GregorianCalendar.HOUR_OF_DAY, 11)
        gc.set(GregorianCalendar.MINUTE, 20)
        gc.set(GregorianCalendar.SECOND, 30)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        d = gc.getTime()
        Assertions.assertEquals("11:20am", TimeFormatter.formatTime(d, TimeZone.getDefault()))

        gc.set(GregorianCalendar.HOUR_OF_DAY, 6)
        gc.set(GregorianCalendar.MINUTE, 45)
        gc.set(GregorianCalendar.SECOND, 0)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        d = gc.getTime()
        Assertions.assertEquals("6:45am", TimeFormatter.formatTime(d, TimeZone.getDefault()))

        gc.set(GregorianCalendar.HOUR_OF_DAY, 21)
        gc.set(GregorianCalendar.MINUTE, 8)
        gc.set(GregorianCalendar.SECOND, 0)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        d = gc.getTime()
        Assertions.assertEquals("9:08pm", TimeFormatter.formatTime(d, TimeZone.getDefault()))

        gc.set(GregorianCalendar.HOUR_OF_DAY, 12)
        gc.set(GregorianCalendar.MINUTE, 0)
        gc.set(GregorianCalendar.SECOND, 0)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        d = gc.getTime()
        Assertions.assertEquals("12pm", TimeFormatter.formatTime(d, TimeZone.getDefault()))

        gc.set(GregorianCalendar.HOUR_OF_DAY, 0)
        gc.set(GregorianCalendar.MINUTE, 0)
        gc.set(GregorianCalendar.SECOND, 0)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        d = gc.getTime()
        Assertions.assertEquals("12am", TimeFormatter.formatTime(d, TimeZone.getDefault()))

        gc.set(GregorianCalendar.HOUR, 10)
        gc.set(GregorianCalendar.MINUTE, 2)
        gc.set(GregorianCalendar.SECOND, 0)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        gc.set(GregorianCalendar.AM_PM, GregorianCalendar.AM)
        d = gc.getTime()
        Assertions.assertEquals("10:02am", TimeFormatter.formatTime(d, TimeZone.getDefault()))

        // HOUR = 21 ... this is just wrong. so the test is checking for failure
        // HOUR_OF_DAY = 21 would be ok. (see testFormatTime3)
        gc.set(GregorianCalendar.HOUR, 21)
        gc.set(GregorianCalendar.MINUTE, 35)
        gc.set(GregorianCalendar.SECOND, 0)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        d = gc.getTime()
        Assertions.assertNotSame("9:35pm", TimeFormatter.formatTime(d, TimeZone.getDefault()))
    }

    @Test
    void testParseTime() throws Exception {
        for (Map.Entry<String, GregorianCalendar> entry : dateList.entrySet()) {
            GregorianCalendar correctResult = entry.getValue()
            String stringInput = entry.getKey()

            Date d = TimeFormatter.parseTime(stringInput, TimeZone.getDefault())
            GregorianCalendar result = new GregorianCalendar()
            result.setTime(d)

            Assertions.assertEquals(correctResult.get(GregorianCalendar.HOUR_OF_DAY), result.get(GregorianCalendar.HOUR_OF_DAY), stringInput + " hour of day")
            Assertions.assertEquals(correctResult.get(GregorianCalendar.HOUR), result.get(GregorianCalendar.HOUR), stringInput + " hour")
            Assertions.assertEquals(correctResult.get(GregorianCalendar.MINUTE), result.get(GregorianCalendar.MINUTE), stringInput + " minute")
            Assertions.assertEquals(correctResult.get(GregorianCalendar.AM_PM), result.get(GregorianCalendar.AM_PM), stringInput + " AM/PM")
        }
    }

    @Test
    void testBoth() throws Exception {
        for (Map.Entry<String, String> entry : dateList2.entrySet()) {
            String correctResult = entry.getValue()
            String stringInput = entry.getKey()

            String result = TimeFormatter.formatTime(TimeFormatter.parseTime(stringInput, TimeZone.getDefault()), TimeZone.getDefault())
            Assertions.assertEquals(correctResult, correctResult, result)
        }
    }

    @Test
    void testBoth2() throws Exception {
        for (Map.Entry<String, GregorianCalendar> entry : dateList.entrySet()) {
            GregorianCalendar correctResult = entry.getValue()

            Date d = TimeFormatter.parseTime(TimeFormatter.formatTime(correctResult.getTime(), TimeZone.getDefault()), TimeZone.getDefault())
            GregorianCalendar result = new GregorianCalendar()
            result.setTime(d)

            Assertions.assertEquals(correctResult.get(GregorianCalendar.HOUR_OF_DAY), result.get(GregorianCalendar.HOUR_OF_DAY),
                    "hour of the day -> " + correctResult.toString())
            Assertions.assertEquals(correctResult.get(GregorianCalendar.HOUR), result.get(GregorianCalendar.HOUR), "hour -> " + correctResult.toString())
            Assertions.assertEquals(correctResult.get(GregorianCalendar.MINUTE), result.get(GregorianCalendar.MINUTE), "minute -> " + correctResult.toString())
            Assertions.assertEquals(correctResult.get(GregorianCalendar.AM_PM), result.get(GregorianCalendar.AM_PM), "am/pm -> " + correctResult.toString())
        }
    }

    @Test
    void testDaylightSavingsTime() {
        String dateTimeInput = "6/4/2008"
        Calendar dateCal = Calendar.getInstance()
        dateCal.setTime(DateFormatter.parseDate(dateTimeInput, TimeZone.getDefault()))

        String timeInput = "11pm"
        Calendar cal = TimeFormatter.parseTimeToCal(timeInput, dateCal, TimeZone.getDefault())
        Assertions.assertEquals(23, cal.get(Calendar.HOUR_OF_DAY), "Daylight savings")
    }

}
