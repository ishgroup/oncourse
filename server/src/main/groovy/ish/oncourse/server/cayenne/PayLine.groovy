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

import ish.common.types.ClassCostRepetitionType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._PayLine

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.math.RoundingMode
import java.time.LocalDate

/**
 * A persistent class mapped as "PayLine" Cayenne entity.
 */
@API
class PayLine extends _PayLine {



	@Override
	protected void postAdd() {
		super.postAdd()

		if (getValue() == null) {
			setValue(Money.ZERO)
		}
		if (getQuantity() == null) {
			setQuantity(Money.ZERO.toBigDecimal())
		}
		if (getTaxValue() == null) {
			setTaxValue(Money.ZERO)
		}
	}

	/**
	 * If this payline was created from a classCost record, then this is the
	 * quantity budgeted. The amount might have been overriden, but this is the original value.
	 *
	 * @return value of original budget quantity
	 */
	@Nullable
	@API
	@Override
	BigDecimal getBudgetedQuantity() {
		return super.getBudgetedQuantity()
	}

	/**
	 * If this payline was created from a classCost record, then this is the
	 * amount budgeted for tax. The amount might have been overriden, but this is the original value.
	 * Tax is typically only applicable for contractors who charge GST. This is not PAYG tax.
	 *
	 * @return value of original budget tax
	 */
	@Nullable
	@API
	@Override
	Money getBudgetedTaxValue() {
		return super.getBudgetedTaxValue()
	}

	/**
	 * If this payline was created from a classCost record, then this is the
	 * amount budgeted. The amount might have been overriden, but this is the original value.
	 *
	 * @return value of original budget
	 */
	@Nullable
	@API
	@Override
	Money getBudgetedValue() {
		return super.getBudgetedValue()
	}

	/**
	 * @return the date and time this record was created
	 */
	@Nonnull
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return the date this payline is for
	 */
	@API
	@Override
	LocalDate getDateFor() {
		return super.getDateFor()
	}

	/**
	 * A description derived from the class, course name and other data to show the recipient on a payslip.
	 *
	 * @return description
	 */
	@Nullable
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	BigDecimal getQuantity() {
		return super.getQuantity()
	}

	/**
	 * @return the amount of tax against this payline
	 */
	@Nonnull
	@API
	@Override
	Money getTaxValue() {
		return super.getTaxValue()
	}

	/**
	 * @return the value of this payline
	 */
	@Nonnull
	@API
	@Override
	Money getValue() {
		return super.getValue()
	}

	/**
	 * @return class cost associated to this payline
	 */
	@Nullable
	@API
	@Override
	ClassCost getClassCost() {
		return super.getClassCost()
	}

	/**
	 * Paylines are grouped together in a payslip
	 *
	 * @return payslip
	 */
	@Nonnull
	@API
	@Override
	Payslip getPayslip() {
		return super.getPayslip()
	}

	/**
	 * If this payline is related to a specific session, you can retrieve it here.
	 *
	 * @return session
	 */
	@Nullable
	@API
	@Override
	Session getSession() {
		return super.getSession()
	}

	String getPaidFor() {
		String paidFor = null
		String quantity = this.quantity.setScale(2, RoundingMode.HALF_UP).toString()
		switch(this.classCost.repetitionType) {
			case ClassCostRepetitionType.PER_TIMETABLED_HOUR:
			case ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR:
				paidFor = quantity.concat(" hour")
				break
			case ClassCostRepetitionType.PER_ENROLMENT:
				paidFor = quantity.concat(" enrolment")
				break
			case ClassCostRepetitionType.PER_UNIT:
				paidFor = quantity.concat(" unit")
				break
			case ClassCostRepetitionType.PER_SESSION:
				paidFor = quantity.concat(" session")
				break
			default:
				return null
		}
		return this.quantity > BigDecimal.ONE ? paidFor.concat("s") : paidFor
	}

	Money getAmount() {
		return this.getAmount(2)
	}

	Money getAmount(int scale) {
		return this.getAmount(scale, RoundingMode.HALF_UP)
	}

	Money getAmount(int scale, RoundingMode mode) {
		return Money.valueOf((this.value * this.quantity).setScale(scale, mode))
	}
}
