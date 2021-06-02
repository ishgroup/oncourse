List records = query {
    entity "Voucher"
}

records.findAll { it.customFields.find {it.customFieldType.key == "serviceNswVoucher"}?.value != null }
        .findAll { it.customFields.find {it.customFieldType.key == "serviceNswRedeemedOn"}?.value == null }
        .each { record ->
            if (record.voucherPaymentsIn*.invoiceLine*.enrolment*.courseClass*.startDateTime.any {it < new Date()}) {
                service_nsw {
                    action "redeem"
                    voucher record
                }
            }
        }