package ish.oncourse.solr.functions.course

import org.junit.Assert
import org.junit.Test

import java.time.Month
import java.time.ZonedDateTime

/**
 * User: akoiro
 * Date: 16/11/17
 */
class DateFunctionsTest {

    static {
        System.setProperty("user.timezone", "Europe/Minsk") // UTC +3
    }
    
    @Test
    void test_toDateTime() {
        Date date = DateFunctions.toDate("2017-10-23 20:00:00")
        ZonedDateTime dateTime = DateFunctions.toDateTime(date, "Australia/Perth") // UTC +8
        Assert.assertEquals(2017, dateTime.getYear())
        Assert.assertEquals(Month.OCTOBER, dateTime.getMonth())
        Assert.assertEquals(24, dateTime.getDayOfMonth())
        Assert.assertEquals(1, dateTime.getHour())
        Assert.assertEquals(0, dateTime.getMinute())
        Assert.assertEquals(0, dateTime.getSecond())
    }

    @Test
    void test_toTimeZone() {
        Date date = DateFunctions.toDate("2017-10-23 20:00:00")
        Calendar calendar = Calendar.instance
        calendar.time = DateFunctions.toTimeZone(date, "Australia/Perth") // UTC +8
        Assert.assertEquals(2017, calendar.get(Calendar.YEAR))
        Assert.assertEquals(Calendar.OCTOBER, calendar.get(Calendar.MONTH))
        Assert.assertEquals(24, calendar.get(Calendar.DAY_OF_MONTH))
        Assert.assertEquals(1, calendar.get(Calendar.HOUR_OF_DAY))
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE))
        Assert.assertEquals(0, calendar.get(Calendar.SECOND))
    }

}
