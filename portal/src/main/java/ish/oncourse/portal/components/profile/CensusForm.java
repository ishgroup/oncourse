package ish.oncourse.portal.components.profile;

import ish.common.types.*;
import ish.oncourse.components.AvetmissStrings;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Student;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.usi.JSONUtils;
import ish.oncourse.portal.usi.Result;
import ish.oncourse.portal.usi.UsiController;
import ish.oncourse.portal.usi.Value;
import ish.oncourse.selectutils.BooleanSelection;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.MessagesNamingConvention;
import ish.oncourse.util.ValidateHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.Date;
import java.util.Map;

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
    private ICayenneService cayenneService;

    @Inject
    private IPortalService portalService;

    @Inject
    private Request request;

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

        if (validateHandler == null)
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
    void afterRender() {
        validateHandler.getErrors().clear();

    }


    public Messages getAvetmissMessages() {
        if (avetmissMessages == null) {
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

        if (validate())
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
        if (languageHomeErrorMessage != null) {
            validateHandler.getErrors().put("languageHome", languageHomeErrorMessage);
            censusForm.recordError(languageHomeErrorMessage);
        }

        schoolYearErrorMessage = validateHandler.error(Student.YEAR_SCHOOL_COMPLETED_PROPERTY);
        if (schoolYearErrorMessage == null && contact.getStudent() != null) {
            schoolYearErrorMessage = contact.getStudent()
                    .validateSchoolYear();
        }
        if (schoolYearErrorMessage != null) {
            validateHandler.getErrors().put("yearSchoolCompleted", schoolYearErrorMessage);
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

    private String getUSIStatus() {
        UsiStatus usiStatus = contact.getStudent().getUsiStatus();
        if (usiStatus == null) {
            usiStatus = UsiStatus.DEFAULT_NOT_SUPPLIED;
        }
        switch (usiStatus) {
            case DEFAULT_NOT_SUPPLIED:
                return "not verified";
            case NON_VERIFIED:
                return "verification failed";
            case VERIFIED:
                return "verified";
            default:
                throw new IllegalArgumentException();
        }
    }

    @OnEvent(value = "usiVerify")
    public Object usiVerify() {
        UsiController usiController = portalService.getUsiController();
        Map<String, Value> inputValues = JSONUtils.getValuesFrom(request);

        Contact contact = usiController.getContact();
        String timeZone = usiController.getContact().getCollege().getTimeZone();
        Date date = usiController.getContact().getDateOfBirth();
        inputValues.put(Contact.DATE_OF_BIRTH_PROPERTY, Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, (date != null ?
                FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, timeZone).format(date) :
                null)));
        inputValues.put(Contact.GIVEN_NAME_PROPERTY, Value.valueOf(Contact.GIVEN_NAME_PROPERTY, contact.getGivenName()));
        inputValues.put(Contact.FAMILY_NAME_PROPERTY, Value.valueOf(Contact.FAMILY_NAME_PROPERTY, contact.getFamilyName()));


        Result result = usiController.next(inputValues);

        JSONObject jsonResult = new JSONObject();
        JSONArray jsonArray = JSONUtils.getJSONValues(usiController.getValue());
        jsonResult.put("values", jsonArray);
        jsonResult.put("hasErrors", result.hasErrors() || usiController.getValidationResult().hasErrors());
        jsonResult.put("step", usiController.getStep().name());
        if (result.hasErrors() || usiController.getValidationResult().hasErrors())
        {
            jsonResult.put("message", usiController.getMessages().format("message-usiVerificationFailed"));
        }

        if (usiController.getStep() == UsiController.Step.wait)
        {
            jsonResult.put("message", usiController.getMessages().format("message-usiVerificationMessage"));
        }
        return new TextStreamResponse("text/json", jsonResult.toString());
    }

    @OnEvent(value = "value")
    public Object usiValue() {
        String usi = contact.getStudent().getUsi();
        UsiStatus usiStatus = contact.getStudent().getUsiStatus();

        JSONObject jsonResult = new JSONObject();


        UsiController usiController = portalService.getUsiController();
        Map<String, Value> values = usiController.getValue();


        JSONArray jsonArray = JSONUtils.getJSONValues(values);
        jsonResult.put("values", jsonArray);
        jsonResult.put("hasErrors", false);
        jsonResult.put("step", usiController.getStep().name());

        return new TextStreamResponse("text/json", jsonResult.toString());
    }
}
