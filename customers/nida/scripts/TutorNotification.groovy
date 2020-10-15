// Return the first session taught by a tutor
Session getFirstSession(tutor, courseClass) {
  return courseClass.sessions.findAll { s-> s.tutors.contains(tutor) }.sort { s -> s.startDatetime }.first()
}

def run(args) {

  def dayAfterTomorrowStart = new Date() + 2
  dayAfterTomorrowStart.set(hourOfDay: 0, minute: 0, second: 0)

  def classesStartingTomorrow = ObjectSelect.query(CourseClass)
        .where(CourseClass.IS_CANCELLED.eq(false))
        .and(CourseClass.START_DATE_TIME.ne(null))
        .and(CourseClass.START_DATE_TIME.between(dayAfterTomorrowStart, dayAfterTomorrowStart + 1))
        .select(args.context)

  // first get all TutorAttendance records
  classesStartingTomorrow.forEach { courseClass ->
    def attendance = courseClass.sessions*.sessionTutors.flatten().findAll { a -> a.courseClassTutor.tutor }

    // discard sessions which aren't the first session for this class for this tutor
    attendance = attendance.findAll { a -> getFirstSession(a.courseClassTutor.tutor, a.session.courseClass) == a.session }

    if (courseClass.course.hasTag("Open")) {
      courseClass.tutorRoles.each() { role ->

        attendance.each() { a ->
        def c = a.courseClassTutor.tutor.contact
        def cc = a.session.courseClass

          smtp {
            to c.email
            subject "NIDA teaching reminder"
            content "Dear " + c.fullName + ",\n\n" + "Your course with NIDA, "+ cc.course.name + ", commences " + cc.startDateTime.format("EEE d MMM 'at' hh:mma") \
              + ". You will be located at " + a.session.room.site.name +".\n\n" \
              + "This email is a reminder of your first session time only, you will not receive reminders for subsequent sessions of this class. Your course materials, class lists and any additional venue or booking information has been communicated separately by the Course Coordinator or Course Manager \n\n" \
              + "Please ensure you arrive 20 minutes prior to the first session on the first day of this course and 10 minutes prior to the first session on any subsequent days if applicable. If you are running late or are unable to attend class on the day, please contact the Course Manger via the office numbers listed below. If you are teaching in Victoria, please contact the Melbourne office. If you are teaching in any other state, please contact the Sydney office.\n\n" \
              + "Sydney Reception: 02 9697 7500 8:30am - 5pm daily.\n" \
              + "Sydney Mobile: 0402 651 086 9am - 5:30pm daily.\n" \
              + "Melbourne Mobile: 0410 483 320  9:00am- 5pm Monday, Tuesday, Thursday and Friday.\n\n" \
              + "Note: Phone messages left out of hours or on personal message banks may not be received until 9am the following day or later. If you are unable to come to work, please ensure the message has been received via the general office numbers above.\n\n" \
              + "Thank you, we hope you enjoy and are rewarded by teaching for NIDA Open.\n\n" \
              + "Regards,\n\n"\
              + "The NIDA Open team."            
          }
        }
      }
    }
  }
}