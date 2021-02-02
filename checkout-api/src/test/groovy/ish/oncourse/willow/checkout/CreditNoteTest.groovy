package ish.oncourse.willow.checkout

import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.model.PaymentIn
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.web.CourseClassPrice
import ish.oncourse.willow.model.web.Discount
import ish.oncourse.willow.service.ApiTest
import org.apache.cayenne.query.ObjectSelect
import org.junit.Test

import static ish.common.types.PaymentStatus.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class CreditNoteTest extends ApiTest {

    private CheckoutModel model
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/checkout/CreditNoteTest.xml'
    }

    @Test
    void testGetAmount() {
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        model = api.getCheckoutModel(createRequest( '1001'))

        assertEquals(170.00,  model.amount.credit, 0)
        assertEquals(220.00,  model.amount.total, 0)
        assertEquals(209.00,  model.amount.subTotal, 0)
        assertEquals(170.00,  model.amount.minPayNow, 0)
        assertEquals(170.00,  model.amount.payNow, 0)
        assertEquals(39.00,  model.amount.owing, 0)
        assertEquals(11.00,  model.amount.discount, 0)
        assertEquals(0.00,  model.amount.ccPayment, 0)

        assertEquals(true,  model.amount.isEditable)

        model = api.getCheckoutModel(createRequest( '1001', 209))

        assertEquals(209.00,  model.amount.credit, 0)
        assertEquals(220.00,  model.amount.total, 0)
        assertEquals(209.00,  model.amount.subTotal, 0)
        assertEquals(170.00,  model.amount.minPayNow, 0)
        assertEquals(0.00,  model.amount.ccPayment, 0)
        assertEquals(0.00,  model.amount.owing, 0)
        assertEquals(11.00,  model.amount.discount, 0)
        assertEquals(true,  model.amount.isEditable)

        model = api.getCheckoutModel(createRequest( '1002', 209))

        assertEquals(200.00,  model.amount.credit, 0)
        assertEquals(220.00,  model.amount.total, 0)
        assertEquals(209.00,  model.amount.subTotal, 0)
        assertEquals(170.00,  model.amount.minPayNow, 0)
        assertEquals(9.00,  model.amount.ccPayment, 0)
        assertEquals(0.00,  model.amount.owing, 0)
        assertEquals(11.00,  model.amount.discount, 0)
        assertEquals(true,  model.amount.isEditable)
        
    }

    @Test
    void testVoucherWithCredit() {
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        
        model = api.getCheckoutModel(createRequest( '1003', null, ['1001', '1002'], ['1001','1002']))

        assertEquals(440.00,  model.amount.total, 0)
        assertEquals(22.00,  model.amount.discount, 0)
        assertEquals(418.00,  model.amount.subTotal, 0)
        assertEquals(379.00,  model.amount.minPayNow, 0)
        assertEquals(379.00,  model.amount.payNow, 0)
        assertEquals(true,  model.amount.isEditable)

        assertEquals(2,  model.amount.voucherPayments.size())
        assertEquals(209,  model.amount.voucherPayments[0].getAmount(), 0)
        assertEquals('1001',  model.amount.voucherPayments[0].redeemVoucherId)
        assertEquals(150,  model.amount.voucherPayments[1].getAmount(), 0)
        assertEquals('1002',  model.amount.voucherPayments[1].redeemVoucherId)

        assertEquals(20,  model.amount.credit, 0)
        assertEquals(0,  model.amount.ccPayment, 0)
        assertEquals(39,  model.amount.owing, 0)
    }

    @Test
    void testMakePayment() {
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        api.makePayment(new PaymentRequest().with {
            it.checkoutModelRequest = createRequest('1003', null, ['1001', '1002'], ['1001','1002'])
            it.creditCardNumber = '5431111111111111'
            it.creditCardName = 'john smith'
            it.expiryMonth = '11'
            it.expiryYear = '2027'
            it.creditCardCvv = '321'
            it.agreementFlag = true
            it.sessionId = 'paymentRandomSession'
            it.ccAmount = 0
            it
        })

        PaymentIn payment = ObjectSelect.query(PaymentIn).where(PaymentIn.SESSION_ID.eq('paymentRandomSession')).selectOne(cayenneService.newContext())
        assertEquals(Money.ZERO, payment.amount)
        assertEquals(SUCCESS, payment.status)
        assertEquals(PaymentType.INTERNAL, payment.type)
        assertEquals(3, payment.paymentInLines.size())
        assertNotNull(payment.paymentInLines.find {it.amount == 0.toMoney()})
        assertNotNull(payment.paymentInLines.find {it.amount == 20.toMoney()})
        assertNotNull(payment.paymentInLines.find {it.amount == -20.toMoney()})

    }

    private static CheckoutModelRequest createRequest(String payerId, Double payNow = null, List<String> classes = ['1001'], List<String> vouchers=[]) {
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
                            price.appliedDiscount = new Discount().with { discount ->
                                discount.id = '1001'
                                discount.discountedFee = 209.00
                                discount.discountValue = 11.00
                                discount.title = 'title'
                                discount
                            }
                            price
                        }
                        e.selected = true
                        e
                    }
                }

                cNode
            }]
            modelRequest.redeemedVoucherIds = vouchers
            modelRequest.promotionIds = ['1001']
            modelRequest.payerId = payerId
            modelRequest.payNow = payNow
            modelRequest
        }
    }
    
    
    
}
