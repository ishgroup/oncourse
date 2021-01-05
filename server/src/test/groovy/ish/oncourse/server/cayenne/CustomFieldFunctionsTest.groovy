package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.common.types.DataType
import ish.oncourse.server.CayenneService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.validation.EntityValidator
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.junit.Before
import org.junit.Test

import javax.ws.rs.ClientErrorException

import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.validateCustomFields
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail


class CustomFieldFunctionsTest extends CayenneIshTestCase {

    @Before
    void setup() throws Exception {
        wipeTables()
    }

    @Test
    void validateCreatingCustomFieldTypeTest() {
        ICayenneService cayenneService = injector.getInstance(CayenneService)
        ObjectContext context = cayenneService.newContext

        createTestCustomFieldType(context, "TestCustomField", "Test", Boolean.TRUE)

        context.commitChanges()
    }


    @Test
    void validateCreatingCustomFieldTest() {
        ICayenneService cayenneService = injector.getInstance(CayenneService)
        ObjectContext context = cayenneService.newContext

        createTestCustomFieldType(context, "TestCustomField", "Test", Boolean.TRUE)
        context.commitChanges()

        Contact contact = context.newObject(Contact)
        contact.firstName = "Name"
        contact.lastName = "Surname"

        try {
            validateCustomFields(context, Contact.simpleName, [:], null, new EntityValidator())
            fail("Contact entity has required custom fields. Validation doesn't work")
        } catch (ClientErrorException ex) {}
    }


    @Test
    void updateCustomFieldsTest() {
        ICayenneService cayenneService = injector.getInstance(CayenneService)
        ObjectContext context = cayenneService.newContext

        createTestCustomFieldType(context, "FirstCustomField", "Test", Boolean.TRUE)
        createTestCustomFieldType(context, "SecondCustomField", "ForTest", Boolean.FALSE)
        context.commitChanges()

        Contact contact = context.newObject(Contact)
        contact.firstName = "Name"
        contact.lastName = "Surname"
        updateCustomFields(context, contact, ["Test":"Text"], ContactCustomField)

        context.commitChanges()

        List<CustomField> customFields = ObjectSelect.query(CustomField).select(context)
        assertEquals("Just one of custom field should be added", 1, customFields.size())
        assertEquals("Text", customFields[0].value)

        updateCustomFields(context, contact, ["Test":"New Text"], ContactCustomField)
        context.commitChanges()
        customFields = ObjectSelect.query(CustomField).select(context)
        assertEquals("Custom field should be updated, not added new one", 1, customFields.size())
        assertEquals("New Text", customFields[0].value)

        updateCustomFields(context, contact, ["Test":"New Text", "ForTest":"ValueForSecondField"], ContactCustomField)
        context.commitChanges()
        assertEquals("New custom field should be added, not updated current.", 2, ObjectSelect.query(CustomField).select(context).size())

        updateCustomFields(context, contact, ["Test":"New Text"], ContactCustomField)
        context.commitChanges()
        assertEquals("Second custom field should be deleted.",1, ObjectSelect.query(CustomField).select(context).size())
    }


    @Test
    void updateCustomFieldsForSeveralEntityTest() {
        ICayenneService cayenneService = injector.getInstance(CayenneService)
        ObjectContext context = cayenneService.newContext

        createTestCustomFieldType(context, "CustomField", "Test", Boolean.TRUE)
        context.commitChanges()

        Contact first = context.newObject(Contact)
        first.firstName = "First Name"
        first.lastName = "First Surname"
        updateCustomFields(context, first, ["Test":"First"], ContactCustomField)

        Contact second = context.newObject(Contact)
        second.firstName = "First Name"
        second.lastName = "First Surname"
        updateCustomFields(context, second, ["Test":"Second"], ContactCustomField)

        Contact third = context.newObject(Contact)
        third.firstName = "First Name"
        third.lastName = "First Surname"
        updateCustomFields(context, third, ["Test":"Third"], ContactCustomField)

        context.commitChanges()

        List<CustomField> customFields = ObjectSelect.query(CustomField).orderBy(CustomField.ID.asc()).select(context)
        assertEquals("Just three custom fields should be added", 3, customFields.size())

        assertNotNull(customFields[0].relatedObject)
        assertEquals("First", customFields.find { first.id == it.relatedObject.id }.value)

        assertNotNull(customFields[1].relatedObject)
        assertEquals("Second", customFields.find { second.id == it.relatedObject.id }.value)

        assertNotNull(customFields[2].relatedObject)
        assertEquals("Third", customFields.find { third.id == it.relatedObject.id }.value)
    }


    private static CustomFieldType createTestCustomFieldType(ObjectContext context, String fieldName, String fieldKey, Boolean isMandatory) {
        context.newObject(CustomFieldType).with { customFieldType ->
            customFieldType.entityIdentifier = Contact.simpleName
            customFieldType.name = fieldName
            customFieldType.key = fieldKey
            customFieldType.dataType = DataType.TEXT
            customFieldType.isMandatory = isMandatory
            customFieldType
        }

    }


}
