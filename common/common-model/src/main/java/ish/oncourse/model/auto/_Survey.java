package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;

/**
 * Class _Survey was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Survey extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String COMMENT_PROPERTY = "comment";
    public static final String COURSE_SCORE_PROPERTY = "courseScore";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PUBLIC_COMMENT_PROPERTY = "publicComment";
    public static final String TUTOR_SCORE_PROPERTY = "tutorScore";
    public static final String UNIQUE_CODE_PROPERTY = "uniqueCode";
    public static final String VENUE_SCORE_PROPERTY = "venueScore";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String ENROLMENT_PROPERTY = "enrolment";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setComment(String comment) {
        writeProperty(COMMENT_PROPERTY, comment);
    }
    public String getComment() {
        return (String)readProperty(COMMENT_PROPERTY);
    }

    public void setCourseScore(Integer courseScore) {
        writeProperty(COURSE_SCORE_PROPERTY, courseScore);
    }
    public Integer getCourseScore() {
        return (Integer)readProperty(COURSE_SCORE_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setPublicComment(Boolean publicComment) {
        writeProperty(PUBLIC_COMMENT_PROPERTY, publicComment);
    }
    public Boolean getPublicComment() {
        return (Boolean)readProperty(PUBLIC_COMMENT_PROPERTY);
    }

    public void setTutorScore(Integer tutorScore) {
        writeProperty(TUTOR_SCORE_PROPERTY, tutorScore);
    }
    public Integer getTutorScore() {
        return (Integer)readProperty(TUTOR_SCORE_PROPERTY);
    }

    public void setUniqueCode(String uniqueCode) {
        writeProperty(UNIQUE_CODE_PROPERTY, uniqueCode);
    }
    public String getUniqueCode() {
        return (String)readProperty(UNIQUE_CODE_PROPERTY);
    }

    public void setVenueScore(Integer venueScore) {
        writeProperty(VENUE_SCORE_PROPERTY, venueScore);
    }
    public Integer getVenueScore() {
        return (Integer)readProperty(VENUE_SCORE_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void setEnrolment(Enrolment enrolment) {
        setToOneTarget(ENROLMENT_PROPERTY, enrolment, true);
    }

    public Enrolment getEnrolment() {
        return (Enrolment)readProperty(ENROLMENT_PROPERTY);
    }


    protected abstract void onPostAdd();

}
