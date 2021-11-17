/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common

import groovy.time.TimeCategory
import ish.common.payable.EnrolmentInterface
import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.SessionModuleInterface

class EndDateCalculator implements DateCalculator{
    @Override
    Date getDateIfNoSessions(EnrolmentInterface enrolment) {
        def courseClass = enrolment.courseClass
        Date endDate
        use(TimeCategory) {
            endDate = courseClass.maximumDays ? enrolment.createdOn + courseClass.maximumDays.day : enrolment.createdOn + 365.day
        }
        return endDate
    }

    @Override
    List<Date> getSessionDates(List<SessionModuleInterface> sessionModules) {
        return sessionModules*.session*.endDatetime
    }

    @Override
    Date getDateOf(CourseClassInterface courseClass) {
        return courseClass.endDateTime
    }
}
