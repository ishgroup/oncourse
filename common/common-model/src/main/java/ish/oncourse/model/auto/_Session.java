package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.SessionModule;
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
    public static final String PRIVATE_NOTES_PROPERTY = "privateNotes";
    public static final String PUBLIC_NOTES_PROPERTY = "publicNotes";
    public static final String START_DATE_PROPERTY = "startDate";
    public static final String TIME_ZONE_PROPERTY = "timeZone";
    public static final String ATTENDANCES_PROPERTY = "attendances";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_CLASS_PROPERTY = "courseClass";
    public static final String MARKER_PROPERTY = "marker";
    public static final String ROOM_PROPERTY = "room";
    public static final String SESSION_MODULES_PROPERTY = "sessionModules";
    public static final String SESSION_TUTORS_PROPERTY = "sessionTutors";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setEndDate(Date endDate) {
        writeProperty(END_DATE_PROPERTY, endDate);
    }
    public Date getEndDate() {
        return (Date)readProperty(END_DATE_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setPrivateNotes(String privateNotes) {
        writeProperty(PRIVATE_NOTES_PROPERTY, privateNotes);
    }
    public String getPrivateNotes() {
        return (String)readProperty(PRIVATE_NOTES_PROPERTY);
    }

    public void setPublicNotes(String publicNotes) {
        writeProperty(PUBLIC_NOTES_PROPERTY, publicNotes);
    }
    public String getPublicNotes() {
        return (String)readProperty(PUBLIC_NOTES_PROPERTY);
    }

    public void setStartDate(Date startDate) {
        writeProperty(START_DATE_PROPERTY, startDate);
    }
    public Date getStartDate() {
        return (Date)readProperty(START_DATE_PROPERTY);
    }

    public void setTimeZone(String timeZone) {
        writeProperty(TIME_ZONE_PROPERTY, timeZone);
    }
    public String getTimeZone() {
        return (String)readProperty(TIME_ZONE_PROPERTY);
    }

    public void addToAttendances(Attendance obj) {
        addToManyTarget(ATTENDANCES_PROPERTY, obj, true);
    }
    public void removeFromAttendances(Attendance obj) {
        removeToManyTarget(ATTENDANCES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Attendance> getAttendances() {
        return (List<Attendance>)readProperty(ATTENDANCES_PROPERTY);
    }


    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void setCourseClass(CourseClass courseClass) {
        setToOneTarget(COURSE_CLASS_PROPERTY, courseClass, true);
    }

    public CourseClass getCourseClass() {
        return (CourseClass)readProperty(COURSE_CLASS_PROPERTY);
    }


    public void setMarker(Tutor marker) {
        setToOneTarget(MARKER_PROPERTY, marker, true);
    }

    public Tutor getMarker() {
        return (Tutor)readProperty(MARKER_PROPERTY);
    }


    public void setRoom(Room room) {
        setToOneTarget(ROOM_PROPERTY, room, true);
    }

    public Room getRoom() {
        return (Room)readProperty(ROOM_PROPERTY);
    }


    public void addToSessionModules(SessionModule obj) {
        addToManyTarget(SESSION_MODULES_PROPERTY, obj, true);
    }
    public void removeFromSessionModules(SessionModule obj) {
        removeToManyTarget(SESSION_MODULES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<SessionModule> getSessionModules() {
        return (List<SessionModule>)readProperty(SESSION_MODULES_PROPERTY);
    }


    public void addToSessionTutors(SessionTutor obj) {
        addToManyTarget(SESSION_TUTORS_PROPERTY, obj, true);
    }
    public void removeFromSessionTutors(SessionTutor obj) {
        removeToManyTarget(SESSION_TUTORS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<SessionTutor> getSessionTutors() {
        return (List<SessionTutor>)readProperty(SESSION_TUTORS_PROPERTY);
    }


}
