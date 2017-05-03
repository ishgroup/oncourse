package ish.oncourse.willow.service

import ish.oncourse.model.Contact
import ish.oncourse.model.Course
import ish.oncourse.model.Field
import ish.oncourse.willow.model.FieldSet
import ish.oncourse.willow.functions.ContactDetailsBuilder
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.*

import static org.junit.Assert.*

class ContactDetailsTest extends  ApiTest{


    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/ContactDetailsTest.xml'
    }
    
    @Test
    @Ignore
    void test() {
        ObjectContext context = cayenneRuntime.newContext()
        
        Contact contact = SelectById.query(Contact, 1001L).selectOne(context)
        Course course = SelectById.query(Course, 1001L).selectOne(context)

        List<Field> fields = new ContactDetailsBuilder().getContactDetails(contact, course, FieldSet.ENROLMENT)

        assertEquals(25l, fields.size())

        //check ordering
        int prevOrder = -1
        fields.subList(0, 23).each { f ->
            assertEquals(1l,f.fieldHeading.id)
            assertTrue(f.order > prevOrder)
            prevOrder = f.order
        }
        prevOrder = -1
        fields.subList(23, 24).each { f ->
            assertEquals(2l,f.fieldHeading.id)
            assertTrue(f.order > prevOrder)
            prevOrder = f.order
        }

        //check that all fields filled
        contact = SelectById.query(Contact, 1002L).selectOne(context)
        fields = new ContactDetailsBuilder().getContactDetails(contact, course, FieldSet.ENROLMENT)
        assertEquals(1, fields.size())
        assertEquals('customField.carMaker', fields[0].property)


    }
}
