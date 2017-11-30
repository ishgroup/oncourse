package ish.oncourse.willow.functions.field

import ish.oncourse.model.College
import ish.oncourse.model.CustomFieldType
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
class ProcessCustomFieldTypeTest {

    private String value
    private int expectedItemSize
    private List<String> expectedList
    private DataType expectedDataType
    private String expectedDefaultValue
    
    ProcessCustomFieldTypeTest(String value, int itemSize, List<String> list,  DataType dataType, String defaultValue) {
        this.value = value
        this.expectedItemSize = itemSize
        this.expectedList = list
        this.expectedDataType = dataType
        this.expectedDefaultValue = defaultValue
    }
    
    @Parameters(name = '{0}')
    static Collection<Object[]> data() {
        [
                ['internet; radio; TV ; *', 3, ['internet', 'radio', 'TV'], DataType.CHOICE, null], 
                ['   internet   ; radio; TV ;*', 3, ['internet', 'radio', 'TV'], DataType.CHOICE, null],
                ['    internet; radio   ; TV ; friends;  * ', 4, ['internet', 'radio', 'TV', 'friends'], DataType.CHOICE, null],
                ['internet; radio; TV ; friends   ', 4, ['internet', 'radio', 'TV', 'friends'],  DataType.ENUM, null],
                ['internet; radio   ; TV ;  ', 3, ['internet', 'radio', 'TV'], DataType.ENUM, null],
                ['internet; radio; TV ', 3, ['internet', 'radio', 'TV'], DataType.ENUM, null],
                ['internet', 0, [], DataType.STRING, 'internet'],
                ['internet;   ', 0, [], DataType.STRING, 'internet;'],
                ['    internet;   ', 0, [], DataType.STRING, 'internet;']
        ]*.toArray()
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
        when(fieldType.defaultValue).thenReturn(value)
        
        ProcessCustomFieldType result = new ProcessCustomFieldType(fieldKey, context, college).process()

        assertEquals(expectedItemSize, result.items.size())
        expectedList.each { expValue ->
            assertEquals(1, result.items.findAll { it -> it.key == expValue && it.value == expValue}.size())
        }
        assertEquals(expectedDataType, result.dataType)
        assertEquals(expectedDefaultValue, result.defaultValue)
    }
}
