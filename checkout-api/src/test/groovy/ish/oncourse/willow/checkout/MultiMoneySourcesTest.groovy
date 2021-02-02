package ish.oncourse.willow.checkout

import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.model.Invoice
import ish.oncourse.model.PaymentIn
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.web.CourseClassPrice
import ish.oncourse.willow.service.ApiTest
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class MultiMoneySourcesTest extends ApiTest {
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/checkout/MultiMoneySourcesTest.xml'
    }

    @Test
    void testTwoEqCredit() {

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        CheckoutModelRequest modelRequest = modelRequest('1100', 220.00, ['1001'], [])
        api.makePayment(zeroPaymentRequest(modelRequest))
        PaymentIn payment = ObjectSelect.query(PaymentIn).selectOne(cayenneService.newContext())
        assertEquals(Money.ZERO, payment.amount)
    }

    @Test
    void testMultiCredit_PPlan() {

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        CheckoutModelRequest modelRequest = modelRequest('1005', 200.00, ['1003'], [])
        api.makePayment(zeroPaymentRequest(modelRequest))
        PaymentIn payment = ObjectSelect.query(PaymentIn).selectOne(cayenneService.newContext())
        assertEquals(Money.ZERO, payment.amount)

        assertEquals(PaymentStatus.SUCCESS, payment.status)

        assertNotNull(payment.paymentInLines.find { it.invoice.id == 1005l })
        assertEquals(new Money("-50.00"), payment.paymentInLines.find { it.invoice.id == 1005l }.amount)

        assertNotNull(payment.paymentInLines.find { it.invoice.id == 1006l })
        assertEquals(new Money("-100.00"), payment.paymentInLines.find { it.invoice.id == 1006l }.amount)

        assertNotNull(payment.paymentInLines.find { it.invoice.id == 1007l })
        assertEquals(new Money("-50.00"), payment.paymentInLines.find { it.invoice.id == 1007l }.amount)

        assertEquals(EnrolmentStatus.SUCCESS, ObjectSelect.query(ish.oncourse.model.Enrolment).selectOne(cayenneService.newContext()).status)
    }

    @Test
    void testCredit_only() {
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
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
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
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

    @Test
    void testCredit_CCPayment_CourseVoucher_MoneyVoucher() {
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        CheckoutModelRequest modelRequest = modelRequest('1001', 440.00, ['1001', '1002'], ['1001', '1002'])
        api.makePayment(validPaymentRequest(modelRequest, 20.00))

        List<PaymentIn> payment = ObjectSelect.query(PaymentIn).select(cayenneService.newContext())
        PaymentIn ccPayment = payment.find {it.type == PaymentType.CREDIT_CARD}
        PaymentIn courseVoucherPayment = payment.find {it.type == PaymentType.VOUCHER && it.voucherPaymentIns[0].voucher.id == 1001l }
        PaymentIn moneyVoucherPayment = payment.find {it.type == PaymentType.VOUCHER && it.voucherPaymentIns[0].voucher.id == 1002l}

        assertEquals(new Money("20.00"), ccPayment.amount)
        assertEquals(PaymentStatus.SUCCESS, ccPayment.status)
        assertEquals(3, ccPayment.paymentInLines.size())
        assertNotNull(ccPayment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001l})
        assertEquals(new Money("0.00"), ccPayment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1001l}.amount)
        assertNotNull(ccPayment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1002l})
        assertEquals(new Money("120.00"), ccPayment.paymentInLines.find {it.invoice.invoiceLines[0].enrolment?.courseClass?.id == 1002l}.amount)
        assertNotNull(ccPayment.paymentInLines.find {it.invoice.id == 1003l})
        assertEquals(new Money("-100.00"), ccPayment.paymentInLines.find {it.invoice.id == 1003l}.amount)

        assertEquals(PaymentStatus.SUCCESS, courseVoucherPayment.status)
        assertEquals(new Money("220.00"), courseVoucherPayment.amount)
        assertEquals(1, courseVoucherPayment.paymentInLines.size())
        assertEquals(new Money("220.00"), courseVoucherPayment.paymentInLines[0].amount)

        assertEquals(PaymentStatus.SUCCESS, moneyVoucherPayment.status)
        assertEquals(new Money("100.00"), moneyVoucherPayment.amount)
        assertEquals(1, moneyVoucherPayment.paymentInLines.size())
        assertEquals(new Money("100.00"), moneyVoucherPayment.paymentInLines[0].amount)

        List<ish.oncourse.model.Enrolment> enrolments = ObjectSelect.query(ish.oncourse.model.Enrolment).select(cayenneService.newContext())
        assertEquals(2, enrolments.size())
        enrolments.each {
            assertEquals(EnrolmentStatus.SUCCESS, it.status)

        }
    }

    @Test
    void testCredit_CCPayment_CourseVoucher_MoneyVoucher_SingleInvoice() {
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        CheckoutModelRequest modelRequest = modelRequest('1001', 440.00, ['1011', '1012'], ['1001', '1002'])
        api.makePayment(validPaymentRequest(modelRequest, 20.00))

        List<PaymentIn> payment = ObjectSelect.query(PaymentIn).select(cayenneService.newContext())
        PaymentIn ccPayment = payment.find {it.type == PaymentType.CREDIT_CARD}
        PaymentIn courseVoucherPayment = payment.find {it.type == PaymentType.VOUCHER && it.voucherPaymentIns[0].voucher.id == 1001l }
        PaymentIn moneyVoucherPayment = payment.find {it.type == PaymentType.VOUCHER && it.voucherPaymentIns[0].voucher.id == 1002l}

        assertEquals(new Money("20.00"), ccPayment.amount)
        assertEquals(PaymentStatus.SUCCESS, ccPayment.status)
        assertEquals(2, ccPayment.paymentInLines.size())

        assertNotNull(ccPayment.paymentInLines.find {it.invoice.id != 1003l})
        assertEquals(new Money("120.00"), ccPayment.paymentInLines.find {it.invoice.id != 1003l}.amount)
        assertNotNull(ccPayment.paymentInLines.find {it.invoice.id == 1003l})
        assertEquals(new Money("-100.00"), ccPayment.paymentInLines.find {it.invoice.id == 1003l}.amount)

        assertEquals(PaymentStatus.SUCCESS, courseVoucherPayment.status)
        assertEquals(new Money("220.00"), courseVoucherPayment.amount)
        assertEquals(1, courseVoucherPayment.paymentInLines.size())
        assertEquals(new Money("220.00"), courseVoucherPayment.paymentInLines[0].amount)

        assertEquals(PaymentStatus.SUCCESS, moneyVoucherPayment.status)
        assertEquals(new Money("100.00"), moneyVoucherPayment.amount)
        assertEquals(1, moneyVoucherPayment.paymentInLines.size())
        assertEquals(new Money("100.00"), moneyVoucherPayment.paymentInLines[0].amount)

        Invoice invoice = ccPayment.paymentInLines.find{it.invoice.id != 1003l}.invoice

        assertEquals(3, invoice.paymentInLines.size())
        assertNotNull( invoice.paymentInLines.find {it.amount == new Money("220.00")})
        assertNotNull( invoice.paymentInLines.find {it.amount == new Money("120.00")})
        assertNotNull( invoice.paymentInLines.find {it.amount == new Money("100.00")})

        List<ish.oncourse.model.Enrolment> enrolments = ObjectSelect.query(ish.oncourse.model.Enrolment).select(cayenneService.newContext())
        assertEquals(2, enrolments.size())
        enrolments.each {
            assertEquals(EnrolmentStatus.SUCCESS, it.status)

        }
    }

    @Test
    void testCredit_CCPayment_Revers() {
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        CheckoutModelRequest modelRequest = modelRequest('1001', 440.00, ['1011', '1012'], [])

        api.makePayment(invalidPaymentRequest(modelRequest, 340.00))

        PaymentIn payment = ObjectSelect.query(PaymentIn).where(PaymentIn.TYPE.eq(PaymentType.CREDIT_CARD)).selectOne(cayenneService.newContext())
        assertNotNull(payment)
        assertEquals(PaymentStatus.FAILED_CARD_DECLINED, payment.status)

        PaymentIn reversPayment = ObjectSelect.query(PaymentIn).where(PaymentIn.TYPE.eq(PaymentType.REVERSE)).selectOne(cayenneService.newContext())
        assertNotNull(reversPayment)
        assertEquals(PaymentStatus.SUCCESS, reversPayment.status)
        assertEquals(Money.ZERO, reversPayment.amount)
        assertEquals(2, reversPayment.paymentInLines.size())
        assertEquals(2, reversPayment.paymentInLines.size())
        assertNotNull(reversPayment.paymentInLines.find {it.amount == new Money("440.00")})
        assertNotNull(reversPayment.paymentInLines.find {it.amount == new Money("-440.00")})

        Invoice credit =  SelectById.query(Invoice, 1003l).selectOne(cayenneService.newContext())

        assertEquals(1, credit.paymentInLines.size())

        assertEquals(new Money("-100.00"), credit.paymentInLines[0].amount)
        assertNotNull(credit.paymentInLines[0].paymentIn)
        assertEquals(PaymentType.CREDIT_CARD, credit.paymentInLines[0].paymentIn.type)

    }

    private static PaymentRequest invalidPaymentRequest(CheckoutModelRequest modelRequest,   Double ccAmount) {
        PaymentRequest request = validPaymentRequest(modelRequest, ccAmount)
        request.creditCardNumber = '9999990000000378'
        return request
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
