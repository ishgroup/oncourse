/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model.field;

import ish.oncourse.cayenne.FieldInterface;
import ish.oncourse.common.field.PropertyGetSet;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.model.Contact;
import org.junit.Test;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldPropertyTest {

	@Test
	public void test() {
		Contact contact = new Contact();
		Date date = new Date();

		FieldInterface emailAddress = mock(FieldInterface.class);
		when(emailAddress.getProperty()).thenReturn("emailAddress");

		FieldInterface dateOfBirth = mock(FieldInterface.class);
		when(dateOfBirth.getProperty()).thenReturn("dateOfBirth");

		contact.setDateOfBirth(date);

		PropertyGetSetFactory factory = new PropertyGetSetFactory("ish.oncourse.model");
		PropertyGetSet emailGetSet = factory.get(emailAddress, contact);
		PropertyGetSet birthDayGetSet = factory.get(dateOfBirth, contact);

		assertNull(emailGetSet.get());
		assertEquals(birthDayGetSet.get(), date);

		String email = "email@com.au";
		emailGetSet.set(email);
		assertNotNull(contact.getEmailAddress());
		assertEquals(emailGetSet.get(), email);


		birthDayGetSet.set(null);
		assertNull(birthDayGetSet.get());
		assertNull(contact.getDateOfBirth());
	}
}
