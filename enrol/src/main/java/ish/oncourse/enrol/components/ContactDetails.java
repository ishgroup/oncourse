package ish.oncourse.enrol.components;

import ish.common.types.AvetmissStudentDisabilityType;
import ish.common.types.AvetmissStudentEnglishProficiency;
import ish.common.types.AvetmissStudentIndigenousStatus;
import ish.common.types.AvetmissStudentPriorEducation;
import ish.common.types.AvetmissStudentSchoolLevel;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.services.preference.IPreferenceService;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ContactDetails {
    /**
     * Contants
     */
    private static final DateFormat FORMAT = new SimpleDateFormat("d/M/y");

    /**
     * tapestry services
     */
    @Inject
    private Messages messages;

    /**
     * ish services
     */
    @Inject
    private IStudentService studentService;

    @Inject
    private ICountryService countryService;

    @Inject
    private ILanguageService languageService;

    @Inject
    private IPreferenceService preferenceService;


    /**
     * Parameters
     */
    @Property
    @Parameter(value = "required")
    private Contact contact;

    @Property
    @Parameter
    private boolean showEmail;

    @Property
    @Parameter
    private boolean requireAdditionalInfo;

    @Parameter
    private Zone parentZone;

    /**
     * components
     */
    @InjectComponent
    @Property
    private Form contactDetailsForm;

    @InjectComponent
    private TextField email;

    @InjectComponent
    private TextField suburb;

    @InjectComponent
    private TextField postcode;

    @InjectComponent
    private TextField state;

    @InjectComponent
    private TextField homePhone;

    @InjectComponent
    private TextField mobilePhone;

    @InjectComponent
    private TextField businessPhone;

    @InjectComponent
    private TextField fax;

    @InjectComponent
    private PasswordField password;

    @InjectComponent
    private PasswordField passwordConfirm;

    @InjectComponent
    private TextField birthDate;

    @InjectComponent
    private TextField countryOfBirth;

    @InjectComponent
    private TextField languageHome;

    @InjectComponent
    private TextField schoolYear;

    /**
     * template properties
     */
    @Property
    private String passwordConfirmProperty;

    @Property
    private ISHEnumSelectModel englishProficiencySelectModel;

    @Property
    private ISHEnumSelectModel indigenousStatusSelectModel;

    @Property
    private ISHEnumSelectModel schoolLevelSelectModel;

    @Property
    private ISHEnumSelectModel priorEducationSelectModel;

    // TODO uncomment for disability type list
    // @Property
    // private ISHEnumSelectModel disabilityTypeSelectModel;

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
    private String homePhoneErrorMessage;

    @Property
    private String mobilePhoneErrorMessage;

    @Property
    private String businessPhoneErrorMessage;

    @Property
    private String faxErrorMessage;

    @Property
    private String passwordErrorMessage;

    @Property
    private String passwordConfirmErrorMessage;

    @Property
    private String birthDateErrorMessage;

    @Property
    private String countryOfBirthErrorMessage;

    @Property
    private String languageHomeErrorMessage;

    @Property
    private String schoolYearErrorMessage;

    /**
     * reset form method flag
     */
    private boolean reset;

    @SetupRender
    void beforeRender() {
        englishProficiencySelectModel = new ISHEnumSelectModel(
                AvetmissStudentEnglishProficiency.class, messages);
        indigenousStatusSelectModel = new ISHEnumSelectModel(AvetmissStudentIndigenousStatus.class,
                messages);
        schoolLevelSelectModel = new ISHEnumSelectModel(AvetmissStudentSchoolLevel.class, messages);
        priorEducationSelectModel = new ISHEnumSelectModel(AvetmissStudentPriorEducation.class,
                messages);
        // TODO uncomment for disability type list
        // disabilityTypeSelectModel=new
        // ISHEnumSelectModel(AvetmissStudentDisabilityType.class, messages);
    }

    @OnEvent(component = "contactDetailsForm", value = "failure")
    Block submitFailed() {
        return parentZone.getBody();
    }

    @OnEvent(component = "contactDetailsForm", value = "success")
    Object submitted() {
        if (reset) {
            contact.setStreet(null);
            contact.setSuburb(null);
            contact.setPostcode(null);
            contact.setState(null);
            contact.setHomePhoneNumber(null);
            contact.setMobilePhoneNumber(null);
            contact.setBusinessPhoneNumber(null);
            contact.setFaxNumber(null);
            contact.setPassword(null);
            contact.setDateOfBirth(null);
            contact.setIsMale(true);
            contact.setIsMarketingViaEmailAllowed(true);
            contact.setIsMarketingViaPostAllowed(true);
            contact.setIsMarketingViaSMSAllowed(true);
            contact.getStudent().setCountryOfBirth(null);
            contact.getStudent().setLanguageHome(null);
            contact.getStudent().setEnglishProficiency(
                    AvetmissStudentEnglishProficiency.DEFAULT_POPUP_OPTION);
            contact.getStudent().setIndigenousStatus(
                    AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION);
            contact.getStudent().setHighestSchoolLevel(
                    AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION);
            contact.getStudent().setYearSchoolCompleted(null);
            contact.getStudent().setPriorEducationCode(
                    AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION);
            contact.getStudent().setDisabilityType(
                    AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION);
            return parentZone.getBody();
        } else {
            contact.getObjectContext().commitChanges();
            studentService.addStudentToShortlist(contact);
            return "EnrolCourses";
        }
    }

    @OnEvent(component = "addDetailsAction", value = "selected")
    void onSelectedFromAddStudentAction() {
        reset = false;
    }

    @OnEvent(component = "resetDetails", value = "selected")
    void onSelectedFromReset() {
        reset = true;
    }

    @OnEvent(component = "contactDetailsForm", value = "validate")
    void validate() {
        if (reset) {
            contactDetailsForm.clearErrors();
        } else {
            emailErrorMessage = contact.validateEmail();
            if (emailErrorMessage != null) {
                contactDetailsForm.recordError(email, emailErrorMessage);
            }
            suburbErrorMessage = contact.validateSuburb();
            if (suburbErrorMessage != null) {
                contactDetailsForm.recordError(suburb, suburbErrorMessage);
            }
            postcodeErrorMessage = contact.validatePostcode();
            if (postcodeErrorMessage != null) {
                contactDetailsForm.recordError(postcode, postcodeErrorMessage);
            }
            stateErrorMessage = contact.validateState();
            if (stateErrorMessage != null) {
                contactDetailsForm.recordError(state, stateErrorMessage);
            }
            homePhoneErrorMessage = contact.validateHomePhone();
            if (homePhoneErrorMessage != null) {
                contactDetailsForm.recordError(homePhone, homePhoneErrorMessage);
            }
            mobilePhoneErrorMessage = contact.validateMobilePhone();
            if (mobilePhoneErrorMessage != null) {
                contactDetailsForm.recordError(mobilePhone, mobilePhoneErrorMessage);
            }
            businessPhoneErrorMessage = contact.validateBusinessPhone();
            if (businessPhoneErrorMessage != null) {
                contactDetailsForm.recordError(businessPhone, businessPhoneErrorMessage);
            }
            faxErrorMessage = contact.validateFax();
            if (faxErrorMessage != null) {
                contactDetailsForm.recordError(fax, faxErrorMessage);
            }
            passwordErrorMessage = contact.validatePassword();
            if (passwordErrorMessage != null) {
                contactDetailsForm.recordError(password, passwordErrorMessage);
            }
            passwordConfirmErrorMessage = contact.validatePasswordConfirm(passwordConfirmProperty);
            if (passwordConfirmErrorMessage != null) {
                contactDetailsForm.recordError(passwordConfirm, passwordConfirmErrorMessage);
            }
            if (birthDateErrorMessage == null) {
                birthDateErrorMessage = contact.validateBirthDate();
            }
            if (birthDateErrorMessage != null) {
                contactDetailsForm.recordError(birthDate, birthDateErrorMessage);
            }

            if (countryOfBirthErrorMessage != null) {
                contactDetailsForm.recordError(countryOfBirth, countryOfBirthErrorMessage);
            }

            if (languageHomeErrorMessage != null) {
                contactDetailsForm.recordError(languageHome, languageHomeErrorMessage);
            }
            if (schoolYearErrorMessage == null) {
                schoolYearErrorMessage = contact.getStudent().validateSchoolYear();
            }
            if (schoolYearErrorMessage != null) {
                contactDetailsForm.recordError(schoolYear, schoolYearErrorMessage);
            }
        }
    }

    public String getEmailInputClass() {
        return getInputSectionClass(email);
    }

    public String getSuburbInputClass() {
        return getInputSectionClass(suburb);
    }

    public String getPostcodeInputClass() {
        return getInputSectionClass(postcode);
    }

    public String getStateInputClass() {
        return getInputSectionClass(state);
    }

    public String getHomePhoneInputClass() {
        return getInputSectionClass(homePhone);
    }

    public String getMobilePhoneInputClass() {
        return getInputSectionClass(mobilePhone);
    }

    public String getBusinessPhoneInputClass() {
        return getInputSectionClass(businessPhone);
    }

    public String getFaxInputClass() {
        return getInputSectionClass(fax);
    }

    public String getPasswordInputClass() {
        return getInputSectionClass(password);
    }

    public String getPasswordConfirmInputClass() {
        return getInputSectionClass(passwordConfirm);
    }

    public String getBirthDateInputClass() {
        return getInputSectionClass(birthDate);
    }

    public String getCountryOfBirthInputClass() {
        return getInputSectionClass(countryOfBirth);
    }

    public String getLanguageHomeInputClass() {
        return getInputSectionClass(languageHome);
    }

    public String getSchoolYearInputClass() {
        return getInputSectionClass(schoolYear);
    }

    private String getInputSectionClass(Field field) {
        ValidationTracker defaultTracker = contactDetailsForm.getDefaultTracker();
        return defaultTracker == null || !defaultTracker.inError(field) ? messages
                .get("validInput") : messages.get("validateInput");
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
            if (birthDateProperty != null && !"".equals(birthDateProperty)) {
                Date parsedDate = FORMAT.parse(birthDateProperty);
                contact.setDateOfBirth(parsedDate);
            }
        } catch (ParseException e) {
            birthDateErrorMessage = "Please enter a valid date of birth and formatted as indicated: in the form 25/12/2000";
        }
    }

    public boolean isShowAdditionalInfoMessage() {
        return requireAdditionalInfo && contactDetailsForm.isValid();
    }

    public boolean isNewStudent() {
        return contact.getStudent().getPersistenceState() == PersistenceState.NEW;
    }

    public String getCountryOfBirthName() {
        Country countryOfBirth = contact.getStudent().getCountryOfBirth();
        if (countryOfBirth == null) {
            return null;
        }
        return countryOfBirth.getName();
    }

    public void setCountryOfBirthName(String countryOfBirthName) {
        if (countryOfBirthName == null || "".equals(countryOfBirthName)) {
            return;
        }
        Country country = countryService.getCountryByName(countryOfBirthName);
        if (country == null) {
            countryOfBirthErrorMessage = "Country name is incorrect";
        } else {
            contact.getStudent().setCountryOfBirth(
                    (Country) contact.getObjectContext()
                            .localObject(country.getObjectId(), country));
        }
    }

    public String getLanguageHomeName() {
        Language languageHome = contact.getStudent().getLanguageHome();
        if (languageHome == null) {
            return null;
        }
        return languageHome.getName();
    }

    public void setLanguageHomeName(String languageHome) {
        if (languageHome == null || "".equals(languageHome)) {
            return;
        }
        Language language = languageService.getLanguageByName(languageHome);
        if (language == null) {
            languageHomeErrorMessage = "Language name is incorrect";
        } else {
            contact.getStudent().setLanguageHome(
                    (Language) contact.getObjectContext().localObject(language.getObjectId(),
                            language));
        }
    }

    public String getSchoolYearStr() {
        Integer yearSchoolCompleted = contact.getStudent().getYearSchoolCompleted();
        if (yearSchoolCompleted == null) {
            return null;
        }
        return Integer.toString(yearSchoolCompleted);
    }

    public void setSchoolYearStr(String schoolYearStr) {
        if (!(schoolYearStr == null) && !"".equals(schoolYearStr)) {
            if (!schoolYearStr.matches("(\\d)+")) {
                schoolYearErrorMessage = "Incorrect format of the year.";
                return;
            }
            contact.getStudent().setYearSchoolCompleted(Integer.parseInt(schoolYearStr));
        }
    }

    // TODO useless if we display disabilityType list select
    public boolean isHasDisability() {
        AvetmissStudentDisabilityType disabilityType = contact.getStudent().getDisabilityType();
        if (disabilityType == null
                || disabilityType.equals(AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION)
                || disabilityType.equals(AvetmissStudentDisabilityType.NONE)) {
            return false;
        } else {
            return true;
        }
    }

    // TODO useless if we display disabilityType list select
    public void setHasDisability(boolean hasDisability) {
        if (hasDisability) {
            contact.getStudent().setDisabilityType(AvetmissStudentDisabilityType.OTHER);
        } else {
            contact.getStudent().setDisabilityType(
                    AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION);
        }
    }

    public boolean isCollegeHasConcessions() {
        return !contact.getCollege().getActiveConcessionTypes().isEmpty();
    }

    public boolean isShowConcessionsEditor() {
        return Boolean.valueOf(preferenceService.getPreferenceByKey(
                "feature.concessionsInEnrolment").getValueString()) || Boolean.valueOf(preferenceService.getPreferenceByKey(
                "feature.concession.existing.users.create").getValueString());
    }
}
