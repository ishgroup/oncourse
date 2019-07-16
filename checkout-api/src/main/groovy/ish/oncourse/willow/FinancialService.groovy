package ish.oncourse.willow

import com.google.inject.Inject
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentInLine
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect

class FinancialService {

    private CayenneService cayenneService
    private CollegeService collegeService

    private static final Expression paymentFilter = Invoice.PAYMENT_IN_LINES.outer().isNull().orExp(
            Invoice.PAYMENT_IN_LINES.outer().dot(PaymentInLine.PAYMENT_IN).outer().dot(PaymentIn.STATUS).nin([PaymentStatus.NEW, PaymentStatus.IN_TRANSACTION]).andExp(
                    Invoice.PAYMENT_IN_LINES.outer().dot(PaymentInLine.PAYMENT_IN).outer().dot(PaymentIn.ANGEL_ID).outer().isNotNull()
            )
    )

    @Inject
    FinancialService(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }

    Money getAvailableCredit(String payerId) {
        Contact payer = contactById(payerId)
        Money credit = creditNoteQuery(payer)
                .column(Invoice.AMOUNT_OWING)
                .select(cayenneService.newContext()).inject(Money.ZERO) { a, b -> a.add(b) }
        return credit.negate()
    }

    List<CreditNode> getAvailableCreditMap(Contact payer) {
        List <Invoice> credits = creditNoteQuery(payer)
                .orderBy(Invoice.AMOUNT_OWING.asc())
                .select(cayenneService.newContext())
        return credits.collect { new CreditNode(invoice: it, amount: it.amountOwing.negate())}
    }


    void contraPay(PaymentIn payment, Invoice credit, Money apply) {
        PaymentInLine line = payment.objectContext.newObject(PaymentInLine)
        line.college = payment.college
        line.paymentIn = payment
        line.invoice = credit
        line.amount = apply.negate()
    }

    private static ObjectSelect creditNoteQuery(Contact payer) {
        return ((ObjectSelect.query(Invoice)
                .where(Invoice.CONTACT.eq(payer)) & Invoice.AMOUNT_OWING.lt(Money.ZERO)) & Invoice.ANGEL_ID.isNotNull()) & paymentFilter
    }

    private Contact contactById(String payerId) {
        Contact payer = new GetContact(cayenneService.newContext(), collegeService.college, payerId).get(false)
        if (!payer) {
            throw new NullPointerException("Payer undefined")
        } else {
            return payer
        }
    }

    static class CreditNode {
        private Invoice invoice
        private Money amount
        Invoice getInvoice() {
            return invoice
        }

        void setInvoice(Invoice invoice) {
            this.invoice = invoice
        }

        Money getAmount() {
            return amount
        }

        void setAmount(Money amount) {
            this.amount = amount
        }

    }

}
