package ish.oncourse.portal.components.profile;

import ish.common.types.*;
import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Student;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.pages.Timetable;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.util.ValidateHandler;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: artem
 * Date: 10/29/13
 * Time: 5:49 PM
 */
public class ProfileForm {


    @Inject
    private IAuthenticationService authService;

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

    @Inject
    private Messages messages;

    private Messages avetmissMessages;
    /**
     * error message template properties
     */

    @Property
    private String emailErrorMessage;

    @Property
    private String suburbErrorMessage;

    @Property
    private String postcodeErrorMessage;

    @Property
    private String stateErrorMessage;

    @Property
    private String countryErrorMessage;

    @Property
    private String homePhoneErrorMessage;

    @Property
    private String mobilePhoneErrorMessage;

    @Property
    private String businessPhoneErrorMessage;

    @Property
    private String faxErrorMessage;

    @Property
    private String birthDateErrorMessage;

    /**
     *page property
     *
     */
    @InjectComponent
    private TextField email;

    @InjectComponent
    private TextField suburb;

    @InjectComponent
    private TextField postcode;

    @InjectComponent
    private TextField state;

    @InjectComponent
    private TextField country;

    @InjectComponent
    private TextField homePhone;

    @InjectComponent
    private TextField mobilePhone;

    @InjectComponent
    private TextField businessPhone;

    @InjectComponent
    private TextField fax;

    @InjectComponent
    private TextField birthDate;

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

    private static final DateFormat FORMAT = new SimpleDateFormat("d/M/y");


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
        return FORMAT.format(dateOfBirth);
    }

    public void setBirthDateProperty(String birthDateProperty) {
        try {
            if (StringUtils.trimToNull(birthDateProperty) != null) {
                Date parsedDate = FORMAT.parse(birthDateProperty);
                contact.setDateOfBirth(parsedDate);
            }
        } catch (ParseException e) {
            birthDateErrorMessage = messages.get("message-birthDateWrongFormat");
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
        //cleanup error if exist
        if (validateHandler.error(Contact.COUNTRY_PROPERTY) != null) {
            validateHandler.getErrors().remove(Contact.COUNTRY_PROPERTY);
        }
        if (StringUtils.trimToNull(value) == null) {
            if (required(Contact.COUNTRY_PROPERTY)) {
                validateHandler.getErrors().put(Contact.COUNTRY_PROPERTY, messageBy(Contact.COUNTRY_PROPERTY));
            }
            return;
        }
        Country country = countryService.getCountryByName(value);
        if (country == null) {
            validateHandler.getErrors().put(Contact.COUNTRY_PROPERTY, messageBy(Contact.COUNTRY_PROPERTY));
        } else {
            contact.setCountry((Country) contact.getObjectContext().localObject(country.getObjectId(), country));
        }
    }


    boolean validate() {



            emailErrorMessage = contact.validateEmail();
            if (emailErrorMessage != null) {
                validateHandler.getErrors().put("email",emailErrorMessage);
                profileForm.recordError(emailErrorMessage);
            }

            suburbErrorMessage = contact.validateSuburb();
            if (suburbErrorMessage != null) {
                validateHandler.getErrors().put("suburb",suburbErrorMessage);
                profileForm.recordError(suburbErrorMessage);
            }

            postcodeErrorMessage = contact.validatePostcode();
            if (postcodeErrorMessage != null) {
                validateHandler.getErrors().put("postcode",postcodeErrorMessage);
                profileForm.recordError(postcodeErrorMessage);
            }
            stateErrorMessage = contact.validateState();
            if (stateErrorMessage != null) {
                validateHandler.getErrors().put("state",stateErrorMessage);
                profileForm.recordError(stateErrorMessage);
            }

            homePhoneErrorMessage = contact.validateHomePhone();
            if (homePhoneErrorMessage != null) {
                validateHandler.getErrors().put("homePhone",homePhoneErrorMessage);
                profileForm.recordError(homePhoneErrorMessage);
            }
            mobilePhoneErrorMessage = contact.validateMobilePhone();
            if (mobilePhoneErrorMessage != null) {
                validateHandler.getErrors().put("mobilePhone",mobilePhoneErrorMessage);
                profileForm.recordError(mobilePhoneErrorMessage);
            }

            businessPhoneErrorMessage = contact.validateBusinessPhone();
            if (businessPhoneErrorMessage != null) {
                validateHandler.getErrors().put("businessPhone",businessPhoneErrorMessage);
                profileForm.recordError(businessPhoneErrorMessage);
            }

            faxErrorMessage = contact.validateFax();
            if (faxErrorMessage != null) {
                validateHandler.getErrors().put("fax",faxErrorMessage);
                profileForm.recordError(faxErrorMessage);
            }

            if (birthDateErrorMessage == null)
                birthDateErrorMessage = contact.validateBirthDate();
            if (birthDateErrorMessage != null) {
                validateHandler.getErrors().put("birthDate",birthDateErrorMessage);
                profileForm.recordError(birthDateErrorMessage);
            }

            countryErrorMessage = validateHandler.error(Contact.COUNTRY_PROPERTY);
            if (countryErrorMessage != null) {
                validateHandler.getErrors().put("country",countryErrorMessage);
                profileForm.recordError(countryErrorMessage);
            }

        return !profileForm.getHasErrors();

    }

    @OnEvent(component = "profileForm")
    Object submitted() {
        if (validate())
        {
            contact.getObjectContext().commitChanges();
        }
        return this;
    }


}
