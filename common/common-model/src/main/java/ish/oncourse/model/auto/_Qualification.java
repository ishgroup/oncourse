package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.common.types.QualificationType;
import ish.oncourse.model.Certificate;
import ish.oncourse.model.Course;
import ish.oncourse.model.PriorLearning;

/**
 * Class _Qualification was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Qualification extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANZSCO_PROPERTY = "anzsco";
    @Deprecated
    public static final String ASCO_PROPERTY = "asco";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String FIELD_OF_EDUCATION_PROPERTY = "fieldOfEducation";
    @Deprecated
    public static final String FIELD_OF_STUDY_PROPERTY = "fieldOfStudy";
    @Deprecated
    public static final String IS_ACCREDITED_COURSE_PROPERTY = "isAccreditedCourse";
    @Deprecated
    public static final String ISH_VERSION_PROPERTY = "ishVersion";
    @Deprecated
    public static final String LEVEL_PROPERTY = "level";
    @Deprecated
    public static final String LEVEL_CODE_PROPERTY = "levelCode";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String NATIONAL_CODE_PROPERTY = "nationalCode";
    @Deprecated
    public static final String NEW_APPRENTICESHIP_PROPERTY = "newApprenticeship";
    @Deprecated
    public static final String NOMINAL_HOURS_PROPERTY = "nominalHours";
    @Deprecated
    public static final String REVIEW_DATE_PROPERTY = "reviewDate";
    @Deprecated
    public static final String TITLE_PROPERTY = "title";
    @Deprecated
    public static final String TRAINING_PACKAGE_ID_PROPERTY = "trainingPackageId";
    @Deprecated
    public static final String CERTIFICATES_PROPERTY = "certificates";
    @Deprecated
    public static final String COURSES_PROPERTY = "courses";
    @Deprecated
    public static final String PRIOR_LEARNINGS_PROPERTY = "priorLearnings";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> ANZSCO = new Property<String>("anzsco");
    public static final Property<String> ASCO = new Property<String>("asco");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> FIELD_OF_EDUCATION = new Property<String>("fieldOfEducation");
    public static final Property<String> FIELD_OF_STUDY = new Property<String>("fieldOfStudy");
    public static final Property<QualificationType> IS_ACCREDITED_COURSE = new Property<QualificationType>("isAccreditedCourse");
    public static final Property<Long> ISH_VERSION = new Property<Long>("ishVersion");
    public static final Property<String> LEVEL = new Property<String>("level");
    public static final Property<String> LEVEL_CODE = new Property<String>("levelCode");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NATIONAL_CODE = new Property<String>("nationalCode");
    public static final Property<String> NEW_APPRENTICESHIP = new Property<String>("newApprenticeship");
    public static final Property<Float> NOMINAL_HOURS = new Property<Float>("nominalHours");
    public static final Property<Date> REVIEW_DATE = new Property<Date>("reviewDate");
    public static final Property<String> TITLE = new Property<String>("title");
    public static final Property<Long> TRAINING_PACKAGE_ID = new Property<Long>("trainingPackageId");
    public static final Property<List<Certificate>> CERTIFICATES = new Property<List<Certificate>>("certificates");
    public static final Property<List<Course>> COURSES = new Property<List<Course>>("courses");
    public static final Property<List<PriorLearning>> PRIOR_LEARNINGS = new Property<List<PriorLearning>>("priorLearnings");

    public void setAnzsco(String anzsco) {
        writeProperty("anzsco", anzsco);
    }
    public String getAnzsco() {
        return (String)readProperty("anzsco");
    }

    public void setAsco(String asco) {
        writeProperty("asco", asco);
    }
    public String getAsco() {
        return (String)readProperty("asco");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setFieldOfEducation(String fieldOfEducation) {
        writeProperty("fieldOfEducation", fieldOfEducation);
    }
    public String getFieldOfEducation() {
        return (String)readProperty("fieldOfEducation");
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        writeProperty("fieldOfStudy", fieldOfStudy);
    }
    public String getFieldOfStudy() {
        return (String)readProperty("fieldOfStudy");
    }

    public void setIsAccreditedCourse(QualificationType isAccreditedCourse) {
        writeProperty("isAccreditedCourse", isAccreditedCourse);
    }
    public QualificationType getIsAccreditedCourse() {
        return (QualificationType)readProperty("isAccreditedCourse");
    }

    public void setIshVersion(Long ishVersion) {
        writeProperty("ishVersion", ishVersion);
    }
    public Long getIshVersion() {
        return (Long)readProperty("ishVersion");
    }

    public void setLevel(String level) {
        writeProperty("level", level);
    }
    public String getLevel() {
        return (String)readProperty("level");
    }

    public void setLevelCode(String levelCode) {
        writeProperty("levelCode", levelCode);
    }
    public String getLevelCode() {
        return (String)readProperty("levelCode");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setNationalCode(String nationalCode) {
        writeProperty("nationalCode", nationalCode);
    }
    public String getNationalCode() {
        return (String)readProperty("nationalCode");
    }

    public void setNewApprenticeship(String newApprenticeship) {
        writeProperty("newApprenticeship", newApprenticeship);
    }
    public String getNewApprenticeship() {
        return (String)readProperty("newApprenticeship");
    }

    public void setNominalHours(Float nominalHours) {
        writeProperty("nominalHours", nominalHours);
    }
    public Float getNominalHours() {
        return (Float)readProperty("nominalHours");
    }

    public void setReviewDate(Date reviewDate) {
        writeProperty("reviewDate", reviewDate);
    }
    public Date getReviewDate() {
        return (Date)readProperty("reviewDate");
    }

    public void setTitle(String title) {
        writeProperty("title", title);
    }
    public String getTitle() {
        return (String)readProperty("title");
    }

    public void setTrainingPackageId(Long trainingPackageId) {
        writeProperty("trainingPackageId", trainingPackageId);
    }
    public Long getTrainingPackageId() {
        return (Long)readProperty("trainingPackageId");
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


    public void addToCourses(Course obj) {
        addToManyTarget("courses", obj, true);
    }
    public void removeFromCourses(Course obj) {
        removeToManyTarget("courses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Course> getCourses() {
        return (List<Course>)readProperty("courses");
    }


    public void addToPriorLearnings(PriorLearning obj) {
        addToManyTarget("priorLearnings", obj, true);
    }
    public void removeFromPriorLearnings(PriorLearning obj) {
        removeToManyTarget("priorLearnings", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PriorLearning> getPriorLearnings() {
        return (List<PriorLearning>)readProperty("priorLearnings");
    }


    protected abstract void onPreUpdate();

    protected abstract void onPrePersist();

}
