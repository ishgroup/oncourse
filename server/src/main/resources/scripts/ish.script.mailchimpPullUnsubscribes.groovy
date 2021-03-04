records = query {
    entity "Preference"
    query "name is 'mailchimp.pullUnsubscribes.lastRun'"
}

def lastRun
if (records.empty) {
    lastRun = context.newObject(Preference)
    lastRun.name = "mailchimp.pullUnsubscribes.lastRun"
    lastRun.uniqueKey = "mailchimp.pullUnsubscribes.lastRun"
    // pull all unsubscribes
    lastRun.valueString = "1970-01-01"
    context.commitChanges()
} else {
    lastRun = records[0]
}
def sinceDate = java.time.LocalDate.parse(lastRun.valueString).minusDays(numberOfDays.toInteger())

mailchimp {
    action "pull unsubscribes"
    since sinceDate
}

lastRun.valueString = java.time.LocalDate.now().toString()
lastRun.context.commitChanges()
