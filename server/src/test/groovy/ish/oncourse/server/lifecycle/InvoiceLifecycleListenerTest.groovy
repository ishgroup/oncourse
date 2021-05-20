/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.lifecycle

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import ish.common.types.StudyReason
import ish.math.Money
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.scripting.GroovyScriptService
import ish.util.AccountUtil
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/lifecycle/invoiceLifecycleTest.xml")
class InvoiceLifecycleListenerTest extends TestWithDatabase {

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
    }
    
    @BeforeEach
    void initTriggers() throws Exception {
        injector.getInstance(GroovyScriptService.class).initTriggers()
    }
    
    @Test
    void testConfirmationEmailsQueued() throws Exception {
        Account account = AccountUtil.getAccountWithId(50L, cayenneContext, Account.class)

        Tax tax = SelectById.query(Tax.class, 3).selectOne(cayenneContext)

        Student student1 = SelectById.query(Student.class, 1).selectOne(cayenneContext)
        Student student2 = SelectById.query(Student.class, 2).selectOne(cayenneContext)

        CourseClass courseClass1 = SelectById.query(CourseClass.class, 1).selectOne(cayenneContext)

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setContact(student1.getContact())
        invoice.setDebtorsAccount(account)
        invoice.setSource(PaymentSource.SOURCE_WEB)

        InvoiceLine il1 = cayenneContext.newObject(InvoiceLine.class)
        il1.setAccount(account)
        il1.setTax(tax)
        il1.setDiscountEachExTax(Money.ZERO)
        il1.setInvoice(invoice)
        il1.setPrepaidFeesAccount(account)
        il1.setPrepaidFeesRemaining(Money.ZERO)
        il1.setPriceEachExTax(Money.ONE)
        il1.setQuantity(BigDecimal.ONE)
        il1.setTaxEach(new Money("0.1"))
        il1.setTitle("il1")

        InvoiceLine il2 = cayenneContext.newObject(InvoiceLine.class)
        il2.setAccount(account)
        il2.setTax(tax)
        il2.setDiscountEachExTax(Money.ZERO)
        il2.setInvoice(invoice)
        il2.setPrepaidFeesAccount(account)
        il2.setPrepaidFeesRemaining(Money.ZERO)
        il2.setPriceEachExTax(Money.ONE)
        il2.setQuantity(BigDecimal.ONE)
        il2.setTaxEach(new Money("0.1"))
        il2.setTitle("il2")

        Enrolment enrol1 = cayenneContext.newObject(Enrolment.class)
        enrol1.setStudent(student1)
        enrol1.setCourseClass(courseClass1)
        enrol1.setEligibilityExemptionIndicator(false)
        enrol1.setSource(PaymentSource.SOURCE_WEB)
        enrol1.setStudyReason(StudyReason.STUDY_REASON_NOT_STATED)
        enrol1.setVetFeeIndicator(false)
        enrol1.setVetIsFullTime(false)
        enrol1.setStatus(EnrolmentStatus.IN_TRANSACTION)
        enrol1.addToInvoiceLines(il1)

        Enrolment enrol2 = cayenneContext.newObject(Enrolment.class)
        enrol2.setStudent(student2)
        enrol2.setCourseClass(courseClass1)
        enrol2.setEligibilityExemptionIndicator(false)
        enrol2.setSource(PaymentSource.SOURCE_WEB)
        enrol2.setStudyReason(StudyReason.STUDY_REASON_NOT_STATED)
        enrol2.setVetFeeIndicator(false)
        enrol2.setVetIsFullTime(false)
        enrol2.setStatus(EnrolmentStatus.IN_TRANSACTION)
        enrol2.addToInvoiceLines(il2)

        cayenneContext.commitChanges()

        Assertions.assertTrue(cayenneContext.select(SelectQuery.query(MessagePerson.class)).isEmpty())
        Assertions.assertTrue(cayenneContext.select(SelectQuery.query(Message.class)).isEmpty())

        invoice.setModifiedOn(new Date())
        enrol1.setStatus(EnrolmentStatus.SUCCESS)

        cayenneContext.commitChanges()

        // still should be no messages, need to wait until all enrolments will be successful

        Assertions.assertTrue(cayenneContext.select(SelectQuery.query(MessagePerson.class)).isEmpty())
        Assertions.assertTrue(cayenneContext.select(SelectQuery.query(Message.class)).isEmpty())

        invoice.setModifiedOn(new Date())
        enrol2.setStatus(EnrolmentStatus.SUCCESS)

        cayenneContext.commitChanges()

        // give script running in separate thread some time to queue emails
        Thread.sleep(5000)

        List<Message> messages = cayenneContext.select(SelectQuery.query(Message.class))
        List<MessagePerson> messagePersons = cayenneContext.select(SelectQuery.query(MessagePerson.class))

        Assertions.assertEquals(3, messages.size())
        Assertions.assertEquals(3, messagePersons.size())
    }

    
    @Test
    void testInstantlySuccesfulEnrolmentConfirmationQueued() throws Exception {
        Account account = AccountUtil.getAccountWithId(50L, cayenneContext, Account.class)
        Tax tax = SelectById.query(Tax.class, 3).selectOne(cayenneContext)

        Student student1 = SelectById.query(Student.class, 1).selectOne(cayenneContext)
        SelectById.query(Student.class, 2).selectOne(cayenneContext)

        CourseClass courseClass1 = SelectById.query(CourseClass.class, 1).selectOne(cayenneContext)

        Invoice invoice = cayenneContext.newObject(Invoice.class)
        invoice.setContact(student1.getContact())
        invoice.setDebtorsAccount(account)
        invoice.setSource(PaymentSource.SOURCE_WEB)

        InvoiceLine il1 = cayenneContext.newObject(InvoiceLine.class)
        il1.setAccount(account)
        il1.setTax(tax)
        il1.setDiscountEachExTax(Money.ZERO)
        il1.setInvoice(invoice)
        il1.setPrepaidFeesAccount(account)
        il1.setPrepaidFeesRemaining(Money.ZERO)
        il1.setPriceEachExTax(Money.ONE)
        il1.setQuantity(BigDecimal.ONE)
        il1.setTaxEach(new Money("0.1"))
        il1.setTitle("il1")

        Enrolment enrol1 = cayenneContext.newObject(Enrolment.class)
        enrol1.setStudent(student1)
        enrol1.setCourseClass(courseClass1)
        enrol1.setEligibilityExemptionIndicator(false)
        enrol1.setSource(PaymentSource.SOURCE_WEB)
        enrol1.setStudyReason(StudyReason.STUDY_REASON_NOT_STATED)
        enrol1.setVetFeeIndicator(false)
        enrol1.setVetIsFullTime(false)
        enrol1.setStatus(EnrolmentStatus.SUCCESS)
        enrol1.addToInvoiceLines(il1)

        cayenneContext.commitChanges()

        // give script running in separate thread some time to queue emails
        Thread.sleep(1000)

        List<Message> messages = cayenneContext.select(SelectQuery.query(Message.class))
        List<MessagePerson> messagePersons = cayenneContext.select(SelectQuery.query(MessagePerson.class))

        Assertions.assertEquals(2, messages.size())
        Assertions.assertEquals(2, messagePersons.size())
    }


}
