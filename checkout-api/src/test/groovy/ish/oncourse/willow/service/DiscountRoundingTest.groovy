package ish.oncourse.willow.service

import ish.oncourse.model.College
import ish.oncourse.willow.checkout.functions.ProcessCheckoutModel
import ish.oncourse.willow.model.checkout.Amount
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Enrolment
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse


class DiscountRoundingTest extends ApiTest {
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/DiscountRoundingTest.xml'
    }
    
    
    @Test
    void testAmount() {

        ObjectContext context = cayenneService.newContext()
        College college = SelectById.query(College, 1l).selectOne(context)
        
        CheckoutModelRequest request = new CheckoutModelRequest().with { r ->
            r.contactNodes << new ContactNode().with { n ->
                n.contactId = '1001'
                n.enrolments << new Enrolment(contactId: '1001', classId: '1001', selected: true)
                n
            }
            r.payerId = '1001'
            r
        }
        
        Amount amount = new ProcessCheckoutModel(context, college, request).process().model.amount
        assertEquals(20.96, amount.discount, 0)
        assertEquals(83.80, amount.payNow, 0)
        assertEquals(104.76, amount.total, 0)

        assertFalse(amount.isEditable)
    }

}
