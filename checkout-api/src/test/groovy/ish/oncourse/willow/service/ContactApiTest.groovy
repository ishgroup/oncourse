package ish.oncourse.willow.service

import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.web.Contact
import ish.oncourse.willow.model.web.ContactId
import ish.oncourse.willow.model.web.CreateContactParams
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.ContactApiServiceImpl
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static ish.oncourse.willow.model.field.FieldSet.ENROLMENT
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class ContactApiTest extends ApiTest {
    
    @Test
    void getContactTest() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))
        
        Contact contact = api.getContact("1wjdestablisheq")
        assertEquals(contact.email, "Student3@Student3.net")
        assertEquals(contact.firstName, "Student3")
        assertEquals(contact.lastName, "Student3")
        assertEquals(contact.id, "1003")
        assertEquals(contact.uniqueIdentifier, "1wjdestablisheq")
        

    }


    @Test
    void getorCreateContact() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))
        ContactId contactId = api.createOrGetContact(new CreateContactParams(firstName: 'Student1', lastName:'Student1', email:'Student1@Student1.net', fieldSet: ENROLMENT))

        assertEquals("1001", contactId.id)
        assertEquals(false, contactId.newContact)

        contactId = api.createOrGetContact(new CreateContactParams(firstName: 'Student5', lastName:'Student5', email:'Student5@Student5.net', fieldSet: ENROLMENT))
        
        assertNotNull(contactId.id)
        assertTrue(contactId.newContact)


        ish.oncourse.model.Contact contact = SelectById.query(ish.oncourse.model.Contact, contactId.id).selectOne(cayenneRuntime.newContext())

        assertEquals('Student5', contact.givenName)
        assertEquals('Student5', contact.familyName)
        assertEquals('Student5@Student5.net', contact.emailAddress)
        assertNotNull(contact.uniqueCode)

    }

    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/CourseClassesApiTest.xml'
    }

}
