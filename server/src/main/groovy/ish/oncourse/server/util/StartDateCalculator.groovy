/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.util

import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.SessionModule

class StartDateCalculator implements DateCalculator{
    @Override
    Date getDateIfNoSessions(Enrolment enrolment) {
        return enrolment.createdOn
    }

    @Override
    List<Date> getSessionDates(List<SessionModule> sessionModules) {
        return sessionModules*.session*.startDatetime
    }

    @Override
    Date getDateOf(CourseClass courseClass) {
        return courseClass.startDateTime;
    }

    @Override
    Date getRequiredOfSorted(List<Date> moduleDates) {
        return moduleDates.first()
    }
}
