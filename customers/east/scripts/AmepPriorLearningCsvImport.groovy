/*(
proirLearningFile=Please select proirLearning CSV file...
)*/

import ish.oncourse.server.imports.CsvParser
import org.apache.cayenne.query.ObjectSelect


import java.time.LocalDate
import java.time.format.DateTimeFormatter

def reader = new CsvParser(new InputStreamReader(new ByteArrayInputStream(proirLearningFile)))

reader.eachLine { line ->

    Contact contact = ObjectSelect.query(Contact)
            .where(Contact.CUSTOM_FIELDS.dot(CustomField.VALUE).eq(line.'contact.customField(AMEP)'))
            .and(Contact.CUSTOM_FIELDS.dot(CustomField.CUSTOM_FIELD_TYPE).dot(CustomFieldType.KEY).eq('amep'))
            .and(Contact.LAST_NAME.eq(line.'contact.lastName'))
            .and(Contact.FIRST_NAME.eq(line.'contact.firstName'))
            .selectFirst(context)

    if (contact) {
        context.newObject(PriorLearning).with { priorLearning ->
            priorLearning.student = contact.student
            priorLearning.externalRef = 'AMEP'

            Outcome outcome = context.newObject(Outcome).with { o ->
                o.priorLearning = priorLearning
                
                o.startDate = line.'outcome.startDate' ? LocalDate.parse(line.'outcome.startDate', DateTimeFormatter.ofPattern("d/MM/yyyy")) : null
                o.endDate = line.'outcome.endDate' ? LocalDate.parse(line.'outcome.endDate', DateTimeFormatter.ofPattern("d/MM/yyyy")) : null
                
                Integer statusValue = line.'outcome.status'?.toInteger()
                o.status = OutcomeStatus.values().find { value -> value.databaseValue == statusValue } ?: OutcomeStatus.STATUS_NOT_SET
                Integer fundingSourceValue = line.'outcome.fundingSource'?.toInteger()
                o.fundingSource = ClassFundingSource.values().find { value -> value.databaseValue == fundingSourceValue }
                o
            }

            String nCode = line.'module.nationalCode'
            Module module = ObjectSelect.query(Module)
                    .where(Module.NATIONAL_CODE.eq(nCode))
                    .selectOne(context)

            if (module) {
                outcome.module = module
                priorLearning.title = module.title
            } else {
                priorLearning.title = nCode
            }
        }
    }
    
    context.commitChanges()
}