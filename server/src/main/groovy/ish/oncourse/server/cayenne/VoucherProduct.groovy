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

import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._VoucherProduct
import ish.util.AccountUtil
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils

import javax.annotation.Nonnull
import java.util.List

/**
 * The Voucher Product represents an item available for sale (through the office or on the website).
 * When sold, a Voucher is created for the contact. So think of VoucherProduce as the template for what
 * that voucher will look like once sold.
 *
 * A voucher may be for a dollar amount or for a certain number of enrolments in particular
 * courses.
 *
 * Vouchers should be thought of as a storage of value. When sold, they do not create a tax invoice
 * since they aren't a taxable sale. They are only a way for a customer to store value with the college, similar
 * to the student storing value in a bank account. For this reason, income is not posted to the GL
 * on sale. Instead a liability is created which is extinguished only when the student redeems the voucher or
 * the voucher expires.
 *
 */
@API
@QueueableEntity
class VoucherProduct extends _VoucherProduct {



	public static final String SOLD_VOUCHERS_COUNT = "soldVouchersCount"

	@Override
	void onEntityCreation() {
		super.onEntityCreation()

		if (getLiabilityAccount() == null) {
			setLiabilityAccount(AccountUtil.getDefaultVoucherLiabilityAccount(getObjectContext(), Account.class))
		}
	}

	@Override
	void validateForSave(@Nonnull ValidationResult result) {
		super.validateForSave(result)

		if (StringUtils.trimToNull(getName()) == null) {
			result.addFailure(ValidationFailure.validationFailure(this, NAME.getName(), "Name cannot be empty."))
		}

		if (StringUtils.trimToNull(getSku()) == null) {
			result.addFailure(ValidationFailure.validationFailure(this, SKU.getName(), "SKU cannot be null."))
		}

		if (getExpiryDays() == null || getExpiryDays() < 1) {
			result.addFailure(ValidationFailure.validationFailure(this, EXPIRY_DAYS.getName(), "Expiry days value must be greater then zero."))
		}
	}

	/**
	 * If this value is not null, the voucher can be redeemed for one or more enrolments. If this value is not null, then
	 * getValue() will be null.
	 *
	 * @return the maximum number of enrolments which can be redeemed with this voucher
	 */
	@API
	@Override
	Integer getMaxCoursesRedemption() {
		return super.getMaxCoursesRedemption()
	}

	/**
	 * If this value is not null, the voucher can be redeemed for a specific dollar value. If this value is not null,
	 * then getMaxCoursesRedemption() will be null.
	 *
	 * If getMaxCoursesRedemption() is null and getValue() is also null, then the value of the Voucher on creation will
	 * be equal to the sale price. For example, if the voucher is sold for $100 then it will also be worth $100 in redemption
	 * value. This allows you to create VoucherProduct records which can be sold for any arbitrary value.
	 *
	 * @return a dollar value for this voucher
	 */
	@API
	@Override
	Money getValue() {
		return super.getValue()
	}

	/**
	 * VoucherProducts are a liability when created. This method can return the account of the general ledger these liabilities are created in
	 * @return the account joined to this voucher product
	 */
	@Nonnull
	@API
	@Override
	Account getLiabilityAccount() {
		return super.getLiabilityAccount()
	}

	/**
	 * If getMaxCoursesRedemption() is not null, then this function will return a list of courses which can be redeemed
	 * using this voucher. If getMaxCoursesRedemption() is not null and this list is empty, then the voucher
	 * can be used against any enrollable class.
	 *
	 * @return a list of courses into which the student can enrol
	 */
	@Nonnull
	@Override
	List<VoucherProductCourse> getVoucherProductCourses() {
		return super.getVoucherProductCourses()
	}
}
