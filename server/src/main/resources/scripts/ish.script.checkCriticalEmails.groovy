records = query {
    entity "Enrolment"
    query "createdOn after today - ${numberOfDays} day and status is SUCCESS and confirmationStatus is NOT_SENT "
}

records.each { enrolment ->
    eventService.postEvent(ish.oncourse.common.SystemEvent.valueOf(SystemEventType.ENROLMENT_SUCCESSFUL, enrolment))
}

records = query {
    entity "Invoice"
    query "createdOn after today - ${numberOfDays} day and confirmationStatus is NOT_SENT "
}

records.each { invoice ->
    invoice.modifiedOn = new Date()
}
records[0]?.context?.commitChanges()

records = query {
    entity "PaymentIn"
    query "createdOn after today - ${numberOfDays} day and status is SUCCESS and confirmationStatus is NOT_SENT "
}

records.each { paymentIn ->
    paymentIn.modifiedOn = new Date()
}
records[0]?.context?.commitChanges()

records = query {
    entity "PaymentOut"
    query "createdOn after today - ${numberOfDays} day and status is SUCCESS and confirmationStatus is NOT_SENT "
}

records.each { paymentOut ->
    paymentOut.modifiedOn = new Date()
}
records[0]?.context?.commitChanges()