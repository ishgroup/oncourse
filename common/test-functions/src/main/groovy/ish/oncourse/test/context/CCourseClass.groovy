package ish.oncourse.test.context

import ish.math.Money
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

    List<CSession> sessions = new ArrayList<>()

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

    CCourseClass withSession(Date date = new Date()) {
        CSession session = CSession.instance(objectContext, courseClass).date(date)
        sessions.add(session)
        setClassStartEndDates()
        this
    }
    
    /**
     * creates Session with Start date = Current date/time + daysFromNow
     * @param daysFromNow
     * @return
     */
    CCourseClass withSession(int daysFromNow) {
        sessions.add(CSession.instance(objectContext, courseClass).date(new Date() + daysFromNow))
        setClassStartEndDates()
        this
    }


    private CCourseClass setClassStartEndDates() {
        courseClass.startDate = sessions.sort {it.session.startDate}.first().session.startDate
        courseClass.endDate = sessions.sort {it.session.startDate}.last().session.startDate
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
        builder.cRoom()
        builder
    }

    CCourseClass feeExTax(int feeExTax) {
        courseClass.feeExGst = new Money(feeExTax)
        this
    }

    CCourseClass feeTax(int feeTaxPercent) {
        if (feeTaxPercent >= 0 && courseClass.feeExGst.isGreaterThan(Money.ZERO))
        courseClass.feeGst = courseClass.feeExGst * (feeTaxPercent / 100L)
        this
    }
}
