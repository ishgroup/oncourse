
  /*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def enrolment = args.entity

  if (enrolment.status == EnrolmentStatus.SUCCESS && enrolment.confirmationStatus == ConfirmationStatus.NOT_SENT) {
    def m
    if (enrolment.courseClass.course.code.startsWith("EL")) {
      m = Email.create("Enrolment Confirmation EL")
    } else {
      m = Email.create("Enrolment Confirmation")
    }
    m.bind(enrolment: enrolment)
    m.to(enrolment.student.contact)

    m.send()

    enrolment.setConfirmationStatus(ConfirmationStatus.SENT)
    args.context.commitChanges()
  }
