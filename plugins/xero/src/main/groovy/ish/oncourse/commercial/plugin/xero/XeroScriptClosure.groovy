/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.xero

import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.Payslip
import static XeroIntegration.*
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait

import java.time.LocalDate
/**
 * Create Xero manual journal for onCourse accaunt transactions.
 * Keeping in sync all onCourse transactions since 'startOnDate' option.
 * Use POSTED status if postJournalsInXerois true, otherwise DRAFT
 *
 * ```
 * xero {
 *     action "journal"
 *     startOn LocalDate.parse('2020-09-04')
 *     postJournals true
 * }
 * ```
 *
 * Use "payroll" action to create the pay records in xero, create the employee in xero if necessary.
 *
 * ```
 * xero {
 *     action "payroll"
 *     payslip record
 *     bounceAddress "support@ish.com.au"
 * }
 * ```
 *
 */

@API
@ScriptClosure(key = "xero", integration = XeroIntegration)
class XeroScriptClosure implements ScriptClosureTrait<XeroIntegration> {
	String action
	LocalDate startOn
	Boolean postJournals = true
	Payslip payslip
	String bounceAddress

	void bounceAddress(String bounceAddress) {
		this.bounceAddress = bounceAddress
	}

	void action(String action) {
		this.action = action
	}

	void startOn(LocalDate startOn) {
		this.startOn = startOn
	}

	void postJournals(Boolean postJournals) {
		this.postJournals = postJournals
	}

	void payslip(Payslip payslip) {
		this.payslip = payslip
	}

	@Override
	Object execute(XeroIntegration integration) {
		switch (action) {
			case 'journal':
				integration.addManualJournalForTransactions(startOn, postJournals)
				break
			case 'payroll':
				try {

					integration.payslip = integration.objectContext.localObject(payslip)
					integration.contact = integration.payslip.contact

					integration.bounceAddress = bounceAddress
					if (!integration.contact.dateOfBirth) {
						integration.interruptExport(XeroIntegration.MESSAGE_DOB_REQUIRED)
					}

					if (!(integration.contact.street && integration.contact.suburb && integration.contact.state && integration.contact.postcode)) {
						integration.interruptExport(XeroIntegration.MESSAGE_ADDRESS_REQUIRED)
					}

					Employee employee = integration.getEmployee()

					if (!employee) {
						integration.createEmployee()
						integration.interruptExport(XeroIntegration.MESSAGE_CONFIGURE_XERO_EMPLOYEE)
					}

					if (!employee.earningId) {
						integration.interruptExport(XeroIntegration.MESSAGE_CONFIGURE_EARNING)
					}
					if (!employee.calendarId) {
						integration.interruptExport(XeroIntegration.MESSAGE_CONFIGURE_CALENDAR)
					}

					String payrunId = integration.getPayRunId(employee.calendarId)

					String payslipId = integration.getPaysplipId(payrunId, employee.id)
					if (!payslipId) {
						integration.interruptExport(XeroIntegration.MESSAGE_PAYSLIP_MISSING)
					}

					Money total = payslip.paylines.collect { payline -> payline.value.multiply(payline.quantity) }.inject { a, b -> a.add(b) }

					integration.addEarningLine(payslipId, employee.earningId, total)
				} catch (XeroException ignored) {
					// ignored and allow proceed with other payslip records
				}
				break
			default:
				throw new IllegalArgumentException("Unsupported xero action")

		}
		return null
	}
}
