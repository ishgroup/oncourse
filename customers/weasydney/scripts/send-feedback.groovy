def run(args) {
    def context = args.context

    def endDate = Calendar.getInstance().getTime()
    endDate.set(hourOfDay: 0, minute: 0, second: 0)

    def classes = ObjectSelect.query(CourseClass)
            .where(CourseClass.END_DATE_TIME.between(endDate - 1, endDate))
            .select(context)

    classes.collectMany { c -> c.successAndQueuedEnrolments }.each() { e ->
        email {
            template "Course Feedback Notification"
            from "info@weasydney.nsw.edu.au"
            to e.student.contact
            bindings enrolment : e
        }
    }
}