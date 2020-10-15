//test
def run(args) {
  def dayAfterTomorrowStart = new Date() 
  dayAfterTomorrowStart.set(hourOfDay: 0, minute: 0, second: 0)

  def dayAfterTomorrowEnd = new Date() + 4
  dayAfterTomorrowEnd.set(hourOfDay: 0, minute: 0, second: 0)

  def context = args.context

  def exp = CourseClass.IS_CANCELLED.eq(false)
        .andExp(CourseClass.START_DATE_TIME.ne(null))
        .andExp(CourseClass.START_DATE_TIME.between(dayAfterTomorrowStart, dayAfterTomorrowEnd))

  def classesStartingTomorrow = context.select(SelectQuery.query(CourseClass, exp))

  classesStartingTomorrow.each() { a ->
      
  def cc = a.sessions.courseClass

    smtp {
      to "kcurran@weahunter.edu.au"
      subject "Admin notice of class commencement"
      content "Hi" + ",\n\n" + "Please note " + cc.course.name.unique() + " is about to begin." + cc.startDateTime + "\n\n"\
        + "You are currently expecting " + cc.enrolmentsCount.unique() + " students. This number may change if there are further enrolments" + ".\n\n" \
        + "Regards,\n\n"\
        + "The WEA Hunter team."  
    }
  }
}