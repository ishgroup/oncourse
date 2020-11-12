import org.apache.cayenne.query.ObjectSelect

import java.time.LocalDate

def sinceDate


def preference = ObjectSelect.query(Preference).where(Preference.NAME.eq("mailchimp.pullUnsubscribes.lastRun")).selectOne(context)
if (!preference) {
    preference = context.newObject(Preference)
    preference.name = "mailchimp.pullUnsubscribes.lastRun"
    preference.uniqueKey = "mailchimp.pullUnsubscribes.lastRun"
    // pull all unsubscribes
    preference.valueString = "1970-01-01"
    context.commitChanges()
}
sinceDate = LocalDate.parse(preference.valueString).minusDays(7)

mailchimp {
    action "pull unsubscribes"
    since sinceDate
}

preference.valueString = LocalDate.now().toString()
context.commitChanges()
