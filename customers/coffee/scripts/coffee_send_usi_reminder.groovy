/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
  def monthBefore = new Date().minus(7)

  def enrolments = ObjectSelect.query(Enrolment)
    .where(Enrolment.CREATED_ON.gt(monthBefore))
    .and(Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).dot(Course.IS_VET).isTrue())
    .and(Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.SUBURB).isNull())
    .and(Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.EMAIL).isNotNull()
      .orExp(Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.SUBURB).eq(""))
      .orExp(Enrolment.STUDENT.dot(Student.USI_STATUS).ne(UsiStatus.VERIFIED))
    ).select(args.context)

 smtp {
   to "dan@coffeeschool.com.au"
   subject "VET questions reminder"
   content "Reminders sent to " + enrolments*.student*.contact*.email.flatten().unique().join(',')
  }

  enrolments.each() { e ->
    email {
      to e.student.contact
      template "coffee USI send reminder"
      bindings enrolment: e
    }
  }
}
