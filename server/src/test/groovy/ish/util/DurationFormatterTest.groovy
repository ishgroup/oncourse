/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.text.ParseException

import static junit.framework.Assert.assertEquals

@RunWith(Parameterized.class)
class DurationFormatterTest {

    private String input
    private String outputDDHHMM
    private String outputHHMM
    private String outputMM
    private Integer lengthInMinutes
    private BigDecimal lengthInHours
    private int days
    private int hours
    private int minutes


    DurationFormatterTest(String input, String[] outputs) {

        this.input = input
        this.outputDDHHMM = outputs[0]
        this.outputHHMM = outputs[1]
        this.outputMM = outputs[2]
        this.lengthInMinutes = Integer.valueOf(outputs[3])
        this.lengthInHours = new BigDecimal(outputs[4])
        this.days = Integer.valueOf(outputs[5])
        this.hours = Integer.valueOf(outputs[6])
        this.minutes = Integer.valueOf(outputs[7])
    }

    @Parameterized.Parameters
    static Collection<Object[]> setUp() {
        Object[][] data = [
                ["15min", ["15min", "15min", "15min", "15", "0.25", "0", "0", "15"]],     //0

                ["0d 1h 15min", ["1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"]],
                ["1h 15min", ["1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"]],

                ["0d 0h 75min", ["1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"]],
                ["0h 75min", ["1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"]],
                ["75min", ["1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"]], //5

                ["2h 15min", ["2h 15min", "2h 15min", "135min", "135", "2.25", "0", "2", "15"]],
                ["1h 75min", ["2h 15min", "2h 15min", "135min", "135", "2.25", "0", "2", "15"]],
                ["0h 135min", ["2h 15min", "2h 15min", "135min", "135", "2.25", "0", "2", "15"]],

                ["1", ["1h 0min", "1h 0min", "60min", "60", "1.00", "0", "1", "0"]],
                ["1d", ["1d 0h 0min", "24h 0min", "1440min", "1440", "24.00", "1", "0", "0"]],//10
                ["1h", ["1h 0min", "1h 0min", "60min", "60", "1.00", "0", "1", "0"]],
                ["1m", ["1min", "1min", "1min", "1", "0.02", "0", "0", "1"]],

                ["1 1", ["1h 1min", "1h 1min", "61min", "61", "1.02", "0", "1", "1"]],
                // line below is a bug
                //["1d 1h",          ["1d 1h 0min", "25h 0min", "1500min", "1500", "25.00", "1", "1", "0"]],
                ["1d 1m", ["1h 1min", "1h 1min", "61min", "61", "1.02", "0", "1", "1"]],     //15
                ["1h 1m", ["1h 1min", "1h 1min", "61min", "61", "1.02", "0", "1", "1"]],

                ["1 1 1", ["1d 1h 1min", "25h 1min", "1501min", "1501", "25.02", "1", "1", "1"]],

                ["2d 1h 15min", ["2d 1h 15min", "49h 15min", "2955min", "2955", "49.25", "2", "1", "15"]],
        ]

        Collection<Object[]> dataList = new ArrayList<>()
        for(int i = 0; i < data.length; i++) {
            dataList.add([data[i][0] as String, data[i][1] as String[]] as Object[])
        }
        return dataList
    }

    @Test
    void testSplitMillisecondsIntoDaysHoursMinutes() throws ParseException {
        long time = DurationFormatter.parseDuration(input)

        int[] duration = DurationFormatter.splitMillisecondsIntoDaysHoursMinutes(time)
        assertEquals(input, days, duration[0])
        assertEquals(input, hours, duration[1])
        assertEquals(input, minutes, duration[2])
    }

    @Test
    void testFormatterDDHHMM() throws ParseException {
        String result = DurationFormatter.formatDuration(DurationFormatter.parseDuration(input), DurationFormatter.FORMAT_DD_HH_MM)
        assertEquals(input, outputDDHHMM, result)
    }

    @Test
    void testFormatterHHMM() throws ParseException {
        String result = DurationFormatter.formatDuration(DurationFormatter.parseDuration(input), DurationFormatter.FORMAT_HH_MM)
        assertEquals(input, outputHHMM, result)
    }

    @Test
    void testFormatterMM() throws ParseException {
        String result = DurationFormatter.formatDuration(DurationFormatter.parseDuration(input), DurationFormatter.FORMAT_MM)
        assertEquals(input, outputMM, result)
    }

    @Test
    void testParseDurationInMinutes() throws ParseException {
        Integer result = DurationFormatter.parseDurationInMinutes(DurationFormatter.parseDuration(input))

        assertEquals(input, lengthInMinutes, result)
    }

    @Test
    void testParseDurationInHours() throws ParseException {
        BigDecimal result = DurationFormatter.parseDurationInHours(DurationFormatter.parseDuration(input))

        assertEquals(input, lengthInHours, result)
    }
}
