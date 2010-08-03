package ish.oncourse.model.auto;

import java.util.List;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Taggable;

/**
 * Class _Course was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Course extends Taggable {

    public static final String VETCOURSE_PROPERTY = "VETCourse";
    public static final String ALLOW_WAITING_LIST_PROPERTY = "allowWaitingList";
    public static final String CODE_PROPERTY = "code";
    public static final String DETAIL_PROPERTY = "detail";
    public static final String DETAIL_TEXTILE_PROPERTY = "detailTextile";
    public static final String FIELD_OF_EDUCATION_PROPERTY = "fieldOfEducation";
    public static final String NAME_PROPERTY = "name";
    public static final String NOMINAL_HOURS_PROPERTY = "nominalHours";
    public static final String QUALIFICATION_ID_PROPERTY = "qualificationId";
    public static final String SEARCH_TEXT_PROPERTY = "searchText";
    public static final String SUFFICIENT_FOR_QUALIFICATION_PROPERTY = "sufficientForQualification";
    public static final String WEB_VISIBLE_PROPERTY = "webVisible";
    public static final String CLASSES_PROPERTY = "classes";

    public static final String ID_PK_COLUMN = "id";

    public void setVETCourse(boolean VETCourse) {
        writeProperty("VETCourse", VETCourse);
    }
	public boolean isVETCourse() {
        Boolean value = (Boolean)readProperty("VETCourse");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setAllowWaitingList(boolean allowWaitingList) {
        writeProperty("allowWaitingList", allowWaitingList);
    }
	public boolean isAllowWaitingList() {
        Boolean value = (Boolean)readProperty("allowWaitingList");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setCode(String code) {
        writeProperty("code", code);
    }
    public String getCode() {
        return (String)readProperty("code");
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

    public void setSufficientForQualification(boolean sufficientForQualification) {
        writeProperty("sufficientForQualification", sufficientForQualification);
    }
	public boolean isSufficientForQualification() {
        Boolean value = (Boolean)readProperty("sufficientForQualification");
        return (value != null) ? value.booleanValue() : false;
    }

    public void setWebVisible(boolean webVisible) {
        writeProperty("webVisible", webVisible);
    }
	public boolean isWebVisible() {
        Boolean value = (Boolean)readProperty("webVisible");
        return (value != null) ? value.booleanValue() : false;
    }

    @SuppressWarnings("unchecked")
    public List<CourseClass> getClasses() {
        return (List<CourseClass>)readProperty("classes");
    }


}
