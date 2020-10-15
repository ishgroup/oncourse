import java.time.temporal.TemporalField
import java.time.temporal.WeekFields
import java.time.LocalDate

def run(args) {

    TemporalField field = WeekFields.of(Locale.getDefault()).dayOfWeek()

    // to use sending interval you need to create 'last send date'
    // now there are 2 variants of send interval: once a week and once a month. You can use only 1 variant, to use it just uncommit the right 'lastSendDate'

    LocalDate lastSendDate = LocalDate.now().with(field, 1) //once a week on first day of week
    //LocalDate lastSendDate = LocalDate.now().withDayOfMonth(1) // once a month

    List<Course> courses = ObjectSelect.query(Course)
            .where(Course.WAITING_LISTS.isNotNull())
            .and(Course.COURSE_CLASSES.dot(CourseClass.IS_SHOWN_ON_WEB).eq(true))
            .select(args.context)

    courses.each { course ->
        List<CourseClass> classes = course.courseClasses.findAll { c -> c.isShownOnWeb && c.isActual() }

        course.waitingLists.each { waitingList ->

            email {
                template "CCE Class Published"
                key "Classes published ${lastSendDate}", course
                keyCollision "drop"
                bindings contact: waitingList.student.contact, classes: classes, course: course
                to waitingList.student.contact
            }
        }
    }
}