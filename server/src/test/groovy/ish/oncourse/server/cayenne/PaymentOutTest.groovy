/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/cayenne/ishDataContextTestDataSet.xml")
class PaymentOutTest extends CayenneIshTestCase {

    @Override
    protected void dataSourceReplaceValues(ReplacementDataSet rDataSet) {
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
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
        Assertions.assertEquals(PaymentStatus.IN_TRANSACTION, payment.getStatus())

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

        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.NEW, null))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.QUEUED))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.IN_TRANSACTION))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.SUCCESS))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.FAILED))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.FAILED_CARD_DECLINED))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.NEW, PaymentStatus.FAILED_NO_PLACES))

        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, null))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.NEW))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.IN_TRANSACTION))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.SUCCESS))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.FAILED))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.FAILED_CARD_DECLINED))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.QUEUED, PaymentStatus.FAILED_NO_PLACES))

        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, null))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.NEW))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.QUEUED))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.SUCCESS))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.FAILED))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.FAILED_CARD_DECLINED))
        Assertions.assertTrue(checkStatusChangeAvailability(context, PaymentStatus.IN_TRANSACTION, PaymentStatus.FAILED_NO_PLACES))

        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, null))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.NEW))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.IN_TRANSACTION))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.QUEUED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.FAILED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.FAILED_CARD_DECLINED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.SUCCESS, PaymentStatus.FAILED_NO_PLACES))

        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, null))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.NEW))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.IN_TRANSACTION))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.QUEUED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.SUCCESS))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.FAILED_CARD_DECLINED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED, PaymentStatus.FAILED_NO_PLACES))

        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, null))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.NEW))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.IN_TRANSACTION))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.QUEUED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.SUCCESS))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.FAILED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_CARD_DECLINED, PaymentStatus.FAILED_NO_PLACES))

        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, null))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.NEW))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.IN_TRANSACTION))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.QUEUED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.SUCCESS))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.FAILED_CARD_DECLINED))
        Assertions.assertFalse(checkStatusChangeAvailability(context, PaymentStatus.FAILED_NO_PLACES, PaymentStatus.FAILED))
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
