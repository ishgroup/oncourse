import java.time.LocalDate

def accounts = query {
    entity "Account"
    query ""
}


LocalDate endDate = LocalDate.now().minusDays(1)
LocalDate startDate = endDate.minusDays(6)

def attach = report {
    keycode reportKeycode
    records accounts
    param 'localdateRange_from': startDate, 'localdateRange_to': endDate
}

message {
    from preference.email.from
    to preference.email.admin
    subject "onCourse transaction summary ${startDate.format("dd/MM/yy")} to ${endDate.format("dd/MM/yy")}"
    content "'Trial Balance' report for the previous 7 days."
    attachment "Trial_Balance.pdf", "application/pdf", attach
}
