package ish.oncourse.willow.service

import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import ish.oncourse.willow.service.impl.CollegeService

import org.junit.Test
import static org.junit.Assert.*

/**
 * API tests for CheckoutApi
 */
class CheckoutApiTest extends ApiTest {

    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/CheckoutTest.xml'
    }
/**
     * Get list of purchases
     * Get list of enrolments/applications/product items for certain contact based on selected classes/products
     */
    @Test
    void getPurchaseItemsTest() {
        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, new CollegeService(cayenneService))
        ContactNodeRequest request = new ContactNodeRequest().with { request ->
            request.contactId = '1001'
            request.classIds = ['1001', '1002']
            request.productIds = ['7','8','12']
            request
        }

        ContactNode items = api.getContactNode(request)

        assertEquals(1, items.enrolments.size())
        assertEquals(1, items.applications.size())
        assertEquals(1, items.articles.size())
        assertEquals(1, items.memberships.size())
        assertEquals(1, items.vouchers.size())
    }
    
}
