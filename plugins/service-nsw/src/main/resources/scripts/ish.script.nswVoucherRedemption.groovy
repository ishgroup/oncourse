records = query {
    entity "Voucher"
    query "serviceNswVoucher not is null and serviceNswRedeemedOn is null"
}
def now = new Date()

// Service NSW require all vouchers to be redeemed before the end of the year, even if
// the class runs next year. So let's give ourselves a few days grace to check all is OK
def endOfyear = now.copyWith(
        month: Calendar.DECEMBER,
        dayOfMonth: 31
)
def isEndOfYear = daysBeforeYearEnd && (endOfyear - now < daysBeforeYearEnd)

records.each { record ->
    def paymentInLines = record.voucherPaymentsIn*.paymentIn*.paymentInLines.flatten()
    if (!paymentInLines.empty) {

        // take the first paymentInLine because the service NSW vouchers always have to be redeemed in one go against just a single invoice.
        Invoice invoice = paymentInLines.first().invoice as Invoice
        def classesStartDates = invoice.invoiceLines.collect { it.enrolment.courseClass.startDateTime }.unique()
        if (classesStartDates.any { it < now || isEndOfYear } ) {
            service_nsw {
                action "redeem"
                voucher record
                errorsTo emailAddress
            }
        }
    }
}