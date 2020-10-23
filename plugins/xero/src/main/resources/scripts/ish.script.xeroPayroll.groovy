
xero {
    action "payroll"
    payslip record
    bounceAddress loggedInUser?.email?: Preferences.get("email.admin")
}