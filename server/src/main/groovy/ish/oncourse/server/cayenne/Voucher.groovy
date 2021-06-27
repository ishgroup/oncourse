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

import ish.common.types.ConfirmationStatus
import ish.common.types.PaymentSource
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Voucher
import ish.validation.ValidationFailure
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils

import javax.annotation.Nonnull
import java.util.List

/**
 * A Voucher is an instance of a Voucher Product which is purchased by a particular contact
 *
 */
@API
@QueueableEntity
class Voucher extends _Voucher implements ExpandableTrait {



	public static final String VOUCHER_PRODUCT_PROPERTY = "voucherProduct"


	@Override
	void validateForSave(@Nonnull ValidationResult result) {
		super.validateForSave(result)

		if (StringUtils.trimToNull(getCode()) == null) {
			result.addFailure(ValidationFailure.validationFailure(this, CODE_PROPERTY, "Code cannot be null."))
		}

		if (getExpiryDate() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, EXPIRY_DATE_PROPERTY, "Expiry date cannot be null."))
		}

		if (getStatus() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, STATUS_PROPERTY, "Status cannot be null."))
		}

		if (getSource() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, SOURCE_PROPERTY, "Source cannot be null."))
		}

		if (getVoucherProduct() != null && getVoucherProduct().getMaxCoursesRedemption() == null) {
			if (getValueOnPurchase() == null) {
				result.addFailure(ValidationFailure.validationFailure(
						this, VALUE_ON_PURCHASE_PROPERTY, "Value on purchase must be set for money vouchers."))
			} else if (!getValueOnPurchase().isGreaterThan(Money.ZERO)) {
				result.addFailure(ValidationFailure.validationFailure(
						this, VALUE_ON_PURCHASE_PROPERTY, "Voucher purchase value must be greater than zero."))
			}
		}
	}

	/**
	 * @return
	 */
	@Override
	Object getValueForKey(String key) {
		if (VOUCHER_PRODUCT_PROPERTY == key) {
			return getVoucherProduct()
		}
		return super.getValueForKey(key)
	}

	/**
	 * @return the product which has been sold
	 */
	@Nonnull
	@API
	VoucherProduct getVoucherProduct() {
		return (VoucherProduct) getProduct()
	}

	void setRedeemableBy(Contact contact) {
		setContact(contact)
	}

	/**
	 * @return contact who can redeem this voucher, null if voucher can be redeemed by anyone
	 */
	@Nonnull
	@API
	Contact getRedeemableBy() {
		return getContact()
	}

	@Override
	protected void postAdd() {
		super.postAdd()
		setConfirmationStatus(ConfirmationStatus.NOT_SENT)
	}

	/**
	 * @return voucher code user is required to enter to use it
	 */
	@API
	@Override
	String getCode() {
		return super.getCode()
	}

	/**
	 * Returns true if voucher can be redeemed for money, false if it can be redeemed for
	 * enrolments count.
	 */
	boolean isMoneyVoucher() {
		return getVoucherProduct().getMaxCoursesRedemption() == null
	}

	/**
	 * @return for enrolment count vouchers - number of enrolments which have been purchased with this voucher, null for money vouchers
	 */
	@API
	@Override
	Integer getRedeemedCourseCount() {
		return super.getRedeemedCourseCount()
	}

	/**
	 * @return remaining money value of this voucher
	 */
	@API
	@Override
	Money getRedemptionValue() {
		return super.getRedemptionValue()
	}

	/**
	 * @return whether this voucher was purchased online or in the office
	 */
	@API
	@Override
	PaymentSource getSource() {
		return super.getSource()
	}

	/**
	 * @return starting money value of this voucher when it was purchased
	 */
	@API
	@Override
	Money getValueOnPurchase() {
		return super.getValueOnPurchase()
	}

	/**
	 * A voucher can be redeemed mutiple times as long as the total does not exceed the total
	 * value of the voucher
	 *
	 * @return all VoucherPaymentInLines joined to this voucher
	 */
	@Nonnull
	@API
	@Override
	List<VoucherPaymentIn> getVoucherPaymentsIn() {
		return super.getVoucherPaymentsIn()
	}

	@Override
	String getSummaryDescription() {
		if(getVoucherProduct() == null) {
			return super.getSummaryDescription()
		}
		return getVoucherProduct().getName()
	}

	/**
	 * Calculate remaining value for voucher
	 *
	 * @return remaining value
	 */
	Money getValueRemaining() {
		return getRedemptionValue()
	}

	/**
	 * Get number of classes left to redeem
	 *
	 * @return remaining classes count
	 */
	Integer getClassesRemaining() {
		if (getVoucherProduct().getMaxCoursesRedemption() == null || getRedeemedCourseCount() == null) {
			throw new IllegalStateException("This voucher is for money redemption only")
		}

		return getVoucherProduct().getMaxCoursesRedemption() - getRedeemedCourseCount()
	}

	/**
	 * Get string representing either money amount remaining or number of courses
	 *
	 * @return value remaining string
	 */
	String getValueRemainingString() {
		VoucherProduct vProduct = getVoucherProduct()
		if (vProduct.getMaxCoursesRedemption() != null && vProduct.getMaxCoursesRedemption() > 0) {
			return getClassesRemaining() + " classes"
		}
		return getValueRemaining().toString()
	}

	/**
	 * Determines if the voucher is fully redeemed.
	 *
	 * @return if the voucher is fully redeemed.
	 */
	boolean isFullyRedeemed() {
		VoucherProduct vProduct = getVoucherProduct()
		if (vProduct.getMaxCoursesRedemption() != null && vProduct.getMaxCoursesRedemption() > 0) {
			return getClassesRemaining() <= 0
		}
		return !getValueRemaining().isGreaterThan(Money.ZERO);
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return VoucherCustomField
	}
}
