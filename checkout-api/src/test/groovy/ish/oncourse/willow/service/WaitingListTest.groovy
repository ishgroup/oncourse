package ish.oncourse.willow.service

import ish.oncourse.model.Contact
import ish.oncourse.model.Student
import ish.oncourse.model.WaitingList
import ish.oncourse.willow.checkout.CheckoutApiImpl
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static org.junit.Assert.*

class WaitingListTest extends ApiTest {
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/WaitingListTest.xml'
    }
    
    @Test
    void testGetNode() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        CheckoutApiImpl api = new CheckoutApiImpl(cayenneService, collegeService, financialService, entityRelationService)
        ContactNodeRequest request = new ContactNodeRequest().with { r ->
            r.contactId = '1002'
            r.waitingCourseIds << '1001'
            r
        }
        
        ContactNode contactNode =  api.getContactNode(request)
        assertEquals(contactNode.waitingLists.size(), 1)
        assertEquals(contactNode.waitingLists[0].errors.size(), 1)
        assertEquals(contactNode.waitingLists[0].errors[0].toString(), 'Student Student1 Student1 has already been added to waiting list for Managerial Accounting course')

        request = new ContactNodeRequest().with { r ->
            r.contactId = '1001'
            r.waitingCourseIds << '1001'
            r
        }
        contactNode = api.getContactNode(request)
        assertEquals(contactNode.waitingLists.size(), 1)
        assertTrue(contactNode.waitingLists[0].errors.empty)
        assertEquals(contactNode.waitingLists[0].selected, true)
        assertEquals(contactNode.waitingLists[0].fieldHeadings.size(), 1)
        assertEquals(contactNode.waitingLists[0].fieldHeadings[0].name, 'First Heading')
        assertEquals(contactNode.waitingLists[0].fieldHeadings[0].fields.size(), 1)
        assertEquals(contactNode.waitingLists[0].fieldHeadings[0].fields[0].name,'Favorite car maker')
        assertEquals(contactNode.waitingLists[0].fieldHeadings[0].fields[0].key,'customField.waitingList.carMaker')


        CheckoutModel model = api.getCheckoutModel(new CheckoutModelRequest().with { r -> 
            r.contactNodes << contactNode
            r.payerId = '1001'
            r
        })
        
        assertNull(model.error)
        assertNotNull(model.validationErrors)
        assertEquals(model.validationErrors.formErrors.size(),1)
        assertEquals(model.validationErrors.formErrors[0],'Favorite car maker for Managerial Accounting is required')
        assertEquals(model.validationErrors.fieldsErrors.size(),1)
        assertEquals(model.validationErrors.fieldsErrors[0].name,'customField.waitingList.carMaker')
        assertEquals(model.validationErrors.fieldsErrors[0].error,'Favorite car maker for Managerial Accounting is required')
        assertEquals(model.contactNodes.size(), 1)
        assertEquals(model.contactNodes[0].waitingLists.size(), 1)
        assertEquals(model.contactNodes[0].waitingLists[0].contactId, '1001')
        assertEquals(model.contactNodes[0].waitingLists[0].courseId, '1001')
        assertTrue(model.contactNodes[0].waitingLists[0].selected)
       
        contactNode.waitingLists[0].fieldHeadings[0].fields[0].value = 'BMW'
        contactNode.waitingLists[0].studentsCount = 2
        contactNode.waitingLists[0].detail = 'detail'

        model = api.getCheckoutModel(new CheckoutModelRequest().with { r ->
            r.contactNodes << contactNode
            r.payerId = '1001'
            r
        })

        assertNull(model.error)
        assertEquals(model.validationErrors.formErrors.size(),0)
        assertEquals(model.validationErrors.fieldsErrors.size(),0)
        assertEquals(model.contactNodes.size(), 1)
        assertEquals(model.contactNodes[0].waitingLists.size(), 1)
        assertEquals(model.contactNodes[0].waitingLists[0].contactId, '1001')
        assertEquals(model.contactNodes[0].waitingLists[0].courseId, '1001')
        assertEquals(model.contactNodes[0].waitingLists[0].studentsCount, 2, 0)
        assertEquals(model.contactNodes[0].waitingLists[0].detail, 'detail')
        assertTrue(model.contactNodes[0].waitingLists[0].selected)
        
        api.makePayment(new PaymentRequest().with { r ->
            r.agreementFlag = true
            r.checkoutModelRequest = new CheckoutModelRequest().with { m -> 
                m.contactNodes += model.contactNodes
                m.payerId = model.payerId
                m
            }
            r
        })

        ObjectContext context = cayenneService.newContext()
        Contact contact = SelectById.query(Contact, 1001L).selectOne(context)
        WaitingList waitingList = ObjectSelect.query(WaitingList).where(WaitingList.STUDENT.dot(Student.CONTACT).eq(contact)).selectFirst(context)
        assertEquals(waitingList.course.id, 1001L)
        assertEquals(waitingList.potentialStudents, 2)
        assertEquals(waitingList.detail, 'detail')
        assertEquals(waitingList.college.id, 1L)
        assertEquals(waitingList.customFields.size(), 1)
        assertEquals(waitingList.customFields[0].value, 'BMW')
        assertEquals(waitingList.customFields[0].customFieldType.name, 'Favorite car maker')
    }
}
