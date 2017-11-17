package ish.oncourse.test.context

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CCourseClass {
    private CourseClass courseClass
    private ObjectContext objectContext

    private CCourseClass(ObjectContext context){
        objectContext = context
    }

    CCourseClass course(Course course){
        courseClass.course = objectContext.localObject(course)
        this
    }

    CCourseClass isDistantLearningCourse(boolean isDistantLearning){
        courseClass.isDistantLearningCourse = isDistantLearning
        this
    }

    CCourseClass isWebVisible(boolean isVisible){
        courseClass.isWebVisible = isVisible
        this
    }

    CCourseClass cancelled(boolean isCancelled){
        courseClass.cancelled = isCancelled
        this
    }

    CCourseClass endDate(Date endDate){
        courseClass.endDate = endDate
        this
    }

    CCourseClass startDate(Date startDate){
        courseClass.startDate = startDate
        this
    }

    CCourseClass build(){
        objectContext.commitChanges()
        this
    }

    static CCourseClass instance(ObjectContext context, String code, Course course){
        CCourseClass builder = new CCourseClass(context)
        builder.createDefaultClass(code, course)
        builder
    }

    private CCourseClass createDefaultClass(String code, Course course){
        courseClass = objectContext.newObject(CourseClass)
        courseClass.college = objectContext.localObject(course.college)
        courseClass.course = objectContext.localObject(course)
        courseClass.code = code
        courseClass.isWebVisible = true
        courseClass.cancelled = false
        courseClass.isDistantLearningCourse = false
        courseClass.isActive = true
        courseClass.maximumPlaces = 100
        courseClass.startDate = new Date()
        this
    }

    CourseClass get() {
        courseClass
    }
}
