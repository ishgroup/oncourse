/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.common.types.AccountType
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.common.BankingType
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.ICayenneService
import ish.util.PaymentMethodUtil
import ish.validation.ValidationFailure
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.validation.ValidationException
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static junit.framework.Assert.assertTrue
import static junit.framework.Assert.fail
import static org.junit.Assert.*

class PaymentInTest extends CayenneIshTestCase {

	@Test
    void testDataModifiedOnForPaymentInLines() throws InterruptedException {
		DataContext newContext = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Contact contact = newContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = newContext.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentIn pIn = newContext.newObject(PaymentIn.class)
        pIn.setPaymentDate(LocalDate.now())
        pIn.setAmount(Money.ONE)
        pIn.setPayer(contact)
        pIn.setAccountIn(account)
        pIn.setStatus(PaymentStatus.SUCCESS)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(newContext, PaymentMethod.class, PaymentType.CASH), pIn).set()

        pIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice invoice = newContext.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(new Money("0"))

        invoice.setDebtorsAccount(account)

        PaymentInLine piLine = newContext.newObject(PaymentInLine.class)
        piLine.setInvoice(invoice)
        piLine.setAmount(Money.ONE)
        piLine.setAccountOut(account)

        pIn.addToPaymentInLines(piLine)
        newContext.commitChanges()

        Thread.sleep(1000)
        Date pInModifiedOn = pIn.getModifiedOn()

        Banking banking = newContext.newObject(Banking.class)
        banking.setType(BankingType.MANUAL)
        banking.setSettlementDate(LocalDate.now())
        pIn.setBanking(banking)
        newContext.commitChanges()
        assertNotSame(pIn.getModifiedOn(), pInModifiedOn)
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

	@Test
    void testZeroContraPayment() {
		DataContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        Banking banking = context.newObject(Banking.class)
        banking.setSettlementDate(LocalDate.now())
        banking.setType(BankingType.GATEWAY)

        Contact contact = context.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = context.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentMethod paymentMethod = context.newObject(PaymentMethod.class)
        paymentMethod.setName("TestPayment")
        paymentMethod.setAccount(account)
        paymentMethod.setType(PaymentType.CONTRA)

        PaymentIn pIn = context.newObject(PaymentIn.class)
        pIn.setAmount(Money.ONE)
        pIn.setPayer(contact)
        pIn.setReconciled(true)
        pIn.setBanking(banking)
        pIn.setPaymentMethod(paymentMethod)
        pIn.setPaymentDate(LocalDate.now())
        try{
			context.commitChanges()
            fail("The CONTRA payment-in amount must be \$0")
        }catch(Exception e){
			assertTrue(e instanceof ValidationException)
            assertEquals(PaymentInLine.AMOUNT.name,
					((ValidationFailure)((ValidationException) e).getValidationResult().getFailures().get(0)).getProperty())
        }
	}

	private boolean checkStatusChangeAvailability(ObjectContext context, PaymentStatus from, PaymentStatus to) {
		try {
			PaymentIn payment = context.newObject(PaymentIn.class)

            payment.setStatus(from)
            payment.setStatus(to)

            return true
        } catch (IllegalArgumentException e) {
			return false
        }
	}
}