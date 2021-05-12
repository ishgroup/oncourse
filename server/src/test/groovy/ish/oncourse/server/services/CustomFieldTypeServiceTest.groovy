package ish.oncourse.server.services

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.CustomFieldType
import org.apache.cayenne.query.ObjectSelect
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class CustomFieldTypeServiceTest extends CayenneIshTestCase {

    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()

        InputStream st = CustomFieldTypeServiceTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/server/services/customFieldTypeServiceTestDataSet.xml")
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)

        FlatXmlDataSet dataSet = builder.build(st)

        executeDatabaseOperation(dataSet)
        super.setup()
    }

    
    @Test
    void testDeleteRecords() {
        CustomFieldTypeService cftService = injector.getInstance(CustomFieldTypeService.class)

        List<CustomFieldType> customFieldTypes = ObjectSelect.query(CustomFieldType.class)
                .select(cayenneContext)
        List<CustomField> customFields = ObjectSelect.query(CustomField.class)
                .select(cayenneContext)

        Assertions.assertEquals(3, customFieldTypes.size())
        Assertions.assertEquals(30, customFields.size())


        List<Long> ids = Arrays.asList(customFieldTypes.get(0).getId(), customFieldTypes.get(2).getId())

        cftService.deleteCustomFieldTypes(ids)

        List<CustomFieldType> customFieldTypesAfter = ObjectSelect.query(CustomFieldType.class)
                .select(cayenneContext)
        List<CustomField> customFieldsAfter = ObjectSelect.query(CustomField.class)
                .select(cayenneContext)

        Assertions.assertEquals(1, customFieldTypesAfter.size())
        Assertions.assertEquals(10, customFieldsAfter.size())
    }

    
    @Test
    void testDeleteTwiceTheSameRecord() {
        CustomFieldTypeService cftService = injector.getInstance(CustomFieldTypeService.class)

        List<CustomFieldType> customFieldTypes = ObjectSelect.query(CustomFieldType.class)
                .select(cayenneContext)
        List<CustomField> customFields = ObjectSelect.query(CustomField.class)
                .select(cayenneContext)

        Assertions.assertEquals(3, customFieldTypes.size())
        Assertions.assertEquals(30, customFields.size())


        List<Long> ids = Collections.singletonList(customFieldTypes.get(0).getId())

        cftService.deleteCustomFieldTypes(ids)

        List<CustomFieldType> customFieldTypesAfter = ObjectSelect.query(CustomFieldType.class)
                .select(cayenneContext)
        List<CustomField> customFieldsAfter = ObjectSelect.query(CustomField.class)
                .select(cayenneContext)

        Assertions.assertEquals(2, customFieldTypesAfter.size())
        Assertions.assertEquals(20, customFieldsAfter.size())

        cftService.deleteCustomFieldTypes(ids)

        List<CustomFieldType> customFieldTypesAfter2 = ObjectSelect.query(CustomFieldType.class)
                .select(cayenneContext)
        List<CustomField> customFieldsAfter2 = ObjectSelect.query(CustomField.class)
                .select(cayenneContext)

        Assertions.assertEquals(2, customFieldTypesAfter2.size())
        Assertions.assertEquals(20, customFieldsAfter2.size())

    }

}
