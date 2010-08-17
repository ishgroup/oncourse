package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.WaitingList;

/**
 * Class _Course was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Course extends CayenneDataObject {

    public static final String ALLOW_WAITING_LIST_PROPERTY = "allowWaitingList";
    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CODE_PROPERTY = "code";
    public static final String CREATED_PROPERTY = "created";
    public static final String DETAIL_PROPERTY = "detail";
    public static final String DETAIL_TEXTILE_PROPERTY = "detailTextile";
    public static final String FIELD_OF_EDUCATION_PROPERTY = "fieldOfEducation";
    public static final String IS_DELETED_PROPERTY = "isDeleted";
    public static final String IS_SUFFICIENT_FOR_QUALIFICATION_PROPERTY = "isSufficientForQualification";
    public static final String IS_VETCOURSE_PROPERTY = "isVETCourse";
    public static final String IS_WEB_VISIBLE_PROPERTY = "isWebVisible";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String NOMINAL_HOURS_PROPERTY = "nominalHours";
    public static final String QUALIFICATION_ID_PROPERTY = "qualificationId";
    public static final String SEARCH_TEXT_PROPERTY = "searchText";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_CLASSES_PROPERTY = "courseClasses";
    public static final String COURSE_MODULES_PROPERTY = "courseModules";
    public static final String WAITING_LISTS_PROPERTY = "waitingLists";

    public static final String ID_PK_COLUMN = "id";

    public void setAllowWaitingList(Boolean allowWaitingList) {
        writeProperty("allowWaitingList", allowWaitingList);
    }
    public Boolean getAllowWaitingList() {
        return (Boolean)readProperty("allowWaitingList");
    }

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCode(String code) {
        writeProperty("code", code);
    }
    public String getCode() {
        return (String)readProperty("code");
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

    public void setFieldOfEducation(String fieldOfEducation) {
        writeProperty("fieldOfEducation", fieldOfEducation);
    }
    public String getFieldOfEducation() {
        return (String)readProperty("fieldOfEducation");
    }

    public void setIsDeleted(Boolean isDeleted) {
        writeProperty("isDeleted", isDeleted);
    }
    public Boolean getIsDeleted() {
        return (Boolean)readProperty("isDeleted");
    }

    public void setIsSufficientForQualification(Boolean isSufficientForQualification) {
        writeProperty("isSufficientForQualification", isSufficientForQualification);
    }
    public Boolean getIsSufficientForQualification() {
        return (Boolean)readProperty("isSufficientForQualification");
    }

    public void setIsVETCourse(Boolean isVETCourse) {
        writeProperty("isVETCourse", isVETCourse);
    }
    public Boolean getIsVETCourse() {
        return (Boolean)readProperty("isVETCourse");
    }

    public void setIsWebVisible(Boolean isWebVisible) {
        writeProperty("isWebVisible", isWebVisible);
    }
    public Boolean getIsWebVisible() {
        return (Boolean)readProperty("isWebVisible");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setNominalHours(Float nominalHours) {
        writeProperty("nominalHours", nominalHours);
    }
    public Float getNominalHours() {
        return (Float)readProperty("nominalHours");
    }

    public void setQualificationId(Long qualificationId) {
        writeProperty("qualificationId", qualificationId);
    }
    public Long getQualificationId() {
        return (Long)readProperty("qualificationId");
    }

    public void setSearchText(String searchText) {
        writeProperty("searchText", searchText);
    }
    public String getSearchText() {
        return (String)readProperty("searchText");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void addToCourseClasses(CourseClass obj) {
        addToManyTarget("courseClasses", obj, true);
    }
    public void removeFromCourseClasses(CourseClass obj) {
        removeToManyTarget("courseClasses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CourseClass> getCourseClasses() {
        return (List<CourseClass>)readProperty("courseClasses");
    }


    public void addToCourseModules(CourseModule obj) {
        addToManyTarget("courseModules", obj, true);
    }
    public void removeFromCourseModules(CourseModule obj) {
        removeToManyTarget("courseModules", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CourseModule> getCourseModules() {
        return (List<CourseModule>)readProperty("courseModules");
    }


    public void addToWaitingLists(WaitingList obj) {
        addToManyTarget("waitingLists", obj, true);
    }
    public void removeFromWaitingLists(WaitingList obj) {
        removeToManyTarget("waitingLists", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WaitingList> getWaitingLists() {
        return (List<WaitingList>)readProperty("waitingLists");
    }


}
