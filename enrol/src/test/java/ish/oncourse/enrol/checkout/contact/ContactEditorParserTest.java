package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.HTMLUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContactEditorParserTest {


	@Test
	public void test() throws ParseException {
		HashMap<String,String> parameters = new HashMap<String, String>();
		parameters.put(street.name(), "");
		parameters.put(suburb.name(), "");
		parameters.put(state.name(), "");
		parameters.put(postcode.name(), "");
		parameters.put(homePhoneNumber.name(), "");
		parameters.put(businessPhoneNumber.name(), "");
		parameters.put(faxNumber.name(), "");
		parameters.put(mobilePhoneNumber.name(), "");
		parameters.put(dateOfBirth.name(), "11/d11/d2011");


		SimpleDateFormat dateFormat = new SimpleDateFormat(Checkout.DATE_FIELD_PARSE_FORMAT);
		Contact contact = new Contact();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -19);
		contact.setDateOfBirth(calendar.getTime());

		ICountryService countryService = mock(ICountryService.class);

		PreferenceController preferenceController = mock(PreferenceController.class);
		ContactFieldHelper contactFieldHelper = mock(ContactFieldHelper.class);
		Messages messages = mock(Messages.class);
        Request request = mock(Request.class);
        when(request.getParameter(Contact.IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY)).thenReturn(HTMLUtils.VALUE_on);
        when(request.getParameter(Contact.IS_MARKETING_VIA_POST_ALLOWED_PROPERTY)).thenReturn(HTMLUtils.VALUE_on);
        when(request.getParameter(Contact.IS_MARKETING_VIA_SMSALLOWED_PROPERTY)).thenReturn(HTMLUtils.VALUE_on);
        when(request.getParameter(Contact.DATE_OF_BIRTH_PROPERTY)).thenReturn("11/d11/d2011");
        when(request.getParameter(Contact.POSTCODE_PROPERTY)).thenReturn("12345");

        when(contactFieldHelper.getPreferenceController()).thenReturn(preferenceController);
        when(contactFieldHelper.isRequiredField(FieldDescriptor.street)).thenReturn(true);
        when(preferenceController.getEnrolmentMinAge()).thenReturn(18);
		when(messages.format(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge, 18)).thenReturn(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge);
        when(messages.format(ContactEditorParser.KEY_ERROR_dateOfBirth_shouldBeInPast)).thenReturn(ContactEditorParser.KEY_ERROR_dateOfBirth_shouldBeInPast);
        ContactEditorParser parser = new ContactEditorParser();
		parser.setCountryService(countryService);
		parser.setContact(contact);
        parser.setRequest(request);
		parser.setContactFieldHelper(contactFieldHelper);
		parser.setMessages(messages);
        parser.setDateFormat(new SimpleDateFormat(Checkout.DATE_FIELD_PARSE_FORMAT));

        FieldDescriptor[] fieldDescriptors =  FieldDescriptor.values();
        ArrayList<String> fields = new ArrayList<String>();
        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            fields.add(fieldDescriptor.name());
        }
        parser.setVisibleFields(fields);
        parser.parse();
        assertNotNull(parser.getErrors());

        testValidateDateOfBirth(parser, contact);
        testParseMarketingFields(parser);

    }

    private void testValidateDateOfBirth(ContactEditorParser parser, Contact contact) {
        contact.setDateOfBirth(null);
        String error = parser.validate(dateOfBirth);
        assertNull(error);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -17);
        contact.setDateOfBirth(calendar.getTime());
        parser.setContact(contact);
        error = parser.validate(dateOfBirth);
        assertNotNull(error);
        assertEquals(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge, error);

        contact.setDateOfBirth(null);
        error = parser.validate(dateOfBirth);
        assertNull(error);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        contact.setDateOfBirth(calendar.getTime());
        error = parser.validate(dateOfBirth);
        assertNotNull(error);
        assertEquals("The birth date cannot be in the future.", error);

    }

    private void testParseMarketingFields(ContactEditorParser parser)
    {
        parser.parseMarketingFields();
        assertTrue(parser.getContact().getIsMarketingViaEmailAllowed());
        assertTrue(parser.getContact().getIsMarketingViaPostAllowed());
        assertTrue(parser.getContact().getIsMarketingViaSMSAllowed());
    }


}
