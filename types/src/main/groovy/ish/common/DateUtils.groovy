/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common

import ish.oncourse.cayenne.AssessmentClassModuleInterface
import ish.oncourse.cayenne.AssessmentSubmissionInterface
import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.ModuleInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.cayenne.SessionModuleInterface

class DateUtils {

    static List<Date> getAssessmentDueDates(CourseClassInterface courseClass, OutcomeInterface outcome, boolean attendanceTakenIntoAccount) {
        List<AssessmentClassModuleInterface> assessmentClassModules = (courseClass.assessmentClasses*.assessmentClassModules
                .flatten() as List<AssessmentClassModuleInterface>)
                .findAll { acm -> acm.module == outcome.module }

        if (attendanceTakenIntoAccount) {
            List<Date> submissionDates = (assessmentClassModules*.assessmentClass*.assessmentSubmissions.flatten() as List<AssessmentSubmissionInterface>)
                    .findAll { outcome.enrolment == it.enrolment }*.submittedDate
            if (!submissionDates.isEmpty()) {
                return submissionDates
            }
        }

        return assessmentClassModules*.assessmentClass*.dueDate
    }

    static List<SessionModuleInterface> getModulesOf(CourseClassInterface courseClass, Boolean attendanceTakenIntoAccount,
                                                     ModuleInterface controlModule){
        def sessionModules =  (courseClass.sessions*.sessionModules
                .flatten() as List<SessionModuleInterface>)
                .findAll { sm -> sm.module == controlModule }
        if (attendanceTakenIntoAccount) {
            sessionModules = sessionModules.findAll { !it.getAttendanceForOutcome(this as OutcomeInterface)?.absent }
        }
        return sessionModules
    }
}
