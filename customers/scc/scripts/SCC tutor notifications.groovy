def run(args) {
  def targetDateStart = new Date() + 7
  targetDateStart.set(hourOfDay: 0, minute: 0, second: 0)

  def targetDateEnd = new Date() + 8
  targetDateEnd.set(hourOfDay: 0, minute: 0, second: 0)

  def exp = CourseClass.IS_CANCELLED.eq(false)
      .andExp(CourseClass.START_DATE_TIME.ne(null))
      .andExp(CourseClass.START_DATE_TIME.between(targetDateStart, targetDateEnd))

  def classes = args.context.select(SelectQuery.query(CourseClass, exp))

  classes.each() { c ->

    if ( c.validEnrolmentCount == 0 ) {
      email {
        subject "ACTION: Zero enrolment class"
        from "no_reply@sydneycommunitycollege.edu.au"
        to "programming@sydneycommunitycollege.edu.au"
        content "The class " + c.course.code + "-" + c.code + " " + c.course.name + " has zero enrolments. It has been removed from the website."
      }
      c.setIsShownOnWeb(false)

    } else if ( c.validEnrolmentCount < c.minimumPlaces ) {
      email {
        subject "ACTION: Low enrolment class"
        from "no_reply@sydneycommunitycollege.edu.au"
        to "programming@sydneycommunitycollege.edu.au"
        content "The class " + c.course.code + "-" + c.code + " " + c.course.name + " has less than the minimum enrolment numbers. Please promote or postpone."
      }

      c.tutorRoles?.each() { role ->
        email {
          template "Low enrolment class"
          from "no_reply@sydneycommunitycollege.edu.au"
          to role.tutor.contact
          bindings cc : c, tutor: role.tutor

        }
      }

    } else {
      // we've reached the minimum
      c.tutorRoles?.each() { role ->
        email {
          template "Class proceeding"
          from "no_reply@sydneycommunitycollege.edu.au"
          to role.tutor.contact
          bindings cc : c
        }
      }
    }
  }
  args.context.commitChanges()
}