package ish.oncourse.willow.checkout

import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.web.CourseClassPrice
import ish.oncourse.willow.model.web.Discount
import ish.oncourse.willow.service.ApiTest
import ish.oncourse.willow.service.impl.CollegeService
import org.junit.Test

import static org.junit.Assert.assertEquals

class CreditNoteTest extends ApiTest {
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/CreditNoteTest.xml'
    }

    @Test
    void testGetAmount() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')

        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, new CollegeService(cayenneService))

        CheckoutModelRequest request = new CheckoutModelRequest().with { modelRequest ->
            modelRequest.contactNodes = [new ContactNode().with { cNode ->
                cNode.contactId = '1001'
                cNode.enrolments = [new Enrolment().with { e ->
                    e.classId = '1001'
                    e.contactId = '1001'
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
                }]
               
                cNode
            }]
            modelRequest.promotionIds = ['1001']
            modelRequest.payerId = '1001'
            modelRequest
        }

        CheckoutModel model = api.getCheckoutModel(request)

        assertEquals(200.00,  model.amount.credit, 0)
        assertEquals(220.00,  model.amount.total, 0)
        assertEquals(209.00,  model.amount.subTotal, 0)
        assertEquals(0.00,  model.amount.payNow, 0)
        assertEquals(0.00,  model.amount.minPayNow, 0)
        assertEquals(9.00,  model.amount.owing, 0)
        assertEquals(11.00,  model.amount.discount, 0)
        assertEquals(true,  model.amount.isEditable)

    }
    
    
    
}
