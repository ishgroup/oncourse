package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.services.courseclass.ClassAge
import ish.oncourse.services.courseclass.GetHideOnWebClassAge

import java.util.concurrent.Callable

/**
 * returns only classes which should be visible on /courses, /course page
 */
class GetWebVisibleClasses {

    private Callable<ClassAge> classAge

    private Course course

    private GetWebVisibleClasses() {}

    static GetWebVisibleClasses valueOf(Course course) {
        GetWebVisibleClasses get = new GetWebVisibleClasses()
        get.course = course
        return get
    }

    protected GetWebVisibleClasses classAge(Callable<ClassAge> classAge) {
        this.classAge = classAge
        return this
    }

    List<CourseClass> get() {
        if (!classAge) {
            classAge = {
                return new GetHideOnWebClassAge().college(course.getCollege()).get()
            }
        }
        return GetActiveClasses.valueOf(course, classAge).get()
    }
}
