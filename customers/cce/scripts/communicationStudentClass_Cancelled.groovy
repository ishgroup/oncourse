/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovy.time.TimeCategory
import ish.oncourse.server.cayenne.CourseClass

def run(args) {
    CourseClass courseClass = args.entity

    Date now = new Date()

    if (courseClass.isCancelled && (courseClass.endDateTime > now || (courseClass.isDistantLearningCourse && courseClass.modifiedOn > now - 7))) {
        Date last5Mins, next5Mins
        use( TimeCategory ) {
            last5Mins = courseClass.modifiedOn - 5.minutes
            next5Mins = courseClass.modifiedOn + 5.minutes
        }

        courseClass.enrolments.findAll { e -> e.modifiedOn.after(last5Mins) && e.modifiedOn.before(next5Mins) }.each { e ->
            email {
                template "CCE Cancellation"
                to e.student.contact
                bindings enrolment : e
            }
        }
    }
}

