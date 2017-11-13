package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.SurveyType;

/**
 * Class _Survey was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Survey extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String COMMENT_PROPERTY = "comment";
    public static final String COURSE_SCORE_PROPERTY = "courseScore";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NET_PROMOTER_SCORE_PROPERTY = "netPromoterScore";
    public static final String PUBLIC_COMMENT_PROPERTY = "publicComment";
    public static final String TESTIMONIAL_PROPERTY = "testimonial";
    public static final String TUTOR_SCORE_PROPERTY = "tutorScore";
    public static final String VENUE_SCORE_PROPERTY = "venueScore";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String ENROLMENT_PROPERTY = "enrolment";
    public static final String SURVEY_TYPE_PROPERTY = "surveyType";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<String> COMMENT = Property.create("comment", String.class);
    public static final Property<Integer> COURSE_SCORE = Property.create("courseScore", Integer.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<Integer> NET_PROMOTER_SCORE = Property.create("netPromoterScore", Integer.class);
    public static final Property<Boolean> PUBLIC_COMMENT = Property.create("publicComment", Boolean.class);
    public static final Property<String> TESTIMONIAL = Property.create("testimonial", String.class);
    public static final Property<Integer> TUTOR_SCORE = Property.create("tutorScore", Integer.class);
    public static final Property<Integer> VENUE_SCORE = Property.create("venueScore", Integer.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<Enrolment> ENROLMENT = Property.create("enrolment", Enrolment.class);
    public static final Property<SurveyType> SURVEY_TYPE = Property.create("surveyType", SurveyType.class);

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setComment(String comment) {
        writeProperty("comment", comment);
    }
    public String getComment() {
        return (String)readProperty("comment");
    }

    public void setCourseScore(Integer courseScore) {
        writeProperty("courseScore", courseScore);
    }
    public Integer getCourseScore() {
        return (Integer)readProperty("courseScore");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setNetPromoterScore(Integer netPromoterScore) {
        writeProperty("netPromoterScore", netPromoterScore);
    }
    public Integer getNetPromoterScore() {
        return (Integer)readProperty("netPromoterScore");
    }

    public void setPublicComment(Boolean publicComment) {
        writeProperty("publicComment", publicComment);
    }
    public Boolean getPublicComment() {
        return (Boolean)readProperty("publicComment");
    }

    public void setTestimonial(String testimonial) {
        writeProperty("testimonial", testimonial);
    }
    public String getTestimonial() {
        return (String)readProperty("testimonial");
    }

    public void setTutorScore(Integer tutorScore) {
        writeProperty("tutorScore", tutorScore);
    }
    public Integer getTutorScore() {
        return (Integer)readProperty("tutorScore");
    }

    public void setVenueScore(Integer venueScore) {
        writeProperty("venueScore", venueScore);
    }
    public Integer getVenueScore() {
        return (Integer)readProperty("venueScore");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setEnrolment(Enrolment enrolment) {
        setToOneTarget("enrolment", enrolment, true);
    }

    public Enrolment getEnrolment() {
        return (Enrolment)readProperty("enrolment");
    }


    public void setSurveyType(SurveyType surveyType) {
        setToOneTarget("surveyType", surveyType, true);
    }

    public SurveyType getSurveyType() {
        return (SurveyType)readProperty("surveyType");
    }


    protected abstract void onPostAdd();

}
