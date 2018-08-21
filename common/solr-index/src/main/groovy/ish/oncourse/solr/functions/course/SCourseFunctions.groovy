package ish.oncourse.solr.functions.course

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Tag
import ish.oncourse.solr.RXObservableFromIterable
import ish.oncourse.solr.model.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.time.DateUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.concurrent.Callable

/**
 * User: akoiro
 * Date: 5/10/17
 */
class SCourseFunctions {
    private static final Logger logger = LogManager.logger

    static final Observable<SCourse> SCourses(ObjectContext context,
                                              Date current = new Date(),
                                              Scheduler scheduler = Schedulers.io(),
                                              Callable<Iterable<Course>> courses) {
        return new RXObservableFromIterable<Course, SCourse>().iterable(courses)
                .mapper({ Course c -> GetSCourse.call(new CourseContext(course: c, context: c.objectContext, current: current)) })
                .scheduler(scheduler)
                .logger(logger).observable()
    }


    public static final Closure<SCourse> GetSCourse = { CourseContext context ->
        SCourse result = CourseFunctions.BuildSCourse.call(context.course)
        ResultIterator<CourseClass> classes = context.courseClasses(context)
        ResultIterator<Tag> tags = context.tags(context.course)
        try {
            result = Observable.fromIterable(classes).filter({ c -> c.hasAvailableEnrolmentPlaces })
                    .flatMap({ cc -> context.applyCourseClass(result, context.courseClassContext.call(cc, context.current)) })
                    .lastElement()
                    .defaultIfEmpty(result)
                    .blockingGet()


            context.tags(context.course).each { t -> result.tagId.add(t.id) }

            if (result.classStart.isEmpty()) result.classStart.add(DateUtils.addYears(context.current, 100))
            if (result.classEnd.isEmpty()) result.classEnd.add(DateUtils.addYears(context.current, 100))
            result.startDate = result.classStart.sort({ d1, d2 -> (d1 <=> d2) }).first()
        } finally {
            IOUtils.closeQuietly({ classes.close() } as Closeable)
            IOUtils.closeQuietly({ tags.close() } as Closeable)
        }
        return result
    }


    static final Closure<Observable<SCourse>> AddCourseClass = {
        SCourse sc, CourseClassContext cc ->
            Observable.just(new GetSCourseClass(cc).get()).map({ SCourseClass scc ->
                if (!sc.classStart.contains(scc.classStart)) sc.classStart.add(scc.classStart)
                if (!sc.classEnd.contains(scc.classEnd)) sc.classEnd.add(scc.classEnd)
                if (!sc.price.contains(scc.price)) sc.price.add(scc.price)
                if (!sc.classCode.contains(scc.classCode)) sc.classCode.add(scc.classCode)
                addSessions(sc, scc)
                addContacts(sc, scc)
                addSites(sc, scc)
                sc
            })
    }

    static final SCourse addSessions(SCourse sc, SCourseClass sClass) {
        sc.when.addAll(sClass.when)
        sc.when = sc.when.unique()
        sc
    }

    static final SCourse addContacts(SCourse sc, SCourseClass sClass) {
        sc.tutorId.addAll(sClass.tutorId)
        sc.tutor.addAll(sClass.tutor)

        sc.tutorId = sc.tutorId.unique()
        sc.tutor = sc.tutor.unique()
        sc
    }

    static final SCourse addSites(SCourse sc, SCourseClass c) {
        sc.siteId.addAll(c.siteId)
        sc.suburb.addAll(c.suburb)
        sc.postcode.addAll(c.postcode)
        sc.location.addAll(c.location)
        
        sc.siteId = sc.siteId.unique()
        sc.suburb = sc.suburb.unique()
        sc.postcode = sc.postcode.unique()
        sc.location = sc.location.unique()
        sc
    }
}
