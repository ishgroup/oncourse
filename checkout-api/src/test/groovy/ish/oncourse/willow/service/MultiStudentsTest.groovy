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

class MultiStudentsTest extends ApiTest {


    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/MultiStudentsTest.xml'
    }


    @Test
    void test() {
        ObjectContext context = cayenneService.newContext()
        College college = SelectById.query(College, 1l).selectOne(context)

        Amount amount = new ProcessCheckoutModel(context, college, getModelRequest()).process().model.amount

        assertEquals(amount.payNow, 209.00, 0)

    }


    private CheckoutModelRequest getModelRequest() {
        new CheckoutModelRequest().with { r ->
            r.contactNodes << new ContactNode().with { n ->
                n.contactId = '1001'
                n.enrolments << new Enrolment(contactId: '1001', classId: '1001', selected: true)
                n
            }
            r.contactNodes << new ContactNode().with { n ->
                n.contactId = '1002'
                n.enrolments << new Enrolment(contactId: '1002', classId: '1001', selected: true)
                n
            }
            r.payerId = '1001'
            r
        }
    }
}
