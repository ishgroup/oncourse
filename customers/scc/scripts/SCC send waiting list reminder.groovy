def run(args) {
  def waitingLists = args.context.select(SelectQuery.query(WaitingList))

  def today = new Date()
  def counter = 0

  waitingLists.each() { waitingList ->
    def courseClasses = waitingList.course.courseClasses.findAll() { cc ->
      cc.isActive && cc.isShownOnWeb && cc.successAndQueuedEnrolments.size() < cc.maximumPlaces && (cc.isDistantLearningCourse || today < cc.startDateTime)
    }
    
    if (courseClasses.size() > 0) {
      counter = counter + 1
      email {
        template "Waiting List reminder"
        bindings waitingList: waitingList, courseClasses: courseClasses
        to waitingList.student.contact
      }

    }
  }
}