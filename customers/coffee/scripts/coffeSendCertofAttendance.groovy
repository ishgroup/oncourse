def run(args) {

  def toDate = new Date().clearTime()

  // Find all non-VET classes (plus Accredited Barista) which ended yesterday
  def yesterdayEnrolments = ObjectSelect.query(Enrolment)
      .where(Enrolment.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse() )
      .and(Enrolment.COURSE_CLASS.dot(CourseClass.END_DATE_TIME).between( toDate - 1, toDate ) )
      .and(Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).dot(Course.IS_VET).isFalse() 
          .orExp(Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).dot(Course.CODE).eq("AB") )
      )
      .select(args.context)

  def goodEnrolments = yesterdayEnrolments.findAll { it.attendancePercent >= 80 }

  goodEnrolments.each { e ->

    def printData = report {
      keycode "ish.oncourse.nonVetCertificate"
      records Arrays.asList(e)
      background "cert of attendance template oncourse"
    }

    def docData = document {
      action "create"
      content printData
      name "${e.courseClass.uniqueCode}_${e.student.contact.lastName}_${e.student.contact.firstName}_Certificate_Attendance.pdf"
      mimeType "image/pdf"
      permission AttachmentInfoVisibility.STUDENTS
      attach e
    }

    email {
      template "Certificate available"
      bindings enrolment: e
      to e.student.contact
    }
  }
}