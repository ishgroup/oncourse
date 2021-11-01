/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/cayenne/SurveyTest.xml")
class SurveyTest extends TestWithDatabase {

    @Override
    void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
    }

    @Test
    void testSurvey() {
        ObjectContext context = cayenneService.getNewContext()

        Enrolment enrolment = context.newObject(Enrolment.class)
        enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION)
        enrolment.setCourseClass(SelectById.query(CourseClass.class, 1).selectOne(context))
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrolment.setStudent(SelectById.query(Student.class, 1).selectOne(context))

        Survey survey = context.newObject(Survey.class)

        String comment  = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
        survey.comment = comment
        survey.setCourseScore(1)
        survey.setTutorScore(1)
        survey.setVenueScore(1)
        survey.setEnrolment(enrolment)

        context.commitChanges()

        survey = ObjectSelect.query(Survey).selectFirst(cayenneService.getNewContext())
        Assertions.assertEquals(1, survey.getComment())
        Assertions.assertEquals(1, survey.getCourseScore())
        Assertions.assertEquals(1, survey.getTutorScore())
        Assertions.assertEquals(1, survey.getVenueScore())
        Assertions.assertNotNull(survey.getEnrolment())
    }

}
