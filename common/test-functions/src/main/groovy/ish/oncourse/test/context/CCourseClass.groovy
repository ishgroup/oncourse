package ish.oncourse.test.context

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Room
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CCourseClass {
    CourseClass courseClass
    private ObjectContext objectContext

    List<CSession> sessions = new LinkedList<>()

    private CCourseClass(){}

    CCourseClass course(Course course){
        courseClass.course = objectContext.localObject(course)
        this
    }

    CRoom cRoom(Site site){
        CRoom cRoom = CRoom.instance(objectContext, site)
        courseClass.room = cRoom.room
        cRoom
    }

    CRoom cRoom(){
        CRoom cRoom = CRoom.instance(objectContext, courseClass.college)
        courseClass.room = cRoom.room
        cRoom
    }

    CCourseClass room(Room room){
        courseClass.room = room
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

    CCourseClass active(boolean isActive){
        courseClass.isActive = isActive
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

    CCourseClass withSession(Date date) {
        CSession session = CSession.instance(objectContext, courseClass).date(date)
        sessions.add(session)
        this
    }
    
    /**
     * creates Session with Start date = Current date/time + daysFromNow
     * @param daysFromNow
     * @return
     */
    CCourseClass withSession(int daysFromNow) {
        CSession.instance(objectContext, courseClass).date(new Date() + daysFromNow)
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
        builder.cRoom()
        builder
    }
}
