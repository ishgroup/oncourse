/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def currentDate = new Date()

records.each { AccountTransaction at ->
        def invoiceLine = at.getInvoiceLineForTransaction(at.context, at)
        def paymentInLine = at.getPaymentInLineForTransaction(at.context, at)
        def paymentOutLine = at.getPaymentOutLineForTransaction(at.context, at)

        csv << [
                "Id" : at.id,
                "Company Code"       : "01",
                "Batch Number"       : currentDate.format('ddMMyyyy').reverse(),
                "Batch Description"  : currentDate.format('dd/MM/yyyy') + ' onCourse export',
                "Document Number"    : '123456',
                "Document Detail"    : '',
                "Document Date"      : currentDate.format('dd/MM/yyyy'),
                "Posting Date"       : currentDate.format('dd/MM/yyyy'),
                "Line Company"       : '01',
                "Account Number"     : at.account.accountCode.split('/').getAt(0),
                "Cash Analysis Code" : '',
                "Quantity"           : '1',
                "Amount"             : AccountType.CREDIT_TYPES.contains(at.account.type) ? at.amount.toBigDecimal().negate() : at.amount.toBigDecimal(),
                "Narration"          : ['onCourse',
                                        at.source,
                                        at.invoiceNumber,
                                        at.contactName,
                                        at.invoiceDescription ? "(${at.invoiceDescription.trim()})" : null,
                                        at.paymentType,
                                        'processed on',
                                        at.transactionDate.format('dd/MM/yyyy hh.mma').replace('AM', 'a.m.').replace('PM', 'p.m.')
                                       ].findAll().join(' '),
                "Rate"               : '1',
                "Multiply Rate"      : '0',
        ]
}
