def run(args) {
  def targetDateStart = new Date() + 14
  targetDateStart.set(hourOfDay: 0, minute: 0, second: 0)

  def targetDateEnd = new Date() + 15
  targetDateEnd.set(hourOfDay: 0, minute: 0, second: 0)

  def exp = CourseClass.IS_CANCELLED.eq(false)
        .andExp(CourseClass.START_DATE_TIME.ne(null))
        .andExp(CourseClass.START_DATE_TIME.between(targetDateStart, targetDateEnd))

  def classes = args.context.select(SelectQuery.query(CourseClass, exp))

  


  classes.each() { c ->

    
    // get a list of students with a non empty specialNeeds field
    def alertStudents = c.successAndQueuedEnrolments.student*.findAll { s -> s.specialNeeds != null  && s.specialNeeds != "" }.flatten()

    if ( c.successAndQueuedEnrolments.size() < c.minimumPlaces ) {
      smtp {
          subject "ACTION: Low enrolment class"
          from "no_reply@weasydney.nsw.edu.au"
          to "james.laughlin@weasydney.nsw.edu.au"
          content "The class " + c.course.code + "-" + c.code + " " + c.course.name + " has less than the minimum enrolment numbers. Please promote or postpone."
      }

      c.tutorRoles?.each() { role ->
        smtp {
            subject "Low enrolment class"
            from "no_reply@weasydney.nsw.edu.au"
            to role.tutor.contact.email
            content "Your class " + c.course.code + "-" + c.code + " " + c.course.name + " has not yet reached its minimum class size and action is being taken to promote it further."\
                    + "If this is not successful the class will be postponed or cancelled. The Education Manager will contact you about this."
        }
      }

    } else if ( c.successAndQueuedEnrolments.size() > c.minimumPlaces ) {
        // we've reached the minimum
      c.tutorRoles?.each() { role ->
        email {
            from "no_reply@weasydney.nsw.edu.au"
            to role.tutor.contact.email
            template "WEA Sydney Tutor Notification"
            bindings role: role, courseClass: c, alertStudents: alertStudents
      }
    } 
  }

  args.context.commitChanges()
  }
}