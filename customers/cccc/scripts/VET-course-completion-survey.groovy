/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 Send the VET completion survey to all students who completed a VET course recently.

 */
def run(args) {

    // We want to search on a period of one day, two weeks ago
    def periodStart = new Date() - 14
    periodStart.set(hourOfDay: 0, minute: 0, second: 0)
    def periodEnd = periodStart + 1

    def classes = ObjectSelect.query(CourseClass)
            .where(CourseClass.IS_CANCELLED.eq(false))
            .and(CourseClass.COURSE.dot(Course.IS_VET).eq(true))
            .and(CourseClass.END_DATE_TIME.between(periodStart, periodEnd))
            .select(args.context)

    classes.each { c ->
        if (c.course.modules.size() != 0) {
            c.enrolments.each { e ->
                email {
                    template "VET Course completion survey"
                    bindings enrolment: e
                    to e.student.contact
                }
            }
        }
    }
}
