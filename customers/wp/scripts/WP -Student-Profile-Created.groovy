def run(args) {

    def contact = args.entity

    if(contact.student) {
    	def pnlEmail = contact.customFields.find { cf -> cf.customFieldType.key == "pnlEmail" }?.value

	    if(pnlEmail && (pnlEmail != "")) {
	    	email {
	    		to pnlEmail
	    		from preference.email.from
	    		subject "New student profile created at Western Power"
	    		template "WP Student Profile Created"
	    		bindings contact: contact
	    	}
	    }

	    if(contact.mobilePhone != null && contact.mobilePhone != "") {
	    	sms {
	    		to contact
	    		text "A PTS student profile has just been created for you. Your info may be used as per PTS T&Cs. Phone PTS if this student profile has been created in error 94117888".take(160)
	    	}
	    }
    }    
}