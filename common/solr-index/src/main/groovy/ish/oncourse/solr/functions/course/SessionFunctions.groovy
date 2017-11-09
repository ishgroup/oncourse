package ish.oncourse.solr.functions.course

import ish.oncourse.model.Session
import ish.oncourse.solr.model.SSession

/**
 * User: akoiro
 * Date: 24/10/17
 */
class SessionFunctions {

    private static final int SATURDAY_INDEX = 6
    private static final int MORNING_TIME = 6
    private static final int EVENING_TIME = 17


    static String getDayName(Session session) {
        session.startDate.format("EEEE")
    }

    static String getDayType(Session session) {
        session.startDate.format("u").toInteger() < SATURDAY_INDEX ? "weekday" : "weekend"
    }

    static String getDayTime(Session session) {
        int time = session.startDate.format("H").toInteger()
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
