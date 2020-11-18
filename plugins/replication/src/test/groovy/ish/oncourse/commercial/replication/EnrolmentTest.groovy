/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication

import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.junit.Before
import org.junit.Test

@groovy.transform.CompileStatic
import static org.junit.Assert.assertEquals

class EnrolmentTest extends CayenneIshTestCase {

    private ICayenneService cayenneService


    @Before
    void setup() throws Exception {
        wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = EnrolmentTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/enrolment-outcomeTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))

        executeDatabaseOperation(rDataSet)
        injector.getInstance(PreferenceController.class).setReplicationEnabled(true)
    }
    
    @Test
    void testAuxillaryRecordsAndQueue() throws Exception {

        DataContext c0 = cayenneService.getNewContext()
        c0.deleteObjects(c0.select(SelectQuery.query(QueuedRecord.class)))
        c0.deleteObjects(c0.select(SelectQuery.query(QueuedTransaction.class)))
        c0.commitChanges()

        DataContext newContext = cayenneService.getNewContext()

        Student student = newContext.select(SelectQuery.query(Student.class, Student.ID.eq(5L))).get(0)
        CourseClass cc = newContext.select(SelectQuery.query(CourseClass.class, CourseClass.ID.eq(5L))).get(0)

        Enrolment enrolment = newContext.newObject(Enrolment.class)
        enrolment.setStudent(student)
        enrolment.setCourseClass(cc)
        enrolment.setSource(PaymentSource.SOURCE_ONCOURSE)

        // test setup ends here

        enrolment.setStatus(EnrolmentStatus.SUCCESS)
        newContext.commitChanges()

        Expression expression = QueuedRecord.TABLE_NAME.eq("Enrolment")
        expression = expression.andExp(QueuedRecord.FOREIGN_RECORD_ID.eq(enrolment.getId()))
        expression = expression.andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.CREATE))
        assertEquals("Check queue ", 1, cayenneService.getNewContext().select(SelectQuery.query(QueuedRecord.class, expression)).size())

        expression = QueuedRecord.TABLE_NAME.eq("Enrolment")
        expression = expression.andExp(QueuedRecord.FOREIGN_RECORD_ID.eq(enrolment.getId()))
        expression = expression.andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.UPDATE))
        assertEquals("Check queue ", 1, cayenneService.getNewContext().select(SelectQuery.query(QueuedRecord.class, expression)).size())

        expression = QueuedRecord.TABLE_NAME.eq("Attendance")
        expression = expression.andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.CREATE))
        assertEquals("Check queue ", 2, cayenneService.getNewContext().select(SelectQuery.query(QueuedRecord.class, expression)).size())

        expression = QueuedRecord.TABLE_NAME.eq("Outcome")
        expression = expression.andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.CREATE))
        assertEquals("Check queue ", 3, cayenneService.getNewContext().select(SelectQuery.query(QueuedRecord.class, expression)).size())

        enrolment.setStatus(EnrolmentStatus.CANCELLED)
        newContext.commitChanges()

        expression = QueuedRecord.TABLE_NAME.eq("Enrolment")
        expression = expression.andExp(QueuedRecord.FOREIGN_RECORD_ID.eq(enrolment.getId()))
        expression = expression.andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.CREATE))
        assertEquals("Check queue ", 1, cayenneService.getNewContext().select(SelectQuery.query(QueuedRecord.class, expression)).size())

        expression = QueuedRecord.TABLE_NAME.eq("Enrolment")
        expression = expression.andExp(QueuedRecord.FOREIGN_RECORD_ID.eq(enrolment.getId()))
        expression = expression.andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.UPDATE))
        // only 2 updates for enrolment now (before 3), because when perform cancel/refund then outcomes are not deleted on postPersist/postUpdate (on server side)
        assertEquals("Check queue ", 2, cayenneService.getNewContext().select(SelectQuery.query(QueuedRecord.class, expression)).size())

        expression = QueuedRecord.TABLE_NAME.eq("Attendance").andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.CREATE))
        assertEquals("Check queue ", 2, cayenneService.getNewContext().performQuery(SelectQuery.query(QueuedRecord.class, expression)).size())

        expression = QueuedRecord.TABLE_NAME.eq("Outcome").andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.CREATE))
        assertEquals("Check queue ", 3, cayenneService.getNewContext().performQuery(SelectQuery.query(QueuedRecord.class, expression)).size())

        expression = QueuedRecord.TABLE_NAME.eq("Attendance").andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.DELETE))
        assertEquals("Check queue ", 2, cayenneService.getNewContext().performQuery(SelectQuery.query(QueuedRecord.class, expression)).size())

        // do not delete outcomes on server side when user perform cancel/refund
        expression = QueuedRecord.TABLE_NAME.eq("Outcome").andExp(QueuedRecord.ACTION.eq(QueuedRecordAction.DELETE))
        assertEquals("Check queue ", 0, cayenneService.getNewContext().performQuery(SelectQuery.query(QueuedRecord.class, expression)).size())

        newContext.deleteObjects(enrolment)
        newContext.commitChanges()
    }

}
