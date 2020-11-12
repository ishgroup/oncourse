import ish.oncourse.server.cayenne.CourseClass

List<CourseClass> classes = query {
    entity "CourseClass"
    query ""
}

classes.each { courseClass ->
    //conditions to send 'Class Confirmed' messages to students
    if (courseClass.getSuccessAndQueuedEnrolments().size() >= courseClass.getMinimumPlaces() && courseClass.actualTotalProfit >= expectedProfit) {
        courseClass.getSuccessAndQueuedEnrolments().each { enrolment ->
            email {
                template classConfirmedTemplate
                key "send-class-confirmed", courseClass
                keyCollision "drop"
                to enrolment.student.contact
                bindings courseClass: courseClass, contact: enrolment.student.contact
            }
        }
    }
    //send notification to admin
    else {
        def creatorKey = MessageUtils.generateCreatorKey("send-class-confirmed", courseClass)

        Message lastStudentMessage = query {
            entity "Message"
            query "creatorKey is \"${creatorKey}\""
            sort "desc"
            first true
        }

        if (lastStudentMessage) {

            //Attention! Set your own 'adminPrefix'
            String prefix = adminPrefix ?: "class no viable ${preference.email.from}"
            String messageUniqueKey = MessageUtils.generateCreatorKey(prefix, courseClass)
            Message lastAdminMessage = query {
                entity "Message"
                query "creatorKey is \"${messageUniqueKey}\""
                sort "desc"
                first true
            }

            if (!lastAdminMessage || lastAdminMessage.createdOn < lastStudentMessage.createdOn) {

                email {
                    from preference.email.from
                    to preference.email.admin
                    subject emailSubjectToAdmin
                    key messageUniqueKey
                    content "Due to enrolment cancellations or other class changes ${courseClass.code} ${courseClass.course.name}, is no longer viable. It has already been confirmed as running via emails to the students. Please investigate. "
                }
            }
        }
    }
}
