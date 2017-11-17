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
    private Course course

    Map<String, CCourseClass> cClasses = new HashMap<>()

    private CCourse (ObjectContext context){
        objectContext = context
    }

    CCourseClass cCourseClass (String code){
        CCourseClass cClass = CCourseClass.instance(objectContext, code, course)
        cClasses.put(cClass.get().code, cClass)
        objectContext.commitChanges()
        cClass
    }

    static CCourse instance(ObjectContext context, College college, String name, String code) {
        CCourse cCourse = new CCourse(context)
        cCourse.createDefaultCourse(college, name, code)
    }

    private CCourse createDefaultCourse(College college, String name, String code){
        course = objectContext.newObject(Course)
        course.college = objectContext.localObject(college)
        course.name = name
        course.code = code
        course.enrolmentType = CourseEnrolmentType.OPEN_FOR_ENROLMENT
        this
    }

    Course get(){
        course
    }
}
