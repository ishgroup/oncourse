package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.common.types.AvetmissStudentDisabilityType;
import ish.common.types.AvetmissStudentEnglishProficiency;
import ish.common.types.AvetmissStudentIndigenousStatus;
import ish.common.types.AvetmissStudentPriorEducation;
import ish.common.types.AvetmissStudentSchoolLevel;
import ish.common.types.StudentCitizenship;
import ish.common.types.UsiStatus;
import ish.oncourse.model.Application;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.Certificate;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Language;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.WaitingList;

/**
 * Class _Student was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Student extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String CHESSN_PROPERTY = "chessn";
    @Deprecated
    public static final String CITIZENSHIP_PROPERTY = "citizenship";
    @Deprecated
    public static final String CONCESSION_TYPE_PROPERTY = "concessionType";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String DISABILITY_TYPE_PROPERTY = "disabilityType";
    @Deprecated
    public static final String ENGLISH_PROFICIENCY_PROPERTY = "englishProficiency";
    @Deprecated
    public static final String FEE_HELP_ELIGIBLE_PROPERTY = "feeHelpEligible";
    @Deprecated
    public static final String HIGHEST_SCHOOL_LEVEL_PROPERTY = "highestSchoolLevel";
    @Deprecated
    public static final String INDIGENOUS_STATUS_PROPERTY = "indigenousStatus";
    @Deprecated
    public static final String IS_OVERSEAS_CLIENT_PROPERTY = "isOverseasClient";
    @Deprecated
    public static final String IS_STILL_AT_SCHOOL_PROPERTY = "isStillAtSchool";
    @Deprecated
    public static final String LABOUR_FORCE_TYPE_PROPERTY = "labourForceType";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String PRIOR_EDUCATION_CODE_PROPERTY = "priorEducationCode";
    @Deprecated
    public static final String SPECIAL_NEEDS_PROPERTY = "specialNeeds";
    @Deprecated
    public static final String SPECIAL_NEEDS_ASSISTANCE_PROPERTY = "specialNeedsAssistance";
    @Deprecated
    public static final String TOWN_OF_BIRTH_PROPERTY = "townOfBirth";
    @Deprecated
    public static final String USI_PROPERTY = "usi";
    @Deprecated
    public static final String USI_STATUS_PROPERTY = "usiStatus";
    @Deprecated
    public static final String YEAR_SCHOOL_COMPLETED_PROPERTY = "yearSchoolCompleted";
    @Deprecated
    public static final String APPLICATIONS_PROPERTY = "applications";
    @Deprecated
    public static final String ATTENDANCES_PROPERTY = "attendances";
    @Deprecated
    public static final String CERTIFICATES_PROPERTY = "certificates";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String CONTACT_PROPERTY = "contact";
    @Deprecated
    public static final String COUNTRY_OF_BIRTH_PROPERTY = "countryOfBirth";
    @Deprecated
    public static final String ENROLMENTS_PROPERTY = "enrolments";
    @Deprecated
    public static final String LANGUAGE_PROPERTY = "language";
    @Deprecated
    public static final String LANGUAGE_HOME_PROPERTY = "languageHome";
    @Deprecated
    public static final String MESSAGE_PEOPLE_PROPERTY = "messagePeople";
    @Deprecated
    public static final String PAYMENTS_IN_PROPERTY = "paymentsIn";
    @Deprecated
    public static final String STUDENT_CONCESSIONS_PROPERTY = "studentConcessions";
    @Deprecated
    public static final String WAITING_LISTS_PROPERTY = "waitingLists";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<String> CHESSN = new Property<String>("chessn");
    public static final Property<StudentCitizenship> CITIZENSHIP = new Property<StudentCitizenship>("citizenship");
    public static final Property<Integer> CONCESSION_TYPE = new Property<Integer>("concessionType");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<AvetmissStudentDisabilityType> DISABILITY_TYPE = new Property<AvetmissStudentDisabilityType>("disabilityType");
    public static final Property<AvetmissStudentEnglishProficiency> ENGLISH_PROFICIENCY = new Property<AvetmissStudentEnglishProficiency>("englishProficiency");
    public static final Property<Boolean> FEE_HELP_ELIGIBLE = new Property<Boolean>("feeHelpEligible");
    public static final Property<AvetmissStudentSchoolLevel> HIGHEST_SCHOOL_LEVEL = new Property<AvetmissStudentSchoolLevel>("highestSchoolLevel");
    public static final Property<AvetmissStudentIndigenousStatus> INDIGENOUS_STATUS = new Property<AvetmissStudentIndigenousStatus>("indigenousStatus");
    public static final Property<Boolean> IS_OVERSEAS_CLIENT = new Property<Boolean>("isOverseasClient");
    public static final Property<Boolean> IS_STILL_AT_SCHOOL = new Property<Boolean>("isStillAtSchool");
    public static final Property<Integer> LABOUR_FORCE_TYPE = new Property<Integer>("labourForceType");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<AvetmissStudentPriorEducation> PRIOR_EDUCATION_CODE = new Property<AvetmissStudentPriorEducation>("priorEducationCode");
    public static final Property<String> SPECIAL_NEEDS = new Property<String>("specialNeeds");
    public static final Property<Boolean> SPECIAL_NEEDS_ASSISTANCE = new Property<Boolean>("specialNeedsAssistance");
    public static final Property<String> TOWN_OF_BIRTH = new Property<String>("townOfBirth");
    public static final Property<String> USI = new Property<String>("usi");
    public static final Property<UsiStatus> USI_STATUS = new Property<UsiStatus>("usiStatus");
    public static final Property<Integer> YEAR_SCHOOL_COMPLETED = new Property<Integer>("yearSchoolCompleted");
    public static final Property<List<Application>> APPLICATIONS = new Property<List<Application>>("applications");
    public static final Property<List<Attendance>> ATTENDANCES = new Property<List<Attendance>>("attendances");
    public static final Property<List<Certificate>> CERTIFICATES = new Property<List<Certificate>>("certificates");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Contact> CONTACT = new Property<Contact>("contact");
    public static final Property<Country> COUNTRY_OF_BIRTH = new Property<Country>("countryOfBirth");
    public static final Property<List<Enrolment>> ENROLMENTS = new Property<List<Enrolment>>("enrolments");
    public static final Property<Language> LANGUAGE = new Property<Language>("language");
    public static final Property<Language> LANGUAGE_HOME = new Property<Language>("languageHome");
    public static final Property<List<MessagePerson>> MESSAGE_PEOPLE = new Property<List<MessagePerson>>("messagePeople");
    public static final Property<List<PaymentIn>> PAYMENTS_IN = new Property<List<PaymentIn>>("paymentsIn");
    public static final Property<List<StudentConcession>> STUDENT_CONCESSIONS = new Property<List<StudentConcession>>("studentConcessions");
    public static final Property<List<WaitingList>> WAITING_LISTS = new Property<List<WaitingList>>("waitingLists");

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setChessn(String chessn) {
        writeProperty("chessn", chessn);
    }
    public String getChessn() {
        return (String)readProperty("chessn");
    }

    public void setCitizenship(StudentCitizenship citizenship) {
        writeProperty("citizenship", citizenship);
    }
    public StudentCitizenship getCitizenship() {
        return (StudentCitizenship)readProperty("citizenship");
    }

    public void setConcessionType(Integer concessionType) {
        writeProperty("concessionType", concessionType);
    }
    public Integer getConcessionType() {
        return (Integer)readProperty("concessionType");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDisabilityType(AvetmissStudentDisabilityType disabilityType) {
        writeProperty("disabilityType", disabilityType);
    }
    public AvetmissStudentDisabilityType getDisabilityType() {
        return (AvetmissStudentDisabilityType)readProperty("disabilityType");
    }

    public void setEnglishProficiency(AvetmissStudentEnglishProficiency englishProficiency) {
        writeProperty("englishProficiency", englishProficiency);
    }
    public AvetmissStudentEnglishProficiency getEnglishProficiency() {
        return (AvetmissStudentEnglishProficiency)readProperty("englishProficiency");
    }

    public void setFeeHelpEligible(Boolean feeHelpEligible) {
        writeProperty("feeHelpEligible", feeHelpEligible);
    }
    public Boolean getFeeHelpEligible() {
        return (Boolean)readProperty("feeHelpEligible");
    }

    public void setHighestSchoolLevel(AvetmissStudentSchoolLevel highestSchoolLevel) {
        writeProperty("highestSchoolLevel", highestSchoolLevel);
    }
    public AvetmissStudentSchoolLevel getHighestSchoolLevel() {
        return (AvetmissStudentSchoolLevel)readProperty("highestSchoolLevel");
    }

    public void setIndigenousStatus(AvetmissStudentIndigenousStatus indigenousStatus) {
        writeProperty("indigenousStatus", indigenousStatus);
    }
    public AvetmissStudentIndigenousStatus getIndigenousStatus() {
        return (AvetmissStudentIndigenousStatus)readProperty("indigenousStatus");
    }

    public void setIsOverseasClient(Boolean isOverseasClient) {
        writeProperty("isOverseasClient", isOverseasClient);
    }
    public Boolean getIsOverseasClient() {
        return (Boolean)readProperty("isOverseasClient");
    }

    public void setIsStillAtSchool(Boolean isStillAtSchool) {
        writeProperty("isStillAtSchool", isStillAtSchool);
    }
    public Boolean getIsStillAtSchool() {
        return (Boolean)readProperty("isStillAtSchool");
    }

    public void setLabourForceType(Integer labourForceType) {
        writeProperty("labourForceType", labourForceType);
    }
    public Integer getLabourForceType() {
        return (Integer)readProperty("labourForceType");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setPriorEducationCode(AvetmissStudentPriorEducation priorEducationCode) {
        writeProperty("priorEducationCode", priorEducationCode);
    }
    public AvetmissStudentPriorEducation getPriorEducationCode() {
        return (AvetmissStudentPriorEducation)readProperty("priorEducationCode");
    }

    public void setSpecialNeeds(String specialNeeds) {
        writeProperty("specialNeeds", specialNeeds);
    }
    public String getSpecialNeeds() {
        return (String)readProperty("specialNeeds");
    }

    public void setSpecialNeedsAssistance(Boolean specialNeedsAssistance) {
        writeProperty("specialNeedsAssistance", specialNeedsAssistance);
    }
    public Boolean getSpecialNeedsAssistance() {
        return (Boolean)readProperty("specialNeedsAssistance");
    }

    public void setTownOfBirth(String townOfBirth) {
        writeProperty("townOfBirth", townOfBirth);
    }
    public String getTownOfBirth() {
        return (String)readProperty("townOfBirth");
    }

    public void setUsi(String usi) {
        writeProperty("usi", usi);
    }
    public String getUsi() {
        return (String)readProperty("usi");
    }

    public void setUsiStatus(UsiStatus usiStatus) {
        writeProperty("usiStatus", usiStatus);
    }
    public UsiStatus getUsiStatus() {
        return (UsiStatus)readProperty("usiStatus");
    }

    public void setYearSchoolCompleted(Integer yearSchoolCompleted) {
        writeProperty("yearSchoolCompleted", yearSchoolCompleted);
    }
    public Integer getYearSchoolCompleted() {
        return (Integer)readProperty("yearSchoolCompleted");
    }

    public void addToApplications(Application obj) {
        addToManyTarget("applications", obj, true);
    }
    public void removeFromApplications(Application obj) {
        removeToManyTarget("applications", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Application> getApplications() {
        return (List<Application>)readProperty("applications");
    }


    public void addToAttendances(Attendance obj) {
        addToManyTarget("attendances", obj, true);
    }
    public void removeFromAttendances(Attendance obj) {
        removeToManyTarget("attendances", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Attendance> getAttendances() {
        return (List<Attendance>)readProperty("attendances");
    }


    public void addToCertificates(Certificate obj) {
        addToManyTarget("certificates", obj, true);
    }
    public void removeFromCertificates(Certificate obj) {
        removeToManyTarget("certificates", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Certificate> getCertificates() {
        return (List<Certificate>)readProperty("certificates");
    }


    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setContact(Contact contact) {
        setToOneTarget("contact", contact, true);
    }

    public Contact getContact() {
        return (Contact)readProperty("contact");
    }


    public void setCountryOfBirth(Country countryOfBirth) {
        setToOneTarget("countryOfBirth", countryOfBirth, true);
    }

    public Country getCountryOfBirth() {
        return (Country)readProperty("countryOfBirth");
    }


    public void addToEnrolments(Enrolment obj) {
        addToManyTarget("enrolments", obj, true);
    }
    public void removeFromEnrolments(Enrolment obj) {
        removeToManyTarget("enrolments", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Enrolment> getEnrolments() {
        return (List<Enrolment>)readProperty("enrolments");
    }


    public void setLanguage(Language language) {
        setToOneTarget("language", language, true);
    }

    public Language getLanguage() {
        return (Language)readProperty("language");
    }


    public void setLanguageHome(Language languageHome) {
        setToOneTarget("languageHome", languageHome, true);
    }

    public Language getLanguageHome() {
        return (Language)readProperty("languageHome");
    }


    public void addToMessagePeople(MessagePerson obj) {
        addToManyTarget("messagePeople", obj, true);
    }
    public void removeFromMessagePeople(MessagePerson obj) {
        removeToManyTarget("messagePeople", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<MessagePerson> getMessagePeople() {
        return (List<MessagePerson>)readProperty("messagePeople");
    }


    public void addToPaymentsIn(PaymentIn obj) {
        addToManyTarget("paymentsIn", obj, true);
    }
    public void removeFromPaymentsIn(PaymentIn obj) {
        removeToManyTarget("paymentsIn", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentIn> getPaymentsIn() {
        return (List<PaymentIn>)readProperty("paymentsIn");
    }


    public void addToStudentConcessions(StudentConcession obj) {
        addToManyTarget("studentConcessions", obj, true);
    }
    public void removeFromStudentConcessions(StudentConcession obj) {
        removeToManyTarget("studentConcessions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<StudentConcession> getStudentConcessions() {
        return (List<StudentConcession>)readProperty("studentConcessions");
    }


    public void addToWaitingLists(WaitingList obj) {
        addToManyTarget("waitingLists", obj, true);
    }
    public void removeFromWaitingLists(WaitingList obj) {
        removeToManyTarget("waitingLists", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WaitingList> getWaitingLists() {
        return (List<WaitingList>)readProperty("waitingLists");
    }


    protected abstract void onPostAdd();

}
