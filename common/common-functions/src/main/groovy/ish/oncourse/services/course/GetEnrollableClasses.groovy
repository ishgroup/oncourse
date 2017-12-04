package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass

class GetEnrollableClasses {

    private Course course
    private String age
    private String ageType

    private GetEnrollableClasses() {}

    static GetEnrollableClasses valueOf(Course course, String age, String ageType) {
        GetEnrollableClasses getter = new GetEnrollableClasses()
        getter.course = course
        getter.age = age
        getter.ageType = ageType
        return getter
    }
    
    List<CourseClass> get() {
        List<CourseClass> currentClasses = GetCurrentClasses.valueOf(course, age, ageType).get()
        currentClasses.findAll { it.hasAvailableEnrolmentPlaces }
    }
}
