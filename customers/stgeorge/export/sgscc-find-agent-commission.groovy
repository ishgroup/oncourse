def run(args) {
		
	def qualClasses = ObjectSelect.query(CourseClass)
					.where(CourseClass.hasTag("Subjects/International/Qualification courses"))

	def today = Calendar.getInstance().getTime()
	today.set(hourOfDay: 0, minute: 0, second: 0)

    use(TimeCategory) {
    	def lastMonth = today - 1.month
    }

    /**
	* Get all payments made in the last month
	*/
    qualClasses.findAll() { cc ->
    	cc*.successAndQueuedEnrolments*.invoiceLine*.invoice*.paymentInLines.flatten().unique().createdOn.between(lastMonth, today)
    }.each { it ->
    		//TODO
			/**
			* Create a collection of PaymentInLines where related paymentype includes contra payments (current export doesnt include contra payments)
			* Exclude 'zero payments' and enrolments with related paymenttype as internal
			*/
    }

	export {
	    template "sgscc.onCourse.cricosAgentCommissionContraExport.csv"
	    records //TODO - run export on collection of paymentIns including contra payments
	}	
}