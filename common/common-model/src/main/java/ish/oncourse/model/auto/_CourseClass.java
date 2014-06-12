package ish.oncourse.model.auto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.common.types.CourseClassAttendanceType;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.model.Discussion;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.TutorRole;

/**
 * Class _CourseClass was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CourseClass extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String ATTENDANCE_TYPE_PROPERTY = "attendanceType";
    public static final String CANCELLED_PROPERTY = "cancelled";
    public static final String CENSUS_DATE_PROPERTY = "censusDate";
    public static final String CODE_PROPERTY = "code";
    public static final String COUNT_OF_SESSIONS_PROPERTY = "countOfSessions";
    public static final String CREATED_PROPERTY = "created";
    public static final String DELIVERY_MODE_PROPERTY = "deliveryMode";
    public static final String DETAIL_PROPERTY = "detail";
    public static final String DETAIL_TEXTILE_PROPERTY = "detailTextile";
    public static final String END_DATE_PROPERTY = "endDate";
    public static final String EXPECTED_HOURS_PROPERTY = "expectedHours";
    public static final String FEE_EX_GST_PROPERTY = "feeExGst";
    public static final String FEE_GST_PROPERTY = "feeGst";
    public static final String FEE_HELP_CLASS_PROPERTY = "feeHelpClass";
    public static final String FULL_TIME_LOAD_PROPERTY = "fullTimeLoad";
    public static final String IS_ACTIVE_PROPERTY = "isActive";
    public static final String IS_DISTANT_LEARNING_COURSE_PROPERTY = "isDistantLearningCourse";
    public static final String IS_WEB_VISIBLE_PROPERTY = "isWebVisible";
    public static final String MATERIALS_PROPERTY = "materials";
    public static final String MATERIALS_TEXTILE_PROPERTY = "materialsTextile";
    public static final String MAX_STUDENT_AGE_PROPERTY = "maxStudentAge";
    public static final String MAXIMUM_DAYS_PROPERTY = "maximumDays";
    public static final String MAXIMUM_PLACES_PROPERTY = "maximumPlaces";
    public static final String MIN_STUDENT_AGE_PROPERTY = "minStudentAge";
    public static final String MINIMUM_PLACES_PROPERTY = "minimumPlaces";
    public static final String MINUTES_PER_SESSION_PROPERTY = "minutesPerSession";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String REPORTING_PERIOD_PROPERTY = "reportingPeriod";
    public static final String SESSION_DETAIL_PROPERTY = "sessionDetail";
    public static final String SESSION_DETAIL_TEXTILE_PROPERTY = "sessionDetailTextile";
    public static final String START_DATE_PROPERTY = "startDate";
    public static final String STARTING_MINUTE_PER_SESSION_PROPERTY = "startingMinutePerSession";
    public static final String TIME_ZONE_PROPERTY = "timeZone";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_PROPERTY = "course";
    public static final String DISCOUNT_COURSE_CLASSES_PROPERTY = "discountCourseClasses";
    public static final String DISCUSSIONS_PROPERTY = "discussions";
    public static final String ENROLMENTS_PROPERTY = "enrolments";
    public static final String INVOICE_LINES_PROPERTY = "invoiceLines";
    public static final String ROOM_PROPERTY = "room";
    public static final String SESSIONS_PROPERTY = "sessions";
    public static final String TUTOR_ROLES_PROPERTY = "tutorRoles";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setAttendanceType(CourseClassAttendanceType attendanceType) {
        writeProperty(ATTENDANCE_TYPE_PROPERTY, attendanceType);
    }
    public CourseClassAttendanceType getAttendanceType() {
        return (CourseClassAttendanceType)readProperty(ATTENDANCE_TYPE_PROPERTY);
    }

    public void setCancelled(boolean cancelled) {
        writeProperty(CANCELLED_PROPERTY, cancelled);
    }
	public boolean isCancelled() {
        Boolean value = (Boolean)readProperty(CANCELLED_PROPERTY);
        return (value != null) ? value.booleanValue() : false;
    }

    public void setCensusDate(Date censusDate) {
        writeProperty(CENSUS_DATE_PROPERTY, censusDate);
    }
    public Date getCensusDate() {
        return (Date)readProperty(CENSUS_DATE_PROPERTY);
    }

    public void setCode(String code) {
        writeProperty(CODE_PROPERTY, code);
    }
    public String getCode() {
        return (String)readProperty(CODE_PROPERTY);
    }

    public void setCountOfSessions(Integer countOfSessions) {
        writeProperty(COUNT_OF_SESSIONS_PROPERTY, countOfSessions);
    }
    public Integer getCountOfSessions() {
        return (Integer)readProperty(COUNT_OF_SESSIONS_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setDeliveryMode(Integer deliveryMode) {
        writeProperty(DELIVERY_MODE_PROPERTY, deliveryMode);
    }
    public Integer getDeliveryMode() {
        return (Integer)readProperty(DELIVERY_MODE_PROPERTY);
    }

    public void setDetail(String detail) {
        writeProperty(DETAIL_PROPERTY, detail);
    }
    public String getDetail() {
        return (String)readProperty(DETAIL_PROPERTY);
    }

    public void setDetailTextile(String detailTextile) {
        writeProperty(DETAIL_TEXTILE_PROPERTY, detailTextile);
    }
    public String getDetailTextile() {
        return (String)readProperty(DETAIL_TEXTILE_PROPERTY);
    }

    public void setEndDate(Date endDate) {
        writeProperty(END_DATE_PROPERTY, endDate);
    }
    public Date getEndDate() {
        return (Date)readProperty(END_DATE_PROPERTY);
    }

    public void setExpectedHours(BigDecimal expectedHours) {
        writeProperty(EXPECTED_HOURS_PROPERTY, expectedHours);
    }
    public BigDecimal getExpectedHours() {
        return (BigDecimal)readProperty(EXPECTED_HOURS_PROPERTY);
    }

    public void setFeeExGst(Money feeExGst) {
        writeProperty(FEE_EX_GST_PROPERTY, feeExGst);
    }
    public Money getFeeExGst() {
        return (Money)readProperty(FEE_EX_GST_PROPERTY);
    }

    public void setFeeGst(Money feeGst) {
        writeProperty(FEE_GST_PROPERTY, feeGst);
    }
    public Money getFeeGst() {
        return (Money)readProperty(FEE_GST_PROPERTY);
    }

    public void setFeeHelpClass(Boolean feeHelpClass) {
        writeProperty(FEE_HELP_CLASS_PROPERTY, feeHelpClass);
    }
    public Boolean getFeeHelpClass() {
        return (Boolean)readProperty(FEE_HELP_CLASS_PROPERTY);
    }

    public void setFullTimeLoad(String fullTimeLoad) {
        writeProperty(FULL_TIME_LOAD_PROPERTY, fullTimeLoad);
    }
    public String getFullTimeLoad() {
        return (String)readProperty(FULL_TIME_LOAD_PROPERTY);
    }

    public void setIsActive(Boolean isActive) {
        writeProperty(IS_ACTIVE_PROPERTY, isActive);
    }
    public Boolean getIsActive() {
        return (Boolean)readProperty(IS_ACTIVE_PROPERTY);
    }

    public void setIsDistantLearningCourse(Boolean isDistantLearningCourse) {
        writeProperty(IS_DISTANT_LEARNING_COURSE_PROPERTY, isDistantLearningCourse);
    }
    public Boolean getIsDistantLearningCourse() {
        return (Boolean)readProperty(IS_DISTANT_LEARNING_COURSE_PROPERTY);
    }

    public void setIsWebVisible(Boolean isWebVisible) {
        writeProperty(IS_WEB_VISIBLE_PROPERTY, isWebVisible);
    }
    public Boolean getIsWebVisible() {
        return (Boolean)readProperty(IS_WEB_VISIBLE_PROPERTY);
    }

    public void setMaterials(String materials) {
        writeProperty(MATERIALS_PROPERTY, materials);
    }
    public String getMaterials() {
        return (String)readProperty(MATERIALS_PROPERTY);
    }

    public void setMaterialsTextile(String materialsTextile) {
        writeProperty(MATERIALS_TEXTILE_PROPERTY, materialsTextile);
    }
    public String getMaterialsTextile() {
        return (String)readProperty(MATERIALS_TEXTILE_PROPERTY);
    }

    public void setMaxStudentAge(Integer maxStudentAge) {
        writeProperty(MAX_STUDENT_AGE_PROPERTY, maxStudentAge);
    }
    public Integer getMaxStudentAge() {
        return (Integer)readProperty(MAX_STUDENT_AGE_PROPERTY);
    }

    public void setMaximumDays(Integer maximumDays) {
        writeProperty(MAXIMUM_DAYS_PROPERTY, maximumDays);
    }
    public Integer getMaximumDays() {
        return (Integer)readProperty(MAXIMUM_DAYS_PROPERTY);
    }

    public void setMaximumPlaces(Integer maximumPlaces) {
        writeProperty(MAXIMUM_PLACES_PROPERTY, maximumPlaces);
    }
    public Integer getMaximumPlaces() {
        return (Integer)readProperty(MAXIMUM_PLACES_PROPERTY);
    }

    public void setMinStudentAge(Integer minStudentAge) {
        writeProperty(MIN_STUDENT_AGE_PROPERTY, minStudentAge);
    }
    public Integer getMinStudentAge() {
        return (Integer)readProperty(MIN_STUDENT_AGE_PROPERTY);
    }

    public void setMinimumPlaces(Integer minimumPlaces) {
        writeProperty(MINIMUM_PLACES_PROPERTY, minimumPlaces);
    }
    public Integer getMinimumPlaces() {
        return (Integer)readProperty(MINIMUM_PLACES_PROPERTY);
    }

    public void setMinutesPerSession(Integer minutesPerSession) {
        writeProperty(MINUTES_PER_SESSION_PROPERTY, minutesPerSession);
    }
    public Integer getMinutesPerSession() {
        return (Integer)readProperty(MINUTES_PER_SESSION_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setReportingPeriod(Integer reportingPeriod) {
        writeProperty(REPORTING_PERIOD_PROPERTY, reportingPeriod);
    }
    public Integer getReportingPeriod() {
        return (Integer)readProperty(REPORTING_PERIOD_PROPERTY);
    }

    public void setSessionDetail(String sessionDetail) {
        writeProperty(SESSION_DETAIL_PROPERTY, sessionDetail);
    }
    public String getSessionDetail() {
        return (String)readProperty(SESSION_DETAIL_PROPERTY);
    }

    public void setSessionDetailTextile(String sessionDetailTextile) {
        writeProperty(SESSION_DETAIL_TEXTILE_PROPERTY, sessionDetailTextile);
    }
    public String getSessionDetailTextile() {
        return (String)readProperty(SESSION_DETAIL_TEXTILE_PROPERTY);
    }

    public void setStartDate(Date startDate) {
        writeProperty(START_DATE_PROPERTY, startDate);
    }
    public Date getStartDate() {
        return (Date)readProperty(START_DATE_PROPERTY);
    }

    public void setStartingMinutePerSession(Integer startingMinutePerSession) {
        writeProperty(STARTING_MINUTE_PER_SESSION_PROPERTY, startingMinutePerSession);
    }
    public Integer getStartingMinutePerSession() {
        return (Integer)readProperty(STARTING_MINUTE_PER_SESSION_PROPERTY);
    }

    public void setTimeZone(String timeZone) {
        writeProperty(TIME_ZONE_PROPERTY, timeZone);
    }
    public String getTimeZone() {
        return (String)readProperty(TIME_ZONE_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void setCourse(Course course) {
        setToOneTarget(COURSE_PROPERTY, course, true);
    }

    public Course getCourse() {
        return (Course)readProperty(COURSE_PROPERTY);
    }


    public void addToDiscountCourseClasses(DiscountCourseClass obj) {
        addToManyTarget(DISCOUNT_COURSE_CLASSES_PROPERTY, obj, true);
    }
    public void removeFromDiscountCourseClasses(DiscountCourseClass obj) {
        removeToManyTarget(DISCOUNT_COURSE_CLASSES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountCourseClass> getDiscountCourseClasses() {
        return (List<DiscountCourseClass>)readProperty(DISCOUNT_COURSE_CLASSES_PROPERTY);
    }


    public void addToDiscussions(Discussion obj) {
        addToManyTarget(DISCUSSIONS_PROPERTY, obj, true);
    }
    public void removeFromDiscussions(Discussion obj) {
        removeToManyTarget(DISCUSSIONS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Discussion> getDiscussions() {
        return (List<Discussion>)readProperty(DISCUSSIONS_PROPERTY);
    }


    public void addToEnrolments(Enrolment obj) {
        addToManyTarget(ENROLMENTS_PROPERTY, obj, true);
    }
    public void removeFromEnrolments(Enrolment obj) {
        removeToManyTarget(ENROLMENTS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Enrolment> getEnrolments() {
        return (List<Enrolment>)readProperty(ENROLMENTS_PROPERTY);
    }


    public void addToInvoiceLines(InvoiceLine obj) {
        addToManyTarget(INVOICE_LINES_PROPERTY, obj, true);
    }
    public void removeFromInvoiceLines(InvoiceLine obj) {
        removeToManyTarget(INVOICE_LINES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<InvoiceLine> getInvoiceLines() {
        return (List<InvoiceLine>)readProperty(INVOICE_LINES_PROPERTY);
    }


    public void setRoom(Room room) {
        setToOneTarget(ROOM_PROPERTY, room, true);
    }

    public Room getRoom() {
        return (Room)readProperty(ROOM_PROPERTY);
    }


    public void addToSessions(Session obj) {
        addToManyTarget(SESSIONS_PROPERTY, obj, true);
    }
    public void removeFromSessions(Session obj) {
        removeToManyTarget(SESSIONS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Session> getSessions() {
        return (List<Session>)readProperty(SESSIONS_PROPERTY);
    }


    public void addToTutorRoles(TutorRole obj) {
        addToManyTarget(TUTOR_ROLES_PROPERTY, obj, true);
    }
    public void removeFromTutorRoles(TutorRole obj) {
        removeToManyTarget(TUTOR_ROLES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<TutorRole> getTutorRoles() {
        return (List<TutorRole>)readProperty(TUTOR_ROLES_PROPERTY);
    }


}
