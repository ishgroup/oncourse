package ish.oncourse.solr.functions.course

import ish.oncourse.model.Session
import ish.oncourse.solr.model.SSession
import org.apache.commons.lang3.text.WordUtils

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * User: akoiro
 * Date: 24/10/17
 */
class SessionFunctions {

    private static final int MORNING_TIME = 6
    private static final int EVENING_TIME = 17

    static ZonedDateTime convertToDateTime(Date date, String timeZone) {
        Instant instant = date.toInstant()
        ZonedDateTime dtSydney = ZonedDateTime.ofInstant(instant, ZoneId.of('Australia/Sydney'))
        return timeZone == null ? dtSydney : dtSydney.withZoneSameInstant(ZoneId.of(timeZone))
    }


    static String getDayName(Session session) {
        WordUtils.capitalizeFully(convertToDateTime(session.startDate, session.timeZone).getDayOfWeek().name())
    }

    static String getDayType(Session session) {
        switch (convertToDateTime(session.startDate, session.timeZone).getDayOfWeek()) {
            case DayOfWeek.MONDAY:
            case DayOfWeek.TUESDAY:
            case DayOfWeek.WEDNESDAY:
            case DayOfWeek.THURSDAY:
            case DayOfWeek.FRIDAY:
                return "weekday"
            case DayOfWeek.SATURDAY:
            case DayOfWeek.SUNDAY:
                return "weekend"
            default:
                throw new IllegalAccessException()
        }
    }

    static String getDayTime(Session session) {
        int time = convertToDateTime(session.startDate, session.timeZone).hour
        return time > MORNING_TIME && time < EVENING_TIME ? "daytime" : "evening"
    }


    static SSession getSSession(Session session) {
        new SSession().with {
            it.dayName = getDayName(session)
            it.dayTime = getDayTime(session)
            it.dayType = getDayType(session)
            it
        }
    }
}
