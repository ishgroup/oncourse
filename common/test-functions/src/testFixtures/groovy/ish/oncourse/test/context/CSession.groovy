package ish.oncourse.test.context

import ish.oncourse.model.CourseClass
import ish.oncourse.model.Room
import ish.oncourse.model.Session
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CSession {
    ObjectContext objectContext
    Session session

    /**
     * session's start date - 'date'
     * session's end date - 'date' + 1 hour
     */
    CSession date(Date date) {
        startDate(date)

        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.HOUR_OF_DAY, 1)

        endDate(cal.getTime())
        this
    }

    CSession startDate(Date date){
        session.startDate = date
        this
    }

    CSession endDate(Date date){
        session.endDate = date
        this
    }

    CRoom newRoom(Site site){
        CRoom cRoom = CRoom.instance(objectContext, site)
        session.room = cRoom.room
        cRoom
    }

    CRoom newRoom(){
        CRoom cRoom = CRoom.instance(objectContext, session.college)
        session.room = cRoom.room
        cRoom
    }

    CRoom newRoomWithSiteLocation(BigDecimal longitude, BigDecimal latitude) {
        CRoom cRoom = CRoom.instance(objectContext, session.college)
        cRoom.location(longitude, latitude)
        session.room = cRoom.room
        cRoom
    }

    CSession room(Room room){
        session.room = room
        this
    }

    CSession build(){
        objectContext.commitChanges()
        this
    }

    static CSession instance(ObjectContext context, CourseClass courseClass){
        CSession builder = new CSession()
        builder.objectContext = context

        builder.session = builder.objectContext.newObject(Session)
        builder.session.courseClass = courseClass
        builder.session.college = courseClass.college
        builder
    }

    CSession withRoomWithTimeZonedSite(String timezone) {
        session.room = CRoom.instance(objectContext, session.college).timeZone(timezone).room
        this
    }

    void delete() {
        objectContext.deleteObject(session)
        objectContext.commitChanges()
    }
}
