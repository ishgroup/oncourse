import com.sun.mail.smtp.SMTPMessage
import ish.export.ExportParameter
import ish.oncourse.server.AngelServer
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.glue._AccountTransaction
import ish.oncourse.server.export.XsltExport
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SelectQuery
import org.apache.cayenne.query.SortOrder
import org.apache.commons.lang3.StringUtils

import javax.activation.DataHandler
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource
import javax.xml.transform.TransformerException
import java.text.SimpleDateFormat
import java.util.*

def run(args) throws IOException, TransformerException, ClassNotFoundException, MessagingException{

	EMAIL_ADDRESS = "transactions@nida.edu.au"

	def context = args.context

	def dateFormatFile = new SimpleDateFormat("yyyy-MM-dd'.csv'")

	def endDate = Calendar.getInstance().getTime()
	def startDate = endDate - 1

	def accountTransactions = getAccountTransactions(context, startDate, endDate)

	def exportParameter = getExportParameter(accountTransactions)

	def result = AngelServer.getInjector().getInstance(XsltExport.class).export(exportParameter).getResult()

	def fileName = dateFormatFile.format(startDate)

	sendEmail(fileName, result, EMAIL_ADDRESS)

	0
}

def getAccountTransactions(context, startDate, endDate) {

	def q = new SelectQuery(AccountTransaction.class)
	q.andQualifier(ExpressionFactory.greaterOrEqualExp(_AccountTransaction.CREATED_ON.getName(), startDate))
	q.andQualifier(ExpressionFactory.lessExp(_AccountTransaction.CREATED_ON.getName(), endDate))
	q.addOrdering(new Ordering(_AccountTransaction.CREATED_ON.getName(), SortOrder.ASCENDING))
	context.performQuery(q)

}

def getExportParameter(accountTransactions) {

	def result = new ExportParameter()
	result.setXslKeyCode("nida.onCourse.accountTransactionCsv")

	def ids = new LinkedList()

	accountTransactions.each() { record ->
		ids.add(record.getId())
	}
	result.setIds(ids)
	result.setEntity("AccountTransaction")

	result

}

def sendEmail(fileName, data, email_address) throws MessagingException {

	def email = getSmtpMessage(fileName, email_address)

	
	def messageBodyPart = new MimeBodyPart()
	messageBodyPart.setContent(StringUtils.EMPTY, "text/html")

	def multipart = new MimeMultipart()
	multipart.addBodyPart(messageBodyPart)

	if (data) {
		def dataSource = new ByteArrayDataSource(data, "application/octet-stream")
		def dataHandler = new DataHandler(dataSource)

		messageBodyPart = new MimeBodyPart()
		messageBodyPart.setDataHandler(dataHandler)
		messageBodyPart.setFileName(fileName)
		messageBodyPart.setHeader("Content-Transfer-Encoding", "base64")
		multipart.addBodyPart(messageBodyPart)

	 } else {
	 	messageBodyPart.setText("There is nothing for export.")
	 }

	 email.setContent(multipart, "text/html")
	 javax.mail.Transport.send(email)
}

def getSmtpMessage(fileName, email_address) throws MessagingException {

	def preferenceController = AngelServer.getInjector().getInstance(PreferenceController.class)
	def properties = System.getProperties()

	if (StringUtils.isBlank(preferenceController.getEmailSMTPHost())) {
		throw new IllegalStateException("smtp server has to be specified")
	}

	properties.put("mail.smtp.host", preferenceController.getEmailSMTPHost())
	properties.put("mail.smtp.connectiontimeout", 300000)
	properties.put("mail.smtp.timeout", 300000)

	def session = javax.mail.Session.getInstance(properties)
	def email = new SMTPMessage(session)

	email.setFrom(new InternetAddress(preferenceController.getEmailFromAddress()))
	email.setRecipients(Message.RecipientType.TO, email_address)
	email.setSubject(fileName)
	email.setHeader("X-Mailer", "onCourse " + AngelServer.application().getVersion())
	email.setSentDate(new Date())
	email
}