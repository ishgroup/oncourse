/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.server.api.v1.model.BankingDTO
import ish.oncourse.server.api.v1.model.PaymentDTO
import ish.oncourse.server.api.v1.model.ReconciledStatusDTO
import ish.oncourse.server.cayenne.Banking
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.util.LocalDateUtils

import java.time.ZoneOffset

@CompileStatic
class BankingFunctions {

	static final String IN_PREFIX = 'p'
	static final String OUT_PREFIX = 'o'

	@CompileStatic(TypeCheckingMode.SKIP)
	static BankingDTO toRestBanking(Banking banking) {
		new BankingDTO().with { bankingDTO ->
			bankingDTO.id = banking.id
			bankingDTO.settlementDate = banking.settlementDate
			bankingDTO.adminSite = banking.adminSite?.name
			bankingDTO.administrationCenterId = banking.adminSite?.id
			bankingDTO.createdBy = banking.createdBy?.fullName
			bankingDTO.total = banking.total?.toBigDecimal()
			bankingDTO.payments = banking.paymentsIn.collect{ asModelPaymentInterface(it) } +
					banking.paymentsOut.collect{ asModelPaymentInterface(it) }
			bankingDTO.reconciledStatus = getReconciledStatus(banking)
			bankingDTO.createdOn = banking.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
			bankingDTO.modifiedOn = banking.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
			bankingDTO
		}
	}
	static ReconciledStatusDTO getReconciledStatus(Banking self) {

		ArrayList<PaymentInterface> payments = new ArrayList<>()
		payments.addAll(self.getPaymentsIn())
		payments.addAll(self.getPaymentsOut())

		if (payments.isEmpty()) {
			return ReconciledStatusDTO.N_A
		}

		Boolean r = payments.get(0).reconciled
		if (payments.find { p -> p.reconciled != r } != null) {
			return ReconciledStatusDTO.PARTIALLY
		}

		return r ? ReconciledStatusDTO.YES : ReconciledStatusDTO.NO
	}
	static PaymentDTO asModelPaymentInterface(PaymentIn source) {
		PaymentDTO target = new PaymentDTO()
		copyCommonProps(source, target)
		target.id = IN_PREFIX + source.id
		target.source = source.source.displayName
		target.contactName = source.payer.getFullName()
		target.contactId = source.payer.id
		target
	}

	static PaymentDTO asModelPaymentInterface(PaymentOut source) {
		PaymentDTO target = new PaymentDTO()
		copyCommonProps(source, target)
		target.id = OUT_PREFIX + source.id
		target.source = ""
		target.contactName = source.payee.getFullName()
		target.contactId = source.payee.id
		target
	}

	private static void copyCommonProps(PaymentInterface source, PaymentDTO target) {
		target.reconciled = source.reconciled
		target.amount = source.amount?.toBigDecimal()
		target.created = LocalDateUtils.dateToValue(source.createdOn)
		target.paymentMethodName = source.paymentMethod?.name
		target.paymentTypeName = source.typeOfPayment
		target.status = source.status?.displayName
		target.reconcilable = source.paymentMethod.reconcilable

		if (source instanceof PaymentIn) {
			target.paymentDate = (source as PaymentIn).paymentDate
			target.paymentId = (source as PaymentIn).id
		} else if (source instanceof PaymentOut) {
			target.paymentDate = (source as PaymentOut).paymentDate
			target.paymentId = (source as PaymentOut).id
		}
	}
}
