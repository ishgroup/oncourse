package ish.oncourse.server.services

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.CustomFieldType
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import static org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by anarut on 6/30/16.
 */
class CustomFieldTypeServiceTest extends CayenneIshTestCase {

    @Before
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

        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewContext()


        List<CustomFieldType> customFieldTypes = ObjectSelect.query(CustomFieldType.class)
                .select(context)
        List<CustomField> customFields = ObjectSelect.query(CustomField.class)
                .select(context)

        assertEquals(3, customFieldTypes.size())
        assertEquals(30, customFields.size())


        List<Long> ids = Arrays.asList(customFieldTypes.get(0).getId(), customFieldTypes.get(2).getId())

        cftService.deleteCustomFieldTypes(ids)

        List<CustomFieldType> customFieldTypesAfter = ObjectSelect.query(CustomFieldType.class)
                .select(context)
        List<CustomField> customFieldsAfter = ObjectSelect.query(CustomField.class)
                .select(context)

        assertEquals(1, customFieldTypesAfter.size())
        assertEquals(10, customFieldsAfter.size())
    }

    @Test
    void testDeleteTwiceTheSameRecord() {
        CustomFieldTypeService cftService = injector.getInstance(CustomFieldTypeService.class)

        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewContext()


        List<CustomFieldType> customFieldTypes = ObjectSelect.query(CustomFieldType.class)
                .select(context)
        List<CustomField> customFields = ObjectSelect.query(CustomField.class)
                .select(context)

        assertEquals(3, customFieldTypes.size())
        assertEquals(30, customFields.size())


        List<Long> ids = Collections.singletonList(customFieldTypes.get(0).getId())

        cftService.deleteCustomFieldTypes(ids)

        List<CustomFieldType> customFieldTypesAfter = ObjectSelect.query(CustomFieldType.class)
                .select(context)
        List<CustomField> customFieldsAfter = ObjectSelect.query(CustomField.class)
                .select(context)

        assertEquals(2, customFieldTypesAfter.size())
        assertEquals(20, customFieldsAfter.size())

        cftService.deleteCustomFieldTypes(ids)

        List<CustomFieldType> customFieldTypesAfter2 = ObjectSelect.query(CustomFieldType.class)
                .select(context)
        List<CustomField> customFieldsAfter2 = ObjectSelect.query(CustomField.class)
                .select(context)

        assertEquals(2, customFieldTypesAfter2.size())
        assertEquals(20, customFieldsAfter2.size())

    }

}
