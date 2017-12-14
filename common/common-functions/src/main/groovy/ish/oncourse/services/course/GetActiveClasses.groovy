package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.services.courseclass.CheckClassAge
import ish.oncourse.services.courseclass.ClassAge

import java.util.concurrent.Callable

class GetActiveClasses {

    private Course course
    private ClassAge classAge

    private GetActiveClasses() {}

    static GetActiveClasses valueOf(Course course, Callable<ClassAge> classAge) {
        GetActiveClasses getter = new GetActiveClasses()
        getter.course = course
        getter.classAge = classAge.call()
        return getter
    }

    List<CourseClass> get() {
        CheckClassAge checkClassAge = new CheckClassAge().classAge(classAge)
        List<CourseClass> currentClasses = course.courseClasses.findAll {
            it.isWebVisible && !it.cancelled && checkClassAge.courseClass(it).check()
        }
        return currentClasses
    }
}
