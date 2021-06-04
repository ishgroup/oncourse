records = query {
    entity "CustomField"
    query "entityIdentifier = \"Voucher\" and customFieldType.key is \"serviceNswVoucher\""
}

records.findAll { it.relatedObject.serviceNswRedeemedOn == null}.each { voucherCustomField ->
    PaymentInLine paymentInLine = voucherCustomField.relatedObject.voucherPaymentsIn*.paymentIn*.paymentInLines.flatten()[0] as PaymentInLine
    if (paymentInLine?.invoice?.invoiceLines?.collect { it.enrolment.courseClass.startDateTime}?.unique()?.any {it < new Date() }) {
        service_nsw {
            action "redeem"
            voucher record
        }
    }
}