package ish.oncourse.entityBuilder

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CourseClassBuilder {
    private CourseClass courseClass
    private ObjectContext objectContext

    CourseClassBuilder createDefaultClass(String code, College college){
        courseClass = objectContext.newObject(CourseClass)
        courseClass.college = objectContext.localObject(college)
        courseClass.code = code
        courseClass.isWebVisible = true
        courseClass.cancelled = false
        courseClass.isDistantLearningCourse = false
        courseClass.isActive = true
        courseClass.maximumPlaces = 100
        courseClass.startDate = new Date()
        this
    }

    CourseClassBuilder addToCourse(Course course){
        courseClass.course = objectContext.localObject(course)
        this
    }

    CourseClassBuilder setDistantLearning(boolean isDistantLearning){
        courseClass.isDistantLearningCourse = isDistantLearning
        this
    }

    CourseClassBuilder setVisible(boolean isVisible){
        courseClass.isWebVisible = isVisible
        this
    }

    CourseClassBuilder setCancelled(boolean isCancelled){
        courseClass.cancelled = isCancelled
        this
    }

    CourseClassBuilder setStartDate(Date startDate){
        courseClass.startDate = startDate
        this
    }

    CourseClass build(){
        objectContext.commitChanges()
        courseClass
    }
}
