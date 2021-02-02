package ish.oncourse.willow.service

import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.Application
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import ish.oncourse.willow.model.checkout.Voucher
import static org.junit.Assert.*
import ish.oncourse.willow.service.impl.CollegeService
import org.junit.Test

class CalculateAmountTest extends ApiTest {
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/CalculateAmountTest.xml'
    }
    
    @Test
    void testGetAmount() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)


        CheckoutModelRequest checkoutModelRequest = new CheckoutModelRequest().with { modelRequest ->
            modelRequest.contactNodes = [new ContactNode().with { cNode ->
                cNode.contactId = '1001'
                cNode.enrolments = [new Enrolment().with { e ->
                    e.classId = '1001'
                    e.contactId = '1001'
                    e.selected = true
                    e
                }]
                cNode.applications = [new Application().with { a ->
                    a.contactId = '1001'
                    a.classId = '1002'
                    a.selected = true
                    a
                }]
                cNode.vouchers = [new Voucher().with { v ->
                    v.price = 150.00
                    v.value = 150.00
                    v.productId = '7'
                    v.contactId = '1001'
                    v.selected = true
                    v.isEditablePrice = true
                    v.quantity = 1
                    v.total = 150.00
                    v
                }]
                cNode
            }]
            modelRequest.promotionIds = ['1002']
            modelRequest.payerId = '1001'
            modelRequest
        }


        CheckoutModel model = api.getCheckoutModel(checkoutModelRequest)
        
        assertNotNull(model.amount)
        assertEquals(370.00, model.amount.total, 0)
        assertEquals(22.00, model.amount.discount,0)
        assertEquals(320.00, model.amount.payNow,0)
        assertEquals(28.00, model.amount.owing,0)
        
    }
}
