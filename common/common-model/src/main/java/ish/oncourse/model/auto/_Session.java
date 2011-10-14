package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tutor;

/**
 * Class _Session was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Session extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String END_DATE_PROPERTY = "endDate";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String START_DATE_PROPERTY = "startDate";
    public static final String TIME_ZONE_PROPERTY = "timeZone";
    public static final String ATTENDANCES_PROPERTY = "attendances";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_CLASS_PROPERTY = "courseClass";
    public static final String MARKER_PROPERTY = "marker";
    public static final String ROOM_PROPERTY = "room";
    public static final String SESSION_TUTORS_PROPERTY = "sessionTutors";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setEndDate(Date endDate) {
        writeProperty("endDate", endDate);
    }
    public Date getEndDate() {
        return (Date)readProperty("endDate");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setStartDate(Date startDate) {
        writeProperty("startDate", startDate);
    }
    public Date getStartDate() {
        return (Date)readProperty("startDate");
    }

    public void setTimeZone(String timeZone) {
        writeProperty("timeZone", timeZone);
    }
    public String getTimeZone() {
        return (String)readProperty("timeZone");
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


    public void setMarker(Tutor marker) {
        setToOneTarget("marker", marker, true);
    }

    public Tutor getMarker() {
        return (Tutor)readProperty("marker");
    }


    public void setRoom(Room room) {
        setToOneTarget("room", room, true);
    }

    public Room getRoom() {
        return (Room)readProperty("room");
    }


    public void addToSessionTutors(SessionTutor obj) {
        addToManyTarget("sessionTutors", obj, true);
    }
    public void removeFromSessionTutors(SessionTutor obj) {
        removeToManyTarget("sessionTutors", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<SessionTutor> getSessionTutors() {
        return (List<SessionTutor>)readProperty("sessionTutors");
    }


}
