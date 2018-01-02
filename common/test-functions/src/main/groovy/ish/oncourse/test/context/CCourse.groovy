package ish.oncourse.test.context

import ish.common.types.CourseEnrolmentType
import ish.oncourse.model.College
import ish.oncourse.model.Course
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/17/17.
 */
class CCourse {
    private ObjectContext objectContext
    Course course

    Map<String, CCourseClass> cClasses = new HashMap<>()

    private CCourse (){}

    CCourseClass cCourseClass (String code){
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        cClasses.put(cClass.courseClass.code, cClass)
        cClass
    }

    static CCourse instance(ObjectContext context, College college, String name, String code) {
        CCourse cCourse = new CCourse()

        cCourse.objectContext = context
        cCourse.course = cCourse.objectContext.newObject(Course)
        cCourse.course.college = cCourse.objectContext.localObject(college)
        cCourse.course.name = name
        cCourse.course.code = code
        cCourse.course.detail = name + " details"
        cCourse.course.enrolmentType = CourseEnrolmentType.OPEN_FOR_ENROLMENT
        cCourse
    }

    /**
     * class duration between start and end is 10 days for default    
     */
    CCourse withClass(String code, Date classStartDate) {
        withClass(code, classStartDate, 10)
    }

    CCourse withClass(String code, Date classStartDate, int durationDays) {
        withClass(code, classStartDate, classStartDate + durationDays)
        this
    }
    
    CCourse withClass(String code, Date startDate, Date endDate) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).startDate(startDate).endDate(endDate)
        cClasses.put(cClass.courseClass.code, cClass)
        this
    }

    CCourse withCancelledClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).cancelled(true)
        cClasses.put(cClass.courseClass.code, cClass)
        this
    }

    CCourse withDistantClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).isDistantLearningCourse(true)
        cClasses.put(cClass.courseClass.code, cClass)
        this
    }

    CCourse withInactiveClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).active(false)
        cClasses.put(cClass.courseClass.code, cClass)
        this
    }

    CCourse withEnrolDisabledClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).active(false).isWebVisible(false)
        cClasses.put(cClass.courseClass.code, cClass)
        this
    }

    CCourse withWebInvisibleClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).isWebVisible(false)
        cClasses.put(cClass.courseClass.code, cClass)
        this
    }
    
    CCourse detail(String detail) {
        course.setDetail(detail)
        this
    }

    CCourse build() {
        objectContext.commitChanges()
        this
    }
}
