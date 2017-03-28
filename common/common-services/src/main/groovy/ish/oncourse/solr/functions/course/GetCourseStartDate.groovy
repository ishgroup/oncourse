package ish.oncourse.solr.functions.course

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import org.apache.commons.lang3.time.DateUtils

import static ish.oncourse.solr.functions.course.Functions.getCourseClasses
import static ish.oncourse.solr.functions.course.GetCourseStartDate.ClassType.*

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

        List<Date> dates = [DateUtils.addYears(current, 100)]
        classes.each {
            ClassType type = valueOf(it, current)

            switch (type) {
                case distantLearning:
                    dates += DateUtils.addDays(current, 1)
                    break
                case withOutSessions:
                    dates += DateUtils.addYears(current, 100)
                    break
                case regular:
                    dates += it.startDate
                    break
                default:
                    throw new IllegalArgumentException("Unsupported type: $type")
            }
        }
        dates.sort({ Date d1, Date d2 -> d1 <=> d2 }).first()
    }

    enum ClassType {
        distantLearning,
        withOutSessions,
        regular

        static ClassType valueOf(CourseClass courseClass, Date current = new Date()) {

            if (courseClass.startDate != null && courseClass.sessions.findAll {
                it.startDate != null && it.endDate >= current
            }.size() > 0) {
                return regular
            }

            if (courseClass.isDistantLearningCourse) {
                return distantLearning
            }

            return withOutSessions
        }
    }

}
