import ish.oncourse.server.cayenne.Voucher

records.each { Voucher v ->
    csv << [
            "createdOn"       : v.createdOn?.format("yyyy-MM-dd"),
            "expiryDate"      : v.expiryDate?.format("yyyy-MM-dd"),
            "code"            : v.code,
            "invoiceToOnRedemption": v.contact?.fullName,
            "invoice"         : v.invoiceLine.invoice.invoiceNumber,
            "purchasedBy"     : v.invoiceLine.invoice.contact?.fullName,
            "productName"     : v.product.name,
            "redeemableBy"    : v.redeemableBy?.fullName,
            "valueOnPurchase" : v.valueOnPurchase,
            "redemptionValueRemaining" : v.redemptionValue,
            "source"          : v.source,
            "status"          : v.status,
            "redemptionsby"	: v.voucherPaymentsIn?.invoiceLine?.enrolment?.student?.contact?.fullName,
            "redeemedFor"	: v.voucherPaymentsIn?.invoiceLine?.description
    ]
}
