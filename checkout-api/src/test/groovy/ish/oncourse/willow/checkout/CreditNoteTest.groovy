package ish.oncourse.willow.checkout

import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.web.CourseClassPrice
import ish.oncourse.willow.model.web.Discount
import ish.oncourse.willow.service.ApiTest
import org.junit.Test

import static org.junit.Assert.assertEquals

class CreditNoteTest extends ApiTest {

    private CheckoutModel model
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/checkout/CreditNoteTest.xml'
    }

    @Test
    void testGetAmount() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService)
        model = api.getCheckoutModel(createRequest(false, '1001'))

        assertEquals(210.00,  model.amount.credit, 0)
        assertEquals(220.00,  model.amount.total, 0)
        assertEquals(209.00,  model.amount.subTotal, 0)
        assertEquals(170.00,  model.amount.minPayNow, 0)
        assertEquals(39.00,  model.amount.owing, 0)
        assertEquals(11.00,  model.amount.discount, 0)
        assertEquals(true,  model.amount.isEditable)

        model = api.getCheckoutModel(createRequest(true, '1001'))
        
        assertEquals(209.00,  model.amount.credit, 0)
        assertEquals(220.00,  model.amount.total, 0)
        assertEquals(209.00,  model.amount.subTotal, 0)
        assertEquals(0.00,  model.amount.minPayNow, 0)
        assertEquals(0.00,  model.amount.owing, 0)
        assertEquals(11.00,  model.amount.discount, 0)
        assertEquals(true,  model.amount.isEditable)

        model = api.getCheckoutModel(createRequest(true, '1002'))

        assertEquals(200.00,  model.amount.credit, 0)
        assertEquals(220.00,  model.amount.total, 0)
        assertEquals(209.00,  model.amount.subTotal, 0)
        assertEquals(0.00,  model.amount.minPayNow, 0)
        assertEquals(9.00,  model.amount.owing, 0)
        assertEquals(11.00,  model.amount.discount, 0)
        assertEquals(true,  model.amount.isEditable)
        
    }

    @Test
    void testVoucherWithCredit() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService)
        
        model = api.getCheckoutModel(createRequest(true, '1003', ['1001', '1002'], ['1001','1002']))

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

    private CheckoutModelRequest createRequest(boolean applyCredit, String payerId,List<String> classes = ['1001'], List<String> vouchers=[]) {
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
            modelRequest.applyCredit = applyCredit
            modelRequest
        }
    }
    
    
    
}
