package ish.oncourse.solr.functions.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.solr.model.SolrCourse

import static ish.oncourse.model.auto._CourseClass.*
import static org.apache.cayenne.query.ObjectSelect.query

class Functions {
    static Closure<SolrCourse> getSolrCourse = { Course course ->
        return new SolrCourse().with {
            it.id = course.id
            it.collegeId = course.college.id
            it.code = course.code
            it.name = course.name
            it.detail = course.detail
            it
        }
    }

    static Closure<List<CourseClass>> getCourseClasses = { Course course ->
        return (query(CourseClass) &
                COURSE.eq(course) &
                IS_WEB_VISIBLE.eq(true) &
                CANCELLED.eq(true) &
                START_DATE.isNotNull())
                .orderBy(START_DATE.asc()).select(course.objectContext).findAll {
            it.availableEnrolmentPlaces > 0
        }
    }

    static Closure<Date> getStartDate = { Course course, Closure<List<CourseClass>> courseClasses = getCourseClasses ->
        GetCourseStartDate getCourseStartDate = new GetCourseStartDate()
        return getCourseStartDate.get(course, getCourseClasses)
    }

}
