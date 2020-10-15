/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records*.paylines.flatten().each { pl ->
	def payRefs = pl.payslip.contact.tutor.payrollRef?.split('\\.')
	def dtr = pl.classCost?.tutorRole?.definedTutorRole




	def rate = (pl?.session ? dtr?.getPayRateForSession(pl?.session) : dtr?.getPayRateForDate(pl?.dateFor?.toDate())) ?.rate
	boolean isPayrateOverriden = !(pl.quantity == pl.budgetedQuantity && pl.value == pl.budgetedValue && (rate == null || pl.value == rate))

	csv << [
			'BATCH#_API'		: 'CCE1',
			'EMPLOYEE#'			: payRefs?.length > 0 ? payRefs[0] : null,
			'JOB#'				: payRefs?.length > 1 ? payRefs[1] : null,
			'WORKDATE'			: (pl.session?.startDatetime ?: pl.classCost?.courseClass?.startDateTime ?: pl.createdOn)?.format('dd-MMM-yyyy'),
			'DUTY_TYPE'			: null,
			'PAYCODE'			: isPayrateOverriden ? 'PLT' : pl.classCost?.tutorRole?.definedTutorRole?.name?.split(' ')[0],
			'FUND_ID'			: null,
			'HOUR_MINUTE'		: null,
			'UNIT'				: pl.quantity,
			'ONE_OFF_FLAG'		: null,
			'TS_REASON'			: null,
			'AWARD'				: null,
			'CLASSIFICATION'	: null,
			'STEP'				: null,
			'RATE'				: isPayrateOverriden ? pl.value.toBigDecimal() : null,
			'CONFIRM_DATE'		: null,
			'COST_CENTRE'		: null,
			'ACCOUNT#'			: null,
			'SUB ACCOUNT'		: null ,
			'PROJECT'			: null,
			'USERID'			: ('CCE_' + ((pl.session?.courseClass?:pl.classCost?.courseClass)?.uniqueCode?:'')).take(30),
			'AMT'				: null,
			'MANUAL_PAY_FLAG'	: null,
			'PAYRUN'			: null,
			'USERID_CHECK'		: ('CCE_' + ((pl.session?.courseClass?:pl.classCost?.courseClass)?.uniqueCode?:'')).take(30),
			'EMPLOYEE#_CHECK'	: payRefs?.length > 0 ? payRefs[0] : null,
			'JOB#_CHECK'		: payRefs?.length > 1 ? payRefs[1] : null,
			'FIRSTNAME_CHECK'	: pl.payslip.contact.firstName,
			'LASTNAME_CHECK'	: pl.payslip.contact.lastName,
			'PAYCODE_CHECK'		: isPayrateOverriden ? 'PLT' : pl.classCost?.tutorRole?.definedTutorRole?.name?.split(' ')[0],
			'WORKDATE_CHECK'	: (pl.session?.startDatetime ?: pl.classCost?.courseClass?.startDateTime ?: pl.createdOn)?.format('dd-MMM-yyyy'),
			'HOURS_WORKED_CHECK': pl.quantity,
			'PAYRATE_CHECK'		: pl.value,
			'TOTAL_PAYABLE_CHECK'	: pl.value * pl.quantity,
			'NOTES_CHECK'			: pl.payslip.notes,
			'PRIVATE_NOTES_CHECK': pl.payslip.privateNotes

	]
}
