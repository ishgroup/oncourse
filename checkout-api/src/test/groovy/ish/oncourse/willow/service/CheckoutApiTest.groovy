package ish.oncourse.willow.service

import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import ish.oncourse.willow.model.checkout.request.ProductContainer
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
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        ContactNodeRequest request = new ContactNodeRequest().with { request ->
            request.contactId = '1001'
            request.classIds = ['1001', '1002']
            request.products = [[productId:'7', quantity:1], [productId:'8', quantity:1], [productId:'12', quantity:1]] as ProductContainer[]
            request
        }

        ContactNode items = api.getContactNode(request)

        assertEquals(1, items.enrolments.size())
        assertEquals(1, items.applications.size())
        assertEquals(1, items.articles.size())
        assertEquals(1, items.memberships.size())
        assertEquals(1, items.vouchers.size())
    }

    @Test
    void getPurchaseItemsWithQuantityTest() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        ContactNodeRequest request = new ContactNodeRequest().with { request ->
            request.contactId = '1001'
            request.classIds = ['1001', '1002']
            request.products = [[productId:'8', quantity:3], [productId:'12', quantity:4], [productId:'13', quantity:5]] as ProductContainer[]
            request
        }

        ContactNode items = api.getContactNode(request)

        assertEquals(1, items.enrolments.size())
        assertEquals(1, items.applications.size())
        assertEquals(1, items.articles.size())
        assertEquals(4, items.articles[0].quantity)
        assertEquals(1, items.memberships.size())
        assertEquals(1, items.vouchers.size())
        assertEquals(5, items.vouchers[0].quantity)

    }
}
