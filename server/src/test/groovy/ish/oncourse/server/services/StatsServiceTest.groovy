/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jfree.data.time.Day
import org.jfree.data.time.TimeSeries
import org.jfree.data.time.TimeSeriesCollection
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 */
class StatsServiceTest {
	private static final Logger logger = LogManager.getLogger()

	/**
	 * plot is saved as a png file in the current working directory, allowing to test plotting itself.<br/>
	 * cannot be used on headless machines, as the plot requires graphic env.
	 */
	@Test
	void testSparkLine() {

		Calendar calendar = GregorianCalendar.getInstance()
		Random r = new Random()

		TimeSeriesCollection dataset = new TimeSeriesCollection()
		Day d = new Day()
		TimeSeries data = new TimeSeries("enrolments", d.getClass())

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

		assertNotNull(result)
		assertTrue(result.length > 0)

		try {
			OutputStream out = new FileOutputStream("sparklineTest.png")

			out.write(result)
			out.close()
		} catch (IOException e) {
			logger.catching(e)
		}

		File f2 = new File("sparklineTest.png")
		assertTrue(f2.exists())
		assertTrue(f2.length() > 0)

	}

}
