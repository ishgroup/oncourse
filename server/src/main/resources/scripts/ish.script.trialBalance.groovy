def accounts = query {
    entity 'Account'
}

accounts = accounts.sort { it.accountCode }

report {
    keycode "ish.onCourse.trialBalance"
    records accounts
    param 'localdateRange_from': fromDate, 'localdateRange_to': toDate
    generatePreview true
}