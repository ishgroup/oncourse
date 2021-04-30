/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals

/**
 */
class SurveyTest extends CayenneIshTestCase {

	private ICayenneService cayenneService

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = SurveyTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/SurveyTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))

        executeDatabaseOperation(rDataSet)

    }


    @Test
    void testSurvey()
    {
        ObjectContext context = cayenneService.getNewContext()

        Enrolment enrolment = context.newObject(Enrolment.class)
        enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION)
        enrolment.setCourseClass(SelectById.query(CourseClass.class, 1).selectOne(context))
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)
        enrolment.setStudent(SelectById.query(Student.class, 1).selectOne(context))

        Survey survey = context.newObject(Survey.class)

        survey.setComment("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
        survey.setCourseScore(1)
        survey.setTutorScore(1)
        survey.setVenueScore(1)
        survey.setEnrolment(enrolment)

        context.commitChanges()

        Survey survey1 = SelectById.query(survey.getClass(), survey.getObjectId()).selectOne(cayenneService.getNewContext())
        assertEquals(survey.getComment(), survey1.getComment())
        assertEquals(survey.getCourseScore(), survey1.getCourseScore())
        assertEquals(survey.getTutorScore(), survey1.getTutorScore())
        assertEquals(survey.getVenueScore(), survey1.getVenueScore())
        assertEquals(survey.getEnrolment().getId(), survey1.getEnrolment().getId())
    }

}
