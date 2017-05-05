package ish.oncourse.willow.service

import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.ContactFieldsRequest
import ish.oncourse.willow.model.web.FieldSet

import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.ContactApiServiceImpl

import org.junit.*

import static org.junit.Assert.assertEquals


class ContactDetailsTest extends  ApiTest{
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/ContactDetailsTest.xml'
    }
    
    @Test
    void test() {
        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')
        ContactApi api = new ContactApiServiceImpl(cayenneRuntime, new CollegeService(cayenneRuntime))

        ContactFields fields = api.getContactFields(new ContactFieldsRequest(contactId: '1001', classesIds: ['1001'], fieldSet: FieldSet.ENROLMENT))
        
        def file = new File(getClass().getResource('/ish/oncourse/willow/service/contact-fields.txt').toURI())

        assertEquals(file.text, fields.toString())

        fields = api.getContactFields(new ContactFieldsRequest(contactId: '1002', classesIds: ['1001'], fieldSet: FieldSet.ENROLMENT))

        file = new File(getClass().getResource('/ish/oncourse/willow/service/contact-fields-empty.txt').toURI())
        
        assertEquals(file.text, fields.toString())

    }
}
