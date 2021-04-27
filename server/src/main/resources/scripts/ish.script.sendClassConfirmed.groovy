def classes = query {
    entity "CourseClass"
}

classes.each { cclass ->
    records = cclass.getSuccessAndQueuedEnrolments()
    //conditions to send 'Class Confirmed' messages to students
    if (records.size() >= cclass.getMinimumPlaces() && cclass.actualTotalProfit >= expectedProfit) {
        message {
            template classConfirmedTemplate
            record records
            key "send-class-confirmed", cclass
            keyCollision "drop"
            courseClass cclass
        }
    }
    //send notification to admin
    else {
        def creatorKey = MessageUtils.generateCreatorKey("send-class-confirmed", cclass)

        records = query {
            entity "Message"
            query "creatorKey is \"${creatorKey}\""
        }
        def lastStudentMessage = records.sort { it.createdOn }.last()
        if (lastStudentMessage) {

            //Attention! Set your own 'adminPrefix'
            String prefix = adminPrefix ?: "class no viable ${preference.email.from}"
            String messageUniqueKey = MessageUtils.generateCreatorKey(prefix, cclass)
            records = query {
                entity "Message"
                query "creatorKey is \"${messageUniqueKey}\""
            }
            def lastAdminMessage = records.sort { it.createdOn }.last()

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