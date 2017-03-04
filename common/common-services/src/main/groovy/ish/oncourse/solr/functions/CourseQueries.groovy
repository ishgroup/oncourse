package ish.oncourse.solr.functions

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import org.apache.cayenne.query.ObjectSelect

/**
 * Contains cayenne queries  which are used to get a course related objects
 */
class CourseQueries {
    /**
     * @return list course classes which should be indexed
     */
    static List<CourseClass> getCourseClasses(Course course) {
        return (ObjectSelect.query(CourseClass) & CourseClass.COURSE.eq(course) & CourseClass.IS_WEB_VISIBLE.eq(true) & CourseClass.CANCELLED.eq(true) & CourseClass.START_DATE.isNotNull())
                .orderBy(CourseClass.START_DATE.asc()).select(course.objectContext).findAll {
            it.availableEnrolmentPlaces > 0
        }
    }
}
