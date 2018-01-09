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

    List<CCourseClass> classes = new LinkedList<>()

    private CCourse() {}

    CCourseClass newCourseClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        classes.add(cClass)
        cClass
    }

    CCourseClass getCourseClassBy(String code) {
        return classes.stream().filter({ (it.courseClass.code == code) }).findFirst().get()
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
        cCourse.course.isWebVisible = true
        cCourse
    }
    
    CCourse withClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        classes.add(cClass)
        this
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
        classes.add(cClass)
        this
    }

    CCourse withCancelledClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).cancelled(true)
        classes.add(cClass)
        this
    }

    CCourse withDistantClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).isDistantLearningCourse(true)
        classes.add(cClass)
        this
    }

    CCourse withInactiveClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).active(false)
        classes.add(cClass)
        this
    }

    CCourse withEnrolDisabledClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).active(false).isWebVisible(false)
        classes.add(cClass)
        this
    }

    CCourse withWebInvisibleClass(String code) {
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course).isWebVisible(false)
        classes.add(cClass)
        this
    }

    CCourse detail(String detail) {
        course.setDetail(detail)
        this
    }

    CCourse isWebVisible(boolean isVisible){
        course.isWebVisible = isVisible
        this
    }

    CCourse build() {
        objectContext.commitChanges()
        this
    }
}
