def run(args) {
    def dayAfterTomorrowStart = new Date() + 2
    dayAfterTomorrowStart.set(hourOfDay: 0, minute: 0, second: 0)

    def dayAfterTomorrowEnd = new Date() + 3
    dayAfterTomorrowEnd.set(hourOfDay: 0, minute: 0, second: 0)

    def context = args.context

    def exp = CourseClass.IS_CANCELLED.eq(false)
        .andExp(CourseClass.START_DATE_TIME.ne(null))
        .andExp(CourseClass.START_DATE_TIME.between(dayAfterTomorrowStart, dayAfterTomorrowEnd))

    def classesStartingTomorrow = context.select(SelectQuery.query(CourseClass, exp))
    
    classesStartingTomorrow.each() { courseClass ->
       courseClass.tutorRoles.each() { role ->
           if(role.tutor.contact.email) {
               email {
                  to role.tutor.contact.email
                  template "Tutor notice of class commencement"
                  bindings courseClass: courseClass, tutor: role.tutor
                  cc "programming@sydneycommunitycollege.edu.au"
               }
           }
       }
   }
}