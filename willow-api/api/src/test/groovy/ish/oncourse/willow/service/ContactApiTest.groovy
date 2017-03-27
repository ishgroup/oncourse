package ish.oncourse.willow.service

import ish.oncourse.willow.model.Contact
import ish.oncourse.willow.service.impl.ContactApiServiceImpl
import org.junit.Test

import static org.junit.Assert.assertEquals

class ContactApiTest extends ApiTest {
    
    @Test
    void getContactTest() {
        ContactApi api = new ContactApiServiceImpl(cayenneRuntime)
        
        Contact contact = api.getContact("1wjdestablisheq")
        assertEquals(contact.email, "Student3@Student3.net")
        assertEquals(contact.firstName, "Student3")
        assertEquals(contact.lastName, "Student3")
        assertEquals(contact.id, "1003")
        assertEquals(contact.uniqueIdentifier, "1wjdestablisheq")
        

    }

    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/CourseClassesApiTest.xml'
    }

}
