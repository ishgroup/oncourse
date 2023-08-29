import ish.common.types.CourseClassType

def waitingLists = query {
    entity "WaitingList"
}
waitingLists.each() { waitingList ->
    def classes = waitingList.course.courseClasses.findAll() { courseClass ->
        courseClass.isActive && courseClass.isShownOnWeb && courseClass.successAndQueuedEnrolments.size() < courseClass.maximumPlaces && (courseClass.type.equals(CourseClassType.DISTANT_LEARNING) || courseClass.type.equals(CourseClassType.HYBRID) || new Date() < courseClass.startDateTime)
    }

    if (classes.size() > 0) {
        message {
            template waitingListReminderTemplate
            record waitingList
            courseClasses classes
        }
    }
}