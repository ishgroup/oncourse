/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
  def enrolment = args.entity

  /**
   * OS-42292 Only run on enrolments created after a certain date
   */

  def startDate = new Date().parse("yyyy-MM-dd hh:mm:ss", "2018-03-01 0:00:00")



  if (enrolment.status == EnrolmentStatus.SUCCESS && enrolment.confirmationStatus == ConfirmationStatus.NOT_SENT && enrolment.createdOn > startDate) {
    def m = Email.create("CCE Enrolment Confirmation")
    m.bind(enrolment: enrolment)
    m.to(enrolment.student.contact)

    m.send()

    enrolment.setConfirmationStatus(ConfirmationStatus.SENT)
    args.context.commitChanges()
  }
}
