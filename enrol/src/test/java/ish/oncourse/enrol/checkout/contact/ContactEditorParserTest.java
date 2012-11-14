package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.ioc.Messages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContactEditorParserTest {


	@Test
	public void test() throws ParseException {
		HashMap<String,String> parameters = new HashMap<String, String>();
		parameters.put(ContactFieldHelper.FieldDescriptor.street.name(), "");
		parameters.put(ContactFieldHelper.FieldDescriptor.suburb.name(), "");
		parameters.put(ContactFieldHelper.FieldDescriptor.state.name(), "");
		parameters.put(ContactFieldHelper.FieldDescriptor.postcode.name(), "");
		parameters.put(ContactFieldHelper.FieldDescriptor.homePhoneNumber.name(), "");
		parameters.put(ContactFieldHelper.FieldDescriptor.businessPhoneNumber.name(), "");
		parameters.put(ContactFieldHelper.FieldDescriptor.faxNumber.name(), "");
		parameters.put(ContactFieldHelper.FieldDescriptor.mobilePhoneNumber.name(), "");
		parameters.put(ContactFieldHelper.FieldDescriptor.dateOfBirth.name(), "");


		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
		Contact contact = new Contact();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -19);
		contact.setDateOfBirth(calendar.getTime());

		PreferenceController preferenceController = mock(PreferenceController.class);
		ContactFieldHelper contactFieldHelper = mock(ContactFieldHelper.class);
		Messages messages = mock(Messages.class);
		when(contactFieldHelper.getPreferenceController()).thenReturn(preferenceController);
		when(preferenceController.getEnrolmentMinAge()).thenReturn(18);
		when(messages.get(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge)).thenReturn(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge);
		ContactEditorParser parser = new ContactEditorParser();
		parser.setContact(contact);
		parser.setContactFieldHelper(contactFieldHelper);
		parser.setMessages(messages);

		String error = parser.validate(ContactFieldHelper.FieldDescriptor.dateOfBirth);
		assertNull(error);

		calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -17);
		contact.setDateOfBirth(calendar.getTime());
		parser.setContact(contact);
		error = parser.validate(ContactFieldHelper.FieldDescriptor.dateOfBirth);
		assertNotNull(error);
		assertEquals(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge, error);

	}
}
