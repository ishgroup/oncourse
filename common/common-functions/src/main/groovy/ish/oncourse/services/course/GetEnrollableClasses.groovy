package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.services.courseclass.ClassAge
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.Preferences

import java.util.concurrent.Callable

/**
 * Returns active classes which students can be enrolled
 */
class GetEnrollableClasses {

    private Course course
    private Callable<ClassAge> classAge

    private GetEnrollableClasses() {}

    static GetEnrollableClasses valueOf(Course course) {
        GetEnrollableClasses getter = new GetEnrollableClasses()
        getter.course = course
        getter.classAge = {
            String age = new GetPreference(course.getCollege(), Preferences.STOP_WEB_ENROLMENTS_AGE, course.getObjectContext()).getValue()
            String type = new GetPreference(course.getCollege(), Preferences.STOP_WEB_ENROLMENTS_AGE_TYPE, course.getObjectContext()).getValue()
            return ClassAge.valueOf(age, type)
        }
        return getter
    }

    protected GetEnrollableClasses classAge(Callable<ClassAge> classAge) {
        this.classAge = classAge
        return this
    }


    List<CourseClass> get() {
        List<CourseClass> currentClasses = GetActiveClasses.valueOf(course, classAge).get()
        currentClasses.findAll { it.hasAvailableEnrolmentPlaces }
    }
}
