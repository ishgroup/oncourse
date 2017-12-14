package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.services.courseclass.ClassAge
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.Preferences

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
                String age = new GetPreference(course.getCollege(), Preferences.HIDE_CLASS_ON_WEB_AGE, course.getObjectContext()).getValue()
                String type = new GetPreference(course.getCollege(), Preferences.HIDE_CLASS_ON_WEB_AGE_TYPE, course.getObjectContext()).getValue()
                return ClassAge.valueOf(age, type)
            }
        }
        return GetActiveClasses.valueOf(course, classAge).get()
    }
}
