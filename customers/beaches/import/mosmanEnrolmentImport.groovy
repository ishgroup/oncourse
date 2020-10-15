/*(
enrolmentFile=Please select enrolment CSV file...
)*/

import ish.oncourse.server.cayenne.*
import ish.oncourse.server.imports.CreateUserFriendlyMessage
import ish.oncourse.server.imports.CsvParser
import org.apache.cayenne.query.ObjectSelect

import javax.script.ScriptException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

def readerEnrolment = new CsvParser(new InputStreamReader(new ByteArrayInputStream(enrolmentFile)))


rowNumber = 0

try {
    readerEnrolment.eachLine { enrolmentLine -> 
        
        rowNumber++
        
        Contact contact = ObjectSelect.query(Contact)
                        .where(Contact.LAST_NAME.eq(enrolmentLine.Surname_deprecated))
                        .and(Contact.FIRST_NAME.eq(enrolmentLine.FirstName_deprecated))
                        .selectFirst(context)


        if(contact) {
                
            context.newObject(ContactCustomField).with { customField ->
                CustomFieldType cft = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq('category')).selectOne(context)
                customField.customFieldType = cft
                customField.relatedObject = contact
                customField.value = enrolmentLine.Category 
            }
        }      
    }    
} catch (Exception e) {
    throw new ScriptException(CreateUserFriendlyMessage.valueOf(e, rowNumber).getMessage())
}
context.commitChanges()