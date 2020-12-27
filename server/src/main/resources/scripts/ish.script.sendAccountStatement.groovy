def contactList = query {
    entity "Contact"
    query "email not is null"
}

contactList.findAll { contact -> contact.totalOwing > 0 }.each { contact ->
    def reportData = report {
        keycode certificateReportTemplate
        records Arrays.asList(contact)
        background certificateReportBackground
    }

    message {
        from preference.email.from
        to contact.email
        subject "Account statement"
        content "Dear ${contact.fullName}, \n\n Your statement from ${preference.college.name} is attached. The total outstanding on the account is ${contact?.totalOwing}. \n\n" \
	 					   + "If you wish to pay by credit card or view the invoice visit ${contact.getPortalLink('history', 30)} \n\n" \
						   + "If you need to speak about this statement or use another payment method, please contact us on ${Preferences.get("avetmiss.phone")}. \n\n" \
                           + "Regards,\n" \
						   + "${preference.college.name}"
        attachment "Statement_Report.pdf", "application/pdf", reportData
    }
}
