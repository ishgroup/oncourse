/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

List records = query {
    entity "Voucher"
}

records.findAll { it.customFields.find {it.customFieldType.key == "serviceNswVoucher"}?.value != null }
        .findAll { it.customFields.find {it.customFieldType.key == "serviceNswRedeemedOn"}?.value == null }
        .each { record ->
            if (record.voucherPaymentsIn*.invoiceLine*.enrolment*.courseClass*.startDateTime.any {it < new Date()}) {
                nsw {
                    action "redeem"
                    voucher record
                }
            }
        }