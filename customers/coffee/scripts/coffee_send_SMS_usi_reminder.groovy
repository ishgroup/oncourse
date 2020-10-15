/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
    def tomorrowStart = new Date() + 1
    tomorrowStart.set(hourOfDay: 0, minute: 0, second: 0)

    def tomorrowEnd = new Date() + 2
    tomorrowEnd.set(hourOfDay: 0, minute: 0, second: 0)

    def context = args.context

    def classesStartingTomorrow = ObjectSelect.query(CourseClass)
            .where(CourseClass.IS_CANCELLED.eq(false))
            .and(CourseClass.START_DATE_TIME.ne(null))
            .and(CourseClass.START_DATE_TIME.between(tomorrowStart, tomorrowEnd))
            .select(context)

    classesStartingTomorrow.findAll { cc ->
        cc.successAndQueuedEnrolments.size() >= cc.minimumPlaces
    }*.successAndQueuedEnrolments.flatten().each() { enrolment ->
        if (enrolment?.student?.contact?.mobilePhone != null && (enrolment?.student?.contact?.suburb == null || enrolment?.student?.usiStatus != UsiStatus.VERIFIED)) {
            sms {
            to enrolment?.student.contact
            text "Urgent! You have NOT completed your ONLINE ENROLMENT QUESTIONS. Check your Coffee School Confirmation Email or u will be charged \$77 to rebook.".take(160)
            }
        }
	}
}
