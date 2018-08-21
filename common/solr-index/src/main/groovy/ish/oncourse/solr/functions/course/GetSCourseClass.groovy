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

import static ish.oncourse.solr.functions.course.DateFunctions.toTimeZone

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
        scc.id = context.courseClass.id
        scc.collegeId = context.courseClass.college.id
        scc.courseId = context.courseClass.course.id
        scc.classStart = getStartDate(type)
        scc.classEnd = getEndDate(type)
        scc.classCode = "${context.courseClass.course.code}-${context.courseClass.code}"
        scc.price = context.courseClass.feeExGst.toPlainString()

        List<SSession> sessions = toSSessions(context.courseClass, this.context.sessions)
        scc.when.addAll(sessions.collect {"${it.dayName} ${it.dayType} ${it.dayTime}"}.unique())
        
        List<SContact> contacts = toSContacts(context.courseClass, this.context.contacts)
        scc.tutorId = contacts.tutorId.unique().findAll { it != null }
        scc.tutor = contacts.name.unique().findAll { it != null }
        
        List<SSite> sites = toSSites(context.courseClass, this.context.courseClassSites)
        sites.addAll(toSSites(context.courseClass, this.context.sessionSites))
        sites = sites.unique()

        scc.suburb = sites.suburb.unique().findAll { it != null }
        scc.postcode = sites.postcode.unique().findAll { it != null }
        scc.location = sites.location.unique().findAll { it != null }
        scc.siteId = sites.id

        return scc
    }

    private Date getStartDate(ClassType type) {
        switch (type) {
            case ClassType.distantLearning:
                return DateUtils.addDays(context.current, 1)
            case ClassType.withOutSessions:
                return DateUtils.addYears(context.current, 100)
            case ClassType.regular:
                return toTimeZone(context.courseClass.startDate, context.courseClass.timeZone)
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
                return toTimeZone(context.courseClass.endDate, context.courseClass.timeZone)
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

    static List<SSite> toSSites(CourseClass courseClass, Closure<Iterable<Site>> getSites) {
        Iterable<Site> sites = getSites.call(courseClass)
        try {
            return Observable.fromIterable(sites).map({ s -> SiteFunctions.getSSite(s) }).toList().blockingGet().unique()
        } finally {
            if (sites instanceof ResultIterator<Site>)
                sites.close()
        }
    }

}
