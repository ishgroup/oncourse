package ish.oncourse.portal.components.profile;

import ish.common.types.*;
import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Student;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.selectutils.BooleanSelection;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.util.ValidateHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

/**
 * User: artem
 * Date: 10/29/13
 * Time: 5:49 PM
 */
public class CensusForm {

    @InjectComponent
    private TextField countryOfBirth;

    @InjectComponent
    private TextField languageHome;

    @InjectComponent
    private TextField yearSchoolCompleted;

    @InjectComponent
    @Property
    private Form censusForm;


    @Property
    private String countryOfBirthErrorMessage;


    @Property
    private String languageHomeErrorMessage;


    @Property
    private String schoolYearErrorMessage;

    @Inject
    private ILanguageService languageService;

    @Inject
    private ICountryService countryService;


    @Inject
    private IAuthenticationService authService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IPortalService portalService;

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

    @Persist
    @Property
    private ValidateHandler validateHandler;


    /**
     * tapestry services
     */


    private Messages avetmissMessages;

    @Parameter
    @Property
    private Contact contact;


    @SetupRender
    void beforeRender() {

        if(validateHandler==null)
            validateHandler = new ValidateHandler();



        englishProficiencySelectModel = new ISHEnumSelectModel(
                AvetmissStudentEnglishProficiency.class, getAvetmissMessages());
        indigenousStatusSelectModel = new ISHEnumSelectModel(
                AvetmissStudentIndigenousStatus.class, getAvetmissMessages());
        schoolLevelSelectModel = new ISHEnumSelectModel(
                AvetmissStudentSchoolLevel.class, getAvetmissMessages());
        priorEducationSelectModel = new ISHEnumSelectModel(
                AvetmissStudentPriorEducation.class, getAvetmissMessages());
    }

    @AfterRender
    void   afterRender (){
        validateHandler.getErrors().clear();

    }



    public Messages getAvetmissMessages()
    {
        if (avetmissMessages == null)
        {
            avetmissMessages = MessagesImpl.forClass(AvetmissStrings.class);
        }
        return avetmissMessages;
    }


    public String getCountryOfBirthName() {


        Country countryOfBirth = contact.getStudent().getCountryOfBirth();
        if (countryOfBirth == null) {
            return ICountryService.DEFAULT_COUNTRY_NAME;
        }
        return countryOfBirth.getName();
    }

    public void setCountryOfBirthName(String countryOfBirthName) {

        if (StringUtils.trimToNull(countryOfBirthName) == null) {
            return;
        }
        Country country = countryService.getCountryByName(countryOfBirthName);
        if (country == null) {
            validateHandler.getErrors().put(Student.COUNTRY_OF_BIRTH_PROPERTY, messageBy(Student.COUNTRY_OF_BIRTH_PROPERTY));
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
        if (StringUtils.trimToNull(languageHome) == null) {
            return;
        }
        Language language = languageService.getLanguageByName(languageHome);
        if (language == null) {
            validateHandler.getErrors().put(Student.LANGUAGE_HOME_PROPERTY, messageBy(Student.LANGUAGE_HOME_PROPERTY));
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
        if (StringUtils.trimToNull(schoolYearStr) != null) {
            if (!schoolYearStr.matches("(\\d)+")) {
                validateHandler.getErrors().put(Student.YEAR_SCHOOL_COMPLETED_PROPERTY, messageBy(Student.YEAR_SCHOOL_COMPLETED_PROPERTY));
                return;
            }
            contact.getStudent().setYearSchoolCompleted(
                    Integer.parseInt(schoolYearStr));
        }
    }

    public ISHEnumSelectModel getDisabilityTypeSelectModel() {
        return new ISHEnumSelectModel(AvetmissStudentDisabilityType.class, getAvetmissMessages());
    }

    public ISHEnumSelectModel getLabourForceStatusSelectModel() {
        return new ISHEnumSelectModel(AvetmissStudentLabourStatus.class, getAvetmissMessages());
    }

    public ISHEnumSelectModel getStillAtSchoolSelectModel() {
        return new ISHEnumSelectModel(BooleanSelection.class, getAvetmissMessages());
    }

    public BooleanSelection getStillAtSchoolSelection() {
        return BooleanSelection.valueOf(contact.getStudent().getIsStillAtSchool());
    }

    public void setStillAtSchoolSelection(BooleanSelection selection) {
        contact.getStudent().setIsStillAtSchool(selection.getValue());
    }

    @OnEvent(component = "censusForm")
    Object submitted() {

        if(validate())
        contact.getObjectContext().commitChanges();

        return this;
    }


    boolean validate() {

            countryOfBirthErrorMessage = validateHandler.error(Student.COUNTRY_OF_BIRTH_PROPERTY);

            if (countryOfBirthErrorMessage != null) {
                validateHandler.getErrors().put("countryOfBirth", countryOfBirthErrorMessage);
                censusForm.recordError(countryOfBirthErrorMessage);
            }

            languageHomeErrorMessage = validateHandler.error(Student.LANGUAGE_HOME_PROPERTY);
            if(languageHomeErrorMessage != null) {
                validateHandler.getErrors().put("languageHome",languageHomeErrorMessage);
                censusForm.recordError(languageHomeErrorMessage);
            }

            schoolYearErrorMessage = validateHandler.error(Student.YEAR_SCHOOL_COMPLETED_PROPERTY);
            if (schoolYearErrorMessage == null && contact.getStudent() != null) {
                schoolYearErrorMessage = contact.getStudent()
                        .validateSchoolYear();
            }
            if (schoolYearErrorMessage != null) {
                validateHandler.getErrors().put( "yearSchoolCompleted",schoolYearErrorMessage);
                censusForm.recordError(schoolYearErrorMessage);
            }

            return !censusForm.getHasErrors();
    }

    public boolean getIsStudent() {
        return portalService.getContact().getStudent() != null;
    }

    public boolean getIsRequiresAvetmiss() {
        boolean isRequired = contact.getCollege().getRequiresAvetmiss();
        return isRequired;
    }

    public String messageBy(String fieldName) {
        return getAvetmissMessages().get(String.format(MessagesNamingConvention.MESSAGE_KEY_TEMPLATE, fieldName));
    }
}
