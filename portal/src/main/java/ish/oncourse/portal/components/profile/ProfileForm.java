package ish.oncourse.portal.components.profile;

import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.portal.pages.Profile;
import ish.oncourse.portal.pages.Timetable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.util.ValidateHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor;

/**
 * User: artem
 * Date: 10/29/13
 * Time: 5:49 PM
 */
public class ProfileForm {

	private static final String KEY_ERROR_MESSAGE_fieldRequired = "message-fieldRequired";

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private PreferenceController preferenceController;

    @Inject
    private ICountryService countryService;

    @Property
    @Persist
    private ContactFieldHelper contactFieldHelper;

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

    /**
     * reset form method flag
     */
    private boolean reset;

    private Timetable timetable;

    @Property
    @Persist
    private ValidateHandler validateHandler;

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

        if (contactFieldHelper == null) {
            contactFieldHelper = new ContactFieldHelper(preferenceController, PreferenceController.ContactFiledsSet.enrolment);
        }

    }

    @AfterRender
    void afteRender(){
        validateHandler.getErrors().clear();

    }


    public boolean visible(String fieldName) {
        return contactFieldHelper.getVisibleFields(contact, false).contains(fieldName);
    }

    public boolean required(String fieldName) {
        return contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.valueOf(fieldName));
    }

	private String getRequiredMessage(PreferenceController.FieldDescriptor fieldDescriptor) {
		return messages.format(KEY_ERROR_MESSAGE_fieldRequired,
				messages.get(String.format(MessagesNamingConvention.LABEL_KEY_TEMPLATE, fieldDescriptor.name())));
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
            contact.setCountry((Country) contact.getObjectContext().localObject(country.getObjectId(), country));
        }
    }


    boolean validate() {
		ConcurrentHashMap<String, String> errors = new ConcurrentHashMap<>(validateHandler.getErrors());

		String emailErrorMessage = contact.validateEmail();
		if (emailErrorMessage != null) {
			errors.putIfAbsent("email", emailErrorMessage);
		}

		if (StringUtils.trimToNull(contact.getSuburb()) == null) {
			if (contactFieldHelper.isRequiredField(FieldDescriptor.suburb)) {
				errors.putIfAbsent(FieldDescriptor.suburb.name(), getRequiredMessage(FieldDescriptor.suburb));
			}
		} else {
			String suburbErrorMessage = contact.validateSuburb();
			
			if (suburbErrorMessage != null) {
				errors.putIfAbsent(FieldDescriptor.suburb.name(), suburbErrorMessage);
			}
		}

		if (StringUtils.trimToNull(contact.getPostcode()) == null) {
			if (contactFieldHelper.isRequiredField(FieldDescriptor.postcode)) {
				errors.putIfAbsent(FieldDescriptor.postcode.name(), getRequiredMessage(FieldDescriptor.postcode));
			}
		} else {
			String postcodeErrorMessage = contact.validatePostcode();
			if (postcodeErrorMessage != null) {
				errors.putIfAbsent(FieldDescriptor.postcode.name(), postcodeErrorMessage);
			}
		}
		
		if (StringUtils.trimToNull(contact.getState()) == null) {
			if (contactFieldHelper.isRequiredField(FieldDescriptor.state)) {
				errors.putIfAbsent(FieldDescriptor.state.name(), getRequiredMessage(FieldDescriptor.state));
			}
		} else {
			String stateErrorMessage = contact.validateState();
			
			if (stateErrorMessage != null) {
				errors.putIfAbsent(FieldDescriptor.state.name(), stateErrorMessage);
			}
		}

		if (StringUtils.trimToNull(contact.getHomePhoneNumber()) == null) {
			if (contactFieldHelper.isRequiredField(FieldDescriptor.homePhoneNumber)) {
				errors.putIfAbsent(FieldDescriptor.homePhoneNumber.name(), getRequiredMessage(FieldDescriptor.homePhoneNumber));
			}
		} else {
			String homePhoneErrorMessage = contact.validateHomePhone();
			
			if (homePhoneErrorMessage != null) {
				errors.putIfAbsent(FieldDescriptor.homePhoneNumber.name(), homePhoneErrorMessage);
			}
		}

		if (StringUtils.trimToNull(contact.getMobilePhoneNumber()) == null) {
			if (contactFieldHelper.isRequiredField(FieldDescriptor.mobilePhoneNumber)) {
				errors.putIfAbsent(FieldDescriptor.mobilePhoneNumber.name(), getRequiredMessage(FieldDescriptor.mobilePhoneNumber));
			}
		} else {
			String mobilePhoneErrorMessage = contact.validateMobilePhone();

			if (mobilePhoneErrorMessage != null) {
				errors.putIfAbsent(FieldDescriptor.mobilePhoneNumber.name(), mobilePhoneErrorMessage);
			}
		}

		if (StringUtils.trimToNull(contact.getBusinessPhoneNumber()) == null) {
			if (contactFieldHelper.isRequiredField(FieldDescriptor.businessPhoneNumber)) {
				errors.putIfAbsent(FieldDescriptor.businessPhoneNumber.name(), getRequiredMessage(FieldDescriptor.businessPhoneNumber));
			}
		} else {
			String businessPhoneErrorMessage = contact.validateBusinessPhone();

			if (businessPhoneErrorMessage != null) {
				errors.putIfAbsent(FieldDescriptor.businessPhoneNumber.name(), businessPhoneErrorMessage);
			}
		}

		if (StringUtils.trimToNull(contact.getFaxNumber()) == null) {
			if (contactFieldHelper.isRequiredField(FieldDescriptor.faxNumber)) {
				errors.putIfAbsent(FieldDescriptor.faxNumber.name(), getRequiredMessage(FieldDescriptor.faxNumber));
			}
		} else {
			String faxErrorMessage = contact.validateFax();

			if (faxErrorMessage != null) {
				errors.putIfAbsent(FieldDescriptor.faxNumber.name(), faxErrorMessage);
			}
		}

		if (contact.getDateOfBirth() == null) {
			if (contactFieldHelper.isRequiredField(FieldDescriptor.dateOfBirth)) {
				errors.putIfAbsent(FieldDescriptor.dateOfBirth.name(), getRequiredMessage(FieldDescriptor.dateOfBirth));
			}
		} else {
			String birthDateErrorMessage = contact.validateBirthDate();
			
			if (birthDateErrorMessage != null) {
				errors.putIfAbsent(FieldDescriptor.dateOfBirth.name(), birthDateErrorMessage);
			}
		}

		if (contact.getCountry() == null) {
			if (contactFieldHelper.isRequiredField(FieldDescriptor.country)) {
				errors.putIfAbsent(FieldDescriptor.country.name(), getRequiredMessage(FieldDescriptor.country));
			}
		}
		
		if (contact.getStudent() != null && StringUtils.trimToNull(contact.getStudent().getSpecialNeeds()) == null) {
			if (contactFieldHelper.isRequiredField(PreferenceController.FieldDescriptor.specialNeeds)) {
				errors.putIfAbsent(FieldDescriptor.specialNeeds.name(), getRequiredMessage(FieldDescriptor.specialNeeds));
			}
		}

		validateHandler.setErrors(errors);
		
		for (Map.Entry<String, String> entry : validateHandler.getErrors().entrySet()) {
			profileForm.recordError(entry.getValue());
		}
		
        return !profileForm.getHasErrors();
    }

    @OnEvent(component = "profileForm")
    Object submitted() {
        if (validate())
        {
            contact.getObjectContext().commitChanges();
        }
        return profile;
    }
}
