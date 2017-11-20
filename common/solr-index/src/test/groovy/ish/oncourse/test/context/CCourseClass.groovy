package ish.oncourse.test.context

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CCourseClass {
    CourseClass courseClass
    private ObjectContext objectContext

    private CCourseClass(){}

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
        CCourseClass builder = new CCourseClass()

        builder.objectContext = context
        builder.courseClass = builder.objectContext.newObject(CourseClass)
        builder.courseClass.college = builder.objectContext.localObject(course.college)
        builder.courseClass.course = builder.objectContext.localObject(course)
        builder.courseClass.code = code
        builder.courseClass.isWebVisible = true
        builder.courseClass.cancelled = false
        builder.courseClass.isDistantLearningCourse = false
        builder.courseClass.isActive = true
        builder.courseClass.maximumPlaces = 100
        builder.courseClass.startDate = new Date()
        builder
    }
}
