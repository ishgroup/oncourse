package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.common.types.AvetmissStudentDisabilityType;
import ish.common.types.AvetmissStudentEnglishProficiency;
import ish.common.types.AvetmissStudentIndigenousStatus;
import ish.common.types.AvetmissStudentPriorEducation;
import ish.common.types.AvetmissStudentSchoolLevel;
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

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CONCESSION_TYPE_PROPERTY = "concessionType";
    public static final String CREATED_PROPERTY = "created";
    public static final String DISABILITY_TYPE_PROPERTY = "disabilityType";
    public static final String ENGLISH_PROFICIENCY_PROPERTY = "englishProficiency";
    public static final String HIGHEST_SCHOOL_LEVEL_PROPERTY = "highestSchoolLevel";
    public static final String INDIGENOUS_STATUS_PROPERTY = "indigenousStatus";
    public static final String IS_OVERSEAS_CLIENT_PROPERTY = "isOverseasClient";
    public static final String IS_STILL_AT_SCHOOL_PROPERTY = "isStillAtSchool";
    public static final String LABOUR_FORCE_TYPE_PROPERTY = "labourForceType";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PRIOR_EDUCATION_CODE_PROPERTY = "priorEducationCode";
    public static final String YEAR_SCHOOL_COMPLETED_PROPERTY = "yearSchoolCompleted";
    public static final String ATTENDANCES_PROPERTY = "attendances";
    public static final String CERTIFICATES_PROPERTY = "certificates";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONTACT_PROPERTY = "contact";
    public static final String COUNTRY_OF_BIRTH_PROPERTY = "countryOfBirth";
    public static final String ENROLMENTS_PROPERTY = "enrolments";
    public static final String LANGUAGE_PROPERTY = "language";
    public static final String LANGUAGE_HOME_PROPERTY = "languageHome";
    public static final String MESSAGE_PEOPLE_PROPERTY = "messagePeople";
    public static final String PAYMENTS_IN_PROPERTY = "paymentsIn";
    public static final String STUDENT_CONCESSIONS_PROPERTY = "studentConcessions";
    public static final String WAITING_LISTS_PROPERTY = "waitingLists";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
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

    public void setYearSchoolCompleted(Integer yearSchoolCompleted) {
        writeProperty("yearSchoolCompleted", yearSchoolCompleted);
    }
    public Integer getYearSchoolCompleted() {
        return (Integer)readProperty("yearSchoolCompleted");
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
