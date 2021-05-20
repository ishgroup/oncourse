/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services


import groovy.transform.CompileStatic
import org.jfree.data.time.Day
import org.jfree.data.time.TimeSeries
import org.jfree.data.time.TimeSeriesCollection
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class StatsServiceTest {

    /**
     * plot is saved as a png file in the current working directory, allowing to test plotting itself.
     */
    @Test
    void testSparkLine() {

        Calendar calendar = GregorianCalendar.getInstance()
        Random r = new Random()

        TimeSeriesCollection dataset = new TimeSeriesCollection()
        Day d = new Day()
        TimeSeries data = new TimeSeries("enrolments" as Comparable, d.getClass())

        for (int i = 0; i < 28; i++) {
            calendar.add(Calendar.DATE, -1)

            data.add(new Day(calendar.getTime()), r.nextInt(100))
        }
        dataset.addSeries(data)

        File f = new File("sparklineTest.png")
        if (f.exists()) {
            f.delete()
        }

        byte[] result = StatsService.graphSparkline(dataset)

        Assertions.assertNotNull(result)
        Assertions.assertTrue(result.length > 0)

        try {
            OutputStream out = new FileOutputStream("sparklineTest.png")
            out.write(result)
            out.close()
        } catch (IOException ignored) {
            Assertions.fail("Could not write file.")
        }

        File f2 = new File("sparklineTest.png")
        Assertions.assertTrue(f2.exists())
        Assertions.assertTrue(f2.length() > 0)

        //clean up
        f2.delete()
    }

}
