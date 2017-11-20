package ish.oncourse.test.context

import ish.oncourse.model.CourseClass
import ish.oncourse.model.Session
import ish.oncourse.model.Site
import org.apache.cayenne.ObjectContext

/**
 * Created by alex on 11/15/17.
 */
class CSession {
    private ObjectContext objectContext
    Session session

    private CSession(){}

    CSession startDate(Date date){
        session.startDate = date
        this
    }

    CRoom cRoom(Site site){
        CRoom cRoom = CRoom.instance(objectContext, site)
        session.room = cRoom.room
        cRoom
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
}
