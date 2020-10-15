def run(args) {
	
	def invoice = args.entity

	def enrolmentType

	if ( invoice.confirmationStatus == ConfirmationStatus.NOT_SENT && (!Money.ZERO.equals(invoice.getTotalIncTax())) ) {
		
		enrolmentType = invoice.contact.tags.find { tag ->
			tag?.name?.equalsIgnoreCase("Smart and Skilled") || tag?.parentTag?.name?.equalsIgnoreCase("International")
		}

		def m
		if(enrolmentType?.name?.equalsIgnoreCase("Smart and Skilled")) {
			m = Email.create("Tax Invoice - Smart and Skilled")
		} else if (enrolmentType?.parentTag?.name?.equalsIgnoreCase("International")) {
			m = Email.create("Tax Invoice - International")
		} else {
			m = Email.create("Tax Invoice")
		}
		m.bind(invoice: invoice)
		if (invoice.corporatePassUsed) {
			m.to(invoice.contact, invoice.corporatePassUsed.getEmail())
		} else {
			m.to(invoice.contact)	
		}
		m.send()
		invoice.setConfirmationStatus(ConfirmationStatus.SENT)
		args.context.commitChanges()

	}
}