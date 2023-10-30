def waitingLists = query {
    entity "WaitingList"
    query "createdOn after today - ${numberOfDays.intValue()} days"
}

waitingLists.each() { waitingList ->
    def classes = waitingList.course.courseClasses.findAll() { courseClass ->
        courseClass.isActive && courseClass.isShownOnWeb && courseClass.successAndQueuedEnrolments.size() < courseClass.maximumPlaces && (courseClass.isDistantLearningCourse || new Date() < courseClass.startDateTime)
    }

    if (classes.size() > 0) {
        message {
            template waitingListReminderTemplate
            record waitingList
            courseClasses classes
        }
    }
}