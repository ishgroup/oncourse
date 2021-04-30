/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.scripting.api

import ish.CayenneIshTestCase
import ish.common.types.MessageType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.scripting.GroovyScriptService
import ish.oncourse.server.scripting.ScriptParameters
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.mail.MessagingException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

class EmailServiceTest extends CayenneIshTestCase {
	
	@BeforeEach
	void setup() throws Exception {
		wipeTables()
		InputStream st = GroovyScriptService.class.getClassLoader().getResourceAsStream("ish/oncourse/server/scripting/api/emailServiceTestDataSet.xml")
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
		builder.setColumnSensing(true)
		FlatXmlDataSet dataSet = builder.build(st)
		executeDatabaseOperation(dataSet)
	}


	@Test
	void testQueueEmail() throws Exception {
		ObjectContext context = injector.getInstance(ICayenneService.class).getNewContext()
		EmailService emailService = injector.getInstance(EmailService.class)

		Contact contact = SelectById.query(Contact.class, 1).selectOne(context)
		EmailMessage message = emailService.create("test.htmlTemplate")

		message.bind(ScriptParameters.empty().add("contact", contact).asMap())
		message.to(contact)
		message.from("college@test.test")

		message.send()

		List<Message> messages = context.select(SelectQuery.query(Message.class))
		assertEquals(1, messages.size())
		Message m = messages.get(0)

		assertEquals("college@test.test", m.getEmailFrom())
		assertEquals("Test Email", m.getEmailSubject())
		assertEquals("Hello John Test!", m.getEmailBody())
		assertEquals("Hello <h3>John Test!</h3>", m.getEmailHtmlBody())

		assertEquals(1, m.getMessagePersons().size())
		assertEquals(contact.getObjectId(), m.getMessagePersons().get(0).getContact().getObjectId())
		assertEquals(MessageType.EMAIL, m.getMessagePersons().get(0).getType())
	}

	/**
	 * tests simple email sending with and without defined key and keyCollision
	 * @throws MessagingException
	 */
	@Test
	void testEmailSend() throws MessagingException {
		ObjectContext context = injector.getInstance(ICayenneService.class).getNewContext()
		EmailService emailService = injector.getInstance(EmailService.class)

		//create EmailSpec, it's recipients and binding
        EmailSpec spec = new EmailSpec()
		List<Contact> contacts = ObjectSelect.query(Contact.class).select(context)
		Map<String, Object> bindings = new HashMap<>()
		bindings.put("contact", contacts.get(0))

		//prepare EmailSpec (1 template, 2 contacts, 0 Message_Person, 0 Audit)
		spec.template("test.htmlTemplate")
		spec.bindings(bindings)
		spec.to(contacts.toArray(new Contact[contacts.size()]) as Contact[])


		// 1) without creatorKey and collision rules
        //expected: all contacts will get a message. Count of message_Person records will become 2.
        emailService.createEmail(spec)
		assertEquals(2, ObjectSelect.query(MessagePerson.class).selectCount(context))


		// 2) without creationKey and with collision drop/error
		spec.keyCollision("drop")

		emailService.createEmail(spec)
		assertEquals(4, ObjectSelect.query(MessagePerson.class).selectCount(context))


		// 3) trying to enter NULL creatorKey
		//expected: nullPointerException
		try {
			spec.key(null)
			fail()
		} catch (NullPointerException e) {}


        // 4) with creatorKey and drop collision
        //expected: all contacts will get a message, cause nobody have the message with creatorKey. Count of message_Person records will become 6.
        spec.key("testKey")
		emailService.createEmail(spec)
		assertEquals(6, ObjectSelect.query(MessagePerson.class).selectCount(context))


		// 5) the same creatorKey and collision
        //expected: all contacts already have such message (same creatorKey), nobody will get this message. Count of message_Person records will stay 6.
        emailService.createEmail(spec)
		assertEquals(6, ObjectSelect.query(MessagePerson.class).selectCount(context))


		// 6) with the same creatorKey and collision, written in wrong case ('Drop')
		//expected: IllegalArgumentException
		try {
			spec.keyCollision("Drop")
			fail()
		} catch (IllegalArgumentException e){}


		// 7) with the same creatorKey and non existing collision
		//expected: IllegalArgumentException
		try {
			spec.keyCollision("error1")
			fail()
		} catch (IllegalArgumentException e){}


		// 8) the same creatorKey and 'error' collision
		//expected: all contacts already have such message (same creatorKey), nobody will get this message. Audit records will be created (reason - 'error' rule) for every contact. Count of message_Person records will stay 6.
		spec.keyCollision("error")
		emailService.createEmail(spec)
		assertEquals(6, ObjectSelect.query(MessagePerson.class).selectCount(context))


		// 9) with unique creationKey and error collision
		//expected: all contacts will get a message, cause nobody have the message with creatorKey. Count of message_Person records will become 8. 
        spec.key("newTestKey")
		spec.keyCollision("error")
		emailService.createEmail(spec)
		assertEquals(8, ObjectSelect.query(MessagePerson.class).selectCount(context))
	}
}
