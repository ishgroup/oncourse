package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass

class GetEnrollableClasses {

    private Course course

    GetEnrollableClasses(Course course) {
        this.course = course
    }
    
    List<CourseClass> get() {
        List<CourseClass> currentClasses = new GetCurrentClasses(course).get()
        currentClasses.findAll { it.hasAvailableEnrolmentPlaces }
    }
}
