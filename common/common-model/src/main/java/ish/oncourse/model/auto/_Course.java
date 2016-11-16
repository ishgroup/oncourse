package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.common.types.CourseEnrolmentType;
import ish.oncourse.model.Application;
import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.CourseCourseRelation;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.FieldConfigurationScheme;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.VoucherProductCourse;
import ish.oncourse.model.WaitingList;

/**
 * Class _Course was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Course extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ALLOW_WAITING_LIST_PROPERTY = "allowWaitingList";
    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CODE_PROPERTY = "code";
    public static final String CREATED_PROPERTY = "created";
    public static final String DETAIL_PROPERTY = "detail";
    public static final String DETAIL_TEXTILE_PROPERTY = "detailTextile";
    public static final String ENROLMENT_TYPE_PROPERTY = "enrolmentType";
    public static final String FIELD_OF_EDUCATION_PROPERTY = "fieldOfEducation";
    public static final String IS_SUFFICIENT_FOR_QUALIFICATION_PROPERTY = "isSufficientForQualification";
    public static final String IS_VETCOURSE_PROPERTY = "isVETCourse";
    public static final String IS_WEB_VISIBLE_PROPERTY = "isWebVisible";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String NOMINAL_HOURS_PROPERTY = "nominalHours";
    public static final String SEARCH_TEXT_PROPERTY = "searchText";
    public static final String APPLICATIONS_PROPERTY = "applications";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_CLASSES_PROPERTY = "courseClasses";
    public static final String COURSE_MODULES_PROPERTY = "courseModules";
    public static final String FIELD_CONFIGURATION_SCHEMA_PROPERTY = "fieldConfigurationSchema";
    public static final String FROM_COURSES_PROPERTY = "fromCourses";
    public static final String QUALIFICATION_PROPERTY = "qualification";
    public static final String TO_COURSES_PROPERTY = "toCourses";
    public static final String VOUCHER_PRODUCT_COURSES_PROPERTY = "voucherProductCourses";
    public static final String WAITING_LISTS_PROPERTY = "waitingLists";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Boolean> ALLOW_WAITING_LIST = new Property<Boolean>("allowWaitingList");
    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<String> CODE = new Property<String>("code");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> DETAIL = new Property<String>("detail");
    public static final Property<String> DETAIL_TEXTILE = new Property<String>("detailTextile");
    public static final Property<CourseEnrolmentType> ENROLMENT_TYPE = new Property<CourseEnrolmentType>("enrolmentType");
    public static final Property<String> FIELD_OF_EDUCATION = new Property<String>("fieldOfEducation");
    public static final Property<Boolean> IS_SUFFICIENT_FOR_QUALIFICATION = new Property<Boolean>("isSufficientForQualification");
    public static final Property<Boolean> IS_VETCOURSE = new Property<Boolean>("isVETCourse");
    public static final Property<Boolean> IS_WEB_VISIBLE = new Property<Boolean>("isWebVisible");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<Float> NOMINAL_HOURS = new Property<Float>("nominalHours");
    public static final Property<String> SEARCH_TEXT = new Property<String>("searchText");
    public static final Property<List<Application>> APPLICATIONS = new Property<List<Application>>("applications");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<List<CourseClass>> COURSE_CLASSES = new Property<List<CourseClass>>("courseClasses");
    public static final Property<List<CourseModule>> COURSE_MODULES = new Property<List<CourseModule>>("courseModules");
    public static final Property<FieldConfigurationScheme> FIELD_CONFIGURATION_SCHEMA = new Property<FieldConfigurationScheme>("fieldConfigurationSchema");
    public static final Property<List<CourseCourseRelation>> FROM_COURSES = new Property<List<CourseCourseRelation>>("fromCourses");
    public static final Property<Qualification> QUALIFICATION = new Property<Qualification>("qualification");
    public static final Property<List<CourseCourseRelation>> TO_COURSES = new Property<List<CourseCourseRelation>>("toCourses");
    public static final Property<List<VoucherProductCourse>> VOUCHER_PRODUCT_COURSES = new Property<List<VoucherProductCourse>>("voucherProductCourses");
    public static final Property<List<WaitingList>> WAITING_LISTS = new Property<List<WaitingList>>("waitingLists");

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

    public void setEnrolmentType(CourseEnrolmentType enrolmentType) {
        writeProperty("enrolmentType", enrolmentType);
    }
    public CourseEnrolmentType getEnrolmentType() {
        return (CourseEnrolmentType)readProperty("enrolmentType");
    }

    public void setFieldOfEducation(String fieldOfEducation) {
        writeProperty("fieldOfEducation", fieldOfEducation);
    }
    public String getFieldOfEducation() {
        return (String)readProperty("fieldOfEducation");
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

    public void setSearchText(String searchText) {
        writeProperty("searchText", searchText);
    }
    public String getSearchText() {
        return (String)readProperty("searchText");
    }

    public void addToApplications(Application obj) {
        addToManyTarget("applications", obj, true);
    }
    public void removeFromApplications(Application obj) {
        removeToManyTarget("applications", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Application> getApplications() {
        return (List<Application>)readProperty("applications");
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


    public void setFieldConfigurationSchema(FieldConfigurationScheme fieldConfigurationSchema) {
        setToOneTarget("fieldConfigurationSchema", fieldConfigurationSchema, true);
    }

    public FieldConfigurationScheme getFieldConfigurationSchema() {
        return (FieldConfigurationScheme)readProperty("fieldConfigurationSchema");
    }


    public void addToFromCourses(CourseCourseRelation obj) {
        addToManyTarget("fromCourses", obj, true);
    }
    public void removeFromFromCourses(CourseCourseRelation obj) {
        removeToManyTarget("fromCourses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CourseCourseRelation> getFromCourses() {
        return (List<CourseCourseRelation>)readProperty("fromCourses");
    }


    public void setQualification(Qualification qualification) {
        setToOneTarget("qualification", qualification, true);
    }

    public Qualification getQualification() {
        return (Qualification)readProperty("qualification");
    }


    public void addToToCourses(CourseCourseRelation obj) {
        addToManyTarget("toCourses", obj, true);
    }
    public void removeFromToCourses(CourseCourseRelation obj) {
        removeToManyTarget("toCourses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CourseCourseRelation> getToCourses() {
        return (List<CourseCourseRelation>)readProperty("toCourses");
    }


    public void addToVoucherProductCourses(VoucherProductCourse obj) {
        addToManyTarget("voucherProductCourses", obj, true);
    }
    public void removeFromVoucherProductCourses(VoucherProductCourse obj) {
        removeToManyTarget("voucherProductCourses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<VoucherProductCourse> getVoucherProductCourses() {
        return (List<VoucherProductCourse>)readProperty("voucherProductCourses");
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
