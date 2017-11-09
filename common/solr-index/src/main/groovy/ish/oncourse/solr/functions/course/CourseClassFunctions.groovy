package ish.oncourse.solr.functions.course

import ish.oncourse.model.*
import org.apache.cayenne.ResultIterator

import static ish.oncourse.model.auto._CourseClass.TUTOR_ROLES
import static ish.oncourse.model.auto._Room.COURSE_CLASSES
import static ish.oncourse.model.auto._Session.COURSE_CLASS
import static ish.oncourse.model.auto._Site.ROOMS
import static java.lang.Boolean.TRUE
import static org.apache.cayenne.query.ObjectSelect.query

/**
 * User: akoiro
 * Date: 9/11/17
 */
class CourseClassFunctions {
    public static final Closure<ResultIterator<Session>> Sessions = { CourseClass courseClass ->
        query(Session).where(COURSE_CLASS.eq(courseClass)).orderBy(Session.START_DATE.asc()).iterator(courseClass.objectContext)
    }


    public static final Closure<ResultIterator<Contact>> Contacts = { CourseClass courseClass ->
        return query(Contact).where(Contact.TUTOR.dot(TUTOR_ROLES).dot(TutorRole.COURSE_CLASS).eq(courseClass)).iterator(courseClass.objectContext)
    }

    public static final Closure<ResultIterator<Site>> CourseClassSites = { CourseClass courseClass ->
        query(Site).where(Site.IS_WEB_VISIBLE.eq(TRUE)).and(ROOMS.dot(COURSE_CLASSES).eq(courseClass)).iterator(courseClass.objectContext)

    }

    public static final Closure<ResultIterator<Site>> SessionSites = { CourseClass courseClass ->
        query(Site).where(Site.IS_WEB_VISIBLE.eq(TRUE)).and(ROOMS.dot(Room.SESSIONS).dot(COURSE_CLASS).eq(courseClass)).iterator(courseClass.objectContext)
    }

}
