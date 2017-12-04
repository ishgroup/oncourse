package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.services.courseclass.CheckClassAge
import ish.oncourse.services.courseclass.ClassAge

class GetCurrentClasses {
    
    private Course course
    private String age
    private String ageType

    private GetCurrentClasses() {}

    static GetCurrentClasses valueOf(Course course, String age, String ageType) {
        GetCurrentClasses currentGetter = new GetCurrentClasses()
        currentGetter.course = course
        currentGetter.age = age
        currentGetter.ageType = ageType
        return currentGetter
    }

    List<CourseClass> get() {
        CheckClassAge classAge = new CheckClassAge().classAge(ClassAge.valueOf(age, ageType))

        List<CourseClass> currentClasses = course.courseClasses.findAll { it.isWebVisible && !it.cancelled && classAge.courseClass(it).check() }
        return currentClasses
    }
}
