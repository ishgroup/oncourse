/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

Map agentGroup = new HashMap<String, List<PaymentIn>>()

records.each { pi ->
        pi.paymentIn.payer?.fromContacts?.each { fc ->
            if (fc.relationType.fromContactName.trim().equalsIgnoreCase("Agent")) {
                if(!agentGroup[fc.fromContact.fullName]) {
                    agentGroup[fc.fromContact.fullName] = []
                }
                agentGroup[fc.fromContact.fullName] << pi
            }
        }
    }

agentGroup.each { group, paymentsIn ->
    agentTotal = Money.ZERO
    printAgent = false

    paymentsIn.each { pi ->

        BigDecimal agentCommission = 0.0g // force this to be a BigDecimal
        def agents = pi.paymentIn.payer?.fromContacts?.findAll() { fc ->
            fc.relationType.fromContactName.trim().equalsIgnoreCase("Agent") && fc.relationType.toContactName.trim().equalsIgnoreCase("International Student")
        }?.collect() { fc ->
            fc.fromContact
        }?.findAll() { con ->
            con.tags.findAll() { tag ->
                if (tag.name == "Gold") {
                    agentCommission = Math.max(agentCommission, 0.35g)
                } else if (tag.name == "Silver") {
                    agentCommission = Math.max(agentCommission, 0.30g)
                } else if (tag.name == "Bronze") {
                    agentCommission = Math.max(agentCommission, 0.25g)
                }
            }
        }

        String course = pi.invoice.invoiceLines.collect { it?.enrolment?.courseClass?.course?.name }.unique().minus(null).join(',')

        if (agents && course && !course.toLowerCase().contains('holding')) {
            csv << [
                    "payerLastName"           : pi.paymentIn.payer.lastName,
                    "payerFirstName"          : pi.paymentIn.payer.firstName,
                    "createdOn"               : pi.createdOn?.format("yyyy-MM-dd"),
                    "amount"                  : pi.amount?.toPlainString(),
                    "type"                    : pi.paymentIn.paymentMethod.name,
                    "invoice_number"          : pi.invoice.invoiceNumber,
                    "agent"                   : agents[0]?.fullName,
                    "agent_commission"        : pi.amount.multiply(agentCommission).toPlainString(),
                    "agent_commission_total"  : ""
            ]
        agentTotal += pi.amount.multiply(agentCommission)
        printAgent = true
        }
    }
    if (printAgent) {
        csv << [
            "payerLastName"   : "",
            "payerFirstName"  : "",
            "createdOn"       : "",
            "amount"          : "",
            "type"            : "",
            "invoice_number"  : "",
            "agent"           : group,
            "agent_commission": "",
            "agent_commission_total"  : agentTotal.toPlainString(),
        ]
    }
}

