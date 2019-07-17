package ish.oncourse.willow.checkout

import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.model.PaymentIn
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.web.CourseClassPrice
import ish.oncourse.willow.service.ApiTest
import org.apache.cayenne.query.ObjectSelect
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class MultiMoneySourcesTest extends ApiTest {
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/checkout/MultiMoneySourcesTest.xml'
    }

    @Test
    void testCredit_only() {
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService)
        CheckoutModelRequest modelRequest = modelRequest('1004', 220.00, ['1001'], [])
        api.makePayment(zeroPaymentRequest(modelRequest))

        PaymentIn payment = ObjectSelect.query(PaymentIn).selectOne(cayenneService.newContext())

        assertEquals(Money.ZERO, payment.amount)
        assertEquals(PaymentStatus.SUCCESS, payment.status)
        assertEquals(PaymentType.INTERNAL, payment.type)
        assertEquals(2, payment.paymentInLines.size())
        assertNotNull(payment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001l})
        assertEquals(new Money("220.00"), payment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001l}.amount)
        assertNotNull(payment.paymentInLines.find {it.invoice.id == 1004l})
        assertEquals(new Money("-220.00"), payment.paymentInLines.find {it.invoice.id == 1004l}.amount)

        assertEquals(EnrolmentStatus.SUCCESS, ObjectSelect.query(ish.oncourse.model.Enrolment).selectOne(cayenneService.newContext()).status)

    }

    @Test
    void testCredit_CCPayment() {
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService)
        CheckoutModelRequest modelRequest = modelRequest('1001', 340.00, ['1001', '1002'], [])
        api.makePayment(validPaymentRequest(modelRequest, 240.00))

        PaymentIn payment = ObjectSelect.query(PaymentIn).selectOne(cayenneService.newContext())

        assertEquals(new Money("240.00"), payment.amount)
        assertEquals(PaymentStatus.SUCCESS, payment.status)
        assertEquals(PaymentType.CREDIT_CARD, payment.type)
        assertEquals(3, payment.paymentInLines.size())
        assertNotNull(payment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001l})
        assertEquals(new Money("170.00"), payment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001l}.amount)
        assertNotNull(payment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1002l})
        assertEquals(new Money("170.00"), payment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1002l}.amount)
        assertNotNull(payment.paymentInLines.find {it.invoice.id == 1003l})
        assertEquals(new Money("-100.00"), payment.paymentInLines.find {it.invoice.id == 1003l}.amount)

        List<ish.oncourse.model.Enrolment> enrolments = ObjectSelect.query(ish.oncourse.model.Enrolment).select(cayenneService.newContext())
        assertEquals(2, enrolments.size())
        enrolments.each {
            assertEquals(EnrolmentStatus.SUCCESS, it.status)

        }
    }


    private static PaymentRequest validPaymentRequest(CheckoutModelRequest modelRequest,   Double ccAmount) {
        new PaymentRequest().with {
            it.checkoutModelRequest = modelRequest
            it.creditCardNumber = '5431111111111111'
            it.creditCardName = 'john smith'
            it.expiryMonth = '11'
            it.expiryYear = '2200'
            it.creditCardCvv = '321'
            it.agreementFlag = true
            it.sessionId = "sessionId"
            it.ccAmount = ccAmount
            it
        }
    }

    private static PaymentRequest zeroPaymentRequest(CheckoutModelRequest modelRequest) {
        new PaymentRequest().with {
            it.checkoutModelRequest = modelRequest
            it.agreementFlag = true
            it.sessionId = "sessionId"
            it.ccAmount = 0.00
            it
        }
    }


    private static CheckoutModelRequest modelRequest(String payerId, Double payNow, List<String> classes , List<String> vouchers) {
        return new CheckoutModelRequest().with { modelRequest ->
            modelRequest.contactNodes = [new ContactNode().with { cNode ->
                cNode.contactId = payerId
                classes.each { classId ->
                    cNode.enrolments << new Enrolment().with { e ->
                        e.classId = classId
                        e.contactId = payerId
                        e.price = new CourseClassPrice().with { price ->
                            price.fee = 220.00
                            price.hasTax = true
                            price
                        }
                        e.selected = true
                        e
                    }
                }

                cNode
            }]
            modelRequest.redeemedVoucherIds = vouchers
            modelRequest.payerId = payerId
            modelRequest.payNow = payNow
            modelRequest
        }
    }

}
