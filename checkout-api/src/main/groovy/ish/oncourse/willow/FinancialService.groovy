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
    
    Money getAvalibleCredit(String payerId) {
        
        Contact payer = new GetContact(cayenneService.newContext(), collegeService.college, payerId).get(false)
        if (!payer) {
            throw new NullPointerException("Payer undefined")
        }

       return ObjectSelect.query(Invoice)
                .where(Invoice.CONTACT.eq(payer))
                .and(Invoice.AMOUNT_OWING.lt(Money.ZERO))
                .and(Invoice.ANGEL_ID.isNotNull())
                .and(paymentFilter)
                .column(Invoice.AMOUNT_OWING)
                .select(cayenneService.newContext()).inject(Money.ZERO) { a,b -> a.add(b)}
    }
    
}
