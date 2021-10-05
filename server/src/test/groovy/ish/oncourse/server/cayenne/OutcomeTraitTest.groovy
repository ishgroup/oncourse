/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.PaymentSource
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/cayenne/outcomeProgressTest.xml")
class OutcomeTraitTest extends TestWithDatabase {

    @Override
    void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))

        rDataSet.addReplacementObject("[yesterday]", DateUtils.addDays(new Date(), -1))
        rDataSet.addReplacementObject("[tomorrow]", DateUtils.addDays(new Date(), 1))
        rDataSet.addReplacementObject("[end_session]", DateUtils.addHours(new Date(), 124))
    }

    @Test
    void testOutcomeSessionsAndAttandancesProgress() {
        DataContext newContext = cayenneService.getNewContext()

        Student student = SelectById.query(Student.class, 5L).selectOne(newContext)
        CourseClass cc = SelectById.query(CourseClass.class, 5L).selectOne(newContext)

        AssessmentClass assessmentClass = SelectById.query(AssessmentClass.class, 300L).selectOne(newContext)

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setCourseClass(cc)
        enrolment.setStudent(student)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        AssessmentSubmission submission = newContext.newObject(AssessmentSubmission.class)
        submission.setEnrolment(enrolment)
        submission.setAssessmentClass(assessmentClass)
        submission.setGrade(BigDecimal.valueOf(84.74F))
        submission.setSubmittedOn(DateUtils.addDays(new Date(), -1))
        submission.setMarkedOn(DateUtils.addDays(new Date(), -1))
        submission.setMarkedBy(student.contact)

        enrolment.assessmentSubmissions.add(submission)

        newContext.commitChanges()
        student = SelectById.query(Student.class, student.getObjectId())
                .selectOne(newContext)

        assertEquals(3, enrolment.getOutcomes().size())

        def outcome = enrolment.getOutcomes().get(0);

        Session s8 = newContext.newObject(Session.class)
        s8.setCourseClass(cc)
        s8.setStartDatetime(new Date(new Date().getTime() - 1000 * 60 * 60 * 34))
        s8.setEndDatetime(new Date(new Date().getTime() - 1000 * 60 * 60 * 32))


        newContext.commitChanges()

        student = SelectById.query(Student.class, student.getObjectId())
                .selectOne(newContext)

        outcome = enrolment.getOutcomes().get(0);

        assertEquals(8, outcome.getOutcomeSessions().size())
        assertEquals(7, outcome.getPastSessions().size())

        assertEquals(100.00, outcome.futureTimetable)
        assertEquals(50.00, outcome.absentAttendancesDuration)
        assertEquals(50.00, outcome.attendedAttendancesDuration)
        assertEquals(10.00, outcome.notMarkedAttendancesDuration)

        newContext.deleteObjects(s8)
        newContext.commitChanges()

        assertEquals(outcome.getOutcomeSessions().size(), 7)

        assertEquals(3, outcome.outcomeAssessments.size())
        assertEquals(2, outcome.releasedAssessments.size())
        assertEquals(1, outcome.markedAssessmentsCount)

        newContext.deleteObjects(enrolment.getOutcomes())
        newContext.deleteObjects(enrolment.getAssessmentSubmissions())
        newContext.deleteObjects(enrolment)
        newContext.commitChanges()
    }
}