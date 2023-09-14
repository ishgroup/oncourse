import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.WaitingList
import org.apache.cayenne.query.ObjectSelect

def updatedCoursesIds = ObjectSelect.columnQuery(Course, Course.ID)
        .where(Course.COURSE_CLASSES.dot(CourseClass.CREATED_ON).gte(new Date().minus(7)))
        .select(context)

def waitingLists = ObjectSelect.query(WaitingList)
        .where(WaitingList.COURSE.dot(Course.ID).in(updatedCoursesIds))
        .select(context)

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