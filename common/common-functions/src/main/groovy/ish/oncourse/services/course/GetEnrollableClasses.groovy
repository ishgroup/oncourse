package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.services.courseclass.CheckClassAge
import ish.oncourse.services.courseclass.ClassAge
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.Preferences

class GetEnrollableClasses {

    private Course course

    GetEnrollableClasses(Course course) {
        this.course = course
    }
    
    List<CourseClass> get() {
        String age = new GetPreference(course.college, Preferences.HIDE_CLASS_ON_WEB_AGE, course.college.objectContext).value
        String type = new GetPreference(course.college, Preferences.HIDE_CLASS_ON_WEB_AGE_TYPE, course.college.objectContext).value

        CheckClassAge classAge = new CheckClassAge().classAge(ClassAge.valueOf(age, type))

        List<CourseClass> currentClasses = course.courseClasses.findAll { it.isWebVisible && !it.cancelled && classAge.courseClass(it).check() }
        currentClasses.findAll { it.hasAvailableEnrolmentPlaces }
    }
}
