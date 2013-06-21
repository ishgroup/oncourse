package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.enrol.checkout.ContactCredentialsEncoder;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.Contact;
import org.apache.tapestry5.services.Request;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This test checks use case when an user is entering the same credetionals
 * but addting chars with code s less which is less or equal 0x20(space) to beginning or end of these credetionals
 * for exapmle: "Andrey" and "Andrey "
 */
public class WhitespaceDuplicateContactTest extends ACheckoutTest {

	private static final String FIRST_NAME = "Caroline";

	private static final String LAST_NAME_PART1 = "van";
	private static final String LAST_NAME_PART2 = "Riet";
	private static final String LAST_NAME = String.format("%s %s",LAST_NAME_PART1, LAST_NAME_PART2);

	private static final String EMAIL = "carolinevanriet@gmail.com";
	private IStudentService studentService;

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/contact/WhitespaceDuplicateContactTest.xml");
		studentService = getService(IStudentService.class);
	}

	@Test
	public void test() {
		Contact contact = studentService.getStudentContact(FIRST_NAME, LAST_NAME, EMAIL);
		assertNotNull(contact);

		for (char i = 0; i < 33; i++) {

			assertChar(contact, i);
		}

	}

	private void assertChar(Contact contact, char i) {
		ContactCredentials contactCredentials = new ContactCredentials();

		Request request = mock(Request.class);
		when(request.getParameter(AddContactParser.FIELD_NAME_firstName)).thenReturn(i + FIRST_NAME + i);
		when(request.getParameter(AddContactParser.FIELD_NAME_lastName)).thenReturn(i == ' ' ?
				i + LAST_NAME_PART1 + i + LAST_NAME_PART2 + i:
				i + LAST_NAME_PART1 + i + " " + i + LAST_NAME_PART2 + i);
		when(request.getParameter(AddContactParser.FIELD_NAME_email)).thenReturn(i + EMAIL + i);

		AddContactParser parser = new AddContactParser();
		parser.setRequest(request);
		parser.setContactCredentials(contactCredentials);
		parser.parse();
		assertEquals(FIRST_NAME, contactCredentials.getFirstName());
		assertEquals(LAST_NAME, contactCredentials.getLastName());
		assertEquals(EMAIL,contactCredentials.getEmail());

		assertEquals(0, parser.getErrors().size());

		ContactCredentialsEncoder encoder = new ContactCredentialsEncoder();
		encoder.setCollege(contact.getCollege());
		encoder.setContactCredentials(contactCredentials);
		encoder.setStudentService(studentService);
		encoder.setObjectContext(cayenneService.newContext());

		encoder.encode();

		Contact contact1 = encoder.getContact();
		assertEquals(contact.getId(), contact1.getId());
	}
}
