package ish.oncourse.solr.functions.course

import ish.oncourse.model.Session
import ish.oncourse.solr.model.SSession
import org.apache.commons.lang3.text.WordUtils

import java.time.DayOfWeek

/**
 * User: akoiro
 * Date: 24/10/17
 */
class SessionFunctions {

    private static final int MORNING_TIME = 6
    private static final int EVENING_TIME = 17

    private static enum DayType {
        weekday, weekend
    }

    private static enum DayTime {
        daytime, evening
    }

    static String getDayName(Session session) {
        WordUtils.capitalizeFully(DateFunctions.toDateTime(session.startDate, session.timeZone).getDayOfWeek().name())
    }

    static String getDayType(Session session) {
        switch (DateFunctions.toDateTime(session.startDate, session.timeZone).getDayOfWeek()) {
            case DayOfWeek.MONDAY:
            case DayOfWeek.TUESDAY:
            case DayOfWeek.WEDNESDAY:
            case DayOfWeek.THURSDAY:
            case DayOfWeek.FRIDAY:
                return DayType.weekday.name()
            case DayOfWeek.SATURDAY:
            case DayOfWeek.SUNDAY:
                return DayType.weekend.name()
            default:
                throw new IllegalAccessException()
        }
    }

    static String getDayTime(Session session) {
        int time = DateFunctions.toDateTime(session.startDate, session.timeZone).hour
        return time > MORNING_TIME && time < EVENING_TIME ? DayTime.daytime.name() : DayTime.evening.name()
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
