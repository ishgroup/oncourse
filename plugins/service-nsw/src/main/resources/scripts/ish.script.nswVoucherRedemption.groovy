records = query {
    entity "Voucher"
    query "serviceNswVoucher not is null and serviceNswRedeemedOn is null"
}

records.each { record ->
    PaymentInLine paymentInLine = record.voucherPaymentsIn*.paymentIn*.paymentInLines.flatten()[0] as PaymentInLine
    if (paymentInLine?.invoice?.invoiceLines?.collect { it.enrolment.courseClass.startDateTime}?.unique()?.any {it < new Date() }) {
        service_nsw {
            action "redeem"
            voucher record
        }
    }
}