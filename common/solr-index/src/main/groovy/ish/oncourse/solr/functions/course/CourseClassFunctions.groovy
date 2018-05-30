package ish.oncourse.solr.functions.course

import ish.oncourse.model.*
import org.apache.cayenne.ResultIterator
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.model.auto._CourseClass.TUTOR_ROLES
import static ish.oncourse.model.auto._Session.COURSE_CLASS
import static ish.oncourse.model.auto._Site.ROOMS
import static java.lang.Boolean.TRUE
import static org.apache.cayenne.query.ObjectSelect.query

/**
 * User: akoiro
 * Date: 9/11/17
 */
class CourseClassFunctions {

    static final ObjectSelect<Session> sessionsQuery(CourseClass courseClass) {
        query(Session).where(COURSE_CLASS.eq(courseClass)).orderBy(Session.START_DATE.asc())
    }

    static final ObjectSelect<Contact> contactsQuery(CourseClass courseClass) {
        query(Contact).where(Contact.TUTOR.dot(TUTOR_ROLES).dot(TutorRole.COURSE_CLASS).eq(courseClass))
    }


    static final ObjectSelect<Site> sessionSitesQuery(CourseClass courseClass) {
        query(Site).where(Site.IS_WEB_VISIBLE.eq(TRUE)).and(ROOMS.dot(Room.SESSIONS).dot(COURSE_CLASS).eq(courseClass))
    }


    public static final Closure<ResultIterator<Session>> Sessions = { CourseClass courseClass ->
        sessionsQuery(courseClass).iterator(courseClass.objectContext)
    }

    public static final Closure<ResultIterator<Contact>> Contacts = { CourseClass courseClass ->
        return contactsQuery(courseClass).iterator(courseClass.objectContext)
    }

    public static final Closure<Iterable<Site>> CourseClassSites = { CourseClass courseClass ->
        Site site = courseClass.room?.site
        return site ? Collections.singleton(site) : Collections.EMPTY_SET
    }

    public static final Closure<ResultIterator<Site>> SessionSites = { CourseClass courseClass ->
        sessionSitesQuery(courseClass).iterator(courseClass.objectContext)
    }
}
