package ish.oncourse.willow.functions.field

import ish.oncourse.model.College
import ish.oncourse.model.CustomFieldType
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.field.DataType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.Select
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

import static org.mockito.Mockito.*
import static org.junit.Assert.*

@RunWith(Parameterized)
class ProcessExtendedCustomFieldsTest {

    private String value
    private ish.common.types.DataType type

    private List<Item> expectedItems
    private DataType expectedType
    private String expectedDefaultValue


    ProcessExtendedCustomFieldsTest(String value, ish.common.types.DataType type, List<Item> expectedItems, DataType expectedType, String expectedDefaultValue) {
        this.value = value
        this.type = type
        this.expectedItems = expectedItems
        this.expectedType = expectedType
        this.expectedDefaultValue = expectedDefaultValue
    }

    @Parameters(name = '{0}')
    static Collection<Object[]> data() {
        [
                [ null, ish.common.types.DataType.TEXT, null, DataType.STRING, null].toArray(),
                [ 'test', ish.common.types.DataType.TEXT, null, DataType.STRING, 'test'].toArray(),
                ['[{"value":"1"}, {"value":"2"}, {"value":"3"}, {"value":"*"}]', ish.common.types.DataType.LIST, [new Item(key: '1',value: '1'), new Item(key: '2',value: '2'),new Item(key: '3',value: '3')], DataType.CHOICE, null].toArray(),
                ['[{"value":"1","label":"label1"},{"value":"2","label":"label2"},{"value":"3","label":"label3"}]', ish.common.types.DataType.MAP, [new Item(value: "label1 (1)",key: '1'), new Item(value: "label2 (2)",key: '2'),new Item(value: "label3 (3)",key: '3')], DataType.ENUM, null].toArray(),

        ]
    }



    private String fieldKey = 'customField.contact.someKey'
    private ObjectContext context
    private College college
    private CustomFieldType fieldType

    @Before
    void before() {
        context = mock(ObjectContext)
        college = mock(College)
        fieldType = mock(CustomFieldType)
        when(context.selectOne(any(Select))).thenReturn(fieldType)
    }


    @Test
    void testResults() {

        when(fieldType.dataType).thenReturn(this.type)
        when(fieldType.defaultValue).thenReturn(value)

        ProcessCustomFieldType result = new ProcessCustomFieldType(fieldKey, context, college).process()

        assertEquals(result.dataType, expectedType)
        if (result.dataType in [DataType.ENUM, DataType.CHOICE]) {
            assertEquals(result.items.size(), expectedItems.size())
            result.items.eachWithIndex { Item item, int i ->
                assertEquals(expectedItems[i].value, item.value)
                assertEquals(expectedItems[i].key, item.key)

            }
        }
        assertEquals(expectedDefaultValue, result.defaultValue)
    }
}
