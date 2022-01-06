/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.util


import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.SessionModule

class AttendanceTakenProcessor extends AttendanceProcessor{
    @Override
    List<Date> getAssessmentDueDates(CourseClass courseClass, Outcome outcome) {
        def assessmentClassModules = getAssessmentClassModulesOf(courseClass, outcome)
        List<Date> submissionDates = (assessmentClassModules*.assessmentClass*.assessmentSubmissions.flatten() as List<AssessmentSubmission>)
                .findAll { outcome.enrolment == it.enrolment }*.submittedDate
        if (!submissionDates.isEmpty()) {
            return submissionDates
        }

        return getDefaultDatesOf(assessmentClassModules)
    }

    @Override
    List<SessionModule> getModulesOf(CourseClass courseClass, Outcome outcome) {
        def sessionModules =  getSessionModulesOf(courseClass, outcome)
        return sessionModules.findAll { !it.getAttendanceForOutcome(outcome)?.absent }
    }
}
