package ish.oncourse.portal.components.profile;

import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.CustomField;
import ish.oncourse.model.CustomFieldType;
import ish.oncourse.portal.pages.Profile;
import ish.oncourse.portal.pages.Timetable;
import ish.oncourse.portal.util.PortalContactValidator;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.util.ValidateHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;

/**
 * User: artem
 * Date: 10/29/13
 * Time: 5:49 PM
 */
public class ProfileForm {

    @Inject
    private ICayenneService cayenneService;

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
	private String customFieldName;

	private Map<String, String> customFieldContainer = new HashMap<>();

    /**
     * reset form method flag
     */
    private boolean reset;

    private Timetable timetable;

    @Property
    @Persist
    private ValidateHandler validateHandler;

	private ContactFieldHelper contactFieldHelper;

	private static final DateFormat DATE_FIELD_PARSE_FORMAT = new SimpleDateFormat(FormatUtils.DATE_FIELD_PARSE_FORMAT);
    private static final DateFormat DATE_FIELD_SHOW_FORMAT = new SimpleDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT);

    static {
        DATE_FIELD_PARSE_FORMAT.setLenient(false);
    }


    @SetupRender
    void beforeRender() {

        if (validateHandler == null)
        {
           validateHandler  = new ValidateHandler();
        }


		//collect all visible Custom field types provided by college
		for (CustomFieldType fieldType : contact.getCollege().getCustomFieldTypes()) {
			if (getContactFieldHelper().isCustomFieldTypeVisible(fieldType)) {
				customFieldContainer.put(fieldType.getName(), null);
			}
		}
		
		//fill values for fields which already predefined for contact
		for (CustomField field : contact.getCustomFields()) {
			if (getContactFieldHelper().isCustomFieldVisible(field)) {
				customFieldContainer.put(field.getCustomFieldType().getName(), field.getValue());
			}
		}
    }

	@Cached
	public ContactFieldHelper getContactFieldHelper() {
		if (contactFieldHelper == null) {
			contactFieldHelper = new ContactFieldHelper(preferenceController, PreferenceController.ContactFieldSet.enrolment);
		}
		return contactFieldHelper;
	}

    @AfterRender
    void afteRender(){
        validateHandler.getErrors().clear();

    }

	public String getDefaultValue() {
		return getCustomFieldTypeByName(customFieldName).getDefaultValue();
	}
	
	public String getCurrentCustomFieldValue() {
		return customFieldContainer.get(customFieldName);
	}

	public void setCurrentCustomFieldValue(String value) {
		customFieldContainer.put(customFieldName, value);
	}
	
	public Set<String> getCustomFieldNames() {
		return customFieldContainer.keySet();
	}
	
	public boolean customFieldRequired(String customFieldName) {
		return getContactFieldHelper().isCustomFieldTypeRequired(getCustomFieldTypeByName(customFieldName));
	}

	public CustomFieldType getCustomFieldTypeByName(String name) {
		for (CustomFieldType fieldType : contact.getCollege().getCustomFieldTypes()) {
			if (fieldType.getName().equals(name)) {
				return fieldType;
			}
		}

		return null;
	}

    public boolean visible(String fieldName) {
        return getContactFieldHelper().getVisibleFields(contact, false).contains(fieldName);
    }

    public boolean required(String fieldName) {
        return getContactFieldHelper().isRequiredField(PreferenceController.FieldDescriptor.valueOf(fieldName), contact);
    }

    public Messages getAvetmissMessages()
    {
        if (avetmissMessages == null)
        {
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


    boolean validate() {
	    PortalContactValidator.valueOf(contact, customFieldContainer, getContactFieldHelper(), validateHandler).validate();

	    for (Map.Entry<String, String> entry : validateHandler.getErrors().entrySet()) {
			profileForm.recordError(entry.getValue());
		}
		
        return !profileForm.getHasErrors();
    }

    @OnEvent(component = "profileForm")
    Object submitted() {
        if (validate())
        {
			Map<String, CustomField> contactFields = new HashMap<>();
			
			//collect all custom field which was already defined for contact
			for (CustomField field : contact.getCustomFields()) {
				contactFields.put(field.getCustomFieldType().getName(), field);
			}
			
			//iterate by all fields from form
			for (Map.Entry<String, String> customFieldEntry : customFieldContainer.entrySet()) {
				CustomField field = contactFields.get(customFieldEntry.getKey());
				if (field != null) {
					//reset value if field for such custom field type already exist for contact
					field.setValue(customFieldEntry.getValue());
				} else if (customFieldEntry.getValue() != null){
					//create new custom field if value for such custom field type populated on form
					CustomField newField = contact.getObjectContext().newObject(CustomField.class);
					newField.setCustomFieldType(getCustomFieldTypeByName(customFieldEntry.getKey()));
					newField.setValue(customFieldEntry.getValue());
					newField.setRelatedObject(contact);
					newField.setCollege(contact.getCollege());
				}
			}

			contact.getObjectContext().commitChanges();
        }
        return profile;
    }

	public String getCountryList() {
		return countryService.getStringCountryList();
	}
}
