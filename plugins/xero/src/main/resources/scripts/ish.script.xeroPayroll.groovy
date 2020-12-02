
/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

xero {
    action "payroll"
    payslip record
    bounceAddress loggedInUser?.email?: Preferences.get("email.admin")
}
