package ish.oncourse.portal.services.payment

import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentTransaction
import ish.oncourse.model.auto._Invoice
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
    def ObjectContext objectContext
    def Contact contact
    def PaymentIn paymentIn
    def Invoice invoice
    def PaymentTransaction paymentTransaction
    def PaymentIn notFinalPaymentIn

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

    public static Context valueOf(Contact contact, Request request) {
        new Context().with {
            it.contact = contact
            it.objectContext = contact.objectContext

            if (request.paymentInId != null) {
                it.paymentIn = SelectById.query(PaymentIn.class, request.paymentInId).selectOne(objectContext)
                if (it.paymentIn) {
                    it.paymentTransaction = query(PaymentTransaction)
                            .where(PAYMENT.eq(it.paymentIn))
                            .and(IS_FINALISED.eq(Boolean.FALSE)).selectFirst(it.objectContext)
                }
            } else {
                it.notFinalPaymentIn = query(PaymentIn).where(STATUS.in(IN_TRANSACTION, CARD_DETAILS_REQUIRED, NEW))
                        .and(CONTACT.eq(contact))
                        .selectFirst(it.objectContext)
            }
            if (request.invoiceId != null) {
                it.invoice = SelectById.query(Invoice, request.invoiceId).selectOne(it.objectContext)
            } else {
                it.invoice = query(Invoice).where(_Invoice.CONTACT.eq(contact)).and(Invoice.AMOUNT_OWING.gt(new Money(0, 1))).orderBy(Invoice.DATE_DUE.asc()).selectFirst(it.objectContext)
            }

            validate()
            return it
        }
    }
}
