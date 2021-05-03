/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.text.ParseException

@CompileStatic
class DurationFormatterTest {


    static Collection<Arguments> values() {
        Object[][] data = [
                ["15min", "15min", "15min", "15min", "15", "0.25", "0", "0", "15"],     //0

                ["0d 1h 15min", "1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"],
                ["1h 15min", "1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"],

                ["0d 0h 75min", "1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"],
                ["0h 75min", "1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"],
                ["75min", "1h 15min", "1h 15min", "75min", "75", "1.25", "0", "1", "15"], //5

                ["2h 15min", "2h 15min", "2h 15min", "135min", "135", "2.25", "0", "2", "15"],
                ["1h 75min", "2h 15min", "2h 15min", "135min", "135", "2.25", "0", "2", "15"],
                ["0h 135min", "2h 15min", "2h 15min", "135min", "135", "2.25", "0", "2", "15"],

                ["1", "1h 0min", "1h 0min", "60min", "60", "1.00", "0", "1", "0"],
                ["1d", "1d 0h 0min", "24h 0min", "1440min", "1440", "24.00", "1", "0", "0"],//10
                ["1h", "1h 0min", "1h 0min", "60min", "60", "1.00", "0", "1", "0"],
                ["1m", "1min", "1min", "1min", "1", "0.02", "0", "0", "1"],

                ["1 1", "1h 1min", "1h 1min", "61min", "61", "1.02", "0", "1", "1"],
                // line below is a bug
                //["1d 1h",          ["1d 1h 0min", "25h 0min", "1500min", "1500", "25.00", "1", "1", "0"]],
                ["1d 1m", "1h 1min", "1h 1min", "61min", "61", "1.02", "0", "1", "1"],     //15
                ["1h 1m", "1h 1min", "1h 1min", "61min", "61", "1.02", "0", "1", "1"],

                ["1 1 1", "1d 1h 1min", "25h 1min", "1501min", "1501", "25.02", "1", "1", "1"],
                ["2d 1h 15min", "2d 1h 15min", "49h 15min", "2955min", "2955", "49.25", "2", "1", "15"],
        ]

        Collection<Arguments> dataList = new ArrayList<>()
        for (Object[] test : data) {
            dataList.add(Arguments.of(test[0], test[1], test[2], test[3], test[4], test[5], test[6], test[7]))
        }
        return dataList
    }

    @ParameterizedTest
    @MethodSource("values")
    void durationTests(String input, String outputDDHHMM, outputHHMM, outputMM, lengthInMinutes, lengthInHours, String days, String hours, String minutes ) throws ParseException {
        long time = DurationFormatter.parseDuration(input)

        int[] duration = DurationFormatter.splitMillisecondsIntoDaysHoursMinutes(time)
        Assertions.assertEquals(days, duration[0], input)
        Assertions.assertEquals(hours, duration[1], input)
        Assertions.assertEquals(minutes, duration[2], input)


        String result = DurationFormatter.formatDuration(DurationFormatter.parseDuration(input), DurationFormatter.FORMAT_DD_HH_MM)
        Assertions.assertEquals(outputDDHHMM, result, input)


        result = DurationFormatter.formatDuration(DurationFormatter.parseDuration(input), DurationFormatter.FORMAT_HH_MM)
        Assertions.assertEquals(outputHHMM, result, input)


        result = DurationFormatter.formatDuration(DurationFormatter.parseDuration(input), DurationFormatter.FORMAT_MM)
        Assertions.assertEquals(outputMM, result, input)


        Integer resultInt = DurationFormatter.parseDurationInMinutes(DurationFormatter.parseDuration(input))
        Assertions.assertEquals(lengthInMinutes, resultInt, input)


        BigDecimal resultDec = DurationFormatter.parseDurationInHours(DurationFormatter.parseDuration(input))
        Assertions.assertEquals(lengthInHours, resultDec, input)
    }
}
