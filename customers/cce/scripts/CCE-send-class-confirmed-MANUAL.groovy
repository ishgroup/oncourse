def run(args) {
    CourseClass courseClass = args.entity
    //conditions to send 'Class Confirmed' messages to students
    if (courseClass.getSuccessAndQueuedEnrolments().size() >= courseClass.getMinimumPlaces() && courseClass.actualTotalProfit >= 500) {
        courseClass.getSuccessAndQueuedEnrolments().each { e ->
            email {
                template "CCE Class Confirmed"
                key "send-class-confirmed", courseClass
                keyCollision "drop"
                to e.student.contact
                bindings courseClass: courseClass, contact: e.student.contact
            }
        }
    }
    //send notification to admin
    else {
        Message lastStudentMessage = ObjectSelect.query(Message.class)
                .where(Message.CREATOR_KEY.eq(MessageUtils.generateCreatorKey("send-class-confirmed", courseClass)))
                .orderBy(Message.ID.desc())
                .selectFirst(args.context)

        if (lastStudentMessage){

            String prefix = "class not viable natalia.borisova@sydney.edu.au"
            String messageUniqueKey = MessageUtils.generateCreatorKey(prefix, courseClass)
            Message lastAdminMessage = ObjectSelect.query(Message.class)
                    .where(Message.CREATOR_KEY.eq(messageUniqueKey))
                    .orderBy(Message.ID.desc())
                    .selectFirst(args.context)

            if (!lastAdminMessage || lastAdminMessage.getCreatedOn() < lastStudentMessage.getCreatedOn()) {
                email {
                    from "info@sydney.edu.au"
                    to "natalia.borisova@sydney.edu.au"
                    subject "Confirmed class now below class minimum"
                    key messageUniqueKey
                    content "Due to enrolment cancellations or other class changes ${courseClass.code} ${courseClass.course.name}, is no longer viable. It has already been confirmed as running via emails to the students. Please investigate. "
                }
            }
        }
    }
}


