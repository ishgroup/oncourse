package ish.oncourse.solr.functions.course

import io.reactivex.Observable
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Tag
import ish.oncourse.solr.model.SCourse
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ResultIterator

/**
 * User: akoiro
 * Date: 23/10/17
 */
class CourseContext {
    Course course
    ObjectContext context
    Date current = new Date()
    Closure<ResultIterator<CourseClass>> courseClasses = CourseFunctions.CourseClasses
    Closure<ResultIterator<Tag>> tags = CourseFunctions.Tags
    Closure<Observable<SCourse>> applyCourseClass = SCourseFunctions.ApplyCourseClass
}
