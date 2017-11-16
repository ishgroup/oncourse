package ish.oncourse.solr.functions.course

import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Session
import ish.oncourse.model.Site
import org.apache.cayenne.ResultIterator

import static ish.oncourse.solr.functions.course.CourseClassFunctions.*

/**
 * User: akoiro
 * Date: 24/10/17
 */
class CourseClassContext {
    CourseClass courseClass
    Date current = new Date()
    Closure<ResultIterator<Session>> sessions = Sessions
    Closure<ResultIterator<Contact>> contacts = Contacts
    Closure<Iterable<Site>> courseClassSites = CourseClassSites
    Closure<ResultIterator<Site>> sessionSites = SessionSites
}
