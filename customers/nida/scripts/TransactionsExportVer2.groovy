import ish.oncourse.server.cayenne.AccountTransaction
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SelectQuery
import org.apache.cayenne.query.SortOrder
import org.apache.commons.lang3.StringUtils

import javax.activation.DataHandler
import javax.mail.MessagingException
import javax.mail.internet.MimeBodyPart
import javax.mail.util.ByteArrayDataSource
import javax.xml.transform.TransformerException
import java.text.SimpleDateFormat
import java.util.*

output = new StringBuilder()

def run(args) throws IOException, TransformerException, ClassNotFoundException, MessagingException{

    EMAIL_ADDRESS = "support@ish.com.au"

    def context = args.context
    def dateFormatFile = new SimpleDateFormat("yyyy-MM-dd'.csv'")

    def endDate = Calendar.getInstance().getTime()
    endDate.set(hourOfDay: 0, minute: 0, second: 0)
    def startDate = endDate - 1
    
    def fileName = dateFormatFile.format(startDate)

    process(context, startDate, endDate)

    sendEmail(fileName, output.toString().getBytes("UTF-8"), EMAIL_ADDRESS)

    return 0
}

def write(value) {
    write(value, false)
}

def write(value, isEndOfLine) {
    output.append('"')
    if (value != null) {
        output.append(StringUtils.trimToEmpty(value.toString()).replace('"','""'))
    }
    output.append('"')
    output.append(isEndOfLine ? '\n' : ',')
}

def process(context, startDate, endDate) {

    def q = new SelectQuery(AccountTransaction.class)
    q.andQualifier(ExpressionFactory.greaterOrEqualExp(AccountTransaction.CREATED_ON.getName(), startDate))
    q.andQualifier(ExpressionFactory.lessExp(AccountTransaction.CREATED_ON.getName(), endDate))
    q.addOrdering(new Ordering(AccountTransaction.CREATED_ON.getName(), SortOrder.ASCENDING))
    accountTransactions = context.performQuery(q)

    def currentDate = new Date()

    accountTransactions.each() { record ->

        // Company Code: always "01"
        write('01')

        // Batch Number: Batch number has to be unique for each import. Export reverse of today's date e.g. 41024080
        write(currentDate.format('ddMMyyyy').reverse())

        // Batch Description: concatenate current date in dd/MM/yyyy format + 'onCourse export' e.g.  "23/03/2014 onCourse export"
        write(currentDate.format('dd/MM/yyyy') + ' onCourse export')

        // Document Number: always "123456"
        write('123456')

        // Document Detail: always {blank}
        write('')

        // Document Date: current date in dd/MM/yyyy e.g. "23/03/2014"
        write(currentDate.format('dd/MM/yyyy'))

        // Posting Date: current date in dd/MM/yyyy e.g. "23/03/2014"
        write(currentDate.format('dd/MM/yyyy'))

        // Line Company: always "01"
        write('01')

        // Account Number: {account number from onCourse prior to the / character} e.g. '01.24.2800.000'
        def code = record.getAccount().getAccountCode()
        write( code.contains('/') ? code.substring(0,code.indexOf('/')) : code )

        // Cash Analysis Code: always {blank}
        write('')

        // Quantity: always "1"
        write('1')

        // Amount: "123.45" {the amount from the journal, format "-123.45" for credit-
        def type = record.getAccount().getType().getDisplayName()
        def amount = record.getAmount().toBigDecimal()
        if (type == 'income' || type == 'liability') {
            write( amount.multiply(-1) )
        } else {
            write( amount )
        }
       
        // Narration: For Invoice Line source concatenate "'onCourse Invoice'; invoice.invoiceNumber; invoiceline.description; 'processed on'; accounttransaction.transactiondate"   e.g. "onCourse Invoice 42 Jenny Smith (WDWK-0017 Woodwork) processed on 18/03/2014 10.04pm"
        // For Payment source concatenate "'onCourse payment {type}'; payer contact.firstname; payer contact.lastname; payment.type; 'processed on'; payment.createdon" e.g. "onCourse payment in Jenny Smith Credit Card processed on 18/03/2014 10.05pm'  <- this needs to work for both payment in and payment out types-->
        def transactionDate = record.getTransactionDate().format('dd/MM/yyyy hh.mmaa').replace('PM','p.m.').replace('AM','a.m.')
        if (record.getSource().contains('Payment')) {
            write( 'onCourse ' + record.getSource() + ' ' + record.getContactName() + ' ' + record.getPaymentType() + ' processed on ' + transactionDate )
        } else {
            write( 'onCourse ' + record.getSource() + ' ' + record.getInvoiceNumber() + ' ' + record.getContactName() + ' (' + StringUtils.trimToEmpty(record.getInvoiceDescription())  + ') processed on ' + transactionDate )
        }

        // Rate: always "1"
        write('1')

        // Multiply Rate: always "0"
        write('0')

        // New fields for Dynamics export
        // only one (or none) of the following lines will be not null
        def invoiceLine = record.getInvoiceLineForTransaction(context, record)
        def paymentInLine = record.getPaymentInLineForTransaction(context, record)
        def paymentOutLine = record.getPaymentOutLineForTransaction(context, record)

        // Australian state in which the enrolment took place
        write( invoiceLine?.enrolment?.courseClass?.room?.site?.state )

        // Course code
        write( invoiceLine?.enrolment?.courseClass?.course?.code )

        // Class code
        write( invoiceLine?.enrolment?.courseClass?.code )

        // settlement date for payments
        if (paymentInLine != null) {
            write( paymentInLine.paymentIn?.dateBanked?.format('dd/MM/yyyy'), true )
        } else if (paymentOutLine != null) {
            write( paymentOutLine.paymentOut?.dateBanked?.format('dd/MM/yyyy'), true )
        } else {
            write('', true)
        }
    }
}

def sendEmail(fileName, data, email_address) throws MessagingException {
    def email = Email.create()

    email.from(Preferences.get("email.from"))
    email.to(email_address)
    email.subject(fileName)

    if (data) {
        email.addPart("text/html", StringUtils.EMPTY)

        def dataSource = new ByteArrayDataSource(data, "application/octet-stream")
        def dataHandler = new DataHandler(dataSource)

        messageBodyPart = new MimeBodyPart()
        messageBodyPart.setDataHandler(dataHandler)
        messageBodyPart.setFileName(fileName)
        messageBodyPart.setHeader("Content-Transfer-Encoding", "base64")

        email.addPart(messageBodyPart)
    } else {
        email.addPart("text/html", "There is nothing for export.")
    }
    
    email.send()
}
