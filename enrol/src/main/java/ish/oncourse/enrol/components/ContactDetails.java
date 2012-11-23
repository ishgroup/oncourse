package ish.oncourse.enrol.components;

import ish.common.types.*;
import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.*;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Deprecated
public class ContactDetails {
    /**
     * Constants
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
    private IConcessionsService concessionsService;
    
    @Inject
    private PreferenceController preferenceController;

    @Property
    @Persist
    private ContactFieldHelper contactFieldHelper;


    /**
     * Parameters
     */
    @Property
    @Parameter(value = "required")
    private Contact contact;

    @SuppressWarnings("all")
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
    private TextField street;

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

    @InjectComponent
    private ConcessionEditor concessionEditor;

    /**
     * template properties
     */
    @SuppressWarnings("all")
	@Property
    private ISHEnumSelectModel englishProficiencySelectModel;

    @SuppressWarnings("all")
    @Property
    private ISHEnumSelectModel indigenousStatusSelectModel;

    @SuppressWarnings("all")
    @Property
    private ISHEnumSelectModel schoolLevelSelectModel;

    @SuppressWarnings("all")
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
    
    /**
     * Inject global parent form to be able return it in a case of clear persisted values after enrollment finished 
     */
    @SuppressWarnings("all")
	@InjectPage
    private EnrolCourses enrolCourses;
    
    /**
     * @see ish.oncourse.enrol.pages.EnrolCourses#handleUnexpectedException(Throwable)
     */
	Object onException(Throwable cause) {
		return enrolCourses.handleUnexpectedException(cause);
	}

    @SetupRender
    void beforeRender() {

        if (contactFieldHelper == null)
                contactFieldHelper = new ContactFieldHelper(preferenceController);
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
        if (!concessionEditor.isConcessionTypeRefreshed()) {
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
                concessionEditor.setConcessionType(null);
                return parentZone.getBody();
            } else {
                ConcessionType studentConcessionType = concessionEditor.getConcessionType();
                if (studentConcessionType != null) {
                    StudentConcession studentConcession = contact.getObjectContext().newObject(StudentConcession.class);
                    studentConcession.setCollege(contact.getStudent().getCollege());
                    ConcessionType concessionType = concessionEditor.getConcessionType();
                    studentConcession.setConcessionType((ConcessionType) contact.getObjectContext()
                            .localObject(concessionType.getObjectId(), concessionType));
                    studentConcession.setConcessionNumber(concessionEditor.getConcessionNumberValue());
                    studentConcession.setExpiresOn(concessionEditor.getExpiryDateValue());
                    studentConcession.setStudent(contact.getStudent());
                    contact.getStudent().setModified(new Date());//this peace of code is just for sure that student will be enqueued on student concession create
                }
                contact.getObjectContext().commitChanges();
                studentService.addStudentToShortlist(contact);
                return "EnrolCourses";
            }
        }
        return parentZone.getBody();
    }

    @OnEvent(component = "addDetailsAction", value = "selected")
    void onSelectedFromAddStudentAction() {
        concessionEditor.setSavePressed(true);
        reset = false;
    }

    @OnEvent(component = "resetDetails", value = "selected")
    void onSelectedFromReset() {
        reset = true;
    }

    @OnEvent(component = "contactDetailsForm", value = "validate")
    void validate() {
    	validateRequiredFields();
        if (!concessionEditor.isConcessionTypeRefreshed()) {
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
                
                if (contactFieldHelper.getRequireDateOfBirth() || contact.getDateOfBirth() != null) {
                	if (birthDateErrorMessage == null) {
                		birthDateErrorMessage = contact.validateBirthDate();
                	}
                	if (birthDateErrorMessage != null) {
                		contactDetailsForm.recordError(birthDate, birthDateErrorMessage);
                	}
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
                concessionEditor.validateConcession();
            }
        }
    }
    
    private void validateRequiredFields() {
    	if (contactFieldHelper.getRequireAddress()) {
    		if (contact.getStreet() == null || "".equals(contact.getStreet())) {
    			contactDetailsForm.recordError(street, "Address is required.");
    		}
    	}
    	if (contactFieldHelper.getRequireSuburb()) {
    		if (contact.getSuburb() == null || "".equals(contact.getSuburb())) {
    			contactDetailsForm.recordError(suburb, "Suburb is required.");
    		}
    	}
    	if (contactFieldHelper.getRequireState()) {
    		if (contact.getState() == null || "".equals(contact.getState())) {
    			contactDetailsForm.recordError(state, "State is required.");
    		}
    	}
    	if (contactFieldHelper.getRequirePostcode()) {
    		if (contact.getPostcode() == null || "".equals(contact.getPostcode())) {
    			contactDetailsForm.recordError(postcode, "Postcode is required.");
    		}
    	}
    	if (contactFieldHelper.getRequireHomePhone()) {
    		if (contact.getHomePhoneNumber() == null || "".equals(contact.getHomePhoneNumber())) {
    			contactDetailsForm.recordError(homePhone, "Home phone is required.");
    		}
    	}
    	if (contactFieldHelper.getRequireBusinessPhone()) {
    		if (contact.getBusinessPhoneNumber() == null || "".equals(contact.getBusinessPhoneNumber())) {
    			contactDetailsForm.recordError(businessPhone, "Business phone is required.");
    		}
    	}
    	if (contactFieldHelper.getRequireFax()) {
    		if (contact.getFaxNumber() == null || "".equals(contact.getFaxNumber())) {
    			contactDetailsForm.recordError(fax, "Fax is required.");
    		}
    	}
    	if (contactFieldHelper.getRequireMobile()) {
    		if (contact.getMobilePhoneNumber() == null || "".equals(contact.getMobilePhoneNumber())) {
    			contactDetailsForm.recordError(mobilePhone, "Mobile phone is required.");
    		}
    	}
    	if (contactFieldHelper.getRequireDateOfBirth()) {
    		if (contact.getDateOfBirth() == null) {
    			contactDetailsForm.recordError(birthDate, "Date of birth is required.");
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

   public boolean isShowConcessionsEditor() {
        return concessionsService.hasActiveConcessionTypes();
    }
   

}
