package ish.oncourse.solr.functions.course

import io.reactivex.Observable
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Session
import ish.oncourse.model.Site
import ish.oncourse.solr.model.SContact
import ish.oncourse.solr.model.SCourseClass
import ish.oncourse.solr.model.SSession
import ish.oncourse.solr.model.SSite
import org.apache.cayenne.ResultIterator
import org.apache.commons.lang3.time.DateUtils

/**
 * User: akoiro
 * Date: 3/10/17
 */
class GetSCourseClass {
    private CourseClassContext context

    GetSCourseClass(CourseClassContext context) {
        this.context = context
    }

    SCourseClass get() {
        ClassType type = ClassType.valueOf(context.courseClass, context.current)
        SCourseClass scc = new SCourseClass()
        scc.classStart = getStartDate(type)
        scc.classEnd = getEndDate(type)
        scc.classCode = "${context.courseClass.course.code}-${context.courseClass.code}"
        scc.price = context.courseClass.feeExGst.toPlainString()

        scc.sessions = toSSessions(context.courseClass, this.context.sessions)
        scc.contacts = toSContacts(context.courseClass, this.context.contacts)
        scc.sites = toSSites(context.courseClass, this.context.sites)
        return scc
    }

    private Date getStartDate(ClassType type) {
        switch (type) {
            case ClassType.distantLearning:
                return DateUtils.addDays(context.current, 1)
            case ClassType.withOutSessions:
                return DateUtils.addYears(context.current, 100)
            case ClassType.regular:
                return context.courseClass.startDate
            default:
                throw new IllegalArgumentException("Unsupported type:  $type ")
        }
    }

    private Date getEndDate(ClassType type) {
        switch (type) {
            case ClassType.distantLearning:
                return DateUtils.addYears(context.current, 100)
            case ClassType.withOutSessions:
                return DateUtils.addYears(context.current, 100)
            case ClassType.regular:
                return context.courseClass.endDate
            default:
                throw new IllegalArgumentException("Unsupported type:  $type ")
        }
    }


    static List<SSession> toSSessions(CourseClass courseClass, Closure<ResultIterator<Session>> getSessions) {
        ResultIterator<Session> sessions = getSessions.call(courseClass)
        try {
            return Observable.fromIterable(sessions).map({ s -> SessionFunctions.getSSession(s) }).toList().blockingGet().unique()
        } finally {
            sessions.close()
        }
    }

    static List<SContact> toSContacts(CourseClass courseClass, Closure<ResultIterator<Contact>> getContacts) {
        ResultIterator<Contact> contacts = getContacts.call(courseClass)
        try {
            return Observable.fromIterable(contacts).map({ c -> ContactFunctions.getSContact(c) }).toList().blockingGet().unique()
        } finally {
            contacts.close()
        }
    }

    static List<SSite> toSSites(CourseClass courseClass, Closure<ResultIterator<Site>> getSites) {
        ResultIterator<Site> sites = getSites.call(courseClass)
        try {
            return Observable.fromIterable(sites).map({ s -> SiteFunctions.getSSite(s) }).toList().blockingGet().unique()
        } finally {
            sites.close()
        }
    }

}
