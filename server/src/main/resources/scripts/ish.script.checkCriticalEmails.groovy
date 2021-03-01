records = query {
    entity "Enrolment"
    query "createdOn after today - 7 day and status is SUCCESS and confirmationStatus is NOT_SENT "
}

records.each { enrolment ->
    enrolment.modifiedOn = new Date()
    enrolment.context.commitChanges()
    eventService.postEvent(ish.oncourse.common.SystemEvent.valueOf(SystemEventType.ENROLMENT_SUCCESSFUL, enrolment))
}


records = query {
    entity "Invoice"
    query "createdOn after today - 7 day and confirmationStatus is NOT_SENT "
}

records.each { invoice ->
    invoice.modifiedOn = new Date()
    invoice.context.commitChanges()
}


records = query {
    entity "PaymentIn"
    query "createdOn after today - 7 day and status is SUCCESS and confirmationStatus is NOT_SENT "
}

records.each { paymentIn ->
    paymentIn.modifiedOn = new Date()
    paymentIn.context.commitChanges()
}

records = query {
    entity "PaymentOut"
    query "createdOn after today - 7 day and status is SUCCESS and confirmationStatus is NOT_SENT "
}

records.each { paymentOut ->
    paymentOut.modifiedOn = new Date()
    paymentOut.context.commitChanges()
}