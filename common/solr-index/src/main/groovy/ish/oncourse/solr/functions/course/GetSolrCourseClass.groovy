package ish.oncourse.solr.functions.course

import ish.oncourse.model.CourseClass
import ish.oncourse.solr.model.SolrCourseClass
import org.apache.commons.lang3.time.DateUtils

/**
 * User: akoiro
 * Date: 3/10/17
 */
class GetSolrCourseClass {
    private CourseClass courseClass
    private Date current = new Date()

    GetSolrCourseClass(CourseClass courseClass, Date current = new Date()) {
        this.courseClass = courseClass
        this.current = current
    }

    SolrCourseClass get() {
        ClassType type = ClassType.valueOf(courseClass)
        SolrCourseClass scc = new SolrCourseClass()
        scc.classStart = getStartDate(type)
        scc.classEnd = getEndDate(type)
        scc.classCode = "${courseClass.course.code}-${courseClass.code}"
        scc.price = courseClass.feeExGst.toPlainString()
        return scc
    }

    private Date getStartDate(ClassType type) {
        switch (type) {
            case ClassType.distantLearning:
                return DateUtils.addDays(current, 1)
            case ClassType.withOutSessions:
                return DateUtils.addYears(current, 100)
            case ClassType.regular:
                return courseClass.startDate
            default:
                throw new IllegalArgumentException("Unsupported type:  $type ")
        }
    }

    private Date getEndDate(ClassType type) {
        switch (type) {
            case ClassType.distantLearning:
                return DateUtils.addYears(current, 100)
            case ClassType.withOutSessions:
                return DateUtils.addYears(current, 100)
            case ClassType.regular:
                return courseClass.endDate
            default:
                throw new IllegalArgumentException("Unsupported type:  $type ")
        }
    }
}
