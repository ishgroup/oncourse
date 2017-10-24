package ish.oncourse.portal.components.profile;

import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.*;
import ish.oncourse.portal.pages.Profile;
import ish.oncourse.portal.pages.Timetable;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.util.GetCustomFieldTypeByKey;
import ish.oncourse.portal.util.PortalContactValidator;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.Preferences;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.util.ValidateHandler;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;
import static org.apache.tapestry5.EventConstants.VALIDATE;

/**
 * User: artem
 * Date: 10/29/13
 * Time: 5:49 PM
 */
public class ProfileForm {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IPortalService portalService;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private ICountryService countryService;

	@Property
	@Parameter
	private boolean requireAdditionalInfo;

	@InjectComponent
	@Property
	private Form profileForm;

	@InjectPage
	private Profile profile;

	@Inject
	private Messages messages;

	private Messages avetmissMessages;

	@Parameter
	@Property
	private Contact contact;

	@Property
	private String customFieldKey;

	private Map<String, String> customFieldContainer = new HashMap<>();

	private GetCustomFieldTypeByKey getCustomFieldType;

	/**
	 * reset form method flag
	 */
	private boolean reset;

	private Timetable timetable;

	@Property
	private ValidateHandler validateHandler = new ValidateHandler();

	private ContactFieldHelper contactFieldHelper;

	private static final DateFormat DATE_FIELD_PARSE_FORMAT = new SimpleDateFormat(FormatUtils.DATE_FIELD_PARSE_FORMAT);
	private static final DateFormat DATE_FIELD_SHOW_FORMAT = new SimpleDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT);

	static {
		DATE_FIELD_PARSE_FORMAT.setLenient(false);
	}

	@SetupRender
	@OnEvent(value = EventConstants.PREPARE_FOR_SUBMIT)
	void setupRender() {
		customFieldContainer.clear();
		if (contact == null) {
			contact = portalService.getContact();
		}

		List<CustomFieldType> customFieldTypes = ObjectSelect.query(CustomFieldType.class)
				.where(CustomFieldType.COLLEGE.eq(contact.getCollege())
						.andExp(CustomFieldType.ENTITY_NAME.eq(Contact.class.getSimpleName())))
				.select(contact.getObjectContext());
		
		getCustomFieldType = GetCustomFieldTypeByKey.valueOf(customFieldTypes, contact.getCollege().getId());

		for (CustomFieldType fieldType : customFieldTypes) {
			if (getContactFieldHelper().isCustomFieldTypeVisible(fieldType) && Contact.class.getSimpleName().equals(fieldType.getEntityName())) {
				customFieldContainer.put(fieldType.getKey(), null);
			}
		}
		//fill values for fields which already predefined for contact
		for (CustomField field : contact.getCustomFields()) {
			if (getContactFieldHelper().isCustomFieldVisible(field) &&
					Contact.class.getSimpleName().equals(field.getCustomFieldType().getEntityName())) {
				customFieldContainer.put(field.getCustomFieldType().getKey(), field.getValue());
			}
		}
	}

	@Cached
	public ContactFieldHelper getContactFieldHelper() {
		if (contactFieldHelper == null) {
			contactFieldHelper = new ContactFieldHelper(preferenceController, Preferences.ContactFieldSet.enrolment);
		}
		return contactFieldHelper;
	}

	@AfterRender
	void afteRender() {
		validateHandler.getErrors().clear();

	}

	public String getDefaultValue() {
		return getCustomFieldType.get(customFieldKey).getDefaultValue();
	}

	public String getCurrentCustomFieldValue() {
		return customFieldContainer.get(customFieldKey);
	}

	public String getCurrentCustomFieldName() {
		return getCustomFieldType.get(customFieldKey).getName();
	}

	public void setCurrentCustomFieldValue(String value) {
		customFieldContainer.put(customFieldKey, value);
	}

	public Set<String> getCustomFieldKeys() {
		return customFieldContainer.keySet();
	}

	public boolean customFieldRequired(String customFieldKey) {
		return getContactFieldHelper().isCustomFieldTypeRequired(getCustomFieldType.get(customFieldKey));
	}

	public boolean visible(String fieldName) {
		return getContactFieldHelper().getVisibleFields(contact, false).contains(fieldName);
	}

	public boolean required(String fieldName) {
		return getContactFieldHelper().isRequiredField(PreferenceController.FieldDescriptor.valueOf(fieldName), contact);
	}

	public Messages getAvetmissMessages() {
		if (avetmissMessages == null) {
			avetmissMessages = MessagesImpl.forClass(AvetmissStrings.class);
		}
		return avetmissMessages;
	}

	public String messageBy(String fieldName) {
		return getAvetmissMessages().get(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE, fieldName));
	}

	public String getBirthDateProperty() {
		Date dateOfBirth = contact.getDateOfBirth();
		if (dateOfBirth == null) {
			return null;
		}
		return DATE_FIELD_SHOW_FORMAT.format(dateOfBirth);
	}

	public void setBirthDateProperty(String birthDateProperty) {
		try {
			if (StringUtils.trimToNull(birthDateProperty) != null) {
				Date parsedDate = DATE_FIELD_PARSE_FORMAT.parse(birthDateProperty);
				contact.setDateOfBirth(parsedDate);
			} else {
				contact.setDateOfBirth(null);
			}
		} catch (ParseException e) {
			validateHandler.getErrors().put(FieldDescriptor.dateOfBirth.name(), messages.get("message-birthDateWrongFormat"));
		}
	}

	public String getContactCountry() {
		Country country = contact.getCountry();
		if (country == null) {
			return ICountryService.DEFAULT_COUNTRY_NAME;
		}
		return country.getName();

	}

	public void setContactCountry(String value) {
		Country country = countryService.getCountryByName(value);
		if (country == null) {
			validateHandler.getErrors().put(FieldDescriptor.country.name(), messageBy(Contact.COUNTRY_PROPERTY));
		} else {
			contact.setCountry(contact.getObjectContext().localObject(country));
		}
	}


	@OnEvent(component = "profileForm", value = VALIDATE)
	void validate() {
		validateHandler = new ValidateHandler();
		PortalContactValidator.valueOf(contact, customFieldContainer, getContactFieldHelper(), validateHandler).validate();
		for (Map.Entry<String, String> entry : validateHandler.getErrors().entrySet()) {
			profileForm.recordError(entry.getValue());
		}
	}

	@OnEvent(component = "profileForm", value = EventConstants.SUCCESS)
	Object submitted() {
		Map<String, CustomField> contactFields = new HashMap<>();

		//collect all custom field which was already defined for contact
		for (CustomField field : contact.getCustomFields()) {
			contactFields.put(field.getCustomFieldType().getKey(), field);
		}

		//iterate by all fields from form
		for (Map.Entry<String, String> customFieldEntry : customFieldContainer.entrySet()) {
			CustomField field = contactFields.get(customFieldEntry.getKey());
			if (field != null) {
				//reset value if field for such custom field type already exist for contact
				field.setValue(customFieldEntry.getValue());
			} else if (customFieldEntry.getValue() != null) {
				//create new custom field if value for such custom field type populated on form
				CustomField newField = contact.getObjectContext().newObject(ContactCustomField.class);
				newField.setCustomFieldType(getCustomFieldType.get(customFieldEntry.getKey()));
				newField.setValue(customFieldEntry.getValue());
				newField.setRelatedObject(contact);
				newField.setCollege(contact.getCollege());
			}
		}

		contact.getObjectContext().commitChanges();
		return profile;
	}

	public String getCountryList() {
		return countryService.getStringCountryList();
	}
}
