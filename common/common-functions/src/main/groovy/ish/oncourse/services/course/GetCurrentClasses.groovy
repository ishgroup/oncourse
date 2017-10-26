package ish.oncourse.services.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.services.courseclass.CheckClassAge

class GetCurrentClasses {
    
    private Course course

    GetCurrentClasses(Course course){
        this.course = course
    }

    List<CourseClass> get() {
        CheckClassAge classAge = new CheckClassAge().college(course.college)
        return course.courseClasses.findAll { it.isWebVisible && !it.cancelled && classAge.courseClass(it).check() }
    }
}
