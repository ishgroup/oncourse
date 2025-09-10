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

import ish.oncourse.server.util.AttendanceProcessor
import ish.oncourse.server.util.DateCalculator
import ish.common.types.AttendanceType
import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.server.util.StartDateCalculator
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate

trait OutcomeTrait {

    abstract Enrolment getEnrolment()

    abstract PriorLearning getPriorLearning()

    abstract Module getModule()

    abstract LocalDate getStartDate()

    abstract LocalDate getEndDate()

    Contact getContact() {
        return (enrolment) ? enrolment?.student?.contact : priorLearning?.student?.contact
    }

    String getStudentName() {
        Contact contact = getContact()
        if (contact) {
            return contact.getFullName()
        } else {
            return StringUtils.EMPTY
        }
    }
    List<AssessmentClass> getAssessments() {
        if (module) {
            enrolment.courseClass.assessmentClasses.findAll {assessment -> module.id in assessment.modules*.id }
        } else {
            return enrolment.courseClass.assessmentClasses
        }
    }

    List<AssessmentSubmission> getSubmissions() {
        if(enrolment) {
            return  assessments.collect {assessment -> assessment.getAssessmentSubmission(enrolment)}.grep()
        } else {
            return []
        }
    }

    /**
     * @return percent of attended hours; if enrolment not set or max duration is 0, returns 0 by default
     */
    Long getPresentAttendancePercent() {
        if(enrolment == null)
            return 0

        def maxPossibleAttendedDuration = getMaxPossibleAttendedDuration()
        if( maxPossibleAttendedDuration == 0)
            return 0

        return Math.round(getAttendedAttendancesDuration().doubleValue() / getMaxPossibleAttendedDuration().doubleValue() * 100)
    }

    /**
     * @return percent of marked assessments; if enrolment not set or assessments are empty, returns 0 by default
     */
    Long getMarkedAssessmentPercent() {
        if(enrolment == null)
            return 0

        def outcomeAssessments = getOutcomeAssessments()
        if(outcomeAssessments.isEmpty())
            return 0

        return Math.round((double) getMarkedAssessmentsCount() / outcomeAssessments.size())
    }

    BigDecimal getMaxPossibleAttendedDuration() {
        return absentAttendancesDuration + attendedAttendancesDuration + notMarkedAttendancesDuration + futureTimetable
    }

    BigDecimal getNotMarkedAttendancesDuration() {
        BigDecimal notMarkedDuration = BigDecimal.ZERO
        Student student = enrolment.student

        pastSessions.each { session ->
            Attendance attendance = session.getAttendance(student)
            BigDecimal duration = session.durationInHours
            if (attendance && attendance.attendanceType) {
                if (attendance.attendanceType == AttendanceType.UNMARKED) {
                    notMarkedDuration += duration
                }
            } else {
                notMarkedDuration += session.durationInHours
            }
        }
        return notMarkedDuration
    }

    BigDecimal getAttendedAttendancesDuration() {
        BigDecimal attendedDuration = BigDecimal.ZERO
        Student student = enrolment.student

        getPastSessions().each { session ->
            Attendance attendance = session.getAttendance(student)
            BigDecimal duration = session.durationInHours
            if (attendance && attendance.attendanceType) {
                if (attendance.attendanceType == AttendanceType.ATTENDED) {
                    attendedDuration += duration
                } else if (attendance.attendanceType == AttendanceType.PARTIAL) {
                    attendedDuration += attendance.durationInHours
                }
            }
        }
        return attendedDuration
    }

    BigDecimal getAbsentAttendancesDuration() {
        BigDecimal absentDuration = BigDecimal.ZERO
        Student student = enrolment.student

        pastSessions.each { session ->
            Attendance attendance = session.getAttendance(student)
            BigDecimal duration = session.durationInHours
            if (attendance && attendance.attendanceType) {
                if (attendance.attendanceType == AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON) {
                    absentDuration += duration
                } else if (attendance.attendanceType == AttendanceType.PARTIAL) {
                    absentDuration += (duration - attendance.durationInHours)
                }
            }
        }
        return absentDuration
    }

    BigDecimal getFutureTimetable() {
        List<Session> futureSessions = getFutureSessions()
        return futureSessions.collect { session -> session.durationInHours }.inject(BigDecimal.ZERO) { a, b -> a.add(b) } as BigDecimal
    }


    List<Session> getPastSessions() {
        return getOutcomeSessions().findAll { session -> session.endDatetime.before(new Date()) }
    }

    List<Session> getFutureSessions() {
        return getOutcomeSessions().findAll { session -> session.endDatetime.after(new Date()) }
    }

    List<Session> getOutcomeSessions() {
        CourseClass clazz = enrolment.courseClass
        if (clazz.isDistantLearningCourse || clazz.sessions.empty)
            return clazz.sessions

        if (module) {
            return clazz.sessions.findAll { session -> module in session.modules }
        } else {
            // all sessions for defaullt outcome
            return clazz.sessions
        }
    }

    int getMarkedAssessmentsCount() {
        def releasedAssessments = getReleasedAssessments()
        int marked = 0
        releasedAssessments.each { assessment ->
            AssessmentSubmission submission = assessment.getAssessmentSubmission(enrolment)
            if (submission) {
                if (submission.markedOn != null) {
                    marked++
                }
            }
        }
        return marked
    }

    List<AssessmentClass> getReleasedAssessments() {
        getOutcomeAssessments().findAll { assessment -> assessment.releaseDate == null || assessment.releaseDate.before(new Date()) }
    }

    List<AssessmentClass> getOutcomeAssessments() {
        CourseClass clazz = enrolment.courseClass
        List<AssessmentClass> outcomeAssessments
        if (clazz.isDistantLearningCourse || clazz.sessions.empty) {
            outcomeAssessments = clazz.assessmentClasses
            // skip attendance diagram since no sessions
        } else {

            //if vet outcome - take in account only training plan sessions
            if (module) {
                outcomeAssessments = clazz.assessmentClasses.findAll { assessment -> module in assessment.modules }
            } else {
                outcomeAssessments = clazz.assessmentClasses
            }
        }
        return outcomeAssessments;
    }

    Date calculateDate(DateCalculator calculator, AttendanceProcessor attendanceProcessor) {
        def enrolment = getEnrolment()
        if (!enrolment) {
            return null
        }
        // this is a flexible delivery class so the start date varies for each enrolment
        // 'No sessions' means CourseClass.endDateTime or CourseClass.startDateTime is null
        CourseClassInterface courseClass = enrolment.courseClass;
        if (hasNoSessions(courseClass)) {
            return calculator.getDateIfNoSessions(enrolment)
        }

        def module = getModule()

        if (module) {
            def modules = attendanceProcessor.getModulesOf(courseClass, this as Outcome)
            List<Date> sessionModuleDates = calculator.getSessionDates(modules)

            List<Date> assessmentModuleDueDates = attendanceProcessor.getAssessmentDueDates(courseClass, this as Outcome)

            if (!sessionModuleDates.isEmpty() || !assessmentModuleDueDates.isEmpty()) {
                return calculator.getRequiredOfSorted((sessionModuleDates + assessmentModuleDueDates).sort())
            }
        }
        // if the module for the outcome isn't found in the sessions, return the class end or start date
        return calculator.getDateOf(courseClass)
    }


    private boolean hasNoSessions(CourseClassInterface courseClass){
        return courseClass.isDistantLearningCourse || (courseClass.sessions.empty && courseClass.assessmentClasses.empty)
    }
}
