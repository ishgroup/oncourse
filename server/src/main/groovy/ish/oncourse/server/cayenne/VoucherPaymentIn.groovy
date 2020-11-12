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
package ish.oncourse.server.cayenne

import ish.common.types.VoucherPaymentStatus
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._VoucherPaymentIn

import javax.annotation.Nonnull
import java.util.Date

/**
 * Object representing relation between voucher and payment record made using it.
 */
@API
@QueueableEntity
class VoucherPaymentIn extends _VoucherPaymentIn  implements Queueable {



	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return number of enrolments purchased for enrolment count vouchers, null for money vouchers
	 */
	@API
	@Override
	Integer getEnrolmentsCount() {
		return super.getEnrolmentsCount()
	}


	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return status of this voucher payment
	 */
	@API
	@Override
	VoucherPaymentStatus getStatus() {
		return super.getStatus()
	}



	/**
	 * @return invoice line paid for with the voucher
	 */
	@API
	@Override
	InvoiceLine getInvoiceLine() {
		return super.getInvoiceLine()
	}

	/**
	 * @return linked payment record
	 */
	@Nonnull
	@API
	@Override
	PaymentIn getPaymentIn() {
		return super.getPaymentIn()
	}

	/**
	 * @return linked voucher record
	 */
	@Nonnull
	@API
	@Override
	Voucher getVoucher() {
		return super.getVoucher()
	}
}
