package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

/**
 * Class _Qualification was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Qualification extends CayenneDataObject {

    public static final String ANZSCO_PROPERTY = "anzsco";
    public static final String ANZSIC_PROPERTY = "anzsic";
    public static final String ASCO_PROPERTY = "asco";
    public static final String CREATED_PROPERTY = "created";
    public static final String FIELD_OF_EDUCATION_PROPERTY = "fieldOfEducation";
    public static final String FIELD_OF_STUDY_PROPERTY = "fieldOfStudy";
    public static final String IS_ACCREDITED_COURSE_PROPERTY = "isAccreditedCourse";
    public static final String ISH_VERSION_PROPERTY = "ishVersion";
    public static final String LEVEL_PROPERTY = "level";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NATIONAL_CODE_PROPERTY = "nationalCode";
    public static final String NEW_APPRENTICESHIP_PROPERTY = "newApprenticeship";
    public static final String NOMINAL_HOURS_PROPERTY = "nominalHours";
    public static final String REVIEW_DATE_PROPERTY = "reviewDate";
    public static final String TITLE_PROPERTY = "title";
    public static final String TRAINING_PACKAGE_ID_PROPERTY = "trainingPackageId";

    public static final String ID_PK_COLUMN = "id";

    public void setAnzsco(String anzsco) {
        writeProperty("anzsco", anzsco);
    }
    public String getAnzsco() {
        return (String)readProperty("anzsco");
    }

    public void setAnzsic(String anzsic) {
        writeProperty("anzsic", anzsic);
    }
    public String getAnzsic() {
        return (String)readProperty("anzsic");
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

    public void setIsAccreditedCourse(Byte isAccreditedCourse) {
        writeProperty("isAccreditedCourse", isAccreditedCourse);
    }
    public Byte getIsAccreditedCourse() {
        return (Byte)readProperty("isAccreditedCourse");
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

    protected abstract void onPreUpdate();

    protected abstract void onPrePersist();

}
