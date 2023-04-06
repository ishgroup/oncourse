/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.util

import groovy.time.TimeCategory
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.SessionModule


class EndDateCalculator implements DateCalculator{
    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    Date getDateIfNoSessions(Enrolment enrolment) {
        def courseClass = enrolment.courseClass
        Date endDate
        use(TimeCategory) {
            endDate = courseClass.maximumDays ? enrolment.createdOn + courseClass.maximumDays.day : enrolment.createdOn + 365.day
        }
        return endDate
    }

    @Override
    List<Date> getSessionDates(List<SessionModule> sessionModules) {
        return sessionModules*.session*.endDatetime
    }

    @Override
    Date getDateOf(CourseClass courseClass) {
        return courseClass.endDateTime
    }

    @Override
    Date getRequiredOfSorted(List<Date> moduleDates) {
        return moduleDates.last()
    }
}
