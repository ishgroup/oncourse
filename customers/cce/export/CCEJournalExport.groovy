records.each { PaymentIn pi ->
    csv << [
            "from account" :  pi.payer.customField('uniInternalTransferAccount') ?: 'unknown',
            "contact"   : pi.payer.fullName,
            "to suspense account" : "1110-43301-11111",
            "amount" : pi.amount?.toPlainString()
    ]
}