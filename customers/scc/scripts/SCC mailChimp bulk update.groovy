records = query {
    entity "Enrolment"
    query "createdOn >= ${fromDate} and (status not is FAILED or status not is FAILED_CARD_DECLINED or status not is FAILED_NO_PLACES)"
}

records.each { record ->
    def student = record.student.contact
    if (student.email && student.allowEmail) {
        mailchimp {
            name enrolmentMailchimpIntegrationName
            action 'subscribe'
            contact student
            tags record.courseClass.course.tags
            optIn false
        }
    }
}

records = query {
    entity "WaitingList"
    query "createdOn >= ${fromDate}"
}

records.each { record ->
    def student = record.student.contact
    if (student.email && student.allowEmail) {
        mailchimp {
            name waitingListMailchimpIntegrationName
            action 'subscribe'
            contact student
            tags record.course.tags
            optIn false
        }
    }
}