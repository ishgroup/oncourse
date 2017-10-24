package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.components.ContactDetailStrings;
import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.enrol.utils.EnrolContactValidator;
import ish.oncourse.model.*;
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
import java.util.*;

import static ish.oncourse.services.preference.Preferences.ContactFieldSet.enrolment;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContactEditorParserTest extends ACheckoutTest{

	private Contact contact;
	private ICountryService countryService;
	private ContactFieldHelper contactFieldHelper;
	private ContactCustomFieldHolder contactCustomFieldHolder;
	private Request request;
	private Messages messages;


	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/contact/ContactEditorParserTest.xml");
	}

	@Test
	public void testValidationForCountryDependentFields() throws ParseException {

		Country country = mock(Country.class);
		when(country.getName()).thenReturn(ICountryService.DEFAULT_COUNTRY_NAME);

		Messages messages = mock(Messages.class);
		when(messages.get(ContactDetailStrings.KEY_ERROR_MESSAGE_message_postcode_4_digits)).thenReturn("Enter 4 digit postcode for Australian postcodes.");

		Contact contact = mock(Contact.class);
		when(contact.getPostcode()).thenReturn("11111");
		when(contact.getHomePhoneNumber()).thenReturn("04123456781");
		when(contact.getBusinessPhoneNumber()).thenReturn("04123456781");
		when(contact.getFaxNumber()).thenReturn("04123456781");
		when(contact.getMobilePhoneNumber()).thenReturn("04123456781");
		when(contact.getCountry()).thenReturn(country);

		EnrolContactValidator validator = EnrolContactValidator.valueOf(contact, Arrays.asList(Contact.POSTCODE.getName(),
				Contact.HOME_PHONE_NUMBER.getName(),
				Contact.BUSINESS_PHONE_NUMBER.getName(),
				Contact.FAX_NUMBER.getName(),
				Contact.MOBILE_PHONE_NUMBER.getName(),
				Contact.COUNTRY.getName()), messages);
		validator.validate();
		Map<String, String> errors = validator.getErrors();
		assertNotNull(errors.get(FieldDescriptor.postcode.name()));
		assertNotNull(errors.get(FieldDescriptor.homePhoneNumber.name()));
		assertNotNull(errors.get(FieldDescriptor.businessPhoneNumber.name()));
		assertNotNull(errors.get(FieldDescriptor.faxNumber.name()));
		assertNotNull(errors.get(FieldDescriptor.mobilePhoneNumber.name()));

		when(country.getName()).thenReturn("USA");
		validator = EnrolContactValidator.valueOf(contact, Arrays.asList(Contact.POSTCODE.getName(),
				Contact.HOME_PHONE_NUMBER.getName(),
				Contact.BUSINESS_PHONE_NUMBER.getName(),
				Contact.FAX_NUMBER.getName(),
				Contact.MOBILE_PHONE_NUMBER.getName(),
				Contact.COUNTRY.getName()), messages);
		validator.validate();
		errors = validator.getErrors();
		assertNull(errors.get(FieldDescriptor.postcode.name()));
		assertNull(errors.get(FieldDescriptor.homePhoneNumber.name()));
		assertNull(errors.get(FieldDescriptor.businessPhoneNumber.name()));
		assertNull(errors.get(FieldDescriptor.faxNumber.name()));
		assertNull(errors.get(FieldDescriptor.mobilePhoneNumber.name()));

	}

	@Test
	public void test() throws ParseException {

		prepareData();


        FieldDescriptor[] fieldDescriptors =  FieldDescriptor.values();
        ArrayList<String> fields = new ArrayList<>();
        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            fields.add(fieldDescriptor.name());
        }
		ContactEditorParser parser = ContactEditorParser.valueOf(request,
				contact, fields,
				messages, countryService, contactFieldHelper, contactCustomFieldHolder);
        parser.parse();
        assertNotNull(parser.getErrors());

        testValidateDateOfBirth(parser, contact);
        testParseMarketingFields(parser);
        assertNull(parser.getContact().getIsMale());

		when(request.getParameter(Contact.COUNTRY.getName())).thenReturn(null);
		parser = ContactEditorParser.valueOf(request,
				contact, fields,
				messages, countryService, contactFieldHelper, contactCustomFieldHolder);
		//clean the errors before parse
		parser.parse();

		assertNotNull(contact.getCountry());
		verify(contact, atLeastOnce()).writeProperty(Contact.COUNTRY.getName(),
				countryService.getCountryByName(ICountryService.DEFAULT_COUNTRY_NAME));

	}

	private void prepareData() {
		ObjectContext context = mock(ObjectContext.class);
		College college = mock(College.class);
		contact = spy(new Contact());
		Country country = new Country();
		country.setName(ICountryService.DEFAULT_COUNTRY_NAME);

		when(contact.getCountry()).thenReturn(country);
		when(contact.getCollege()).thenReturn(college);
		when(contact.getIsMale()).thenReturn(null);
		when(contact.getObjectContext()).thenReturn(context);
		when(context.localObject(country)).thenReturn(country);


		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -19);
		when(contact.getDateOfBirth()).thenReturn(calendar.getTime());

		countryService = mock(ICountryService.class);
		when(countryService.getCountryByName(ICountryService.DEFAULT_COUNTRY_NAME)).thenReturn(country);

		CustomFieldType customFieldType = mock(CustomFieldType.class);
		when(customFieldType.getObjectContext()).thenReturn(context);
		when(customFieldType.getIsMandatory()).thenReturn(false);
		when(customFieldType.getName()).thenReturn("Test custom field");
		when(customFieldType.getRequireForEnrolment()).thenReturn("Show");
		when(customFieldType.getRequireForMailingList()).thenReturn("Show");
		when(customFieldType.getRequireForWaitingList()).thenReturn("Show");
		when(college.getCustomFieldTypes()).thenReturn(Collections.singletonList(customFieldType));

		ContactCustomField customField = mock(ContactCustomField.class);
		when(customField.getObjectContext()).thenReturn(context);
		when(customField.getCustomFieldType()).thenReturn(customFieldType);
		when(customField.getValue()).thenReturn("test value");
		when(customField.getRelatedObject()).thenReturn(contact);
		
		when(contact.getCustomFields()).thenReturn(Collections.singletonList(customField));

		PreferenceController preferenceController = mock(PreferenceController.class);
		contactFieldHelper = mock(ContactFieldHelper.class);
		messages = mock(Messages.class);
		request = mock(Request.class);
		when(request.getParameter(Contact.IS_MARKETING_VIA_EMAIL_ALLOWED.getName())).thenReturn(null);
		when(request.getParameter(Contact.IS_MARKETING_VIA_POST_ALLOWED.getName())).thenReturn(null);
		when(request.getParameter(Contact.IS_MARKETING_VIA_SMSALLOWED.getName())).thenReturn("true");
		when(request.getParameter(Contact.DATE_OF_BIRTH.getName())).thenReturn("11/d11/d2011");
		when(request.getParameter(Contact.POSTCODE.getName())).thenReturn("12345");

		when(contactFieldHelper.getPreferenceController()).thenReturn(preferenceController);
		when(contactFieldHelper.isRequiredField(FieldDescriptor.street, contact)).thenReturn(true);
		when(contactFieldHelper.isCustomFieldTypeVisible(customFieldType)).thenReturn(true);
		when(preferenceController.getEnrolmentMinAge()).thenReturn(18);
		when(messages.format(ContactDetailStrings.KEY_ERROR_dateOfBirth_youngAge, 18))
			.thenReturn(ContactDetailStrings.KEY_ERROR_dateOfBirth_youngAge);
		when(messages.format(ContactDetailStrings.KEY_ERROR_dateOfBirth_shouldBeInPast))
			.thenReturn(ContactDetailStrings.KEY_ERROR_dateOfBirth_shouldBeInPast);
		when(messages.get(ContactDetailStrings.KEY_ERROR_MESSAGE_birthdate_old)).
			thenReturn(ContactDetailStrings.KEY_ERROR_MESSAGE_birthdate_old);
		contactCustomFieldHolder = ContactCustomFieldHolder.valueOf(contactFieldHelper, contact, false);
		contactCustomFieldHolder.setCustomFieldValue(customField.getCustomFieldType().getName(), customField.getValue());
	}

	@Test
	public void testDateOfBirth() {

		 prepareData();

		ContactEditorParser parser = ContactEditorParser.valueOf(request,
				contact, Collections.singletonList(Contact.DATE_OF_BIRTH.getName()),
				messages, countryService, contactFieldHelper, contactCustomFieldHolder);
		//additional check for birth date validation
		parser.getErrors().clear();
		assertTrue("No errors should appears", parser.getErrors().isEmpty());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -50);
		String testDate = parser.getDateFormat().format(cal.getTime());
		when(request.getParameter(Contact.DATE_OF_BIRTH.getName())).thenReturn(testDate);
		doCallRealMethod().when(contact).setDateOfBirth(any(Date.class));

		parser = ContactEditorParser.valueOf(request,
				contact, Collections.singletonList(Contact.DATE_OF_BIRTH.getName()),
				messages, countryService, contactFieldHelper, contactCustomFieldHolder);
		parser.parse();
		assertNull("No date of birth validation errors should be found", parser.getErrors().get(Contact.DATE_OF_BIRTH.getName()));

		//check for 2 digit birth date
		cal.set(Calendar.YEAR, 84);
		testDate = String.format("%s/%s/%s", cal.get(Calendar.DATE), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
		when(request.getParameter(Contact.DATE_OF_BIRTH.getName())).thenReturn(testDate);
		parser = ContactEditorParser.valueOf(request,
				contact, Collections.singletonList(Contact.DATE_OF_BIRTH.getName()),
				messages, countryService, contactFieldHelper, contactCustomFieldHolder);
		parser.parse();
		assertNull("No date of birth validation errors should be found", parser.getErrors().get(Contact.DATE_OF_BIRTH.getName()));

		//check for 3 digit birth date
		cal.set(Calendar.YEAR, 184);
		testDate = String.format("%s/%s/%s", cal.get(Calendar.DATE), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
		//parser.dateFormat.format(cal.getTime());
		when(request.getParameter(Contact.DATE_OF_BIRTH.getName())).thenReturn(testDate);
		when(contact.getDateOfBirth()).thenReturn(cal.getTime());
		parser = ContactEditorParser.valueOf(request,
				contact, Collections.singletonList(Contact.DATE_OF_BIRTH.getName()),
				messages, countryService, contactFieldHelper, contactCustomFieldHolder);
		parser.parse();
		assertNotNull("Date of birth validation errors should be found", parser.getErrors().get(Contact.DATE_OF_BIRTH.getName()));
	}



	private void testValidateDateOfBirth(ContactEditorParser parser, Contact contact) {
        contact.setDateOfBirth(null);
		String error = EnrolContactValidator.valueOf(contact, Collections.singletonList(Contact.DATE_OF_BIRTH.getName()), parser.getMessages())
				.validate().getErrors().get(FieldDescriptor.dateOfBirth.name());

        assertNull(error);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -17);
        when(contact.getDateOfBirth()).thenReturn(calendar.getTime());

		error = EnrolContactValidator.valueOf(contact, Collections.singletonList(Contact.DATE_OF_BIRTH.getName()), parser.getMessages())
				.validate().getErrors().get(FieldDescriptor.dateOfBirth.name());
        assertNull(error);

		when(contact.getDateOfBirth()).thenReturn(null);
		error = EnrolContactValidator.valueOf(contact, Collections.singletonList(Contact.DATE_OF_BIRTH.getName()), parser.getMessages())
				.validate().getErrors().get(FieldDescriptor.dateOfBirth.name());
		assertNull(error);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
		when(contact.getDateOfBirth()).thenReturn(calendar.getTime());
		when(messages.get(ContactDetailStrings.KEY_ERROR_dateOfBirth_shouldBeInPast)).thenReturn("Your date of birth must be in past.");
		error = EnrolContactValidator.valueOf(contact, Collections.singletonList(Contact.DATE_OF_BIRTH.getName()), parser.getMessages())
				.validate().getErrors().get(FieldDescriptor.dateOfBirth.name());
        assertNotNull(error);
		//message from Contact "The birth date cannot be in the future."
        assertEquals("Your date of birth must be in past.", error);

    }

    private void testParseMarketingFields(ContactEditorParser parser)
    {
        parser.parseMarketingFields();
        assertFalse(parser.getContact().getIsMarketingViaEmailAllowed());
		assertFalse(parser.getContact().getIsMarketingViaPostAllowed());
		assertTrue(parser.getContact().getIsMarketingViaSMSAllowed());
    }


	@Test
	public void testCountryForExistingContact() throws ParseException {
		ObjectContext context = cayenneService.newContext();

		Contact contact = Cayenne.objectForPK(context, Contact.class, 1001);


		PreferenceController preferenceController = getService(PreferenceController.class);
		ICountryService countryService = getService(ICountryService.class);

		ContactFieldHelper contactFieldHelper = new ContactFieldHelper(preferenceController, enrolment);
		ContactCustomFieldHolder fieldHolder = ContactCustomFieldHolder.valueOf(contactFieldHelper, contact, false);

		Request request = mock(Request.class);
		when(request.getParameter(Contact.DATE_OF_BIRTH.getName())).thenReturn("11/11/2011");


		Messages messages = mock(Messages.class);
		when(messages.format(ContactDetailStrings.KEY_ERROR_dateOfBirth_shouldBeInPast))
			.thenReturn(ContactDetailStrings.KEY_ERROR_dateOfBirth_shouldBeInPast);

		FieldDescriptor[] fieldDescriptors =  new FieldDescriptor[]{FieldDescriptor.dateOfBirth};
		ArrayList<String> fields = new ArrayList<>();
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			fields.add(fieldDescriptor.name());
		}
		ContactEditorParser parser = ContactEditorParser.valueOf(request,
				contact, Collections.singletonList(FieldDescriptor.dateOfBirth.name()),
				messages, countryService, contactFieldHelper, fieldHolder);
		parser.parse();
	}


}
