records.sort{ it -> it.invoiceNumber }.each { Invoice i ->
    csv << [
            "invoiceNumber"   : i.invoiceNumber,
            "invoiceDate"     : i.invoiceDate?.format("yyyy-MM-dd"),
            "dateDue"         : i.dateDue?.format("yyyy-MM-dd"),
            "payer"           : i.contact.fullName,
            "total"           : i.total?.toPlainString(),
            "invoiceAmountOwingIncTax"     : i.amountOwing?.toPlainString(),
            "invoiceAmountOwingExclTax"     :  Money.ONE.subtract(i.totalTax.divide(i.totalIncTax)).multiply(i.amountOwing).toPlainString()
    ]
}