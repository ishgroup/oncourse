package ish.oncourse.solr.functions.course

import java.text.SimpleDateFormat
import java.time.*

/**
 * User: akoiro
 * Date: 16/11/17
 */
class DateFunctions {

    public static final String DEFAULT_TIME_ZONE = 'Australia/Sydney'

    static ZonedDateTime toDateTime(Date date, String timeZone = DEFAULT_TIME_ZONE) {
        Instant instant = date.toInstant()
        ZonedDateTime dtSydney = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
        return timeZone == null ? dtSydney : dtSydney.withZoneSameInstant(ZoneId.of(timeZone))
    }

    static Date toTimeZone(Date date, String timeZone) {
        if (!timeZone) return date
        if (timeZone == DEFAULT_TIME_ZONE) return date
        Instant instant = date.toInstant()
        ZoneId zoneId = ZoneId.of(timeZone)
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(instant)
        return Date.from(LocalDateTime.ofInstant(instant, zoneId).toInstant(zoneOffset))
    }

    static Date toDate(LocalDate localDate, ZoneId zoneId = ZoneId.systemDefault()) {
        return Date.from(localDate.atStartOfDay(zoneId).toInstant())
    }

    static Date toDate(LocalDateTime localDate, ZoneId zoneId = ZoneId.systemDefault()) {
        return Date.from(localDate.atZone(zoneId).toInstant())
    }

    /**
     * format "2010-10-01 18:00:00"
     */
    static Date toDate(String mysqlDate, String timeZone = DEFAULT_TIME_ZONE) {
        SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone))
        return dateFormat.parse(mysqlDate)
    }
}
