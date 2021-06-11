records = query {
    entity "Voucher"
    query "serviceNswVoucher not is null and serviceNswRedeemedOn is null"
}
def now = new Date()
records.each { record ->
    def paymentInLines = record.voucherPaymentsIn*.paymentIn*.paymentInLines.flatten()
    if (!paymentInLines.empty) {

        // take the first paymentInLine because the service NSW vouchers always have to be redeemed in one go against just a single invoice.
        Invoice invoice = paymentInLines.first().invoice as Invoice
        def classesStartDates = invoice.invoiceLines.collect { it.enrolment.courseClass.startDateTime }.unique()

        if (classesStartDates.any { it < now } ) {
            service_nsw {
                action "redeem"
                voucher record
                errorsTo emailAddress
            }
        }
    }
}