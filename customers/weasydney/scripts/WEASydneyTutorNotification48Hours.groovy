def run(args) {
  def targetDateStart = new Date() + 1
  targetDateStart.set(hourOfDay: 0, minute: 0, second: 0)

  def targetDateEnd = new Date() + 2
  targetDateEnd.set(hourOfDay: 0, minute: 0, second: 0)

  def exp = CourseClass.IS_CANCELLED.eq(false)
        .andExp(CourseClass.START_DATE_TIME.ne(null))
        .andExp(CourseClass.START_DATE_TIME.between(targetDateStart, targetDateEnd))

  def classes = args.context.select(SelectQuery.query(CourseClass, exp))

  classes.each() { c ->
    // get a list of students with a non empty specialNeeds field
    def alertStudents = c.successAndQueuedEnrolments.student*.findAll { s -> s.specialNeeds != null && s.specialNeeds != "" }.flatten()

    if ( c.successAndQueuedEnrolments.size() > c.minimumPlaces ) {
        // we've reached the minimum
      c.tutorRoles?.each() { role ->
        email {
            from "no_reply@weasydney.nsw.edu.au"
            to role.tutor.contact
            template "WEA Sydney Tutor Notification 48 hours"
            bindings role: role, courseClass: c, alertStudents: alertStudents
        }
      } 
    }
  args.context.commitChanges()
  }
}