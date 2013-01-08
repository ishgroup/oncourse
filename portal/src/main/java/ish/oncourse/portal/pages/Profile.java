package ish.oncourse.portal.pages;

import ish.common.types.*;
import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
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

public class Profile {

    private static final Logger LOGGER = Logger.getLogger(Profile.class);

	private static final DateFormat FORMAT = new SimpleDateFormat("d/M/y");

	@Inject
	private IAuthenticationService authService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ICountryService countryService;

	@Inject
	private ILanguageService languageService;

    @Inject
    private PreferenceController preferenceController;

    @Property
    @Persist
    private ContactFieldHelper contactFieldHelper;

	@Property
	@Persist
	private Contact contact;
	
    private Timetable timetable;
    
	/**
	 * tapestry services
	 */
	@Inject
	private Messages messages;
	
	private Messages externalMessages;

	/**
	 * components
	 */

	@InjectComponent
	@Property
	private Form profileForm;

	@InjectComponent
	@Property
	private Form passwordForm;

	@Property
	private String password;

	@Property
	private String confirmPassword;

	@InjectComponent("password")
	private PasswordField passwordField;

	@InjectComponent("confirmPassword")
	private PasswordField confirmPasswordField;

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
	private TextField birthDate;

	@InjectComponent
	private TextField countryOfBirth;

	@InjectComponent
	private TextField languageHome;

	@InjectComponent
	private TextField schoolYear;

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
	private String birthDateErrorMessage;

	@Property
	private String countryOfBirthErrorMessage;

	@Property
	private String languageHomeErrorMessage;

	@Property
	private String schoolYearErrorMessage;

	@Property
	private String passwordErrorMessage;

	@Property
	private String confirmPasswordErrorMessage;

	/**
	 * template properties
	 */
	@Property
	private ISHEnumSelectModel englishProficiencySelectModel;

	@Property
	private ISHEnumSelectModel indigenousStatusSelectModel;

	@Property
	private ISHEnumSelectModel schoolLevelSelectModel;

	@Property
	private ISHEnumSelectModel priorEducationSelectModel;

	@Property
	@Parameter
	private boolean requireAdditionalInfo;

	/**
	 * reset form method flag
	 */
	private boolean reset;

	@SetupRender
	void beforeRender() {

        if (contactFieldHelper == null) {
			contactFieldHelper = new ContactFieldHelper(preferenceController, PreferenceController.ContactFiledsSet.enrolment);
		}

		if (contact == null) {
			ObjectContext ctx = cayenneService.newContext();
			this.contact = (Contact) ctx.localObject(authService.getUser()
					.getObjectId(), null);
		}

		englishProficiencySelectModel = new ISHEnumSelectModel(
				AvetmissStudentEnglishProficiency.class, messages);
		indigenousStatusSelectModel = new ISHEnumSelectModel(
				AvetmissStudentIndigenousStatus.class, messages);
		schoolLevelSelectModel = new ISHEnumSelectModel(
				AvetmissStudentSchoolLevel.class, messages);
		priorEducationSelectModel = new ISHEnumSelectModel(
				AvetmissStudentPriorEducation.class, messages);
		externalMessages = MessagesImpl.forClass(AvetmissStrings.class);
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

	public String getPasswordInputClass() {
		return getInputSectionClass(passwordField);
	}

	public String getBirthDateInputClass() {
		return getInputSectionClass(birthDate);
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

	public String getCountryOfBirthInputClass() {
		return getInputSectionClass(countryOfBirth);
	}

	public String getLanguageHomeInputClass() {
		return getInputSectionClass(languageHome);
	}

	public String getSchoolYearInputClass() {
		return getInputSectionClass(schoolYear);
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
					(Country) contact.getObjectContext().localObject(
							country.getObjectId(), country));
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
					(Language) contact.getObjectContext().localObject(
							language.getObjectId(), language));
		}
	}

	public String getSchoolYearStr() {
		Integer yearSchoolCompleted = contact.getStudent()
				.getYearSchoolCompleted();
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
			contact.getStudent().setYearSchoolCompleted(
					Integer.parseInt(schoolYearStr));
		}
	}

	// TODO useless if we display disabilityType list select
	public boolean isHasDisability() {
		AvetmissStudentDisabilityType disabilityType = contact.getStudent()
				.getDisabilityType();
		if (disabilityType == null
				|| disabilityType
						.equals(AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION)
				|| disabilityType.equals(AvetmissStudentDisabilityType.NONE)) {
			return false;
		} else {
			return true;
		}
	}

	// TODO useless if we display disabilityType list select
	public void setHasDisability(boolean hasDisability) {
		if (hasDisability) {
			contact.getStudent().setDisabilityType(
					AvetmissStudentDisabilityType.OTHER);
		} else {
			contact.getStudent().setDisabilityType(
					AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION);
		}
	}

	private String getInputSectionClass(Field field) {
		ValidationTracker defaultTracker = profileForm.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? messages
				.get("validInput") : messages.get("validateInput");
	}

	public boolean isShowAdditionalInfoMessage() {
		return requireAdditionalInfo && profileForm.isValid();
	}

	@OnEvent(component = "profileForm", value = "failure")
	Object submitFailed() {
		return "profile";
	}
	
	@OnEvent(component = "passwordForm", value = "failure")
	Object passwordSubmitFailed() {
		return "profile";
	}

	@OnEvent(component = "saveProfileAction", value = "selected")
	void onSelectedFromSaveStudentAction() {
		reset = false;
	}

    Object onException(Throwable cause) throws Throwable{
        if (contact == null)
        {
            LOGGER.warn("Session expired",cause);
            return this;
        }
        throw cause;
    }
    
    @OnEvent(component = "passwordForm", value = "success")
    Object passwordSubmitted() {
		if (password != null) {
			contact.setPassword(password);
		}
		contact.getObjectContext().commitChanges();
		return timetable;
    }

	@OnEvent(component = "profileForm", value = "success")
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
			contact.setDateOfBirth(null);
			contact.setIsMale(true);
			contact.setIsMarketingViaEmailAllowed(true);
			contact.setIsMarketingViaPostAllowed(true);
			contact.setIsMarketingViaSMSAllowed(true);
			
			if(getIsStudent()){
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
			}
			return this;
		} else {
			if (password != null) {
				contact.setPassword(password);
			}
			contact.getObjectContext().commitChanges();
			requireAdditionalInfo = true;
			return timetable;
		}
	}
	
	@OnEvent(component = "passwordForm", value = "validate")
	void validatePassword() {
		if (password != null && password.length() > 0) {
			passwordErrorMessage = validatedPassword(password, false);
			confirmPasswordErrorMessage = validatedPassword(
					confirmPassword, true);
			passwordForm.recordError(confirmPasswordField,
					confirmPasswordErrorMessage);
			passwordForm.recordError(passwordField, passwordErrorMessage);
		}
	}

	@OnEvent(component = "profileForm", value = "validate")
	void validate() {

		if (reset) {
			profileForm.clearErrors();
		} else {
			emailErrorMessage = contact.validateEmail();
			if (emailErrorMessage != null) {
				profileForm.recordError(email, emailErrorMessage);
			}

			suburbErrorMessage = contact.validateSuburb();
			if (suburbErrorMessage != null) {
				profileForm.recordError(suburb, suburbErrorMessage);
			}
			postcodeErrorMessage = contact.validatePostcode();
			if (postcodeErrorMessage != null) {
				profileForm.recordError(postcode, postcodeErrorMessage);
			}
			stateErrorMessage = contact.validateState();
			if (stateErrorMessage != null) {
				profileForm.recordError(state, stateErrorMessage);
			}

			homePhoneErrorMessage = contact.validateHomePhone();
			if (homePhoneErrorMessage != null) {
				profileForm.recordError(homePhone, homePhoneErrorMessage);
			}
			mobilePhoneErrorMessage = contact.validateMobilePhone();
			if (mobilePhoneErrorMessage != null) {
				profileForm.recordError(mobilePhone, mobilePhoneErrorMessage);
			}

			businessPhoneErrorMessage = contact.validateBusinessPhone();
			if (businessPhoneErrorMessage != null) {
				profileForm.recordError(businessPhone,
						businessPhoneErrorMessage);
			}
			faxErrorMessage = contact.validateFax();
			if (faxErrorMessage != null) {
				profileForm.recordError(fax, faxErrorMessage);
			}

            if (birthDateErrorMessage == null)
		        birthDateErrorMessage = contact.validateBirthDate();
			if (birthDateErrorMessage != null) {
				profileForm.recordError(birthDate, birthDateErrorMessage);
			}

			if (countryOfBirthErrorMessage != null) {
				profileForm.recordError(countryOfBirth,
						countryOfBirthErrorMessage);
			}

			if (languageHomeErrorMessage != null) {
				profileForm.recordError(languageHome, languageHomeErrorMessage);
			}
			if (schoolYearErrorMessage == null && contact.getStudent() != null) {
				schoolYearErrorMessage = contact.getStudent()
						.validateSchoolYear();
			}
			if (schoolYearErrorMessage != null) {
				profileForm.recordError(schoolYear, schoolYearErrorMessage);
			}

		}
	}

	private String validatedPassword(String aValue, boolean isConfirm) {

		String prefix = "The password" + (isConfirm ? " confirm" : "") + " ";
		int minimumPasswordChars = 4;
		if (aValue == null || aValue.length() < minimumPasswordChars) {
			return prefix + "must be at least " + minimumPasswordChars
					+ " characters long.";
		}
		if (aValue.split("\\s").length != 1) {
			return prefix + "cannot contain blank spaces.";
		}
		if (isConfirm && !aValue.equals(password)) {
			return prefix + "does not match the given password.";
		}
		return null;
	}
	
	public boolean getIsStudent(){
		return authService.getUser().getStudent() != null;
	}
	
	public String externalMessageLabel(String fieldName) {
		return externalMessages.get(fieldName);
	}
	
	public boolean getIsRequiresAvetmiss() {
		boolean isRequired = contact.getCollege().getRequiresAvetmiss();
		return isRequired;
	}
	
}
