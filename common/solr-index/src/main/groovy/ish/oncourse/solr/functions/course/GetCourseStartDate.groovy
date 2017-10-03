package ish.oncourse.solr.functions.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass

import static ish.oncourse.solr.functions.course.Functions.getCourseClasses

/**
 * Gets startDate value for SolrCourse document
 */
class GetCourseStartDate {

    Date current = new Date()

    Date get(Course course, Closure<List<CourseClass>> courseClasses = getCourseClasses) {

        List<CourseClass> classes = courseClasses.call(course)

        Date startDate = getStartDate(classes)

        return startDate
    }

    private Date getStartDate(List<CourseClass> classes) {
        return classes.collect { c -> new GetSolrCourseClass(c, current).get().classStart }.sort({ Date d1, Date d2 -> d1 <=> d2 }).first()
    }
}
