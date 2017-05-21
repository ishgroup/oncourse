package ish.oncourse.willow.service

import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.request.PurchaseItemsRequest
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.ContactApiServiceImpl;

import java.util.List;
import ish.oncourse.willow.model.checkout.PurchaseItems;
import ish.oncourse.willow.model.common.CommonError;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneRuntime, new CollegeService(cayenneRuntime))
        PurchaseItemsRequest request = new PurchaseItemsRequest().with { request ->
            request.contactId = '1001'
            request.classesIds = ['1001', '1002']
            request.productIds = ['7','8','12']
            request
        }

        PurchaseItems items = api.getPurchaseItems(request)

        assertEquals(1, items.enrolments.size())
        assertEquals(1, items.applications.size())
        assertEquals(1, items.articles.size())
        assertEquals(1, items.memberships.size())
        assertEquals(1, items.vouchers.size())
    }
    
}
