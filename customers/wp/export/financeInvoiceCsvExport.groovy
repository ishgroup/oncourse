/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.collectMany { Invoice invoice -> invoice.invoiceLines }.each { InvoiceLine invoiceLine ->
    csv << [
            "Invoice date"           : invoiceLine.invoice.invoiceDate.format("yyyy-MM-dd"),
            "FT Type"                : null,
            "Contractor"             : invoiceLine.invoice.contact.getName(true),
            "Cus Code"               : null,
            "Student Name"           : invoiceLine?.enrolment?.student?.contact?.getName(true),
            "Ref"                    : null,
            "No charge"              : null,
            "Class code"             : invoiceLine?.enrolment?.courseClass?.uniqueCode,
            "Class start date"       : invoiceLine?.enrolment?.courseClass?.startDateTime?.format("dd/MM/yyyy h:mm a"),
            "Course name"            : invoiceLine?.enrolment?.courseClass?.course?.name,
            "Fee"                    : invoiceLine.priceTotalIncTax.toPlainString(),
            "Tax type"               : invoiceLine?.tax?.taxCode ?: "unknown",
            "Internal"               : null,
            "Reporting month"        : null,
            "onCourse invoice number": invoiceLine.invoice.invoiceNumber,
            "Description"            : invoiceLine.description,
            "Title"                  : invoiceLine.title,
            "Tax"                    : invoiceLine.priceTotalIncTax.subtract(invoiceLine.priceTotalExTax).toPlainString(),
            "Purchase order"         : invoiceLine.invoice.customerReference,
            "Discount"               : invoiceLine.discountTotalExTax.toPlainString(),
            "Account Code"           : invoiceLine.account.accountCode,
            "Account name"           : invoiceLine.account.description
    ]
}
