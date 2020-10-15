/**

OW-2255 // OS-42098
Script for sending student evalutations on class completion
Sends email at 9am on the day of the last session of the courseclass

*/

def run(args) {

    def endDate =  new Date()
    endDate.set(hourOfDay: 0, minute: 0, second: 0)

    def allClasses = ObjectSelect.query(CourseClass)
            .where(CourseClass.IS_CANCELLED.eq(false))
            .and(CourseClass.END_DATE_TIME.between(endDate, endDate+1))
            .select(args.context)

    allClasses.each { cc -> 
        cc.successAndQueuedEnrolments.each { e ->
            email {
                to e.student.contact
                template    'CCE Student Survey Reminder'
                bindings    enrolment: e
            }
        }
    }
}