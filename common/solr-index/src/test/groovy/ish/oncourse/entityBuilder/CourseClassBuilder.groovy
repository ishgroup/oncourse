package ish.oncourse.entityBuilder

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Room
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CourseClassBuilder {
    private CourseClass courseClass
    private ObjectContext objectContext

    private CourseClassBuilder(ObjectContext context){
        objectContext = context
    }

    CourseClassBuilder course(Course course){
        courseClass.course = objectContext.localObject(course)
        this
    }

    CourseClassBuilder isDistantLearningCourse(boolean isDistantLearning){
        courseClass.isDistantLearningCourse = isDistantLearning
        this
    }

    CourseClassBuilder isWebVisible(boolean isVisible){
        courseClass.isWebVisible = isVisible
        this
    }

    CourseClassBuilder cancelled(boolean isCancelled){
        courseClass.cancelled = isCancelled
        this
    }

    CourseClassBuilder startDate(Date startDate){
        courseClass.startDate = startDate
        this
    }

    CourseClassBuilder room(Room room){
        courseClass.room = room
        this
    }

    CourseClassBuilder newDefaultRoom(){
        Site site = SiteBuilder.instance(objectContext, courseClass.college).build()
        courseClass.room = RoomBuilder.instance(objectContext, site).build()
        this
    }

    CourseClass build(){
        objectContext.commitChanges()
        courseClass
    }

    static CourseClassBuilder instance(ObjectContext context, String code, Course course){
        CourseClassBuilder builder = new CourseClassBuilder(context)
        builder.createDefaultClass(code, course)
        builder
    }

    private CourseClassBuilder createDefaultClass(String code, Course course){
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
}
