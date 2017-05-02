package ish.oncourse.willow.service

import ish.oncourse.model.Contact
import ish.oncourse.model.Course
import ish.oncourse.willow.model.FieldSet
import ish.oncourse.willow.functions.ContactDetailsBuilder
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.Test

class ContactDetailsTest extends  ApiTest{


    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/ContactDetailsTest.xml'

    }
    
    @Test
    void test() {
        ObjectContext context = cayenneRuntime.newContext()
        
        Contact contact = SelectById.query(Contact, 1001L).selectOne(context)
        Course course = SelectById.query(Course, 1001L).selectOne(context)

        new ContactDetailsBuilder().getContactDetails(contact, course, FieldSet.ENROLMENT)
    }
}
