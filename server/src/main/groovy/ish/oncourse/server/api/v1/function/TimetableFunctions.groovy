/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.time.TimeCategory
import groovy.transform.CompileDynamic
import ish.oncourse.server.api.dao.SessionDao
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.cayenne.Session
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression

import java.time.LocalDateTime


// Due to their dynamic nature, usage of categories is not possible with static type checking active
@CompileDynamic
class TimetableFunctions {

    /**
     *          s1                   ------|---                             |            END_DATETIME.between(from, to)
     *          s2                         |     -----------------          |            END_DATETIME.between(from, to) || START_DATETIME.between(from, to)
     *          s3                         |                         -------|-----       START_DATETIME.between(from, to)
     *          s4                       --|--------------------------------|---         START_DATETIME <= from && END_DATETIME => to
     *                                     |                                |
     *          timeline ------------------|--------------------------------|----------------------
     *                                    /                                /
     *                                 from                              to
     * @return Expression
     */
    static Expression getDateRangeExpression(LocalDateTime fromLocal, LocalDateTime toLocal) {
        Date from = null
        Date to = null

        use (TimeCategory) {
            from = fromLocal ? LocalDateUtils.timeValueToDate(fromLocal) : new Date().clearTime()
            to = toLocal ? LocalDateUtils.timeValueToDate(toLocal) : from + 60.days
        }

        return Session.END_DATETIME.between(from, to)
                .orExp(Session.START_DATETIME.between(from, to))
                .orExp(Session.START_DATETIME.lte(from).andExp(Session.END_DATETIME.gte(to)))

    }
    static SessionDTO toRestSession(Session session) {
        new SessionDTO().with {
            id = session.id
            classId = session.courseClass.id
            start = LocalDateUtils.dateToTimeValue(session.startDatetime)
            end = LocalDateUtils.dateToTimeValue(session.endDatetime)
            name = session.courseClass.course.name
            code = session.courseClass.uniqueCode
            room = session.room?.name
            site = session.room?.site?.name
            tutors = session.tutors*.contact*.fullName
            hasPaylines = session.payLines != null && !session.payLines.empty
            it
        }
    }

}
