package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;

/**
 * Class _TutorRole was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _TutorRole extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CONFIRMED_DATE_PROPERTY = "confirmedDate";
    public static final String CREATED_PROPERTY = "created";
    public static final String DETAIL_PROPERTY = "detail";
    public static final String DETAIL_TEXTILE_PROPERTY = "detailTextile";
    public static final String IS_CONFIRMED_PROPERTY = "isConfirmed";
    public static final String IS_DELETED_PROPERTY = "isDeleted";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_CLASS_PROPERTY = "courseClass";
    public static final String TUTOR_PROPERTY = "tutor";

    public static final String COURSE_CLASS_ID_PK_COLUMN = "courseClassId";
    public static final String TUTOR_ID_PK_COLUMN = "tutorId";

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setConfirmedDate(Date confirmedDate) {
        writeProperty("confirmedDate", confirmedDate);
    }
    public Date getConfirmedDate() {
        return (Date)readProperty("confirmedDate");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDetail(String detail) {
        writeProperty("detail", detail);
    }
    public String getDetail() {
        return (String)readProperty("detail");
    }

    public void setDetailTextile(String detailTextile) {
        writeProperty("detailTextile", detailTextile);
    }
    public String getDetailTextile() {
        return (String)readProperty("detailTextile");
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        writeProperty("isConfirmed", isConfirmed);
    }
    public Boolean getIsConfirmed() {
        return (Boolean)readProperty("isConfirmed");
    }

    public void setIsDeleted(Boolean isDeleted) {
        writeProperty("isDeleted", isDeleted);
    }
    public Boolean getIsDeleted() {
        return (Boolean)readProperty("isDeleted");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setCourseClass(CourseClass courseClass) {
        setToOneTarget("courseClass", courseClass, true);
    }

    public CourseClass getCourseClass() {
        return (CourseClass)readProperty("courseClass");
    }


    public void setTutor(Tutor tutor) {
        setToOneTarget("tutor", tutor, true);
    }

    public Tutor getTutor() {
        return (Tutor)readProperty("tutor");
    }


}
