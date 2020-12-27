import ish.oncourse.server.cayenne.CourseClass

List<CourseClass> classes = query {
    entity "CourseClass"
    query ""
}

classes.each { cclass ->
    //conditions to send 'Class Confirmed' messages to students
    if (cclass.getSuccessAndQueuedEnrolments().size() >= cclass.getMinimumPlaces() && cclass.actualTotalProfit >= expectedProfit) {
        cclass.getSuccessAndQueuedEnrolments().each { enrolment ->
            message {
                template classConfirmedTemplate
                record enrolment
                key "send-class-confirmed", cclass
                keyCollision "drop"
                courseClass cclass
            }
        }
    }
    //send notification to admin
    else {
        def creatorKey = MessageUtils.generateCreatorKey("send-class-confirmed", cclass)

        Message lastStudentMessage = query {
            entity "Message"
            query "creatorKey is \"${creatorKey}\""
            sort "desc"
            first true
        }

        if (lastStudentMessage) {

            //Attention! Set your own 'adminPrefix'
            String prefix = adminPrefix ?: "class no viable ${preference.email.from}"
            String messageUniqueKey = MessageUtils.generateCreatorKey(prefix, cclass)
            Message lastAdminMessage = query {
                entity "Message"
                query "creatorKey is \"${messageUniqueKey}\""
                sort "desc"
                first true
            }

            if (!lastAdminMessage || lastAdminMessage.createdOn < lastStudentMessage.createdOn) {

                message {
                    from preference.email.from
                    to preference.email.admin
                    subject emailSubjectToAdmin
                    key messageUniqueKey
                    content "Due to enrolment cancellations or other class changes ${cclass.code} ${cclass.course.name}, is no longer viable. It has already been confirmed as running via emails to the students. Please investigate. "
                }
            }
        }
    }
}
