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

package ish.oncourse.server.cayenne

import ish.oncourse.server.api.v1.function.OutcomeFunctions
import org.apache.commons.lang3.StringUtils

trait OutcomeTrait {

    abstract Enrolment getEnrolment()

    abstract PriorLearning getPriorLearning()

    abstract Module getModule()

    String getStudentName() {
        Contact contact = (enrolment) ? enrolment?.student?.contact : priorLearning?.student?.contact
        if (contact) {
            return contact.getFullName()
        } else {
            return StringUtils.EMPTY
        }
    }

    List<AssessmentClass> getAssessments() {
        if (module) {
            enrolment.courseClass.assessmentClasses.findAll { assessment -> module.id in assessment.modules*.id }
        } else {
            return enrolment.courseClass.assessmentClasses
        }
    }

    List<AssessmentSubmission> getSubmissions() {
        if (enrolment) {
            return assessments.collect { assessment -> assessment.getAssessmentSubmission(enrolment) }.grep()
        } else {
            return []
        }
    }

    Long getPresentAttendancePercent() {
        if (this instanceof Outcome) {
            def progression = OutcomeFunctions.getProgression((Outcome) this)
            return Math.round(progression.getAttended().doubleValue() / progression.getMaxAttendedHoursProgressValue().doubleValue() * 100)
        }
        throw new UnsupportedOperationException("Incorrect using of OutcomeTrait class; inheritor must be Outcome instance")
    }

    Long getMarkedAssessmentPercent() {
        if (this instanceof Outcome) {
            def progression = OutcomeFunctions.getProgression((Outcome) this)
            return Math.round(progression.getMarked().doubleValue() / progression.getMaxMarkedAssessmentsProgressValue().doubleValue() * 100)
        }
        throw new UnsupportedOperationException("Incorrect using of OutcomeTrait class; inheritor must be Outcome instance")
    }
}
