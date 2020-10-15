def run(args) {

    //Value is a course class published to web
    def cc = args.value


    if ( cc.course.waitingLists.size() > 0 && cc.successAndQueuedEnrolments.size() < cc.maximumPlaces ) {
        cc.course.waitingLists.each { w ->
            if(w.student.contact.email != null && (!w.student.contact.email.equals("")) ) {
                email {
                        to w.student.contact
                        template "CCE Waiting List reminder"
                        bindings waitingList: w, courseClass: cc                               
                }
            }    
        }
    }
}