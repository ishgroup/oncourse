package ish.oncourse.willow.service

import ish.oncourse.model.Contact
import ish.oncourse.model.Tag
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.ContactFieldsRequest
import ish.oncourse.willow.model.field.DataType
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.field.FieldSet
import ish.oncourse.willow.model.field.SubmitFieldsRequest
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.ContactApiServiceImpl
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.junit.Test

import javax.ws.rs.BadRequestException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class ContactDetailsTagTest extends ApiTest {
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/ContactDetailsTagTest.xml'
    }

    @Test
    void testGet() {
        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))

        ContactFields fields = api.getContactFields(new ContactFieldsRequest(contactId: '1001', classIds: ['1001'], fieldSet: FieldSet.ENROLMENT))

        def file = new File(getClass().getResource('/ish/oncourse/willow/service/contact-tag-fields.txt').toURI())

        assertEquals(file.text, fields.toString())
    }

    @Test
    void testSubmitWrongRequest() {
        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))

        try {
            api.submitContactDetails( wrongRequest())
        } catch (BadRequestException e) {
            def file = new File(getClass().getResource('/ish/oncourse/willow/service/validation-tag-error.txt').toURI())
            assertEquals(file.text, e.response.entity.toString() )
            return
        }
        assertFalse(true)
    }

    @Test
    void testSubmitProperRequest() {

        ObjectContext context = cayenneService.newContext()

        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))

        api.submitContactDetails(properRequest())

        Contact contact = SelectById.query(Contact, 1001l).selectOne(cayenneService.newContext())

        Closure<Boolean> isContactLinkedWith = { Tag tag ->
            tag.getTaggableTags().stream()
                    .map { tt -> tt.taggable.entityWillowId}
                    .filter { id -> id == contact.id}.count() > 0
        }

        Closure<Tag> getTagById = { long id ->
            SelectById.query(Tag, id).selectOne(context)
        }

        // non mandatory multiple tags

        Tag nonMandatoryMultiple = getTagById.call(1l)
        Tag tag1C1 = getTagById.call(2l)
        Tag tag1C2 = getTagById.call(3l)
        Tag tag1C2C1 = getTagById.call(4l)
        Tag tag1C2C2 = getTagById.call(5l)
        Tag tag1C2C2C1 = getTagById.call(6l)

        assertFalse(isContactLinkedWith.call(nonMandatoryMultiple))
        assertTrue(isContactLinkedWith.call(tag1C1))
        assertFalse(isContactLinkedWith.call(tag1C2))
        assertTrue(isContactLinkedWith.call(tag1C2C1))
        assertFalse(isContactLinkedWith.call(tag1C2C2))
        assertFalse(isContactLinkedWith.call(tag1C2C2C1))

        // non mandatory limit to one

        Tag nonMandatoryLimitToOne = getTagById.call( 7l)
        Tag tag2C1 = getTagById.call(8l)
        Tag tag2C2 = getTagById.call(9l)
        Tag tag2C3 = getTagById.call(10l)
        Tag tag2C4 = getTagById.call(11l)
        Tag tag2C5 = getTagById.call(12l)

        assertFalse(isContactLinkedWith.call(nonMandatoryLimitToOne))
        assertTrue(isContactLinkedWith.call(tag2C1))
        assertFalse(isContactLinkedWith.call(tag2C2))
        assertFalse(isContactLinkedWith.call(tag2C3))
        assertFalse(isContactLinkedWith.call(tag2C4))
        assertFalse(isContactLinkedWith.call(tag2C5))

        // mandatory multiple

        Tag mandatoryMultiple = getTagById.call(13l)
        Tag tag3C1 = getTagById.call(14l)
        Tag tag3C2 = getTagById.call(15l)
        Tag tag3C2C1 = getTagById.call(16l)
        Tag tag3C2C2 = getTagById.call(17l)
        Tag tag3C2C2C1 = getTagById.call(18l)

        assertFalse(isContactLinkedWith.call(mandatoryMultiple))
        assertFalse(isContactLinkedWith.call(tag3C1))
        assertFalse(isContactLinkedWith.call(tag3C2))
        assertTrue(isContactLinkedWith.call(tag3C2C1))
        assertFalse(isContactLinkedWith.call(tag3C2C2))
        assertTrue(isContactLinkedWith.call(tag3C2C2C1))

        // mandatory limit to one

        Tag mandatoryLimitToOne = getTagById.call(30l)
        Tag tag4C1 = getTagById.call(31l)
        Tag tag4C2 = getTagById.call(32l)
        Tag tag4C3 = getTagById.call(33l)
        Tag tag4C4 = getTagById.call(34l)
        Tag tag4C5 = getTagById.call(35l)

        assertFalse(isContactLinkedWith.call(mandatoryLimitToOne))
        assertTrue(isContactLinkedWith.call(tag4C1))
        assertFalse(isContactLinkedWith.call(tag4C2))
        assertFalse(isContactLinkedWith.call(tag4C3))
        assertFalse(isContactLinkedWith.call(tag4C4))
        assertFalse(isContactLinkedWith.call(tag4C5))

        // mailing lists

        Tag mailingList1 = getTagById.call(21l)
        Tag mailingList2 = getTagById.call(22l)
        Tag mailingList3 = getTagById.call(23l)
        Tag mailingList4 = getTagById.call(24l)

        assertFalse(isContactLinkedWith.call(mailingList1))
        assertTrue(isContactLinkedWith.call(mailingList2))
        assertFalse(isContactLinkedWith.call(mailingList3))
        assertTrue(isContactLinkedWith.call(mailingList4))
    }

    private SubmitFieldsRequest wrongRequest() {
        new SubmitFieldsRequest().with {
            it.contactId = '1001'
            it.fields << new Field().with { f ->
                f.key = 'tag/NonExistedTag'
                f.name = 'NonExistedTag'
                f.dataType = DataType.TAGGROUP
                f.value = ''
                f.mandatory = false
                f
            }
            it.fields << new Field().with { f ->
                f.key = 'tag/Tag3'
                f.name = 'Tag3'
                f.dataType = DataType.TAGGROUP
                f.value = null
                f.mandatory = false
                f
            }
            it.fields << new Field().with { f ->
                f.key = 'tag/Tag4'
                f.name = 'Tag4C2'
                f.dataType = DataType.TAGGROUP
                f.value = '[\"Tag4/Tag4C2\",\"Tag4/Tag4C3\"]'
                f.mandatory = false
                f
            }
            it.fields << new Field().with { f ->
                f.key = 'mailingList/NonExistedMailingList'
                f.name = 'NonExistedMailingList'
                f.dataType = DataType.MAILINGLIST
                f.value = '1'
                f.mandatory = false
                f
            }
            it.fields << new Field().with { f ->
                f.key = 'mailingList/Mailing List 1'
                f.name = 'Mailing List 1'
                f.dataType = DataType.MAILINGLIST
                f.value = null
                f.mandatory = false
                f
            }

            it
        }
    }

    private SubmitFieldsRequest properRequest() {
        new SubmitFieldsRequest().with {
            it.contactId = '1001'

            // tag properties

            // non mandatory multiple
            it.fields << new Field().with { f ->
                f.key = 'tag/Tag1'
                f.name = 'Tag1'
                f.dataType = DataType.TAGGROUP
                f.value = '[\"Tag1/Tag1C1\",\"Tag1/Tag1C2/Tag1C2C1\"]'
                f.mandatory = false
                f
            }
            // non mandatory single
            it.fields << new Field().with { f ->
                f.key = 'tag/Tag2'
                f.name = 'Tag2'
                f.dataType = DataType.TAGGROUP
                f.value = '[\"Tag2/Tag2C1\"]'
                f.mandatory = false
                f
            }
            // mandatory multiple
            it.fields << new Field().with { f ->
                f.key = 'tag/Tag3'
                f.name = 'Tag3'
                f.dataType = DataType.TAGGROUP
                f.value = '[\"Tag3/Tag3C2/Tag3C2C1/\",\"Tag3/Tag3C2/Tag3C2C2/Tag3C2C2C1\"]'
                f.mandatory = true
                f
            }
            // mandatory single
            it.fields << new Field().with { f ->
                f.key = 'tag/Tag4'
                f.name = 'Tag4'
                f.dataType = DataType.TAGGROUP
                f.value = '[\"Tag4/Tag4C1\"]'
                f.mandatory = true
                f
            }

            // mailing lists

            it.fields << new Field().with { f ->
                f.key = 'mailingList/Mailing list 1'
                f.name = 'Mailing List 1'
                f.dataType = DataType.MAILINGLIST
                f.value = '0'
                f.mandatory = true
                f
            }
            it.fields << new Field().with { f ->
                f.key = 'mailingList/Mailing List 2'
                f.name = 'Mailing List 2'
                f.dataType = DataType.MAILINGLIST
                f.value = '1'
                f.mandatory = true
                f
            }
            it.fields << new Field().with { f ->
                f.key = 'mailingList/Mailing list 3'
                f.name = 'Mailing List 3'
                f.dataType = DataType.MAILINGLIST
                f.value = '0'
                f.mandatory = false
                f
            }
            it.fields << new Field().with { f ->
                f.key = 'mailingList/Mailing List 4'
                f.name = 'Mailing List 4'
                f.dataType = DataType.MAILINGLIST
                f.value = '1'
                f.mandatory = false
                f
            }

            it
        }
    }
}
