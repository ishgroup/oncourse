/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.scripting.api

import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.server.scripting.ScriptParameters
import org.apache.cayenne.ObjectContext
import static org.junit.Assert.assertEquals
import org.junit.Test

class TemplateServiceTest extends CayenneIshTestCase {

	@Test
	void testRenderTemplate() throws Exception {
		ObjectContext context = injector.getInstance(ICayenneService.class).getNewContext()
		TemplateService templateService = injector.getInstance(TemplateService.class)

		EmailTemplate template = context.newObject(EmailTemplate.class)
		template.setName("test")
		template.setKeyCode("test.plainTemplate")
		template.setBodyPlain('Hello ${contact.firstName} ${contact.lastName}!')
		template.setBodyHtml('Hello <h3>${contact.firstName} ${contact.lastName}!</h3>')

		Contact contact = new Contact()
		contact.setFirstName("John")
		contact.setLastName("Test")

		assertEquals("Hello John Test!", templateService.renderPlain(
				template, ScriptParameters.empty().add("contact", contact).asMap()))
		assertEquals("Hello <h3>John Test!</h3>", templateService.renderHtml(
				template, ScriptParameters.empty().add("contact", contact).asMap()))
	}

	@Test
	void testRenderNestedTemplates() throws Exception {
		ObjectContext context = injector.getInstance(ICayenneService.class).getNewContext()
		TemplateService templateService = injector.getInstance(TemplateService.class)

		EmailTemplate footer = context.newObject(EmailTemplate.class)
		footer.setName("footer")
		footer.setKeyCode("test.footer")
		footer.setBodyPlain("Nice to meet you!")
		footer.setBodyHtml("<h3>Nice to meet you!</h3>")

		EmailTemplate template = context.newObject(EmailTemplate.class)
		template.setName("test")
		template.setName("test.htmlTemplate")
		template.setBodyPlain('Hello ${contact.firstName} ${contact.lastName}!\n' +
				'${render("test.footer")}')
		template.setBodyHtml('Hello <h3>${contact.firstName} ${contact.lastName}!</h3>\n' +
				'${render("test.footer")}')

		context.commitChanges()

		Contact contact = new Contact()
		contact.setFirstName("John")
		contact.setLastName("Test")

		assertEquals("Hello John Test!\nNice to meet you!",
				templateService.renderPlain(template, ScriptParameters.empty().add("contact", contact).asMap()))
		assertEquals("Hello <h3>John Test!</h3>\n<h3>Nice to meet you!</h3>",
				templateService.renderHtml(template, ScriptParameters.empty().add("contact", contact).asMap()))
	}
}
