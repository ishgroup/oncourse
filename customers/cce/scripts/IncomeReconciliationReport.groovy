import groovy.transform.ToString
import ish.common.types.AccountType
import ish.math.Money
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.export.CsvBuilder
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLSelect
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.activation.DataHandler
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.mail.Multipart
import javax.mail.util.ByteArrayDataSource
import java.time.LocalDate
import java.time.ZoneId

import ish.oncourse.server.scripting.api.CollegePreferenceService
import ish.oncourse.server.scripting.api.EmailService
import groovy.time.TimeCategory

import static ish.common.types.AccountTransactionType.INVOICE_LINE

def run(args) {
    Processor processor = new Processor(new Date())

    // uncomment the next line if you want to test with a specific month
    //Processor processor = new Processor(new Date().copyWith(month: Calendar.MAY))

    processor.emailAddress = "natalia.borisova@sydney.edu.au"
    //processor.emailAddress = "support@ish.com.au"

    processor.context = args.context
    processor.Email = Email
    processor.Preferences = Preferences
    processor.process()
}

class Processor {

    EmailService Email
    CollegePreferenceService Preferences

    static final String RESULT_FILE_PREFIX = 'IncomeReconciliationReport'

    static final String FILE_NAME_PATTERN = 'IncomeReconciliationReport-%s-%s.csv'

    static final String INVOICE_LINE_SQL = 'select distinct il.* from InvoiceLine il \n' +
            'inner join Invoice i on i.id = il.invoiceId\n' +
            'where \n' +
            'i.invoiceDate >= \'$from\'\n' +
            'AND\n' +
            'i.invoiceDate < \'$to\'\n' +
            'order by il.id'

    ObjectContext context

    Date reportGeneratedDate
    Date startDate
    Date endDate
    Date currentMonthStart
    Date currentMonthEnd
    String emailAddress

    private File exportResult = File.createTempFile(RESULT_FILE_PREFIX, StringUtils.EMPTY)
    private String fileName

    Processor(Date reportGeneratedDate) {
        this.reportGeneratedDate = reportGeneratedDate
        use(TimeCategory) {

            // start from 6pm on 31/12 at the end of last year
            startDate = reportGeneratedDate.clone() - 1.year
            startDate.set(month: 11, date: 31, hourOfDay: 18, minute: 0, second: 0)
            if (reportGeneratedDate[Calendar.MONTH] == 0) { //in January go back another year
                startDate -= 1.year
            }

            // end at 6pm on the last day of the end of last month
            endDate = reportGeneratedDate.clone()
            endDate.set(date: 1, hourOfDay: 18, minute: 0, second: 0)
            endDate -= 1.day


            currentMonthEnd = endDate.clone()

            currentMonthStart = reportGeneratedDate.clone() - 1.month
            currentMonthStart.set(date: 1, hourOfDay: 18, minute: 0, second: 0)
            currentMonthStart -= 1.day
        }
    }

    void process() {

        try {
            fileName = String.format(FILE_NAME_PATTERN, startDate.format("yyyy-MM-dd"), endDate.format("yyyy-MM-dd"))

            FileWriter fileWriter = new FileWriter(exportResult)

            CsvBuilder csv = new CsvBuilder(fileWriter)

            use(TimeCategory) {
                // we need to collect an extra year of data for enrolments in the year before the class
                def collectDataStart = startDate.clone() - 1.year

                SQLSelect.query(InvoiceLine, INVOICE_LINE_SQL)
                        .params('from', collectDataStart.format("yyyy-MM-dd HH:mm:ss"))
                        .params('to', endDate.format("yyyy-MM-dd HH:mm:ss"))
                        .select(context).each {

                    Row row = processInvoiceLine(it)
                    if (!(row.prepaidFeesRemaining.isZero() && row.revenueRecognisedCurrentYear.isZero())) {
                        csv << [
                                reportGeneratedDate          : reportGeneratedDate.format("dd-MM-yyyy"),
                                startDateTime                : row.startDateTime,
                                courseClassCode              : row.courseClassCode,
                                startMonth                   : row.startMonth,
                                invoiceNumber                : row.invoiceNumber,
                                invoiceTo                    : row.invoiceTo,
                                createdOn                    : row.createdOn,
                                createdOnMonth               : row.createdOnMonth,
                                createdOnDate                : row.createdOnDate,
                                interfaceFileDate            : row.interfaceFileDate,
                                netTotal                     : row.netTotal.doubleValue(),
                                incTotal                     : row.incTotal.doubleValue(),
                                prepaidFeesRemaining         : row.prepaidFeesRemaining.doubleValue(),
                                revenueRecognisedCurrentMonth: row.revenueRecognisedCurrentMonth.doubleValue(),
                                revenueRecognisedCurrentYear : row.revenueRecognisedCurrentYear.doubleValue(),
                                studentNumber                : row.studentNumber,
                                studentName                  : row.studentName,
                                title                        : row.title
                        ]
                    }
                }
            }
            fileWriter.close()
            send()
        } finally {
            exportResult.deleteOnExit()
        }
    }


    Row processInvoiceLine(InvoiceLine invoiceLine) {
        Row row = new Row()
        row.id = invoiceLine.id

        Enrolment enrolment = invoiceLine.enrolment
        CourseClass courseClass = enrolment?.courseClass ? enrolment?.courseClass : invoiceLine.courseClass

        if (courseClass) {
            row.startDateTime = courseClass.startDateTime?.format("dd-MM-yyyy HH:mm")
            row.courseClassCode = courseClass.uniqueCode
            row.startMonth = courseClass.startDateTime?.format("MM-yyyy")
            row.studentNumber = enrolment?.student?.studentNumber
            row.studentName = enrolment?.student?.contact?.fullName
        }

        row.invoiceNumber = invoiceLine.invoice.invoiceNumber
        row.invoiceTo = invoiceLine.invoice.contact.fullName
        row.createdOn = invoiceLine.createdOn.format("dd-MM-yyyy HH:mm")
        row.createdOnMonth = invoiceLine.createdOn.format("MM-yyyy")
        row.createdOnDate = invoiceLine.createdOn.format("dd-MM-yyyy")
        row.interfaceFileDate = Integer.valueOf(invoiceLine.createdOn.format('HH')) >= 18 ?
                DateUtils.addDays(invoiceLine.createdOn, 1).format("dd-MM-yyyy") :
                invoiceLine.createdOn.format("dd-MM-yyyy")
        row.netTotal = invoiceLine.priceEachExTax.subtract(invoiceLine.discountEachExTax).multiply(invoiceLine.quantity)
        row.incTotal = invoiceLine.priceEachExTax.subtract(invoiceLine.discountEachExTax).add(invoiceLine.taxEach).multiply(invoiceLine.quantity)

        row.prepaidFeesRemaining = prepaidFeesRemaining(invoiceLine)
        row.revenueRecognisedCurrentMonth = revenueRecognisedCurrentMonth(invoiceLine)
        row.revenueRecognisedCurrentYear = revenueRecognisedCurrentYear(invoiceLine)

        row.title = invoiceLine.title
        row
    }

    Money prepaidFeesRemaining(InvoiceLine invoiceLine) {

        Money result = Money.ZERO

        if (invoiceLine.prepaidFeesAccount) {
            ObjectSelect.query(AccountTransaction).and(
                    AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.id),
                    AccountTransaction.TABLE_NAME.eq(INVOICE_LINE),
                    AccountTransaction.CREATED_ON.lt(endDate),
                    AccountTransaction.ACCOUNT.eq(invoiceLine.prepaidFeesAccount)
            ).select(context).each {
                result = result.add(it.amount)
            }
            return result
        }
        return result
    }

    Money revenueRecognisedCurrentYear(InvoiceLine invoiceLine) {
        return revenueRecognisedPeriod(invoiceLine, startDate, endDate)
    }

    Money revenueRecognisedCurrentMonth(InvoiceLine invoiceLine) {
        return revenueRecognisedPeriod(invoiceLine, currentMonthStart, currentMonthEnd)
    }

    /**
     * 1. an AccountTransaction.amount is being added to the revenueRecognised value if the AccountTransaction has INCOME account
     * 2. an AccountTransaction.amount is being subtracted  if the AccountTransaction has COS or EXPENSE account
     * 3. an AccountTransaction.amount is being ignored if the AccountTransaction has ASSET, EQUITY or LIABILITY account
     */
    private Money revenueRecognisedPeriod(InvoiceLine invoiceLine, Date start, Date end) {
        Money result = Money.ZERO

        ObjectSelect.query(AccountTransaction).and(
                AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.id),
                AccountTransaction.TABLE_NAME.eq(INVOICE_LINE),
                AccountTransaction.CREATED_ON.between(start, end),
                AccountTransaction.ACCOUNT.dot(Account.TYPE).in(AccountType.INCOME,AccountType.COS, AccountType.EXPENSE)
        ).select(context).each {
            if (it.account.type == AccountType.INCOME) {
                result = result.add(it.amount)
            } else {
                //it.account.type is AccountType.COS or AccountType.EXPENSE
                result = result.add(it.amount.negate())
            }
        }
        return result
    }


    void send() {
        def email = Email.create()

        email.from(Preferences.get("email.from"))
        email.to(this.emailAddress)
        email.subject(fileName)

        if (exportResult.exists()) {
            def dataSource = new ByteArrayDataSource(new FileInputStream(exportResult), "application/octet-stream")
            def dataHandler = new DataHandler(dataSource)

            def fileBodyPart = new MimeBodyPart()
            fileBodyPart.setDataHandler(dataHandler)
            fileBodyPart.setFileName(fileName)
            fileBodyPart.setHeader("Content-Transfer-Encoding", "base64")

            String message = "Attached is the income reconciliation report.<br/><br/>" +
                    "Executed on: $reportGeneratedDate<br/>" +
                    "Start: $startDate<br/>" +
                    "End: $endDate<br/>" +
                    "Current month from: $currentMonthStart<br/>" +
                    "Current month to: $currentMonthEnd<br/>"
                    .toString()
            def messageBodyPart = new MimeBodyPart()
            messageBodyPart.setContent(message,"text/html")

            email.addPart(messageBodyPart)
            email.addPart(fileBodyPart)
        } else {
            email.content("There is nothing for export.")
        }
        email.send()
    }
}


@ToString
class Row {
    long id
    String reportGeneratedDate
    String startDateTime
    String courseClassCode
    String startMonth
    String invoiceNumber
    String invoiceTo
    String createdOn
    String createdOnMonth
    String createdOnDate
    String interfaceFileDate
    Money netTotal
    Money incTotal
    Money prepaidFeesRemaining
    Money revenueRecognisedCurrentMonth
    Money revenueRecognisedCurrentYear
    String studentNumber
    String studentName
    String title

}
