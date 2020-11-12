import ish.oncourse.server.cayenne.VoucherProduct

records.each { VoucherProduct vp ->
    csv << [
        "id"                    : vp.id,
        "createdOn"             : vp.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
        "modifiedOn"            : vp.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
        "sku"                   : vp.sku,
        "name"                  : vp.name,
        "priceExTax"            : vp.priceExTax?.toPlainString(),
        "maxCoursesRedemption"  : vp.maxCoursesRedemption,
        "expiryDays"            : vp.expiryDays
    ]
}
