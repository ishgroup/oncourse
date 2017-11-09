package ish.oncourse.solr.functions.course

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.solr.model.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator
import org.apache.commons.lang3.time.DateUtils

import static ish.oncourse.solr.functions.course.CourseFunctions.Courses

/**
 * User: akoiro
 * Date: 5/10/17
 */
class SCourseFunctions {

    public static final Closure<SCourse> BuildSolrCourse = { Course course ->
        return new SCourse().with {
            it.id = course.id
            it.collegeId = course.college.id
            it.code = course.code
            it.name = course.name
            it.detail = course.detail
            it
        }
    }


    public static final Closure<Observable<SCourse>> SCourses = {
        Closure<ObjectContext> context, Date current = new Date() ->
            Flowable.fromIterable(Courses(context()))
                    .parallel()
                    .runOn(Schedulers.io())
                    .map({ Course c -> GetSCourse.call(new CourseContext(course: c, context: c.objectContext, current: current)) })
                    .sequential().toObservable()
    }


    public static final Closure<SCourse> GetSCourse = { CourseContext context ->
        SCourse result = BuildSolrCourse.call(context.course)
        ResultIterator<CourseClass> classes = context.courseClasses(context)
        result = Observable.fromIterable(classes).filter({ c -> c.hasAvailableEnrolmentPlaces })
                .flatMap({ cc -> context.applyCourseClass(result, context.courseClassContext.call(cc, context.current)) })
                .lastElement()
                .defaultIfEmpty(result)
                .blockingGet()

        if (result.classStart.isEmpty()) result.classStart.add(DateUtils.addYears(context.current, 100))
        if (result.classEnd.isEmpty()) result.classEnd.add(DateUtils.addYears(context.current, 100))
        result.startDate = result.classStart.sort({ d1, d2 -> (d1 <=> d2) }).first()


        classes.close()
        return result
    }


    static final Closure<Observable<SCourse>> ApplyCourseClass = {
        SCourse sc, CourseClassContext cc ->
            Observable.just(new GetSCourseClass(cc).get()).map({ SCourseClass scc ->
                if (!sc.classStart.contains(scc.classStart)) sc.classStart.add(scc.classStart)
                if (!sc.classEnd.contains(scc.classEnd)) sc.classEnd.add(scc.classEnd)
                if (!sc.price.contains(scc.price)) sc.price.add(scc.price)
                if (!sc.classCode.contains(scc.classCode)) sc.classCode.add(scc.classCode)
                addSessions(sc, scc.sessions)
                addContacts(sc, scc.contacts)
                addSites(sc, scc.sites)
                sc
            })
    }

    static final SCourse addSessions(SCourse sc, List<SSession> sessions) {
        Observable.fromIterable(sessions).map({ SSession s -> sc.when.add("${s.dayName} ${s.dayType} ${s.dayTime}"); s }).blockingSubscribe()
        sc.when.unique()
        sc
    }

    static final SCourse addContacts(SCourse sc, List<SContact> contacts) {
        Observable.fromIterable(contacts).map({ SContact c ->
            sc.tutorId.add(c.tutorId)
            sc.tutor.add(c.name)
            c
        }).blockingSubscribe()
        sc.tutorId.unique()
        sc.tutor.unique()
        sc
    }

    static final SCourse addSites(SCourse sc, List<SSite> sites) {
        Observable.fromIterable(sites).map({ SSite s ->
            sc.siteId.add(s.id)
            sc.suburb.add(s.suburb)
            sc.postcode.add(s.postcode)
            sc.location.add(s.location)
        }).blockingSubscribe()
        sc.siteId.unique()
        sc.suburb.unique()
        sc.postcode.unique()
        sc.location.unique()
        sc
    }
}
