package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

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
public abstract class _Qualification extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANZSCO_PROPERTY = "anzsco";
    public static final String ASCO_PROPERTY = "asco";
    public static final String CREATED_PROPERTY = "created";
    public static final String FIELD_OF_EDUCATION_PROPERTY = "fieldOfEducation";
    public static final String FIELD_OF_STUDY_PROPERTY = "fieldOfStudy";
    public static final String IS_ACCREDITED_COURSE_PROPERTY = "isAccreditedCourse";
    public static final String ISH_VERSION_PROPERTY = "ishVersion";
    public static final String LEVEL_PROPERTY = "level";
    public static final String LEVEL_CODE_PROPERTY = "levelCode";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NATIONAL_CODE_PROPERTY = "nationalCode";
    public static final String NEW_APPRENTICESHIP_PROPERTY = "newApprenticeship";
    public static final String NOMINAL_HOURS_PROPERTY = "nominalHours";
    public static final String REVIEW_DATE_PROPERTY = "reviewDate";
    public static final String TITLE_PROPERTY = "title";
    public static final String TRAINING_PACKAGE_ID_PROPERTY = "trainingPackageId";
    public static final String CERTIFICATES_PROPERTY = "certificates";
    public static final String COURSES_PROPERTY = "courses";
    public static final String PRIOR_LEARNINGS_PROPERTY = "priorLearnings";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> ANZSCO = Property.create("anzsco", String.class);
    public static final Property<String> ASCO = Property.create("asco", String.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<String> FIELD_OF_EDUCATION = Property.create("fieldOfEducation", String.class);
    public static final Property<String> FIELD_OF_STUDY = Property.create("fieldOfStudy", String.class);
    public static final Property<QualificationType> IS_ACCREDITED_COURSE = Property.create("isAccreditedCourse", QualificationType.class);
    public static final Property<Long> ISH_VERSION = Property.create("ishVersion", Long.class);
    public static final Property<String> LEVEL = Property.create("level", String.class);
    public static final Property<String> LEVEL_CODE = Property.create("levelCode", String.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> NATIONAL_CODE = Property.create("nationalCode", String.class);
    public static final Property<String> NEW_APPRENTICESHIP = Property.create("newApprenticeship", String.class);
    public static final Property<Float> NOMINAL_HOURS = Property.create("nominalHours", Float.class);
    public static final Property<Date> REVIEW_DATE = Property.create("reviewDate", Date.class);
    public static final Property<String> TITLE = Property.create("title", String.class);
    public static final Property<Long> TRAINING_PACKAGE_ID = Property.create("trainingPackageId", Long.class);
    public static final Property<List<Certificate>> CERTIFICATES = Property.create("certificates", List.class);
    public static final Property<List<Course>> COURSES = Property.create("courses", List.class);
    public static final Property<List<PriorLearning>> PRIOR_LEARNINGS = Property.create("priorLearnings", List.class);

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
