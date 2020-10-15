/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { PaymentIn pi ->
    csv << [
            "from account" :  pi.payer.customField('uniInternalTransferAccount') ?: 'unknown',
            "contact"   : pi.payer.fullName,
            "to suspense account" : "1110-43301-11111",
            "amount" : pi.amount?.toPlainString()
    ]
}
