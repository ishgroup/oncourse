/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util

import org.apache.commons.collections.CollectionUtils
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static org.junit.Assert.assertEquals

@RunWith(Parameterized.class)
class TimeFormatterTest2 {

	private String expectedSydney
    private String expectedPerth
    private String expectedPerthDST
    private String expectedBrisbane
    private String expectedBrisbaneDST
    private String inputSydney

    private static TimeZone sydney = TimeZone.getTimeZone("Australia/Sydney")
    private static TimeZone perth = TimeZone.getTimeZone("Australia/Perth")
    private static TimeZone brisbane = TimeZone.getTimeZone("Australia/Brisbane")

    TimeFormatterTest2(String inputSydney, String expectedSydney, String expectedPerth, String expectedPerthDST, String expectedBrisbane, String expectedBrisbaneDST) {
		this.inputSydney = inputSydney
        this.expectedSydney = expectedSydney
        this.expectedPerth = expectedPerth
        this.expectedPerthDST = expectedPerthDST
        this.expectedBrisbane = expectedBrisbane
        this.expectedBrisbaneDST = expectedBrisbaneDST
    }

	@Parameterized.Parameters
    static Collection<String[]> setUp() {
		def data =  [
				//input      sydney,    perth, perthDST, bris, brisDST
				[ "11",      "11am",    "9am", "8am",   "11am", "10am"],
				[ "11p",     "11pm",    "9pm", "8pm",   "11pm", "10pm"],
				[ "11pm",    "11pm",    "9pm", "8pm",   "11pm", "10pm"],
				[ "11a",     "11am",    "9am", "8am",   "11am", "10am"],
				[ "23",      "11pm",    "9pm", "8pm",   "11pm", "10pm"],

				[ "11:10",   "11:10am", "9:10am", "8:10am", "11:10am", "10:10am"],
				[ "11:10a",  "11:10am", "9:10am", "8:10am", "11:10am", "10:10am"],
				[ "11:10p",  "11:10pm", "9:10pm", "8:10pm", "11:10pm", "10:10pm"],
				[ "11:10AM", "11:10am", "9:10am", "8:10am", "11:10am", "10:10am"],
				[ "11:10 AM","11:10am", "9:10am", "8:10am", "11:10am", "10:10am"],
				[ "23:10",   "11:10pm", "9:10pm", "8:10pm", "11:10pm", "10:10pm"]

		] as String[][]

        Collection<String[]> dataList = new ArrayList<>()
        CollectionUtils.addAll(dataList, data)
        return dataList
    }

	@Test
    void parseAndFormat() throws Exception {
		// check sydney input sydney output
		String result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(this.inputSydney, getTestCalendar(), sydney).getTime(), sydney)
        assertEquals(this.inputSydney, this.expectedSydney, result)

        // check sydney input perth output
		result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(this.inputSydney, getTestCalendar(), sydney).getTime(), perth)
        assertEquals(this.inputSydney, this.expectedPerth, result)

        // check sydney input brisbane output
		result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(this.inputSydney, getTestCalendar(), sydney).getTime(), brisbane)
        assertEquals(this.inputSydney, this.expectedBrisbane, result)
    }


	@Test
    void parseAndFormatDST() throws Exception {
		// check sydney input sydney output
		String result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(this.inputSydney, getTestCalendarDST(), sydney).getTime(), sydney)
        assertEquals(this.inputSydney, this.expectedSydney, result)

        // check sydney input perth output
		result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(this.inputSydney, getTestCalendarDST(), sydney).getTime(), perth)
        assertEquals(this.inputSydney, this.expectedPerthDST, result)

        // check sydney input brisbane output
		result = TimeFormatter.formatTime(TimeFormatter.parseTimeToCal(this.inputSydney, getTestCalendarDST(), sydney).getTime(), brisbane)
        assertEquals(this.inputSydney, this.expectedBrisbaneDST, result)
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