/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.ICayenneService
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.junit.Assert.*

class PaymentOutTest extends CayenneIshTestCase {

	private ICayenneService cayenneService

    @BeforeEach
    void setup() throws Exception {
		wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = PaymentOutTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/ishDataContextTestDataSet.xml")
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
    void testCCPaymentOutProcessing() {

		ObjectContext context = cayenneService.getNewContext()

        Account accountOut = SelectById.query(Account.class, 50).selectOne(context)
        Contact payer = SelectById.query(Contact.class, 1).selectOne(context)
        Invoice invoice1 = SelectById.query(Invoice.class, 1).selectOne(context)
        Invoice invoice2 = SelectById.query(Invoice.class, 2).selectOne(context)

        PaymentOut payment = context.newObject(PaymentOut.class)
        payment.setPaymentDate(LocalDate.now())
        payment.setAccountOut(accountOut)
        payment.setAmount(new Money(new BigDecimal(50)))
        payment.setPayee(payer)
        payment.setReconciled(false)
        payment.setStatus(PaymentStatus.IN_TRANSACTION)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getRealTimeCreditCardPaymentMethod(context, PaymentMethod.class), payment).set()

        PaymentOutLine pol1 = context.newObject(PaymentOutLine.class)
        pol1.setInvoice(invoice1)
        pol1.setAccountIn(accountOut)
        pol1.setAmount(invoice1.getAmountOwing())
        pol1.setPaymentOut(payment)

        PaymentOutLine pol2 = context.newObject(PaymentOutLine.class)
        pol2.setInvoice(invoice2)
        pol2.setAccountIn(accountOut)
        pol2.setAmount(invoice2.getAmountOwing())
        pol2.setPaymentOut(payment)

        context.commitChanges()

        payment.setPersistenceState(PersistenceState.HOLLOW)

        // payment processor should be invoked and failed, payment should left in IN_TRANSACTION state
		assertEquals(PaymentStatus.IN_TRANSACTION, payment.getStatus())

        context.deleteObjects(payment, pol1, pol2)

        context.commitChanges()
    }

	@Test
    void testStatusConstraints() {
		/**
		 * List of allowed status changes: <br>
		 * <ul>
		 * <li>null -> anything</li>
		 * <li>NEW -> anything but null</li>
		 * <li>QUEUED -> anything but null/NEW</li>
		 * <li>IN_TRANSACTION -> anything but null/NEW/QUEUED</li>
		 * <li>CARD_DETAILS_REQUIRED -> anything but null/NEW/QUEUED</li>
		 * <li>SUCCESS -> only STATUS_CANCELLED/STATUS_REFUNDED allowed</li>
		 * <li>FAILED/FAILED_CARD_DECLINED/FAILED_NO_PLACES -> no further status change allowed</li>
		 * <li>STATUS_CANCELLED/STATUS_REFUNDED -> no further status change allowed</li>
		 * </ul>
		 */

		ObjectContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        // allowed changes

		assertFalse(checkStatusChangeAvailability(context, PaymentStatus.NEW, null))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.QUEUED))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.IN_TRANSACTION))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.SUCCESS))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.FAILED))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.FAILED_CARD_DECLINED))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.FAILED_NO_PLACES))

        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, null))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.NEW))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.IN_TRANSACTION))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.SUCCESS))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.FAILED))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.FAILED_CARD_DECLINED))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.FAILED_NO_PLACES))

        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, null))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.QUEUED))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.SUCCESS))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.FAILED))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.FAILED_CARD_DECLINED))
        assertTrue(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.FAILED_NO_PLACES))

        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, null))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.FAILED))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.FAILED_CARD_DECLINED))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.FAILED_NO_PLACES))

        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, null))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.SUCCESS))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.FAILED_CARD_DECLINED))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.FAILED_NO_PLACES))

        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, null))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.SUCCESS))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.FAILED))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.FAILED_NO_PLACES))

        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, null))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.NEW))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.IN_TRANSACTION))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.QUEUED))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.SUCCESS))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.FAILED_CARD_DECLINED))
        assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.FAILED))
    }

	private boolean checkStatusChangeAvailability(ObjectContext context, PaymentStatus from, PaymentStatus to) {
		try {
			PaymentOut payment = context.newObject(PaymentOut.class)

            payment.setStatus(from)
            payment.setStatus(to)

            return true
        } catch (IllegalArgumentException e) {
			return false
        }
	}
}
