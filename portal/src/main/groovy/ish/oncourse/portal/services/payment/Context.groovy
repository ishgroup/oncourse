package ish.oncourse.portal.services.payment

import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentTransaction
import ish.oncourse.utils.invoice.GetAmountOwing
import ish.oncourse.utils.invoice.GetInvoiceOverdue
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import static ish.common.types.PaymentStatus.*
import static ish.oncourse.model.auto._PaymentIn.CONTACT
import static ish.oncourse.model.auto._PaymentIn.STATUS
import static ish.oncourse.model.auto._PaymentTransaction.IS_FINALISED
import static ish.oncourse.model.auto._PaymentTransaction.PAYMENT
import static org.apache.cayenne.query.ObjectSelect.query

/**
 * User: akoiro
 * Date: 1/07/2016
 */
class Context {
    private static final Money MIN_AMOUNT_OWING = new Money(0, 1);

    def ObjectContext objectContext
    def Contact contact
    def PaymentIn paymentIn
    def Invoice invoice
    def PaymentTransaction paymentTransaction
    def PaymentIn notFinalPaymentIn
    def Double nextAmount
    def Date dateDue

    private void validate() {
        if (contact == null) {
            throw new IllegalAccessError()
        }
        if (paymentIn != null && paymentIn.contact != contact) {
            throw new IllegalAccessError()
        }
        if (invoice != null && invoice.contact != contact) {
            throw new IllegalAccessError()
        }
    }

    private void initNextAmount() {
        if (this.invoice) {
            GetInvoiceOverdue getInvoiceOverdue = GetInvoiceOverdue.valueOf(invoice).call();
            if (getInvoiceOverdue.overdue.isGreaterThan(Money.ZERO)) {
                nextAmount = getInvoiceOverdue.overdue.doubleValue()
                dateDue = getInvoiceOverdue.dateDue;
            }
            else {
                nextAmount = getInvoiceOverdue.next.doubleValue()
                dateDue = getInvoiceOverdue.nextDateDue;
            }

        }
    }

    public static Context valueOf(Contact contact, Request request) {
        new Context().with {
            it.contact = contact
            it.objectContext = contact.objectContext

            if (request.paymentInId != null) {
                it.paymentIn = SelectById.query(PaymentIn.class, request.paymentInId).selectOne(objectContext)
                if (it.paymentIn) {
                    it.paymentTransaction = (query(PaymentTransaction)
                            .where(PAYMENT.eq(it.paymentIn)) & IS_FINALISED.eq(Boolean.FALSE)).selectFirst(it.objectContext)
                }
            } else {
                it.notFinalPaymentIn = (query(PaymentIn).where(STATUS.in(IN_TRANSACTION, CARD_DETAILS_REQUIRED, NEW)) & CONTACT.eq(contact))
                        .selectFirst(it.objectContext)
            }
            if (request.invoiceId != null) {
                it.invoice = SelectById.query(Invoice, request.invoiceId).selectOne(it.objectContext)
                it.nextAmount = request.card.amount
                it.dateDue = it.invoice.dateDue
            } else {
                List<Invoice> invoices = (query(Invoice).where(Invoice.CONTACT.eq(contact)) & Invoice.AMOUNT_OWING.gt(MIN_AMOUNT_OWING)).orderBy(Invoice.DATE_DUE.asc()).select(it.objectContext)
                it.invoice = invoices.find { invoice ->
                    GetAmountOwing.valueOf(invoice).get().isGreaterThan(MIN_AMOUNT_OWING)
                }
                it.initNextAmount()
            }

            it.invoice

            validate()
            return it
        }
    }
}
