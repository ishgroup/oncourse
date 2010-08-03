package ish.oncourse.model.auto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ish.oncourse.model.Course;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TutorRole;

/**
 * Class _CourseClass was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CourseClass extends Taggable {

    public static final String CANCELLED_PROPERTY = "cancelled";
    public static final String CODE_PROPERTY = "code";
    public static final String COUNT_OF_SESSIONS_PROPERTY = "countOfSessions";
    public static final String DELIVERY_MODE_PROPERTY = "deliveryMode";
    public static final String DETAIL_PROPERTY = "detail";
    public static final String DETAIL_TEXTILE_PROPERTY = "detailTextile";
    public static final String END_DATE_PROPERTY = "endDate";
    public static final String FEE_EX_GST_PROPERTY = "feeExGst";
    public static final String FEE_GST_PROPERTY = "feeGst";
    public static final String MATERIALS_PROPERTY = "materials";
    public static final String MATERIALS_TEXTILE_PROPERTY = "materialsTextile";
    public static final String MAXIMUM_PLACES_PROPERTY = "maximumPlaces";
    public static final String MINIMUM_PLACES_PROPERTY = "minimumPlaces";
    public static final String MINUTES_PER_SESSION_PROPERTY = "minutesPerSession";
    public static final String SESSION_DETAIL_PROPERTY = "sessionDetail";
    public static final String SESSION_DETAIL_TEXTILE_PROPERTY = "sessionDetailTextile";
    public static final String START_DATE_PROPERTY = "startDate";
    public static final String STARTING_MINUTE_PER_SESSION_PROPERTY = "startingMinutePerSession";
    public static final String TIME_ZONE_PROPERTY = "timeZone";
    public static final String WEB_VISIBLE_PROPERTY = "webVisible";
    public static final String COURSE_PROPERTY = "course";
    public static final String ENROLMENTS_PROPERTY = "enrolments";
    public static final String ROOM_PROPERTY = "room";
    public static final String SESSIONS_PROPERTY = "sessions";
    public static final String TUTOR_ROLES_PROPERTY = "tutorRoles";

    public static final String ID_PK_COLUMN = "id";

    public void setCancelled(boolean cancelled) {
        writeProperty("cancelled", cancelled);
    }
	public boolean isCancelled() {
        Boolean value = (Boolean)readProperty("cancelled");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setCode(String code) {
        writeProperty("code", code);
    }
    public String getCode() {
        return (String)readProperty("code");
    }

    public void setCountOfSessions(Integer countOfSessions) {
        writeProperty("countOfSessions", countOfSessions);
    }
    public Integer getCountOfSessions() {
        return (Integer)readProperty("countOfSessions");
    }

    public void setDeliveryMode(Integer deliveryMode) {
        writeProperty("deliveryMode", deliveryMode);
    }
    public Integer getDeliveryMode() {
        return (Integer)readProperty("deliveryMode");
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

    public void setEndDate(Date endDate) {
        writeProperty("endDate", endDate);
    }
    public Date getEndDate() {
        return (Date)readProperty("endDate");
    }

    public void setFeeExGst(BigDecimal feeExGst) {
        writeProperty("feeExGst", feeExGst);
    }
    public BigDecimal getFeeExGst() {
        return (BigDecimal)readProperty("feeExGst");
    }

    public void setFeeGst(BigDecimal feeGst) {
        writeProperty("feeGst", feeGst);
    }
    public BigDecimal getFeeGst() {
        return (BigDecimal)readProperty("feeGst");
    }

    public void setMaterials(String materials) {
        writeProperty("materials", materials);
    }
    public String getMaterials() {
        return (String)readProperty("materials");
    }

    public void setMaterialsTextile(String materialsTextile) {
        writeProperty("materialsTextile", materialsTextile);
    }
    public String getMaterialsTextile() {
        return (String)readProperty("materialsTextile");
    }

    public void setMaximumPlaces(Integer maximumPlaces) {
        writeProperty("maximumPlaces", maximumPlaces);
    }
    public Integer getMaximumPlaces() {
        return (Integer)readProperty("maximumPlaces");
    }

    public void setMinimumPlaces(Integer minimumPlaces) {
        writeProperty("minimumPlaces", minimumPlaces);
    }
    public Integer getMinimumPlaces() {
        return (Integer)readProperty("minimumPlaces");
    }

    public void setMinutesPerSession(Integer minutesPerSession) {
        writeProperty("minutesPerSession", minutesPerSession);
    }
    public Integer getMinutesPerSession() {
        return (Integer)readProperty("minutesPerSession");
    }

    public void setSessionDetail(String sessionDetail) {
        writeProperty("sessionDetail", sessionDetail);
    }
    public String getSessionDetail() {
        return (String)readProperty("sessionDetail");
    }

    public void setSessionDetailTextile(String sessionDetailTextile) {
        writeProperty("sessionDetailTextile", sessionDetailTextile);
    }
    public String getSessionDetailTextile() {
        return (String)readProperty("sessionDetailTextile");
    }

    public void setStartDate(Date startDate) {
        writeProperty("startDate", startDate);
    }
    public Date getStartDate() {
        return (Date)readProperty("startDate");
    }

    public void setStartingMinutePerSession(Integer startingMinutePerSession) {
        writeProperty("startingMinutePerSession", startingMinutePerSession);
    }
    public Integer getStartingMinutePerSession() {
        return (Integer)readProperty("startingMinutePerSession");
    }

    public void setTimeZone(String timeZone) {
        writeProperty("timeZone", timeZone);
    }
    public String getTimeZone() {
        return (String)readProperty("timeZone");
    }

    public void setWebVisible(boolean webVisible) {
        writeProperty("webVisible", webVisible);
    }
	public boolean isWebVisible() {
        Boolean value = (Boolean)readProperty("webVisible");
        return (value != null) ? value.booleanValue() : false;
    }


    public Course getCourse() {
        return (Course)readProperty("course");
    }


    public void addToEnrolments(Enrolment obj) {
        addToManyTarget("enrolments", obj, true);
    }
    public void removeFromEnrolments(Enrolment obj) {
        removeToManyTarget("enrolments", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Enrolment> getEnrolments() {
        return (List<Enrolment>)readProperty("enrolments");
    }



    public Room getRoom() {
        return (Room)readProperty("room");
    }


    @SuppressWarnings("unchecked")
    public List<Session> getSessions() {
        return (List<Session>)readProperty("sessions");
    }


    public void addToTutorRoles(TutorRole obj) {
        addToManyTarget("tutorRoles", obj, true);
    }
    public void removeFromTutorRoles(TutorRole obj) {
        removeToManyTarget("tutorRoles", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<TutorRole> getTutorRoles() {
        return (List<TutorRole>)readProperty("tutorRoles");
    }


}
