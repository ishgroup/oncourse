/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util

import ish.IshTestCase
import static org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test

class DateTimeFormatterTest {

	private static HashMap<String, GregorianCalendar> dateList = new HashMap<>()
    private static HashMap<String, String> dateList2 = new HashMap<>()

	@BeforeClass
    static void setUp() throws Exception {
        int currentYear = new GregorianCalendar().get(GregorianCalendar.YEAR)
        int currentMonth = new GregorianCalendar().get(GregorianCalendar.MONTH)
        int currentDate = new GregorianCalendar().get(GregorianCalendar.DATE)

        dateList.put("2/10/2004 11:24 AM", new GregorianCalendar(2004, GregorianCalendar.OCTOBER, 2, 11, 24, 0))
        dateList.put("2/10/04 11:24 AM", new GregorianCalendar(2004, GregorianCalendar.OCTOBER, 2, 11, 24, 0))
        dateList.put("02-10-04 9pm", new GregorianCalendar(2004, GregorianCalendar.OCTOBER, 2, 21, 0, 0))
        dateList.put("02.10.04 9:00 PM", new GregorianCalendar(2004, GregorianCalendar.OCTOBER, 2, 21, 0, 0))
        dateList.put("03/03", new GregorianCalendar(currentYear, GregorianCalendar.MARCH, 3, 0, 0, 0))
        dateList.put("+10d", new GregorianCalendar(currentYear, currentMonth, currentDate + 10, 0, 0, 0))
        dateList.put("+10d 08:05 AM", new GregorianCalendar(currentYear, currentMonth, currentDate + 10, 8, 5, 0))
        dateList.put("+10d 08:05 PM", new GregorianCalendar(currentYear, currentMonth, currentDate + 10, 20, 5, 0))
        dateList.put("+10d 08:05", new GregorianCalendar(currentYear, currentMonth, currentDate + 10, 8, 5, 0))

        dateList2.put("2/10/04 11:24 AM", "Sat 2 Oct 2004 11:24am")
        dateList2.put("2/10/2004 11:24 AM", "Sat 2 Oct 2004 11:24am")
        dateList2.put("2/10/04 11:24", "Sat 2 Oct 2004 11:24am")
        dateList2.put("2/10/2004 11:24AM", "Sat 2 Oct 2004 11:24am")

        dateList2.put("2/10/04 11:24 AM", "Sat 2 Oct 2004 11:24am")
        dateList2.put("2\\10\\04 11:24 AM", "Sat 2 Oct 2004 11:24am")
        dateList2.put("2.10.04 11:24 AM", "Sat 2 Oct 2004 11:24am")
        dateList2.put("2,10,04 11:24 AM", "Sat 2 Oct 2004 11:24am")
        dateList2.put("2-10-04 11:24 AM", "Sat 2 Oct 2004 11:24am")

        dateList2.put("2/10/04", "Sat 2 Oct 2004 12am")

        dateList2.put("2/5/06 3:30 p", "Tue 2 May 2006 3:30pm")
        dateList2.put("2/5/06 15:30 p", "Tue 2 May 2006 3:30pm")

        dateList2.put("2/5/06 15:30:21 p", "Tue 2 May 2006 3:30pm")

        dateList2.put("Sun 18 Feb 2007 6:30pm", "Sun 18 Feb 2007 6:30pm")
        dateList2.put("Wed 18 Feb 2007 6:30pm", "Sun 18 Feb 2007 6:30pm")
        dateList2.put("Xxx 18 Feb 2007 6:30pm", "Sun 18 Feb 2007 6:30pm")
        dateList2.put("Sun 18 Feb 2007 6:30 pm", "Sun 18 Feb 2007 6:30pm")
        dateList2.put("Sun 18 Feb 2007 6:30 A", "Sun 18 Feb 2007 6:30am")
        dateList2.put("Sun 18 Feb 2007 6:30x", "Sun 18 Feb 2007 6:30am")
        dateList2.put("Sun 18 Feb 2007 6:30pmm", "Sun 18 Feb 2007 6:30pm")
    }

	@Test
    void testStringToValue() throws Exception {
		for (Map.Entry<String, GregorianCalendar> entry : dateList.entrySet()) {
			GregorianCalendar correctResult = entry.getValue()
            String stringInput = entry.getKey()

            Date d = DateTimeFormatter.parseDateTime(stringInput, TimeZone.getDefault())
            GregorianCalendar result = new GregorianCalendar()
            result.setTime(d)

            assertEquals(stringInput + " year", correctResult.get(GregorianCalendar.YEAR), result.get(GregorianCalendar.YEAR))
            assertEquals(stringInput + " month", correctResult.get(GregorianCalendar.MONTH), result.get(GregorianCalendar.MONTH))
            assertEquals(stringInput + " day", correctResult.get(GregorianCalendar.DAY_OF_MONTH), result.get(GregorianCalendar.DAY_OF_MONTH))

            assertEquals(stringInput + " hour", correctResult.get(GregorianCalendar.HOUR), result.get(GregorianCalendar.HOUR))
            assertEquals(stringInput + " minute", correctResult.get(GregorianCalendar.MINUTE), result.get(GregorianCalendar.MINUTE))
            assertEquals(stringInput + " am/pm", correctResult.get(GregorianCalendar.AM_PM), result.get(GregorianCalendar.AM_PM))
        }

	}

	@Test
    void testValueToString() {
		DateTimeFormatter instance = new DateTimeFormatter(TimeZone.getDefault())

        GregorianCalendar gc = new GregorianCalendar()
        Date d = new Date()
        gc.setTime(d)
        gc.set(GregorianCalendar.YEAR, 2004)
        gc.set(GregorianCalendar.MONTH, GregorianCalendar.OCTOBER)
        gc.set(GregorianCalendar.DAY_OF_MONTH, 1)
        gc.set(GregorianCalendar.HOUR_OF_DAY, 11)
        gc.set(GregorianCalendar.MINUTE, 20)
        gc.set(GregorianCalendar.SECOND, 30)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        d = gc.getTime()

        String expResult = "Fri 1 Oct 2004 11:20am"
        String result = instance.valueToString(d)
        assertEquals(expResult, result)
    }

	@Test
    void testValueToString2() {
		DateTimeFormatter instance = new DateTimeFormatter(TimeZone.getDefault())

        GregorianCalendar gc = new GregorianCalendar()
        Date d = new Date()
        gc.setTime(d)
        gc.set(GregorianCalendar.YEAR, 1993)
        gc.set(GregorianCalendar.MONTH, GregorianCalendar.JUNE)
        gc.set(GregorianCalendar.DAY_OF_MONTH, 13)
        gc.set(GregorianCalendar.HOUR_OF_DAY, 21)
        gc.set(GregorianCalendar.MINUTE, 7)
        gc.set(GregorianCalendar.SECOND, 0)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        d = gc.getTime()

        String expResult = "Sun 13 Jun 1993 9:07pm"
        String result = instance.valueToString(d)
        assertEquals(expResult, result)
    }

	@Test
    void testValueToString3() {
		DateTimeFormatter instance = new DateTimeFormatter(TimeZone.getDefault())

        GregorianCalendar gc = new GregorianCalendar()
        Date d = new Date()
        gc.setTime(d)
        gc.set(GregorianCalendar.YEAR, 2008)
        gc.set(GregorianCalendar.MONTH, GregorianCalendar.JANUARY)
        gc.set(GregorianCalendar.DAY_OF_MONTH, 1)
        gc.set(GregorianCalendar.HOUR_OF_DAY, 1)
        gc.set(GregorianCalendar.MINUTE, 1)
        gc.set(GregorianCalendar.SECOND, 0)
        gc.set(GregorianCalendar.MILLISECOND, 0)
        d = gc.getTime()

        String expResult = "Tue 1 Jan 2008 1:01am"
        String result = instance.valueToString(d)
        assertEquals(expResult, result)
    }

	@Test
    void testBoth() throws Exception {
		for (Map.Entry<String, String> entry : dateList2.entrySet()) {
			String correctResult = entry.getValue()
            String stringInput = entry.getKey()

            String result = DateTimeFormatter.formatDateTime(DateTimeFormatter.parseDateTime(stringInput, TimeZone.getDefault()), TimeZone.getDefault())

            assertEquals(stringInput, correctResult, result)
        }
	}

	@Test
    void testDaylightSavingsDateTime() {
		String dateTimeInput = "6/4/2008 11pm"
        Calendar cal = Calendar.getInstance()
        cal.setTime(DateTimeFormatter.parseDateTime(dateTimeInput, TimeZone.getDefault()))
        assertEquals("DateTime parsed wrong", 23, cal.get(Calendar.HOUR_OF_DAY))
    }
}
