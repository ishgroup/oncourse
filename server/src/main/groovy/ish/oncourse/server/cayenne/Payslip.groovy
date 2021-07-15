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

import ish.common.types.PayslipPayType
import ish.common.types.PayslipStatus
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._Payslip

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * A payslip is a collection of paylines for a specific tutor, generated at one time. They can be altered until exported
 * after which the numerical data within the paylines is immutable.
 *
 * Payslips are generated either from a specific set of classes, or across the whole college. In either case, the user
 * specifies an end-date and paylines are only generated for payments until that date.
 */
@API
class Payslip extends _Payslip {

	@Override
	protected void postAdd() {
		super.postAdd()

		if (getStatus() == null) {
			setStatus(PayslipStatus.HOLLOW)
		}
	}

	@Override
	void setExported() {
		setStatus(PayslipStatus.FINALISED)
	}

	/**
	 * @return the date and time this record was created
	 */
	@Nonnull @API @Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull @API @Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return public notes appended to this payslip
	 */
	@Nullable @API @Override
	String getNotes() {
		return super.getNotes()
	}

	/**
	 * @return private notes appended to this payslip
	 */
	@Nullable @API @Override
	String getPrivateNotes() {
		return super.getPrivateNotes()
	}

	/**
	 * @return the workflow status of the payslip
	 */
	@Nonnull @API @Override
	PayslipStatus getStatus() {
		return super.getStatus()
	}

	/**
	 * @return the contact for the tutor this payslip belongs to
	 */
	@Nonnull @API @Override
	Contact getContact() {
		return super.getContact()
	}

	/**
	 * @return all paylines attached to this payslip
	 */
	@Nonnull @API @Override
	List<PayLine> getPaylines() {
		return super.getPaylines()
	}

	/**
	 * @return paymentInLines attached to this payslip
	 */
	@Nonnull @API @Override
	List<PaymentInLine> getPaymentInLines() {
		return super.getPaymentInLines()
	}

	/**
	 * @return paymentOutLines attached to this payslip
	 */
	@Nonnull @API @Override
	List<PaymentOutLine> getPaymentOutLines() {
		return super.getPaymentOutLines()
	}

	@Nullable @Override
	String getSummaryDescription() {
		if(getContact() == null) {
			return super.getSummaryDescription()
		}
		return "pay for " + getContact().getFullName()
	}

	/**
	 * The payslip can be either for an employee (payroll) or contractor (invoice)
	 * @return a pay type
	 */
	@Nonnull @API @Override
	PayslipPayType getPayType() {
		return super.getPayType()
	}

	Money getFullAmount() {
		super.getPaylines().sum { it -> it.amount } as Money
	}
}
