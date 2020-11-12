import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.imports.avetmiss.Avetmiss80Parser
import ish.oncourse.server.imports.avetmiss.Avetmiss85Parser
import ish.oncourse.server.imports.avetmiss.ContactProvider
import ish.oncourse.server.imports.avetmiss.InputLine
import org.apache.cayenne.ObjectContext

def import80 = new Import80(data: new String(avetmiss80), context: context, clientIdFN: "clientId")
import80.run()

def import85 = new Import85(data: new String(avetmiss85), context: import80.context, clientIdFN: import80.clientIdFN)
import85.run()

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
            throw new RuntimeException(errors.join("\n"))
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
            throw new RuntimeException(errors.join("\n"))
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
