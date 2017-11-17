package ish.oncourse.entityBuilder

import ish.oncourse.model.CourseClass
import ish.oncourse.model.Room
import ish.oncourse.model.Session
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class SessionBuilder {
    private ObjectContext objectContext
    private Session session

    private SessionBuilder(ObjectContext context){
        objectContext = context
    }

    SessionBuilder startDate(Date date){
        session.startDate = date
        this
    }

    SessionBuilder room(Room room){
        session.setRoom(room)
        this
    }

    SessionBuilder newDefaultRoomWithSite(Site site){
        session.setRoom(RoomBuilder.instance(objectContext, site).build())
        this
    }

    SessionBuilder newDefaultRoom(){
        Site site = SiteBuilder.instance(objectContext, session.college).build()
        session.setRoom(RoomBuilder.instance(objectContext, site).build())
        this
    }



    Session build(){
        objectContext.commitChanges()
        session
    }

    static SessionBuilder instance(ObjectContext context, CourseClass courseClass){
        SessionBuilder builder = new SessionBuilder(context)
        builder.createDefaultSession(courseClass)
        builder
    }

    private SessionBuilder createDefaultSession(CourseClass courseClass){
        session = objectContext.newObject(Session)
        session.courseClass = courseClass
        session.college = courseClass.college
        this
    }
}
