import org.apache.commons.lang3.StringUtils

import java.time.LocalDate


def import80 = new Import80(data: new String(avetmiss80),
        context: context,
        clientIdFN: "clientId")
import80.run()

def import85 = new Import85(data: new String(avetmiss85),
        context: import80.context,
        clientIdFN: import80.clientIdFN)
import85.run()



def import120 = new Import120(nat60: new String(avetmiss60),
        nat120: new String(avetmiss120),
        context: context,
)
import120.run()



class Import80 {
    def logger = LogManager.getLogger(getClass())

    String data
    ObjectContext context
    String clientIdFN

    Map<String, Contact> imported = [:]

    def errors = []

    int lineNumber = 0
    InputLine line
    Avetmiss80Parser parser
    Map value


    def run() {
        data.eachLine { rawLine ->

            line = new InputLine(rawLine)
            parser = Avetmiss80Parser.valueOf(line, lineNumber, context)
            value = parser.parse()

            if (parser.errors.empty) {
                parser.fill(contact)
                commit()
            } else {
                errors.addAll(parser.errors)
                logger.error(parser.errors)
            }
            lineNumber++
        }
        context.commitChanges()
        imported.clear()
        if (!errors.empty) {
            logger.error(errors.join("\n"))
        }
    }

    private Contact getContact() {
        return new ContactProvider(service: parser.service,
                clientIdFieldName: clientIdFN, data: value, imported: imported).provide()
    }

    private void commit() {
        if ((lineNumber % 10) == 0) {
            try {
                context.commitChanges()
                imported.clear()
            } catch (Exception e) {
                logger.error(e)
                throw e
            }
        }
    }
}


class Import85 {
    def logger = LogManager.getLogger(getClass())

    String data
    ObjectContext context
    String clientIdFN

    Map<String, Contact> imported = [:]

    def errors = []

    int lineNumber = 0
    InputLine line
    Avetmiss85Parser parser
    Map value

    def run() {
        data.eachLine { rawLine ->

            line = new InputLine(rawLine)
            parser = Avetmiss85Parser.valueOf(line, lineNumber, context)
            value = parser.parse()

            if (parser.errors.empty) {
                parser.fill(contact)
                commit()
            } else {
                errors.addAll(parser.errors)
                logger.error(parser.errors)
            }
            lineNumber++
        }
        context.commitChanges()
        imported.clear()
        if (!errors.empty) {
            logger.error(errors.join("\n"))
        }
    }

    private Contact getContact() {
        return new ContactProvider(service: parser.service,
                clientIdFieldName: clientIdFN, data: value, imported: imported).provide()
    }

    private void commit() {
        if ((lineNumber % 10) == 0) {
            try {
                context.commitChanges()
                imported.clear()
                logger.warn("{} lines committed", lineNumber)
            } catch (Exception e) {
                logger.error(e)
                throw e
            }
        }
    }

}

class Import120 {
    static logger = LogManager.getLogger("Import120")

    def nat120
    def nat60
    ObjectContext context

    List<String> errors = []
    Map<String, Qualification> qualifications = [:]
    Map<String, PriorLearning> priorLearnings = [:]
    Map<String, Module> modules = [:]


    def lineNumber = 1
    InputLine line
    AvetmissImportService service


    def run() {
        service = new AvetmissImportService(context: context, fileType: "Import120", errors: errors)

        nat120.eachLine { rawLine ->
            line = new InputLine(rawLine)
            Avetmiss120Parser parser = new Avetmiss120Parser(line: line, context: context, service: service,
                    errors: errors, priorLearnings: priorLearnings, qualifications: qualifications, nat60:nat60)
            parser.parse()

            if (!errors.isEmpty()) {
                throw new RuntimeException(errors.join("\n"))
            }
            if ((lineNumber % 10) == 0) {
                try {
                    context.commitChanges()
                    priorLearnings.clear()
                    qualifications.clear()
                } catch (Exception e) {
                    throw e
                }
            }

            lineNumber = lineNumber + 1
        }

        context.commitChanges()
        priorLearnings.clear()
    }

}

class Avetmiss120Parser {
    static logger = LogManager.getLogger("Avetmiss120Parser")

    InputLine line
    ObjectContext context
    AvetmissImportService service
    def nat60

    List<String> errors = []
    Map<String, Qualification> qualifications = [:]
    Map<String, PriorLearning> priorLearnings = [:]
    Map<String, Map> extModules = [:]

    private Map data

    // "clientId" is Student.studentNumber in DB or CustomField.clientId
    private String clientId
    private Contact contact
    private Qualification qualification
    private PriorLearning priorLearning
    private String priorLearningKey
    private Module module
    private Map extModule

    def parse() {
        parseData()
        initContact()
        if (!contact){
            return
        }
        initQualification()
        initModule()

        if (alreadyImported())
            return

        initPriorLearning()



        Outcome outcome = context.newObject(Outcome)
        outcome.module = module
        outcome.priorLearning = priorLearning
        outcome.startDate = data.start
        outcome.endDate = data.end

        outcome.deliveryMode = DeliveryMode.values().find { value -> value.code == data.deliveryMode }
        outcome.status = OutcomeStatus.values().find { value -> value.databaseValue == data.result } ?: OutcomeStatus.STATUS_NOT_SET
        outcome.reportableHours = new BigDecimal(data.scheduledHours)
        outcome.fundingSource = ClassFundingSource.values().find { value -> value.avetmissCode.equals(data.fundingSource) }
    }

    private void parseData() {
        data = [clientId      : (String) null,
                moduleId      : (String) null,
                courseId      : (String) null,
                start         : (LocalDate) null,
                end           : (LocalDate) null,
                deliveryMode  : (Integer) null,
                result        : (Integer) null,
                scheduledHours: (String) null,
                fundingSource : (String) null,
                outcomeId     : (String) null
        ]

        line.readString(10)

        data.clientId = line.readString(10)
        data.moduleId = line.readString(12)
        data.courseId = line.readString(10)
        data.start = line.readLocalDate(8)
        data.end = line.readLocalDate(8)
        data.deliveryMode = line.readInteger(2)
        data.result = line.readInteger(2)
        data.scheduledHours = line.readString(4)
        data.fundingSource = line.readString(2)
        line.readString(34)
        data.outcomeId = line.readString(3)

        clientId = data.clientId

    }

    private void initContact() {
        contact = service.getContactBy("clientId", clientId)
        if (contact)
            return

        contact = service.getContactBy(clientId)
        if (contact)
            return

        if (!contact) {
            logger.error("Cannot find contact with clientId: {}", clientId)
        }
    }

    private void initPriorLearning() {
        if (priorLearningKey) {
            priorLearning = priorLearnings[priorLearningKey]
            if (priorLearning)
                return

            priorLearning = findPL()

            if (priorLearning)
                return
        }

        priorLearning = context.newObject(PriorLearning)
        priorLearning.student = contact.student
        priorLearning.outcomeIdTrainingOrg = data.outcomeId

        if (StringUtils.trimToNull(data.courseId)) {
            priorLearning.externalRef = data.courseId
        }

        if (priorLearningKey) {
            priorLearnings[priorLearningKey] = priorLearning
            if (qualification) {
                priorLearning.qualification = qualification
                priorLearning.title = qualification.title
            } else {
                if (module) {
                    priorLearning.title = module.title
                } else {
                    priorLearning.title = extModule.title
                }
            }
        } else {
            if (module) {
                priorLearning.title = module.title
            } else {
                priorLearning.title = extModule.title
            }
        }
    }

    private initModule() {
        module = ObjectSelect.query(Module).where(Module.NATIONAL_CODE.eq(data.moduleId)).selectOne(context)
        if (module) {
            if (StringUtils.trimToNull(data.courseId)) {
                priorLearningKey = String.format("%s:%s", clientId, data.courseId)
            }
            return
        }

        extModule = extModules[data.moduleId]
        if (extModule) {
            return
        }

        def nat60Line = nat60.readLines().collect { line -> new InputLine(line) }.find { line ->
            line.readString(1)
            line.readString(12) == data.moduleId
        }
        if (nat60Line) {
            extModule = [moduleId: data.moduleId, title: nat60Line.readString(100)]
            extModules[data.moduleId] = extModule
        } else {
            throw new RuntimeException("AVETMISS-60: can't find module code " + moduleId + " which was found in the 120 file.")
        }

        if (StringUtils.trimToNull(data.courseId)) {
            if(module) {
                priorLearningKey = String.format("%s:%s", clientId, data.courseId)
            }
        }
    }

    private void initQualification() {
        if (data.courseId) {
            qualification = qualifications[data.courseId]
            if (qualification)
                return

            qualification = ObjectSelect.query(Qualification).where(Qualification.NATIONAL_CODE.eq(data.courseId)).selectOne(context)
            if (qualification) {
                qualifications[data.courseId] = qualification
            }
        }
    }

    private PriorLearning findPL() {
        if (qualification) {
            ObjectSelect.query(PriorLearning)
                    .where(PriorLearning.QUALIFICATION.eq(qualification))
                    .and(PriorLearning.STUDENT.eq(contact.student)).selectOne(context)
        } else if (module) {
            ObjectSelect.query(PriorLearning)
                    .where(PriorLearning.EXTERNAL_REF.eq(data.courseId))
                    .and(PriorLearning.STUDENT.eq(contact.student)).selectOne(context)
        }
    }

    private boolean alreadyImported() {
        def qualExp =  Outcome.PRIOR_LEARNING.dot(PriorLearning.STUDENT).eq(contact.student)
        def outcomeExp = Outcome.START_DATE.eq(data.start).andExp(Outcome.END_DATE.eq(data.end))

        if (qualification) {
            qualExp = qualExp.andExp(Outcome.PRIOR_LEARNING.dot(PriorLearning.QUALIFICATION).eq(qualification))
        } else if (StringUtils.trimToNull(data.courseId)) {
            qualExp = qualExp.andExp(Outcome.PRIOR_LEARNING.dot(PriorLearning.EXTERNAL_REF).eq(data.courseId))
        } else {
            if (module) {
                qualExp = qualExp.andExp(Outcome.PRIOR_LEARNING.dot(PriorLearning.TITLE).eq(module.title))
            }
        }

        if (module) {
            outcomeExp = outcomeExp.andExp(Outcome.MODULE.eq(module))
        } else {
            qualExp = qualExp.andExp(Outcome.PRIOR_LEARNING.dot(PriorLearning.TITLE).eq(extModule.title))
        }

        ObjectSelect.query(Outcome).where(outcomeExp).and(qualExp).select(context).size() > 0
    }
}



