package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.enrolment;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContactEditorParserTest extends ACheckoutTest{

	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/contact/ContactEditorParserTest.xml");
	}

	@Test
	public void testValidationForCountryDependentFields() throws ParseException {

		Country country = new Country();
		country.setName(ICountryService.DEFAULT_COUNTRY_NAME);
		Contact contact = mock(Contact.class);
		when(contact.getPostcode()).thenReturn("11111");
		when(contact.getHomePhoneNumber()).thenReturn("04123456781");
		when(contact.getBusinessPhoneNumber()).thenReturn("04123456781");
		when(contact.getFaxNumber()).thenReturn("04123456781");
		when(contact.getMobilePhoneNumber()).thenReturn("04123456781");
		when(contact.getCountry()).thenReturn(country);

		when(contact.validatePostcode()).thenCallRealMethod();
		when(contact.validateHomePhone()).thenCallRealMethod();
		when(contact.validateBusinessPhone()).thenCallRealMethod();
		when(contact.validateFax()).thenCallRealMethod();
		when(contact.validateMobilePhone()).thenCallRealMethod();

		ContactEditorParser parser = new ContactEditorParser();
		parser.setContact(contact);
		assertNotNull(parser.validate(FieldDescriptor.postcode));
		assertNotNull(parser.validate(FieldDescriptor.homePhoneNumber));
		assertNotNull(parser.validate(FieldDescriptor.businessPhoneNumber));
		assertNotNull(parser.validate(FieldDescriptor.faxNumber));
		assertNotNull(parser.validate(FieldDescriptor.mobilePhoneNumber));

		country.setName("USA");
		assertNull(parser.validate(FieldDescriptor.postcode));
		assertNull(parser.validate(FieldDescriptor.homePhoneNumber));
		assertNull(parser.validate(FieldDescriptor.businessPhoneNumber));
		assertNull(parser.validate(FieldDescriptor.faxNumber));
		assertNull(parser.validate(FieldDescriptor.mobilePhoneNumber));

	}

	@Test
	public void test() throws ParseException {
		HashMap<String,String> parameters = new HashMap<>();
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
		ObjectContext context = mock(ObjectContext.class);
		Contact contact = mock(Contact.class);
		Country country = new Country();
		country.setName(ICountryService.DEFAULT_COUNTRY_NAME);

		when(contact.getCountry()).thenReturn(country);

		//the code emulates Contact.postAdd method
		when(contact.getIsMarketingViaEmailAllowed()).thenReturn(Boolean.TRUE);
		when(contact.getIsMarketingViaPostAllowed()).thenReturn(Boolean.TRUE);
		when(contact.getIsMarketingViaSMSAllowed()).thenReturn(Boolean.TRUE);
		when(contact.getIsMale()).thenReturn(null);
		when(contact.getObjectContext()).thenReturn(context);
		when(context.localObject(country)).thenReturn(country);


		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -19);
		when(contact.getDateOfBirth()).thenReturn(calendar.getTime());

		ICountryService countryService = mock(ICountryService.class);
		when(countryService.getCountryByName(ICountryService.DEFAULT_COUNTRY_NAME)).thenReturn(country);

		PreferenceController preferenceController = mock(PreferenceController.class);
		ContactFieldHelper contactFieldHelper = mock(ContactFieldHelper.class);
		Messages messages = mock(Messages.class);
        Request request = mock(Request.class);
        when(request.getParameter(Contact.IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY)).thenReturn(null);
        when(request.getParameter(Contact.IS_MARKETING_VIA_POST_ALLOWED_PROPERTY)).thenReturn(null);
        when(request.getParameter(Contact.IS_MARKETING_VIA_SMSALLOWED_PROPERTY)).thenReturn(null);
        when(request.getParameter(Contact.DATE_OF_BIRTH_PROPERTY)).thenReturn("11/d11/d2011");
        when(request.getParameter(Contact.POSTCODE_PROPERTY)).thenReturn("12345");

        when(contactFieldHelper.getPreferenceController()).thenReturn(preferenceController);
        when(contactFieldHelper.isRequiredField(FieldDescriptor.street)).thenReturn(true);
        when(preferenceController.getEnrolmentMinAge()).thenReturn(18);
		when(messages.format(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge, 18))
			.thenReturn(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge);
        when(messages.format(ContactEditorParser.KEY_ERROR_dateOfBirth_shouldBeInPast))
			.thenReturn(ContactEditorParser.KEY_ERROR_dateOfBirth_shouldBeInPast);
		when(messages.get(ContactEditorParser.KEY_ERROR_MESSAGE_birthdate_old)).
			thenReturn(ContactEditorParser.KEY_ERROR_MESSAGE_birthdate_old);
        ContactEditorParser parser = new ContactEditorParser();
		parser.setCountryService(countryService);
		parser.setContact(contact);
        parser.setRequest(request);
		parser.setContactFieldHelper(contactFieldHelper);
		parser.setMessages(messages);
        parser.setDateFormat(new SimpleDateFormat(Checkout.DATE_FIELD_PARSE_FORMAT));

        FieldDescriptor[] fieldDescriptors =  FieldDescriptor.values();
        ArrayList<String> fields = new ArrayList<>();
        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            fields.add(fieldDescriptor.name());
        }
        parser.setVisibleFields(fields);
        parser.parse();
        assertNotNull(parser.getErrors());

        testValidateDateOfBirth(parser, contact);
        testParseMarketingFields(parser);
        assertNull(parser.getContact().getIsMale());

		when(request.getParameter(Contact.COUNTRY_PROPERTY)).thenReturn(null);
		//clean the errors before parse
		parser.parse();

		assertNotNull(contact.getCountry());
		verify(contact, atLeastOnce()).writeProperty(Contact.COUNTRY_PROPERTY,
			countryService.getCountryByName(ICountryService.DEFAULT_COUNTRY_NAME));

		//additional check for birth date validation
		parser.getErrors().clear();
		assertTrue("No errors should appears", parser.getErrors().isEmpty());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -50);
		String testDate = parser.dateFormat.format(cal.getTime());
		when(request.getParameter(Contact.DATE_OF_BIRTH_PROPERTY)).thenReturn(testDate);
		when(contact.getDateOfBirth()).thenReturn(cal.getTime());
		parser.setRequest(request);
		parser.setContact(contact);
		parser.parse();
		assertNull("No date of birth validation errors should be found", parser.getErrors().get("dateOfBirth"));

		//check for 2 digit birth date
		cal.set(Calendar.YEAR, 84);
		testDate = String.format("%s/%s/%s", cal.get(Calendar.DATE), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
		when(request.getParameter(Contact.DATE_OF_BIRTH_PROPERTY)).thenReturn(testDate);
		when(contact.getDateOfBirth()).thenReturn(cal.getTime());
		parser.setRequest(request);
		parser.setContact(contact);
		parser.parse();
		assertNull("No date of birth validation errors should be found", parser.getErrors().get("dateOfBirth"));

		//check for 3 digit birth date
		cal.set(Calendar.YEAR, 184);
		testDate = String.format("%s/%s/%s", cal.get(Calendar.DATE), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
		//parser.dateFormat.format(cal.getTime());
		when(request.getParameter(Contact.DATE_OF_BIRTH_PROPERTY)).thenReturn(testDate);
		when(contact.getDateOfBirth()).thenReturn(cal.getTime());
		parser.setRequest(request);
		parser.setContact(contact);
		parser.parse();
		assertNotNull("Date of birth validation errors should be found", parser.getErrors().get("dateOfBirth"));
    }



    private void testValidateDateOfBirth(ContactEditorParser parser, Contact contact) {
        contact.setDateOfBirth(null);
        String error = parser.validate(dateOfBirth);
        assertNull(error);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -17);
        when(contact.getDateOfBirth()).thenReturn(calendar.getTime());
        parser.setContact(contact);
        error = parser.validate(dateOfBirth);
        assertNotNull(error);
        assertEquals(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge, error);

		when(contact.getDateOfBirth()).thenReturn(null);
        error = parser.validate(dateOfBirth);
        assertNull(error);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
		when(contact.getDateOfBirth()).thenReturn(calendar.getTime());
        error = parser.validate(dateOfBirth);
        assertNotNull(error);
        assertEquals(ContactEditorParser.KEY_ERROR_dateOfBirth_youngAge, error);

    }

    private void testParseMarketingFields(ContactEditorParser parser)
    {
        parser.parseMarketingFields();
        assertNotNull(parser.getContact().getIsMarketingViaEmailAllowed());
		assertNotNull(parser.getContact().getIsMarketingViaPostAllowed());
		assertNotNull(parser.getContact().getIsMarketingViaSMSAllowed());
    }


	@Test
	public void testCountryForExistingContact() throws ParseException {
		ObjectContext context = cayenneService.newContext();

		Contact contact = Cayenne.objectForPK(context, Contact.class, 1001);


		PreferenceController preferenceController = getService(PreferenceController.class);
		ICountryService countryService = getService(ICountryService.class);

		ContactFieldHelper contactFieldHelper = new ContactFieldHelper(preferenceController, enrolment);

		Request request = mock(Request.class);
		when(request.getParameter(Contact.DATE_OF_BIRTH_PROPERTY)).thenReturn("11/11/2011");


		Messages messages = mock(Messages.class);
		when(messages.format(ContactEditorParser.KEY_ERROR_dateOfBirth_shouldBeInPast))
			.thenReturn(ContactEditorParser.KEY_ERROR_dateOfBirth_shouldBeInPast);

		ContactEditorParser parser = new ContactEditorParser();
		parser.setCountryService(countryService);
		parser.setContact(contact);
		parser.setRequest(request);
		parser.setContactFieldHelper(contactFieldHelper);
		parser.setMessages(messages);
		parser.setDateFormat(new SimpleDateFormat(Checkout.DATE_FIELD_PARSE_FORMAT));

		FieldDescriptor[] fieldDescriptors =  new FieldDescriptor[]{FieldDescriptor.dateOfBirth};
		ArrayList<String> fields = new ArrayList<>();
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			fields.add(fieldDescriptor.name());
		}
		parser.setVisibleFields(fields);
		parser.parse();
	}


}
