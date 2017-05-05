package ish.oncourse.willow.service

import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.web.FieldSet
import ish.oncourse.willow.functions.ContactDetailsBuilder
import ish.oncourse.willow.model.field.ClassHeadings
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.*

import static org.junit.Assert.*

class ContactDetailsTest extends  ApiTest{

    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/ContactDetailsTest.xml'
    }
    
    @Ignore
    @Test
    void test() {
        ObjectContext context = cayenneRuntime.newContext()
        
        Contact contact = SelectById.query(Contact, 1001L).selectOne(context)
        CourseClass courseClass = SelectById.query(CourseClass, 1001L).selectOne(context)

        ClassHeadings classHeadings = new ContactDetailsBuilder().getContactDetails(contact, courseClass, FieldSet.ENROLMENT)

        List<Field> fields =  classHeadings.headings*.fields
        assertEquals(25l, fields.size())

        //check ordering
//        int prevOrder = -1
//        fields.subList(0, 23).each { f ->
//            assertEquals(1l,f.fieldHeading.id)
//            assertTrue(f.order > prevOrder)
//            prevOrder = f.order
//        }
//        prevOrder = -1
//        classHeadings.headings*.fields.subList(23, 24).each { f ->
//            assertEquals(2l,f.fieldHeading.id)
//            assertTrue(f.order > prevOrder)
//            prevOrder = f.order
//        }
//
//        //check that all fields filled
//        contact = SelectById.query(Contact, 1002L).selectOne(context)
//        classHeadings = new ContactDetailsBuilder().getContactDetails(contact, course, FieldSet.ENROLMENT)
//        assertEquals(1, fields.size())
//        assertEquals('customField.carMaker', fields[0].property)
//

    }
}
