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

    Map<Invoice, Money> getAvailableCreditMap(Contact payer) {
        List <Invoice> credits = creditNoteQuery(payer)
                .select(cayenneService.newContext())
        return credits.collectEntries { it -> [it, it.amountOwing.negate()]}
    }

    Money calculateOutstandingAmount(Invoice invoice) {
        Money totalPurchases =  invoice.invoiceLines.collect {it.finalPriceToPayIncTax}.inject(Money.ZERO) {a, b -> a.add(b)}
        Money paymentTotal = invoice.paymentInLines.collect{it.amount}.inject(Money.ZERO) {a, b -> a.add(b)}
        return totalPurchases.subtract(paymentTotal)
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

}
