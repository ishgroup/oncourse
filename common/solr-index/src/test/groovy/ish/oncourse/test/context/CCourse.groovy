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
        objectContext.commitChanges()
        cClass
    }

    static CCourse instance(ObjectContext context, College college, String name, String code) {
        CCourse cCourse = new CCourse()

        cCourse.objectContext = context
        cCourse.course = cCourse.objectContext.newObject(Course)
        cCourse.course.college = cCourse.objectContext.localObject(college)
        cCourse.course.name = name
        cCourse.course.code = code
        cCourse.course.enrolmentType = CourseEnrolmentType.OPEN_FOR_ENROLMENT
        cCourse
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
