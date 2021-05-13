package ish.oncourse.server.cayenne


import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.common.types.DataType
import ish.oncourse.server.CayenneService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.validation.EntityValidator
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.ws.rs.ClientErrorException

import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.validateCustomFields

@CompileStatic
class CustomFieldFunctionsTest extends TestWithDatabase {
    

    @Test
    void validateCreatingCustomFieldTypeTest() {

        createTestCustomFieldType(cayenneContext, "TestCustomField", "Test", Boolean.TRUE)

        cayenneContext.commitChanges()
    }


    
    @Test
    void validateCreatingCustomFieldTest() {

        createTestCustomFieldType(cayenneContext, "TestCustomField", "Test", Boolean.TRUE)
        cayenneContext.commitChanges()

        Contact contact = cayenneContext.newObject(Contact)
        contact.firstName = "Name"
        contact.lastName = "Surname"

        try {
            validateCustomFields(cayenneContext, Contact.simpleName, [:], null, new EntityValidator())
            Assertions.fail("Contact entity has required custom fields. Validation doesn't work")
        } catch (ClientErrorException ex) {
        }
    }


    
    @Test
    void updateCustomFieldsTest() { 

        createTestCustomFieldType(cayenneContext, "FirstCustomField", "Test", Boolean.TRUE)
        createTestCustomFieldType(cayenneContext, "SecondCustomField", "ForTest", Boolean.FALSE)
        cayenneContext.commitChanges()

        Contact contact = cayenneContext.newObject(Contact)
        contact.firstName = "Name"
        contact.lastName = "Surname"
        updateCustomFields(cayenneContext, contact, ["Test": "Text"], ContactCustomField)

        cayenneContext.commitChanges()

        List<CustomField> customFields = ObjectSelect.query(CustomField).select(cayenneContext)
        Assertions.assertEquals(1, customFields.size(), "Just one of custom field should be added")
        Assertions.assertEquals("Text", customFields[0].value)

        updateCustomFields(cayenneContext, contact, ["Test": "New Text"], ContactCustomField)
        cayenneContext.commitChanges()
        customFields = ObjectSelect.query(CustomField).select(cayenneContext)
        Assertions.assertEquals(1, customFields.size(), "Custom field should be updated, not added new one")
        Assertions.assertEquals("New Text", customFields[0].value)

        updateCustomFields(cayenneContext, contact, ["Test": "New Text", "ForTest": "ValueForSecondField"], ContactCustomField)
        cayenneContext.commitChanges()
        Assertions.assertEquals(2, ObjectSelect.query(CustomField).select(cayenneContext).size(), "New custom field should be added, not updated current.")

        updateCustomFields(cayenneContext, contact, ["Test": "New Text"], ContactCustomField)
        cayenneContext.commitChanges()
        Assertions.assertEquals(1, ObjectSelect.query(CustomField).select(cayenneContext).size(), "Second custom field should be deleted.")
    }


    
    @Test
    void updateCustomFieldsForSeveralEntityTest() {
       
        createTestCustomFieldType(cayenneContext, "CustomField", "Test", Boolean.TRUE)
        cayenneContext.commitChanges()

        Contact first = cayenneContext.newObject(Contact)
        first.firstName = "First Name"
        first.lastName = "First Surname"
        updateCustomFields(cayenneContext, first, ["Test": "First"], ContactCustomField)

        Contact second = cayenneContext.newObject(Contact)
        second.firstName = "First Name"
        second.lastName = "First Surname"
        updateCustomFields(cayenneContext, second, ["Test": "Second"], ContactCustomField)

        Contact third = cayenneContext.newObject(Contact)
        third.firstName = "First Name"
        third.lastName = "First Surname"
        updateCustomFields(cayenneContext, third, ["Test": "Third"], ContactCustomField)

        cayenneContext.commitChanges()

        List<CustomField> customFields = ObjectSelect.query(CustomField).orderBy(CustomField.ID.asc()).select(cayenneContext)
        Assertions.assertEquals(3, customFields.size(), "Just three custom fields should be added")

        Assertions.assertNotNull(customFields[0].relatedObject)
        Assertions.assertEquals("First", customFields.find { first.id == it.relatedObject.id }.value)

        Assertions.assertNotNull(customFields[1].relatedObject)
        Assertions.assertEquals("Second", customFields.find { second.id == it.relatedObject.id }.value)

        Assertions.assertNotNull(customFields[2].relatedObject)
        Assertions.assertEquals("Third", customFields.find { third.id == it.relatedObject.id }.value)
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
