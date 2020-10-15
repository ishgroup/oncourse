import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.StringUtils
import org.supercsv.cellprocessor.FmtDate
import org.supercsv.cellprocessor.FmtNumber
import org.supercsv.cellprocessor.Optional
import org.supercsv.cellprocessor.constraint.NotNull
import org.supercsv.cellprocessor.ift.CellProcessor
import org.supercsv.io.CsvMapWriter
import org.supercsv.prefs.CsvPreference

import javax.activation.DataHandler
import javax.mail.MessagingException
import javax.mail.internet.MimeBodyPart
import javax.mail.util.ByteArrayDataSource
import javax.xml.transform.TransformerException
import java.text.SimpleDateFormat
import java.util.*

def run(args) {

	/**
	 * get monday of the week
	 */
	Calendar calendar = Calendar.getInstance()
	calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

	/**
	 * get end date as current monday and 00:00:00
	 */
	def endDate = DateUtils.truncate(calendar.getTime(),  Calendar.DAY_OF_MONTH)

	/**
	 * get start date as previois monday and 00:00:00
	 */
	def startDate =  DateUtils.addDays(endDate, -7)
	
	def q = SelectQuery.query(Invoice)
	q.andQualifier(Invoice.DATE_DUE.gte(startDate))
	q.andQualifier(Invoice.DATE_DUE.lt(endDate))
	q.addOrdering(Invoice.DATE_DUE.asc())
	q.addOrdering(Invoice.INVOICE_NUMBER.asc())
	
	def invoices = args.context.select(q)

    CellProcessor[] processors = [
			new FmtDate("yyyy-MM-dd"), 							// invoice date
			new Optional(),										// FT Type (blank)
			new NotNull(),										// Contractor
			new Optional(),										// Cus Code (blank)
			new Optional(), 									// Student name
			new Optional(),										// Ref (blank)
			new Optional(),										// No charge (blank)
			new Optional(), 									// Class code
			new Optional(new FmtDate("dd/MM/yyyy h:mm a")),		// Class start date
			new Optional(), 									// Course name
			new FmtNumber("####0.00"),							// Fee
			new Optional(), 									// Tax type
			new Optional(), 									// Internal (blank)
			new Optional(), 									// Reporting month (blank)
			new NotNull(), 										// Invoice number
			new Optional(), 									// Description
			new Optional(),										// Title
			new FmtNumber("####0.00"),							// Tax
			new Optional(),										// Purchase order
			new FmtNumber("####0.00"),							// Discount
			new NotNull(),										// Account code
			new NotNull()										// Account name
	]

    String[] headers = [
			"Invoice date",
			"FT Type",
			"Contractor",
			"Cus Code",
			"Student Name",
			"Ref",
			"No charge",
			"Class code",
			"Class start date",
			"Course name",
			"Fee",
			"Tax type",
			"Internal",
			"Reporting month",
			"onCourse invoice number",
			"Description",
			"Title",
			"Tax",
			"Purchase order", 
			"Discount",
			"Account Code",
			"Account name"
	]
	
	def writer = new StringWriter()

    def csvWriter = new CsvMapWriter(writer, CsvPreference.STANDARD_PREFERENCE)
	
	csvWriter.writeHeader(headers)
	
	invoices.each { invoice ->
		invoice.invoiceLines.each { invoiceLine ->
			csvWriter.write([
					"Invoice date"           : invoiceLine.invoice.invoiceDate,
					"FT Type"                : null,
					"Contractor"             : invoiceLine.invoice.contact.getName(true),
					"Cus Code"               : null,
					"Student Name"           : invoiceLine?.enrolment?.student?.contact?.getName(true),
					"Ref"                    : null,
					"No charge"              : null,
					"Class code"             : invoiceLine?.enrolment?.courseClass?.uniqueCode,
					"Class start date"       : invoiceLine?.enrolment?.courseClass?.startDateTime,
					"Course name"            : invoiceLine?.enrolment?.courseClass?.course?.name,
					"Fee"                    : invoiceLine.priceTotalIncTax.toBigDecimal(),
					"Tax type"               : invoiceLine?.tax?.taxCode ?: "unknown",
					"Internal"               : null,
					"Reporting month"        : null,
					"onCourse invoice number": invoiceLine.invoice.invoiceNumber,
					"Description"            : invoiceLine.description,
					"Title"                  : invoiceLine.title,
					"Tax"                    : invoiceLine.priceTotalIncTax.subtract(invoiceLine.priceTotalExTax).toBigDecimal(),
					"Purchase order"         : invoiceLine.invoice.customerReference,
					"Discount"               : invoiceLine.discountTotalExTax.toBigDecimal(),
					"Account Code"           : invoiceLine.account.accountCode,
					"Account name"           : invoiceLine.account.description
			], headers, processors)
		}
	}
	
	csvWriter.close()
	
	sendEmail(endDate.format("yyyy-MM-dd'.csv'"), writer.toString(), "janice.jenkins@westernpower.com.au")
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
