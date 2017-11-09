package ish.oncourse.solr.functions.course

import ish.oncourse.model.CourseClass

/**
 * User: akoiro
 * Date: 3/10/17
 */
enum ClassType {
    distantLearning,
    withOutSessions,
    regular

    static ClassType valueOf(CourseClassContext context) {
        valueOf(context.courseClass, context.current)
    }

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
