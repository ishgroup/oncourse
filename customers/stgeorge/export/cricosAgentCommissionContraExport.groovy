records*.findAll() { it ->
    it.payment.status == PaymentStatus.SUCCESS &&
            it.amount.isGreaterThan(ish.math.Money.ZERO) &&
                    it.payment.amount.isGreaterThan(ish.math.Money.ZERO) &&
                            !["glep transfer", "payment via agent"].contains(it.payment.paymentMethod.name.toLowerCase().trim()) &&
                                    it.payment.reversedBy == null
}.toSorted {
    a, b -> a.createdOn <=> b.createdOn
}.each { pi ->

    BigDecimal agentCommission = 0.0
    def agents = pi.paymentIn.payer?.fromContacts?.findAll() { fc ->
        fc.relationType.fromContactName.trim().equalsIgnoreCase("Agent") && fc.relationType.toContactName.trim().equalsIgnoreCase("International Student")
    }?.collect() { fc ->
        fc.fromContact
    }?.findAll() { con ->
        con.tags.findAll() { tag ->
            if (tag.name == "Gold") {
                agentCommission = Math.max(agentCommission, 0.35)
            } else if (tag.name == "Silver") {
                agentCommission = Math.max(agentCommission, 0.30)
            } else if (tag.name == "Bronze") {
                agentCommission = Math.max(agentCommission, 0.25)
            }
        }
    }

    if (agents) {
        csv << [
                "payerLastName"   : pi.paymentIn.payer.lastName,
                "payerFirstName"  : pi.paymentIn.payer.firstName,
                "createdOn"       : pi.createdOn?.format("yyyy-MM-dd"),
                "amount"          : pi.amount?.toPlainString(),
                "source"          : pi.paymentIn.source.displayName,
                "status"          : pi.paymentIn.status.displayName,
                "type"            : pi.paymentIn.paymentMethod.name,
                "invoice number"  : pi.invoice.invoiceNumber,
                "course"		  : pi.invoice.invoiceLines.collect { it?.enrolment?.courseClass?.course?.name }.unique().minus(null).join(','),
                "agent"			  : agents[0]?.fullName,
                "agent commission": pi.amount.multiply(agentCommission).toPlainString()
        ]
    }
}