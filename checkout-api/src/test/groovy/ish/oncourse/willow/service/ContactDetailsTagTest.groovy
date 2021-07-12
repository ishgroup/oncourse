package ish.oncourse.willow.service

import ish.oncourse.model.Contact
import ish.oncourse.model.Tag
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.ContactFieldsRequest
import ish.oncourse.willow.model.field.DataType
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.field.FieldHeading
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
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        ContactApi api = new ContactApiServiceImpl(cayenneService, new CollegeService(cayenneService))

        ContactFields fields = api.getContactFields(new ContactFieldsRequest(contactId: '1001', classIds: ['1001'], fieldSet: FieldSet.ENROLMENT))

        def file = new File(getClass().getResource('/ish/oncourse/willow/service/contact-tag-fields.txt').toURI())

        assertEquals(file.text, fields.toString())
    }

    @Test
    void testSubmitProperRequest() {

        ObjectContext context = cayenneService.newContext()

        RequestFilter.ThreadLocalSiteKey.set('mammoth')
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
    }

    private ContactFields properRequest() {
        new ContactFields().with {
            it.contactId = '1001'

            // tag properties
            List<Field> fields = []
            // non mandatory multiple
            fields << new Field().with { f ->
                f.key = 'multipleTag/Tag1/Tag1C1'
                f.name = 'Tag1'
                f.dataType = DataType.TAGGROUP_M
                f.value = '1'
                f.mandatory = false
                f
            }
            fields << new Field().with { f ->
                f.key = 'multipleTag/Tag1/Tag1C2/Tag1C2C1'
                f.name = 'Tag1'
                f.dataType = DataType.TAGGROUP_M
                f.value = '1'
                f.mandatory = false
                f
            }
            // non mandatory single
            fields << new Field().with { f ->
                f.key = 'singleTag/Tag2/Tag2C1'
                f.name = 'Tag2'
                f.dataType = DataType.TAGGROUP_S
                f.value = '/Tag2/Tag2C1'
                f.mandatory = false
                f
            }
            // mandatory multiple
            fields << new Field().with { f ->
                f.key = 'multipleTag/Tag3/Tag3C2/Tag3C2C1/'
                f.name = 'Tag3'
                f.dataType = DataType.TAGGROUP_M
                f.value = 'Tag3/Tag3C2/Tag3C2C1/'
                f.mandatory = true
                f
            }
            fields << new Field().with { f ->
                f.key = 'multipleTag/Tag3/Tag3C2/Tag3C2C2/Tag3C2C2C1'
                f.name = 'Tag3'
                f.dataType = DataType.TAGGROUP_M
                f.value = '0'
                f.mandatory = true
                f
            }
            // mandatory single
            fields << new Field().with { f ->
                f.key = 'singleTag/Tag4/Tag4C1'
                f.name = 'Tag4'
                f.dataType = DataType.TAGGROUP_S
                f.value = '/Tag4/Tag4C1'
                f.mandatory = true
                f
            }

            it.headings << new FieldHeading(name: "First Heading", description: "description", ordering: 1, fields: fields)

            it
        }
    }
}
