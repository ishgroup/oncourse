/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {

	def paySlips = ObjectSelect.query(Payslip).where(Payslip.STATUS.eq(PayslipStatus.APPROVED))
			.and(Payslip.CONTACT.dot(Contact.TUTOR).dot(Tutor.PAYROLL_REF).isNotNull())
			.and(Payslip.CONTACT.dot(Contact.TUTOR).dot(Tutor.PAYROLL_REF).nlike('invoice'))
			.and(Payslip.CONTACT.dot(Contact.TUTOR).dot(Tutor.PAYROLL_REF).nlike('payroll'))
			.select(args.context)

	if (paySlips.size() > 0) {

		def pay_export = export {
			template "cce.payrunExport.csv"
			records paySlips
		}

		email {
			from "cathy.anderson@sydney.edu.au"
			to "hr.payroll@sydney.edu.au"
			cc "cathy.anderson@sydney.edu.au", "natalia.borisova@sydney.edu.au", "natalie@ish.com.au"
			subject "CCE batch payrun- please process"
			content "Please action immediately for tutor pays to be processed in the current pay period. If there are any issues or questions CCE must be notified by Wednesday midday at the latest in order for us to make any changes and resubmit still within the current pay period. Any queries must be returned to cathy.anderson@sydney.edu.au and CC operations@cce.sydney.edu.au. If for any reason this export cannot be actioned, please contact us immediately by email or call 8627 6713. Thank you, Natalia Borisova"
			attachment "CCE_Payroll_Export.csv", "text/csv", pay_export
		}

		paySlips.each { p ->
			email {
				to p.contact
				bindings payslip: p
				template "Tutor Payrun Notification"
			}
			p.status = PayslipStatus.FINALISED
		}
		args.context.commitChanges()

	}
}
