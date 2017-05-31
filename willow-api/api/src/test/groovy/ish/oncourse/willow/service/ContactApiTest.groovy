package ish.oncourse.willow.service

import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.web.Contact
import ish.oncourse.willow.model.web.CreateContactParams
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.ContactApiServiceImpl
import org.apache.cayenne.query.SelectById
import org.junit.Test

import static ish.oncourse.willow.model.field.FieldSet.ENROLMENT
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class ContactApiTest extends ApiTest {
    
    @Test
    void getContactTest() {
        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')
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
        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))
        String id = api.createOrGetContact(new CreateContactParams(firstName: 'Student1', lastName:'Student1', email:'Student1@Student1.net', fieldSet: ENROLMENT))

        assertEquals("1001", id)

        id = api.createOrGetContact(new CreateContactParams(firstName: 'Student2', lastName:'Student2', email:'Student2@Student2.net', fieldSet: ENROLMENT))
        
        assertNotNull(id)

        ish.oncourse.model.Contact contact = SelectById.query(ish.oncourse.model.Contact, id).selectOne(cayenneRuntime.newContext())

        assertEquals('Student2', contact.givenName)
        assertEquals('Student2', contact.familyName)
        assertEquals('Student2@Student2.net', contact.emailAddress)
        assertNotNull(contact.uniqueCode)


        assertEquals(id, api.createOrGetContact(new CreateContactParams(firstName: 'Student2', lastName:'Student2', email:'Student2@Student2.net', fieldSet: ENROLMENT)))
        
    }

    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/CourseClassesApiTest.xml'
    }

}
