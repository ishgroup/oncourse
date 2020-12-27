package ish.oncourse.server.scripting.api

import ish.CayenneIshTestCase
import ish.oncourse.server.CayenneService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.messaging.MailDeliveryParam
import ish.oncourse.server.messaging.MailDeliveryService
import ish.oncourse.server.messaging.MessageService
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.services.AuditService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

import javax.mail.Multipart
import javax.mail.internet.InternetAddress
import java.util.function.Function

import static junit.framework.TestCase.assertEquals
import static junit.framework.TestCase.assertNotNull
import static junit.framework.TestCase.assertNull
import static org.mockito.Mockito.doNothing
import static org.mockito.Mockito.mock

class MessageServiceTest extends CayenneIshTestCase {

    @Before
    void setup() throws Exception {
        wipeTables()
        InputStream st = GroovyScriptService.class.getClassLoader().getResourceAsStream("ish/oncourse/server/scripting/api/messageServiceTestDataSet.xml")
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        resetAutoIncrement()
        executeDatabaseOperation(dataSet)
    }

    @Test
    void sendMessageFromEmailTemplateTest() {
        ObjectContext context = injector.getInstance(ICayenneService).newContext
        MessageService messageService = injector.getInstance(MessageService)

        List<Enrolment> enrolments = ObjectSelect.query(Enrolment).where(Enrolment.ID.in([1l, 2l, 3l])).select(context)
        SystemUser systemUser = SelectById.query(SystemUser, 1l).selectOne(context)

        MessageSpec messageSpec = new MessageSpec()

        messageSpec.template("test.email")
        messageSpec.records(enrolments)
        messageSpec.from("test@gmail.com")
        messageSpec.bindings.put("money", "100")
        messageSpec.createdBy(systemUser)

        messageService.sendMessage(messageSpec)

        List<Message> messages = ObjectSelect.query(Message).prefetch(Message.MESSAGE_PERSONS.joint()).select(context)
        List<MessagePerson> messagePeople = ObjectSelect.query(MessagePerson).select(context)


        assertEquals(3, messages.size())
        assertEquals(2, messagePeople.size())

        Message messageToFirstStudent = messages.find { it.messagePersons.find { it.contact.id == 1l } }
        assertNotNull("The email would be sent to the first student!", messageToFirstStudent)
        assertEquals("Hello Student First", messageToFirstStudent.emailSubject)
        assertEquals("Thank you for you enrolment to our course TestCourse! Common cost is 100", messageToFirstStudent.emailBody)
        assertEquals("&lt;p&gt; Thank you for you enrolment to our course TestCourse!&lt;br/&gt; Common cost is 100 &lt;/p&gt;", messageToFirstStudent.emailHtmlBody)
        assertEquals("test@gmail.com", messageToFirstStudent.emailFrom)
        assertEquals(systemUser, messageToFirstStudent.createdBy)
        assertNull(messageToFirstStudent.creatorKey)
        assertNull(messageToFirstStudent.smsText)
    }


    @Test
    void sendMessageFromSMSTemplateTest() {
        ObjectContext context = injector.getInstance(ICayenneService).newContext
        MessageService messageService = injector.getInstance(MessageService)

        List<Enrolment> enrolments = ObjectSelect.query(Enrolment).where(Enrolment.ID.in([1l, 2l, 3l])).select(context)
        SystemUser systemUser = SelectById.query(SystemUser, 1l).selectOne(context)

        MessageSpec messageSpec = new MessageSpec()

        messageSpec.template("test.sms")
        messageSpec.records(enrolments)
        messageSpec.bindings.put("text", "Thank you!")
        messageSpec.createdBy(systemUser)

        messageService.sendMessage(messageSpec)

        List<Message> messages = ObjectSelect.query(Message).prefetch(Message.MESSAGE_PERSONS.joint()).select(context)
        List<MessagePerson> messagePeople = ObjectSelect.query(MessagePerson).select(context)


        assertEquals(3, messages.size())
        assertEquals(1, messagePeople.size())

        Message messageToSecondStudent = messages.find { it.messagePersons.find { it.contact.id == 2l } }
        assertNotNull("The SMS would be sent to the second student!", messageToSecondStudent)
        assertEquals("Hi, Student Second. Thank you!", messageToSecondStudent.smsText)
        assertNull("The subject should be null", messageToSecondStudent.emailSubject)
        assertNull("The textBody should be null", messageToSecondStudent.emailBody)
        assertNull("The htmlBody should be null", messageToSecondStudent.emailHtmlBody)
        assertNull("The email from should be null", messageToSecondStudent.emailFrom)
        assertEquals(systemUser, messageToSecondStudent.createdBy)
        assertNull(messageToSecondStudent.creatorKey)
    }

    @Test
    void sendSMTPMessageTest() {
        ICayenneService cayenneService = injector.getInstance(CayenneService)
        PreferenceController preferenceController = injector.getInstance(PreferenceController)
        TemplateService templateService = injector.getInstance(TemplateService)
        AuditService auditService = injector.getInstance(AuditService)

        MailDeliveryService mailDeliveryService = mock(MailDeliveryService)
        ArgumentCaptor<MailDeliveryParam> mailDeliveryParamCapture = ArgumentCaptor.forClass(MailDeliveryParam)
        doNothing().when(mailDeliveryService).sendEmail(mailDeliveryParamCapture.capture())

        MessageService messageService = new MessageService(cayenneService, preferenceController, templateService, mailDeliveryService, auditService)

        MessageSpec messageSpec = new MessageSpec()

        messageSpec.subject("Hello World")
        messageSpec.content("That is content.")
        messageSpec.from("fromEmail@gmail.com", "test college")
        messageSpec.to("toEmail@gmail.com")
        messageSpec.cc("ccEmail@gmail.com")
        messageSpec.bcc("bccEmail@gmail.com")
        messageSpec.attachment("attachText", "text/plain", "Example of attachment")

        messageService.sendMessage(messageSpec)

        MailDeliveryParam mailDeliveryParamValue = mailDeliveryParamCapture.getValue()
        assertNotNull(mailDeliveryParamValue)
        assertEquals("Hello World", mailDeliveryParamValue.getSubject.get())
        assertEquals(1, mailDeliveryParamValue.getAddressesTO.get().size())
        assertEquals(1, mailDeliveryParamValue.getAddressesCC.get().size())
        assertEquals(1, mailDeliveryParamValue.getAddressesBCC.get().size())

        assertNotNull(mailDeliveryParamValue.getFrom.get())
        InternetAddress address = mailDeliveryParamValue.getFrom.get()
        assertEquals("fromEmail@gmail.com", address.address)
        assertEquals("test college", address.personal)

        assertNotNull(mailDeliveryParamValue.getContent.get())
        Multipart multipart = mailDeliveryParamValue.getContent.get()
        assertEquals("That is content.", multipart.getBodyPart(0).content)
        assertEquals("text/plain", multipart.getBodyPart(0).contentType)
        assertEquals("Example of attachment", multipart.getBodyPart(1).content)
        assertEquals("text/plain", multipart.getBodyPart(1).contentType)
    }
}
