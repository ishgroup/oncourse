def containsNoJournalTransfers( List<PaymentInLine> li ) {
	li.each { line ->
		if( line.paymentIn.paymentMethod.name == "Journal transfer" ) {
			return false
		}
	}
	return true
}


def run(args) {

	def contactList = ObjectSelect.query(Contact)
            .where(Contact.EMAIL.isNotNull())
            .select(args.context)

    contactList.findAll { contact -> contact.totalOwing > 0 }.findAll { c -> c.email }.each { c ->
    	invoices = c.invoices.findAll { i ->
    		(containsNoJournalTransfers(i.paymentInLines) && (i.overdue.toBigDecimal() > new BigDecimal(0)))
    	}


		invoices.each { i ->
	    	email {
	            from preference.email.from
	            to i.contact
	            template 'CCE Overdue Invoice'
	            bindings invoice: i
 			}
		}
	}
}