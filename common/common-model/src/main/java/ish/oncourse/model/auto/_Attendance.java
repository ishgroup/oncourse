package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;

/**
 * Class _Attendance was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Attendance extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String ATTENDANCE_TYPE_PROPERTY = "attendanceType";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String DURATION_MINUTES_PROPERTY = "durationMinutes";
    @Deprecated
    public static final String MARKED_BY_TUTOR_DATE_PROPERTY = "markedByTutorDate";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String NOTE_PROPERTY = "note";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String MARKED_BY_TUTOR_PROPERTY = "markedByTutor";
    @Deprecated
    public static final String SESSION_PROPERTY = "session";
    @Deprecated
    public static final String STUDENT_PROPERTY = "student";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Integer> ATTENDANCE_TYPE = new Property<Integer>("attendanceType");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Integer> DURATION_MINUTES = new Property<Integer>("durationMinutes");
    public static final Property<Date> MARKED_BY_TUTOR_DATE = new Property<Date>("markedByTutorDate");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NOTE = new Property<String>("note");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Tutor> MARKED_BY_TUTOR = new Property<Tutor>("markedByTutor");
    public static final Property<Session> SESSION = new Property<Session>("session");
    public static final Property<Student> STUDENT = new Property<Student>("student");

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setAttendanceType(Integer attendanceType) {
        writeProperty("attendanceType", attendanceType);
    }
    public Integer getAttendanceType() {
        return (Integer)readProperty("attendanceType");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDurationMinutes(Integer durationMinutes) {
        writeProperty("durationMinutes", durationMinutes);
    }
    public Integer getDurationMinutes() {
        return (Integer)readProperty("durationMinutes");
    }

    public void setMarkedByTutorDate(Date markedByTutorDate) {
        writeProperty("markedByTutorDate", markedByTutorDate);
    }
    public Date getMarkedByTutorDate() {
        return (Date)readProperty("markedByTutorDate");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setNote(String note) {
        writeProperty("note", note);
    }
    public String getNote() {
        return (String)readProperty("note");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setMarkedByTutor(Tutor markedByTutor) {
        setToOneTarget("markedByTutor", markedByTutor, true);
    }

    public Tutor getMarkedByTutor() {
        return (Tutor)readProperty("markedByTutor");
    }


    public void setSession(Session session) {
        setToOneTarget("session", session, true);
    }

    public Session getSession() {
        return (Session)readProperty("session");
    }


    public void setStudent(Student student) {
        setToOneTarget("student", student, true);
    }

    public Student getStudent() {
        return (Student)readProperty("student");
    }


}
