package ish.oncourse.willow.service


import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import ish.oncourse.willow.service.impl.CollegeService
import org.junit.Test

import static org.junit.Assert.*

class GetContactNodeTest extends ApiTest {

    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/GetContactNodeTest.xml'
    }
    
    @Test
    void testGetContactNode() {
        
        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')
        CollegeService service = new CollegeService(cayenneService)
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, service)

        ContactNodeRequest nodeRequest = new ContactNodeRequest().with { request -> 
            request.contactId = '1001'
            request.classIds = ['1001', '1002', '1005']
            request.productIds = ['7', '8', '12']
            request
        }
        
        ContactNode node = api.getContactNode(nodeRequest)
        assertEquals('1001', node.contactId)
        assertEquals(2, node.enrolments.size())
        assertEquals('1001', node.enrolments[0].classId)
        assertTrue(node.enrolments[0].errors.empty)
        assertTrue(node.enrolments[0].selected)
        assertEquals('1005', node.enrolments[1].classId)
        assertFalse(node.enrolments[1].selected)
        assertEquals(1, node.enrolments[1].errors.size())
        assertEquals('Student1 Student1 is already enrolled in this class. Please select another class from <a href="/course/AAB"> this course</a>.', node.enrolments[1].errors[0])
        
        assertEquals(1, node.applications.size())
        assertEquals('1002', node.applications[0].classId)
        assertTrue( node.applications[0].selected)
        assertTrue(node.applications[0].errors.empty)
        
        assertEquals(1, node.vouchers.size())
        assertEquals('7', node.vouchers[0].productId)
        assertTrue(node.vouchers[0].errors.empty)
        assertTrue(node.vouchers[0].selected)
        assertEquals('100.00',node.vouchers[0].value)
        assertEquals('100.00',node.vouchers[0].price)
        assertTrue(node.vouchers[0].isEditablePrice)
        
        assertEquals(1, node.articles.size())
        assertEquals('12', node.articles[0].productId)
        assertTrue(node.articles[0].selected)
        assertTrue(node.articles[0].errors.empty)
        
        assertEquals(1, node.memberships.size())
        assertEquals('8', node.memberships[0].productId)
        assertFalse(node.memberships[0].selected)
        assertEquals(1, node.memberships[0].errors.size())
        assertEquals('Student1 Student1 is already has this membership 234567.', node.memberships[0].errors[0])

    }
    
}
